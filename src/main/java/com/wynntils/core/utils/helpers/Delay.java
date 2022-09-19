/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.helpers;

import com.wynntils.core.framework.FrameworkManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Delay {

    private Runnable function;
    private int delay;
    private boolean isRunning = true;
    private boolean onPause = false;

    /**
     * Creates a new Delay which is registered to the event bus
     * @param function the function to run after the delay
     * @param delay the delay in ticks
     */
    public Delay(Runnable function, int delay) {
        this.function = function;
        this.delay = delay;

        FrameworkManager.getEventBus().register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && !onPause && isRunning) {
            if (delay < 0) {
                start();
            }
            delay--;
        }
    }

    public boolean isRunning() {
        return isRunning && !onPause;
    }

    public boolean pause() {
        if (!onPause && isRunning) {
            onPause = true;
            return true;  // success
        }

        return false;
    }

    public boolean resume() {
        if (onPause && isRunning) {
            onPause = false;
            return true;  // success
        }

        return false;
    }

    public void start() {
        isRunning = false;
        function.run();
        end();
    }

    public void end() {
        isRunning = false;
        FrameworkManager.getEventBus().unregister(this);
    }
}
