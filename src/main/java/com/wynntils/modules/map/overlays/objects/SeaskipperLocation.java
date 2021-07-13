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
import com.wynntils.webapi.profiles.SeaskipperProfile;

public class SeaskipperLocation {

    private static final CustomColor seaskipperNameColour = new CustomColor(CommonColors.WHITE);
    private static final CustomColor costColour = new CustomColor(CommonColors.LIGHT_GREEN);
    private static final CustomColor gotoColour = new CustomColor(CommonColors.CYAN);

    ScreenRenderer renderer = null;

    SeaskipperProfile location;
    Accessibility accessibility = Accessibility.INACCESSIBLE;

    float initX, initY, endX, endY;
    int cost;

    boolean shouldRender = false;

    public SeaskipperLocation(SeaskipperProfile location) {
        this.location = location;
    }

    public SeaskipperLocation setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;

        return this;
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

    public float getCenterX() {
        return initX + ((endX - initX)/2f);
    }

    public float getCenterY() {
        return initY + ((endY - initY)/2f);
    }

    public SeaskipperProfile getLocation() {
        return location;
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

        if (MapConfig.WorldMap.INSTANCE.showTerritoryName || isHovered(mouseX, mouseY) || isAccessible())
            renderer.drawString(location.getName(), ppX, ppY - 20, seaskipperNameColour, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        switch (accessibility) {
            case ACCESSIBLE:
                renderer.drawString(cost + " " + EmeraldSymbols.EMERALDS, ppX, ppY - 10, costColour, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                renderer.drawString("Click on to go to here!", ppX, ppY + 10, gotoColour, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                break;
            case INACCESSIBLE:
                renderer.drawString("Inaccessible!", ppX, ppY + 10, color, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                break;
            case ORIGIN:
                renderer.drawString("Origin", ppX, ppY, color, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }


    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
    }

    public void setActiveType(Accessibility accessibility) {
        this.accessibility = accessibility;
    }

    public boolean isAccessible() {
        return accessibility == Accessibility.ACCESSIBLE;
    }

    public SquareRegion getSquareRegion() {
        return new SquareRegion(initX, initY, endX, endY);
    }

    public void setCost(int cost) {
        this.cost = cost;
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
