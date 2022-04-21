package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.App;
import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.model.Advertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.google.common.cache.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 *
 * Activity class for generate ad operation.
 *
 */
public class GenerateAdActivity implements FutureMonitor<GenerateAdvertisementResponse> {
    private static final Logger LOG = LogManager.getLogger(GenerateAdActivity.class);
    private static final List<GenerateAdvertisementResponse> results = new ArrayList<>();
    private final AdvertisementSelectionLogic adSelector;

    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    private LoadingCache<String, Map<String, List<TargetingGroup>>> loadingCache;

    /**
     * A Coral activity for the GenerateAdvertisement API.
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
//        targetingGroupMap = loadingCache.asMap();
    }

    GenerateAdvertisementResponse sortSelectedAdByCtr(List<TargetingGroup> tgs) {
        List<TargetingGroup> sortedTgs = adSelector.getCtrOf(tgs);
        String key = sortedTgs.get(0).getContentId();
        List<Advertisement> ads = results.stream().map(GenerateAdvertisementResponse::getAdvertisement).collect(Collectors.toList());

        return results.stream().filter(r -> r.getAdvertisement().getId().equals(key)).findFirst().orElse(null);
    }

    private List<TargetingGroup> loadCachedRes(String key) {
        ConcurrentMap<String, Map<String, List<TargetingGroup>>> res = loadingCache.asMap();

        return res.get(key).values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    /**
     * Decides on the ad most likely to be clicked on by the provided customer, from the group of ads a customer is
     * eligible to see.
     * @param request Contains the customerId to generate an advertisement for, and the marketplace id where the ad
     *                will be rendered
     * @return the response will contain the generated advertisement. It's content will be an empty String if no
     *      advertisement could be generated.
     */
    public GenerateAdvertisementResponse generateAd(GenerateAdvertisementRequest request) {
        String customerId = request.getCustomerId();
        String marketplaceId = request.getMarketplaceId();

        CompletableFuture<GenerateAdvertisementResponse> future =
                CompletableFuture.supplyAsync(() -> adSelector.selectAdvertisement(
                        customerId, marketplaceId), executorService).handle((ad, throwable) -> {
                            if (throwable != null) LOG.error("Error generating advertisement", throwable);
                            return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(ad));
                        });
        monitor(future);
        onComplete(future);

        CacheLoader<String, Map<String, List<TargetingGroup>>> cached = adSelector.getCache();
        try {
            cached.load(customerId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return results.get(0);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void onComplete(CompletableFuture<GenerateAdvertisementResponse> onComplete) {
        executorService.execute(() -> {
            try {
                GenerateAdvertisementResponse ad = onComplete.get();
                results.add(ad);
                LOG.info(System.out.printf("result -> %n{%s}%n" , ad));
                App.toJson.accept(results);
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new AdvertisementClientException(e);
            }
        });
    }

}

