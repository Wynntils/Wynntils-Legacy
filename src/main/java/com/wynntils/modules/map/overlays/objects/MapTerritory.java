/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.GuildResourceManager;
import com.wynntils.modules.map.overlays.renderer.TerritoryInfoUI;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.renderer.GlStateManager;

public class MapTerritory {

    private static final CustomColor territoryNameColour = new CustomColor(CommonColors.WHITE);

    ScreenRenderer renderer = null;

    float alpha = 1;
    TerritoryProfile territory;

    float initX, initY, endX, endY;

    boolean shouldRender = false;

    GuildResourceContainer resources;
    TerritoryInfoUI infoBox;

    public MapTerritory(TerritoryProfile territory) {
        this.territory = territory;
        this.resources = GuildResourceManager.getResources(territory.getFriendlyName());
        this.infoBox = new TerritoryInfoUI(territory, resources);
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

        if ((initX > 0 && initX < 1) || (initY > 0 && initY < 1) || (endX > 0 && endX < 1) || (endY > 0 && endY < 1)) {
            shouldRender = true;

            initX*=width; initY*=height;
            endX*=width; endY*=height;

            this.initX = initX; this.initY = initY;
            this.endX = endX; this.endY = endY;
            return;
        }

        shouldRender = false;
    }

    public float getCenterX() {
        return initX + ((endX - initX)/2f);
    }

    public float getCenterY() {
        return initY + ((endY - initY)/2f);
    }

    public boolean isRendering() {
        return shouldRender;
    }

    public TerritoryProfile getTerritory() {
        return territory;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean territoryArea, boolean resourceColor, boolean showHeadquarters, boolean showNames) {
        if (!shouldRender || renderer == null) return;

        CustomColor color;
        if (!resourceColor) {
            color = territory.getGuildColor() == null ? StringUtils.colorFromString(territory.getGuild()) :
                    StringUtils.colorFromHex(territory.getGuildColor());
        } else {
            color = resources.getColor();
        }

        if (territoryArea) {
            renderer.drawRectF(color.setA(MapConfig.WorldMap.INSTANCE.colorAlpha), initX, initY, endX, endY);
            renderer.drawRectWBordersF(color.setA(1), initX, initY, endX, endY, 2f);
        }

        float ppX = getCenterX();
        float ppY = getCenterY();

        boolean hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);

        if (showHeadquarters) {
            if (!resources.isHeadquarters()) return;

            GlStateManager.color(1f, 1f, 1f, 1f);
            renderer.drawRect(Textures.Map.map_territory_info, (int) ppX-8, (int) ppY-7, (int) ppX+8, (int) ppY+7, 0, 49, 16, 62);
        }

        if (!showNames) return;

        if ((MapConfig.WorldMap.INSTANCE.showTerritoryName || hovering) && alpha > 0)
            renderer.drawString(territory.getFriendlyName(), ppX, ppY - 10, territoryNameColour.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        if (MapConfig.WorldMap.INSTANCE.useGuildShortNames) alpha = 1;
        if (alpha <= 0) return;

        renderer.drawString(MapConfig.WorldMap.INSTANCE.useGuildShortNames ? territory.getGuildPrefix() : territory.getGuild(), ppX, ppY, color.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    public void postDraw(int mouseX, int mouseY, float partialTicks, int width, int height) {
        boolean hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
        if (!hovering) return;

        infoBox.render((int)(width * 0.95), (int)(height * 0.1));
    }

}
