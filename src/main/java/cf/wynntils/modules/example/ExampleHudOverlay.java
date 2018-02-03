package cf.wynntils.modules.example;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.EventPriority;
import cf.wynntils.core.framework.instances.HudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import sun.security.tools.keytool.Main;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ExampleHudOverlay extends HudOverlay {

    /**
     * Default constructor
     *
     * @param mc Minecraft
     * @param x Default X
     * @param y Default Y
     */
    public ExampleHudOverlay(Minecraft mc, int x, int y) {
        super(mc, x, y);

        setConfigValue("Example", true); //true is the default value
    }

    /**
     * Post render event
     *
     * @param e event
     */
    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }

        setConfigValue("Example", true);

        if(Boolean.valueOf(getConfigValue("Example").toString())) {
            drawString("This is an example", x, y, -1);
        }else{
            drawString("This is a test", x, y, -1);
        }
    }

    /**
     * Pre render event
     *
     * @param e event
     */
    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {
        if(e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            //e.setCanceled(true);
        }
    }

    /**
     * If the Hud Overlay is active
     *
     * @return if the overlay is active
     */
    @Override
    public boolean isActive() {
        return true;
    }
}
