package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.enums.EventPriority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ModuleContainer {

    ModuleInfo info;
    Module module;

    HashMap<EventPriority, ArrayList<ListenerContainer>> registeredEvents = new HashMap<>();
    ArrayList<HudOverlay> hudOverlays = new ArrayList<>();
    ArrayList<KeyHolder> keyHolders = new ArrayList<>();

    public ModuleContainer(ModuleInfo info, Module module) {
        this.info = info; this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public ModuleInfo getInfo() {
        return info;
    }

    public void registerKeyBinding(KeyHolder holder) {
        keyHolders.add(holder);
    }

    public void triggerKeyBinding() {
        if(!getModule().isActive()) {
            return;
        }
        if(keyHolders.size() <= 0) {
            return;
        }
        keyHolders.forEach(k -> {
            if(k.press && k.keyBinding.isPressed()) {
                k.getOnAction().run();
            }else if(!k.press && k.keyBinding.isKeyDown()) {
                k.getOnAction().run();
            }
        });
    }

    public void registerHudOverlay(HudOverlay ho) {
        hudOverlays.add(ho);
    }

    public void triggerPreHud(RenderGameOverlayEvent.Pre e) {
        if(!getModule().isActive()) {
            return;
        }
        hudOverlays.forEach(c -> {
            if(c.isActive()) {
                ScreenRenderer.beginGL(c.x,c.y);
                c.preRender(e);
                ScreenRenderer.endGL();
            }
        });
    }

    public void triggerPostHud(RenderGameOverlayEvent.Post e) {
        if(!getModule().isActive()) {
            return;
        }
        hudOverlays.forEach(c -> {
            if(c.isActive()) {
                ScreenRenderer.beginGL(c.x,c.y);
                c.postRender(e);
                ScreenRenderer.endGL();
            }
        });
    }

    public void registerEvents(Listener sClass) {
        for(Method m : sClass.getClass().getMethods()) {
            EventHandler eh = m.getAnnotation(EventHandler.class);
            if(eh == null) {
                continue;
            }

            if(registeredEvents.containsKey(eh.priority())) {
                registeredEvents.get(eh.priority()).add(new ListenerContainer(sClass, m));
            }else{
                ArrayList<ListenerContainer> list = new ArrayList<>();
                list.add(new ListenerContainer(sClass, m));
                registeredEvents.put(eh.priority(), list);
            }
        }
    }

    public void triggerEvent(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(EventPriority.HIGHEST)) {
            callEvent(e, EventPriority.HIGHEST);
        }
        if(registeredEvents.containsKey(EventPriority.HIGH)) {
            callEvent(e, EventPriority.HIGH);
        }
        if(registeredEvents.containsKey(EventPriority.NORMAL)) {
            callEvent(e, EventPriority.NORMAL);
        }
        if(registeredEvents.containsKey(EventPriority.LOW)) {
            callEvent(e, EventPriority.LOW);
        }
        if(registeredEvents.containsKey(EventPriority.LOWEST)) {
            callEvent(e, EventPriority.LOWEST);
        }
    }

    private void callEvent(Event e, EventPriority priority) {
        for(ListenerContainer container : registeredEvents.get(priority)) {
            Method m = container.m;
            if(m.getParameterCount() <= 0 || m.getParameterCount() > 1) {
                continue;
            }

            if(m.getParameterTypes()[0].isAssignableFrom(e.getClass())) {
                try{
                    m.invoke(container.instance, e);
                }catch (Exception ex) { ex.printStackTrace(); }
            }else{

            }
        }
    }

    class ListenerContainer {

        public Listener instance; public Method m;

        public ListenerContainer(Listener instance, Method m) {
            this.instance = instance; this.m = m;
        }

    }

}
