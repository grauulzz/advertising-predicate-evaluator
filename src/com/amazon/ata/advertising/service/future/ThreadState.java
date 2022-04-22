package com.amazon.ata.advertising.service.future;

import com.amazon.ata.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ThreadState {

    private static final Logger LOG = LogManager.getLogger(ThreadState.class);

    private ThreadState() {}

    static void listen() {
        org.apache.commons.lang3.ThreadUtils.getAllThreads().forEach(t -> {
            t.setUncaughtExceptionHandler((t1, e) -> LOG.error("[{}] thread has thrown an exception", t1.getName(), e));
            App.toJson.accept(String.format("[%s] thread is in [%s] state, group [%s], priority [%s]",
                    t.getName(), t.getState(), t.getThreadGroup(), t.getPriority()));
        });
    }
}
