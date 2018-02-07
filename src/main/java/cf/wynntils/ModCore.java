package cf.wynntils;

import cf.wynntils.core.CoreManager;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.ModuleManager;
import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION, acceptedMinecraftVersions = "[1.11,1.12.2]", clientSideOnly = true)
public class ModCore {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        CoreManager.setupCore();
        WebManager.setupWebApi();
        ModuleManager.initModules();

        FrameworkManager.startModules();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FrameworkManager.postInitModules();
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
