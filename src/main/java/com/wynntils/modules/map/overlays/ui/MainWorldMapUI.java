/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.commands.CommandCompass;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import com.wynntils.modules.map.overlays.objects.MapButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;

import static net.minecraft.util.text.TextFormatting.*;

public class MainWorldMapUI extends WorldMapUI {

    private boolean holdingMapKey = false;
    private final long creationTime = System.currentTimeMillis();
    private long lastClickTime = Integer.MAX_VALUE;
    private static final long doubleClickTime = Utils.getDoubleClickTime();

    public MainWorldMapUI() {
        super();
    }

    public MainWorldMapUI(float startX, float startZ) {
        super(startX, startZ);
    }

    @Override
    public void initGui() {
        super.initGui();

        this.mapButtons.clear();

        addButton(MapButtonType.PLUS, 0, Arrays.asList(
            DARK_GREEN + "[>] Create a new Waypoint",
            GRAY + "Click here to create",
            GRAY + "a new waypoint."
        ), (v) -> true, (btn) -> Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(null)));

        addButton(MapButtonType.PENCIL, 0, Arrays.asList(
            GOLD + "[>] Manage Paths",
            GRAY + "List, Delete or Create",
            GRAY + "drawed lines that help you",
            GRAY + "to navigate around the world!"
        ), (v) -> true, (btn) -> Minecraft.getMinecraft().displayGuiScreen(new PathWaypointOverwiewUI()));

        addButton(MapButtonType.PIN, 1, Arrays.asList(
                RED + "[>] Manage Waypoints",
                GRAY + "List, Delete or Create",
                GRAY + "all your preview set",
                GRAY + "waypoints!"
        ), (v) -> true, (btn) -> Minecraft.getMinecraft().displayGuiScreen(new WaypointOverviewUI()));

        addButton(MapButtonType.SEARCH, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Search",
                RED + "In Development"
        ), (v) -> true, (btn) -> {});

        addButton(MapButtonType.CENTER, 3, Arrays.asList(
                AQUA + "[>] Configurate Markers",
                GRAY + "Enable or disable each",
                GRAY + "map marker available."
        ), (v) -> true, (btn) -> Minecraft.getMinecraft().displayGuiScreen(new WorldMapSettingsUI()));

        addButton(MapButtonType.SHARE, 4, Arrays.asList(
                BLUE + "[>] Share Location",
                AQUA + "Left click" + GRAY +" to share your",
                GRAY + "pin with your current party.",
                AQUA + "Right click" + GRAY + " to share your",
                GRAY + "location with your party."
        ), (v) -> PlayerInfo.get(SocialData.class).getPlayerParty().isPartying(), (btn) -> handleShareButton(btn == 0));

        addButton(MapButtonType.INFO, 5, Arrays.asList(
                YELLOW + "[>] Quick Guide",
                GRAY + " - CTRL to show territories",
                GRAY + " - Left click on waypoint to place a pin.",
                GRAY + " - Middle click to place a pin.",
                GRAY + " - Double click on a pin to create a waypoint.",
                GRAY + " - Right click on pin to remove it.",
                GRAY + " - Right click to center on player."
        ), (v) -> true, (btn) -> {});
    }

    private static boolean isHoldingMapKey() {
        int mapKey = MapModule.getModule().getMapKey().getKey();
        if (mapKey == 0) return false;  // Unknown key
        if (-100 <= mapKey && mapKey <= -85) {
            // Mouse key
            return Mouse.isButtonDown(mapKey + 100);
        }

        return Keyboard.isKeyDown(mapKey);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // HeyZeer0: This detects if the user is holding the map key;
        if (!holdingMapKey && (System.currentTimeMillis() - creationTime >= 150) && isHoldingMapKey()) holdingMapKey = true;

        // HeyZeer0: This close the map if the user was pressing the map key and after a moment dropped it
        if (holdingMapKey && !isHoldingMapKey()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Map buttons
        for (MapButton button : mapButtons) {
            if (!button.isHovering(mouseX, mouseY)) continue;
            if (button.getType().isIgnoreAction()) continue;

            button.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        if (mouseButton == 1) {
            if (compassIcon.mouseOver(mouseX, mouseY)) {
                CompassManager.reset();
                resetCompassMapIcon();
                return;
            }
            updateCenterPositionWithPlayerPosition();
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
            if (compassIcon.mouseOver(mouseX, mouseY)) {
                long currentTime = Minecraft.getSystemTime();
                if (currentTime - lastClickTime < doubleClickTime) {
                    Location location = CompassManager.getCompassLocation();
                    Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(null, (int) location.getX(), (int) location.getZ()));
                } else {
                    lastClickTime = currentTime;
                }
                return;
            }

            forEachIcon(c -> {
                if (c.mouseOver(mouseX, mouseY)) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));

                    CompassManager.setCompassLocation(new Location(c.getInfo().getPosX(), 0, c.getInfo().getPosZ()));
                    resetCompassMapIcon();
                }
            });
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!holdingMapKey && keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    private void handleShareButton(boolean leftClick) {
        if (leftClick) {
            Location location = CompassManager.getCompassLocation();
            if (location != null) {
                int x = (int) location.getX();
                int z = (int) location.getZ();
                CommandCompass.shareCoordinates(null, "compass", x, z);
            }
            return;
        }

        int x = (int) Minecraft.getMinecraft().player.posX;
        int z = (int) Minecraft.getMinecraft().player.posZ;
        CommandCompass.shareCoordinates(null, "location", x, z);
    }

}
