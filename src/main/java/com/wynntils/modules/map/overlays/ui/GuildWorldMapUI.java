/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.GuildResourceManager;
import com.wynntils.modules.map.overlays.enums.MapButtonIcon;
import com.wynntils.modules.map.overlays.objects.MapButton;
import com.wynntils.modules.map.overlays.objects.MapTerritory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

import static net.minecraft.util.text.TextFormatting.*;

public class GuildWorldMapUI extends WorldMapUI {

    private final long creationTime = System.currentTimeMillis();
    private boolean holdingMapKey = false;
    private boolean holdingDecided = false;

    // Properties
    private boolean showTerritory = true;
    private boolean showOwners = false;
    private boolean resourceColors = false;
    private boolean showTradeRoutes = true;
    private boolean territoryManageShortcut = true;

    MapButton cycleButton;
    private int territoryDefenseFilter = 0; // See #getDefenseFilterText for number definitions

    public GuildWorldMapUI() {
        super((float) McIf.player().posX, (float) McIf.player().posZ);
    }

    public GuildWorldMapUI(float startX, float startZ) {
        super(startX, startZ);

    }

    @Override
    public void initGui() {
        super.initGui();

        this.mapButtons.clear();

        addButton(MapButtonIcon.CENTER, 0, Arrays.asList(
                AQUA + "[>] Show territory borders",
                GRAY + "Click here to enable/disable",
                GRAY + "territory borders."
        ), true, (v) -> showTerritory ? 1 : 0, (i, btn) -> showTerritory = !showTerritory);

        addButton(MapButtonIcon.PENCIL, 0, Arrays.asList(
                YELLOW + "[>] Show territory owners",
                GRAY + "Click here to enable/disable",
                GRAY + "territory owners."
        ), true, (v) -> showOwners ? 1 : 0, (i, btn) -> showOwners = !showOwners);

        addButton(MapButtonIcon.INFO, 1, Arrays.asList(
                GOLD + "[>] Use resource generation colors",
                GRAY + "Click here to switch between",
                GRAY + "resource generation colors and",
                GRAY + "guild colors."
        ), true, (v) -> resourceColors ? 1 : 0, (i, btn) -> resourceColors = !resourceColors);

        addButton(MapButtonIcon.PIN, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Show trade routes",
                GRAY + "Click here to enable/disable",
                GRAY + "territory trade routes."
        ), true, (v) -> showTradeRoutes ? 1 : 0, (i, btn) -> showTradeRoutes = !showTradeRoutes);

        addButton(MapButtonIcon.PLUS, 3, Arrays.asList(
                RED + "[>] Shift + Right Click on a territory",
                RED + "to open management menu.",
                GRAY + "Click here to enable/disable",
                GRAY + "territory management shortcut."
        ), true , (v) -> territoryManageShortcut ? 1 : 0, (i, btn) -> territoryManageShortcut = !territoryManageShortcut);

