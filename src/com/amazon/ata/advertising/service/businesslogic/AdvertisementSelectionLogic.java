package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.future.AsyncUtils;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.future.ThreadUtilities;
import com.amazon.ata.advertising.service.model.*;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.model.translator.TargetingGroupTranslator;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.google.common.cache.CacheLoader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.*;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import javax.swing.text.html.Option;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.Opt;


import static com.amazon.ata.ConsoleColors.*;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic implements FutureMonitor<List<Advertisement>> {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
    private Random r = new Random();

    public ReadableDao<String, List<AdvertisementContent>> getContentDao() {
        return contentDao;
    }

    private final ReadableDao<String, List<AdvertisementContent>> contentDao;

    public ReadableDao<String, List<TargetingGroup>> getTargetingGroupDao() {
        return targetingGroupDao;
    }

    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;

    private Predicate<TargetingGroup> filterByTrue;
    private Predicate<TargetingGroup> filterByFalse;
    private Predicate<TargetingGroup> filterByIntr;

    private final BiConsumer<String, String> predicatesInit = (customerId1, marketplaceId1) -> {
        RequestContext context = new RequestContext(customerId1, marketplaceId1);
        TargetingEvaluator eval = new TargetingEvaluator(context);
        this.filterByTrue = pred -> eval.evaluate(pred).equals(TargetingPredicateResult.TRUE);
        this.filterByFalse = pred -> eval.evaluate(pred).equals(TargetingPredicateResult.FALSE);
        this.filterByIntr = pred -> eval.evaluate(pred).equals(TargetingPredicateResult.INDETERMINATE);
    };

    private final BiFunction<String, List<TargetingGroup>, Advertisement> eval = (customerId, targetingGroups) -> {
        TargetingGroup tg = targetingGroups.stream().filter(filterByTrue).reduce(
                                    (a, b) -> a.getClickThroughRate() > b.getClickThroughRate() ? a : b
        ).orElse(null);
        return (tg != null) ? // not likely to be null
                       Advertisement.builder().withId(customerId).withContent(tg.getContentId()).build() : null;
    };

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }
//    public CompletableFuture<Stack<Advertisement>> getEligibleADS() {
//        return eligibleADS;
//    }
//    private CompletableFuture<Stack<Advertisement>> eligibleADS;
    private List<CompletableFuture<Advertisement>> futureAdContents;

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
     * EmptyGeneratedAdvertisement.
     *
     * @param customerId    - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     *
     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
     * not be generated.
     */
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.info("Marketplace id is empty, returning empty advertisement");
        } else {
            final List<AdvertisementContent> contents = contentDao.get(marketplaceId);
            List<TargetingGroup> allMatchingTgForContentId =
                    contents.stream().map(AdvertisementContent::getContentId)
                            .map(targetingGroupDao::get).flatMap(List::stream)
                            .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(contents)) {
                predicatesInit.accept(customerId,marketplaceId);
                this.futureAdContents = contents.stream().map(
                        c -> CompletableFuture.supplyAsync(() -> eval.apply(
                                customerId, allMatchingTgForContentId), AsyncUtils.getExecutor))
                                                .collect(Collectors.toList());
            }
        }

        CompletableFuture<List<Advertisement>> sequencedFutures =
                AsyncUtils.sequenceFuture(this.futureAdContents);

        monitor(sequencedFutures, pB);

        try {
            List<Advertisement> ads = sequencedFutures.get();
            Advertisement adv = ads.get(0);
            if (CollectionUtils.isNotEmpty(ads)) {
                String adContent = adv.getContent();
                // should be the customerId
                String adIdSlashCustomerId = adv.getId();

                // GenerateAdvertisementResponse r = GenerateAdvertisementResponse.builder().withAdvertisement(adv).build();
                AdvertisingContent advertisingContent =
                        AdvertisingContent.builder().withContent(adContent).withId(adIdSlashCustomerId)
                                .withMarketplaceId(marketplaceId).build();

                String adContentId = advertisingContent.getId();
                String renderableContent = advertisingContent.getContent();

                AdvertisementContent advertisementContent =
                        AdvertisementContent.builder().withContentId(adContentId)
                                .withRenderableContent(renderableContent).withMarketplaceId(marketplaceId).build();

                AsyncUtils.getExecutor.shutdown();

                return new GeneratedAdvertisement(advertisementContent);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error getting advertisement", e);
        }
        AsyncUtils.getExecutor.shutdown();
        return new EmptyGeneratedAdvertisement();
    }


    public TargetingGroup sortCtr(List<TargetingGroup> targetingGroups) {
//        TargetingGroup sortedCtr = sortCtr(filtered);

//        Optional<AdvertisementContent> adv = getContentDao().get(sortedCtr.getContentId()).stream().findAny();

//        if (tg != null) {
//            AdvertisementContent a = adv.get();
//            return Advertisement.builder().withId(customerId).withContent(a.getRenderableContent()).build();
//        }
        UnaryOperator<List<TargetingGroup>> ctr =
                targetGroup -> targetGroup.stream().sorted(
                                Comparator.comparing(TargetingGroup::getClickThroughRate))
                                       .collect(Collectors.toList());

        return ctr.apply(targetingGroups).get(0);
    }

    private CacheLoader<String, Map<Double, Advertisement>> cache(Double ctr, List<Advertisement> contents) {
        return new CacheLoader<>() {
            @Override
            @ParametersAreNonnullByDefault
            public Map<Double, Advertisement> load(String key) throws Exception {
                return contents.stream().collect(Collectors.toMap(k -> ctr, v -> v));
            }
        };
    }


    public void setRandom(Random random) {
        this.r = random;
    }

    @Override
    public void monitor(CompletableFuture<List<Advertisement>> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void monitor(CompletableFuture<List<Advertisement>> completableFuture, Consumer<String> color) {
        FutureMonitor.super.monitor(completableFuture, color);
    }

    @Override
    public void onCompleteMonitor(CompletableFuture<List<Advertisement>> onComplete) {
        ThreadUtilities.currentThreadLogger(onComplete);
        monitor(onComplete);
        onComplete.whenComplete((result, throwable) -> {
            if (throwable != null) {
                pR.accept(String.format("{%s} {%s} %n", throwable, onComplete));
            }
            boolean b = onComplete.isDone() && !onComplete.isCompletedExceptionally() && onComplete.join() != null;
            pM.accept(String.format("Completed future -> {%s} ...completed with no errors!? -> {%s}%n", result, b));
        });
    }
}



//                            TargetingGroup tg = targetGroups.stream().reduce(
//                                    (a, b) -> a.getClickThroughRate() > b.getClickThroughRate() ? a : b)
//                                                        .orElse(targetGroups.get(0));
//                            String contentId = tg.getContentId();

// probs need to be a list of completable futures rather then a single completable future list
//                this.eligibleADS = CompletableFuture.supplyAsync(() ->
//                    contents.stream().map(content -> {
//                        String contentId = content.getContentId();
//                        return eval.apply(contentId, allMatchingTgForContentId);
//                    }).collect(Stack::new, Stack::push, Stack::addAll), AsyncUtils.getExecutor);



