package com.amazon.ata.advertising.service.model;

import com.google.common.base.Objects;
import java.util.UUID;
import org.apache.commons.lang3.Validate;

/**
 * The unique advertisement generated for a customer, containing an ID unique to this ad's impression and advertisement
 * content.
 */
public class GeneratedAdvertisement {

    private final String id;
    private final AdvertisementContent content;

    /**
     * Constructs GeneratedAdvertisements - generating a value for the id.
     *
     * @param content - the content for the generated ad, cannot be null
     */
    public GeneratedAdvertisement(AdvertisementContent content) {
        Validate.notNull(content, "Advertisement Content may not be null");
        this.id = UUID.randomUUID().toString();
        this.content = content;
    }

    public AdvertisementContent getContent() {
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "GeneratedAdvertisement{" +
                       "id='" + id + '\'' +
                       ", content=" + content +
                       '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedAdvertisement that = (GeneratedAdvertisement) o;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getContent());
    }
}
