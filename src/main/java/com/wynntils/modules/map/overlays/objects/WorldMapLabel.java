/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.renderer.GlStateManager;

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

    private CustomColor getColorFromLayer() {
        CustomColor color;
        if (label.getLayer() == 1) {
            color = new CustomColor(1.0f, 0, 0);
        } else if (label.getLayer() == 2) {
            color = new CustomColor(1.0f, 1.0f, 0);
        } else {
            color = new CustomColor(1.0f, 1.0f, 1.0f);
        }
        return color;
    }

    private CustomColor getDimmedColorFromLayer(float alpha) {
        CustomColor color = getColorFromLayer();
        color.setA(0.75f * alpha);
        return color;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, float blockScale, ScreenRenderer renderer) {
        if (!shouldRender || renderer == null) return;

        renderer.drawString(label.getName(), axisX - label.getSizeX(), axisZ - label.getSizeZ(), getDimmedColorFromLayer(alpha));
    }

    public void drawHovering(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if (!shouldRender || !mouseOver(mouseX, mouseY)) return;

        String level = label.getLevel();
        if (level != null) {
            String lvStr = "[Lv. " + level + "]";
            renderer.drawString(lvStr,  (int) (axisX), (int) (axisZ) + 8, CommonColors.MAGENTA, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }
        renderer.drawString(label.getName(), axisX - label.getSizeX(), axisZ - label.getSizeZ(), getColorFromLayer()); }
}
