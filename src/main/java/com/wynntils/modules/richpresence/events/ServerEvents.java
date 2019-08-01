/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.richpresence.RichPresenceModule;
import com.wynntils.modules.richpresence.configs.RichPresenceConfig;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ServerEvents implements Listener {

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static ScheduledFuture updateTimer;

    /**
     * Starts to check player location for RichPresence current player territory info
     */
    public static void startUpdateRegionName() {
        currentTime = OffsetDateTime.now();
        updateTimer = executor.scheduleAtFixedRate(() -> {
            EntityPlayerSP pl = ModCore.mc().player;

            boolean forceUpdate = ServerEvents.forceUpdate;
            forceUpdate |= currentLevel != ModCore.mc().player.experienceLevel;

            if (!forceUpdate) {
                if (!RichPresenceModule.getModule().getData().getLocation().equals("Waiting")) {
                    if (WebManager.getTerritories().get(RichPresenceModule.getModule().getData().getLocation().replace('\'', '’')).insideArea((int) pl.posX, (int) pl.posZ) && !classUpdate) {
                        return;
                    }
                }
            }
            
            currentLevel = ModCore.mc().player.experienceLevel;
            ServerEvents.forceUpdate = false;

            for (TerritoryProfile pf : WebManager.getTerritories().values()) {
                if(pf.insideArea((int)pl.posX, (int)pl.posZ)) {
                    RichPresenceModule.getModule().getData().setLocation(pf.getFriendlyName());
                    RichPresenceModule.getModule().getData().setUnknownLocation(false);

                    classUpdate = false;
                    
                    if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;

                    if(PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "In " + RichPresenceModule.getModule().getData().getLocation(), PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
                    }else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "In " + RichPresenceModule.getModule().getData().getLocation(), getPlayerInfo(), currentTime);
                    }
                    return;
                }
            }

            if (!RichPresenceModule.getModule().getData().getUnknownLocation() || classUpdate || forceUpdate) {
                classUpdate = false;
                RichPresenceModule.getModule().getData().setUnknownLocation(true);
                RichPresenceModule.getModule().getData().setLocation("Waiting");
                if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
                if (PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "Exploring Wynncraft", PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), currentTime);
                }
            }

        }, 0, 3, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onServerLeave(WynncraftServerEvent.Leave e) {
        RichPresenceModule.getModule().getRichPresence().stopRichPresence();
        currentLevel = 0;

        if (updateTimer != null && !updateTimer.isCancelled()) {
            updateTimer.cancel(true);
        }
    }

    @SubscribeEvent
    public void onWorldJoin(WynnWorldEvent.Join e) {
        if (Reference.onWars) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            if (WarTimerOverlay.getTerritory() != null) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring in " + WarTimerOverlay.getTerritory(), getPlayerInfo(), OffsetDateTime.now());
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring", getPlayerInfo(), OffsetDateTime.now());
            }
        }
        else if (Reference.onNether) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("N", ""), "In the nether", getPlayerInfo(), OffsetDateTime.now());
        }
        else {
            startUpdateRegionName();
        }
    }

    @SubscribeEvent
    public void onServerJoin(WynncraftServerEvent.Login e) {
        if (!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.contains("wynncraft") && RichPresenceConfig.INSTANCE.enableRichPresence) {
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("In Lobby", null, null, OffsetDateTime.now());
        }
    }

    public static boolean classUpdate = false;
    
    public static boolean forceUpdate = false;

    public static int currentLevel = 0;

    public static OffsetDateTime currentTime = null;

    @SubscribeEvent
    public void onWorldLeft(WynnWorldEvent.Leave e) {
        if (updateTimer != null) {
            updateTimer.cancel(true);
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            currentLevel = 0;
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("In Lobby", null, null, OffsetDateTime.now());
        }
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        if (Reference.onWars && e.getCurrentClass() != ClassType.NONE) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            currentLevel = 0;
            if (WarTimerOverlay.getTerritory() != null) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring in " + WarTimerOverlay.getTerritory(), e.getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring", e.getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
            }
        } else if (Reference.onNether && e.getCurrentClass() != ClassType.NONE) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("N", ""), "In the nether", e.getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
        } else if (e.getCurrentClass() != ClassType.NONE) {
            classUpdate = true;
        } else if (Reference.onWorld) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            currentLevel = 0;
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "Selecting a class", getPlayerInfo(), OffsetDateTime.now());
        }
    }

    @SubscribeEvent
    public void onSetExperience(PacketEvent.SetExperience e) {
        if (Reference.onWars && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE && currentLevel == 0 && e.getPacket().getLevel() != 0) {
            if (!RichPresenceConfig.INSTANCE.enableRichPresence) return;
            currentLevel = e.getPacket().getLevel();
            Minecraft mc = Minecraft.getMinecraft();
            String userInformation = RichPresenceConfig.INSTANCE.showUserInformation ? mc.player.getName() + " | Level " + currentLevel + " " + PlayerInfo.getPlayerInfo().getCurrentClass().toString() : null;
            if (WarTimerOverlay.getTerritory() != null) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring in " + WarTimerOverlay.getTerritory(), PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), userInformation, OffsetDateTime.now());
            } else {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring", PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), userInformation, OffsetDateTime.now());
            }
        }
    }

    public static void onEnableSettingChange() {
        if (RichPresenceConfig.INSTANCE.enableRichPresence) {
            if (Reference.onLobby) {
                RichPresenceModule.getModule().getRichPresence().updateRichPresence("In Lobby", null, null, OffsetDateTime.now());
            } else if (Reference.onWars) {
                if (PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                    if (WarTimerOverlay.getTerritory() != null) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring in " + WarTimerOverlay.getTerritory(), PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                    } else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring", PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                    }
                } else {
                    if (WarTimerOverlay.getTerritory() != null) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring in " + WarTimerOverlay.getTerritory(), getPlayerInfo(), OffsetDateTime.now());
                    } else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WAR", ""), "Warring", getPlayerInfo(), OffsetDateTime.now());
                    }
                }
            } else if (Reference.onNether) {
                if (PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("N", ""), "In the nether", PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                } else {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("N", ""), "In the nether", getPlayerInfo(), OffsetDateTime.now());
                }
            } else if (Reference.onWorld) {
                if (PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                    forceUpdate = true;
                    if (updateTimer == null || updateTimer.isCancelled()) {
                        startUpdateRegionName();
                    }
                } else {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "Selecting a class", getPlayerInfo(), OffsetDateTime.now());
                }
            }
        } else {
            RichPresenceModule.getModule().getRichPresence().stopRichPresence();
        }
    }


    /**
     * Just a simple method to short other ones
     * @return RichPresence largeImageText
     */
    public static String getPlayerInfo() {
        Minecraft mc = Minecraft.getMinecraft();
        return RichPresenceConfig.INSTANCE.showUserInformation ? mc.player.getName() + " | Level " + mc.player.experienceLevel + " " + PlayerInfo.getPlayerInfo().getCurrentClass().toString() : null;
    }

}
