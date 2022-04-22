package com.amazon.ata.advertising.service.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncUtils {
    private AsyncUtils() {}

    public static <R> R getValue(CompletableFuture<R> future) {
        try { return future.get(); } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
