package com.amazon.ata.advertising.service.model.requests;

/**
 * The type Delete content request.
 */
public class DeleteContentRequest {
    private String contentId;

    /**
     * Instantiates a new Delete content request.
     *
     * @param contentId the content id
     */
    public DeleteContentRequest(String contentId) {
        this.contentId = contentId;
    }

    /**
     * Instantiates a new Delete content request.
     *
     * @param builder the builder
     */
    public DeleteContentRequest(Builder builder) {
        this.contentId = builder.contentId;
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
         * Build delete content request.
         *
         * @return the delete content request
         */
        public DeleteContentRequest build() {
            return new DeleteContentRequest(this);
        }
    }
}
