package cf.wynntils;

import cf.wynntils.core.CoreManager;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.rendering.textures.Textures;
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

@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION, acceptedMinecraftVersions = "[" + Reference.MINECRAFT_VERSIONS + "]", clientSideOnly = true)
public class ModCore {

    public static final boolean DEBUG = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        CoreManager.setupCore();
        WebManager.setupWebApi();
        ModuleManager.initModules();

        FrameworkManager.startModules();
        WebManager.setupUserAccount();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FrameworkManager.postEnableModules();
        Textures.loadTextures();
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
