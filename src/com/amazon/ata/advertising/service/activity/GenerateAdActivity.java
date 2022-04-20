package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.future.ThreadSleep;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
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

    private final AdvertisementSelectionLogic adSelector;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();

    /**
     * A Coral activity for the GenerateAdvertisement API.
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
    }

    static  <T> boolean compare(Comparable<T> left, T right, int min, int max) {
        int comparison = left.compareTo(right);
        return comparison >= min && comparison <= max;
    }
    /**
     * Decides on the ad most likely to be clicked on by the provided customer, from the group of ads a customer is
     * eligible to see.
     * @param request Contains the customerId to generate an advertisement for, and the marketplace id where the ad
     *                will be rendered
     * @return the response will contain the generated advertisement. It's content will be an empty String if no
     *      advertisement could be generated.
     */
    private static final List<GenerateAdvertisementResponse> results = new ArrayList<>();
    public GenerateAdvertisementResponse generateAd(GenerateAdvertisementRequest request) {
        String customerId = request.getCustomerId();
        String marketplaceId = request.getMarketplaceId();
        GeneratedAdvertisement adSelectorSupplier = adSelector.selectAdvertisement(customerId, marketplaceId);
        CompletableFuture<GenerateAdvertisementResponse> future =
                CompletableFuture.supplyAsync(() -> adSelectorSupplier, forkJoinPool)
                        .handle((ad, throwable) -> {
                            if (throwable != null) LOG.error("Error generating advertisement", throwable);

                            return GenerateAdvertisementResponse.builder()
                                           .withAdvertisement(AdvertisementTranslator.toCoral(ad))
                                           .build();
                        });
        monitor(future);

        onComplete(future);

        return results.get(0);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public <T> void onComplete(CompletableFuture<T> onComplete) {
        ForkJoinPool.commonPool().execute(() -> {
            try {
                GenerateAdvertisementResponse ad = (GenerateAdvertisementResponse) onComplete.get();
                results.add(ad);
                LOG.info(System.out.printf("ForkJoinPool.execute(() -> %n{%s}%n" , results));
                toJson.accept(ad);
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new AdvertisementClientException(e);
            }
        });
        boolean r = ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.SECONDS);
        if (!r) {
            throw new AdvertisementClientException("Timeout waiting for advertisement generation to complete");
        }
        ForkJoinPool.commonPool().shutdown();
    }

}

