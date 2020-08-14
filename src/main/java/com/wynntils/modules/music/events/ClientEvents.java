/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.AreaManager;
import com.wynntils.modules.music.managers.MusicManager;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onTerritoryUpdate(WynnTerritoryChangeEvent e) {
        if (e.getNewTerritory().equals("") || Reference.onWars || AreaManager.isTerritoryUpdateBlocked()) return;

        MusicManager.checkForMusic(e.getNewTerritory());
    }

    @SubscribeEvent
    public void classChange(WynnClassChangeEvent e) {
        if (e.getNewClass() != ClassType.NONE || Reference.onWorld) return;

        MusicManager.getPlayer().stop();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void openClassSelection(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!MusicConfig.INSTANCE.classSelectionMusic || !e.getGui().getLowerInv().getName().contains("Select a Class")) return;

        MusicManager.playSong(WebManager.getApiUrl("CharacterSelectionSong"), true);
        MusicManager.setFastSwitchNext();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void closeClassSelection(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        if (!MusicConfig.INSTANCE.classSelectionMusic || !e.getGui().getLowerInv().getName().contains("Select a Class")) return;

        MusicManager.getPlayer().getStatus().setStopping(true);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) return;

        MusicManager.getPlayer().update();
    }

    @SubscribeEvent
    public void serverLeft(WynncraftServerEvent.Leave e) {
        MusicManager.getPlayer().stop();
    }

    @SubscribeEvent
    public void dungeonTracks(PacketEvent<SPacketTitle> e) {
        if (!MusicConfig.INSTANCE.replaceJukebox || e.getPacket().getType() != SPacketTitle.Type.TITLE) return;

        String title = TextFormatting.getTextWithoutFormattingCodes(e.getPacket().getMessage().getFormattedText());
        String songName = WebManager.getMusicLocations().getDungeonTrack(title);
        if (songName == null) return;

        MusicManager.playSong(songName, true);
    }

    @SubscribeEvent
    public void warTrack(WarStageEvent e) {
        if (!MusicConfig.INSTANCE.replaceJukebox || e.getNewStage() != WarTimerOverlay.WarStage.WAITING_FOR_MOB_TIMER) return;

        MusicManager.playSong(WebManager.getMusicLocations().getEntryTrack("wars"), true);
    }

    @SubscribeEvent
    public void specialAreas(SchedulerEvent.RegionUpdate e) {
        if (!MusicConfig.INSTANCE.replaceJukebox) return;

        AreaManager.update(new Location(Minecraft.getMinecraft().player));
    }

}
