/*
 *  * Copyright © Wynntils - 2022.
 */
package com.wynntils.modules.core.instances;

import com.wynntils.core.utils.objects.Location;

public class ShamanTotem {
    private int timerId;
    private int time;
    private TotemState state;
    private Location location;

    public ShamanTotem(int timerId, int time, TotemState totemState, Location location) {
        this.timerId = timerId;
        this.time = time;
        this.state = totemState;
        this.location = location;
    }

    public int getTimerId() {
        return timerId;
    }

    public void setTimerId(int timerId) {
        this.timerId = timerId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public TotemState getState() {
        return state;
    }

    public void setState(TotemState state) {
        this.state = state;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public enum TotemState {
        SUMMONED,
        ACTIVE
    }
}