package com.amazon.ata.advertising.service.future;

import com.amazon.ata.advertising.service.activity.GenerateAdActivity;
import com.amazon.ata.advertising.service.activity.dagger.GenerateAdActivityDagger;
import com.amazon.ata.advertising.service.businesslogic.AdvertisementSelectionLogic;
import com.amazon.ata.advertising.service.model.requests.GenerateAdvertisementRequest;
import com.amazon.ata.advertising.service.model.responses.GenerateAdvertisementResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenerateAdActivityFutureTest {

    GenerateAdActivityDagger dagger = new GenerateAdActivityDagger();
    GenerateAdvertisementRequest request1 = GenerateAdvertisementRequest.builder()
                                                   .withCustomerId("0b633dee-9c16-11e8-98d0-529269fb1459")
                                                   .withMarketplaceId("ATVPDKIKX0DER").build();
    GenerateAdvertisementRequest request2 = GenerateAdvertisementRequest.builder()
                                                   .withCustomerId("0b633dee-9c16-11e8-98d0-529269fb1459")
                                                   .withMarketplaceId("A2EUQ1WTGCTBG2").build();
    GenerateAdvertisementRequest request3 = GenerateAdvertisementRequest.builder()
                                                    .withCustomerId("0b633dee-9c16-11e8-98d0-529269fb1459")
                                                    .withMarketplaceId("A1AM78C64UM0Y8").build();



    @Test
    void generateAd() {
        GenerateAdvertisementResponse response = dagger.handleRequest(request2, null);
        System.out.println(response);
    }

    @Test
    void generateAdList() {
        List<GenerateAdvertisementResponse> responses = List.of(dagger.handleRequest(request1, null),
                                                                dagger.handleRequest(request2, null),
                                                                dagger.handleRequest(request3, null));
        System.out.println(responses);

//        assert GenerateAdActivity.generatedAdResponses == responses;
    }

    @Test
    void sequenceFuture() {
    }

    @Test
    void onComplete() {
    }

    @Test
    void sortSelectedAdByCtr() {
    }
}
