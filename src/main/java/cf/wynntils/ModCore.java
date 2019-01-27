package cf.wynntils;

import cf.wynntils.core.CoreManager;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.ModuleManager;
import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Mod(
        name = Reference.NAME,
        modid = Reference.MOD_ID,
        acceptedMinecraftVersions = "[" + Reference.MINECRAFT_VERSIONS + "]",
        guiFactory = "cf.wynntils.core.framework.settings.ui.ModConfigFactory",
        clientSideOnly = true
)
public class ModCore {

    public static final boolean DEBUG = false;
    public static File jarFile = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Reference.VERSION = e.getModMetadata().version;

        Reference.developmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        if (Reference.developmentEnvironment)
            Reference.LOGGER.info("Development environment detected, automatic update detection disabled");

        CoreManager.setupCore();
        WebManager.setupWebApi();
        ModuleManager.initModules();

        FrameworkManager.startModules();
        WebManager.setupUserAccount();

        jarFile = e.getSourceFile();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FrameworkManager.postEnableModules();
        FrameworkManager.registerCommands();
        Textures.loadTextures();
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
