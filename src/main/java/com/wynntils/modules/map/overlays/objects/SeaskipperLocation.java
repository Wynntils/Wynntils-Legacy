package com.wynntils.modules.map.overlays.objects;

/*
 *  * Copyright © Wynntils - 2021.
 */


import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.objects.SquareRegion;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.renderer.MapInfoUI;
import com.wynntils.webapi.profiles.SeaskipperProfile;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeaskipperLocation {

    private static final CustomColor seaskipperNameColour = new CustomColor(CommonColors.WHITE);

    ScreenRenderer renderer = null;

    SeaskipperProfile location;
    Accessibility accessibility = Accessibility.INACCESSIBLE;

    float initX, initY, endX, endY;
    int cost;

    boolean shouldRender = false;

    MapInfoUI infoBox;

    public SeaskipperLocation(SeaskipperProfile location) {
        this.location = location;

        this.infoBox = new MapInfoUI(location.getName())
                .setDescription(Collections.singletonList(TextFormatting.RED + "Info box is not constructed yet"));
    }

    public SeaskipperLocation setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;
        infoBox.setRenderer(renderer);
        return this;
    }

    public void setActiveType(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void constructInfoBox() {
        List<String> description = new ArrayList<>();

        description.add(TextFormatting.GOLD + "Level " + location.getLevel());
        description.add(TextFormatting.YELLOW + "Starting Coordinates: " + location.getStartX() + ". " + location.getStartZ());
        description.add(TextFormatting.YELLOW + "Ending Coordinates: " + location.getEndX() + ". " + location.getEndZ());
        description.add(" ");

        switch (accessibility) {
            case ACCESSIBLE:
                description.add(TextFormatting.GREEN + "Cost: " + cost + EmeraldSymbols.EMERALDS);
                description.add(TextFormatting.BOLD + (TextFormatting.BLUE + "Click to go here!"));
                break;
            case INACCESSIBLE:
                description.add(TextFormatting.GRAY + "Inaccessible");
                break;
            case ORIGIN:
                description.add(TextFormatting.RED + "Origin");
        }

        infoBox.setDescription(description);
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ) {
        float initX = ((mp.getTextureXPosition(location.getStartX()) - minX) / (maxX - minX));
        float initY = ((mp.getTextureZPosition(location.getStartZ()) - minZ) / (maxZ - minZ));
        float endX = ((mp.getTextureXPosition(location.getEndX()) - minX) / (maxX - minX));
        float endY = ((mp.getTextureZPosition(location.getEndZ()) - minZ) / (maxZ - minZ));

        this.initX = initX * width; this.initY = initY * height;
        this.endX = endX * width; this.endY = endY  * height;

        shouldRender = ((initX > 0 && initX < 1) || (initY > 0 && initY < 1) || (endX > 0 && endX < 1) || (endY > 0 && endY < 1));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean showLocations, boolean showInaccessibleLocations) {
        if (!shouldRender || renderer == null) return;
        if (!showInaccessibleLocations && accessibility == Accessibility.INACCESSIBLE) return;

        CustomColor color = accessibility.getColor();

        if (showLocations) {
            renderer.drawRectF(color.setA(MapConfig.WorldMap.INSTANCE.colorAlpha), initX, initY, endX, endY);
            renderer.drawRectWBordersF(color.setA(1), initX, initY, endX, endY, 2f);
        }

        float ppX = getCenterX();
        float ppY = getCenterY();

        boolean hovering = isHovered(mouseX, mouseY);

        if (MapConfig.WorldMap.INSTANCE.showTerritoryName || hovering || isAccessible())
            renderer.drawString(location.getName(), ppX, ppY - 20, seaskipperNameColour, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    public void postDraw(int mouseX, int mouseY, float partialTicks, int width, int height, boolean showInaccessibleLocations) {
        if (!isHovered(mouseX, mouseY) || (!showInaccessibleLocations && accessibility == Accessibility.INACCESSIBLE)) return;

        infoBox.render((int)(width * 0.95), (int)(height * 0.1));
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
    }

    public float getCenterX() {
        return initX + ((endX - initX)/2f);
    }

    public float getCenterY() {
        return initY + ((endY - initY)/2f);
    }

    public SeaskipperProfile getLocation() {
        return location;
    }

    public boolean isAccessible() {
        return accessibility == Accessibility.ACCESSIBLE;
    }

    public SquareRegion getSquareRegion() {
        return new SquareRegion(location.getStartX(), location.getStartZ(), location.getEndX(), location.getEndZ());
    }

    public enum Accessibility {
        INACCESSIBLE(new CustomColor(CommonColors.LIGHT_GRAY)),
        ACCESSIBLE(new CustomColor(CommonColors.LIGHT_BLUE)),
        ORIGIN(new CustomColor(CommonColors.ORANGE));

        private final CustomColor color;

        Accessibility(CustomColor color) {
            this.color = color;
        }

        public CustomColor getColor() {
            return color;
        }
    }
}
