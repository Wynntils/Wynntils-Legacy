package com.wynndevs.richpresence;

import com.wynndevs.richpresence.events.ChatEvents;
import com.wynndevs.richpresence.guis.overlay.LocationGUI;
import com.wynndevs.richpresence.utils.RichUtils;
import com.wynndevs.ConfigValues;
import com.wynndevs.richpresence.events.ServerEvents;
import com.wynndevs.richpresence.profiles.DataProfile;
import com.wynndevs.richpresence.profiles.RichProfile;
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

    public static void init(FMLPreInitializationEvent e) {
        WynnRichPresence.logger = e.getModLog();

        try{
            richPresence = new RichProfile(387266678607577088L, ConfigValues.wynnRichPresence.discordConfig.discordBuild);

            //events
            MinecraftForge.EVENT_BUS.register(new ServerEvents());
            MinecraftForge.EVENT_BUS.register(new ChatEvents());

            //guis
            MinecraftForge.EVENT_BUS.register(new LocationGUI(Minecraft.getMinecraft()));

            RichUtils.updateRegions();
        }catch (Exception ignored) {}
    }

    public static RichProfile getRichPresence() {
        return richPresence;
    }

    public static DataProfile getData() {
        return modData;
    }

}
