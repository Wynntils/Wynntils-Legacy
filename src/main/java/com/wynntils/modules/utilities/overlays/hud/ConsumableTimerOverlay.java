/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.TimerContainer;
import com.wynntils.modules.utilities.instances.DynamicTimerContainer;
import com.wynntils.modules.utilities.instances.StaticTimerContainer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.*;

public class ConsumableTimerOverlay extends Overlay {

    private static List<TimerContainer> activeTimers = new ArrayList<>();

    public ConsumableTimerOverlay() {
        super("Consumable Timer", 125, 60, true, 1, 0.2f, 0, 0, OverlayGrowFrom.TOP_RIGHT, RenderGameOverlayEvent.ElementType.ALL);
    }

    public static void clearTimers(boolean clearPersistent) {
        if (clearPersistent) {
            // assigns a new object to avoid CME
            activeTimers = new ArrayList<>();
        } else {
            List<TimerContainer> persistent = new ArrayList<>(activeTimers);
            persistent.removeIf(c -> !c.isPersistent());
            activeTimers = persistent;
        }
    }

    public static void clearStaticTimers(boolean clearPersistent) {
        if (clearPersistent) {
            // assigns a new object to avoid CME
            activeTimers = new ArrayList<>(getDynamicTimers());
        } else {
            List<TimerContainer> persistentDynamic = getDynamicTimers();
            persistentDynamic.removeIf(c -> !c.isPersistent());
            activeTimers = persistentDynamic;
        }
    }

    public static List<TimerContainer> getDynamicTimers() {
        List<TimerContainer> toReturn = new ArrayList<>();
        for (TimerContainer c : activeTimers) {
            if (c instanceof DynamicTimerContainer) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }

    /**
     * @param name The consumable name to find
     * @return The {@link StaticTimerContainer} or {@link DynamicTimerContainer} with the given name, or null if not found
     */
    public static TimerContainer findTimerContainer(String name) {
        for (TimerContainer c : activeTimers) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Adds a new {@link DynamicTimerContainer} to the list of active consumables
     * Replaces any existing {@link TimerContainer} with the same name
     * @param name The name of the consumable
     * @param initialTimeSeconds The initial time remaining for the consumable in seconds
     * @param persistent If the consumable should persist through death and character changes
     */
    public static void addDynamicTimer(String name, int initialTimeSeconds, boolean persistent) {
        long expirationTime = McIf.getSystemTime() + initialTimeSeconds;

        // If a DynamicTimerContainer or StaticTimerContainer already exists, get rid of it
        activeTimers.remove(findTimerContainer(name));

        activeTimers.add(new DynamicTimerContainer(name, expirationTime, persistent));
    }

    /**
     * Adds a new {@link StaticTimerContainer} to the list of active consumables
     * Replaces any existing {@link TimerContainer} with the same name
     * @param prefix The prefix to display before the name. Not included in identifying name.
     * @param name The name of the consumable
     * @param persistent If the consumable should persist through death and character changes
     */
    public static void addStaticTimer(String prefix, String name, String displayedTime, boolean persistent) {
        // If a DynamicTimerContainer or StaticTimerContainer already exists, get rid of it
        activeTimers.remove(findTimerContainer(name));

        activeTimers.add(new StaticTimerContainer(prefix, name, displayedTime, persistent));
    }

    public static void removeTimer(String name) {
        activeTimers.remove(findTimerContainer(name));
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        event.setCanceled(false);
        if (activeTimers.isEmpty()) return;

        Iterator<TimerContainer> it = activeTimers.iterator();

        int extraY = 0; // y-offset to make sure each timer does not overlap with the previous timer
        while (it.hasNext()) {
            TimerContainer timer = it.next();

            if (timer instanceof DynamicTimerContainer) { // Remove expired DynamicTimerContainers
                DynamicTimerContainer dynamicTimer = (DynamicTimerContainer) timer;
                long timeRemaining = dynamicTimer.getExpirationTime() - McIf.getSystemTime();
                if (timeRemaining <= 0) {
                    it.remove();
                    continue;
                }
            }

            if (timer instanceof DynamicTimerContainer) { // Update the displayed time for DynamicTimerContainers
                drawString(timer.getName() + " (" + StringUtils.timeLeft(((DynamicTimerContainer) timer).getExpirationTime() - McIf.getSystemTime()) + ")",
                        0, extraY, CommonColors.WHITE,
                        OverlayConfig.ConsumableTimer.INSTANCE.textAlignment,
                        OverlayConfig.ConsumableTimer.INSTANCE.textShadow);
            }

            if (timer instanceof StaticTimerContainer) { // Update the displayed time for StaticTimerContainers
                drawString(((StaticTimerContainer) timer).getPrefix() + " " + timer.getName() + " (" + ((StaticTimerContainer) timer).getDisplayedTime() + ")",
                        0, extraY, CommonColors.WHITE,
                        OverlayConfig.ConsumableTimer.INSTANCE.textAlignment,
                        OverlayConfig.ConsumableTimer.INSTANCE.textShadow);
            }

            extraY += 10;
        }
    }

}
