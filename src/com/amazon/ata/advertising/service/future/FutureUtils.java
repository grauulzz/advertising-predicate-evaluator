package com.amazon.ata.advertising.service.future;

import com.amazon.ata.advertising.service.future.FutureMonitor.ConsoleLogger;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FutureUtils {
    private FutureUtils() {}
    public static final ExecutorService getExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static <T> T get(CompletableFuture<T> future) {
        monitor(future,ConsoleLogger.CYAN.getColor());
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            ConsoleLogger.RED.log("Exception while getting future result, shutting down executor");
            shutdown();
            throw new RuntimeException(e);
        }
    }
    public static List<AdvertisementContent> appyAsyncProcessing(
            Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> function, List<TargetingGroup> groups
    ) {
        CompletableFuture<Optional<List<AdvertisementContent>>> c =
                CompletableFuture.supplyAsync(() -> function.apply(groups), getExecutor);
        monitor(c, ConsoleLogger.MAGENTA.getColor());
        return get(c.thenApply(Optional::get));
    }
    public static  <R> CompletableFuture<List<R>> sequenceFuture(List<CompletableFuture<R>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                       .thenApply(v -> futures.stream().map(FutureUtils::get).filter(Objects::nonNull)
                                               .collect(Collectors.toList()));
    }
    public static <G> void monitor(CompletableFuture<G> completableFuture, Consumer<String> color) {
        // allThreadsLogger();
        if (!completableFuture.isDone()) {
            color.accept(String.format("[%s][%s]",Thread.currentThread().getName(), Thread.currentThread().getState()));
            ConsoleLogger.YELLOW.getColor().accept(String.format("Waiting for {%s} %n", completableFuture));
        }

        completableFuture.whenComplete((G g, Throwable t) -> {
            if (t != null) {
                ConsoleLogger.RED.log(String.format("Future -> {%s} exception {%s}", completableFuture, t));
            }

            if (completableFuture.isCompletedExceptionally() || completableFuture.join() == null) {
                ConsoleLogger.RED.log(String.format("Future completed with errors -> {%s}%n", completableFuture));
            }
            ConsoleLogger.GREEN.getColor().accept(String.format("Completed future -> {%s}%n", g));
        });
    }
    public static void shutdown() {
        ConsoleLogger.RED.log("Shutting down executor");
        getExecutor.shutdown();
    }

}
