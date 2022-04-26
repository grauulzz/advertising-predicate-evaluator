package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.advertising.service.future.FutureUtils;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static com.amazon.ata.advertising.service.future.FutureUtils.getExecutor;

/**
 *
 * Activity class for generate ad operation.
 *
 */
public class GenerateAdActivity implements FutureMonitor<GenerateAdvertisementResponse> {
    private static final Logger LOG = LogManager.getLogger(GenerateAdActivity.class);
    private final AdvertisementSelectionLogic adSelector;

    /**
     * A Coral activity for the GenerateAdvertisement API.
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
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

        CompletableFuture<GenerateAdvertisementResponse> future = CompletableFuture.supplyAsync(
                () -> adSelector.selectAdvertisement(customerId, marketplaceId), getExecutor).handleAsync(
                        (generatedAd, throwable) -> {
                            if (throwable != null) {
                                return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(
                                        new EmptyGeneratedAdvertisement()));
                            }
                            return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(generatedAd));
                        });
        monitor(future, ConsoleLogger.MAGENTA.getColor());
        return FutureUtils.get(future);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture, Consumer<String> color) {
        FutureMonitor.super.monitor(completableFuture, color);
    }
}

