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
import com.wynntils.modules.map.overlays.objects.MapTerritory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    private int territoryDifficultyFilter = 0; // 0=off; 1=verylow; 2=low; 3=med; 4=high; 5=veryhigh

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

        addButton(MapButtonIcon.PLUS, 4, Arrays.asList(
                BLUE + "[>] Territory difficulty filter",
                GRAY + "Click here to cycle territory",
                GRAY + "difficulty filter."
        ), false, (v) -> territoryDifficultyFilter, (i, btn) -> {
            if (territoryDifficultyFilter == 5) {
                territoryDifficultyFilter = 0;
            } else {
                territoryDifficultyFilter += 1;
            }
        });
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

        if (territoryDifficultyFilter != 0) {
            HashMap<String, MapTerritory> renderableTerritories;
            for (Map.Entry<String, MapTerritory> territory : territories.entrySet()) {
                if territory.getValue().getTerritory().
            }
        }

        territories.values().forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks, showTerritory, resourceColors, !showOwners, showOwners));
        territories.values().forEach(c -> c.postDraw(mouseX, mouseY, partialTicks, width, height));

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
