/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

public abstract class Module {

    private Logger logger;

    public abstract void onEnable();

    public void postEnable() {

    }

    public void onDisable() {

    }

    public boolean isActive() {
        return true;
    }

    public PlayerInfo getPlayerInfo() {
        return PlayerInfo.getPlayerInfo();
    }

    public void registerEvents(Listener listenerClass) {
        FrameworkManager.registerEvents(this, listenerClass);
    }

    public void registerSettings(Class<? extends SettingsHolder> settingsClass) {
        FrameworkManager.registerSettings(this, settingsClass);
    }

    public void registerOverlay(Overlay overlay, Priority priority) {
        FrameworkManager.registerOverlay(this, overlay, priority);
    }

    public KeyHolder registerKeyBinding(String name, int key, String tab, boolean press, Runnable onPress) {
        return FrameworkManager.registerKeyBinding(this, new KeyHolder(name, key, tab, press, onPress));
    }

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
