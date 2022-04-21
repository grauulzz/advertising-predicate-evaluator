package com.amazon.ata.advertising.service.future;


import com.amazon.ata.App;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<G> completableFuture) {
        while (!completableFuture.isDone()) {
            LOG.info(System.out.printf("ThreadCount: %s%n", Thread.activeCount()));
            ThreadUtils.SHORT.sleep();
            LOG.info("Waiting for {} to complete... {} %n", completableFuture, App.getCurrentTime());
        }
    }
    void onComplete(CompletableFuture<G> onComplete);
}
