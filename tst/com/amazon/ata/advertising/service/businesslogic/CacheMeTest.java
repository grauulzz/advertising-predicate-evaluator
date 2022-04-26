package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dependency.DaggerLambdaComponent;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CacheMeTest {
    AdvertisementSelectionLogic adLogic = new AdvertisementSelectionLogic(DaggerLambdaComponent.create().provideContentDao(),
            DaggerLambdaComponent.create().provideTargetingGroupDao());

    GenerateAdvertisementRequest generateRequest = new GenerateAdvertisementRequest("A123B456", "ATVPDKIKX0DER");



//               {tgId}                      {ctr}                   {contentId}                           {tgPred}
// 8b4e796d-d251-4c07-9e45-875220f4810d	 |  0.03437  |  0b632e26-9c16-11e8-98d0-529269fb1459  |  [{"@class":"com.amazon.ata.advertising.service.targeting.predicate.AgeTargetingPredicate","inverse":false,"targetedAgeRange":"AGE_31_TO_35"},{"@class":"com.amazon.ata.advertising.service.targeting.predicate.CategorySpendFrequencyTargetingPredicate","inverse":false,"targetedCategory":"FRESH","comparison":"GT","targetedNumberOfPurchases":0}]
// 249959cc-eab8-4d76-8434-4803fc877246	 |  0.0073   |  0b632e26-9c16-11e8-98d0-529269fb1459  |  [{"@class":"com.amazon.ata.advertising.service.targeting.predicate.AgeTargetingPredicate","inverse":false,"targetedAgeRange":"AGE_22_TO_25"},{"@class":"com.amazon.ata.advertising.service.targeting.predicate.CategorySpendFrequencyTargetingPredicate","inverse":false,"targetedCategory":"FRESH","comparison":"GT","targetedNumberOfPurchases":0}]
// b389bc42-7a18-4196-bc4f-7a0515728311	 |  0.03929  |  0b632e26-9c16-11e8-98d0-529269fb1459  |  [{"@class":"com.amazon.ata.advertising.service.targeting.predicate.AgeTargetingPredicate","inverse":false,"targetedAgeRange":"AGE_18_TO_21"},{"@class":"com.amazon.ata.advertising.service.targeting.predicate.CategorySpendFrequencyTargetingPredicate","inverse":false,"targetedCategory":"FRESH","comparison":"GT","targetedNumberOfPurchases":0}]
// 1d3686dd-9211-46e6-b315-abcbeeb3c97d	 |  0.18007  |  0b632e26-9c16-11e8-98d0-529269fb1459  |  [{"@class":"com.amazon.ata.advertising.service.targeting.predicate.AgeTargetingPredicate","inverse":false,"targetedAgeRange":"AGE_26_TO_30"},{"@class":"com.amazon.ata.advertising.service.targeting.predicate.CategorySpendFrequencyTargetingPredicate","inverse":false,"targetedCategory":"FRESH","comparison":"GT","targetedNumberOfPurchases":0}]

    // 4caf11df-4c2a-43b1-b85a-8137189b282f
    // cd00d1f6-9b48-11e8-98d0-529269fb1459	 | ATVPDKIKX0DER | Hey there parent, join ATA!
    Function<GenerateAdvertisementRequest, GenerateAdvertisementResponse> daggerHandler =
            request -> DaggerLambdaComponent.create().provideGenerateAdActivity().generateAd(request);
    @Test
    void handleRequestNotNull() {
        GenerateAdvertisementResponse response = daggerHandler.apply(generateRequest);
        Assertions.assertNotNull(response);
    }
}
