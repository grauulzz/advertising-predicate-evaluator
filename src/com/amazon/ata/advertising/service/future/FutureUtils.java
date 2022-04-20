//package com.amazon.ata.advertising.service.future;
//
//import java.util.concurrent.CompletableFuture;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public interface FutureUtils {
//    static final Logger LOG = LogManager.getLogger(FutureUtils.class);
//
//
//    static <G> FutureMonitor<G> create(CompletableFuture<G> future) {
//        String name = future.getClass().getSimpleName();
//        return onComplete -> future.whenComplete((result, throwable) -> {
//            if (throwable != null) {
//                FutureMonitor.LOG.error("Future {} failed with exception {}", name, throwable.getMessage());
//            } else {
//                FutureMonitor.LOG.info("Future {} completed with result {}", name, result);
//                onComplete.accept(result);
//            }
//        });
//    }
//
//}
