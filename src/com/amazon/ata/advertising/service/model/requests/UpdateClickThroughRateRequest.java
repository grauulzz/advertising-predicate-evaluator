package com.amazon.ata.advertising.service.model.requests;

/**
 * The type Update click through rate request.
 */
public class UpdateClickThroughRateRequest {
    private String targetingGroupId;
    private double clickThroughRate;

    /**
     * Instantiates a new Update click through rate request.
     *
     * @param targetingGroupId the targeting group id
     * @param clickThroughRate the click through rate
     */
    public UpdateClickThroughRateRequest(String targetingGroupId, double clickThroughRate) {
        this.targetingGroupId = targetingGroupId;
        this.clickThroughRate = clickThroughRate;
    }

    /**
     * Instantiates a new Update click through rate request.
     */
    public UpdateClickThroughRateRequest() {
    }

    /**
     * Gets targeting group id.
     *
     * @return the targeting group id
     */
    public String getTargetingGroupId() {
        return targetingGroupId;
    }

    /**
     * Sets targeting group id.
     *
     * @param targetingGroupId the targeting group id
     */
    public void setTargetingGroupId(String targetingGroupId) {
        this.targetingGroupId = targetingGroupId;
    }

    /**
     * Gets click through rate.
     *
     * @return the click through rate
     */
    public double getClickThroughRate() {
        return clickThroughRate;
    }

    /**
     * Sets click through rate.
     *
     * @param clickThroughRate the click through rate
     */
    public void setClickThroughRate(double clickThroughRate) {
        this.clickThroughRate = clickThroughRate;
    }

    /**
     * Instantiates a new Update click through rate request.
     *
     * @param builder the builder
     */
    public UpdateClickThroughRateRequest(Builder builder) {
        this.targetingGroupId = builder.targetingGroupId;
        this.clickThroughRate = builder.clickThroughRate;
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
        private String targetingGroupId;
        private double clickThroughRate;

        private Builder() {

        }

        /**
         * With targeting group id builder.
         *
         * @param targetingGroupIdToUse the targeting group id to use
         *
         * @return the builder
         */
        public Builder withTargetingGroupId(String targetingGroupIdToUse) {
            this.targetingGroupId = targetingGroupIdToUse;
            return this;
        }

        /**
         * With click through rate builder.
         *
         * @param clickThroughRateToUse the click through rate to use
         *
         * @return the builder
         */
        public Builder withClickThroughRate(double clickThroughRateToUse) {
            this.clickThroughRate = clickThroughRateToUse;
            return this;
        }

        /**
         * Build update click through rate request.
         *
         * @return the update click through rate request
         */
        public UpdateClickThroughRateRequest build() {
            return new UpdateClickThroughRateRequest(this);
        }
    }
}
