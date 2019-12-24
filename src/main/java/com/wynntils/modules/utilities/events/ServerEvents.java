/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.ServerResourcePackManager;
import com.wynntils.modules.utilities.managers.WarManager;
import com.wynntils.modules.utilities.managers.WindowIconManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.client.event.GuiScreenEvent;
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
            ModCore.mc().addScheduledTask(() -> {
                Display.setTitle(oldWindowTitle);
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void joinServer(WynncraftServerEvent.Login ev) {
        WindowIconManager.update();

        String title = Display.getTitle();
        if (!title.equals("Wynncraft")) {
            oldWindowTitle = title;
        }
        if (UtilitiesConfig.INSTANCE.changeWindowTitle) {
            ModCore.mc().addScheduledTask(() -> {
                Display.setTitle("Wynncraft");
            });
        }

        ServerResourcePackManager.applyOnServerJoin();
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
        if (ServerResourcePackManager.shouldCancelResourcePackLoad(e.getPacket())) {
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent<SPacketSpawnObject> e) {
        if (WarManager.filterMob(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClickEntity(PacketEvent<CPacketUseEntity> e) {
        if (WarManager.allowClick(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onMainMenu(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.getGui() instanceof GuiMainMenu)) return;

        boolean loadedServerResourcePack = Minecraft.getMinecraft().getResourcePackRepository().getServerResourcePack() != null;
        if (UtilitiesConfig.INSTANCE.autoResourceOnLoad) {
            if (!loadedServerResourcePack)
                ServerResourcePackManager.loadServerResourcePack();
        } else if (loadedServerResourcePack) {
            Minecraft.getMinecraft().getResourcePackRepository().clearResourcePack();
        }
    }

}
