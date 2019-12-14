/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils.helpers;

public class LongPress {

    long timeout;
    Runnable whenFinished;

    long endTime = 0;

    public LongPress(long timeout, Runnable whenFinished) {
        this.timeout = timeout; this.whenFinished = whenFinished;
    }

    public boolean isFinished() {
        return endTime != 0 && System.currentTimeMillis() >= endTime;
    }

    public void tick(boolean isKeyDown) {
        if (!isKeyDown) {
            endTime = 0;
            return;
        }

        if (endTime == 0) {
            endTime = System.currentTimeMillis() + timeout;
            return;
        }

        if (System.currentTimeMillis() < endTime) return;

        whenFinished.run();
    }

}
