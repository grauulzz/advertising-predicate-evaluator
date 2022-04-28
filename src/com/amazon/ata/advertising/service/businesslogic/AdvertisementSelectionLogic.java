package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.future.FutureUtils;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * The type Advertisement selection logic.
 */
public class AdvertisementSelectionLogic {
    private final DynamoDBMapper db;
    private final Random r = new Random();
    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private Predicate<TargetingGroup> evalTruePredicate;
    private Predicate<TargetingGroup> evalFalsePredicate;
    private Predicate<TargetingGroup> evalIndeterminate;
    private final BiConsumer<String, String> initializePredicates = (customerId, marketPlaceId) -> {
        RequestContext context = new RequestContext(customerId, marketPlaceId);
        TargetingEvaluator evaluator = new TargetingEvaluator(context);
        evalTruePredicate = p -> evaluator.evaluate(p).equals(TargetingPredicateResult.TRUE);
        evalFalsePredicate = p -> evaluator.evaluate(p).equals(TargetingPredicateResult.FALSE);
        evalIndeterminate = p -> evaluator.evaluate(p).equals(TargetingPredicateResult.INDETERMINATE);
    };

    private final Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> mapHighestCtrToContentId =
            tg -> Optional.of(tg.stream().filter(getEvalTruePredicate()).reduce(
                            (tg1, tg2) -> tg1.getClickThroughRate() > tg2.getClickThroughRate() ? tg1 : tg2)
                                      .stream()
                                      .map(TargetingGroup::getContentId)
                                      .map(id -> getDb().load(AdvertisementContent.class, id))
                                      .collect(Collectors.toList()));

    /**
     * Instantiates a new Advertisement selection logic.
     *
     * @param contentDao        the content dao
     * @param targetingGroupDao the targeting group dao
     */
    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao,
                                       DynamoDBMapper db) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
        this.db = db;
    }

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.
     * Returns the eligible content with the highest click through rate.
     *
     * If no advertisement is available or eligible, returns an EmptyGeneratedAdvertisement.
     *
     * @param customerId    - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     *
     * @return an advertisement customized for the customer id provided, or an empty advertisement
     * if one could not be generated.
     */
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isNullOrEmpty(marketplaceId)) {
            return new EmptyGeneratedAdvertisement();
        }
        RequestContext context = new RequestContext(customerId, marketplaceId);
        TargetingEvaluator evaluator = new TargetingEvaluator(context);
        final List<AdvertisementContent> contents = contentDao.get(marketplaceId);
        final List<TargetingGroup> groups = contents.stream().map(AdvertisementContent::getContentId)
                        .map(targetingGroupDao::get).flatMap(List::stream)
                        .collect(Collectors.toList());

        if (CollectionUtils.isNullOrEmpty(contents)) {
            return new EmptyGeneratedAdvertisement();
        }
        List<TargetingGroup> groups =
                contents.stream().map(AdvertisementContent::getContentId)
                        .map(targetingGroupDao::get).flatMap(List::stream)
                        .collect(Collectors.toList());
        List<AdvertisementContent> l = FutureUtils.sequenceFutures(mapHighestCtrToContentId, groups);

        final Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> mapHighestCtrToContentId =
                targetingGroups -> Optional.of(targetingGroups.stream().filter(
                                tg -> evaluator.evaluteTgConcurently(tg).equals(TargetingPredicateResult.TRUE)).reduce(
                                (group1, group2) -> group1.getClickThroughRate() >
                                                            group2.getClickThroughRate() ? group1 : group2)
                                                       .stream().map(TargetingGroup::getContentId)
                                                       .map(id -> db.load(AdvertisementContent.class, id))
                                                       .collect(Collectors.toList()));



        List<AdvertisementContent> l = mapHighestCtrToContentId.apply(groups).orElse(Collections.emptyList());

    private DynamoDBMapper getDb() {
        return db;
    }

        return new GeneratedAdvertisement(l.get(r.nextInt(l.size())));
    }
}
