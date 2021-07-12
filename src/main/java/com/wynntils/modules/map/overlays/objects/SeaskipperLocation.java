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
import net.minecraft.util.text.TextFormatting;

public class SeaskipperLocation {

    private static final CustomColor seaskipperNameColour = new CustomColor(CommonColors.WHITE);

    ScreenRenderer renderer = null;

    float alpha = 1;
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

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ, int zoom) {
        alpha = 1 - ((zoom - 10) / 40.0f);

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

    public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean showLocations, boolean showInacessableLocations) {
        if (!shouldRender || renderer == null) return;
        if (!showInacessableLocations && accessibility == Accessibility.INACCESSIBLE) return;

        CustomColor color = accessibility.getColor();

        if (showLocations) {
            renderer.drawRectF(color.setA(MapConfig.WorldMap.INSTANCE.colorAlpha), initX, initY, endX, endY);
            renderer.drawRectWBordersF(color.setA(1), initX, initY, endX, endY, 2f);
        }

        float ppX = getCenterX();
        float ppY = getCenterY();

        boolean hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);

        if ((MapConfig.WorldMap.INSTANCE.showTerritoryName || hovering) && alpha > 0)
            renderer.drawString(location.getName(), ppX, ppY - 30, seaskipperNameColour.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        if (MapConfig.WorldMap.INSTANCE.useGuildShortNames) alpha = 1;
        if (alpha <= 0) return;

        renderer.drawString(getHoveredText(), ppX, ppY, CommonColors.YELLOW.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        if (accessibility == Accessibility.ACCESSIBLE) renderer.drawString(cost + " " + EmeraldSymbols.EMERALDS, ppX, ppY - 20, CommonColors.GREEN.setA(alpha), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    public String getHoveredText() {
        if (accessibility != Accessibility.ACCESSIBLE) return TextFormatting.RED + "You can not go to this location!";

        return TextFormatting.YELLOW + "Click on to go to " + location.getName() + "!";
    }

    public void postDraw(int mouseX, int mouseY, float partialTicks, int width, int height) {
        boolean hovering = (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
        if (!hovering) return;


    }

    public boolean isHovered(int mouseX, int mouseY) {
        return (mouseX > initX && mouseX < endX && mouseY > initY && mouseY < endY);
    }

    public void setActiveType(Accessibility accessability) {
        this.accessibility = accessability;
    }

    public Accessibility getActiveType() {
        return accessibility;
    }

    public SquareRegion getSquareRegion() {
        return new SquareRegion(initX, initY, endX, endY);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public enum Accessibility {
        INACCESSIBLE(CommonColors.LIGHT_GRAY),
        ACCESSIBLE(CommonColors.LIGHT_BLUE),
        ORIGIN(CommonColors.ORANGE);

        private final CustomColor color;

        Accessibility(CustomColor color) {
            this.color = color;
        }

        public CustomColor getColor() {
            return color;
        }
    }

}
