/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.ui;

import com.google.common.collect.Lists;
import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.commands.CommandCompass;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import com.wynntils.modules.map.overlays.objects.MapButton;
import com.wynntils.modules.music.managers.SoundTrackManager;
import com.wynntils.webapi.WebManager;
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

    private int easterEggClicks = 0;

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
        ), (v) -> true, (i, btn) -> McIf.mc().displayGuiScreen(new WaypointCreationMenu(null)));

        addButton(MapButtonType.PENCIL, 0, Arrays.asList(
            GOLD + "[>] Manage Paths",
            GRAY + "List, Delete or Create",
            GRAY + "drawed lines that help you",
            GRAY + "to navigate around the world!"
        ), (v) -> true, (i, btn) -> McIf.mc().displayGuiScreen(new PathWaypointOverwiewUI()));

        addButton(MapButtonType.PIN, 1, Arrays.asList(
                RED + "[>] Manage Waypoints",
                GRAY + "List, Delete or Create",
                GRAY + "all your preview set",
                GRAY + "waypoints!"
        ), (v) -> true, (i, btn) -> McIf.mc().displayGuiScreen(new WaypointOverviewUI()));

        addButton(MapButtonType.SEARCH, 2, Arrays.asList(
                LIGHT_PURPLE + "[>] Search",
                RED + "In Development"
        ), (v) -> true, (i, btn) -> {});

        addButton(MapButtonType.CENTER, 3, Arrays.asList(
                AQUA + "[>] Configure Markers",
                GRAY + "Enable or disable each",
                GRAY + "map marker available."
        ), (v) -> true, (i, btn) -> McIf.mc().displayGuiScreen(new WorldMapSettingsUI()));

        addButton(MapButtonType.SHARE, 4, Arrays.asList(
                BLUE + "[>] Share Location",
                AQUA + "Left click" + GRAY +" to share your",
                GRAY + "location with your guild..",
                AQUA + "Right click" + GRAY + " to share your",
                GRAY + "location with your party.",
                AQUA + "Shift" + GRAY + " to share your",
                GRAY + "pin location instead."
        ), (v) -> true, (i, btn) -> handleShareButton(btn == 0));

        addButton(MapButtonType.INFO, 5, Lists.newArrayList(Arrays.asList(
                YELLOW + "[>] Quick Guide",
                GRAY + " - CTRL to show territories",
                GRAY + " - Left click on waypoint to place a pin.",
                GRAY + " - Middle click to place a pin.",
                GRAY + " - Double click on a pin to create a waypoint.",
                GRAY + " - Right click on pin to remove it.",
                GRAY + " - Right click to center on player."
        )), (v) -> true, (i, btn) -> {
            easterEggClicks += 1;

            // Easter egg :)
            if (easterEggClicks == 20) {
                i.getHoverLore().set(1, RED + "Alright you asked for it...");
                SoundTrackManager.findTrack(WebManager.getMusicLocations().getEntryTrack("easterEgg1"),
                        true, true, true, false, true, false);
            } else if (easterEggClicks == 15) {
                i.getHoverLore().set(1, RED + "Are you really sure you want this?");
                i.getHoverLore().remove(2);
            } else if (easterEggClicks == 10) {
                i.getHoverLore().set(1, RED + "You better stop, you'll");
                i.getHoverLore().set(2, RED + "not like the consequences.");
            } else if (easterEggClicks == 5) {
                i.getHoverLore().add(1, RED + "an useless button?");
                i.getHoverLore().add(1, RED + "Alright why are you clicking");
            }
        });
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
                long currentTime = McIf.getSystemTime();
                if (currentTime - lastClickTime < doubleClickTime) {
                    Location location = CompassManager.getCompassLocation();
                    McIf.mc().displayGuiScreen(new WaypointCreationMenu(null, (int) location.getX(), (int) location.getZ()));
                } else {
                    lastClickTime = currentTime;
                }
                return;
            }

            forEachIcon(c -> {
                if (c.mouseOver(mouseX, mouseY)) {
                    McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));

                    CompassManager.setCompassLocation(new Location(c.getInfo().getPosX(), 0, c.getInfo().getPosZ()));
                    resetCompassMapIcon();
                }
            });
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!holdingMapKey && keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) {
            McIf.mc().displayGuiScreen(null);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    private void handleShareButton(boolean leftClick) {
        int x, z;
        String type;
        if (isShiftKeyDown()) {
            type = "compass";
            Location location = CompassManager.getCompassLocation();
            if (location == null) return;
            x = (int) location.getX();
            z = (int) location.getZ();
        } else {
            type = "location";
            x = (int) McIf.player().posX;
            z = (int) McIf.player().posZ;
        }

        if (leftClick)
            CommandCompass.shareCoordinates("guild", type, x, z);
        else
            CommandCompass.shareCoordinates(null, type, x, z);
    }

}
