/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.WarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.ExecutionException;

public class ServerEvents implements Listener {

    private static boolean loadedResourcePack = false;

    @SubscribeEvent
    public void leaveServer(WynncraftServerEvent.Leave e) {
        loadedResourcePack = false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void joinServer(WynncraftServerEvent.Login ev) {
        if (!Reference.onLobby) return;

        if (loadedResourcePack &&
                Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack() == null &&
                Minecraft.getMinecraft().getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED
        ) {
            // Not actually loaded resource pack
            loadedResourcePack = false;
        }

        if (!loadedResourcePack && UtilitiesConfig.INSTANCE.autoResource && !UtilitiesConfig.INSTANCE.lastServerResourcePack.isEmpty()) {
            if (Minecraft.getMinecraft().getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.ENABLED) {
                Reference.LOGGER.warn("Did not auto apply Wynncraft server resource pack because resource packs have not been enabled");
                return;
            }

            String resourcePack = UtilitiesConfig.INSTANCE.lastServerResourcePack;
            String hash = UtilitiesConfig.INSTANCE.lastServerResourcePackHash;

            try {
                Minecraft.getMinecraft().getResourcePackRepository().downloadResourcePack(resourcePack, hash).get();
                loadedResourcePack = true;
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                Reference.LOGGER.error("Could not load server resource pack");
                e.printStackTrace();

                UtilitiesConfig.INSTANCE.lastServerResourcePack = "";
                UtilitiesConfig.INSTANCE.lastServerResourcePackHash = "";
                UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
            }
        }
    }

    @SubscribeEvent
    public void onResourcePackReceive(PacketEvent<SPacketResourcePackSend> e) {
        if(!Reference.onServer) return;

        String resourcePack = e.getPacket().getURL();
        String hash = e.getPacket().getHash();
        if (!resourcePack.equals(UtilitiesConfig.INSTANCE.lastServerResourcePack) || !hash.equalsIgnoreCase(UtilitiesConfig.INSTANCE.lastServerResourcePackHash)) {
            if (UtilitiesConfig.INSTANCE.lastServerResourcePack.isEmpty()) {
                Reference.LOGGER.info("Found server resource pack: \"server-resource-packs/" + DigestUtils.sha1Hex(resourcePack) + "\"#" + hash + " from \"" + resourcePack + "\"");
            } else {
                String lastPack = UtilitiesConfig.INSTANCE.lastServerResourcePack;
                String lastHash = UtilitiesConfig.INSTANCE.lastServerResourcePackHash;
                Reference.LOGGER.info(
                        "New server resource pack: \"server-resource-packs/" + DigestUtils.sha1Hex(resourcePack) + "\"#" + hash + " from \"" + resourcePack +
                        "\" (Was \"server-resource-packs/" + DigestUtils.sha1Hex(lastPack) + "\"#" + lastHash + " from \"" + lastPack + "\")"
                );
                // Loaded old resource pack, so don't cancel to load the new one
                loadedResourcePack = false;
            }
            UtilitiesConfig.INSTANCE.lastServerResourcePack = resourcePack;
            UtilitiesConfig.INSTANCE.lastServerResourcePackHash = hash;
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
        }

        if(loadedResourcePack && (  // Don't cancel if there isn't currently a server resource pack
                Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack() != null ||
                Minecraft.getMinecraft().getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.ENABLED
        )) {
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));

            e.setCanceled(true);
            return;
        }

        loadedResourcePack = true;
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent<SPacketSpawnObject> e) {
        if(WarManager.filterMob(e)) e.setCanceled(true);
    }


}
