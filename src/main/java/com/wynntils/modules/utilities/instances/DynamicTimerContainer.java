/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public class DynamicTimerContainer extends TimerContainer {

    private long expirationTime; // The timestamp when the timer expires

    public DynamicTimerContainer(String prefix, String name, String suffix, long expirationTime, boolean persistent) {
        super(prefix, name, suffix, persistent);
        this.expirationTime = expirationTime;
    }

    /**
     * @return The timestamp when the timer expires
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime The new timestamp for when the timer expires
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
