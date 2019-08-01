package com.wynntils.core.utils;

public class LongPress {

    long timeout;
    Runnable whenFinished;

    long endTime = 0;

    public LongPress(long timeout, Runnable whenFinished) {
        this.timeout = timeout; this.whenFinished = whenFinished;
    }

    public void tick(boolean isKeyDown) {
        if(!isKeyDown) {
            endTime = 0;
            return;
        }

        if(endTime == 0) {
            endTime = System.currentTimeMillis() + timeout;
            return;
        }

        if(System.currentTimeMillis() < endTime) return;

        whenFinished.run();
        endTime = 0;
    }

}
