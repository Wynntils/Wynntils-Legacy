/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.DynamicTimerContainer;
import com.wynntils.modules.utilities.instances.StaticTimerContainer;
import com.wynntils.modules.utilities.instances.TimerContainer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.TreeSet;
import java.util.function.Predicate;

public class ConsumableTimerOverlay extends Overlay {

    // TreeSet for automatic sorting
    private static TreeSet<TimerContainer> activeTimers = new TreeSet<>();
    private static boolean timerReadLock = false;

    public ConsumableTimerOverlay() {
        super("Consumable Timer", 125, 60, true, 1, 0.2f, 0, 0, OverlayGrowFrom.TOP_RIGHT, RenderGameOverlayEvent.ElementType.ALL);
    }

    public static void clearTimers(boolean clearPersistent) {
        if (clearPersistent) {
            // assigns a new object to avoid CME
            activeTimers = new TreeSet<>();
        } else {
            TreeSet<TimerContainer> persistent = new TreeSet<>(activeTimers);
            persistent.removeIf(c -> !c.isPersistent());
            activeTimers = persistent;
        }
    }

    public static void clearStaticTimers(boolean clearPersistent) {
        Predicate<TimerContainer> staticPersistent = clearPersistent ?
                c -> c instanceof StaticTimerContainer :
                c -> c instanceof StaticTimerContainer && !c.isPersistent();

        activeTimers.removeIf(staticPersistent);
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
     * @param prefix The prefix to display before the name. Not included in identifying name.
     *               Put color codes here if you wish to use a colored timer.
     * @param name The name of the consumable
     * @param suffix The suffix to display after the name. Not included in identifying name.
     * @param initialTimeSeconds The initial time remaining for the consumable in seconds
     * @param persistent If the consumable should persist through death and character changes
     */
    public static void addDynamicTimer(String prefix, String name, String suffix, int initialTimeSeconds, boolean persistent) {
        long expirationTime = McIf.getSystemTime() + initialTimeSeconds*1000L;

        // If a DynamicTimerContainer or StaticTimerContainer already exists, get rid of it
        removeTimer(name);

        activeTimers.add(new DynamicTimerContainer(prefix, name, suffix, expirationTime, persistent));
    }

    /**
     * Adds a new {@link StaticTimerContainer} to the list of active consumables
     * Replaces any existing {@link TimerContainer} with the same name
     * @param prefix The prefix to display before the name. Not included in identifying name.
     *               Put color codes here if you wish to use a colored timer.
     * @param name The name of the consumable
     * @param suffix The suffix to display after the name. Not included in identifying name.
     * @param displayedTime The displayed time remaining. Should look something like "01:43".
     * @param persistent If the consumable should persist through death and character changes
     */
    public static void addStaticTimer(String prefix, String name, String suffix, String displayedTime, boolean persistent) {
        // If a DynamicTimerContainer or StaticTimerContainer already exists, get rid of it
        removeTimer(name);

        activeTimers.add(new StaticTimerContainer(prefix, name, suffix, displayedTime, persistent));
    }

    public static void removeTimer(String name) {
        if (timerReadLock) return;
        TimerContainer toRemove = findTimerContainer(name);
        if (toRemove != null) activeTimers.remove(toRemove);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        event.setCanceled(false);
        if (activeTimers.isEmpty()) return;

        TreeSet<TimerContainer> activeTimersCopy = new TreeSet<>();
        timerReadLock = true; // I don't know why this is required but without it the #removeTimer() sometimes decides to cause CME anyway
        activeTimersCopy.addAll(activeTimers); // Copy to avoid CME and NSEE
        timerReadLock = false;
        // Remove any expired timers, +1000 so we expire on 00:01 instead of 00:00 (consistent with game)
        activeTimersCopy.removeIf(c -> c instanceof DynamicTimerContainer && ((DynamicTimerContainer) c).getExpirationTime() < McIf.getSystemTime());

        int extraY = 0; // y-offset to make sure each timer does not overlap with the previous timer
        for (TimerContainer timer : activeTimersCopy) {

            // These first two lines makes sure we don't add extraneous spaces if the prefix or suffix is empty
            String prefixString = timer.getPrefix().isEmpty() ? "" : timer.getPrefix().trim() + " ";
            String suffixString = timer.getSuffix().isEmpty() ? "" : " " + timer.getSuffix().trim();

            // The displayed time remaining is decided by the type of timer here
            String timeLeftString = timer instanceof DynamicTimerContainer ?
                    // +1000 so we expire on 00:01 instead of 00:00 (consistent with game)
                    " (" + StringUtils.timeLeft(((DynamicTimerContainer) timer).getExpirationTime() + 1000 - McIf.getSystemTime()) + ")" :
                    " (" + ((StaticTimerContainer) timer).getDisplayedTime() + ")";

            // Draw both timer types
            drawString(prefixString + timer.getName() + suffixString + timeLeftString,
                    0, extraY, CommonColors.WHITE,
                    OverlayConfig.ConsumableTimer.INSTANCE.textAlignment,
                    OverlayConfig.ConsumableTimer.INSTANCE.textShadow);

            extraY += 10;
        }

        activeTimers = activeTimersCopy;
    }

}
