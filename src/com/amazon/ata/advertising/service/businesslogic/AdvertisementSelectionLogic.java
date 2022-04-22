package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.ConsoleColors;
import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.*;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);
    private final Maps.EntryTransformer<String, String, TargetingEvaluator> transformer = (customerId1, marketplaceId1) -> {
        RequestContext context = new RequestContext(customerId1, marketplaceId1);
        return new TargetingEvaluator(context);
    };
    private CacheLoader<String, Map<String, List<TargetingGroup>>> cache;
    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private final UnaryOperator<List<TargetingGroup>> ctr =
            targetGroup -> targetGroup.stream().sorted(
                            Comparator.comparing(TargetingGroup::getClickThroughRate))
                                   .collect(Collectors.toList());
    private final Function<List<AdvertisementContent>, Map<String, List<TargetingGroup>>> tgMap;
    private Random r = new Random();

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;

        this.tgMap = ads -> ads.stream().map(AdvertisementContent::getContentId).collect(
                Collectors.toMap(k -> k, v -> new ArrayList<TargetingGroup>(targetingGroupDao.get(v))));

    }



    public void setRandom(Random random) {
        this.r = random;
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
        GeneratedAdvertisement generatedAdvertisement = new EmptyGeneratedAdvertisement();
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.info("Marketplace id is empty, returning empty advertisement");
        } else {
            final List<AdvertisementContent> contents = contentDao.get(marketplaceId);

            if (CollectionUtils.isNotEmpty(contents)) {
                TargetingEvaluator targetingEvaluator = transformer.transformEntry(customerId, marketplaceId);

                logg1(customerId, marketplaceId);

                Predicate<TargetingGroup> filterAllTrue =
                        p ->  targetingEvaluator.evaluate(p).equals(TargetingPredicateResult.TRUE);

                Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predChain =
                        p1 -> p2 -> targetingGroupDao.get(p2.getContentId()).stream().anyMatch(p1);

                List<AdvertisementContent> eligibleAds = contents.stream().filter(predChain.apply(filterAllTrue))
                                                                     .collect(Collectors.toList());
                if (!eligibleAds.isEmpty()) {
                    int index = r.nextInt(eligibleAds.size());
                    AdvertisementContent randomAd = eligibleAds.get(index);
                    generatedAdvertisement = new GeneratedAdvertisement(randomAd);
                }

            }

        }
        return generatedAdvertisement;
    }


    public List<TargetingGroup> getCtrOf(List<TargetingGroup> targetingGroups) {
        return ctr.apply(targetingGroups);
    }

    public List<TargetingGroup> getTGfromAdContent(AdvertisementContent adContent) {
        return targetingGroupDao.get(adContent.getContentId());
    }

    private CacheLoader<String, Map<String, List<TargetingGroup>>> cache(
            List<AdvertisementContent> contents) {
        return new CacheLoader<>() {
            @Override
            @ParametersAreNonnullByDefault
            public Map<String, List<TargetingGroup>> load(String key) throws Exception {
                return tgMap.apply(contents);
            }
        };
    }

    public CacheLoader<String, Map<String, List<TargetingGroup>>> getCache() {
        return cache;
    }
    private void logg2(String customerId, GeneratedAdvertisement res) {
        String log = String.format("generated ad for: {%s} -> %n{%s}%n ", customerId, res);
        ConsoleColors.pM.accept(log);
    }

    private void logg1(String customerId, String marketplaceId) {
        String log = String.format("selecting ad for: {%s} in marketplace {%s}", customerId, marketplaceId);
        ConsoleColors.pM.accept(log);
    }

    public AdvertisementContent getAdContentFromTG(TargetingGroup tg) {
        return contentDao.get(tg.getContentId()).stream().findFirst().orElse(null);
    }
}
