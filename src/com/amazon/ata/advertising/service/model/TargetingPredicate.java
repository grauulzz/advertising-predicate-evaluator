package com.amazon.ata.advertising.service.model;

import java.util.Map;
import java.util.Objects;

/**
 * The type Targeting predicate.
 */
public class TargetingPredicate {
    private TargetingPredicateType targetingPredicateType;
    private boolean negate;
    private Map<String, String> attributes;

    /**
     * Instantiates a new Targeting predicate.
     *
     * @param targetingPredicateType       the targeting predicate type
     * @param negate                       the negate
     * @param targetingPredicateAttributes the targeting predicate attributes
     */
    public TargetingPredicate(TargetingPredicateType targetingPredicateType, boolean negate,
                              Map<String, String> targetingPredicateAttributes) {
        this.targetingPredicateType = targetingPredicateType;
        this.negate = negate;
        this.attributes = targetingPredicateAttributes;
    }

    /**
     * Instantiates a new Targeting predicate.
     *
     * @param builder the builder
     */
    public TargetingPredicate(Builder builder) {
        targetingPredicateType = builder.targetingPredicateType;
        negate = builder.negate;
        attributes = builder.attributes;
    }


    /**
     * Instantiates a new Targeting predicate.
     */
    public TargetingPredicate() {
    }

    /**
     * Gets targeting predicate type.
     *
     * @return the targeting predicate type
     */
    public TargetingPredicateType getTargetingPredicateType() {
        return targetingPredicateType;
    }

    /**
     * Sets targeting predicate type.
     *
     * @param targetingPredicateType the targeting predicate type
     */
    public void setTargetingPredicateType(TargetingPredicateType targetingPredicateType) {
        this.targetingPredicateType = targetingPredicateType;
    }

    /**
     * Is negate boolean.
     *
     * @return the boolean
     */
    public boolean isNegate() {
        return negate;
    }

    /**
     * Sets negate.
     *
     * @param negate the negate
     */
    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TargetingPredicate that = (TargetingPredicate) o;
        return negate == that.negate &&
                       targetingPredicateType == that.targetingPredicateType &&
                       Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetingPredicateType, negate, attributes);
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
        private TargetingPredicateType targetingPredicateType;
        private boolean negate;
        private Map<String, String> attributes;

        private Builder() {

        }

        /**
         * With targeting predicate type builder.
         *
         * @param targetingPredicateTypeToUse the targeting predicate type to use
         *
         * @return the builder
         */
        public Builder withTargetingPredicateType(TargetingPredicateType targetingPredicateTypeToUse) {
            this.targetingPredicateType = targetingPredicateTypeToUse;
            return this;
        }

        /**
         * With negate builder.
         *
         * @param negateToUse the negate to use
         *
         * @return the builder
         */
        public Builder withNegate(boolean negateToUse) {
            this.negate = negateToUse;
            return this;
        }

        /**
         * With attributes builder.
         *
         * @param attributesToUse the attributes to use
         *
         * @return the builder
         */
        public Builder withAttributes(Map<String, String> attributesToUse) {
            this.attributes = attributesToUse;
            return this;
        }

        /**
         * Build targeting predicate.
         *
         * @return the targeting predicate
         */
        public TargetingPredicate build() {
            return new TargetingPredicate(this);
        }
    }
}
