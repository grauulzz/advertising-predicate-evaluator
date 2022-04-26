package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.Advertisement;

import com.google.common.base.Objects;

/**
 * The type Generate advertisement response.
 */
public class GenerateAdvertisementResponse {
    private Advertisement advertisement;

    /**
     * Instantiates a new Generate advertisement response.
     *
     * @param advertisement the advertisement
     */
    public GenerateAdvertisementResponse(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    /**
     * Instantiates a new Generate advertisement response.
     *
     * @param builder the builder
     */
    public GenerateAdvertisementResponse(Builder builder) {
        this.advertisement = builder.advertisement;
    }

    /**
     * Instantiates a new Generate advertisement response.
     */
    public GenerateAdvertisementResponse() {
    }

    /**
     * Gets advertisement.
     *
     * @return the advertisement
     */
    public Advertisement getAdvertisement() {
        return advertisement;
    }

    /**
     * Sets advertisement.
     *
     * @param advertisement the advertisement
     */
    public void setAdvertisement(Advertisement advertisement) {
        this.advertisement = advertisement;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "GenerateAdvertisementResponse{" +
                       "advertisement=" + advertisement +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GenerateAdvertisementResponse response = (GenerateAdvertisementResponse) o;
        return Objects.equal(getAdvertisement(), response.getAdvertisement());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getAdvertisement());
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private Advertisement advertisement;

        private Builder() {

        }

        /**
         * With advertisement builder.
         *
         * @param advertisementToUse the advertisement to use
         *
         * @return the builder
         */
        public Builder withAdvertisement(Advertisement advertisementToUse) {
            this.advertisement = advertisementToUse;
            return this;
        }

        /**
         * Build generate advertisement response.
         *
         * @return the generate advertisement response
         */
        public GenerateAdvertisementResponse build() {
            return new GenerateAdvertisementResponse(this);
        }
    }
}
