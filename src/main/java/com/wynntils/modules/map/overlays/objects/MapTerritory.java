/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.webapi.profiles.TerritoryProfile;

public class MapTerritory {

    ScreenRenderer renderer = null;

    float alpha = 1;
    TerritoryProfile territory;

    float initX, initY, endX, endY;

    boolean shouldRender = false;

    public MapTerritory(TerritoryProfile territory) {
        this.territory = territory;
    }

    public MapTerritory setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ, int zoom) {
        alpha = 1 - ((zoom - 10) / 40.0f);

        float initX = ((mp.getTextureXPosition(territory.getStartX()) - minX) / (maxX - minX));
        float initY = ((mp.getTextureZPosition(territory.getStartZ()) - minZ) / (maxZ - minZ));
        float endX = ((mp.getTextureXPosition(territory.getEndX()) - minX) / (maxX - minX));
        float endY = ((mp.getTextureZPosition(territory.getEndZ()) - minZ) / (maxZ - minZ));

        if((initX > 0 && initX < 1) || (initY > 0 && initY < 1) || (endX > 0 && endX < 1) || (endY > 0 && endY < 1)) {
            shouldRender = true;

            initX*=width; initY*=height;
            endX*=width; endY*=height;

            this.initX = initX; this.initY = initY;
            this.endX = endX; this.endY = endY;
            return;
        }

        shouldRender = false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!shouldRender || renderer == null) return;

        CustomColor color = territory.getGuildColor() == null ? Utils.colorFromString(territory.getGuild()) : Utils.colorFromHex(territory.getGuildColor());

        if(MapConfig.WorldMap.INSTANCE.territoryArea) {
            renderer.drawRectF(color.setA(MapConfig.WorldMap.INSTANCE.colorAlpha), initX, initY, endX, endY);
            renderer.drawRectWBordersF(color.setA(1), initX, initY, endX, endY, 2f);
        }

        float ppX = initX + ((endX - initX)/2f);
        float ppY = initY + ((endY - initY)/2f);

        boolean Hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);

        if((MapConfig.WorldMap.INSTANCE.showTerritoryName || Hovering) && alpha > 0)
            renderer.drawString(territory.getFriendlyName(), ppX, ppY - 10, CommonColors.WHITE.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        if(MapConfig.WorldMap.INSTANCE.useGuildShortNames) alpha = 1;
        if(alpha <= 0) return;

        renderer.drawString(MapConfig.WorldMap.INSTANCE.useGuildShortNames ? territory.getGuildPrefix() : territory.getGuild(), ppX, ppY, color.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

}
