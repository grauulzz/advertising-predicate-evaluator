package com.amazon.ata.advertising.service.model;

import com.google.common.base.Objects;

/**
 * The type Advertising content.
 */
public class AdvertisingContent {
    private String id;
    private String marketplaceId;
    private String content;


    /**
     * Instantiates a new Advertising content.
     *
     * @param id            the id
     * @param marketplaceId the marketplace id
     * @param content       the content
     */
    public AdvertisingContent(String id, String marketplaceId, String content) {
        this.id = id;
        this.marketplaceId = marketplaceId;
        this.content = content;
    }

    /**
     * Instantiates a new Advertising content.
     *
     * @param builder the builder
     */
    public AdvertisingContent(Builder builder) {
        this.id = builder.id;
        this.marketplaceId = builder.marketplaceId;
        this.content = builder.content;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
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
     * @param marketplaceId the marketplace id
     */
    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
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
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "AdvertisingContent{" +
                       "id='" + id + '\'' +
                       ", marketplaceId='" + marketplaceId + '\'' +
                       ", content='" + content + '\'' +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdvertisingContent content1 = (AdvertisingContent) o;
        return Objects.equal(getId(), content1.getId()) && Objects.equal(getMarketplaceId(),
                content1.getMarketplaceId()) && Objects.equal(getContent(), content1.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getMarketplaceId(), getContent());
    }

    /**
     * The type Builder.
     */
    public static final class Builder {
        private String id;
        private String marketplaceId;
        private String content;

        private Builder() {

        }

        /**
         * With id builder.
         *
         * @param idToUse the id to use
         *
         * @return the builder
         */
        public Builder withId(String idToUse) {
            this.id = idToUse;
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
         * Build advertising content.
         *
         * @return the advertising content
         */
        public AdvertisingContent build() {
            return new AdvertisingContent(this);
        }
    }


}
