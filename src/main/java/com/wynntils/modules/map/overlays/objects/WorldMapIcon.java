/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.renderer.GlStateManager;

public class WorldMapIcon {

    MapIcon info;

    float axisX = 0; float axisZ = 0;
    boolean shouldRender = false;

    float alpha = 1;

    public WorldMapIcon(MapIcon info) {
        this.info = info;
    }

    public MapIcon getInfo() {
        return info;
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ, int zoom) {
        if (!info.isEnabled()) {
            shouldRender = false;
            return;
        }
        if(info.getZoomNeeded() != MapIcon.ANY_ZOOM) {
            alpha = 1 - ((zoom - info.getZoomNeeded()) / 40.0f);

            if(alpha <= 0) {
                shouldRender = false;
                return;
            }
        }

        float axisX = ((mp.getTextureXPosition(info.getPosX()) - minX) / (maxX - minX));
        float axisZ = ((mp.getTextureZPosition(info.getPosZ()) - minZ) / (maxZ - minZ));

        if(axisX > 0 && axisX < 1 && axisZ > 0 && axisZ < 1) {
            shouldRender = true;
            axisX = width * axisX;
            axisZ = height * axisZ;

            this.axisX = axisX; this.axisZ = axisZ;
        }else shouldRender = false;
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        float sizeX = info.getSizeX();
        float sizeZ = info.getSizeZ();
        return mouseX >= (axisX - sizeX) && mouseX <= (axisX + sizeX) && mouseY >= (axisZ - sizeZ) && mouseY <= (axisZ + sizeZ);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if(!shouldRender || renderer == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, alpha);
        float multi = mouseOver(mouseX, mouseY) ? 1.3f : 1f;

        info.renderAt(renderer, axisX, axisZ, multi);

        GlStateManager.color(1,1,1,1);

        GlStateManager.popMatrix();
    }

    public void drawHovering(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if(!shouldRender || !mouseOver(mouseX, mouseY)) return;

        GlStateManager.color(1, 1, 1, 1);
        renderer.drawString(info.getName(), (int)(axisX), (int)(axisZ)-20, CommonColors.MAGENTA, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }
}
