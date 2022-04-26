package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.dependency.DynamoDBModule;
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

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * The type Advertisement selection logic.
 */
public class AdvertisementSelectionLogic {
    private final DynamoDBMapper db = new DynamoDBModule().provideDynamoDBMapper();
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
    private final Function<TargetingGroup, AdvertisementContent> loadAdContent =
            targetingGroup -> db.load(AdvertisementContent.class, targetingGroup.getContentId());
    private final Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> mapHighestCtrToContentId =
            targetingGroups -> Optional.of(targetingGroups.stream().filter(this.evalTruePredicate).reduce(
                            (group1, group2) -> group1.getClickThroughRate() >
                                                        group2.getClickThroughRate() ? group1 : group2)
                                                   .stream().map(loadAdContent).collect(Collectors.toList()));

    /**
     * Instantiates a new Advertisement selection logic.
     *
     * @param contentDao        the content dao
     * @param targetingGroupDao the targeting group dao
     */
    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
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
        this.initializePredicates.accept(customerId, marketplaceId);
        List<AdvertisementContent> contents = contentDao.get(marketplaceId);

        if (CollectionUtils.isNullOrEmpty(contents)) {
            return new EmptyGeneratedAdvertisement();
        }
        List<TargetingGroup> groups =
                contents.stream().map(AdvertisementContent::getContentId)
                        .map(targetingGroupDao::get).flatMap(List::stream)
                        .collect(Collectors.toList());
        List<AdvertisementContent> l = FutureUtils.callableAsyncProcessing(mapHighestCtrToContentId, groups);

        return new GeneratedAdvertisement(l.get(r.nextInt(l.size())));
    }

    /**
     * Gets eval true predicate.
     *
     * @return the eval true predicate
     */
    public Predicate<TargetingGroup> getEvalTruePredicate() {
        return evalTruePredicate;
    }

    /**
     * Gets eval indeterminate.
     *
     * @return the eval indeterminate
     */
    public Predicate<TargetingGroup> getEvalIndeterminate() {
        return evalIndeterminate;
    }

    /**
     * Gets eval false predicate.
     *
     * @return the eval false predicate
     */
    public Predicate<TargetingGroup> getEvalFalsePredicate() {
        return evalFalsePredicate;
    }


}
