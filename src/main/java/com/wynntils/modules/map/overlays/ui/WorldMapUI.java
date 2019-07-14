/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.GuiMovementScreen;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.objects.MapIcon;
import com.wynntils.modules.map.overlays.objects.MapTerritory;
import com.wynntils.modules.map.overlays.objects.WorldMapIcon;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorldMapUI extends GuiMovementScreen {

    private ScreenRenderer renderer = new ScreenRenderer();

    private GuiButton settingsBtn;
    private GuiButton waypointMenuBtn;
    private GuiButtonImage addWaypointBtn;

    private float centerPositionX;
    private float centerPositionZ;
    private int zoom = 0;

    private List<WorldMapIcon> apiMapIcons;
    private List<WorldMapIcon> wpMapIcons;
    private WorldMapIcon compassIcon;
    private List<MapTerritory> territories;

    boolean holdingMapKey = false;
    long creationTime;

    public WorldMapUI() {
        mc = Minecraft.getMinecraft();

        creationTime = System.currentTimeMillis();

        //HeyZeer0: Handles MiniMap markers provided by Wynn API
        apiMapIcons = MapIcon.getApiMarkers(MapConfig.INSTANCE.iconTexture)
                .stream()
                .map(WorldMapIcon::new)
                .collect(Collectors.toList());

        //HeyZeer0: Handles all waypoints
        wpMapIcons = MapIcon.getWaypoints().stream().map(WorldMapIcon::new).collect(Collectors.toList());

        compassIcon = new WorldMapIcon(MapIcon.getCompass());

        //HeyZeer0: Handles the territories
        territories = WebManager.getTerritories().values().stream().map(c -> new MapTerritory(c).setRenderer(renderer)).collect(Collectors.toList());

        updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
    }

    private void resetCompassMapIcon() {
        compassIcon.updateAxis(MapModule.getModule().getMainMap(), width, height, maxX, minX, maxZ, minZ, zoom);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(settingsBtn = new GuiButton(1,22,23,60,18, "Markers"));
        this.buttonList.add(waypointMenuBtn = new GuiButton(3, 22, 46, 60, 18, "Waypoints"));
        this.buttonList.add(addWaypointBtn = new GuiButtonImage(2,24,69,14,14,0,0, 0, Textures.Map.map_options.resourceLocation));

        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    float minX = 0; float maxX = 0;
    float minZ = 0; float maxZ = 0;

    private void forEachIcon(Consumer<WorldMapIcon> c) {
        apiMapIcons.forEach(c);
        wpMapIcons.forEach(c);
        if (CompassManager.getCompassLocation() != null)
            c.accept(compassIcon);
    }

    private void updateCenterPosition(float centerPositionX, float centerPositionZ) {
        this.centerPositionX = centerPositionX; this.centerPositionZ = centerPositionZ;

        MapProfile map = MapModule.getModule().getMainMap();

        minX = map.getTextureXPosition(centerPositionX) - ((width)/2.0f) - (width*zoom/100.0f); // <--- min texture x point
        minZ = map.getTextureZPosition(centerPositionZ) - ((height)/2.0f) - (height*zoom/100.0f); // <--- min texture z point

        maxX = map.getTextureXPosition(centerPositionX) + ((width)/2.0f) + (width*zoom/100.0f); // <--- max texture x point
        maxZ = map.getTextureZPosition(centerPositionZ) + ((height)/2.0f) + (height*zoom/100.0f); // <--- max texture z point

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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!Reference.onWorld || !MapModule.getModule().getMainMap().isReadyToUse()) return;

        //HeyZeer0: This detects if the user is holding the map key;
        if(!holdingMapKey && (System.currentTimeMillis() - creationTime >= 150) && Keyboard.isKeyDown(MapModule.getModule().getMapKey().getKeyBinding().getKeyCode())) holdingMapKey = true;

        //HeyZeer0: This close the map if the user was pressing the map key and after a moment dropped it
        if(holdingMapKey && !Keyboard.isKeyDown(MapModule.getModule().getMapKey().getKeyBinding().getKeyCode())) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }

        //draging
        if(clicking && !settingsBtn.isMouseOver()) {
            float acceleration = (1f + zoom/100f); //<---- this is basically 1.0~10 || Min = 1.0 Max = 2.0
            updateCenterPosition(centerPositionX += (lastMouseX - mouseX) * acceleration, centerPositionZ += (lastMouseY - mouseY) * acceleration);
        }
        lastMouseX = mouseX; lastMouseY = mouseY;

        //start rendering
        ScreenRenderer.beginGL(0, 0);

        //texture
        renderer.drawRect(CommonColors.BLACK, 19, 19, width - 19, height - 19);
        renderer.drawRectF(Textures.Map.full_map, 10, 10, width-10, height-10, 1, 1, 511, 255);
        renderer.createMask(Textures.Map.full_map, 10, 10, width-10, height-10, 1, 257, 511, 510);

        MapProfile map = MapModule.getModule().getMainMap();
        minX /= (float)map.getImageWidth(); maxX /= (float)map.getImageWidth();
        minZ /= (float)map.getImageHeight(); maxZ /= (float)map.getImageHeight();

        try{
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1, 1f);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            map.bindTexture(); // <--- binds the texture
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
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

        }catch (Exception ignored) {}

        //draw map icons
        forEachIcon(c -> c.drawScreen(mouseX, mouseY, partialTicks, renderer));

        minX = minX*map.getImageWidth(); maxX = maxX*map.getImageWidth();
        minZ = minZ*map.getImageHeight(); maxZ = maxZ*map.getImageHeight();
        float playerPostionX = (map.getTextureXPosition(mc.player.posX) - minX) / (maxX - minX);
        float playerPostionZ = (map.getTextureZPosition(mc.player.posZ) - minZ) / (maxZ - minZ);

        if(playerPostionX > 0 && playerPostionX < 1 && playerPostionZ > 0 && playerPostionZ < 1) { // <--- player position
            playerPostionX = width * playerPostionX;
            playerPostionZ = height * playerPostionZ;

            ScreenRenderer.transformationOrigin((int)playerPostionX, (int)playerPostionZ);
            ScreenRenderer.rotate(180 + MathHelper.fastFloor(mc.player.rotationYaw));

            MapConfig.PointerType type = MapConfig.Textures.INSTANCE.pointerStyle;

            MapConfig.Textures.INSTANCE.pointerColor.applyColor();
            renderer.drawRectF(Textures.Map.map_pointers, playerPostionX - type.dWidth*1.5f, playerPostionZ - type.dHeight*1.5f, playerPostionX + type.dWidth*1.5f, playerPostionZ + type.dHeight*1.5f, 0, type.yStart, type.width, type.yStart + type.height);
            GlStateManager.color(1, 1, 1, 1);

            ScreenRenderer.resetRotation();
        }

        if(MapConfig.WorldMap.INSTANCE.keepTerritoryVisible || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            territories.forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks));
        }

        forEachIcon(c -> c.drawHovering(mouseX, mouseY, partialTicks, renderer));
        renderer.clearMask();

        // Render coordinate mouse is over
        int worldX = getMouseWorldX(mouseX, map);
        int worldZ = getMouseWorldZ(mouseY, map);

        renderer.drawString(worldX + ", " + worldZ, width / 2, height - 20, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    boolean clicking = false;

    private void zoomBy(int by) {
        zoom = Math.max(MapConfig.WorldMap.INSTANCE.minZoom, Math.min(MapConfig.WorldMap.INSTANCE.maxZoom, zoom - by));
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    @Override
    public void handleMouseInput() throws IOException {
        clicking = Mouse.isButtonDown(0);

        int mDwehll = Mouse.getEventDWheel();
        if(mDwehll >= 1) {
            zoomBy(+5);
        }else if(mDwehll <= -1) {
            zoomBy(-5);
        }

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (settingsBtn.isMouseOver() || addWaypointBtn.isMouseOver() || waypointMenuBtn.isMouseOver()) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        } else if (mouseButton == 1) {
            updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
            return;
        } else if (mouseButton == 2) {
            // Set compass to middle clicked location
            MapProfile map = MapModule.getModule().getMainMap();
            int worldX = getMouseWorldX(mouseX, map);
            int worldZ = getMouseWorldZ(mouseY, map);
            CompassManager.setCompassLocation(new Location(worldX, 0, worldZ));

            resetCompassMapIcon();
            return;
        }

        if (mouseButton == 0) {
            Consumer<WorldMapIcon> consumer = c -> {
                if (c.mouseOver(mouseX, mouseY)) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));

                    CompassManager.setCompassLocation(new Location(c.getInfo().getPosX(), 0, c.getInfo().getPosZ()));
                    resetCompassMapIcon();
                }
            };

            apiMapIcons.forEach(consumer);
            wpMapIcons.forEach(consumer);
            // Don't try to set compass to the current compass icon
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!holdingMapKey && keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }

        if (keyCode == KeyManager.getZoomInKey().getKeyBinding().getKeyCode()) {
            zoomBy(+20);
            return;  // Return early so it doesn't zoom the minimap
        } else if (keyCode == KeyManager.getZoomOutKey().getKeyBinding().getKeyCode()) {
            zoomBy(-20);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn == settingsBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapSettingsUI());
        } else if (btn == addWaypointBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(null));
        } else if (btn == waypointMenuBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointOverviewUI());
        }
    }

}
