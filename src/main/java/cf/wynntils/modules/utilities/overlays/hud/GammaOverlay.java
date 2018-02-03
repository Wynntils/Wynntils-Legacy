package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.instances.HudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class GammaOverlay extends HudOverlay {

    public GammaOverlay(Minecraft mc, int x, int y) {
        super(mc, x, y);
    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        ScaledResolution resolution = new ScaledResolution(mc);

        if (mc.gameSettings.gammaSetting >= 1000) {
            drawString("§6GammaBright", resolution.getScaledWidth() - x, y, -1);
        }
    }
}
