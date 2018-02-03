package cf.wynntils;

import cf.wynntils.core.CoreManager;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.ModuleManager;
import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION)
public class ModCore {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        CoreManager.setupCore();
        WebManager.setupWebApi();
        ModuleManager.initModules();

        FrameworkManager.startModules();
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
