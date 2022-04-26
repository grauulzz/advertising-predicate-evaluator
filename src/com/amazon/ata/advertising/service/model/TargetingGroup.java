package com.amazon.ata.advertising.service.model;

import java.util.List;

/**
 * The type Targeting group.
 */
public class TargetingGroup {
    private String targetingGroupId;
    private String contentId;
    private double clickThroughRate;
    private List<TargetingPredicate> targetingPredicates;

    /**
     * Instantiates a new Targeting group.
     *
     * @param targetingGroupId    the targeting group id
     * @param contentId           the content id
     * @param clickThroughRate    the click through rate
     * @param targetingPredicates the targeting predicates
     */
    public TargetingGroup(String targetingGroupId, String contentId, double clickThroughRate,
                          List<TargetingPredicate> targetingPredicates) {
        this.targetingGroupId = targetingGroupId;
        this.contentId = contentId;
        this.clickThroughRate = clickThroughRate;
        this.targetingPredicates = targetingPredicates;
    }
    /**
     * Instantiates a new Targeting group.
     *
     * @param builder the builder
     */
    public TargetingGroup(Builder builder) {
        this.contentId = builder.contentId;
        this.targetingPredicates = builder.targetingPredicates;
        this.targetingGroupId = builder.targetingGroupId;
        this.clickThroughRate = builder.clickThroughRate;
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
        private String targetingGroupId;
        private String contentId;
        private double clickThroughRate;
        private List<TargetingPredicate> targetingPredicates;

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
         * Build targeting group.
         *
         * @return the targeting group
         */
        public TargetingGroup build() {
            return new TargetingGroup(this);
        }
    }
}
