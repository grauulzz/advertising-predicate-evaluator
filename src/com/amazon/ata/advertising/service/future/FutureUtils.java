package com.amazon.ata.advertising.service.future;

import com.amazon.ata.advertising.service.future.FutureMonitor.ConsoleLogger;
import com.amazon.ata.advertising.service.model.AdvertisementContent;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * The type Future utils.
 */
public class FutureUtils {
    public static final ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private FutureUtils() {
    }

    /**
     * Get t.
     *
     * @param <T>    the type parameter
     * @param future the future
     *
     * @return the t
     */
    public static <T> T get(CompletableFuture<T> future) {
        monitor(future, ConsoleLogger.CYAN.getColor());
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            ConsoleLogger.RED.log("Exception while getting future result, shutting down executor");
            EXECUTOR_SERVICE.shutdown();
            throw new RuntimeException(e);
        }
    }

    /**
     * Callable async processing list.
     *
     * @param function the function
     * @param groups   the groups
     *
     * @return the list
     */
    public static List<AdvertisementContent> callableAsyncProcessing(
            Function<List<TargetingGroup>, Optional<List<AdvertisementContent>>> function, List<TargetingGroup> groups
    ) {
        CompletableFuture<Optional<List<AdvertisementContent>>> future =
                CompletableFuture.supplyAsync(() -> function.apply(groups), EXECUTOR_SERVICE);
        monitor(future, ConsoleLogger.MAGENTA.getColor());
        return get(future.thenApply(Optional::get));
    }

    /**
     * Callable async processing list.
     *
     * @param future    the CompletableFuture
     * @param color     the console output color
     */
    private static <G> void monitor(CompletableFuture<G> future, Consumer<String> color) {
        if (!future.isDone()) {
            color.accept(String.format("[%s][%s]", Thread.currentThread().getName(),
                    Thread.currentThread().getState()));
            ConsoleLogger.YELLOW.getColor().accept(String.format("Waiting for {%s} %n", future));
        }

        future.whenComplete((G g, Throwable t) -> {
            if (t != null) {
                ConsoleLogger.RED.log(String.format("Future -> {%s} exception {%s}", future, t));
            }

            if (future.isCompletedExceptionally() || future.join() == null) {
                ConsoleLogger.RED.log(String.format("Future completed with errors -> {%s}%n", future));
            }
            ConsoleLogger.GREEN.getColor().accept(String.format("Completed future -> {%s}%n", g));
        });
    }

}
