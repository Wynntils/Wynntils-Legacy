/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.google.common.collect.Iterables;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.GuiMovementScreen;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import com.wynntils.modules.map.overlays.objects.*;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.client.renderer.GlStateManager.*;

public class WorldMapUI extends GuiMovementScreen {

    private static final int MAX_ZOOM = 300;  // Note that this is the most zoomed out
    private static final int MIN_ZOOM = -10;  // And this is the most zoomed in
    private static final float ZOOM_SCALE_FACTOR = 1.1f;
    private static final long ZOOM_RESISTENCE = 100; // The zoom resistence in ms (any change takes 200ms)

    protected ScreenRenderer renderer = new ScreenRenderer();

    // Position Related
    protected float centerPositionX = Float.NaN;
    protected float centerPositionZ = Float.NaN;

    // Zoom
    protected float zoom = 0;  // Zoom goes from 300 (whole world) to -10 (max details)
    protected float zoomInitial = 0;
    protected float zoomTarget = 0;
    protected float zoomEnd = 0;

    // Properties
    float minX = 0; float maxX = 0;
    float minZ = 0; float maxZ = 0;
    int lastMouseX = -Integer.MAX_VALUE;
    int lastMouseY = -Integer.MAX_VALUE;
    protected boolean[] clicking = new boolean[]{ false, false };

    // Data Objects
    protected List<WorldMapIcon> icons = new ArrayList<>();
    protected WorldMapIcon compassIcon;
    protected HashMap<String, MapTerritory> territories = new HashMap<>();
    protected List<MapButton> mapButtons = new ArrayList<>();

    // Open Animation Stuff
    protected long animationEnd;

    // Outside the map text
    protected final CustomColor OUTSIDE_MAP_COLOR_1 = new CustomColor(1f, 1f, 1f);
    protected final CustomColor OUTSIDE_MAP_COLOR_2 = new CustomColor(.5f, .5f, .5f);
    protected boolean outsideMap = false;
    protected float outsideTextOpacity = 0f;

    protected WorldMapUI() {
        this((float) McIf.player().posX, (float) McIf.player().posZ);
    }

