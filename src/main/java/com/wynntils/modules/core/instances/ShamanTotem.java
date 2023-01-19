/*
 *  * Copyright © Wynntils - 2022.
 */
package com.wynntils.modules.core.instances;

import com.wynntils.core.utils.objects.Location;

public class ShamanTotem {
    private final int totemNumber;
    private final int visibleEntityId;
    private int timerEntityId;
    private int time;
    private TotemState state;
    private Location location;

    public ShamanTotem(int totemNumber, int visibleEntityId, int timerEntityId, int time, TotemState totemState, Location location) {
        this.totemNumber = totemNumber;
        this.visibleEntityId = visibleEntityId;
        this.timerEntityId = timerEntityId;
        this.time = time;
        this.state = totemState;
        this.location = location;
    }

    public int getTotemNumber() {
        return totemNumber;
    }

    public int getVisibleEntityId() {
        return visibleEntityId;
    }

    public int getTimerEntityId() {
        return timerEntityId;
    }

    public void setTimerEntityId(int timerEntityId) {
        this.timerEntityId = timerEntityId;
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