package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Utils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RestartTimerOverlay extends Overlay {
    private static long startTime = -1;
    private static long countDownTime = -1;

    public RestartTimerOverlay() {
        super("Restart Timer", 60, 10, true, 0.5f, 0f, -30, 4, OverlayGrowFrom.TOP_LEFT);
    }

    public static void start(String time, String unit) {
        try {
            countDownTime = (unit.toLowerCase().contains("minute") ? Long.parseLong(time) * 60 : Long.parseLong(time)) * 1000;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();
    }

    public static void clear() {
        startTime = -1;
        countDownTime = -1;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (startTime == -1 || countDownTime == -1) return;

        drawString(Utils.millisToString(Math.max(0, countDownTime - (System.currentTimeMillis() - startTime))), 0, 0, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
    }
}