    protected WorldMapUI(float startX, float startZ) {
        // HeyZeer0: Handles the territories
        for (TerritoryProfile territory : WebManager.getTerritories().values()) {
            territories.put(territory.getFriendlyName(), new MapTerritory(territory).setRenderer(renderer));
        }

        // Also creates icons
        updateCenterPosition(startX, startZ);

        this.animationEnd = System.currentTimeMillis() + MapConfig.WorldMap.INSTANCE.animationLength;

        // Opening SFX
        McIf.mc().getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1f)
        );
    }

    protected void addButton(MapButtonType type, int offsetX, List<String> hover, Function<Void, Boolean> isEnabled, BiConsumer<MapButton, Integer> onClick) {
        // add the buttom base
        if (mapButtons.isEmpty()) {
            mapButtons.add(new MapButton(width / 2, height - 45, MapButtonType.BASE, null, (v) -> true, null));
        }

        int posX = mapButtons.get(0).getStartX() + 13 + (19 * (mapButtons.size() - 1)) + offsetX;
        int posY = height - 45;
        mapButtons.add(new MapButton(posX, posY, type, hover, isEnabled, onClick));
    }

    protected void createIcons() {
        // HeyZeer0: Handles MiniMap markers provided by Wynn API
        List<MapIcon> apiMapIcons = MapIcon.getApiMarkers(MapConfig.INSTANCE.iconTexture);
        // Handles map labels from map.wynncraft.com
        List<MapIcon> mapLabels = MapIcon.getLabels();
        // Handles all waypoints
        List<MapIcon> wpMapIcons = MapIcon.getWaypoints();
        List<MapIcon> pathWpMapIcons = MapIcon.getPathWaypoints();
        // Handles guild / party / friends
        List<MapIcon> friendsIcons = MapIcon.getPlayers();
        // Handles lootrun path waypoints
        List<MapIcon> lootrunWpMapIcons = LootRunManager.getMapPathWaypoints();
        // Handles compass
        compassIcon = new WorldMapIcon(MapIcon.getCompass());

        icons.clear();

        for (MapIcon i : Iterables.concat(
            apiMapIcons,
            wpMapIcons,
            pathWpMapIcons,
            mapLabels,
            friendsIcons,
            lootrunWpMapIcons
        )) {
            if (i.isEnabled(false)) {
                WorldMapIcon icon;
                if (i instanceof MapLabel) {
                    icon = new WorldMapLabel((MapLabel) i);
                } else {
                    icon = new WorldMapIcon(i);
                }

                icons.add(icon);
            }
        }
    }

    public void resetAllIcons() {
        MapProfile map = MapModule.getModule().getMainMap();
        createIcons();
        forEachIcon(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
    }

    protected void resetIcon(WorldMapIcon icon) {
        icon.updateAxis(MapModule.getModule().getMainMap(), width, height, maxX, minX, maxZ, minZ, zoom);
    }

    protected void resetCompassMapIcon() {
        resetIcon(compassIcon);
    }

    protected void forEachIcon(Consumer<WorldMapIcon> c) {
        icons.forEach(c);
        if (CompassManager.getCompassLocation() != null)
            c.accept(compassIcon);
    }

    protected void updateCenterPositionWithPlayerPosition() {
        float newX = (float) McIf.player().posX;
        float newZ = (float) McIf.player().posZ;
        if (newX == centerPositionX && newZ == centerPositionZ) return;
        updateCenterPosition(newX, newZ);
    }

    protected void updateCenterPosition(float centerPositionX, float centerPositionZ) {
        this.centerPositionX = centerPositionX; this.centerPositionZ = centerPositionZ;

        MapProfile map = MapModule.getModule().getMainMap();

        minX = map.getTextureXPosition(centerPositionX) - ((width)/2.0f) - (width*zoom/100.0f);  // <--- min texture x point
        minZ = map.getTextureZPosition(centerPositionZ) - ((height)/2.0f) - (height*zoom/100.0f);  // <--- min texture z point

        maxX = map.getTextureXPosition(centerPositionX) + ((width)/2.0f) + (width*zoom/100.0f);  // <--- max texture x point
        maxZ = map.getTextureZPosition(centerPositionZ) + ((height)/2.0f) + (height*zoom/100.0f);  // <--- max texture z point

        createIcons();

        forEachIcon(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
        territories.values().forEach(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
    }

    // Returns the x coordinate in the world from the x coordinate on the screen
    public int getMouseWorldX(int mouseScreenX, MapProfile map) {
        return map.getWorldXPosition(((float) mouseScreenX / width) * (maxX - minX) + minX);
    }

    // Returns the z coordinate in the world from the y coordinate on the screen
    public int getMouseWorldZ(int mouseScreenY, MapProfile map) {
        return map.getWorldZPosition(((float) mouseScreenY / height) * (maxZ - minZ) + minZ);
    }

    protected void createMask() {
        ScreenRenderer.createMask(Textures.Map.full_map, 10, 10, width - 10, height - 10, 1, 257, 511, 510);
    }

    protected void clearMask() {
        ScreenRenderer.clearMask();
    }

    protected void updatePosition(int mouseX, int mouseY) {
        updatePosition(mouseX, mouseY, clicking[0]);
    }

    protected float getScaleFactor() {
        return getScaleFactor(zoom);
    }

    protected float getScaleFactor(float zoom) {
        // How many blocks in one pixel
        return 50f / (zoom + 50f);
    }

    protected void updatePosition(int mouseX, int mouseY, boolean canMove) {
        // dragging
        if (canMove && lastMouseX != -Integer.MAX_VALUE) {
            float acceleration = 1f / getScaleFactor();  // <---- this is basically 1.0~10 || Min = 1.0 Max = 2.0
            updateCenterPosition(centerPositionX + (lastMouseX - mouseX) * acceleration, centerPositionZ + (lastMouseY - mouseY) * acceleration);
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    protected void drawMap(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld || !MapModule.getModule().getMainMap().isReadyToUse()) return;

        handleOpenAnimation();
        handleZoomAcceleration(partialTicks);

        // texture
        renderer.drawRectF(Textures.Map.full_map, 10, 10, width - 10, height - 10, 1, 1, 511, 255);
        createMask();
        renderer.drawRect(CommonColors.BLACK, 10, 10, width - 10, height - 10);

        MapProfile map = MapModule.getModule().getMainMap();
        float minX = this.minX / (float)map.getImageWidth(); float maxX = this.maxX / (float)map.getImageWidth();
        float minZ = this.minZ / (float)map.getImageHeight(); float maxZ = this.maxZ / (float)map.getImageHeight();

        outsideMap = (minX > 1) || (maxX < 0) || (minZ > 1) || (maxZ < 0);

        try {
            enableAlpha();
            color(1, 1, 1, 1f);
            enableTexture2D();

            map.bindTexture();  // <--- binds the texture
            glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);

            glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder bufferbuilder = tessellator.getBuffer();
            {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

                bufferbuilder.pos(0, height, 0).tex(minX, maxZ).endVertex();
                bufferbuilder.pos(width, height, 0).tex(maxX, maxZ).endVertex();
                bufferbuilder.pos(width, 0, 0).tex(maxX, minZ).endVertex();
                bufferbuilder.pos(0, 0, 0).tex(minX, minZ).endVertex();
                tessellator.draw();
            }

        } catch (Exception ignored) {}

        clearMask();
    }

    protected void drawIcons(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        createMask();

        float scale = getScaleFactor();
        // draw map icons
        boolean[] needToReset = { false };
        enableBlend();
        forEachIcon(i -> {
            if (i.getInfo().hasDynamicLocation()) resetIcon(i);
            if (!i.getInfo().isEnabled(false)) {
                needToReset[0] = true;
                return;
            }
            i.drawScreen(mouseX, mouseY, partialTicks, scale, renderer);
        });

        if (needToReset[0]) resetAllIcons();

        float playerPositionX = (map.getTextureXPosition(McIf.player().posX) - minX) / (maxX - minX);
        float playerPositionZ = (map.getTextureZPosition(McIf.player().posZ) - minZ) / (maxZ - minZ);

        if (playerPositionX > 0 && playerPositionX < 1 && playerPositionZ > 0 && playerPositionZ < 1) {  // <--- player position
            playerPositionX = width * playerPositionX;
            playerPositionZ = height * playerPositionZ;

            Point drawingOrigin = ScreenRenderer.drawingOrigin();

            pushMatrix();
            translate(drawingOrigin.x + playerPositionX, drawingOrigin.y + playerPositionZ, 0);
            rotate(180 + MathHelper.fastFloor(McIf.player().rotationYaw), 0, 0, 1);
            translate(-drawingOrigin.x - playerPositionX, -drawingOrigin.y - playerPositionZ, 0);

            MapConfig.PointerType type = MapConfig.Textures.INSTANCE.pointerStyle;

            MapConfig.Textures.INSTANCE.pointerColor.applyColor();
            enableAlpha();
            renderer.drawRectF(Textures.Map.map_pointers, playerPositionX - type.dWidth * 1.5f, playerPositionZ - type.dHeight * 1.5f, playerPositionX + type.dWidth * 1.5f, playerPositionZ + type.dHeight * 1.5f, 0, type.yStart, type.width, type.yStart + type.height);
            color(1, 1, 1, 1);

            popMatrix();
        }

        if (MapConfig.WorldMap.INSTANCE.keepTerritoryVisible || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            territories.values().forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks,
                    MapConfig.WorldMap.INSTANCE.territoryArea, false, false, true));
        }

        forEachIcon(c -> c.drawHovering(mouseX, mouseY, partialTicks, renderer));
        handleOutsideAreaText();

        clearMask();
    }

    protected void drawCoordinates(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        // Render coordinate mouse is over
        int worldX = getMouseWorldX(mouseX, map);
        int worldZ = getMouseWorldZ(mouseY, map);

        renderer.drawString(worldX + ", " + worldZ, width / 2.0f, height - 70, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    protected void drawMapButtons(int mouseX, int mouseY, float partialTicks) {
        if (mapButtons.isEmpty()) return;

        for (MapButton button : mapButtons) {
            button.drawScreen(mouseX, mouseY, partialTicks);
        }

        // hover lore
        for (MapButton button : mapButtons) {
            if (button.getType().isIgnoreAction()) continue;
            if (!button.isHovering(mouseX, mouseY)) continue;

            drawHoveringText(button.getHoverLore(), mouseX, mouseY);
            return;
        }
    }

    protected void handleOpenAnimation() {
        if (System.currentTimeMillis() > animationEnd || !MapConfig.WorldMap.INSTANCE.openAnimation) return;

        float invertedProgress = (animationEnd - System.currentTimeMillis()) / (float) MapConfig.WorldMap.INSTANCE.animationLength;
        double radians = (Math.PI / 2f) * invertedProgress;

        zoom = (float) (25 * Math.sin(radians));
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    protected void handleZoomAcceleration(float partialTicks) {
        if (McIf.getSystemTime() > zoomEnd) return;

        float percentage = Math.min(1f, 1f - (zoomEnd - McIf.getSystemTime()) / ZOOM_RESISTENCE);
        double toIncrease = (zoomTarget - zoomInitial) * Math.sin((Math.PI / 2f) * percentage);

        zoom = zoomInitial + (float) toIncrease;
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    protected void handleOutsideAreaText() {
        if (outsideTextOpacity > 0f && (clicking[0] || !outsideMap)) {
            outsideTextOpacity -= 0.1f;
        } else if (outsideTextOpacity < 1f && outsideMap) {
            outsideTextOpacity += 0.1f;
        }

        if (outsideTextOpacity <= 0.0f) return;

        OUTSIDE_MAP_COLOR_1.setA(outsideTextOpacity);
        OUTSIDE_MAP_COLOR_2.setA(outsideTextOpacity);

        pushMatrix();
        {
            translate(width / 2, height / 2, 0);
            scale(2, 2, 2);
            renderer.drawString("You're outside the main map area", 0, 0, OUTSIDE_MAP_COLOR_1,
                    SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL
            );
            scale(0.5, 0.5, 0.5);
            renderer.drawString("Don't worry, you'll come back soon", 0, 20, OUTSIDE_MAP_COLOR_2,
                    SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL
            );
        }
        popMatrix();
    }

    private void zoomBy(float by) {
        double zoomScale = Math.pow(ZOOM_SCALE_FACTOR, -by);
        zoom = (float) MathHelper.clamp(zoomScale * (zoom + 50) - 50, MIN_ZOOM, MAX_ZOOM);
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    private void accelerateZoomBy(float by) {
        double zoomScale = Math.pow(ZOOM_SCALE_FACTOR, -by);
        zoomTarget = (float) MathHelper.clamp(zoomScale * (zoom + 50) - 50, MIN_ZOOM, MAX_ZOOM);

        zoomEnd = McIf.getSystemTime() + ZOOM_RESISTENCE;
        zoomInitial = zoom;
    }

    @Override
    public void initGui() {
        super.initGui();

        updateCenterPosition(centerPositionX, centerPositionZ);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        clicking[0] = Mouse.isButtonDown(0);
        clicking[1] = Mouse.isButtonDown(1);

        // allow scroll only after animation ended
        if (System.currentTimeMillis() > animationEnd) {
            int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

            // Zoom faster if we are really far
            float zoomAmount = 2f + (4f * (zoom / MAX_ZOOM));

            if (mDWheel > 0) {
                accelerateZoomBy(zoomAmount);
            } else if (mDWheel < 0) {
                accelerateZoomBy(-zoomAmount);
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == KeyManager.getZoomInKey().getKeyBinding().getKeyCode()) {
            accelerateZoomBy(4f);
            return;
        }

        if (keyCode == KeyManager.getZoomOutKey().getKeyBinding().getKeyCode()) {
            accelerateZoomBy(-4f);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mapButtons.isEmpty()) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        for (MapButton button : mapButtons) {
            if (!button.isHovering(mouseX, mouseY)) continue;
            if (button.getType().isIgnoreAction()) continue;

            button.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
