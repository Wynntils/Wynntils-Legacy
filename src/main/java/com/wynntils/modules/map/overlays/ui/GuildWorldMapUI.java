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
import java.util.function.Predicate;

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
    private enum TDFTypes {
        NORMAL,
        HIGHER,
        LOWER
    }
    private TDFTypes territoryDefenseFilterType = TDFTypes.NORMAL;
    private HashMap<String, MapTerritory> filteredTerritories;

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
        ), (v) -> showTerritory, (i, btn) -> showTerritory = !showTerritory);

        addButton(MapButtonIcon.PENCIL, 0, Arrays.asList(
                YELLOW + "[>] Show territory owners",
                GRAY + "Click here to enable/disable",
                GRAY + "territory owners."
        ), (v) -> showOwners, (i, btn) -> showOwners = !showOwners);

        addButton(MapButtonIcon.INFO, 1, Arrays.asList(
                GOLD + "[>] Use resource generation colors",
                GRAY + "Click here to switch between",
                GRAY + "resource generation colors and",
                GRAY + "guild colors."
        ), (v) -> resourceColors, (i, btn) -> resourceColors = !resourceColors);

        addButton(MapButtonIcon.PIN, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Show trade routes",
                GRAY + "Click here to enable/disable",
                GRAY + "territory trade routes."
        ), (v) -> showTradeRoutes, (i, btn) -> showTradeRoutes = !showTradeRoutes);

        addButton(MapButtonIcon.PLUS, 3, Arrays.asList(
                RED + "[>] Shift + Right Click on a territory",
                RED + "to open management menu.",
                GRAY + "Click here to enable/disable",
                GRAY + "territory management shortcut."
        ), (v) -> territoryManageShortcut, (i, btn) -> territoryManageShortcut = !territoryManageShortcut);

        cycleButton = addButton(MapButtonIcon.SEARCH, 4, Arrays.asList(
                BLUE + "[>] Territory defense filter",
                GRAY + "Click here to cycle territory defense filter",
                GRAY + "Hold SHIFT to filter higher, CTRL to filter lower",
                GRAY + "Current value: " + getDefenseFilterText()
        ), (v) -> true, (i, btn) -> {
            if (isShiftKeyDown()) {
                territoryDefenseFilterType = TDFTypes.HIGHER;
            } else if (isCtrlKeyDown()) {
                territoryDefenseFilterType = TDFTypes.LOWER;
            } else {
                territoryDefenseFilterType = TDFTypes.NORMAL;
            }

            if (territoryDefenseFilter >= 5 || (territoryDefenseFilterType != TDFTypes.NORMAL && territoryDefenseFilter == 4)) { // Safety check and reset
                territoryDefenseFilter = 0;
            } else {
                ++territoryDefenseFilter;
            }

            // Remove redundant settings (Very low and lower, very high and lower, very low and higher, very high and higher)
            if (territoryDefenseFilterType != TDFTypes.NORMAL && territoryDefenseFilter == 1) {
                territoryDefenseFilter = 2;
            }

            cycleButton.editHoverLore(Arrays.asList(
                    BLUE + "[>] Territory defense filter",
                    GRAY + "Click here to cycle territory defense filter",
                    GRAY + "Hold SHIFT to filter higher, CTRL to filter lower",
                    GRAY + "Current value: " + getDefenseFilterText()));
        });
    }

    private String getDefenseFilterText() {
        String diff;
        String type;
        switch (territoryDefenseFilter) {
            case 1:
                diff = GREEN + "Very Low";
                break;
            case 2:
                diff = GREEN + "Low";
                break;
            case 3:
                diff = YELLOW + "Medium";
                break;
            case 4:
                diff = RED + "High";
                break;
            case 5:
                diff = RED + "Very High";
                break;
            default:
                return GRAY + "Off";
        }

        switch (territoryDefenseFilterType) {
            case HIGHER:
                type = RESET + " and higher";
                break;
            case LOWER:
                type = RESET + " and lower";
                break;
            case NORMAL:
            default:
                type = "";
                break;
        }

        return diff + type;
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

    private HashMap<String, MapTerritory> getFilteredTerritories() {
        if (territoryDefenseFilter == 0) return territories;

        Predicate<MapTerritory> filterCheck;
        if (territoryDefenseFilterType == TDFTypes.HIGHER) {
            filterCheck = territory -> territory.getDefenses() >= territoryDefenseFilter;
        } else if (territoryDefenseFilterType == TDFTypes.LOWER) {
            filterCheck = territory -> territory.getDefenses() <= territoryDefenseFilter;
        } else {
            filterCheck = territory -> territory.getDefenses() == territoryDefenseFilter;
        }

        HashMap<String, MapTerritory> filteredTerritories = new HashMap<>();

        for (Map.Entry<String, MapTerritory> territory : territories.entrySet()) {
            if (filterCheck.test(territory.getValue())) {
                filteredTerritories.put(territory.getKey(), territory.getValue());
            }
        }

        return filteredTerritories;
    }

    @Override
    protected void drawIcons(int mouseX, int mouseY, float partialTicks) {
        if (!Reference.onWorld) return;

        MapProfile map = MapModule.getModule().getMainMap();
        if (!map.isReadyToUse()) return;

        createMask();

        drawPositionCursor(map);

        filteredTerritories = getFilteredTerritories();

        if (showTradeRoutes) generateTradeRoutes();

        filteredTerritories.values().forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks, showTerritory, resourceColors, !showOwners, showOwners));
        filteredTerritories.values().forEach(c -> c.postDraw(mouseX, mouseY, partialTicks, width, height));

        clearMask();
    }

    protected void generateTradeRoutes() {
        HashSet<String> alreadyGenerated = new HashSet<>();
        for (String territoryName : filteredTerritories.keySet()) {
            GuildResourceContainer resources = GuildResourceManager.getResources(territoryName);
            if (resources == null) continue;

            MapTerritory origin = filteredTerritories.get(territoryName);
            for (String tradingRoute : resources.getTradingRoutes()) {
                // we don't want lines to be duplicated from each route so we just draw them one time
                if (alreadyGenerated.contains(tradingRoute + territoryName)) continue;

                // Destination does not exist, ignore the routes that involve them
                if (!filteredTerritories.containsKey(tradingRoute)) continue;

                MapTerritory destination = filteredTerritories.get(tradingRoute);

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

        for (MapTerritory territory : filteredTerritories.values()) {
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
