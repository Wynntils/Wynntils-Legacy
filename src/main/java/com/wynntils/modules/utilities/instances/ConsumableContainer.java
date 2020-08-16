/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public class ConsumableContainer {

    private String name;
    private long expirationTime = 0;
    private boolean persistent = false;

    Map<String, IdentificationHolder> effects = new HashMap<>();

    public ConsumableContainer(String name) {
        this.name = name;
    }

    public ConsumableContainer(String name, boolean persistent) {
        this.name = name;
        this.persistent = persistent;
    }

    /**
     * @return The display name for the consumable
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time in millis when the consumable is going to end
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @return the effects for the current Consumable
     */
    public Map<String, IdentificationHolder> getEffects() {
        return effects;
    }

    /**
     * This register a new effect for the ConsumableContainer instance
     * used while calculating the effects amount in {@link com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay}
     *
     * @param shortIdName the identification short name, ex: healthRegen
     * @param currentAmount the identification amount
     * @param modifier the identification modifier
     */
    public void addEffect(String shortIdName, int currentAmount, IdentificationModifier modifier) {
        effects.put(shortIdName, new IdentificationHolder(currentAmount, modifier));
    }

    /**
     * @param shortIdName the effect id short name
     * @return if the consumable has the provided effect
     */
    public boolean hasEffect(String shortIdName) {
        return effects.containsKey(shortIdName);
    }

    /**
     * @param shortIdName the effect id short name
     * @return the identification holder
     */
    public IdentificationHolder getEffect(String shortIdName) {
        return effects.getOrDefault(shortIdName, null);
    }

    /**
     * Changes the consumable final time
     * @param expirationTime the provided time in milliseconds
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime + 1000; // extra 1s to include the ping
    }

    /**
     * @return if the consumable is a valid one based on if it has a expirationTime and any effects
     */
    public boolean isValid() {
        return expirationTime != 0 && effects.size() >= 1;
    }

    /**
     * @return if the consumable duration has already expired
     */
    public boolean hasExpired() {
        return Minecraft.getSystemTime() >= expirationTime;
    }

    public boolean isPersistent() {
        return persistent;
    }

}