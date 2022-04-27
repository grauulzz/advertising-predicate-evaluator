package com.amazon.ata.advertising.service.model;

import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratedAdvertisementTest {

    private static final String CONTENT_ID = UUID.randomUUID().toString();
    private static final String CONTENT = "Hello World";
    private static final GeneratedAdvertisement ADVERTISEMENT_CONTENT =
            new GeneratedAdvertisement(AdvertisementContent.builder().withContentId(CONTENT_ID)
                                               .withMarketplaceId("1").withRenderableContent("1")
                                               .build());
    @Test
    public void equals_referentiallyEquals_returnsTrue() {
        // GIVEN
        Object other = ADVERTISEMENT_CONTENT;

        // WHEN
        boolean isEqual = ADVERTISEMENT_CONTENT.equals(other);

        // THEN
        assertTrue(isEqual);
    }

    @Test
    public void equals_null_returnsFalse() {
        // GIVEN
        Object other = null;

        // WHEN
        boolean isEqual = ADVERTISEMENT_CONTENT.equals(other);

        // THEN
        assertFalse(isEqual);
    }

    @Test
    public void equals_otherClass_returnsFalse() {
        // GIVEN
        Object other = "String";

        // WHEN
        boolean isEqual = ADVERTISEMENT_CONTENT.equals(other);

        // THEN
        assertFalse(isEqual);
    }

    @Test
    public void equals_unequalContentIds_returnsFalse() {
        // GIVEN
        Object other = new GeneratedAdvertisement(AdvertisementContent.builder()
                                                         .withContentId("adfs")
                                                         .build());

        // WHEN
        boolean isEqual = ADVERTISEMENT_CONTENT.equals(other);

        // THEN
        assertFalse(isEqual);
    }

    @Test
    public void hashCode_equalObjects_notEqualHashCodes() {
        // GIVEN
        Object other =
                new GeneratedAdvertisement(AdvertisementContent.builder().withContentId(CONTENT_ID)
                                                   .withMarketplaceId("1").withRenderableContent("1")
                                                   .build());


        // WHEN
        int hashCode = other.hashCode();

        // THEN
        assertNotEquals(ADVERTISEMENT_CONTENT.hashCode(), hashCode);
    }


    @Test
    void testEquals() {
        GeneratedAdvertisement generatedAdvertisement1 =
                new GeneratedAdvertisement(AdvertisementContent.builder().build());
        GeneratedAdvertisement generatedAdvertisement2 =
                new GeneratedAdvertisement(AdvertisementContent.builder().build());
        assertNotEquals(generatedAdvertisement1, generatedAdvertisement2);
    }

    @Test
    void testHashCode() {
        GeneratedAdvertisement generatedAdvertisement1 =
                new GeneratedAdvertisement(AdvertisementContent.builder().build());
        GeneratedAdvertisement generatedAdvertisement2 =
                new GeneratedAdvertisement(AdvertisementContent.builder().build());
        assertNotEquals(generatedAdvertisement1.hashCode(), generatedAdvertisement2.hashCode());
    }
}
