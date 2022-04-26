package com.amazon.ata.advertising.service.future;

import com.amazon.ata.advertising.service.future.FutureMonitor.ConsoleLogger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class FutureUtils {
    private FutureUtils() {}
    public static final ExecutorService getExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final CompletableFuture<Void> canceller = new CompletableFuture<>();
    public static void shutdown() {
        ConsoleLogger.RED.log("Shutting down executor");
        getExecutor.shutdown();
    }

    public static <T> void cancel(CompletableFuture<T> future) {
        future.cancel(true);
    }

    public static <T> void cancelAll(List<CompletableFuture<T>> futures) {
        futures.forEach(FutureUtils::cancel);
    }

    public static <T> List<T> getAll(List<CompletableFuture<T>> futures) {
        return futures.stream()
                .map(FutureUtils::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static <T> CompletableFuture<T> combine(CompletableFuture<T> future1, CompletableFuture<T> future2) {
        return future1.thenCombine(future2, (t1, t2) -> t1);
    }

    public static <T> CompletableFuture<T> combine(CompletableFuture<T> future1, CompletableFuture<T> future2, CompletableFuture<T> future3) {
        return combine(combine(future1, future2), future3);
    }

    public static <T> CompletableFuture<T> cancelFutureThenCombineAnother(CompletableFuture<T> cancelable,
                                                                          CompletableFuture<T> combinable1,
                                                                          CompletableFuture<T> combinable2) {
        cancelable.cancel(true);
        return combine(combinable1, combinable2).exceptionally(e -> null);
    }


    public static <T> T get(CompletableFuture<T> future) {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            ConsoleLogger.RED.log("Exception while getting future result, shutting down executor");
            shutdown();
            throw new RuntimeException(e);
        }
    }

    public static  <R> CompletableFuture<List<R>> sequenceFuture(List<CompletableFuture<R>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                       .thenApply(v -> futures.stream().map(FutureUtils::get).filter(Objects::nonNull)
                                               .collect(Collectors.toList()));
    }
}
