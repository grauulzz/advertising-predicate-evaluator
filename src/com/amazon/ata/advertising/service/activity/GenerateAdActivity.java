package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.function.Consumer;
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
    public GenerateAdvertisementResponse generateAd(GenerateAdvertisementRequest request) {
        String customerId = request.getCustomerId();
        String marketplaceId = request.getMarketplaceId();
//        LOG.info(System.out.printf("Generating ad for customerId: %s in marketplace: %s", customerId, marketplaceId));
        List<GenerateAdvertisementResponse> results = new ArrayList<>();
        try {
            GeneratedAdvertisement adSelectorSupplier = adSelector.selectAdvertisement(customerId, marketplaceId);
            CompletableFuture<GenerateAdvertisementResponse> response =
                    CompletableFuture.supplyAsync(() -> adSelectorSupplier, forkJoinPool)
                            .handle((ad, throwable) -> {
                                if (throwable != null) LOG.error("Error generating advertisement", throwable);

                                return GenerateAdvertisementResponse.builder()
                                               .withAdvertisement(AdvertisementTranslator.toCoral(ad))
                                               .build();
                            });

            monitor(response, forkJoinPool);

            results.add(response.whenComplete((r, t) -> {
                if (t != null) LOG.error("Error generating advertisement", t);
                LOG.info(System.out.printf("Generated ad: %s for customerId: %s in marketplace: %s", r, customerId, marketplaceId));
            }).get());

        } catch (RuntimeException e) {
            LOG.error(System.out.printf(
                    "Something unexpected happened when calling GenerateAdvertisement for customer, %s, in marketplace %s.",
                    request.getCustomerId(),
                    request.getMarketplaceId()), e);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error(e.getCause());
            throw new AdvertisementClientException(e);
        }

        LOG.info(System.out.printf("Results List: %s",  new Gson().toJson(results)));
        return results.get(0);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture, ForkJoinPool forkJoinPool) {
        FutureMonitor.super.monitor(completableFuture, forkJoinPool);
    }

    @Override
    public void monitor() {
        FutureMonitor.super.monitor();
    }

    @Override
    public void onComplete(Consumer<GenerateAdvertisementResponse> onComplete) {}

//    @Override
//    public void monitorFutureObject(CompletableFuture<GenerateAdvertisementResponse> future) {
//        FutureMonitor.super.monitorFutureObject(future);
//    }
}
//        try {
//            return results.stream().sorted().findFirst()
//                           .orElse(GenerateAdvertisementResponse.builder()
//                                           .withAdvertisement(AdvertisementTranslator.toCoral(
//                                                   new EmptyGeneratedAdvertisement()))
//                                           .build());
//
//        } catch (NoSuchElementException | ClassCastException e) {
//            LOG.error(System.out.printf(
//                    "Something unexpected happened when calling GenerateAdvertisement for customer, %s, in marketplace %s.",
//                    request.getCustomerId(),
//                    request.getMarketplaceId()), e);
//            return GenerateAdvertisementResponse.builder()
//                           .withAdvertisement(AdvertisementTranslator.toCoral(
//                                   new EmptyGeneratedAdvertisement()))
//                           .build();
//        }
