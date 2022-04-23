package com.amazon.ata.advertising.service.activity;

import com.amazon.ata.ConsoleColors;
import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
import com.amazon.ata.advertising.service.future.AsyncUtils;
import com.amazon.ata.advertising.service.future.FutureMonitor;
import com.amazon.ata.advertising.service.future.ThreadUtilities;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.translator.AdvertisementTranslator;

import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import com.google.common.cache.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import static com.amazon.ata.ConsoleColors.*;

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

        CompletableFuture<GenerateAdvertisementResponse> future = CompletableFuture.supplyAsync(() -> adSelector.selectAdvertisement(
                customerId, marketplaceId), AsyncUtils.getExecutor
        ).handle((generatedAd, throwable) -> {
            if (throwable != null) {
                LOG.error("Error generating advertisement", throwable);
            }
            return new GenerateAdvertisementResponse(AdvertisementTranslator.toCoral(generatedAd));
        });
        monitor(future, pG);
        try {
            GenerateAdvertisementResponse r = future.get();
            AsyncUtils.getExecutor.shutdown();
            return r;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture) {
        FutureMonitor.super.monitor(completableFuture);
    }

    @Override
    public void monitor(CompletableFuture<GenerateAdvertisementResponse> completableFuture, Consumer<String> color) {
        FutureMonitor.super.monitor(completableFuture, color);
    }

    @Override
    public void onCompleteMonitor(CompletableFuture<GenerateAdvertisementResponse> onComplete) {
        ThreadUtilities.currentThreadLogger(onComplete);
        monitor(onComplete);
        onComplete.whenComplete((result, throwable) -> {
            if (throwable != null) {
                pR.accept(String.format("{%s} {%s} %n", throwable, onComplete));
            }
            boolean b = onComplete.isDone() && !onComplete.isCompletedExceptionally() && onComplete.join() != null;
            pM.accept(String.format("Completed future -> {%s} ...completed with no errors!? -> {%s}%n", result, b));
        });
    }
}

