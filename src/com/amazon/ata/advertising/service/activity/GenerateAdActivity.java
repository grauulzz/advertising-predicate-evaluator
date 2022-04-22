package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.App;
import com.amazon.ata.ConsoleColors;
import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.AsyncUtils;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.future.ThreadUtils;
import com.amazon.ata.advertising.service.model.Advertisement;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.google.common.cache.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 *
 * Activity class for generate ad operation.
 *
 */
public class GenerateAdActivity implements FutureMonitor<AdvertisementContent> {
    private static final Logger LOG = LogManager.getLogger(GenerateAdActivity.class);
    public static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//    private final List<GeneratedAdvertisement> generatedAds = new ArrayList<>();
    public static List<GenerateAdvertisementResponse> generatedAdResponses = new ArrayList<>();
    private static List<AdvertisementContent> adContentResults = new ArrayList<>();
    private final AdvertisementSelectionLogic adSelector;

    UnaryOperator<List<TargetingGroup>> ctr =
            tg -> tg.stream().sorted(Comparator.comparing(TargetingGroup::getClickThroughRate))
                          .collect(Collectors.toList());

    private LoadingCache<String, Map<String, List<TargetingGroup>>> loadingCache;
    public static List<AdvertisementContent> adContents = new ArrayList<>();
//    List<CompletableFuture<AdvertisementContent>> futureAdContents;

    private List<CompletableFuture<AdvertisementContent>> futureAdContents = new ArrayList<>();
    private Function<AdvertisementContent, List<CompletableFuture<AdvertisementContent>>> adContentToFutureAdContent =
            adContent -> {
                futureAdContents.add(CompletableFuture.supplyAsync(() -> adContent, executorService));
                return Collections.unmodifiableList(futureAdContents);
            };

