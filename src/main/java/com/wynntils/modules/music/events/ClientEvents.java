/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.music.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.enums.ToggleSetting;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.AreaTrackManager;
import com.wynntils.modules.music.managers.BossTrackManager;
import com.wynntils.modules.music.managers.SoundTrackManager;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ClientEvents implements Listener {

    // player ticking
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (!MusicConfig.INSTANCE.enabled || e.phase == TickEvent.Phase.START) return;

        SoundTrackManager.getPlayer().update();
    }

    @SubscribeEvent
    public void serverLeft(WynncraftServerEvent.Leave e) {
        SoundTrackManager.getPlayer().stop();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void worldLeft(WynnWorldEvent.Leave e) {
        SoundTrackManager.getPlayer().stop();
    }

    // character selection
    @SubscribeEvent
    public void characterChange(WynnClassChangeEvent e) {
        if (e.getNewClass() == ClassType.NONE && Reference.onWorld) return; // character selection

        // Toggle wynncraft music off if wynntils music replacer is enabled
        if (MusicConfig.INSTANCE.replaceJukebox && MusicConfig.INSTANCE.enabled && Reference.onWorld) {
            new Delay(() -> ToggleSetting.MUSIC.set(false), 20);
        }

        SoundTrackManager.getPlayer().stop();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void openCharacterSelection(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!MusicConfig.INSTANCE.classSelectionMusic || !e.getGui().getLowerInv().getName().contains("Select a Character")) return;

        SoundTrackManager.findTrack(WebManager.getMusicLocations().getEntryTrack("characterSelector"), true, MusicConfig.INSTANCE.characterSelectorQuiet);
    }

    // special tracks
    @SubscribeEvent
    public void dungeonTracks(PacketEvent<SPacketTitle> e) {
        if (!MusicConfig.INSTANCE.replaceJukebox || e.getPacket().getType() != SPacketTitle.Type.TITLE) return;

        String title = TextFormatting.getTextWithoutFormattingCodes(McIf.getFormattedText(e.getPacket().getMessage()));
        String songName = WebManager.getMusicLocations().getDungeonTrack(title);
        if (songName == null) return;

        SoundTrackManager.findTrack(songName, true);
    }

    @SubscribeEvent
    public void warTrack(WarStageEvent e) {
        if (!MusicConfig.INSTANCE.replaceJukebox || e.getNewStage() != WarTimerOverlay.WarStage.WAITING_FOR_MOB_TIMER) return;

        SoundTrackManager.findTrack(WebManager.getMusicLocations().getEntryTrack("wars"), true);
    }

    // area tracks
    @SubscribeEvent
    public void areaTracks(SchedulerEvent.RegionUpdate e) {
        if (!MusicConfig.INSTANCE.replaceJukebox) return;

        McIf.mc().addScheduledTask(BossTrackManager::update);

        if (BossTrackManager.isAlive()) return;
        AreaTrackManager.update(new Location(McIf.player()));
    }
}
