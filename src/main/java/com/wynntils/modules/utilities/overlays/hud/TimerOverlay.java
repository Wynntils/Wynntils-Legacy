package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.LongPress;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class TimerOverlay extends Overlay {
    private TimerOverlay(String displayName, float anchorX, float anchorY, int offsetX, int offsetY, RenderGameOverlayEvent.ElementType... overrideElements) {
        super(displayName, 60, 10, true, anchorX, anchorY, offsetX, offsetY, OverlayGrowFrom.TOP_LEFT, overrideElements);
    }

    public static class StopWatch extends TimerOverlay {
        private static long startTime = -1;
        private static long lastTime = -1;

        private static final LongPress longPressDetection = new LongPress(2000, StopWatch::clear);

        public StopWatch() {
            super("Stop Watch", 0, 1f, 10, -15, RenderGameOverlayEvent.ElementType.EXPERIENCE, RenderGameOverlayEvent.ElementType.JUMPBAR);
        }

        public static void clear() {
            startTime = -1;
            lastTime = -1;
        }

        public static void start() {
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
                return;
            }

            lastTime = System.currentTimeMillis() - startTime;
            startTime = -1;
        }

        @Override
        public void render(RenderGameOverlayEvent.Pre event) {
            if (startTime == -1 && lastTime == -1) return;

            longPressDetection.tick(KeyManager.getStopwatchKey().getKeyBinding().isKeyDown());

            if (startTime != -1) {
                drawString(Utils.millisToString(System.currentTimeMillis() - startTime), 0, 0, CommonColors.ORANGE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
                return;
            }

            drawString(Utils.millisToString(lastTime), 0, 0, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
        }
    }

    public static class Restart extends TimerOverlay {
        private static long startTime = -1;
        private static long countDownTime = -1;

        public Restart() {
            super("Restart Timer", 0.5f, 0f, -30, 4);
        }

        public static void clear() {
            startTime = -1;
            countDownTime = -1;
        }

        public static void start(String time, String unit) {
            try {
                countDownTime = (unit.toLowerCase().contains("minute") ? Long.parseLong(time) * 60 : Long.parseLong(time)) * 1000;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            startTime = System.currentTimeMillis();
        }

        @Override
        public void render(RenderGameOverlayEvent.Pre event) {
            if (startTime == -1 || countDownTime == -1) return;

            drawString(Utils.millisToString(Math.max(0, countDownTime - (System.currentTimeMillis() - startTime))), 0, 0, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
        }
    }
}
