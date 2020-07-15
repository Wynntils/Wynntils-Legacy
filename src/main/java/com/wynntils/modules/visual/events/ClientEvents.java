/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.entities.EntityManager;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityDamageSplash;
import com.wynntils.modules.visual.entities.EntityFirefly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void spawnFireflies(TickEvent.ClientTickEvent e) {
        if (!Reference.onServer
                || !VisualConfig.Fireflies.INSTANCE.enabled
                || e.phase != TickEvent.Phase.END
                || Minecraft.getMinecraft().player == null) return;

        // TODO add biome verification here!
        if (!Reference.onLobby) return;

        Random r = Utils.getRandom();
        if (r.nextBoolean()) return; // reduce the spawn rates by half

        double maxDistance = (Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16) / 2;

        // makes a 10x10 cuboid and checks if a firefly should spawn in each block
        for (double x = -10; x < 10; x++) {
            for (double y = -1; y < 6; y++) {
                for (double z = -10; z < 10; z++) {
                    // firefly limit and randomness
                    if (EntityFirefly.fireflies.get() >= VisualConfig.Fireflies.INSTANCE.spawnLimit
                            || r.nextInt(VisualConfig.Fireflies.INSTANCE.spawnRate) != 0) continue;

                    Location base = new Location(Minecraft.getMinecraft().player).add(x, y, z);
                    EntityManager.spawnEntity(new EntityFirefly(base));
                }
            }
        }
    }

    @SubscribeEvent
    public void damageIndicators(GameEvent.DamageEntity e) {
        if (!VisualConfig.DamageSplash.INSTANCE.enabled) return;
        EntityManager.spawnEntity(new EntityDamageSplash(e.getDamageTypes(),
                new Location(e.getEntity())));

        e.getEntity().world.removeEntity(e.getEntity());
    }

}
