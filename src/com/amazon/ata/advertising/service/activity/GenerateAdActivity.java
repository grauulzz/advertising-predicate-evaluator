package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 *
 * Activity class for generate ad operation.
 *
 */
public class GenerateAdActivity {
    private static final Logger LOG = LogManager.getLogger(GenerateAdActivity.class);

    private final AdvertisementSelectionLogic adSelector;

    private ExecutorService executor = Executors.newCachedThreadPool();

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
//        LOG.info(System.out.printf("Generating ad for customerId: %s in marketplace: %s", customerId, marketplaceId));
        List<GenerateAdvertisementResponse> results = new ArrayList<>();
        try {
            GeneratedAdvertisement adSelectorSupplier = adSelector.selectAdvertisement(customerId, marketplaceId);
            CompletableFuture<GenerateAdvertisementResponse> response =
                    CompletableFuture.supplyAsync(() -> adSelectorSupplier, executor)
                            .handle((ad, throwable) -> {
                                if (throwable != null) {
                                    LOG.error("Error generating advertisement", throwable);
                                }
                                return GenerateAdvertisementResponse.builder()
                                       .withAdvertisement(AdvertisementTranslator.toCoral(ad))
                                       .build();
            });

            return response.whenComplete((r, t) -> {
                if (t != null) {
                    LOG.error("Error generating advertisement", t);
                }
                results.add(r);
                LOG.info(System.out.printf(new Gson().toJson(results)));
            }).get();

        } catch (RuntimeException e) {
            LOG.error(System.out.printf(
                "Something unexpected happened when calling GenerateAdvertisement for customer, %s, in marketplace %s.",
                request.getCustomerId(),
                request.getMarketplaceId()), e);
            return GenerateAdvertisementResponse.builder()
                    .withAdvertisement(AdvertisementTranslator.toCoral(
                            new EmptyGeneratedAdvertisement()))
                    .build();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
