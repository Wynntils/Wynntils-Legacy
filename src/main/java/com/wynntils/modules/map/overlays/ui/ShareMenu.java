/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.commands.CommandCompass;
import com.wynntils.modules.core.managers.CompassManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ShareMenu extends UI {
    private GuiButton locationButton;
    private GuiButton compassButton;
    private GuiButton partyButton;
    private GuiButton shareButton;
    private GuiButton cancelButton;
    protected Map<GuiButton, String> userButtons= new HashMap<>();
    private String recipient;
    private String type;
    private int x;
    private int z;

    public ShareMenu() {
    }

    @Override
    public void onInit() {
    }

    @Override
    public void onTick() {
    }

    private void markSelected(GuiButton button) {
        final int enabledColour = 0;
        button.packedFGColour= enabledColour;
    }

    private void markNotSelected(GuiButton button) {
        final int disabledColour = 0xA0A0A0;
        button.packedFGColour= disabledColour;
    }

    private void enableCompassPosition(boolean useCompassPosition) {
        if (useCompassPosition) {
            markSelected(compassButton);
            markNotSelected(locationButton);
            Location location = CompassManager.getCompassLocation();
            if (location != null) {
                x = (int) location.getX();
                z = (int) location.getZ();
                type = "compass";
            }
        } else {
            markNotSelected(compassButton);
            markSelected(locationButton);
            x = (int) Minecraft.getMinecraft().player.posX;
            z = (int) Minecraft.getMinecraft().player.posZ;
            type = "location";
        }
    }

    @Override
    public void onWindowUpdate() {
        buttonList.clear();
        labelList.clear();

        GuiLabel shareTypeLabel = new GuiLabel(mc.fontRenderer, 0, 10, 25, 40, 10, 0xFFFFFF);
        shareTypeLabel.addLine("Share:");

        GuiLabel withLabel = new GuiLabel(mc.fontRenderer, 1, 10, 55, 40, 10, 0xFFFFFF);
        withLabel.addLine("With:");

        labelList.add(shareTypeLabel);
        labelList.add(withLabel);

        compassButton = new GuiButton(1, 50, 20, 80, 18, "Compass");
        locationButton = new GuiButton(2, 140, 20, 80, 18, "Location");

        Location location = CompassManager.getCompassLocation();
        if (location == null) {
            compassButton.enabled = false;
            enableCompassPosition(false);
        } else {
            enableCompassPosition(true);
        }

        buttonList.add(locationButton);
        buttonList.add(compassButton);

        partyButton = new GuiButton(3, 50, 50, 80, 18, "Party");
        markSelected(partyButton);
        buttonList.add(partyButton);

        List<String> friends = new ArrayList<>(PlayerInfo.getPlayerInfo().getFriendList());
        Collections.sort(friends);

        // Start one more column to the left
        int x = -80;
        int y = 84;
        for (int i = 0; i < Math.min(friends.size(), 15); i++) {
            String friend = friends.get(i);
            y += 24;
            // Start a new column after every fifth name
            if ((i % 5) == 0) {
                x += 130;
                y = 84;
            }

            String friendDisplayName = friend;
            // Shorten name if too long
            while (mc.fontRenderer.getStringWidth(friendDisplayName) > (120 - 4)) {
                friendDisplayName = friendDisplayName.substring(0, friendDisplayName.length() - 2);
            }
            GuiButton button = new GuiButton(100 + i, x, y, 120, 18, friendDisplayName);
            markNotSelected(button);
            buttonList.add(button);
            userButtons.put(button, friend);
        }

        int saveButtonHeight = this.height - 10 - 18;
        buttonList.add(shareButton = new GuiButton(4, 115, saveButtonHeight, 45, 18, "Share"));
        buttonList.add(cancelButton = new GuiButton(5, 200, saveButtonHeight, 45, 18, "Cancel"));
        cancelButton.packedFGColour = CommonColors.RED.toInt();
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onRenderPreUIE(ScreenRenderer renderer) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer renderer) {
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == shareButton) {
            System.out.println("SHARING: " + x + ", " + z);
            if (recipient != null) {
                System.out.println("sharing to recp" + recipient + " " + type);
            } else {
                System.out.println("sharing to party"+ " " + type);
            }
            CommandCompass.shareCoordinates(recipient, type, x, z);
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button == cancelButton) {
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button == locationButton) {
            enableCompassPosition(false);
        } else if (button == compassButton) {
            enableCompassPosition(true);
        } else if (button == partyButton) {
            userButtons.keySet().forEach(this::markNotSelected);
            markSelected(partyButton);
            recipient = null;
        } else if (userButtons.keySet().contains(button)) {
            userButtons.keySet().forEach(this::markNotSelected);
            markNotSelected(partyButton);
            markSelected(button);
            recipient = userButtons.get(button);
        }
    }
}
