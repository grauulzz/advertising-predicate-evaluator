package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.ConsoleColors;
import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.*;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import com.google.common.cache.CacheLoader;
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
    private final ReadableDao<String, List<TargetingGroup>> tgdao;
    private final UnaryOperator<List<TargetingGroup>> ctr =
            targetGroup -> targetGroup.stream().sorted(
                            Comparator.comparing(TargetingGroup::getClickThroughRate))
                                   .collect(Collectors.toList());
    private final Function<List<AdvertisementContent>, Map<String, List<TargetingGroup>>> cacheFunc;
    private Random r = new Random();

    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.tgdao = targetingGroupDao;

        this.cacheFunc = ads -> ads.stream().map(AdvertisementContent::getContentId).collect(
                Collectors.toMap(k -> k, v -> new ArrayList<TargetingGroup>(tgdao.get(v))));

    }

    public UnaryOperator<List<TargetingGroup>> getCtrOperator() {
        return ctr;
    }

    public List<TargetingGroup> getCtrOf(List<TargetingGroup> targetingGroups) {
        return ctr.apply(targetingGroups);
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
        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.info("Marketplace id is empty, returning empty advertisement");
        }

        RequestContext context = new RequestContext(customerId, marketplaceId);
        TargetingEvaluator targetingEvaluator = new TargetingEvaluator(context);

        logg1(customerId, marketplaceId);

        List<AdvertisementContent> marketPlaceContent = contentDao.get(marketplaceId);
//        cache = cache(marketPlaceContent);

        Predicate<TargetingGroup> allTrue =
                p ->  targetingEvaluator.evaluate(p).equals(TargetingPredicateResult.TRUE);

        Function<Predicate<TargetingGroup>, Predicate<AdvertisementContent>> predChain =
                p1 -> p2 -> getTGfromAdContent(p2).stream().anyMatch(p1);

        List<AdvertisementContent> adContentFromTG = marketPlaceContent.stream().filter(predChain.apply(allTrue))
                                                    .collect(Collectors.toList());
//        cache = cache(adContentFromTG);


        GeneratedAdvertisement res = adContentFromTG.stream().map(GeneratedAdvertisement::new)
                                             .findFirst().orElse(new EmptyGeneratedAdvertisement());

        logg2(customerId, res);

        return res;

    }

    private void logg2(String customerId, GeneratedAdvertisement res) {
        String log = String.format("generated ad for: {%s} -> %n{%s}%n ", customerId, res);
        ConsoleColors.pM.accept(log);
    }

    private void logg1(String customerId, String marketplaceId) {
        String log = String.format("selecting ad for: {%s} in marketplace {%s}", customerId, marketplaceId);
        ConsoleColors.pM.accept(log);
    }

    private List<TargetingGroup> getTGfromAdContent(AdvertisementContent adContent) {
        return tgdao.get(adContent.getContentId());
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

}