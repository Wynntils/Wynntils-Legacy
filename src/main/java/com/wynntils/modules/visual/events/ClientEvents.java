/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.visual.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.managers.CachedChunkManager;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void cacheChunks(PacketEvent<SPacketChunkData> event) {
        if (!Reference.onWorld || !VisualConfig.CachedChunks.INSTANCE.enabled) return;

        SPacketChunkData packet = event.getPacket();

        // Requests the chunk to be unloaded if loaded before loading (???)
        // this fixes some weird ass issue with optifine, don't ask too much
        if (packet.isFullChunk() && McIf.world().getChunk(packet.getChunkX(), packet.getChunkZ()).isLoaded()) {
            McIf.mc().addScheduledTask(() -> McIf.world().getChunkProvider().unloadChunk(packet.getChunkX(), packet.getChunkZ()));
        }

        CachedChunkManager.asyncCacheChunk(packet);
    }

    @SubscribeEvent
    public void joinWorld(WynnWorldEvent.Join e) {
        if (!VisualConfig.CachedChunks.INSTANCE.enabled) return;
        CachedChunkManager.startAsyncChunkLoader();
    }

}
