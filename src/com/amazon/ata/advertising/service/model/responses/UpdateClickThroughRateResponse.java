package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.TargetingGroup;

/**
 * The type Update click through rate response.
 */
public class UpdateClickThroughRateResponse {
    private TargetingGroup targetingGroup;

    /**
     * Instantiates a new Update click through rate response.
     *
     * @param targetingGroup the targeting group
     */
    public UpdateClickThroughRateResponse(TargetingGroup targetingGroup) {
        this.targetingGroup = targetingGroup;
    }

    /**
     * Instantiates a new Update click through rate response.
     *
     * @param builder the builder
     */
    public UpdateClickThroughRateResponse(Builder builder) {
        this.targetingGroup = builder.targetingGroup;
    }

    /**
     * Instantiates a new Update click through rate response.
     */
    public UpdateClickThroughRateResponse() {
    }

    /**
     * Gets targeting group.
     *
     * @return the targeting group
     */
    public TargetingGroup getTargetingGroup() {
        return targetingGroup;
    }

    /**
     * Sets targeting group.
     *
     * @param targetingGroup the targeting group
     */
    public void setTargetingGroup(TargetingGroup targetingGroup) {
        this.targetingGroup = targetingGroup;
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
        private TargetingGroup targetingGroup;

        private Builder() {

        }

        /**
         * With targeting group builder.
         *
         * @param targetingGroupToUse the targeting group to use
         *
         * @return the builder
         */
        public Builder withTargetingGroup(TargetingGroup targetingGroupToUse) {
            this.targetingGroup = targetingGroupToUse;
            return this;
        }

        /**
         * Build update click through rate response.
         *
         * @return the update click through rate response
         */
        public UpdateClickThroughRateResponse build() {
            return new UpdateClickThroughRateResponse(this);
        }
    }
}
