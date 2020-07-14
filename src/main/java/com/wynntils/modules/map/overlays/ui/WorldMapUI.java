/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.overlays.ui;

import com.google.common.collect.Iterables;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.GuiMovementScreen;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.objects.*;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorldMapUI extends GuiMovementScreen {

    protected ScreenRenderer renderer = new ScreenRenderer();

    protected float centerPositionX = Float.NaN;
    protected float centerPositionZ = Float.NaN;
    // Zoom goes from 300 (whole world) to -10 (max details)
    protected int zoom = 0;

    protected List<WorldMapIcon> icons = new ArrayList<>();
    protected WorldMapIcon compassIcon;
    protected List<MapTerritory> territories;

    protected WorldMapUI() {
        this((float) Minecraft.getMinecraft().player.posX, (float) Minecraft.getMinecraft().player.posZ);
    }

    protected WorldMapUI(float startX, float startZ) {
        mc = Minecraft.getMinecraft();

        // HeyZeer0: Handles the territories
        territories = WebManager.getTerritories().values().stream().map(c -> new MapTerritory(c).setRenderer(renderer)).collect(Collectors.toList());

        // Also creates icons
        updateCenterPosition(startX, startZ);

        if (MapConfig.INSTANCE.hideCompletedQuests) {
            // Request analyse if not already done to
            // hide completed quests
            QuestManager.readQuestBook();
        }
    }

    protected void createIcons() {
        // HeyZeer0: Handles MiniMap markers provided by Wynn API
        List<MapIcon> apiMapIcons = MapIcon.getApiMarkers(MapConfig.INSTANCE.iconTexture);
        // Handles map labels from map.wynncraft.com
        List<MapIcon> mapLabels = MapIcon.getLabels();
        // HeyZeer0: Handles all waypoints
        List<MapIcon> wpMapIcons = MapIcon.getWaypoints();
        List<MapIcon> pathWpMapIcons = MapIcon.getPathWaypoints();
        // Handles guild / party / friends
        List<MapIcon> friendsIcons = MapIcon.getPlayers();
        // Handles compass
        compassIcon = new WorldMapIcon(MapIcon.getCompass());

        icons.clear();

        for (MapIcon i : Iterables.concat(
            apiMapIcons,
            wpMapIcons,
            pathWpMapIcons,
            mapLabels,
            friendsIcons
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

    float minX = 0; float maxX = 0;
    float minZ = 0; float maxZ = 0;

    protected void forEachIcon(Consumer<WorldMapIcon> c) {
        icons.forEach(c);
        if (CompassManager.getCompassLocation() != null)
            c.accept(compassIcon);
    }

    protected void updateCenterPositionWithPlayerPosition() {
        float newX = (float) mc.player.posX;
        float newZ = (float) mc.player.posZ;
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
        territories.forEach(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
    }

    int lastMouseX = -Integer.MAX_VALUE;
    int lastMouseY = -Integer.MAX_VALUE;

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

    protected float getScaleFactor(int zoom) {
        // How many blocks in one pixel
        // TODO this needs to scale in even numbers to avoid distortion!
        return 1f / (1f + zoom / 50f);
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

        // texture
        renderer.drawRectF(Textures.Map.full_map, 10, 10, width - 10, height - 10, 1, 1, 511, 255);
        createMask();
        renderer.drawRect(CommonColors.BLACK, 10, 10, width - 10, height - 10);

        MapProfile map = MapModule.getModule().getMainMap();
        float minX = this.minX / (float)map.getImageWidth(); float maxX = this.maxX / (float)map.getImageWidth();
        float minZ = this.minZ / (float)map.getImageHeight(); float maxZ = this.maxZ / (float)map.getImageHeight();

        try {
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1, 1f);
            GlStateManager.enableTexture2D();

            map.bindTexture();  // <--- binds the texture
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);

            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

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
        GlStateManager.enableBlend();
        forEachIcon(i -> {
            if (i.getInfo().hasDynamicLocation()) resetIcon(i);
            if (!i.getInfo().isEnabled(false)) {
                needToReset[0] = true;
                return;
            }
            i.drawScreen(mouseX, mouseY, partialTicks, scale, renderer);
        });

        if (needToReset[0]) resetAllIcons();

        float playerPositionX = (map.getTextureXPosition(mc.player.posX) - minX) / (maxX - minX);
        float playerPositionZ = (map.getTextureZPosition(mc.player.posZ) - minZ) / (maxZ - minZ);

        if (playerPositionX > 0 && playerPositionX < 1 && playerPositionZ > 0 && playerPositionZ < 1) {  // <--- player position
            playerPositionX = width * playerPositionX;
            playerPositionZ = height * playerPositionZ;

            Point drawingOrigin = ScreenRenderer.drawingOrigin();

            GlStateManager.pushMatrix();
            GlStateManager.translate(drawingOrigin.x + playerPositionX, drawingOrigin.y + playerPositionZ, 0);
            GlStateManager.rotate(180 + MathHelper.fastFloor(mc.player.rotationYaw), 0, 0, 1);
            GlStateManager.translate(-drawingOrigin.x - playerPositionX, -drawingOrigin.y - playerPositionZ, 0);

            MapConfig.PointerType type = MapConfig.Textures.INSTANCE.pointerStyle;

            MapConfig.Textures.INSTANCE.pointerColor.applyColor();
            GlStateManager.enableAlpha();
            renderer.drawRectF(Textures.Map.map_pointers, playerPositionX - type.dWidth * 1.5f, playerPositionZ - type.dHeight * 1.5f, playerPositionX + type.dWidth * 1.5f, playerPositionZ + type.dHeight * 1.5f, 0, type.yStart, type.width, type.yStart + type.height);
            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.popMatrix();
        }

        if (MapConfig.WorldMap.INSTANCE.keepTerritoryVisible || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            territories.forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks));
        }

        forEachIcon(c -> c.drawHovering(mouseX, mouseY, partialTicks, renderer));

        clearMask();
    }

    protected void drawCoordinates(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        // Render coordinate mouse is over
        int worldX = getMouseWorldX(mouseX, map);
        int worldZ = getMouseWorldZ(mouseY, map);

        renderer.drawString(worldX + ", " + worldZ, width / 2.0f, height - 20, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    private static final int MAX_ZOOM = 300;  // Note that this is the most zoomed out
    private static final int MIN_ZOOM = -10;  // And this is the most zoomed in
    private static final float ZOOM_SCALE_FACTOR = 1.1f;

    private void zoomBy(int by) {
        double zoomScale = Math.pow(ZOOM_SCALE_FACTOR, -by);
        zoom = MathHelper.clamp((int) Math.round(zoomScale * (zoom + 50) - 50), MIN_ZOOM, MAX_ZOOM);
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    protected boolean[] clicking = new boolean[]{ false, false };

    @Override
    public void handleMouseInput() throws IOException {
        clicking[0] = Mouse.isButtonDown(0);
        clicking[1] = Mouse.isButtonDown(1);

        int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
        if (mDWheel > 0) {
            zoomBy(+1);
        } else if (mDWheel < 0) {
            zoomBy(-1);
        }

        super.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == KeyManager.getZoomInKey().getKeyBinding().getKeyCode()) {
            zoomBy(+2);
        } else if (keyCode == KeyManager.getZoomOutKey().getKeyBinding().getKeyCode()) {
            zoomBy(-2);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

}
