package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
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

        Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predicateChain =
                p1 -> p2 -> (targetingGroupDao.get(p2.getContentId()).stream().anyMatch(p1));

        List<AdvertisementContent> eligibleContents =
                marketPlaceContent.stream().filter(predicateChain.apply(targetGroupPredicate))
                        .collect(Collectors.toList());

        if (!eligibleContents.isEmpty()) {
            int index = random.nextInt(eligibleContents.size());
            return new GeneratedAdvertisement(eligibleContents.get(index));
        }

        return new EmptyGeneratedAdvertisement();
    }

}


// probably a better way to structure this class

//package com.amazon.ata.advertising.service.businesslogic;
//import com.amazon.ata.advertising.service.dao.ReadableDao;
//import com.amazon.ata.advertising.service.model.AdvertisementContent;
//import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
//import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
//import com.amazon.ata.advertising.service.model.RequestContext;
//import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
//import com.amazon.ata.advertising.service.targeting.TargetingGroup;
//import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
//
//import java.util.List;
//import java.util.Random;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import javax.inject.Inject;
//
///**
// * This class is responsible for picking the advertisement to be rendered.
// */
//public class AdvertisementSelectionLogic {
//
//    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
//
//    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
//    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
//    private Random random = new Random();
//
//    private final BiFunction<String, String, TargetingEvaluator> evalContext = (customerId1, marketplaceId1) -> {
//        RequestContext context = new RequestContext(customerId1, marketplaceId1);
//        return new TargetingEvaluator(context);
//    };
//
//    private final Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predicateChain;
//
//
//
//    @Inject
//    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
//                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
//        this.contentDao = contentDao;
//        this.targetingGroupDao = targetingGroupDao;
//        this.predicateChain = p1 -> p2 -> (this.targetingGroupDao.get(p2.getContentId()).stream().anyMatch(p1));
//    }
//
//    public void setRandom(Random random) {
//        this.random = random;
//    }
//
//    /**
//     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
//     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
//     * EmptyGeneratedAdvertisement.
//     *
//     * @param customerId - the customer to generate a custom advertisement for
//     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
//     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
//     *     not be generated.
//     */
//    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {
//        if (StringUtils.isEmpty(marketplaceId)) {
//            LOG.info("Marketplace id is empty, returning empty advertisement");
//            return new EmptyGeneratedAdvertisement();
//        }
//
//        final List<AdvertisementContent> marketPlaceContent = contentDao.get(marketplaceId);
//
//        Predicate<TargetingGroup> targetGroupPredicate =
//                p ->  evalContext.apply(customerId, marketplaceId)
//                              .evaluate(p)
//                              .equals(TargetingPredicateResult.TRUE);
//
//
//        List<AdvertisementContent> eligibleContents =
//                marketPlaceContent.stream().filter(predicateChain.apply(targetGroupPredicate))
//                        .collect(Collectors.toList());
//
//        if (!eligibleContents.isEmpty()) {
//            int index = random.nextInt(eligibleContents.size());
//            return new GeneratedAdvertisement(eligibleContents.get(index));
//        }
//
//        return new EmptyGeneratedAdvertisement();
//    }
//
//}
