package com.amazon.ata.advertising.service.future;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);
    static final Thread.State[] STATES = {
            Thread.State.BLOCKED,
            Thread.State.NEW,
            Thread.State.RUNNABLE,
            Thread.State.TERMINATED,
            Thread.State.TIMED_WAITING,
            Thread.State.WAITING
    };

    final GsonBuilder builder = new GsonBuilder();
    final Gson gsonPty = builder.setPrettyPrinting().create();

    Consumer<Object> printJsonPty = o -> {
        String teal = "\u001B[36m"; String json = gsonPty.toJson(o); String[] lines = json.split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append(teal);
        for (String line : lines) {
            sb.append(line).append("\n").append(teal);
        }
        System.out.println(sb + "\u001B[0m");
    };

    default void monitor(CompletableFuture<G> completableFuture, ForkJoinPool forkJoinPool) {
        while (!completableFuture.isDone()) {
            try {
                LOG.info(System.out.printf("Monitoring future %s", completableFuture));
                ThreadSleep.SHORT.sleep();
            } finally {
                forkJoinPool.execute(() -> {
                    try {
                        LOG.info(System.out.printf("Monitoring future %s", completableFuture));
                    } finally {
                        try {
                            completableFuture.complete(completableFuture.get());
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        }
    }

    default void monitor() {
        ForkJoinPool.commonPool().execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.State currentState = Thread.currentThread().getState();
                System.out.println(Arrays.stream(STATES).allMatch(s -> s == currentState));
            }
        });
    }

    void onComplete(Consumer<G> onComplete);
}





//    default void monitorFutureObject(CompletableFuture<G> future) {
//        while (!future.isDone()) {
//            // get the current thread name
//            String threadName = Thread.currentThread().getName();
//            ThreadSleep.LONG.sleep();
//            System.out.println("Waiting for future to complete...");
//
//        }
//    }