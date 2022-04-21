package com.amazon.ata.advertising.service.future;

public enum ThreadUtils {
    NONE(0),
    TINY(100),
    SHORT(1000),
    MEDIUM(2000),
    LONG(3000);

    private final long mSleepTime;

    private ThreadUtils(long sleepTime) {
        mSleepTime = sleepTime;
    }

    public void sleep() {
        try {
            Thread.sleep(mSleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

}
