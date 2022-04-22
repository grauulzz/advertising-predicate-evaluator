package com.amazon.ata.advertising.service.future;


import com.amazon.ata.App;
import com.amazon.ata.ConsoleColors;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface FutureMonitor<G> {
    static final Logger LOG = LogManager.getLogger(FutureMonitor.class);

    default void monitor(CompletableFuture<G> completableFuture) {
        while (!completableFuture.isDone()) {
            ConsoleColors.pY.accept(String.format("Waiting for {%s} {%s} %n",
                    completableFuture, App.getCurrentTime()));
            ThreadUtils.LONG.sleep();
            ThreadUtils.logSleepDuration();


//            ThreadState.listen();
        }
    }
    void onComplete(CompletableFuture<G> onComplete);
}
