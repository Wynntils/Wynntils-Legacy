/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Utils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class StopWatchOverlay extends Overlay {

    private static long startTime = -1;
    private static long lastTime = -1;

    public static void start() {
        if(startTime == -1) {
            startTime = System.currentTimeMillis();
            return;
        }

        lastTime = System.currentTimeMillis() - startTime;
        startTime = -1;
    }

    public StopWatchOverlay() {
        super("Stop Watch", 60, 10, true, 0, 1f, 10, -15, OverlayGrowFrom.BOTTOM_LEFT, RenderGameOverlayEvent.ElementType.EXPERIENCE, RenderGameOverlayEvent.ElementType.JUMPBAR);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if(startTime == -1 && lastTime == -1) return;

        if(startTime != -1) {
            drawString(Utils.millisToString(System.currentTimeMillis() - startTime), 0, 0, CommonColors.ORANGE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
            return;
        }

        drawString(Utils.millisToString(lastTime), 0, 0, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
    }

}
