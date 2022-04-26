package com.amazon.ata.advertising.service.model.responses;

/**
 * The type Delete content response.
 */
public class DeleteContentResponse {
    /**
     * Instantiates a new Delete content response.
     */
    public DeleteContentResponse() {
    }

    /**
     * Instantiates a new Delete content response.
     *
     * @param builder the builder
     */
    public DeleteContentResponse(Builder builder) {

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
        private Builder() {

        }

        /**
         * Build delete content response.
         *
         * @return the delete content response
         */
        public DeleteContentResponse build() {
            return new DeleteContentResponse(this);
        }
    }
}
