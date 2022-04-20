package com.amazon.ata.advertising.service.future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

public interface FutureUtils {
    static final Logger LOG = LogManager.getLogger(FutureUtils.class);


    static <G> FutureMonitor<G> create(CompletableFuture<G> future) {
        String name = future.getClass().getSimpleName();
        return onComplete -> future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                FutureMonitor.LOG.error("Future {} failed with exception {}", name, throwable.getMessage());
            } else {
                FutureMonitor.LOG.info("Future {} completed with result {}", name, result);
                onComplete.accept(result);
            }
        });
    }

}
