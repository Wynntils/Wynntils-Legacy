/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.GuildResourceManager;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import com.wynntils.modules.map.overlays.objects.MapTerritory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import static net.minecraft.util.text.TextFormatting.*;

public class GuildWorldMapUI extends WorldMapUI {

    private final long creationTime = System.currentTimeMillis();
    private boolean holdingMapKey = false;

    // Properties
    private boolean showTerritory = true;
    private boolean showOwners = false;
    private boolean resourceColors = false;
    private boolean showTradeRoutes = true;

    public GuildWorldMapUI() {
        super((float) McIf.player().posX, (float) McIf.player().posZ);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.mapButtons.clear();

        addButton(MapButtonType.CENTER, 0, Arrays.asList(
                AQUA + "[>] Show territory borders",
                GRAY + "Click here to enable/disable",
                GRAY + "territory borders."
        ), (v) -> showTerritory, (i, btn) -> showTerritory = !showTerritory);

        addButton(MapButtonType.PENCIL, 0, Arrays.asList(
                YELLOW + "[>] Show territory owners",
                GRAY + "Click here to enable/disable",
                GRAY + "territory owners."
        ), (v) -> showOwners, (i, btn) -> showOwners = !showOwners);

        addButton(MapButtonType.INFO, 1, Arrays.asList(
                GOLD + "[>] Use resource generation colors",
                GRAY + "Click here to switch between",
                GRAY + "resource generation colors and",
                GRAY + "guild colors."
        ), (v) -> resourceColors, (i, btn) -> resourceColors = !resourceColors);

        addButton(MapButtonType.PIN, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Show trade routes",
                GRAY + "Click here to enable/disable",
                GRAY + "territory trade routes."
        ), (v) -> showTradeRoutes, (i, btn) -> showTradeRoutes = !showTradeRoutes);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // HeyZeer0: This detects if the user is holding the map key;
        if (!holdingMapKey && (System.currentTimeMillis() - creationTime >= 150) && isHoldingMapKey()) holdingMapKey = true;

        // HeyZeer0: This close the map if the user was pressing the map key and after a moment dropped it
        if (holdingMapKey && !isHoldingMapKey()) {
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

        if (showTradeRoutes) generateTradeRoutes();
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
        if (!origin.isRendering() || !destination.isRendering()) return;

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

}
