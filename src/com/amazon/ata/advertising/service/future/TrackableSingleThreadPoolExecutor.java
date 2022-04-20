//package com.amazon.ata.advertising.service.future;
//
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * A custom trackable thread pool which can keep and provide a currently running
// * task and is able to execute {@link TrackableRunnable} which keeps useful
// * information about the current execution.
// * <p>
// * This implementation follows configuration representing
// * {@link Executors#newSingleThreadExecutor()}, the tracking will stop working
// * with multiple workers, some additional changes needed to be done
// * to support multiple workers.
// */
//public class TrackableSingleThreadPoolExecutor extends ThreadPoolExecutor {
//
//    /*
//     * Task must be held as a volatile variable even in SingleThreadedExecutor.
//     * - A thread is destroyed and new one is recreated when an exception is thrown and caught.
//     */
//    private volatile TrackableRunnable activeTask;
//
//    private TrackableSingleThreadPoolExecutor(ThreadFactory threadFactory) {
//        super(1, 1, 0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<>(), threadFactory);
//    }
//
//    @Override
//    protected void beforeExecute(Thread thread, Runnable runnable) {
//        if (!(runnable instanceof TrackableRunnable)) {
//            throw new IllegalArgumentException("Executed task must be an instance of "
//                                                       + TrackableRunnable.class.getSimpleName());
//        }
//
//        this.activeTask = (TrackableRunnable) runnable;
//    }
//
//    @Override
//    protected void afterExecute(Runnable runnable, Throwable thread) {
//        this.activeTask = null;
//    }
//
//    public TrackableRunnable getActiveTask() {
//        return activeTask;
//    }
//
//    /**
//     * Keeps a context with an executed runnable. We can track information
//     * about currently executed task.
//     */
//    public static class TrackableRunnable implements Runnable {
//
//        private final Contextual context;
//
//        public TrackableRunnable(Contextual context) {
//            this.context = context;
//        }
//
//        @Override
//        public void run() {
//            // Some interesting computation.
//        }
//
//        public Contextual getContext() {
//            return context;
//        }
//    }
//}