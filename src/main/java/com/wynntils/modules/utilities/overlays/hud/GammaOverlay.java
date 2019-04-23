/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class GammaOverlay extends Overlay {

    public GammaOverlay() {
        super("Gamma",0,11,true,1.0f,0.0f,0,0, OverlayGrowFrom.MIDDLE_CENTRE);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        if (ScreenRenderer.mc.gameSettings.gammaSetting >= 1000) {
            drawString("GammaBright", 0, 0, CommonColors.ORANGE, SmartFontRenderer.TextAlignment.RIGHT_LEFT, OverlayConfig.INSTANCE.textShadow);
        }
    }
}
