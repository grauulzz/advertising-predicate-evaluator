package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.App;
import com.amazon.ata.ConsoleColors;
import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
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
public class GenerateAdActivity implements FutureMonitor<GenerateAdvertisementResponse> {
    private static final Logger LOG = LogManager.getLogger(GenerateAdActivity.class);
    private static final List<GenerateAdvertisementResponse> generatedAdResponses = new ArrayList<>();
    private static final List<AdvertisementContent> adContentResults = new ArrayList<>();
    private Consumer<AdvertisementContent> addAdConent = adContentResults::add;
    private static Consumer<GenerateAdvertisementResponse> addGeneratedAd = generatedAdResponses::add;

    private final AdvertisementSelectionLogic adSelector;

    public static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    final UnaryOperator<List<TargetingGroup>> ctr =
                    targetGroup -> targetGroup.stream().sorted(
                                    Comparator.comparing(TargetingGroup::getClickThroughRate))
                                           .collect(Collectors.toList());

    private LoadingCache<String, Map<String, List<TargetingGroup>>> loadingCache;

    /**
     * A Coral activity for the GenerateAdvertisement API.
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
//        targetingGroupMap = loadingCache.asMap();
    }

    GenerateAdvertisementResponse sortSelectedAdByCtr(List<TargetingGroup> tgs) {
        List<TargetingGroup> sortedTgs = adSelector.getCtrOf(tgs);
        String key = sortedTgs.get(0).getContentId();

        List<AdvertisementContent> advertisementContents = adContentResults;

        return generatedAdResponses.stream().filter(r -> r.getAdvertisement().getId().equals(key)).findFirst().orElse(null);
    }

    private List<TargetingGroup> loadCachedRes(String key) {
        ConcurrentMap<String, Map<String, List<TargetingGroup>>> res = loadingCache.asMap();

        return res.get(key).values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    private AdvertisementContent adContent;
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

        CompletableFuture<GenerateAdvertisementResponse> future =
                CompletableFuture.supplyAsync(() -> adSelector.selectAdvertisement(
                        customerId, marketplaceId), executorService).handle((generatedAd, throwable) -> {
                            if (throwable != null) LOG.error("Error generating advertisement", throwable);
                            this.adContent = generatedAd.getContent();
                            GenerateAdvertisementResponse r = GenerateAdvertisementResponse.builder().withAdvertisement(AdvertisementTranslator.toCoral(generatedAd)).build();
                            return new GenerateAdvertisementResponse(r.getAdvertisement());
                        });
        monitor(future);
        onComplete(future);
        CompletableFuture<Void> lastStage = future.thenAcceptAsync(gen -> addGeneratedAd.accept(gen));
        return generatedAdResponses.get(0);
    }

    private void extractedCacheComparison(String customerId) {
//        extractedCacheComparison(customerId);
        CacheLoader<String, Map<String, List<TargetingGroup>>> cached = adSelector.getCache();
        try {
            Map<String, List<TargetingGroup>> tgFromCache = cached.load(customerId);
            String contentId;
            tgFromCache.forEach((k, v) -> {
                System.out.println(k + " -> " + v);
                System.out.println(v.get(0).getContentId());

            });

            List<TargetingGroup> tgsList = tgFromCache.values().stream().flatMap(List::stream).collect(Collectors.toList());

            App.toJson.accept(tgsList);

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



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void onComplete(CompletableFuture<GenerateAdvertisementResponse> onComplete) {
        executorService.execute(() -> {
            try {
                GenerateAdvertisementResponse ad = onComplete.get();
                addAdConent.accept(adContent);
                addGeneratedAd.accept(ad);
                ConsoleColors.pG.accept(String.format("Completed Async {%s} {%s} %n", generatedAdResponses, App.getCurrentTime()));
                ConsoleColors.pG.accept(String.format("Completed Async {%s} {%s} %n", adContentResults, App.getCurrentTime()));
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new AdvertisementClientException(e);
            }
        });
    }

}

