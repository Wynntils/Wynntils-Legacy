/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils;

import com.wynntils.core.CoreManager;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.ModuleManager;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(
        name = Reference.NAME,
        modid = Reference.MOD_ID,
        acceptedMinecraftVersions = "[" + Reference.MINECRAFT_VERSIONS + "]",
        guiFactory = "com.wynntils.core.framework.settings.ui.ModConfigFactory",
        clientSideOnly = true
)
public class ModCore {

    public static final boolean DEBUG = false;
    public static File jarFile = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        Reference.VERSION = e.getModMetadata().version;
        String[] splitDescription = e.getModMetadata().description.split(" ");
        try {
            Reference.BUILD_NUMBER = Integer.valueOf(splitDescription[splitDescription.length - 1]);
        } catch (NumberFormatException ignored) {}

        jarFile = e.getSourceFile();

        Reference.developmentEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

        if (Reference.developmentEnvironment)
            Reference.LOGGER.info("Development environment detected, automatic update detection disabled");

        WebManager.setupWebApi();
        WebManager.setupUserAccount();

        CoreManager.setupCore();
        ModuleManager.initModules();

        FrameworkManager.startModules();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        FrameworkManager.postEnableModules();
        FrameworkManager.registerCommands();
        Textures.loadTextures();
        Mappings.loadMappings();

        //HeyZeer0: This will reload our cache if a texture or similar is applied
        ((SimpleReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> {
            Textures.loadTextures();
            Mappings.loadMappings();
        });
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
