package com.amazon.ata.advertising.service.model.requests;

import com.amazon.ata.advertising.service.model.TargetingPredicate;

import java.util.List;

/**
 * The type Add targeting group request.
 */
public class AddTargetingGroupRequest {
    private String contentId;
    private List<TargetingPredicate> targetingPredicates;

    /**
     * Instantiates a new Add targeting group request.
     *
     * @param id                  the id
     * @param targetingPredicates the targeting predicates
     */
    public AddTargetingGroupRequest(String id, List<TargetingPredicate> targetingPredicates) {
        this.contentId = id;
        this.targetingPredicates = targetingPredicates;
    }

    /**
     * Instantiates a new Add targeting group request.
     *
     * @param builder the builder
     */
    public AddTargetingGroupRequest(Builder builder) {
        this.contentId = builder.contentId;
        this.targetingPredicates = builder.targetingPredicates;
    }

    /**
     * Instantiates a new Add targeting group request.
     */
    public AddTargetingGroupRequest() {
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
        private List<TargetingPredicate> targetingPredicates;

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
         * Build add targeting group request.
         *
         * @return the add targeting group request
         */
        public AddTargetingGroupRequest build() {
            return new AddTargetingGroupRequest(this);
        }
    }
}
