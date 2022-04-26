package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.future.FutureUtils;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

import static com.amazon.ata.advertising.service.future.FutureUtils.EXECUTOR_SERVICE;

/**
 * Activity class for generate ad operation.
 */
public class GenerateAdActivity {
    private final AdvertisementSelectionLogic adSelector;

    /**
     * A Coral activity for the GenerateAdvertisement API.
     *
     * @param advertisementSelector The business logic to select an ad.
     */
    @Inject
    public GenerateAdActivity(AdvertisementSelectionLogic advertisementSelector) {
        this.adSelector = advertisementSelector;
    }

    /**
     * Decides on the ad most likely to be clicked on by the provided customer, from the group of ads a customer is
     * eligible to see.
     *
     * @param request Contains the customerId to generate an advertisement for, and the marketplace id where the ad
     *                will be rendered
     *
     * @return the response will contain the generated advertisement. It's content will be an empty String if no
     * advertisement could be generated.
     */
    public GenerateAdvertisementResponse generateAd(GenerateAdvertisementRequest request) {
        String customerId = request.getCustomerId();
        String marketplaceId = request.getMarketplaceId();

        CompletableFuture<GenerateAdvertisementResponse> future =
                CompletableFuture.supplyAsync(() -> adSelector.selectAdvertisement(customerId, marketplaceId),
                        EXECUTOR_SERVICE).handleAsync(
                                (generatedAd, throwable) -> {
                                    if (throwable != null) {
                                        return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(
                                                new EmptyGeneratedAdvertisement()));
                                    }
                                    return new GenerateAdvertisementResponse(
                                            AdvertisementTranslator.toCoral(generatedAd));
                                });

        return FutureUtils.get(future);
    }

}

