package com.amazon.ata.advertising.service.future;

import com.amazon.ata.advertising.service.dependency.DaggerLambdaComponent;
import com.amazon.ata.advertising.service.dependency.LambdaComponent;
import com.amazon.ata.advertising.service.model.Advertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateAdActivityFutureTest {
    private final LambdaComponent daggerLambdaComponent = DaggerLambdaComponent.create();
    private static final GenerateAdvertisementRequest EMPTY_REQUEST =
            GenerateAdvertisementRequest.builder()
                    .withCustomerId(null)
                    .withMarketplaceId(null)
                    .build();
    private final Function<GenerateAdvertisementRequest, GenerateAdvertisementResponse>
            handleWithDagger = request -> daggerLambdaComponent.provideGenerateAdActivity().generateAd(request);

    @Test
    void whenGenerateAdRequest_returnHandledAdvertisement_withCorrectContent1() {
        Advertisement response = handleWithDagger.apply(
                GenerateAdvertisementRequest.builder().withCustomerId(
                        "0b633dee-9c16-11e8-98d0-529269fb1459").withMarketplaceId("ATVPDKIKX0DER").build())
                                         .getAdvertisement();
        assertEquals("Hey there person over 18, join ATA in Seattle!", response.getContent());
    }

    @Test
    void whenGenerateAdRequest_returnHandledAdvertisement_withAnIdNotEqualToTheCustomerId() {
        Advertisement response = handleWithDagger.apply(GenerateAdvertisementRequest.builder().withCustomerId(
                        "0b633dee-9c16-11e8-98d0-529269fb1459").withMarketplaceId("ATVPDKIKX0DER").build())
                                         .getAdvertisement();
        assertNotNull(response.getId());
        // checks that it's not the same as the customerId, which doesn't check for randomness but for uniqueness
        assertNotEquals("0b633dee-9c16-11e8-98d0-529269fb1459", response.getId());
    }

    @Test
    void whenGenerateAdRequestWithNoCustomerId_returnHandledAdvertisement_withNoContent() {
        assertEquals("", handleWithDagger.apply(EMPTY_REQUEST).getAdvertisement().getContent());
    }

}
