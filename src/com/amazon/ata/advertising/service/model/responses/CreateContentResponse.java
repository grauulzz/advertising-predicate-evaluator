package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.AdvertisingContent;
import com.amazon.ata.advertising.service.model.TargetingGroup;

/**
 * The type Create content response.
 */
public class CreateContentResponse {
    private AdvertisingContent advertisingContent;
    private TargetingGroup targetingGroup;

    /**
     * Instantiates a new Create content response.
     *
     * @param advertisingContent the advertising content
     * @param targetingGroup     the targeting group
     */
    public CreateContentResponse(AdvertisingContent advertisingContent, TargetingGroup targetingGroup) {
        this.advertisingContent = advertisingContent;
        this.targetingGroup = targetingGroup;
    }

    /**
     * Instantiates a new Create content response.
     */
    public CreateContentResponse() {
    }

    /**
     * Gets advertising content.
     *
     * @return the advertising content
     */
    public AdvertisingContent getAdvertisingContent() {
        return advertisingContent;
    }

    /**
     * Sets advertising content.
     *
     * @param advertisingContent the advertising content
     */
    public void setAdvertisingContent(AdvertisingContent advertisingContent) {
        this.advertisingContent = advertisingContent;
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
     * Instantiates a new Create content response.
     *
     * @param builder the builder
     */
    public CreateContentResponse(Builder builder) {
        this.targetingGroup = builder.targetingGroup;
        this.advertisingContent = builder.advertisingContent;
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
        private AdvertisingContent advertisingContent;
        private TargetingGroup targetingGroup;

        private Builder() {

        }

        /**
         * With advertising content builder.
         *
         * @param advertisingContentToUse the advertising content to use
         *
         * @return the builder
         */
        public Builder withAdvertisingContent(AdvertisingContent advertisingContentToUse) {
            this.advertisingContent = advertisingContentToUse;
            return this;
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
         * Build create content response.
         *
         * @return the create content response
         */
        public CreateContentResponse build() {
            return new CreateContentResponse(this);
        }
    }
}
