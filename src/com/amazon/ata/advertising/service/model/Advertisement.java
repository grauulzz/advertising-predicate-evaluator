package com.amazon.ata.advertising.service.model;

import com.google.common.base.Objects;

/**
 * The type Advertisement.
 */
public class Advertisement {
    private String id;
    private String content;

    /**
     * Instantiates a new Advertisement.
     *
     * @param id      the id
     * @param content the content
     */
    public Advertisement(String id, String content) {
        this.id = id;
        this.content = content;
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

    public Advertisement(Builder builder) {
        this.id = builder.id;
        this.content = builder.content;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private String content;

        private Builder() {

        }

        public Builder withId(String idToUse) {
            this.id = idToUse;
            return this;
        }

        public Builder withContent(String contentToUse) {
            this.content = contentToUse;
            return this;
        }

        public Advertisement build() {
            return new Advertisement(this);
        }
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                       "id='" + id + '\'' +
                       ", content='" + content + '\'' +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advertisement that = (Advertisement) o;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getContent());
    }
}
