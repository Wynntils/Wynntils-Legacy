/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.visual.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.entities.EntityManager;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityDamageSplash;
import com.wynntils.modules.visual.managers.CachedChunkManager;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void damageIndicators(GameEvent.DamageEntity e) {
        if (!VisualConfig.DamageSplash.INSTANCE.enabled) return;
        EntityManager.spawnEntity(new EntityDamageSplash(e.getDamageTypes(),
                new Location(e.getEntity())));

        e.getEntity().world.removeEntity(e.getEntity());
    }

    @SubscribeEvent
    public void cacheChunks(PacketEvent<SPacketChunkData> event) {
        if (!Reference.onWorld || !VisualConfig.CachedChunks.INSTANCE.enabled) return;

        CachedChunkManager.asyncCacheChunk(event.getPacket());
    }

    @SubscribeEvent
    public void joinWorld(WynnWorldEvent.Join e) {
        if (!VisualConfig.CachedChunks.INSTANCE.enabled) return;
        CachedChunkManager.startAsyncChunkLoader();
    }

}