        cycleButton = addButton(MapButtonIcon.SEARCH, 4, Arrays.asList(
                BLUE + "[>] Territory defense filter",
                GRAY + "Click here to cycle territory defense filter",
                GRAY + "Hold SHIFT to filter higher, CTRL to filter lower",
                GRAY + "Current value: " + getDefenseFilterText(territoryDefenseFilter)
        ), false, (v) -> territoryDefenseFilter, (i, btn) -> {
            if (territoryDefenseFilter == 5) {
                territoryDefenseFilter = 0;
            } else if (!isShiftKeyDown() && !isCtrlKeyDown()) {
                if (territoryDefenseFilter > 20) { // Larger than 20, "and lower" range (Ctrl)
                    territoryDefenseFilter -= 20;
                } else if (territoryDefenseFilter > 10) { // Larger than 10, "and higher" range (Shift)
                    territoryDefenseFilter -= 10;
                } else {
                    ++territoryDefenseFilter;
                }
            } else {
                ++territoryDefenseFilter;
            }

            // Shift (and higher) handling
            if ((territoryDefenseFilter > 14 || territoryDefenseFilter < 12) && isShiftKeyDown()) {
                territoryDefenseFilter = 12;
            }

            // Ctrl (and lower) handling
            if ((territoryDefenseFilter > 24 || territoryDefenseFilter < 22) && isCtrlKeyDown()) {
                territoryDefenseFilter = 22;
            }

            // Safety check in case number goes out of bounds
            if (territoryDefenseFilter > 24 || (territoryDefenseFilter > 14 && territoryDefenseFilter < 22)) {
                territoryDefenseFilter = 0;
            }

            cycleButton.editHoverLore(Arrays.asList(
                    BLUE + "[>] Territory defense filter",
                    GRAY + "Click here to cycle territory defense filter",
                    GRAY + "Hold SHIFT to filter higher, CTRL to filter lower",
                    GRAY + "Current value: " + getDefenseFilterText(territoryDefenseFilter)));
        });
    }

    private String getDefenseFilterText(int defenseLevel) {
        switch (defenseLevel) {
            case 1:
                return GREEN + "Very Low";
            case 2:
                return GREEN + "Low";
            case 3:
                return YELLOW + "Medium";
            case 4:
                return RED + "High";
            case 5:
                return RED + "Very High";

            // verylow+higher, veryhigh+higher, verylow+lower, and veryhigh+lower are missing because they're redundant
            case 12:
                return GREEN + "Low" + RESET + " and higher";
            case 13:
                return YELLOW + "Medium" + RESET + " and higher";
            case 14:
                return RED + "High" + RESET + " and higher";

            case 22:
                return GREEN + "Low" + RESET + " and lower";
            case 23:
                return YELLOW + "Medium" + RESET + " and lower";
            case 24:
                return RED + "High" + RESET + " and lower";

            default:
                return GRAY + "Off";
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // HeyZeer0: This detects if the user is holding the map key;
        if (!holdingDecided && (System.currentTimeMillis() - creationTime >= 150)) {
            holdingDecided = true;
            if (isHoldingMapKey())
                holdingMapKey = true;
        }
        // HeyZeer0: This close the map if the user was pressing the map key and after a moment dropped it
        if (holdingMapKey && !isHoldingMapKey()) {
            McIf.mc().displayGuiScreen(null);
            return;
        }

        if (MapConfig.WorldMap.INSTANCE.autoCloseMapOnMovement && checkForPlayerMovement(holdingDecided, holdingMapKey))  {
            McIf.mc().displayGuiScreen(null);
            return;
        }

        if (!Mouse.isButtonDown(1)) {
            updatePosition(mouseX, mouseY);
        }

        // start rendering
        ScreenRenderer.beginGL(0, 0);

        drawMap(mouseX, mouseY, partialTicks);
        drawIcons(mouseX, mouseY, partialTicks);
        drawCoordinates(mouseX, mouseY, partialTicks);
        drawMapButtons(mouseX, mouseY, partialTicks);

        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawIcons(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        createMask();

        drawPositionCursor(map);
        if (showTradeRoutes) generateTradeRoutes();

        HashMap<String, MapTerritory> renderableTerritories = new HashMap<>();
        int calculatedFilter;
        if (territoryDefenseFilter > 20) { // if "and lower" is active (Ctrl)
            calculatedFilter = territoryDefenseFilter - 20; // Don't do math in the for loop
            for (Map.Entry<String, MapTerritory> territory : territories.entrySet()) {
                if (territory.getValue().getDefenses() <= calculatedFilter) {
                    renderableTerritories.put(territory.getKey(), territory.getValue());
                }
            }
        } else if (territoryDefenseFilter > 10) { // if "and higher" is active (Shift)
            calculatedFilter = territoryDefenseFilter - 10;
            for (Map.Entry<String, MapTerritory> territory : territories.entrySet()) {
                if (territory.getValue().getDefenses() >= calculatedFilter) {
                    renderableTerritories.put(territory.getKey(), territory.getValue());
                }
            }
        } else if (territoryDefenseFilter != 0) { // If not off and also not higher/lower
            for (Map.Entry<String, MapTerritory> territory : territories.entrySet()) {
                if (territory.getValue().getDefenses() == territoryDefenseFilter) {
                    renderableTerritories.put(territory.getKey(), territory.getValue());
                }
            }
        } else {
            renderableTerritories = territories;
        }

        renderableTerritories.values().forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks, showTerritory, resourceColors, !showOwners, showOwners));
        renderableTerritories.values().forEach(c -> c.postDraw(mouseX, mouseY, partialTicks, width, height));

        clearMask();
    }

    protected void generateTradeRoutes() {
        HashSet<String> alreadyGenerated = new HashSet<>();
        for (String territoryName : territories.keySet()) {
            GuildResourceContainer resources = GuildResourceManager.getResources(territoryName);
            if (resources == null) continue;

            MapTerritory origin = territories.get(territoryName);
            for (String tradingRoute : resources.getTradingRoutes()) {
                // we don't want lines to be duplicated from each route so we just draw them one time
                if (alreadyGenerated.contains(tradingRoute + territoryName)) continue;

                MapTerritory destination = territories.get(tradingRoute);

                alreadyGenerated.add(territoryName + tradingRoute);
                drawTradeRoute(origin, destination);
            }
        }
    }

    protected void drawTradeRoute(MapTerritory origin, MapTerritory destination) {
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
                buffer.pos(destination.getCenterX(), destination.getCenterY(), 0).color(1f, 1f, 1f, .5f).endVertex();
                buffer.pos(origin.getCenterX(), origin.getCenterY(), 0).color(1f, 1f, 1f, .5f).endVertex();
            }
            tess.draw();

            GlStateManager.enableTexture2D();
        }
        GlStateManager.popMatrix();
    }

    private static boolean isHoldingMapKey() {
        int mapKey = MapModule.getModule().getGuildMapKey().getKey();
        if (mapKey == 0) return false;  // Unknown key
        if (-100 <= mapKey && mapKey <= -85) {
            // Mouse key
            return Mouse.isButtonDown(mapKey + 100);
        }

        return Keyboard.isKeyDown(mapKey);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!holdingMapKey && keyCode == MapModule.getModule().getGuildMapKey().getKeyBinding().getKeyCode()) {
            McIf.mc().displayGuiScreen(null);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        //Check for shift + left click
        if (!territoryManageShortcut || !Mouse.isButtonDown(1) || !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            super.handleMouseInput();
            return;
        }

        for (MapTerritory territory : territories.values()) {
            boolean hovering = territory.isBeingHovered(lastMouseX, lastMouseY);
            if (!hovering) continue;

            //Close map and open territory management menu
            Utils.displayGuiScreen(null);
            McIf.player().sendChatMessage("/guild territory " + territory.getTerritory().getName());
            return;
        }

        super.handleMouseInput();
    }
}
