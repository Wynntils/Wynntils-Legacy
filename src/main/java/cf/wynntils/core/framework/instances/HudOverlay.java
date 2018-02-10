package cf.wynntils.core.framework.instances;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.configs.ConfigParser;
import cf.wynntils.core.framework.interfaces.HudOverlayBase;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.io.File;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class HudOverlay extends ScreenRenderer implements HudOverlayBase {

    public int x, y;

    private ConfigParser config;
    private HashMap<String, Object> defaultValues = new HashMap<>();

    public HudOverlay(String name, int x, int y) {
        super(Minecraft.getMinecraft());
        config = new ConfigParser(new File(Reference.MOD_STORAGE_ROOT, "overlays"), name + "Overlay", defaultValues);

        addDefaultConfigValue("x", (int)x);
        addDefaultConfigValue("y", (int)y);
    }

    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {

    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    public void loadConfig() {
        config.loadConfig();

        this.x = MathHelper.floor((double)config.getValue("x")); this.y = MathHelper.floor((double)config.getValue("y"));
    }

    public void setConfigValue(String name, Object value) {
        config.setValue(name, value);
    }

    public void addDefaultConfigValue(String name, Object value) {
        defaultValues.put(name, value);
    }

    public Object getConfigValue(String name) {
        return config.getValue(name);
    }
    public boolean getConfigBoolean(String name) { return (boolean)config.getValue(name) ;}
    public String getConfigString(String name) { return (String)config.getValue(name) ;}
    public int getConfigInt(String name) { return (int)config.getValue(name) ;}
    public <T> T getConfigInstance(String name) { return (T)config.getValue(name) ;}

}
