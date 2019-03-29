package com.wynntils.core.utils;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Delay {

    private Runnable function;
    private int delay;

    public Delay(Runnable function, int delay) {
        this.function = function;
        this.delay = delay;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            if (delay-- < 0) {
                function.run();
                end();
            }
        }
    }

    public void end() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
