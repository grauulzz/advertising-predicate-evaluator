package com.amazon.ata.advertising.service.future;


import com.amazon.ata.advertising.service.exceptions.AdvertisementClientException;
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
    final Gson gson = builder.setPrettyPrinting().create();
    final String TEAL = "\u001B[36m";

    Consumer<Object> toJson = o -> {
        String[] lines = gson.toJson(o).split("\n");

        Arrays.stream(lines).map(
                line -> new StringBuilder().append(TEAL).append(line).append("\n").append(TEAL)).forEach(
                        line -> { ThreadSleep.SHORT.sleep(); System.out.println(line);
        });
    };

    default void monitor(CompletableFuture<G> completableFuture) {
        while (!completableFuture.isDone()) {
            ThreadSleep.SHORT.sleep();
            String threadName = Thread.currentThread().getName();
            System.out.printf("Waiting for {%s} to complete... {%s} %n", completableFuture, threadName);
        }
    }
    <T> void onComplete(CompletableFuture<T> onComplete);


//    void onComplete(Consumer<G> onComplete);
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