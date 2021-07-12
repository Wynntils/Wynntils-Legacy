/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.objects.SeaskipperLocation;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.SeaskipperProfile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.sql.Ref;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.text.TextFormatting.*;

public class SeaskipperWorldMapUI extends WorldMapUI {

    private final static Pattern SEASKIPPER_PASS_MATCHER = Pattern.compile("^([\\w ]+) Pass for (\\d+)");

    SeaskipperLocation origin;
    private final HashMap<String, SeaskipperLocation> locations = new HashMap<>();
    private final HashMap<SeaskipperLocation, Integer> routes = new HashMap<>();
    private SeaskipperLocation hoveredLocation;

    private boolean receivedItems = false;
    ChestReplacer chest;

    // Properties
    private boolean showLocations = true;
    private boolean showInacessableLocations = true;
    private boolean showSeaskipperRoutes = true;


    public SeaskipperWorldMapUI(ChestReplacer chest) {
        super((float) McIf.player().posX, (float) McIf.player().posZ);

        this.chest = chest;

        for (SeaskipperProfile profile : WebManager.getSeaskipperLocations()) {
            locations.put(profile.getName(), new SeaskipperLocation(profile).setRenderer(renderer));
        }
    }

    @Override
    protected void updateCenterPosition(float centerPositionX, float centerPositionZ) {
        super.updateCenterPosition(centerPositionX, centerPositionZ);

        locations.values().forEach(c -> c.updateAxis(MapModule.getModule().getMainMap(), width, height, maxX, minX, maxZ, minZ, zoom));
    }

