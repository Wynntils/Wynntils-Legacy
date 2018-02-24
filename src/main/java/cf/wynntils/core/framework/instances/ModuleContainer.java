package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynndevs.ModCore;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ModuleContainer {

    ModuleInfo info;
    Module module;

    HashMap<Priority, ArrayList<ListenerContainer>> registeredEvents = new HashMap<>();
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

    public void triggerEventHighest(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(Priority.HIGHEST)) {
            callEvent(e, Priority.HIGHEST);
        }
    }

    public void triggerEventHigh(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(Priority.HIGH)) {
            callEvent(e, Priority.HIGH);
        }
    }

    public void triggerEventNormal(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(Priority.NORMAL)) {
            callEvent(e, Priority.NORMAL);
        }
    }

    public void triggerEventLow(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(Priority.LOW)) {
            callEvent(e, Priority.LOW);
        }
    }

    public void triggerEventLowest(Event e) {
        if(!module.isActive()) {
            return;
        }
        if(registeredEvents.containsKey(Priority.LOWEST)) {
            callEvent(e, Priority.LOWEST);
        }
    }

    private void callEvent(Event e, Priority priority) {
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
