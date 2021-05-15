/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.ClientEvents;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.*;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public class ServerEvents implements Listener {

    private static String oldWindowTitle = "Minecraft " + ForgeVersion.mcVersion;

    @SubscribeEvent
    public void leaveServer(WynncraftServerEvent.Leave e) {
        WindowIconManager.update();
        if (UtilitiesConfig.INSTANCE.changeWindowTitle) {
            McIf.mc().addScheduledTask(() -> Display.setTitle(oldWindowTitle));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void joinServer(WynncraftServerEvent.Login ev) {
        WindowIconManager.update();
        ServerListManager.updateServers();

        String title = Display.getTitle();
        if (!title.equals("Wynncraft")) {
            oldWindowTitle = title;
        }
        if (UtilitiesConfig.INSTANCE.changeWindowTitle) {
            McIf.mc().addScheduledTask(() -> Display.setTitle("Wynncraft"));
        }
        ClientEvents.setLoadingStatusMsg("Loading resources...");
        ServerResourcePackManager.applyOnServerJoin();
    }

    @SubscribeEvent
    public void worldLeave(WynnWorldEvent.Leave e) {
        ServerListManager.updateServers();
    }

    @SubscribeEvent
    public void worldJoin(WynnWorldEvent.Join e) {
        LeaderboardManager.updateLeaders();
    }

    public static void onWindowTitleSettingChanged() {
        if (UtilitiesConfig.INSTANCE.changeWindowTitle && Reference.onServer && !Display.getTitle().equals("Wynncraft")) {
            oldWindowTitle = Display.getTitle();
            Display.setTitle("Wynncraft");
        } else if (!UtilitiesConfig.INSTANCE.changeWindowTitle && Reference.onServer && Display.getTitle().equals("Wynncraft")) {
            Display.setTitle(oldWindowTitle);
        }
    }

    @SubscribeEvent
    public void onResourcePackReceive(PacketEvent<SPacketResourcePackSend> e) {
        if (!ServerResourcePackManager.shouldCancelResourcePackLoad(e.getPacket())) return;

        e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
        e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent<SPacketSpawnObject> e) {
        if (WarManager.filterMob(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClickEntity(PacketEvent<CPacketUseEntity> e) {
        if (WarManager.allowClick(e)) e.setCanceled(true);
    }

}
