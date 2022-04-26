package com.amazon.ata.advertising.service.model.requests;

/**
 * The type Generate advertisement request.
 */
public class GenerateAdvertisementRequest {
    private String customerId;
    private String marketplaceId;

    /**
     * Instantiates a new Generate advertisement request.
     *
     * @param customerId    the customer id
     * @param marketplaceId the marketplace id
     */
    public GenerateAdvertisementRequest(String customerId, String marketplaceId) {
        this.customerId = customerId;
        this.marketplaceId = marketplaceId;
    }

    /**
     * Instantiates a new Generate advertisement request.
     */
    public GenerateAdvertisementRequest() {
    }

    /**
     * Gets customer id.
     *
     * @return the customer id
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets customer id.
     *
     * @param customerId the customer id
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets marketplace id.
     *
     * @return the marketplace id
     */
    public String getMarketplaceId() {
        return marketplaceId;
    }

    /**
     * Sets marketplace id.
     *
     * @param marketPlaceId the market place id
     */
    public void setMarketplaceId(String marketPlaceId) {
        this.marketplaceId = marketPlaceId;
    }

    /**
     * Instantiates a new Generate advertisement request.
     *
     * @param builder the builder
     */
    public GenerateAdvertisementRequest(Builder builder) {
        this.customerId = builder.customerId;
        this.marketplaceId = builder.marketplaceId;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String customerId;
        private String marketplaceId;

        private Builder() {

        }

        /**
         * With customer id builder.
         *
         * @param customerIdToUse the customer id to use
         *
         * @return the builder
         */
        public Builder withCustomerId(String customerIdToUse) {
            this.customerId = customerIdToUse;
            return this;
        }

        /**
         * With marketplace id builder.
         *
         * @param marketplaceIdToUse the marketplace id to use
         *
         * @return the builder
         */
        public Builder withMarketplaceId(String marketplaceIdToUse) {
            this.marketplaceId = marketplaceIdToUse;
            return this;
        }

        /**
         * Build generate advertisement request.
         *
         * @return the generate advertisement request
         */
        public GenerateAdvertisementRequest build() {
            return new GenerateAdvertisementRequest(this);
        }
    }
}
