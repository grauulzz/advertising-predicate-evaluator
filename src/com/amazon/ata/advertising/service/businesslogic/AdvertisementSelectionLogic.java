package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.App;
import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.RequestContext;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
    private CacheLoader<String, Map<String, List<TargetingGroup>>> cache;
    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private final UnaryOperator<List<TargetingGroup>> ctr =
            targetGroup -> targetGroup.stream().sorted(
                            Comparator.comparing(TargetingGroup::getClickThroughRate))
                                   .collect(Collectors.toList());
    private final Function<List<AdvertisementContent>, Map<String, List<TargetingGroup>>> cacheFunc;
    private Random random = new Random();

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;

        this.cacheFunc = ads -> ads.stream().map(AdvertisementContent::getContentId).collect(
                Collectors.toMap(k -> k, v -> new ArrayList<TargetingGroup>(targetingGroupDao.get(v))));

    }

    public UnaryOperator<List<TargetingGroup>> getCtrOperator() {
        return ctr;
    }

    public List<TargetingGroup> getCtrOf(List<TargetingGroup> targetingGroups) {
        return ctr.apply(targetingGroups);
    }

    public void setRandom(Random random) {
        this.random = random;
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
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.info("Marketplace id is empty, returning empty advertisement");
        }

        RequestContext context = new RequestContext(customerId, marketplaceId);
        TargetingEvaluator targetingEvaluator = new TargetingEvaluator(context);

        List<AdvertisementContent> marketPlaceContent = contentDao.get(marketplaceId);
        cache = cache(marketPlaceContent);

        Predicate<TargetingGroup> targetGroupPredicate =
                p -> targetingEvaluator.evaluate(p).equals(TargetingPredicateResult.TRUE);

        Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predicateChain =
                p1 -> p2 -> {
                    List<TargetingGroup> correspondingTargetGroups = targetingGroupDao.get(p2.getContentId());

                    return ctr.apply(correspondingTargetGroups).stream().anyMatch(p1);
                };

        return marketPlaceContent.stream().filter(predicateChain.apply(targetGroupPredicate))
                       .map(GeneratedAdvertisement::new).findFirst()
                       .orElse(new EmptyGeneratedAdvertisement());

    }

    private CacheLoader<String, Map<String, List<TargetingGroup>>> cache(
            List<AdvertisementContent> contents) {
        return new CacheLoader<>() {
            @Override
            @ParametersAreNonnullByDefault
            public Map<String, List<TargetingGroup>> load(String key) throws Exception {
                return cacheFunc.apply(contents);
            }
        };
    }

    public CacheLoader<String, Map<String, List<TargetingGroup>>> getCache() {
        return cache;
    }

    // make cache callable from another class
}

