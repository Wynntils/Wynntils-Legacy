/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils;

import com.wynntils.core.CoreManager;
import com.wynntils.core.events.custom.ClientEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.rendering.WynnRenderItem;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.ModuleRegistry;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.core.managers.SSLCertificateManager;
import com.wynntils.modules.core.overlays.ui.ForgeConflictScreen;
import com.wynntils.modules.core.overlays.ui.ModConflictScreen;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.overlays.objects.MapApiIcon;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Mod(
        name = Reference.NAME,
        modid = Reference.MOD_ID,
        acceptedMinecraftVersions = "[" + Reference.MINECRAFT_VERSIONS + "]",
        guiFactory = "com.wynntils.core.framework.settings.ui.ModConfigFactory",
        clientSideOnly = true
)
public class ModCore {

    public static File jarFile = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        if (ForgeVersion.getBuildVersion() < 2856) throw new ForgeConflictScreen();

        SSLCertificateManager.registerCerts();

        Reference.VERSION = e.getModMetadata().version;
        String[] splitDescription = e.getModMetadata().description.split(" ");
        try {
            Reference.BUILD_NUMBER = Integer.parseInt(splitDescription[splitDescription.length - 1]);
        } catch (NumberFormatException ignored) {
        }

        jarFile = e.getSourceFile();

        Reference.developmentEnvironment = ((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
                || (System.getProperty("wynntils.development") != null && System.getProperty("wynntils.development").equals("true"));
        // Reference.developmentEnvironment = false;  // Uncomment to test updater

        if (Reference.developmentEnvironment)
            Reference.LOGGER.info("Development environment detected, automatic update detection disabled");

        Reference.LOGGER.info("Wynntils loaded " + Reference.VERSION + " (Build " + Reference.BUILD_NUMBER + ")"); // Print version

        WebManager.setupUserAccount();
        WebManager.setupWebApi(true);

        CoreManager.preModules();

        ModuleRegistry.registerModules();
        FrameworkManager.startModules();

        CoreManager.afterModules();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        Map<String, String> conflicts = new HashMap<>();
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if (!mod.getModId().equalsIgnoreCase("labymod")) continue;

            conflicts.put(mod.getName(), mod.getVersion());
        }

        if (!conflicts.isEmpty()) throw new ModConflictScreen(conflicts);

        WynnRenderItem.inject();

        FrameworkManager.postEnableModules();

        // HeyZeer0: This will reload our cache if a texture or similar is applied
        // This also immediately loads it
        ((SimpleReloadableResourceManager) McIf.mc().getResourceManager()).registerReloadListener(resourceManager -> {
            Textures.loadTextures();
            Mappings.loadMappings();
            MapApiIcon.resetApiMarkers();
        });

        if (MapConfig.INSTANCE.enabledMapIcons.containsKey("tnt")) {
            MapConfig.INSTANCE.enabledMapIcons = MapConfig.resetMapIcons(false);
            MapConfig.INSTANCE.saveSettings(MapModule.getModule());
        }

        FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
            @Override
            public String getLabel() {
                return "Wynntils Details";
            }

            @Override
            public String call() {
                UpdateStream stream = CoreDBConfig.INSTANCE == null ? null : CoreDBConfig.INSTANCE.updateStream;
                return "Running Wynntils v" + Reference.VERSION + " in " + stream + ", " + (Reference.developmentEnvironment ? "being a dev env" : "at a normal env") + (Reference.onBeta ? " (This crash occured on the Hero Beta)" : "");
            }
        });

        FrameworkManager.getEventBus().post(new ClientEvent.Ready());
    }

}
