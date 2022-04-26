package com.amazon.ata.advertising.service.model.requests;

import com.amazon.ata.advertising.service.model.TargetingPredicate;
import java.util.List;

/**
 * The type Create content request.
 */
public class CreateContentRequest {
    private String content;
    private String marketplaceId;
    private List<TargetingPredicate> targetingPredicates;

    /**
     * Instantiates a new Create content request.
     *
     * @param content             the content
     * @param marketplaceId       the marketplace id
     * @param targetingPredicates the targeting predicates
     */
    public CreateContentRequest(String content, String marketplaceId, List<TargetingPredicate> targetingPredicates) {
        this.content = content;
        this.marketplaceId = marketplaceId;
        this.targetingPredicates = targetingPredicates;
    }

    /**
     * Instantiates a new Create content request.
     */
    public CreateContentRequest() {
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
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
     * Gets targeting predicates.
     *
     * @return the targeting predicates
     */
    public List<TargetingPredicate> getTargetingPredicates() {
        return targetingPredicates;
    }

    /**
     * Sets targeting predicates.
     *
     * @param targetingPredicates the targeting predicates
     */
    public void setTargetingPredicates(List<TargetingPredicate> targetingPredicates) {
        this.targetingPredicates = targetingPredicates;
    }

    /**
     * Instantiates a new Create content request.
     *
     * @param builder the builder
     */
    public CreateContentRequest(Builder builder) {
        this.content = builder.content;
        this.marketplaceId = builder.marketplaceId;
        this.targetingPredicates = builder.targetingPredicates;
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
        private String content;
        private String marketplaceId;
        private List<TargetingPredicate> targetingPredicates;

        private Builder() {

        }

        /**
         * With content builder.
         *
         * @param contentToUse the content to use
         *
         * @return the builder
         */
        public Builder withContent(String contentToUse) {
            this.content = contentToUse;
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
         * With targeting predicates builder.
         *
         * @param targetingPredicatesToUse the targeting predicates to use
         *
         * @return the builder
         */
        public Builder withTargetingPredicates(List<TargetingPredicate> targetingPredicatesToUse) {
            this.targetingPredicates = targetingPredicatesToUse;
            return this;
        }

        /**
         * Build create content request.
         *
         * @return the create content request
         */
        public CreateContentRequest build() {
            return new CreateContentRequest(this);
        }
    }
}
