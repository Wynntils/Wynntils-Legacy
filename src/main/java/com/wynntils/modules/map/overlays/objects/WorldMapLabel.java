/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;

public class WorldMapLabel extends WorldMapIcon {

    MapLabel label;

    public WorldMapLabel(MapLabel label) {
        super(label);
        this.label = label;
    }

    protected void updateAlphaForZoom(int zoom) {
        if (label.getLayer() == 1) {
            alpha = ((zoom - 80) / 60.0f);
        } else if (label.getLayer() == 2) {
            alpha =  1 - ((zoom - 270) / 40.0f);
        } else {
            alpha =  1 - ((zoom - 110) / 40.0f);
        }

        if (alpha > 1.0) {
            alpha = 1.0f;
        }
    }

    private final CustomColor GOLD = new CustomColor(1f, 0.6f, 0f);
    private final CustomColor YELLOW = new CustomColor(1f, 1f, 0.3f);
    private final CustomColor WHITE = new CustomColor(1f, 1f, 1f);

    private CustomColor getColorFromLayer() {
        if (label.getLayer() == 1) {
            return GOLD;
        } else if (label.getLayer() == 2) {
           return YELLOW;
        }

        return WHITE;
    }

    private SmartFontRenderer.TextShadow getTextShadowFromLayer() {
        if (label.getLayer() == 1) {
            return SmartFontRenderer.TextShadow.OUTLINE;
        }

        return SmartFontRenderer.TextShadow.NORMAL;
    }

    private CustomColor getDimmedColorFromLayer(float alpha) {
        CustomColor color = getColorFromLayer();
        color.setA(0.75f * alpha);

        return color;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, float blockScale, ScreenRenderer renderer) {
        if (!shouldRender || renderer == null) return;

        renderer.drawString(label.getName(), axisX - label.getSizeX(), axisZ - label.getSizeZ(),
                getDimmedColorFromLayer(alpha), SmartFontRenderer.TextAlignment.LEFT_RIGHT, getTextShadowFromLayer());
    }

    public void drawHovering(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if (!shouldRender || !mouseOver(mouseX, mouseY)) return;

        String level = label.getLevel();
        if (level != null) {
            String lvStr = "[Lv. " + level + "]";
            renderer.drawString(lvStr,  (int) (axisX), (int) (axisZ) + 8, CommonColors.GRAY, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }

        renderer.drawString(label.getName(), axisX - label.getSizeX(), axisZ - label.getSizeZ(), getColorFromLayer());
    }

}
