/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.InfoFormatter;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Pattern;

public abstract class InfoOverlay extends Overlay {
    private static final CustomColor backgroundColour = new CustomColor(CommonColors.BLACK);
    private static final Pattern colourRegex = Pattern.compile("§[^kKlLmMnNoOrR]");
    private static final InfoFormatter formatter = UtilitiesModule.getModule().getInfoFormatter();
    
    private InfoOverlay(int index) {
        super("Info " + index, 100, 9, true, 0, 0, 10, 105 + index * 11, OverlayGrowFrom.TOP_LEFT);
    }

    public abstract int getIndex();
    public abstract String getFormat();

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (!Reference.onWorld || e.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        String format = getFormat();
        if (format != null && !format.isEmpty()) {
            String formatted = formatter.doFormat(format);
            if (!formatted.isEmpty()) {
                float center = staticSize.x / 2f;
                String[] lines = formatted.split("\n");
                int nLines = lines.length;
                int[] lineWidths = new int[nLines];
                for (int i = 0; i < nLines; ++i) {
                    lineWidths[i] = mc.fontRenderer.getStringWidth(lines[i]);
                }

                if (OverlayConfig.InfoOverlays.INSTANCE.opacity != 0) {
                    int height = 11 * nLines;

                    int width = 0;
                    for (int currentWidth : lineWidths) {
                        if (currentWidth > width) {
                            width = currentWidth;
                        }
                    }

                    drawRect(backgroundColour, (int) (center - width / 2f - 1.5f), 0, (int) (center + width / 2f + 1.5f), height - 2);
                }

                center += drawingOrigin().x;
                int y = -10 + drawingOrigin().y;
                switch (OverlayConfig.InfoOverlays.INSTANCE.textShadow) {
                    // This switch could have been inside the for loop,
                    // but since this is run up to 4 times every frame, it has been hoisted
                    case OUTLINE:
                        for (int i = 0; i < nLines; ++i) {
                            int x = (int) (center - lineWidths[i] / 2f);
                            y += 11;

                            // Render outline
                            String withoutColours = colourRegex.matcher(lines[i]).replaceAll("§r");
                            mc.fontRenderer.drawString(withoutColours, x - 1, y, 0xFF000000, false);
                            mc.fontRenderer.drawString(withoutColours, x + 1, y, 0xFF000000, false);
                            mc.fontRenderer.drawString(withoutColours, x, y - 1, 0xFF000000, false);
                            mc.fontRenderer.drawString(withoutColours, x, y + 1, 0xFF000000, false);

                            mc.fontRenderer.drawString(lines[i], x, y, 0xFFFFFFFF, false);
                        }
                        break;

                    case NORMAL:
                        for (int i = 0; i < nLines; ++i) {
                            int x = (int) (center - lineWidths[i] / 2f);
                            y += 11;

                            mc.fontRenderer.drawString(lines[i], x, y, 0xFFFFFFFF, true);
                        }
                        break;

                    default:
                        for (int i = 0; i < nLines; ++i) {
                            int x = (int) (center - lineWidths[i] / 2f);
                            y += 11;

                            mc.fontRenderer.drawString(lines[i], x, y, 0xFFFFFFFF, false);
                        }
                        break;
                }
            }
        }
    }
    

    public static class _1 extends InfoOverlay {
        public _1() { 
            super(1);   
        }
        @Override public final int getIndex() { return 1; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info1Format; }
        @Override public void render(RenderGameOverlayEvent.Pre e) {
            backgroundColour.setA(OverlayConfig.InfoOverlays.INSTANCE.opacity / 100f);
            super.render(e);
        }
    }

    public static class _2 extends InfoOverlay {
        public _2() { super(2); }
        @Override public final int getIndex() { return 2; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info2Format; }
    }

    public static class _3 extends InfoOverlay {
        public _3() { super(3); }
        @Override public final int getIndex() { return 3; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info3Format; }
    }

    public static class _4 extends InfoOverlay {
        public _4() { super(4); }
        @Override public final int getIndex() { return 4; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info4Format; }
    }

}
