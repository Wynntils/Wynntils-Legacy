package cf.wynntils.core.framework;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.HudOverlay;
import cf.wynntils.core.framework.instances.KeyHolder;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class FrameworkManager {

    public static HashMap<String, ModuleContainer> availableModules = new HashMap<>();

    public static void registerModule(Module module) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return;
        }

        module.setLogger(LogManager.getFormatterLogger(Reference.MOD_ID + "-" + info.name().toLowerCase()));

        availableModules.put(info.name(), new ModuleContainer(info, module));
    }

    public static void registerEvents(Module module, Listener listener) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return;
        }

        availableModules.get(info.name()).registerEvents(listener);
    }


    public static void registerHudOverlay(Module module, HudOverlay overlay) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return;
        }

        availableModules.get(info.name()).registerHudOverlay(overlay);
    }

    public static void registerKeyBinding(Module module, KeyHolder holder) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return;
        }

        availableModules.get(info.name()).registerKeyBinding(holder);
    }

    public static void startModules() {
        availableModules.values().forEach(c -> c.getModule().onEnable());
    }

    public static void postInitModules() {
        availableModules.values().forEach(c -> c.getModule().postInit());
    }

    public static void disableModules() {
        availableModules.values().forEach(c -> c.getModule().onDisable());
    }

    public static void triggerEvent(Event e) {
        if(Reference.onServer())
            availableModules.values().forEach(c -> c.triggerEvent(e));
    }

    public static void triggerPreHud(RenderGameOverlayEvent.Pre e) {
        if(Reference.onServer())
            availableModules.values().forEach(c -> c.triggerPreHud(e));
    }

    public static void triggerPostHud(RenderGameOverlayEvent.Post e) {
        if(Reference.onServer())
            availableModules.values().forEach(c -> c.triggerPostHud(e));
    }

    public static void triggerKeyPress() {
        if(Reference.onServer())
            availableModules.values().forEach(ModuleContainer::triggerKeyBinding);
    }

}
