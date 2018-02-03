package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.interfaces.HudOverlayBase;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class HudOverlay extends ScreenRenderer implements HudOverlayBase {

    public int x, y;

    private HashMap<String, Object> availableConfigs = new HashMap<>();

    public HudOverlay(Minecraft mc, int x, int y) {
        super(mc);

        this.x = x; this.y = y;
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

    @Override
    public HashMap<String, Object> getCurrentConfigs() {
        return availableConfigs;
    }

    public void setConfigValue(String name, Object value) {
        availableConfigs.put(name, value);
    }

    public Object getConfigValue(String name) {
        return availableConfigs.getOrDefault(name, null);
    }

}
