package com.wynndevs.richpresence;

import com.wynndevs.core.enums.ModuleResult;
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

    /**
     * Loads the module
     *
     * @param e
     *        FMLPreInit event
     *
     * @return The result of module load
     */
    public static ModuleResult initModule(FMLPreInitializationEvent e) {
        WynnRichPresence.logger = e.getModLog();

        try{
            richPresence = new RichProfile(387266678607577088L, ConfigValues.wynnRichPresence.discordConfig.discordBuild);

            //events
            MinecraftForge.EVENT_BUS.register(new ServerEvents());
            MinecraftForge.EVENT_BUS.register(new ChatEvents());

            //guis
            MinecraftForge.EVENT_BUS.register(new LocationGUI(Minecraft.getMinecraft()));

            RichUtils.updateRegions();

            return ModuleResult.SUCCESS;
        }catch (Exception ignored) {}

        return ModuleResult.ERROR;
    }

    /**
     * Get the current RichPresence online instance
     * @return RichPresence profile
     */
    public static RichProfile getRichPresence() {
        return richPresence;
    }

    /**
     * Get the current session saved data
     * @return Memory data profile
     */
    public static DataProfile getData() {
        return modData;
    }

}
