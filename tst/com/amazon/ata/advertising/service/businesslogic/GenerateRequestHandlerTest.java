package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dependency.DaggerLambdaComponent;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GenerateRequestHandlerTest {
    private final GenerateAdvertisementRequest generateRequest =
            new GenerateAdvertisementRequest("A123B456", "ATVPDKIKX0DER");

    //               {tgId}                      {ctr}                   {contentId}
    // 8b4e796d-d251-4c07-9e45-875220f4810d	 |  0.03437  |  0b632e26-9c16-11e8-98d0-529269fb1459
    // 249959cc-eab8-4d76-8434-4803fc877246	 |  0.0073   |  0b632e26-9c16-11e8-98d0-529269fb1459
    // b389bc42-7a18-4196-bc4f-7a0515728311	 |  0.03929  |  0b632e26-9c16-11e8-98d0-529269fb1459
    // 1d3686dd-9211-46e6-b315-abcbeeb3c97d	 |  0.18007  |  0b632e26-9c16-11e8-98d0-529269fb1459

    private final Function<GenerateAdvertisementRequest, GenerateAdvertisementResponse> daggerHandler =
            request -> DaggerLambdaComponent.create().provideGenerateAdActivity().generateAd(request);
    @Test
    void handleRequestNotNull() {
        GenerateAdvertisementResponse response = daggerHandler.apply(generateRequest);
        // content of the highest ctr for marketPlaceId 'ATVPDKIKX0DER'
        Assertions.assertEquals("Become a Software Developer. Stop Dreaming, Start Training. Join ATA Today!",
                response.getAdvertisement().getContent());
    }
}
