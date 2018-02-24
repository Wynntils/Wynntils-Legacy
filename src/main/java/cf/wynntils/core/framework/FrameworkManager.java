package cf.wynntils.core.framework;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.KeyHolder;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class FrameworkManager {

    public static HashMap<String, ModuleContainer> availableModules = new HashMap<>();
    public static HashMap<Priority, ArrayList<Overlay>> registeredOverlays = new HashMap<>();

    static {
        registeredOverlays.put(Priority.LOWEST,new ArrayList<>());
        registeredOverlays.put(Priority.LOW,new ArrayList<>());
        registeredOverlays.put(Priority.NORMAL,new ArrayList<>());
        registeredOverlays.put(Priority.HIGH,new ArrayList<>());
        registeredOverlays.put(Priority.HIGHEST,new ArrayList<>());
    }

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


    public static void registerOverlay(Overlay overlay, Priority priority) {
        registeredOverlays.get(priority).add(overlay);
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

    public static void postEnableModules() {
        availableModules.values().forEach(c -> c.getModule().postEnable());
    }

    public static void disableModules() {
        availableModules.values().forEach(c -> c.getModule().onDisable());
    }

    public static void triggerEvent(Event e) {
        if(Reference.onServer()) {
            availableModules.values().forEach(c -> c.triggerEventHighest(e));
            availableModules.values().forEach(c -> c.triggerEventHigh(e));
            availableModules.values().forEach(c -> c.triggerEventNormal(e));
            availableModules.values().forEach(c -> c.triggerEventLow(e));
            availableModules.values().forEach(c -> c.triggerEventLowest(e));
        }
    }

    public static void triggerPreHud(RenderGameOverlayEvent.Pre e) {
        if (Reference.onServer()) {
            for (ArrayList<Overlay> overlays : registeredOverlays.values()) {
                for (Overlay overlay : overlays) {
                    if (overlay.module.getModule().isActive() && overlay.visible && overlay.active) {
                        ScreenRenderer.beginGL(overlay.oX(), overlay.oY());
                        overlay.render(e);
                        ScreenRenderer.endGL();
                    }
                }
            }
        }
    }

    public static void triggerPostHud(RenderGameOverlayEvent.Post e) {
        if (Reference.onServer()) {
            for (ArrayList<Overlay> overlays : registeredOverlays.values()) {
                for (Overlay overlay : overlays) {
                    if (overlay.module.getModule().isActive() && overlay.visible && overlay.active) {
                        ScreenRenderer.beginGL(overlay.oX(), overlay.oY());
                        overlay.render(e);
                        ScreenRenderer.endGL();
                    }
                }
            }
        }
    }

    public static void triggerKeyPress() {
        if(Reference.onServer())
            availableModules.values().forEach(ModuleContainer::triggerKeyBinding);
    }

}
