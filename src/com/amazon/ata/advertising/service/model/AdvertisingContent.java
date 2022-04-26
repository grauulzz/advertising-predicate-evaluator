package com.amazon.ata.advertising.service.model;

import com.google.common.base.Objects;

public class AdvertisingContent {
    private String id;
    private String marketplaceId;
    private String content;

    public AdvertisingContent(String id, String marketplaceId, String content) {
        this.id = id;
        this.marketplaceId = marketplaceId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AdvertisingContent(Builder builder) {
        this.id = builder.id;
        this.marketplaceId = builder.marketplaceId;
        this.content = builder.content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String marketplaceId;
        private String content;

        private Builder() {

        }

        public Builder withId(String idToUse) {
            this.id = idToUse;
            return this;
        }

        public Builder withMarketplaceId(String marketplaceIdToUse) {
            this.marketplaceId = marketplaceIdToUse;
            return this;
        }

        public Builder withContent(String contentToUse) {
            this.content = contentToUse;
            return this;
        }

        public AdvertisingContent build() {
            return new AdvertisingContent(this);
        }
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertisingContent content1 = (AdvertisingContent) o;
        return Objects.equal(getId(), content1.getId()) && Objects.equal(getMarketplaceId(), content1.getMarketplaceId()) && Objects.equal(getContent(), content1.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getMarketplaceId(), getContent());
    }
}
