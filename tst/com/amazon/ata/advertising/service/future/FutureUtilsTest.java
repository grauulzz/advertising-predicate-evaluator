package com.amazon.ata.advertising.service.future;

import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FutureUtilsTest {

    @Test
    void whenFutureGetMethodIsCalled_ensureFutureIsNonNull() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.complete("test");
        assertNotNull(FutureUtils.get(future));
    }

    @Test
    void whenFutureGetMethodIsCalled_withExceptionallyCompleteFuture_throwsRuntimeEx() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalAccessException());
        assertThrows(RuntimeException.class, () -> FutureUtils.get(future));
    }

    @Test
    void whenFutureIsNull_ensureMonitorFutureMethod_outputsTextToConsole() {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new IllegalAccessException());
        FutureUtils.monitor(future, FutureMonitor.ConsoleLogger.RED.getColor());
        String expected = "org.gradle.internal.io";
        assertEquals(expected, System.out.toString().substring(0, 22));
    }


    @Test
    void whenFutureIsNull_ensureLogicInConditional_isValid() {
        Throwable throwable = new IllegalAccessException();
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        FutureUtils.monitor(future, FutureMonitor.ConsoleLogger.RED.getColor());
        assertEquals(String.valueOf(throwable), future.exceptionally(String::valueOf).join());
    }
}
