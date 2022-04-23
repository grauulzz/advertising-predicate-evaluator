package com.amazon.ata.advertising.service.future;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.ThreadUtils;

import static com.amazon.ata.ConsoleColors.*;

public enum ThreadUtilities {
    NONE(0),
    TINY(100),
    SHORT(1000),
    MEDIUM(2000),
    LONG(3000);

    private final long mSleepTime;
    private static final String TEST_WORKER = "Test worker";
    final boolean isAExecutorShutdown = AsyncUtils.getExecutor.isShutdown();

    private ThreadUtilities(long sleepTime) {
        mSleepTime = sleepTime;
    }

    public void sleep() {
        if (isAExecutorShutdown) return;

        try {
            Thread.sleep(mSleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Thread createThread(Runnable runnable, String name) {
        return new Thread(runnable, name);
    }

    public Collection<Thread> getAllThreads() {
        return ThreadUtils.getAllThreads();
    }

    static void currentThreadLogger() {

        String name = Thread.currentThread().getName();
        if (name.equals(TEST_WORKER)) {
            // skip logging Test worker thread
            return;
        }
        pC.accept(String.format("current thread -> {%s}%n", Thread.currentThread().getName()));

    }
    public static void currentThreadLogger(CompletableFuture<?> completableFuture) {
        String name = Thread.currentThread().getName();
        if (name.equals(TEST_WORKER)) {
            // skip logging Test worker thread
            return;
        }
        pC.accept(String.format("current thread -> {%s}%nworking on -> %n{%s}%n",
                Thread.currentThread().getName(), completableFuture));
    }

    public static void currentThreadLogger(Class<?> clazz) {

        String name = Thread.currentThread().getName();
        if (name.equals("Test worker")) {
            // skip logging Test worker thread
            return;
        }
        pC.accept(String.format("current thread -> {%s}%nworking on -> %n{%s}%n", Thread.currentThread().getName(),
                clazz));
    }

    static void allThreadsLogger() {
        ThreadUtils.getAllThreads().forEach(t -> {
            t.setUncaughtExceptionHandler((t1, e) -> pR.accept(
                    String.format("{%s} thread has thrown an exception -> %n{%s}%n", t1.getName(), e)));
            pC.accept(String.format("current thread -> {%s}%n", Thread.currentThread().getName()));
            pY.accept(String.format("[%s] thread is in [%s] state, group [%s], priority [%s]%n", t.getName(),
                    t.getState(), t.getThreadGroup(), t.getPriority()));
        });
    }

}
