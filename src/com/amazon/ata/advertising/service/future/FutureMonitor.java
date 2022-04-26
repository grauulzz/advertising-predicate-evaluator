package com.amazon.ata.advertising.service.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.commons.lang3.ThreadUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<G> completableFuture) {
        if (!completableFuture.isDone()) {
            System.out.println((String.format("Waiting for {%s} %n", completableFuture)));
        }
        completableFuture.thenAccept((G g) -> System.out.println("Completed future"));
    }
    default void monitor(CompletableFuture<G> completableFuture, Consumer<String> color) {
        // allThreadsLogger();
        if (!completableFuture.isDone()) {
            color.accept(String.format("[%s][%s]",Thread.currentThread().getName(), Thread.currentThread().getState()));
            color.accept(String.format("Waiting for {%s} %n", completableFuture));
        }

        completableFuture.whenComplete((G g, Throwable t) -> {
            if (t != null) {
                ConsoleLogger.RED.log(String.format("Future -> {%s} exception {%s}", completableFuture, t));
            }

            if (completableFuture.isCompletedExceptionally() || completableFuture.join() == null) {
                ConsoleLogger.RED.log(String.format("Future completed with errors -> {%s}%n", completableFuture));
            }
            color.accept(String.format("Completed future -> {%s}%n", g));
        });
    }

    static void allThreadsLogger() {
        ThreadUtils.getAllThreads().forEach(t -> {
            t.setUncaughtExceptionHandler((t1, e) -> ConsoleLogger.RED.getColor().accept(
                    String.format("{%s} thread has thrown an exception {%s}%n", t1.getName(), e)));
            System.out.printf("thread{%s} state{%s}, group{%s}, priority{%s}%n", t.getName(), t.getState(),
                    t.getThreadGroup(), t.getPriority());
        });
    }
    static final String DEFAULT = "\u001B[0m";
    static final Consumer<String> magenta = s -> System.out.println("\u001b[35m" + s + DEFAULT);
    static final Consumer<String> yellow = s -> System.out.println("\u001b[33m" + s + DEFAULT);
    static final Consumer<String> green = s -> System.out.println("\u001b[32m" + s + DEFAULT);
    static final Consumer<String> cyan = s -> System.out.println("\u001B[36m" + s + DEFAULT);
    static final Consumer<String> red = s -> System.out.println("\u001b[31m" + s + DEFAULT);

    public enum ConsoleLogger {
        MAGENTA(magenta), YELLOW(yellow), GREEN(green), CYAN(cyan), RED(red);
        private final Consumer<String> color;
        ConsoleLogger(Consumer<String> color) {
            this.color = color;
        }
        public Consumer<String> getColor() {
            return color;
        }
        public void log(String message) {
            color.accept(message);
        }
    }
}
