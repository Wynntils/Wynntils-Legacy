/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer.TextAlignment;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.InfoFormatter;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class InfoOverlay extends Overlay {

    private static final InfoFormatter formatter = UtilitiesModule.getModule().getInfoFormatter();

    private InfoOverlay(int index) {
        super("Info " + index, 100, 9, true, 0, 0, 10, 105 + index * 11, OverlayGrowFrom.TOP_LEFT);
    }

    public abstract int getIndex();
    public abstract String getFormat();
    public abstract TextAlignment getAlignment();

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (!Reference.onWorld || e.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        String format = getFormat();
        if (format == null || format.isEmpty()) return;

        String formatted = formatter.doFormat(format);
        if (formatted.isEmpty()) return;

        String[] lines = formatted.split("\n");

        // Determine width of largest string
        int width = 0;
        for (String line : lines) {
            if (ScreenRenderer.fontRenderer.getStringWidth(line) > width) {
                width = ScreenRenderer.fontRenderer.getStringWidth(line);
            }
        }

        // Determine element locations
        int backgroundLeft;
        int backgroundRight;
        int textOrigin;
        switch (getAlignment()) {
            case LEFT_RIGHT:
                backgroundLeft = 0;
                backgroundRight = width;
                textOrigin = 0;
                break;
            case MIDDLE:
                float center = staticSize.x / 2f;
                backgroundLeft = (int) (center - width / 2);
                backgroundRight = (int) (center + width / 2);
                textOrigin = (int) center;
                break;
            default:
            case RIGHT_LEFT:
                backgroundLeft = staticSize.x - width;
                backgroundRight = staticSize.x;
                textOrigin = staticSize.x;
                break;
        }

        // Draw Background
        if (OverlayConfig.InfoOverlays.INSTANCE.opacity != 0) {
            drawRect(OverlayConfig.InfoOverlays.INSTANCE.backgroundColor, backgroundLeft - 2, -2, backgroundRight + 2, 11 * lines.length - 2);
        }

        // Draw Text
        for (int i = 0; i < lines.length; i++) {
            drawString(lines[i], textOrigin, i*11, CommonColors.WHITE, getAlignment(), OverlayConfig.InfoOverlays.INSTANCE.textShadow);
        }
    }

    public static class _1 extends InfoOverlay {
        public _1() { super(1); }
        @Override public final int getIndex() { return 1; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info1Format; }
        @Override public final TextAlignment getAlignment() { return OverlayConfig.InfoOverlays.INSTANCE.info1Alignment; }
    }

    public static class _2 extends InfoOverlay {
        public _2() { super(2); }
        @Override public final int getIndex() { return 2; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info2Format; }
        @Override public final TextAlignment getAlignment() { return OverlayConfig.InfoOverlays.INSTANCE.info2Alignment; }
    }

    public static class _3 extends InfoOverlay {
        public _3() { super(3); }
        @Override public final int getIndex() { return 3; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info3Format; }
        @Override public final TextAlignment getAlignment() { return OverlayConfig.InfoOverlays.INSTANCE.info3Alignment; }
    }

    public static class _4 extends InfoOverlay {
        public _4() { super(4); }
        @Override public final int getIndex() { return 4; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info4Format; }
        @Override public final TextAlignment getAlignment() { return OverlayConfig.InfoOverlays.INSTANCE.info4Alignment; }
    }

}
