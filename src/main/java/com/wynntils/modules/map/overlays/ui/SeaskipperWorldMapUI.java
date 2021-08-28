/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.map.MapModule;
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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.text.TextFormatting.*;

public class SeaskipperWorldMapUI extends WorldMapUI {

    private final static Pattern SEASKIPPER_PASS_MATCHER = Pattern.compile("^([A-Za-z ']+) Pass for (\\d+)");

    SeaskipperLocation origin;
    private final HashMap<String, SeaskipperLocation> locations = new HashMap<>();
    private final HashMap<SeaskipperLocation, Integer> routes = new HashMap<>();
    private int boatSlot = -1;

    private boolean receivedItems = false;
    ChestReplacer chest;

    // Properties
    private static boolean showLocations = true;
    private static boolean showInaccessibleLocations = false;
    private static boolean showSeaskipperRoutes = false;

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

        locations.values().forEach(c -> c.updateAxis(MapModule.getModule().getMainMap(), width, height, maxX, minX, maxZ, minZ));
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
        ), (v) -> showInaccessibleLocations, (i, btn) -> showInaccessibleLocations = !showInaccessibleLocations);

        addButton(MapButtonType.PIN, 1, Arrays.asList(
                LIGHT_PURPLE + "[>] Show Seaskipper routes",
                GRAY + "Click here to enable/disable",
                GRAY + "Seaskipper routes."
        ), (v) -> showSeaskipperRoutes, (i, btn) -> showSeaskipperRoutes = !showSeaskipperRoutes);

        addButton(MapButtonType.BOAT, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Buy a boat",
                GRAY + "Click here to buy a boat."
        ), (v) -> {
                for (Slot slot : McIf.player().inventoryContainer.inventorySlots) {
                    if (slot.getStack().getItem() == Items.BOAT) return false;
                }

                return true;
        }, (i, btn) -> {
            if (!i.isEnabled() || boatSlot == -1) return;
            chest.handleMouseClick(chest.inventorySlots.getSlot(boatSlot), boatSlot, 0, ClickType.PICKUP);
        });

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

            if (stack.getItem() == Items.NAME_TAG) {
                receivedItems = true;

                String displayname = getTextWithoutFormattingCodes(stack.getDisplayName());

                if (displayname == null) continue;

                Matcher result = SEASKIPPER_PASS_MATCHER.matcher(displayname);

                if (!result.find() || result.groupCount() != 2) {
                    Reference.LOGGER.info("Seaskipper Pass with name" + stack.getDisplayName() + " didn't parse");
                    continue;
                }

                SeaskipperLocation location = locations.get(result.group(1));
                int cost = Integer.parseInt(result.group(2));
                location.setCost(cost);
                location.setActiveType(SeaskipperLocation.Accessibility.ACCESSIBLE);
                routes.put(location, s.slotNumber);
                continue;
            }

            if (stack.getItem() == Items.BOAT) { //Buy boat
                boatSlot = s.slotNumber;
            }
        }

        if (!receivedItems) return;

        // resets the animation timer
        animationEnd = System.currentTimeMillis() + 300;

        //Get origin based on box, make sure Seaskipper area is in region
        double playerX = McIf.player().posX;
        double playerZ = McIf.player().posZ;

        for (SeaskipperLocation location : locations.values()) {
            if (routes.containsKey(location)) continue;

            if (location.getSquareRegion().isInside(playerX, playerZ)) {
                location.setActiveType(SeaskipperLocation.Accessibility.ORIGIN);
                origin = location;
                break;
            }
        }

        //Distance based way to find origin if that fails
        if (origin == null) {
            double sqdist = Double.MAX_VALUE;
            SeaskipperLocation closest = null;

            for (SeaskipperLocation location : locations.values()) {
                if (routes.containsKey(location)) continue;

                double sqLocationToPlayer = location.getSquareRegion().signedDist(playerX, playerZ);

                if (sqLocationToPlayer < sqdist) {
                    sqdist = sqLocationToPlayer;
                    closest = location;
                }
            }

            if (closest != null) {
                closest.setActiveType(SeaskipperLocation.Accessibility.ORIGIN);
                origin = closest;
            }
        }

        //Construct info boxes after all info is gathered
        locations.values().forEach(SeaskipperLocation::constructInfoBox);
    }

    @Override
    protected void drawIcons(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        createMask();

        if (showSeaskipperRoutes) generateSeaSkipperRoutes();
        locations.values().forEach((c) -> c.drawScreen(mouseX, mouseY, partialTicks, showLocations, showInaccessibleLocations));
        locations.values().forEach((c) -> c.postDraw(mouseX, mouseY, partialTicks, width, height, showInaccessibleLocations));
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
                buffer.pos(origin.getCenterX(), origin.getCenterY(), 0).color(67, 142, 130, .5f).endVertex();
            }
            tess.draw();

            GlStateManager.enableTexture2D();
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Skip if hovering base
        if (mouseButton != 0 || mapButtons.get(0).isHovering(mouseX, mouseY)) return;

        for (SeaskipperLocation location : locations.values()) {
            if (!routes.containsKey(location)) continue;

            if (location.isHovered(mouseX, mouseY) && location.isAccessible()) {
                int slotNumber = routes.get(location);
                chest.handleMouseClick(chest.inventorySlots.getSlot(slotNumber), slotNumber, 0, ClickType.PICKUP);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
}
