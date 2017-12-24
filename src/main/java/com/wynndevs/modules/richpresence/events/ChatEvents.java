package com.wynndevs.modules.richpresence.events;

import com.wynndevs.ConfigValues;
import com.wynndevs.ModCore;
import com.wynndevs.modules.richpresence.WynnRichPresence;
import com.wynndevs.modules.richpresence.guis.overlay.LocationGUI;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 04/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ChatEvents {

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public static ScheduledFuture updateTimer;

    /**
     * Handlers for world login and entering notifier
     *
     * @param e
     *        Origem event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onChatReceive(ClientChatReceivedEvent e) {
        if(e.getMessage().getFormattedText().toLowerCase().contains("loading the wynnpack...")) {

            new Thread(() -> {
                try{
                    InputStream st = new URL("https://api.wynncraft.com/public_api.php?action=playerStats&command=" + Minecraft.getMinecraft().player.getName()).openStream();
                    WynnRichPresence.getData().setActualServer(new JSONObject(IOUtils.toString(st)).getString("current_server"));

                    WynnRichPresence.getRichPresence().updateRichPresence("World " + WynnRichPresence.getData().getActualServer().replace("WC", ""), "At " + WynnRichPresence.getData().getLocation(), RichUtils.getPlayerInfo(), null);
                    WynnRichPresence.getData().setOnServer(true);

                    startUpdateRegionName();

                }catch (Exception ex) { ModCore.logger.warn("Cannot update status", ex); }
            }).start();

            return;
        }
        if(e.getMessage().getFormattedText().toLowerCase().contains("you are now entering") && !e.getMessage().getFormattedText().contains("/")) {
            if(ConfigValues.wynnRichPresence.enteringNotifier) {
                String loc = e.getMessage().getFormattedText();
                LocationGUI.location = RichUtils.stripColor(loc.replace("[You are now entering ", "").replace("]", ""));
                e.setCanceled(true);
            }
            return;
        }
        if(e.getMessage().getFormattedText().toLowerCase().contains("you are now leaving") && !e.getMessage().getFormattedText().contains("/")) {
            if(ConfigValues.wynnRichPresence.enteringNotifier) {
                LocationGUI.last_loc = "Waiting";
                LocationGUI.location = "Waiting";
                e.setCanceled(true);
            }
            return;
        }
    }

    /**
     * Starts to check player location for RichPresence current player territory info
     */
    public static void startUpdateRegionName() {
        updateTimer = executor.scheduleAtFixedRate(() -> {
            EntityPlayerSP pl = ModCore.mc().player;
            if(WynnRichPresence.getData().getLocId() != -1) {
                if(WebManager.getTerritories().get(WynnRichPresence.getData().getLocId()).insideArea((int)pl.posX, (int)pl.posZ)) {
                    return;
                }
            }

            for(int i = 0; i < WebManager.getTerritories().size(); i++) {
                TerritoryProfile pf = WebManager.getTerritories().get(i);
                if(pf.insideArea((int)pl.posX, (int)pl.posZ)) {
                    WynnRichPresence.getData().setLocation(pf.getName());
                    WynnRichPresence.getData().setLocId(i);

                    WynnRichPresence.getRichPresence().updateRichPresence("World " + WynnRichPresence.getData().getActualServer().replace("WC", ""), "At " + WynnRichPresence.getData().getLocation(), RichUtils.getPlayerInfo(), null);
                    break;
                }
            }

        }, 0, 3, TimeUnit.SECONDS);
    }

}
