/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.richpresence.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.LocationData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.richpresence.RichPresenceModule;
import com.wynntils.modules.richpresence.configs.RichPresenceConfig;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay.WarStage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.time.OffsetDateTime;
import java.util.Objects;

public class ServerEvents implements Listener {

    @SubscribeEvent
    public void onServerLeave(WynncraftServerEvent.Leave e) {
        RichPresenceModule.getModule().getRichPresence().stopRichPresence();
    }

    @SubscribeEvent
    public void onWorldJoin(WynnWorldEvent.Join e) {
        if (!Reference.onWars) {
            currentTime = OffsetDateTime.now();
        }
    }

    @SubscribeEvent
    public void onServerJoin(WynncraftServerEvent.Login e) {
        if (!McIf.mc().isSingleplayer() && McIf.mc().getCurrentServerData() != null && Objects.requireNonNull(McIf.mc().getCurrentServerData()).serverIP.contains("wynncraft") && RichPresenceConfig.INSTANCE.enableRichPresence) {
            String state = "In Lobby";
            RichPresenceModule.getModule().getRichPresence().updateRichPresence(state, null, null, OffsetDateTime.now());
        }
    }

    public static boolean forceUpdate = false;

    public static OffsetDateTime currentTime = null;

    @SubscribeEvent
    public void onWorldLeft(WynnWorldEvent.Leave e) {
        if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
        String state = "In Lobby";
        RichPresenceModule.getModule().getRichPresence().updateRichPresence(state, null, null, OffsetDateTime.now());
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        if (!Reference.onWars && Reference.onWorld && e.getNewClass() == ClassType.NONE) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            RichPresenceModule.getModule().getRichPresence().updateRichPresence(getWorldDescription(), "Selecting a class", null, currentTime);
            forceUpdate = false;
        }
    }

    @SubscribeEvent
    public void onWarStageChange(WarStageEvent e) {
        if (e.getNewStage() == WarStage.WAITING_FOR_MOBS) {
            currentTime = OffsetDateTime.now().plusSeconds(30);
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            if (WarTimerOverlay.getTerritory() != null) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresenceEndDate("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Waiting for the war for " + WarTimerOverlay.getTerritory() + " to start", PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresenceEndDate("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Waiting for a war to start", PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
            }
        } else if (e.getNewStage() == WarStage.IN_WAR) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            if (WarTimerOverlay.getTerritory() != null) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring in " + WarTimerOverlay.getTerritory(), PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring", PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
            }
        }
    }

    @SubscribeEvent
    public void onTerritoryChange(WynnTerritoryChangeEvent e) {
        if (RichPresenceConfig.INSTANCE.enableRichPresence && Reference.onWorld && PlayerInfo.get(CharacterData.class).isLoaded()) {
            forceUpdate = true;
        }
    }

    @SubscribeEvent
    public void onLevelChange(PacketEvent<SPacketSetExperience> e) {
        if (!RichPresenceConfig.INSTANCE.enableRichPresence || !Reference.onWorld
                || !PlayerInfo.get(CharacterData.class).isLoaded()) return;

        if (e.getPacket().getLevel() != McIf.player().experienceLevel) {
            forceUpdate = true;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (Reference.onWorld && !Reference.onWars && forceUpdate && PlayerInfo.get(CharacterData.class).isLoaded()) {
            forceUpdate = false;
            if (!PlayerInfo.get(LocationData.class).inUnknownLocation()) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence(getWorldDescription(), "In " + PlayerInfo.get(LocationData.class).getLocation(), PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence(getWorldDescription(), "Exploring Wynncraft", PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
            }
        }
    }
    public static void onEnableSettingChange() {
        if (RichPresenceConfig.INSTANCE.enableRichPresence) {
            if (Reference.onLobby) {
                String state = "In Lobby";
                RichPresenceModule.getModule().getRichPresence().updateRichPresence(state, null, null, OffsetDateTime.now());
            } else if (Reference.onWars) {
                if (PlayerInfo.get(CharacterData.class).isLoaded()) {
                    if (WarTimerOverlay.getTerritory() != null) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring in " + WarTimerOverlay.getTerritory(), PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                    } else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring", PlayerInfo.get(CharacterData.class).getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                    }
                } else {
                    if (WarTimerOverlay.getTerritory() != null) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring in " + WarTimerOverlay.getTerritory(), getPlayerInfo(), OffsetDateTime.now());
                    } else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WAR", "")), "Warring", getPlayerInfo(), OffsetDateTime.now());
                    }
                }
            } else if (Reference.onWorld) {
                if (!PlayerInfo.get(CharacterData.class).isLoaded()) {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence(getWorldDescription(), "Selecting a class", getPlayerInfo(), OffsetDateTime.now());
                }
            }
        } else {
            RichPresenceModule.getModule().getRichPresence().stopRichPresence();
        }
    }

    private static String getWorldDescription() {
        if (Reference.getUserWorld() == null) return "Lobby";

        return "World " + (Reference.inStream ? "-" : Reference.getUserWorld().replace("WC", ""));
    }

    /**
     * Just a simple method to short other ones
     * @return RichPresence largeImageText
     */
    public static String getPlayerInfo() {
        return RichPresenceConfig.INSTANCE.showUserInformation ? McIf.player().getName() + " | Level " + McIf.player().experienceLevel + " " + PlayerInfo.get(CharacterData.class).getCurrentClass().toString() : null;
    }

}
