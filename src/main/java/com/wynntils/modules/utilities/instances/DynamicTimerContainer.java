/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public class DynamicTimerContainer extends TimerContainer {

    private long expirationTime; // The time remaining for the consumable in seconds

    public DynamicTimerContainer(String name, long timeRemaining, boolean persistent) {
        super(name, persistent);
        this.expirationTime = timeRemaining;
    }

    /**
     * @return The time remaining for the consumable in seconds
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime The new time remaining for the consumable in seconds
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
