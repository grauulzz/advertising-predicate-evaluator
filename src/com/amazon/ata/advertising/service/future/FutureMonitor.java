package com.amazon.ata.advertising.service.future;

import com.amazon.ata.ConsoleColors;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<G> completableFuture) {
        while (!completableFuture.isDone()) {
            ConsoleColors.pY.accept(String.format("Waiting for {%s} %n", completableFuture));
            ThreadUtilities.MEDIUM.sleep();
        }
    }
    default void monitor(CompletableFuture<G> completableFuture, Consumer<String> color) {
        while (!completableFuture.isDone()) {
            color.accept(String.format("Waiting for {%s} %n", completableFuture));
            ThreadUtilities.MEDIUM.sleep();
        }
    }

    void onCompleteMonitor(CompletableFuture<G> onComplete);

}
