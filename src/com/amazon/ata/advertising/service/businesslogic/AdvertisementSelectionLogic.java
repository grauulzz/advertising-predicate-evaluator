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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
        TargetingEvaluator evaluator = new TargetingEvaluator(context);
        this.filterByTrue = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.TRUE);
        this.filterByFalse = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.FALSE);
        this.filterByIntr = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.INDETERMINATE);
    };

    private final BiFunction<String, List<TargetingGroup>, Advertisement> eval = (customerId, targetingGroups) -> {
        TargetingGroup tg = targetingGroups.stream().filter(filterByTrue).reduce(
                                    (a, b) -> a.getClickThroughRate() > b.getClickThroughRate() ? a : b
        ).orElse(null);
        return (tg != null) ?
                       Advertisement.builder().withId(customerId).withContent(tg.getContentId()).build() :
                       Advertisement.builder().withId(customerId).build();
    };

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

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
            if (CollectionUtils.isNotEmpty(contents)) {
                List<TargetingGroup> allMatchingTgForContentId = contents.stream().map(AdvertisementContent::getContentId).map(targetingGroupDao::get)
                                                                         .flatMap(List::stream).collect(Collectors.toList());
                predicatesInit.accept(customerId, marketplaceId);
                this.futureAdContents = contents.stream().map(
                        c -> CompletableFuture.supplyAsync(() -> eval.apply(
                                customerId, allMatchingTgForContentId), AsyncUtils.getExecutor))
                                                .collect(Collectors.toList());
            } else {
                return new EmptyGeneratedAdvertisement();
            }
        }
        CompletableFuture<List<Advertisement>> sequencedFutures =
                AsyncUtils.sequenceFuture(this.futureAdContents);

        monitor(sequencedFutures, pB);

        try {
            List<Advertisement> ads = sequencedFutures.get();
            Advertisement adv = ads.get(r.nextInt(ads.size()));
            if (CollectionUtils.isNotEmpty(ads)) {
                String adContent = adv.getContent();
                // should be the customerId
                String adIdSlashCustomerId = adv.getId();

                // conversion to AdvertisingContent
                AdvertisingContent advertisingContent =
                        AdvertisingContent.builder().withContent(adContent).withId(adIdSlashCustomerId)
                                .withMarketplaceId(marketplaceId).build();

                // conversion to AdvertisementContent
                String adContentId = advertisingContent.getId();
                String renderableContent = advertisingContent.getContent();
                AdvertisementContent advertisementContent =
                        AdvertisementContent.builder().withContentId(adContentId)
                                .withRenderableContent(renderableContent).withMarketplaceId(marketplaceId).build();
                return new GeneratedAdvertisement(advertisementContent);
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error getting advertisement", e);
        }
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

}




