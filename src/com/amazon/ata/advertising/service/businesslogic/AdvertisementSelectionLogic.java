package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.dependency.DynamoDBModule;
import com.amazon.ata.advertising.service.future.FutureUtils;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.google.common.cache.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvertisementSelectionLogic implements FutureMonitor<List<AdvertisementContent>> {
    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
    private final DynamoDBModule mod = new DynamoDBModule();
    private Random r = new Random();
    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private List<CompletableFuture<AdvertisementContent>> futureAdContents;
    private Predicate<TargetingGroup> filterByTrue;
    private Predicate<TargetingGroup> filterByFalse;
    private Predicate<TargetingGroup> filterByIntr;
    private final BiConsumer<String, String> predicatesInit = (customerId, marketPlaceId) -> {
        RequestContext context = new RequestContext(customerId, marketPlaceId);
        TargetingEvaluator evaluator = new TargetingEvaluator(context);
        this.filterByTrue = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.TRUE);
        this.filterByFalse = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.FALSE);
        this.filterByIntr = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.INDETERMINATE);
    };

    private final Function<List<TargetingGroup>, AdvertisementContent> asyncEvaluator = targetingGroups -> {

                Optional<TargetingGroup> tg = targetingGroups.stream().filter(filterByTrue).reduce(
                        (a, b) -> a.getClickThroughRate() > b.getClickThroughRate() ? a : b);

                if (tg.isPresent()) {
                    String highestCtrTgContentId = tg.get().getContentId();
                    Optional<AdvertisementContent> matchingContent =
                            Optional.ofNullable(mod.provideDynamoDBMapper().load(AdvertisementContent.class,
                                    highestCtrTgContentId));

                    if (matchingContent.isPresent()) {
                        String matchRenderableContent = matchingContent.get().getRenderableContent();
                        return AdvertisementContent.builder().withContentId(highestCtrTgContentId)
                                       .withRenderableContent(matchRenderableContent)
                                       .build();
                    }
                    AdvertisementContent.builder().withContentId(highestCtrTgContentId).build();
                }

        return AdvertisementContent.builder().build();
    };
    private final CacheLoader<String, Optional<List<AdvertisementContent>>> loader = new CacheLoader<>() {
        @Override
        @ParametersAreNonnullByDefault
        public Optional<List<AdvertisementContent>> load(String key) {
            return Optional.ofNullable(contentDao.get(key));
        }
    };

    private final RemovalListener<String, Optional<List<AdvertisementContent>>> listener = n -> {
        if (n.wasEvicted()) {
            String cause = n.getCause().name();
            System.out.printf("Cache hit -> {%s} %nCause -> {%s}", n.getKey(), cause);
        }
    };

    private final LoadingCache<String, Optional<List<AdvertisementContent>>> cache =
            CacheBuilder.newBuilder().removalListener(listener).maximumSize(100).build(loader);

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

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
        if (marketplaceId == null || StringUtils.isEmpty(marketplaceId)) {
            return new EmptyGeneratedAdvertisement();
        } else {
            List<AdvertisementContent> contents = cache.getUnchecked(marketplaceId).orElseGet(() -> {
                List<AdvertisementContent> content = contentDao.get(marketplaceId);
                cache.putAll(Collections.singletonMap(marketplaceId, Optional.ofNullable(content)));
                return content;
            });
            if (CollectionUtils.isNotEmpty(contents)) {
                predicatesInit.accept(customerId, marketplaceId);
                List<TargetingGroup> allMatchingTgForContentId = loadCorrespondingTargetGroupsListFromCache(contents);
                this.futureAdContents = contents.stream().map(
                        c -> CompletableFuture.supplyAsync(() -> asyncEvaluator.apply(allMatchingTgForContentId),
                                FutureUtils.getExecutor)
                ).collect(Collectors.toList());

            } else {
                return new EmptyGeneratedAdvertisement();
            }
        }

        CompletableFuture<List<AdvertisementContent>> sequencedFutures =
                FutureUtils.sequenceFuture(this.futureAdContents);

        monitor(sequencedFutures, ConsoleLogger.CYAN.getColor());

        List<AdvertisementContent> ads = FutureUtils.get(sequencedFutures);

        if (CollectionUtils.isNotEmpty(ads)) {
            AdvertisementContent adv = ads.get(r.nextInt(ads.size()));
            return new GeneratedAdvertisement(adv);
        } else {
            return new EmptyGeneratedAdvertisement();
        }

    }

    @Override
    public void monitor(CompletableFuture<List<AdvertisementContent>> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void monitor(CompletableFuture<List<AdvertisementContent>> completableFuture, Consumer<String> color) {
        FutureMonitor.super.monitor(completableFuture, color);
    }

    private List<TargetingGroup> loadCorrespondingTargetGroupsListFromCache(List<AdvertisementContent> contents) {
        return contents.stream()
                       .map(AdvertisementContent::getContentId)
                       .map(targetingGroupDao::get)
                       .flatMap(List::stream)
                       .collect(Collectors.toList());
    }
    public void setRandom(Random random) {
        this.r = random;
    }
}



//   if (CollectionUtils.isNotEmpty(ads)) {
//            AdvertisementContent adv = ads.get(r.nextInt(ads.size()));
//            return new GeneratedAdvertisement(adv);
//        } else {
//            for (AdvertisementContent ad : ads) {
//                if (Objects.isNull(ad)) {
//                    List<TargetingGroup> allMatchingTgForContentId = loadCorrespondingTargetGroupsListFromCache(contents);
//                    AdvertisementContent highestCTRContent = asyncEvaluator.apply(allMatchingTgForContentId);
//                    return new GeneratedAdvertisement(
//                            AdvertisementContent.builder().withContentId(highestCTRContent.getContentId())
//                                    .withRenderableContent(highestCTRContent.getRenderableContent()).build()
//                    );
//                }
//            }
//            return new EmptyGeneratedAdvertisement();
//        }
