package com.wynndevs.wynnrp;

import com.wynndevs.wynnrp.events.ChatEvents;
import com.wynndevs.wynnrp.guis.overlay.LocationGUI;
import com.wynndevs.wynnrp.utils.Utils;
import com.wynndevs.ConfigValues;
import com.wynndevs.wynnrp.events.ServerEvents;
import com.wynndevs.wynnrp.profiles.DataProfile;
import com.wynndevs.wynnrp.profiles.RichProfile;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by HeyZeer0 on 04/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WynnRichPresence {

    private static RichProfile richPresence;
    private static DataProfile modData = new DataProfile();


    public static Logger logger;

    public static void startRichPresence(FMLPreInitializationEvent e) {
        WynnRichPresence.logger = e.getModLog();

        try{
            richPresence = new RichProfile(387266678607577088L, ConfigValues.discordConfig.discordBuild);

            //events
            MinecraftForge.EVENT_BUS.register(new ServerEvents());
            MinecraftForge.EVENT_BUS.register(new ChatEvents());

            //guis
            MinecraftForge.EVENT_BUS.register(new LocationGUI(Minecraft.getMinecraft()));

            Utils.updateRegions();
        }catch (Exception ignored) {}
    }

    public static RichProfile getRichPresence() {
        return richPresence;
    }

    public static DataProfile getData() {
        return modData;
    }
}
