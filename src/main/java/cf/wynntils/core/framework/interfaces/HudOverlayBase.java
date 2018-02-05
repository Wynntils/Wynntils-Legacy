package cf.wynntils.core.framework.interfaces;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public interface HudOverlayBase {

    void preRender(RenderGameOverlayEvent.Pre e);
    void postRender(RenderGameOverlayEvent.Post e);
    boolean isActive();

}
