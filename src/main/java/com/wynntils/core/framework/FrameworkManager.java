/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.settings.SettingsContainer;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.commands.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraft.client.gui.Gui.ICONS;

public class FrameworkManager {

    private static long tick = 0;

    public static HashMap<String, ModuleContainer> availableModules = new HashMap<>();
    public static HashMap<Priority, ArrayList<Overlay>> registeredOverlays = new HashMap<>();

    private static EventBus eventBus = new EventBus();

    static {
        registeredOverlays.put(Priority.LOWEST, new ArrayList<>());
        registeredOverlays.put(Priority.LOW, new ArrayList<>());
        registeredOverlays.put(Priority.NORMAL, new ArrayList<>());
        registeredOverlays.put(Priority.HIGH, new ArrayList<>());
        registeredOverlays.put(Priority.HIGHEST, new ArrayList<>());
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

    public static void registerSettings(Module module, Class<? extends SettingsHolder> settingsClass) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null)
            return;

        availableModules.get(info.name()).registerSettings(settingsClass);
    }


    public static void registerOverlay(Module module, Overlay overlay, Priority priority) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null)
            return;

        ModuleContainer mc = availableModules.get(info.name());

        overlay.module = mc;

        mc.registerSettings("overlay" + overlay.displayName, overlay);

        registeredOverlays.get(priority).add(overlay);
    }

    public static KeyHolder registerKeyBinding(Module module, KeyHolder holder) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return null;
        }

        availableModules.get(info.name()).registerKeyBinding(holder);
        return holder;
    }

    public static void reloadSettings() {
        availableModules.values().forEach(ModuleContainer::reloadSettings);
    }

    public static void startModules() {
        availableModules.values().forEach(c -> c.getModule().onEnable());
    }

    public static void postEnableModules() {
        availableModules.values().forEach(c -> c.getModule().postEnable());
    }

    public static void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new CommandWynntils());
        ClientCommandHandler.instance.registerCommand(new CommandToken());
        ClientCommandHandler.instance.registerCommand(new CommandForceUpdate());
        ClientCommandHandler.instance.registerCommand(new CommandCompass());
        ClientCommandHandler.instance.registerCommand(new CommandTerritory());
    }

    public static void disableModules() {
        availableModules.values().forEach(c -> {
            c.getModule().onDisable(); c.unregisterAllEvents();
        });
    }

    public static void triggerEvent(Event e) {
        if(Reference.onServer || e instanceof WynncraftServerEvent || e instanceof TickEvent.RenderTickEvent) {
            ReflectionFields.Event_phase.setValue(e, null);
            eventBus.post(e);
        }
    }

    public static void triggerPreHud(RenderGameOverlayEvent.Pre e) {
        if (Reference.onServer && !ModCore.mc().playerController.isSpectator()) {
            if(e.getType() == RenderGameOverlayEvent.ElementType.AIR || //move it to somewhere else if you want, it seems pretty core to wynncraft tho..
               e.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
                e.setCanceled(true);
                return;
            }
            Minecraft.getMinecraft().profiler.startSection("preRenOverlay");
            for (ArrayList<Overlay> overlays : registeredOverlays.values()) {
                for (Overlay overlay : overlays) {
                    if(!overlay.active) continue;

                    if (overlay.overrideElements.length != 0) {
                        boolean contained = false;
                        for (RenderGameOverlayEvent.ElementType type : overlay.overrideElements) {
                            if (e.getType() == type) {
                                contained = true;
                                break;
                            }
                        }
                        if (contained)
                            e.setCanceled(true);
                        else
                            continue;
                    }
                    if ((overlay.module == null || overlay.module.getModule().isActive()) && overlay.visible && overlay.active) {
                        Minecraft.getMinecraft().profiler.startSection(overlay.displayName);
                        ScreenRenderer.beginGL(overlay.position.getDrawingX(), overlay.position.getDrawingY());
                        overlay.render(e);
                        ScreenRenderer.endGL();
                        Minecraft.getMinecraft().profiler.endSection();
                    }
                }
            }
            Minecraft.getMinecraft().profiler.endSection();
            Minecraft.getMinecraft().getTextureManager().bindTexture(ICONS);
        }
    }

    public static void triggerPostHud(RenderGameOverlayEvent.Post e) {
        if (Reference.onServer && !ModCore.mc().playerController.isSpectator()) {
            Minecraft.getMinecraft().profiler.startSection("posRenOverlay");
            for (ArrayList<Overlay> overlays : registeredOverlays.values()) {
                for (Overlay overlay : overlays) {
                    if(!overlay.active) continue;
                    if ((overlay.module == null || overlay.module.getModule().isActive()) && overlay.visible && overlay.active) {
                        Minecraft.getMinecraft().profiler.startSection(overlay.displayName);
                        ScreenRenderer.beginGL(overlay.position.getDrawingX(), overlay.position.getDrawingY());
                        overlay.render(e);
                        ScreenRenderer.endGL();
                        Minecraft.getMinecraft().profiler.endSection();
                    }
                }
            }
            Minecraft.getMinecraft().profiler.endSection();
        }
    }

    public static void triggerHudTick(TickEvent.ClientTickEvent e) {
        if(e.phase == TickEvent.Phase.START) return;

        if (Reference.onServer) {
            tick++;
            for (ArrayList<Overlay> overlays : registeredOverlays.values()) {
                for (Overlay overlay : overlays) {
                    if ((overlay.module == null || overlay.module.getModule().isActive()) && overlay.active) {
                        overlay.position.refresh(ScreenRenderer.screen);
                        overlay.tick(e, tick);
                    }
                }
            }
        }
    }

    public static void triggerKeyPress() {
        if(Reference.onServer)
            availableModules.values().forEach(ModuleContainer::triggerKeyBinding);
    }

    public static SettingsContainer getSettings(Module module, SettingsHolder holder) {
        ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);
        if(info == null) {
            return null;
        }

        SettingsInfo info2 = holder.getClass().getAnnotation(SettingsInfo.class);
        if(info2 == null) {
            if(holder instanceof Overlay)
                return availableModules.get(info.name()).getRegisteredSettings().get("overlay" + ((Overlay) holder).displayName);
            else
                return null;
        }

        return availableModules.get(info.name()).getRegisteredSettings().get(info2.name());
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

}
