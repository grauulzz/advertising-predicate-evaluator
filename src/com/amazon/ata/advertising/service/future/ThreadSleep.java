package com.amazon.ata.advertising.service.future;

public enum ThreadSleep {
    NONE(0),
    SHORT(1000),
    MEDIUM(2000),
    LONG(3000);

    private final long mSleepTime;

    private ThreadSleep(long sleepTime) {
        mSleepTime = sleepTime;
    }

    public long getSleepTime() {
        return mSleepTime;
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
