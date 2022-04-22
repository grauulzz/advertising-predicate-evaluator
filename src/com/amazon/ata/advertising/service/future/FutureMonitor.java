package com.amazon.ata.advertising.service.future;


import com.amazon.ata.App;
import com.amazon.ata.ConsoleColors;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<T> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<T> completableFuture) {
        while (!completableFuture.isDone()) {
            ThreadUtils.SHORT.sleep();
            ConsoleColors.pY.accept(String.format("Waiting for {%s} %n", completableFuture));
        }
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                ConsoleColors.pR.accept(String.format("{%s} {%s} %n", throwable, completableFuture));
            }
            boolean b = completableFuture.isDone() && !completableFuture.isCompletedExceptionally() && completableFuture.join() != null;
            ConsoleColors.pG.accept(String.format("Completed Async {%s} %nisComplete?{%s}%n", result, b));
        });
    }
//    void onComplete(CompletableFuture<G> onComplete);
}
