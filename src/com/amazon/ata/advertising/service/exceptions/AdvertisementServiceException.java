package com.amazon.ata.advertising.service.exceptions;

public class AdvertisementServiceException extends RuntimeException {
    /**
     * Instantiates a new Advertisement service exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AdvertisementServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
