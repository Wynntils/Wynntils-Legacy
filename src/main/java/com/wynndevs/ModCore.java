package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;
import com.wynndevs.core.events.ClientEvents;
import com.wynndevs.core.gui.UpdateOverlay;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.WynnExpansion;
import com.wynndevs.modules.map.WynnMap;
import com.wynndevs.modules.market.WynnMarket;
import com.wynndevs.modules.richpresence.WynnRichPresence;
import com.wynndevs.modules.wynnicmap.WynnicMap;
import com.wynndevs.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION, acceptedMinecraftVersions = "[1.11,1.12.2]", clientSideOnly = true)
public class ModCore {

    public static ArrayList<String> invalidModules = new ArrayList<>();
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        WebManager.init();
        KeyBindings.init();

        logger = e.getModLog();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new UpdateOverlay(mc()));


        if(WynnRichPresence.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("RichPresence");
        }
        if(WynnExpansion.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Expansion");
        }
        if(WynnMap.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Map");
        }
        if(WynnMarket.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Market");
        }
        if(WynnicMap.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("WynnicMap");
        }

    }
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		if (!invalidModules.contains("Expansion")) WynnExpansion.init(e);
	}
	
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