    /**
     * A Coral activity for the GenerateAdvertisement API.
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
    }

    /**
     * Decides on the ad most likely to be clicked on by the provided customer, from the group of ads a customer is
     * eligible to see.
     * @param request Contains the customerId to generate an advertisement for, and the marketplace id where the ad
     *                will be rendered
     * @return the response will contain the generated advertisement. It's content will be an empty String if no
     *      advertisement could be generated.
     */
    public GenerateAdvertisementResponse generateAd(GenerateAdvertisementRequest request) {
        String customerId = request.getCustomerId();
        String marketplaceId = request.getMarketplaceId();

        CompletableFuture<AdvertisementContent> future =
                CompletableFuture.supplyAsync(() -> adSelector.selectAdvertisement(
                        customerId, marketplaceId), executorService).handle((generatedAd, throwable) -> {
                            if (throwable != null) {
                                LOG.error("Error generating advertisement", throwable);
                            }
                            AdvertisementContent content = generatedAd.getContent();
                            adContentToFutureAdContent.apply(content);
                            return content;
                        });
        monitor(future);
        CompletableFuture<List<AdvertisementContent>> sequenced = sequenceFuture(futureAdContents);
        CompletableFuture<AdvertisementContent> futureAdContent = sequenced.thenApply(contents -> {

            TargetingGroup tgHighestCtr = contents.stream().map(adSelector::getTGfromAdContent).flatMap(List::stream)
                                                  .max(Comparator.comparing(TargetingGroup::getClickThroughRate)).orElse(null);
            if (tgHighestCtr != null) {
                AdvertisementContent content = adSelector.getAdContentFromTG(tgHighestCtr);
                ConsoleColors.pG.accept(String.format("Generated advertisement with highest ctr for customer {%s} -> %n{%s}%n", customerId, content));
                return content;
            }
            return contents.stream().filter(Objects::nonNull)
                           .findFirst().orElseGet(() -> {
                               ConsoleColors.pR.accept(String.format("Could not sort add by highest ctr, null add {%s}%n", customerId));
                               return null;
            });
        });
        monitor(futureAdContent);
        GeneratedAdvertisement generatedAdvertisement = new GeneratedAdvertisement(futureAdContent.join());
        return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(generatedAdvertisement));
    }

    private static <R> CompletableFuture<List<R>> sequenceFuture(List<CompletableFuture<R>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                       .thenApply(v -> futures.stream().map(AsyncUtils::getValue).filter(Objects::nonNull)
                                               .collect(Collectors.toList())
                       );
    }

    @Override
    public void monitor(CompletableFuture<AdvertisementContent> future) {
        FutureMonitor.super.monitor(future);
    }


    public GenerateAdvertisementResponse sortSelectedAdByCtr(List<TargetingGroup> tgs) {
        List<TargetingGroup> sortedTgs = adSelector.getCtrOf(tgs);
        String key = sortedTgs.get(0).getContentId();

        List<AdvertisementContent> advertisementContents = adContentResults;

        return generatedAdResponses.stream().filter(r -> r.getAdvertisement().getId().equals(key)).findFirst().orElse(null);
    }
    private List<TargetingGroup> loadCachedRes(String key) {
        ConcurrentMap<String, Map<String, List<TargetingGroup>>> res = loadingCache.asMap();

        return res.get(key).values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    private void cacheComparison(String customerId) {
        CacheLoader<String, Map<String, List<TargetingGroup>>> cached = adSelector.getCache();
        try {
            Map<String, List<TargetingGroup>> tgFromCache = cached.load(customerId);
            String contentId;
            tgFromCache.forEach((k, v) -> {
                System.out.println(k + " -> " + v);
                System.out.println(v.get(0).getContentId());
            });

            List<TargetingGroup> tgsList = tgFromCache.values().stream().flatMap(List::stream).collect(Collectors.toList());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}






//        future.whenCompleteAsync((response, throwable) -> {
//            if (throwable != null) {
//                ConsoleColors.pR.accept(String.format("{%s} {%s} %n", throwable, response));
//            }
////            generatedAdResponses.add(response);
//            TargetingGroup tg = adContents.stream().map(adSelector::getTGfromAdContent).flatMap(List::stream)
//                                         .max(Comparator.comparing(TargetingGroup::getClickThroughRate)).orElse(null);
//
//            if (tg != null) {
//                ConsoleColors.pM.accept(String.format("found corresponding tg {%s}  %n", tg));
//
//                adContentResults.add(adSelector.getAdContentFromTG(tg));
//                ConsoleColors.pM.accept(String.format("adContentResults (sorted by ctr) {%s}  %n", adContentResults));
//            }
//
//            ConsoleColors.pG.accept(String.format("added generatedAdResponse to list {%s}  %n", generatedAdResponses));
//        });
//
//        try {
//            return future.get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }




//    @Override
//    public void onComplete(CompletableFuture<GenerateAdvertisementResponse> onComplete) {
//        executorService.execute(() -> {
//            try {
//                GenerateAdvertisementResponse ad = onComplete.get();
//                generatedAdResponses.add(ad);
//                ConsoleColors.pG.accept(String.format("Completed Async {%s} %n", generatedAdResponses));
//            } catch (InterruptedException | ExecutionException e) {
//                Thread.currentThread().interrupt();
//                throw new AdvertisementClientException(e);
//            }
//        });
//    }









//        CompletableFuture<Void> lastStage = future.thenAcceptAsync(gen -> addGeneratedAd.accept(gen));
//                ConsoleColors.pG.accept(String.format("Completed Async {%s} {%s} %n", adContentResults, App.getCurrentTime()));


//            Stream<Double> stream = tgFromCache.values().stream().flatMap(Collection::stream)
//                                            .map(TargetingGroup::getClickThroughRate)
//                                            .onClose(() -> loadingCache.invalidate(customerId));
//            LOG.info(System.out.printf("unsorted ctr -> %n%s%n%n",
//                    tgFromCache.values().stream().flatMap(Collection::stream).collect(Collectors.toList())));
//            LOG.info(System.out.printf("sorted ctr -> %n%s%n%n",
//                    tgFromCache.values().stream().map(ctr).collect(Collectors.toList())));
//            LOG.info(System.out.printf("generated ads responses -> %n%s%n%n" , results));


//            return sortSelectedAdByCtr(tgFromCache.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

//            System.out.println("cached map ->  ");
//            App.toJson.accept(tgFromCache);

