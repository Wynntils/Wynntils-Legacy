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

    MapIconInfo info;

    float axisX = 0; float axisZ = 0;
    boolean shouldRender = false;

    float alpha = 1;

    public WorldMapIcon(MapIconInfo info) {
        this.info = info;
    }

    public MapIconInfo getInfo() {
        return info;
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ, int zoom) {
        if(info.zoomNeeded != -1000) {
            alpha = 1 - ((zoom - info.zoomNeeded) / 40.0f);

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
        return mouseX >= (axisX - info.sizeX) && mouseX <= (axisX + info.sizeX) && mouseY >= (axisZ - info.sizeZ) && mouseY <= (axisZ + info.sizeZ);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if(!shouldRender || renderer == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, alpha);
        float multi = mouseOver(mouseX, mouseY) ? 1.3f : 1f;
        renderer.drawRectF(info.texture, axisX - info.sizeX * multi, axisZ - info.sizeZ * multi, axisX + info.sizeX * multi, axisZ + info.sizeZ * multi, info.texPosX, info.texPosZ, info.texSizeX, info.texSizeZ);
        GlStateManager.color(1,1,1,1);

        GlStateManager.popMatrix();
    }

    public void drawHovering(int mouseX, int mouseY, float partialTicks, ScreenRenderer renderer) {
        if(!shouldRender || !mouseOver(mouseX, mouseY)) return;

        GlStateManager.color(1, 1, 1, 1);
        renderer.drawString(info.name, (int)(axisX), (int)(axisZ)-20, CommonColors.MAGENTA, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }
}
