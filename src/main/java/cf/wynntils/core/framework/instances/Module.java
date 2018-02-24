package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.overlays.Overlay;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Module {

    private Logger logger;

    public void onEnable() {

    }

    public void postInit() {

    }

    public void onDisable() {

    }

    public boolean isActive() {
        return true;
    }

    public void registerEvents(Listener selectedListener) {
        FrameworkManager.registerEvents(this, selectedListener);
    }

    public void registerOverlay(Overlay overlay, Priority priority) {
        FrameworkManager.registerOverlay(overlay, priority);
    }

    public void registerKeyBinding(String name, int key, String tab, boolean press, Runnable onPress) {
        FrameworkManager.registerKeyBinding(this, new KeyHolder(name, key, tab, press, onPress));
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
