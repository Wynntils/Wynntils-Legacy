/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.instances.containers;

import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.core.framework.settings.SettingsContainer;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ModuleContainer {

    ModuleInfo info;
    Module module;

    ArrayList<KeyHolder> keyHolders = new ArrayList<>();
    HashMap<String, SettingsContainer> registeredSettings = new HashMap<>();
    HashSet<Object> registeredEvents = new HashSet<>();

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
            if(k.isPress() && k.getKeyBinding().isPressed()) {
                k.getOnAction().run();
            }else if(!k.isPress() && k.getKeyBinding().isKeyDown()) {
                k.getOnAction().run();
            }
        });
    }

    public void registerEvents(Object sClass) {
        FrameworkManager.getEventBus().register(sClass);
        registeredEvents.add(sClass);
    }

    public void unregisterAllEvents() {
        registeredEvents.forEach(FrameworkManager.getEventBus()::unregister);
        registeredEvents.clear();
    }

    public void registerSettings(Class<? extends SettingsHolder> holder) {
        SettingsInfo info = holder.getAnnotation(SettingsInfo.class);
        if(info == null) {
            return;
        }

        for(Field field : holder.getDeclaredFields()){
            if(field.getType() == holder && Modifier.isStatic(field.getModifiers())) {
                try {
                    field.set(null, holder.getConstructor().newInstance());
                    registeredSettings.put(info.name(), new SettingsContainer(this, (SettingsHolder) field.get(null)));
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void registerSettings(String name, SettingsHolder sh) {
        registeredSettings.put(name, new SettingsContainer(this, sh));
    }

    public void reloadSettings() {
        registeredSettings.values().forEach(c -> {
            try { c.tryToLoad();
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    public HashMap<String, SettingsContainer> getRegisteredSettings() {
        return registeredSettings;
    }

}
