package com.amazon.ata.advertising.service.model.responses;

import com.amazon.ata.advertising.service.model.AdvertisingContent;
import com.amazon.ata.advertising.service.model.TargetingGroup;

import java.util.List;

/**
 * The type Update content response.
 */
public class UpdateContentResponse {
    private AdvertisingContent advertisingContent;
    private List<TargetingGroup> targetingGroups;

    /**
     * Instantiates a new Update content response.
     *
     * @param advertisingContent the advertising content
     * @param targetingGroupList the targeting group list
     */
    public UpdateContentResponse(AdvertisingContent advertisingContent, List<TargetingGroup> targetingGroupList) {
        this.advertisingContent = advertisingContent;
        this.targetingGroups = targetingGroupList;
    }

    /**
     * Instantiates a new Update content response.
     *
     * @param builder the builder
     */
    public UpdateContentResponse(Builder builder) {
        this.targetingGroups = builder.targetingGroups;
        this.advertisingContent = builder.advertisingContent;
    }

    /**
     * Instantiates a new Update content response.
     */
    public UpdateContentResponse() {
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
     * Gets targeting groups.
     *
     * @return the targeting groups
     */
    public List<TargetingGroup> getTargetingGroups() {
        return targetingGroups;
    }

    /**
     * Sets targeting group list.
     *
     * @param targetingGroupList the targeting group list
     */
    public void setTargetingGroupList(List<TargetingGroup> targetingGroupList) {
        this.targetingGroups = targetingGroupList;
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
        private List<TargetingGroup> targetingGroups;

        private Builder() {

        }

        /**
         * With targeting groups builder.
         *
         * @param targetingGroupsToUse the targeting groups to use
         *
         * @return the builder
         */
        public Builder withTargetingGroups(List<TargetingGroup> targetingGroupsToUse) {
            this.targetingGroups = targetingGroupsToUse;
            return this;
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
         * Build update content response.
         *
         * @return the update content response
         */
        public UpdateContentResponse build() {
            return new UpdateContentResponse(this);
        }
    }
}
