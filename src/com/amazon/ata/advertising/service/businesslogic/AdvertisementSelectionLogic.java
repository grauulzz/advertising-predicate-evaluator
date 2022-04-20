package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ContentDao;
import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);

    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private Random random = new Random();

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
     * EmptyGeneratedAdvertisement.
     *
     * @param customerId - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
     *     not be generated.
     */
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.info("Marketplace id is empty, returning empty advertisement");
        }

        RequestContext context = new RequestContext(customerId, marketplaceId);
        TargetingEvaluator targetingEvaluator = new TargetingEvaluator(context);

        final List<AdvertisementContent> marketPlaceContent = contentDao.get(marketplaceId);

        Predicate<TargetingGroup> targetGroupPredicate =
                p ->  targetingEvaluator.evaluate(p).equals(TargetingPredicateResult.TRUE);

        UnaryOperator<List<TargetingGroup>> ctr =
                targetGroup -> targetGroup.stream().sorted(
                        Comparator.comparing(TargetingGroup::getClickThroughRate))
                             .collect(Collectors.toList());

        Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predicateChain =
                p1 -> p2 -> {
                    List<TargetingGroup> correspondingTargetGroups = targetingGroupDao.get(p2.getContentId());

                    return ctr.apply(correspondingTargetGroups).stream().anyMatch(p1);
                };


        List<AdvertisementContent> eligibleContents =
                marketPlaceContent.stream().filter(predicateChain.apply(targetGroupPredicate))
                        .collect(Collectors.toList());

        if (!eligibleContents.isEmpty()) {
            int index = random.nextInt(eligibleContents.size());
            return new GeneratedAdvertisement(eligibleContents.get(0));
        }

        return new EmptyGeneratedAdvertisement();
    }

    public Supplier<GeneratedAdvertisement> getSupplier(String s1, String s2) {
        return () -> selectAdvertisement(s1, s2);
    }
}


//        final AtomicReference<TargetingGroup> maxClickThroughRateGroup = new AtomicReference<>();
//                    maxClickThroughRateGroup.set(maxClickThroughRate.apply(correspondingTargetGroups));

//         Function<List<TargetingGroup>, TargetingGroup> maxClickThroughRate2 =
//                p -> p.stream().max(Comparator.comparing(TargetingGroup::getClickThroughRate))
//                        .orElse(new TargetingGroup());
//        Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predicateChain =
//                p1 -> p2 -> {
//                    List<TargetingGroup> correspondingTargetGroups = targetingGroupDao.get(p2.getContentId());
//                    List<TargetingGroup> filteredTargetGroups = correspondingTargetGroups.stream()
//                                                                        .filter(p1)
//                                                                        .collect(Collectors.toList());
//                    return maxClickThroughRate.apply(filteredTargetGroups).stream().anyMatch(p1);
//                };





//         List<TargetingGroup> correspondingTargetGroups = eligibleContents.stream()
//                                                                 .map(AdvertisementContent::getContentId)
//                                                                 .map(targetingGroupDao::get)
//                                                                 .map(maxClickThroughRate)
//                                                                 .flatMap(Collection::stream)
//                                                                 .collect(Collectors.toList());
//        List<AdvertisementContent> contents = correspondingTargetGroups.stream()
//                                                      .map(TargetingGroup::getContentId)
//                                                      .map(contentDao::get)
//                                                      .flatMap(Collection::stream)
//                                                      .collect(Collectors.toList());

