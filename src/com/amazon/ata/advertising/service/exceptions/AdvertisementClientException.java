package com.amazon.ata.advertising.service.exceptions;

/**
 * The type Advertisement client exception.
 */
public class AdvertisementClientException extends RuntimeException {
    /**
     * Instantiates a new Advertisement client exception.
     *
     * @param message the message
     */
    public AdvertisementClientException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Advertisement client exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AdvertisementClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Advertisement client exception.
     *
     * @param cause the cause
     */
    public AdvertisementClientException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Advertisement client exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public AdvertisementClientException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
