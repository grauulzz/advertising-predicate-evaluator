package com.tct.mastery.task2;

import com.amazon.ata.advertising.service.activity.dagger.GenerateAdActivityDagger;
import com.amazon.ata.advertising.service.dependency.DaggerLambdaComponent;
import com.amazon.ata.advertising.service.dependency.LambdaComponent;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static com.tct.helper.TestConstants.PARENT_PROFILE_CUSTOMER_ID;
import static com.tct.helper.TestConstants.US_MARKETPLACE_ID;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * Participants update their stream implementation to execute all predicates concurrently
 */
public class MasteryTaskTwoTests {
    private static final Logger LOG = LogManager.getLogger(MasteryTaskTwoTests.class);

    private static final int HARDCODED_SLEEP_DURATION = 200;
    // Empirical testing indicates this number is likely 88. Logging indicates 89 predicates.
    private static final int NUMBER_OF_PREDICATES = 85;
    // Empirical testing indicates this number is close to 55. Logging indicates 29 targeting groups.
    private static final int NUMBER_OF_TARGETING_GROUPS = 55;
    private static final int CONCURRENT_APPROXIMATE_EXECUTION_DURATION =
        HARDCODED_SLEEP_DURATION * NUMBER_OF_TARGETING_GROUPS;
    private static final int SERIAL_MINIMUM_EXECUTION_DURATION = HARDCODED_SLEEP_DURATION * NUMBER_OF_PREDICATES;

    LambdaComponent l = DaggerLambdaComponent.create();


    @Test
    public void generateAdvertisement_withTargetCustomerIdInMarketplace_returnsAdvertisement() {
        GenerateAdvertisementRequest request = GenerateAdvertisementRequest.builder()
            .withCustomerId(PARENT_PROFILE_CUSTOMER_ID)
            .withMarketplaceId(US_MARKETPLACE_ID)
            .build();

        long start = System.currentTimeMillis();
        GenerateAdvertisementResponse result = l.provideGenerateAdActivity().generateAd(request);
        long finish = System.currentTimeMillis();

        long elapsed = finish - start;
        LOG.info(System.out.printf("GenerateAdvertisement request took {%s} milliseconds to complete, " +
                    "expected less than {%s} ms. GenerateAdvertisement should " +
                    "evaluate predicates concurrently!",
                elapsed, SERIAL_MINIMUM_EXECUTION_DURATION));

        assertNotNull(result.getAdvertisement(), "Expected a non null advertisement in the response.");
        assertNotNull(result.getAdvertisement().getId(), "Expected the advertisement to have a non-null " +
            "content ID.");
        assertFalse(StringUtils.isBlank(result.getAdvertisement().getContent()), "Expected a non-empty " +
            "advertisement content when generating an advertisement for a customer ID with a parent profile " +
            "in marketplace ID: " + request.getMarketplaceId());
    }
}
