package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.dependency.DynamoDBModule;
import com.amazon.ata.advertising.service.future.FutureUtils;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvertisementSelectionLogic {
    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
    private final DynamoDBMapper db = new DynamoDBModule().provideDynamoDBMapper();
    private Random r = new Random();
    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    public Predicate<TargetingGroup> filterByTrue;
    private Predicate<TargetingGroup> filterByFalse;
    private Predicate<TargetingGroup> filterByIntr;
    private final BiConsumer<String, String> predicatesInit = (customerId, marketPlaceId) -> {
        RequestContext context = new RequestContext(customerId, marketPlaceId);
        TargetingEvaluator evaluator = new TargetingEvaluator(context);
        filterByTrue = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.TRUE);
        filterByFalse = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.FALSE);
        filterByIntr = pred -> evaluator.evaluate(pred).equals(TargetingPredicateResult.INDETERMINATE);
    };

    private final Function<TargetingGroup, AdvertisementContent> getCorresponding =
            targetingGroup -> db.load(AdvertisementContent.class, targetingGroup.getContentId());
    private final Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> mapHighestCtrToContentId =
            targetingGroups -> Optional.of(targetingGroups.stream().filter(filterByTrue).reduce(
                            (group1, group2) -> group1.getClickThroughRate() >
                                                        group2.getClickThroughRate() ? group1 : group2)
                                                   .stream().map(getCorresponding).collect(Collectors.toList()));


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
        predicatesInit.accept(customerId, marketplaceId);

        List<TargetingGroup> groups =
                contentDao.get(marketplaceId).stream().map(AdvertisementContent::getContentId)
                        .map(targetingGroupDao::get).flatMap(List::stream)
                        .collect(Collectors.toList());

        List<AdvertisementContent> l = FutureUtils.appyAsyncProcessing(mapHighestCtrToContentId, groups);
        return new GeneratedAdvertisement(l.get(r.nextInt(l.size())));
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




















// if (marketplaceId == null || StringUtils.isEmpty(marketplaceId)) {
//            return new EmptyGeneratedAdvertisement();
//        } else {
//            List<AdvertisementContent> contents = cache.getUnchecked(marketplaceId).orElseGet(() -> {
//                List<AdvertisementContent> content = contentDao.get(marketplaceId);
//                cache.putAll(Collections.singletonMap(marketplaceId, Optional.ofNullable(content)));
//                return content;
//            });
//            if (CollectionUtils.isNotEmpty(contents)) {
//                predicatesInit.accept(customerId, marketplaceId);
//                List<TargetingGroup> allMatchingTgForContentId = loadCorrespondingContentFromTg(contents);
//                this.futureAdContents = contents.stream().map(
//                        c -> CompletableFuture.supplyAsync(() -> asyncEvaluator.apply(allMatchingTgForContentId),
//                                FutureUtils.getExecutor)
//                ).collect(Collectors.toList());
//
//            } else {
//                return new EmptyGeneratedAdvertisement();
//            }
//        }
//
//        CompletableFuture<List<AdvertisementContent>> sequencedFutures =
//                FutureUtils.sequenceFuture(this.futureAdContents);
//
//        monitor(sequencedFutures, ConsoleLogger.CYAN.getColor());
//
//        List<AdvertisementContent> ads = FutureUtils.get(sequencedFutures);
//
//        if (CollectionUtils.isNotEmpty(ads)) {
//            AdvertisementContent adv = ads.get(r.nextInt(ads.size()));
//            return new GeneratedAdvertisement(adv);
//        } else {
//            return new EmptyGeneratedAdvertisement();
//        }


