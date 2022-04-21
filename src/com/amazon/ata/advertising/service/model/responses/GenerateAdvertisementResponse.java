package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.Advertisement;
import com.google.common.base.Objects;

public class GenerateAdvertisementResponse {
    private Advertisement advertisement;

    public GenerateAdvertisementResponse(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public GenerateAdvertisementResponse() {
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    public GenerateAdvertisementResponse(Builder builder) {
        this.advertisement = builder.advertisement;
    }

    public static Builder builder() {return new Builder();}

    public static final class Builder {
        private Advertisement advertisement;

        private Builder() {

        }

        public Builder withAdvertisement(Advertisement advertisementToUse) {
            this.advertisement = advertisementToUse;
            return this;
        }

        public GenerateAdvertisementResponse build() { return new GenerateAdvertisementResponse(this); }
    }

    @Override
    public String toString() {
        return "GenerateAdvertisementResponse{" +
                       "advertisement=" + advertisement +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateAdvertisementResponse response = (GenerateAdvertisementResponse) o;
        return Objects.equal(getAdvertisement(), response.getAdvertisement());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAdvertisement());
    }
}
