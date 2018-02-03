package com.wynndevs;

import com.wynndevs.modules.expansion.WynnExpansion;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class ModCore {

    public static ArrayList<String> invalidModules = new ArrayList<>();
    public static Logger logger;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		if (!invalidModules.contains("Expansion")) WynnExpansion.init(e);
	}
	
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
