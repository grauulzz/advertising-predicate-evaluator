package com.amazon.ata.advertising.service.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<G> completableFuture) {
        if (!completableFuture.isDone()) {
            System.out.printf("Waiting for {%s} %n", completableFuture);
        }
    }
    default void monitor(CompletableFuture<G> completableFuture, Consumer<String> color) {
        if (!completableFuture.isDone()) {
            color.accept(String.format("Waiting for {%s} %n", completableFuture));
        }
    }

}
