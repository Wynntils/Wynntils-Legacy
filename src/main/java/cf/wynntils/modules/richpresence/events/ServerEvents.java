package cf.wynntils.modules.richpresence.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.events.custom.WynnWorldLeftEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.richpresence.RichPresenceConfig;
import cf.wynntils.modules.richpresence.RichPresenceModule;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ServerEvents implements Listener {

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static ScheduledFuture updateTimer;

    /**
     * Starts to check player location for RichPresence current player territory info
     */
    public static void startUpdateRegionName() {
        updateTimer = executor.scheduleAtFixedRate(() -> {
            EntityPlayerSP pl = ModCore.mc().player;

            if(RichPresenceModule.getModule().getData().getLocId() != -1) {
                if(WebManager.getTerritories().get(RichPresenceModule.getModule().getData().getLocId()).insideArea((int)pl.posX, (int)pl.posZ) && !classUpdate) {
                    return;
                }
            }

            for(int i = 0; i < WebManager.getTerritories().size(); i++) {
                TerritoryProfile pf = WebManager.getTerritories().get(i);
                if(pf.insideArea((int)pl.posX, (int)pl.posZ)) {
                    RichPresenceModule.getModule().getData().setLocation(pf.getName());
                    RichPresenceModule.getModule().getData().setLocId(i);
                    RichPresenceModule.getModule().getData().setUnknownLocation(false);

                    classUpdate = false;

                    if(PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "At " + RichPresenceModule.getModule().getData().getLocation(), PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                    }else {
                        RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "At " + RichPresenceModule.getModule().getData().getLocation(), getPlayerInfo(), OffsetDateTime.now());
                    }
                    return;
                }
            }

            if (!RichPresenceModule.getModule().getData().getUnknownLocation() || classUpdate) {
                classUpdate = false;
                RichPresenceModule.getModule().getData().setUnknownLocation(true);
                RichPresenceModule.getModule().getData().setLocId(-1);
                if (PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) {
                    RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "Exploring Wynncraft", PlayerInfo.getPlayerInfo().getCurrentClass().toString().toLowerCase(), getPlayerInfo(), OffsetDateTime.now());
                }
            }

        }, 0, 3, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onServerLeave(WynncraftServerEvent.Leave e) {
        RichPresenceModule.getModule().getRichPresence().stopRichPresence();

        if (updateTimer != null && !updateTimer.isCancelled()) {
            updateTimer.cancel(true);
        }
    }

    @EventHandler
    public void onWorldJoin(WynnWorldJoinEvent e) {
        startUpdateRegionName();
    }

    @EventHandler
    public void onServerJoin(WynncraftServerEvent.Login e) {
        if (!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.contains("wynncraft")) {
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("At Lobby", null, null, OffsetDateTime.now());
        }
    }

    public static boolean classUpdate = false;

    @EventHandler
    public void onWorldLeft(WynnWorldLeftEvent e) {
        if (updateTimer != null) {
            updateTimer.cancel(true);
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("At Lobby", null, null, OffsetDateTime.now());
        }
    }

    @EventHandler
    public void onClassChange(WynnClassChangeEvent e) {
        if (e.getCurrentClass() != ClassType.NONE) {
            classUpdate = true;
        } else if (Reference.onWorld) {
            RichPresenceModule.getModule().getRichPresence().updateRichPresence("World " + Reference.getUserWorld().replace("WC", ""), "Selecting a class", getPlayerInfo(), OffsetDateTime.now());
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