    @Override
    public void initGui() {
        super.initGui();

        this.mapButtons.clear();

        addButton(MapButtonType.CENTER, 0, Arrays.asList(
                AQUA + "[>] Show territory borders",
                GRAY + "Click here to enable/disable",
                GRAY + "territory borders."
        ), (v) -> showLocations, (i, btn) -> showLocations = !showLocations);

        addButton(MapButtonType.PLUS, 0, Arrays.asList(
                AQUA + "[>] Show inaccessible locations",
                GRAY + "Click here to enable/disable",
                GRAY + "inaccessible locations."
        ), (v) -> showInacessableLocations, (i, btn) -> showInacessableLocations = !showInacessableLocations);

        addButton(MapButtonType.PIN, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Show Seaskipper routes",
                GRAY + "Click here to enable/disable",
                GRAY + "Seaskipper routes."
        ), (v) -> showSeaskipperRoutes, (i, btn) -> showSeaskipperRoutes = !showSeaskipperRoutes);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!Mouse.isButtonDown(1)) {
            updatePosition(mouseX, mouseY);
        }

        updateItems();

        // start rendering
        ScreenRenderer.beginGL(0, 0);

        drawMap(mouseX, mouseY, partialTicks);
        drawIcons(mouseX, mouseY, partialTicks);
        drawCoordinates(mouseX, mouseY, partialTicks);
        drawMapButtons(mouseX, mouseY, partialTicks);

        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void updateItems() {
        if (receivedItems) return;

        for (Slot s : chest.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();

            if (stack.isEmpty() || !stack.hasDisplayName()) continue;

            if (stack.getItem() == Items.NAME_TAG) receivedItems = true;
            else return;

            String displayname = getTextWithoutFormattingCodes(stack.getDisplayName());

            if (displayname != null) {
                Matcher result = SEASKIPPER_PASS_MATCHER.matcher(displayname);
                if (!result.matches()) {
                    Reference.LOGGER.info(displayname);
                }

                SeaskipperLocation location = locations.get(result.group(1));
                int cost = Integer.parseInt(result.group(2));
                location.setCost(cost);
                location.setActiveType(SeaskipperLocation.Accessibility.ACCESSIBLE);
                routes.put(location, s.slotNumber);
            }
        }

        // resets the animation timer
        if (!receivedItems) return;

        animationEnd = System.currentTimeMillis() + 300;

        //Get origin based on box, make sure seaskipper area is in region
        double playerX = McIf.player().posX;
        double playerY = McIf.player().posY;

        //Distance based way to find origin if that fails
        double sqdist = Double.MAX_VALUE;
        SeaskipperLocation closest = null;

        for (SeaskipperLocation location : locations.values()) {
            if (routes.containsKey(location)) return;

            if (location.getSquareRegion().isInside(playerX, playerY)) {
                origin = location;
                location.setActiveType(SeaskipperLocation.Accessibility.ORIGIN);
                return;
            }

            double sqLocationToPlayer = (location.getCenterX() - playerX) * (location.getCenterX() - playerX) + (location.getCenterY() - playerY) * (location.getCenterY() - playerY);

            if (sqLocationToPlayer < sqdist) {
                sqdist = sqLocationToPlayer;
                closest = location;
            }
        }

        origin = closest;
        origin.setActiveType(SeaskipperLocation.Accessibility.ORIGIN);
    }


    protected void drawIcons(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        createMask();

        float scale = getScaleFactor();

        float playerPositionX = (map.getTextureXPosition(McIf.player().posX) - minX) / (maxX - minX);
        float playerPositionZ = (map.getTextureZPosition(McIf.player().posZ) - minZ) / (maxZ - minZ);

        if (playerPositionX > 0 && playerPositionX < 1 && playerPositionZ > 0 && playerPositionZ < 1) {  // <--- player position
            playerPositionX = width * playerPositionX;
            playerPositionZ = height * playerPositionZ;

            Point drawingOrigin = ScreenRenderer.drawingOrigin();

            GlStateManager.pushMatrix();
            GlStateManager.translate(drawingOrigin.x + playerPositionX, drawingOrigin.y + playerPositionZ, 0);
            GlStateManager.rotate(180 + MathHelper.fastFloor(McIf.player().rotationYaw), 0, 0, 1);
            GlStateManager.translate(-drawingOrigin.x - playerPositionX, -drawingOrigin.y - playerPositionZ, 0);

            MapConfig.PointerType type = MapConfig.Textures.INSTANCE.pointerStyle;

            MapConfig.Textures.INSTANCE.pointerColor.applyColor();
            GlStateManager.enableAlpha();
            renderer.drawRectF(Textures.Map.map_pointers, playerPositionX - type.dWidth * 1.5f, playerPositionZ - type.dHeight * 1.5f, playerPositionX + type.dWidth * 1.5f, playerPositionZ + type.dHeight * 1.5f, 0, type.yStart, type.width, type.yStart + type.height);
            GlStateManager.color(1, 1, 1, 1);

            GlStateManager.popMatrix();
        }

        if (showSeaskipperRoutes) generateSeaSkipperRoutes();
        locations.values().forEach((c) -> c.drawScreen(mouseX, mouseY, partialTicks, showLocations, showInacessableLocations));
        locations.values().forEach(c -> c.postDraw(mouseX, mouseY, partialTicks, width, height));

        SeaskipperLocation hovered = locations.values().stream().filter(c -> c.isHovered(mouseX, mouseY)).findFirst().orElse(null);

        hoveredLocation = null;
        if (hovered != null && hovered.getActiveType() == SeaskipperLocation.Accessibility.ACCESSIBLE) {
                hoveredLocation = hovered;
        }

        clearMask();
    }

    protected void generateSeaSkipperRoutes() {
        for (SeaskipperLocation location : routes.keySet()) {
            drawSeaskipperRoute(origin, location);
        }
    }

    protected void drawSeaskipperRoute(SeaskipperLocation origin, SeaskipperLocation destination) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();

            // outline
            GlStateManager.glLineWidth(4f);
            {
                buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(destination.getCenterX(), destination.getCenterY(), 0).color(0f, 0f, 0f, .5f).endVertex();
                buffer.pos(origin.getCenterX(), origin.getCenterY(), 0).color(0f, 0f, 0f, .5f).endVertex();
            }
            tess.draw();

            // line
            GlStateManager.glLineWidth(1.5f);
            {
                buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                buffer.pos(destination.getCenterX(), destination.getCenterY(), 0).color(0f, 249, 255f, .5f).endVertex();
                buffer.pos(origin.getCenterX(), origin.getCenterY(), 0).color(0f, 249, 255f, .5f).endVertex();
            }
            tess.draw();

            GlStateManager.enableTexture2D();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (hoveredLocation != null) chest.handleMouseClick(chest.inventorySlots.getSlot(routes.get(hoveredLocation)), routes.get(hoveredLocation), 0, ClickType.PICKUP);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
