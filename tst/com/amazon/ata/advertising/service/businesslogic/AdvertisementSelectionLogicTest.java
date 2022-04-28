package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ContentDao;
import com.amazon.ata.advertising.service.dao.TargetingGroupDao;
import com.amazon.ata.advertising.service.dependency.DaggerLambdaComponent;
import com.amazon.ata.advertising.service.dependency.LambdaComponent;
import com.amazon.ata.advertising.service.model.Advertisement;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.model.EmptyGeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.GeneratedAdvertisement;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


class AdvertisementSelectionLogicTest {

    private final LambdaComponent daggerLambdaComponent = DaggerLambdaComponent.create();
    private final Function<GenerateAdvertisementRequest, GenerateAdvertisementResponse>
            handleWithDagger = request -> daggerLambdaComponent.provideGenerateAdActivity().generateAd(request);
    private final ContentDao cd = daggerLambdaComponent.provideContentDao();
    private final TargetingGroupDao td = daggerLambdaComponent.provideTargetingGroupDao();

    private final DynamoDBMapper db = daggerLambdaComponent.provideDynamoDBMapper();
    private final AdvertisementSelectionLogic adSelectionService = new AdvertisementSelectionLogic(cd, td, db);

    @Test
    void whenGenerateAdRequest_returnHandledAdvertisement_withCorrectContent1() {
        Advertisement response = handleWithDagger.apply(
                        GenerateAdvertisementRequest.builder().withCustomerId(
                                "0b633dee-9c16-11e8-98d0-529269fb1459").withMarketplaceId("ATVPDKIKX0DER").build())
                                         .getAdvertisement();
        Assertions.assertEquals("Hey there person over 18, join ATA in Seattle!", response.getContent());
    }

    @Test
    void whenGenerateAdRequest_returnHandledAdvertisement_withAnIdNotEqualToTheCustomerId() {
        Advertisement response = handleWithDagger.apply(GenerateAdvertisementRequest.builder().withCustomerId(
                        "0b633dee-9c16-11e8-98d0-529269fb1459").withMarketplaceId("ATVPDKIKX0DER").build())
                                         .getAdvertisement();
        Assertions.assertNotNull(response.getId());
        // checks that it's not the same as the customerId, which doesn't check for randomness but for uniqueness
        Assertions.assertNotEquals("0b633dee-9c16-11e8-98d0-529269fb1459", response.getId());
    }


    @ParameterizedTest
    @CsvSource({"0b633dee-9c16-11e8-98d0-529269fb1459,  ",      // null marketplaceId
                "0b633dee-9c16-11e8-98d0-529269fb1459, 2",     // invalid marketplace
                ":[, 1",                                      // invalid customerId
                "0b633dee-9c16-11e8-98d0-529269fb1459, ' '"  // empty marketplaceId
    })
    void selectAdvertisement(String id, String marketPlaceId) {
        GeneratedAdvertisement ad =
                adSelectionService.selectAdvertisement(id, marketPlaceId);

        Assertions.assertTrue(ad instanceof EmptyGeneratedAdvertisement);
    }

    @Test
    void selectAdvertisement_oneAd_returnsAd() {
        GeneratedAdvertisement ad =
                adSelectionService.selectAdvertisement("0b633dee-9c16-11e8-98d0-529269fb1459", "1");

        Assertions.assertNotNull(ad);
    }

    @Test
    void selectAdvertisement_multipleAds_returnsOneAdd() {

        AdvertisementContent ACTUAL_CONTENT = AdvertisementContent.builder().withContentId(
                "0b633dee-9c16-11e8-98d0-529269fb1459").build();

        // these will all be null because they don't exist in the database
        AdvertisementContent C1 = AdvertisementContent.builder().withContentId(UUID.randomUUID().toString()).build();
        AdvertisementContent C2 = AdvertisementContent.builder().withContentId(UUID.randomUUID().toString()).build();
        AdvertisementContent C3 = AdvertisementContent.builder().withContentId(UUID.randomUUID().toString()).build();

        List<AdvertisementContent> contents = new ArrayList<>();
        contents.add(ACTUAL_CONTENT);
        contents.add(C1);
        contents.add(C2);
        contents.add(C3);

        contents.stream().map(c -> adSelectionService.selectAdvertisement(c.getContentId(), "1"))
                .filter(Objects::nonNull)
                .forEach(Assertions::assertNotNull);
    }


}
