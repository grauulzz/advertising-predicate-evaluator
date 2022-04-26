package com.amazon.ata.advertising.service.model.requests;

import com.amazon.ata.advertising.service.model.AdvertisingContent;

/**
 * The type Update content request.
 */
public class UpdateContentRequest {
    private String contentId;
    private AdvertisingContent advertisingContent;

    /**
     * Instantiates a new Update content request.
     *
     * @param contentid          the contentid
     * @param advertisingContent the advertising content
     */
    public UpdateContentRequest(String contentid, AdvertisingContent advertisingContent) {
        this.contentId = contentid;
        this.advertisingContent = advertisingContent;
    }

    /**
     * Instantiates a new Update content request.
     *
     * @param builder the builder
     */
    public UpdateContentRequest(Builder builder) {
        this.contentId = builder.contentId;
        this.advertisingContent = builder.advertisingContent;
    }

    /**
     * Instantiates a new Update content request.
     */
    public UpdateContentRequest() {
    }

    /**
     * Gets content id.
     *
     * @return the content id
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * Sets content id.
     *
     * @param contentId the content id
     */
    public void setContentId(String contentId) {
        this.contentId = contentId;
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
        private String contentId;
        private AdvertisingContent advertisingContent;

        private Builder() {

        }

        /**
         * With content id builder.
         *
         * @param contentIdToUse the content id to use
         *
         * @return the builder
         */
        public Builder withContentId(String contentIdToUse) {
            this.contentId = contentIdToUse;
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
         * Build update content request.
         *
         * @return the update content request
         */
        public UpdateContentRequest build() {
            return new UpdateContentRequest(this);
        }
    }
}
