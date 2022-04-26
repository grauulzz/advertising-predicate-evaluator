package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.TargetingGroup;

/**
 * The type Add targeting group response.
 */
public class AddTargetingGroupResponse {
    private TargetingGroup targetingGroup;

    /**
     * Instantiates a new Add targeting group response.
     *
     * @param targetingGroup the targeting group
     */
    public AddTargetingGroupResponse(TargetingGroup targetingGroup) {
        this.targetingGroup = targetingGroup;
    }

    /**
     * Instantiates a new Add targeting group response.
     *
     * @param builder the builder
     */
    public AddTargetingGroupResponse(Builder builder) {
        this.targetingGroup = builder.targetingGroup;
    }

    /**
     * Instantiates a new Add targeting group response.
     */
    public AddTargetingGroupResponse() {
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
         * Build add targeting group response.
         *
         * @return the add targeting group response
         */
        public AddTargetingGroupResponse build() {
            return new AddTargetingGroupResponse(this);
        }
    }
}
