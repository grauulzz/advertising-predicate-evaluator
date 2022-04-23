package com.amazon.ata.advertising.service.future;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AsyncUtils {
    public static final ExecutorService getExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private AsyncUtils() {}

    public static <T> CompletableFuture<T> async(ExecutorService executor, Runnable runnable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> async(Runnable runnable) {
        return async(Executors.newSingleThreadExecutor(), runnable);
    }

    public static <T> CompletableFuture<T> async(ExecutorService executor, Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                future.complete(supplier.get());
            } catch (RuntimeException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return async(Executors.newSingleThreadExecutor(), supplier);
    }

    public static <T> T get(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static  <R> CompletableFuture<List<R>> sequenceFuture(List<CompletableFuture<R>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                       .thenApply(v -> futures.stream().map(AsyncUtils::get).filter(Objects::nonNull)
                                               .collect(Collectors.toList())
                       );
    }
}
