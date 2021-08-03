/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.enums.GuildResource;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Storage;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.GuildResourceManager;
import com.wynntils.modules.map.overlays.renderer.MapInfoUI;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class MapTerritory {

    private static final CustomColor territoryNameColour = new CustomColor(CommonColors.WHITE);

    ScreenRenderer renderer = null;

    float alpha = 1;
    TerritoryProfile territory;

    float initX, initY, endX, endY;

    boolean shouldRender = false;

    GuildResourceContainer resources;
    MapInfoUI infoBox;

    public MapTerritory(TerritoryProfile territory) {
        this.territory = territory;
        this.resources = GuildResourceManager.getResources(territory.getFriendlyName());

        List<String> description = new ArrayList<>();

        description.add(TextFormatting.LIGHT_PURPLE + territory.getGuild() + " [" + territory.getGuildPrefix() + "]");
        description.add(" ");

        for (GuildResource resource : GuildResource.values()) {
            int generation = resources.getGeneration(resource);
            if (generation != 0) {
                description.add(resource.getPrettySymbol() + "+" + generation + " " + resource.getName() + " per Hour");
            }

            Storage storage = resources.getStorage(resource);
            if (storage == null) continue;

            description.add(resource.getPrettySymbol() + storage.getCurrent() + "/" + storage.getMax() + " stored");
        }

        description.add("");
        description.add(TextFormatting.GRAY + "✦ Treasury: " + resources.getTreasury());
        description.add(TextFormatting.GRAY + "Territory Defences: " + resources.getDefences());

        if (resources.isHeadquarters()) {
            description.add(" ");
            description.add(TextFormatting.RED + "Guild Headquarters");
        }

        this.infoBox = new MapInfoUI(territory.getFriendlyName())
                .setDescription(description);
    }

    public MapTerritory setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;
        infoBox.setRenderer(renderer);
        return this;
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ, float zoom) {
        alpha = 1 - ((zoom - 10) / 40.0f);

        float initX = ((mp.getTextureXPosition(territory.getStartX()) - minX) / (maxX - minX));
        float initY = ((mp.getTextureZPosition(territory.getStartZ()) - minZ) / (maxZ - minZ));
        float endX = ((mp.getTextureXPosition(territory.getEndX()) - minX) / (maxX - minX));
        float endY = ((mp.getTextureZPosition(territory.getEndZ()) - minZ) / (maxZ - minZ));

        this.initX = initX * width; this.initY = initY * height;
        this.endX = endX * width; this.endY = endY  * height;

        shouldRender = (initX > 0 && initX < 1) || (initY > 0 && initY < 1) || (endX > 0 && endX < 1) || (endY > 0 && endY < 1);
    }

    public float getCenterX() {
        return initX + ((endX - initX)/2f);
    }

    public float getCenterY() {
        return initY + ((endY - initY)/2f);
    }

    public TerritoryProfile getTerritory() {
        return territory;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean territoryArea, boolean resourceColor, boolean showHeadquarters, boolean showNames, boolean useAlpha) {
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

        if (!useAlpha) alpha = 1;
        else if (!showNames || alpha <= 0) return;

        if ((MapConfig.WorldMap.INSTANCE.showTerritoryName || hovering))
            renderer.drawString(territory.getFriendlyName(), ppX, ppY - 10, territoryNameColour.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        renderer.drawString(MapConfig.WorldMap.INSTANCE.useGuildShortNames ? territory.getGuildPrefix() : territory.getGuild(), ppX, ppY, color.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    public void postDraw(int mouseX, int mouseY, float partialTicks, int width, int height) {
        boolean hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
        if (!hovering) return;

        infoBox.render((int)(width * 0.95), (int)(height * 0.1));
    }

}
