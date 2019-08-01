package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.framework.ui.elements.UIEColorWheel;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.instances.WaypointProfile.WaypointType;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class WaypointCreationMenu extends UI {
    private GuiLabel nameFieldLabel;
    private GuiTextField nameField;
    private GuiLabel xCoordFieldLabel;
    private GuiTextField xCoordField;
    private GuiLabel yCoordFieldLabel;
    private GuiTextField yCoordField;
    private GuiLabel zCoordFieldLabel;
    private GuiTextField zCoordField;
    private GuiLabel coordinatesLabel;
    private GuiButton defaultVisibilityButton;
    private GuiButton alwaysVisibleButton;
    private GuiButton hiddenButton;
    private UIEColorWheel colorWheel;
    private GuiButton saveButton;
    private GuiButton cancelButton;
    private GuiButton waypointTypeNext;
    private GuiButton waypointTypeBack;

    private boolean isUpdatingExisting;
    private WaypointProfile wp;
    private MapWaypointIcon wpIcon;
    private GuiScreen previousGui;

    private int initialX;
    private int initialZ;

    private WaypointCreationMenuState state;

    public WaypointCreationMenu(GuiScreen previousGui) {
        this.previousGui = previousGui;

        initialX = Minecraft.getMinecraft().player.getPosition().getX();
        initialZ = Minecraft.getMinecraft().player.getPosition().getZ();
    }

    // Create a waypoint at a position other than the current player's position
    // TODO: use this (Maybe double clicking somewhere on the main map would create a waypoint at that position?)
    public WaypointCreationMenu(GuiScreen previousGui, int initialX, int initialZ) {
        this.previousGui = previousGui;

        this.initialX = initialX;
        this.initialZ = initialZ;
    }

    public WaypointCreationMenu(WaypointProfile wp, GuiScreen previousGui) {
        this(previousGui);
        this.wp = wp;
        isUpdatingExisting = true;
    }

    @Override public void onInit() { }
    @Override public void onClose() { }
    @Override public void onTick() { }
    @Override public void onWindowUpdate() {
        buttonList.clear();

        nameField = new GuiTextField(0, mc.fontRenderer, this.width/2 - 80, this.height/2 - 70, 160, 20);
        xCoordField = new GuiTextField(1, mc.fontRenderer, this.width/2 - 65, this.height/2 - 30, 40, 20);
        zCoordField = new GuiTextField(2, mc.fontRenderer, this.width/2 - 5, this.height/2 - 30, 40, 20);
        yCoordField = new GuiTextField(3, mc.fontRenderer, this.width/2 + 55, this.height/2 - 30, 25, 20);
        buttonList.add(waypointTypeNext = new GuiButton(97, this.width/2 - 40, this.height/2 + 10, 18, 18, ">"));
        buttonList.add(waypointTypeBack = new GuiButton(98, this.width/2 - 80, this.height/2 + 10, 18, 18, "<"));

        int visibilityButtonWidth = 100;
        int visibilityButtonHeight = this.height/2 + 40;
        buttonList.add(defaultVisibilityButton = new GuiButton(99, this.width/2 - 3 * visibilityButtonWidth / 2 - 2, visibilityButtonHeight, visibilityButtonWidth, 18, "Default"));
        buttonList.add(alwaysVisibleButton = new GuiButton(100, this.width/2 - visibilityButtonWidth / 2,visibilityButtonHeight, visibilityButtonWidth, 18, "Always Visible"));
        buttonList.add(hiddenButton = new GuiButton(101, this.width/2 + visibilityButtonWidth / 2 + 2,visibilityButtonHeight, visibilityButtonWidth, 18, "Hidden"));

        buttonList.add(cancelButton = new GuiButton(102, this.width/2 - 71, this.height - 80, 45, 18, "Cancel"));
        buttonList.add(saveButton = new GuiButton(103, this.width/2 + 25, this.height - 80, 45, 18, "Save"));
        saveButton.enabled = false;

        xCoordField.setText(Integer.toString(initialX));
        zCoordField.setText(Integer.toString(initialZ));
        yCoordField.setText(Integer.toString(Minecraft.getMinecraft().player.getPosition().getY()));

        nameFieldLabel = new GuiLabel(mc.fontRenderer,0,this.width/2 - 80,this.height/2 - 81,40,10,0xFFFFFF);
        nameFieldLabel.addLine("Waypoint Name:");
        xCoordFieldLabel = new GuiLabel(mc.fontRenderer,1,this.width/2 - 75,this.height/2 - 24,40,10,0xFFFFFF);
        xCoordFieldLabel.addLine("X");
        yCoordFieldLabel = new GuiLabel(mc.fontRenderer,2,this.width/2 + 45,this.height/2 - 24,40,10,0xFFFFFF);
        yCoordFieldLabel.addLine("Y");
        zCoordFieldLabel = new GuiLabel(mc.fontRenderer,3,this.width/2 - 15,this.height/2 - 24,40,10,0xFFFFFF);
        zCoordFieldLabel.addLine("Z");
        coordinatesLabel = new GuiLabel(mc.fontRenderer,3,this.width/2 - 80,this.height/2 - 41,40,10,0xFFFFFF);
        coordinatesLabel.addLine("Coordinates:");

        boolean returning = state != null;  // true if reusing gui (i.e., returning from another gui)

        if (!returning) {
            UIElements.add(colorWheel = new UIEColorWheel(0, 0, this.width / 2, this.height / 2 + 9, 20, 20, true, this::setColor, this));
        }

        WaypointProfile wp = returning ? wpIcon.getWaypointProfile() : this.wp;
        CustomColor color = wp == null ? CommonColors.WHITE : wp.getColor();

        if (wp == null) {
            setWpIcon(WaypointType.FLAG, 0, color);
        } else if (!returning) {
            nameField.setText(wp.getName());
            xCoordField.setText(Integer.toString((int) wp.getX()));
            yCoordField.setText(Integer.toString((int) wp.getY()));
            zCoordField.setText(Integer.toString((int) wp.getZ()));

            setWpIcon(wp.getType(), wp.getZoomNeeded(), color);
        }

        if (returning) {
            state.resetState(this);
        } else {
            state = new WaypointCreationMenuState();
            state.putState(this);
        }

        isAllValidInformation();
    }

    private void setWpIcon(WaypointType type, int zoomNeeded, CustomColor colour) {
        wpIcon = new MapWaypointIcon(new WaypointProfile("", 0, 0, 0, colour, type, zoomNeeded));

        final int disabledColour = 10526880;
        final int enabledColour = 0;

        defaultVisibilityButton.packedFGColour = disabledColour;
        alwaysVisibleButton.packedFGColour = disabledColour;
        hiddenButton.packedFGColour = disabledColour;

        switch (zoomNeeded) {
            case MapWaypointIcon.ANY_ZOOM:
                alwaysVisibleButton.packedFGColour = enabledColour;
                break;
            case MapWaypointIcon.HIDDEN_ZOOM:
                hiddenButton.packedFGColour = enabledColour;
                break;
            default:
                defaultVisibilityButton.packedFGColour = enabledColour;
        }
    }

    private int getZoomNeeded() {
        return wpIcon.getWaypointProfile().getZoomNeeded();
    }

    private void setZoomNeeded(int zoomNeeded) {
        setWpIcon(getWaypointType(), zoomNeeded, getColor());
    }

    private WaypointType getWaypointType() {
        return wpIcon.getWaypointProfile().getType();
    }

    private void setWaypointType(WaypointType waypointType) {
        setWpIcon(waypointType, getZoomNeeded(), getColor());
    }

    private CustomColor getColor() {
        return wpIcon.getWaypointProfile().getColor();
    }

    private void setColor(CustomColor color) {
        setWpIcon(getWaypointType(), getZoomNeeded(), color);
    }

    @Override public void onRenderPreUIE(ScreenRenderer renderer) {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer renderer) {
        if (nameField != null) nameField.drawTextBox();
        if (xCoordField != null) xCoordField.drawTextBox();
        if (yCoordField != null) yCoordField.drawTextBox();
        if (zCoordField != null) zCoordField.drawTextBox();

        nameFieldLabel.drawLabel(mc, mouseX, mouseY);
        xCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        yCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        zCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        coordinatesLabel.drawLabel(mc, mouseX, mouseY);

        fontRenderer.drawString("Icon:", this.width/2 - 80,this.height/2,0xFFFFFF, true);
        fontRenderer.drawString("Colour:", this.width/2,this.height/2,0xFFFFFF, true);

        float centreX = this.width / 2f - 60 + 9;
        float centreZ = this.height / 2f + 10 + 9;
        float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
        wpIcon.renderAt(renderer, centreX, centreZ, multiplier);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        state.putState(this);

        super.mouseClicked(mouseX, mouseY, mouseButton);

        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        xCoordField.mouseClicked(mouseX, mouseY, mouseButton);
        yCoordField.mouseClicked(mouseX, mouseY, mouseButton);
        zCoordField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        nameField.textboxKeyTyped(typedChar, keyCode);
        xCoordField.textboxKeyTyped(typedChar, keyCode);
        yCoordField.textboxKeyTyped(typedChar,keyCode);
        zCoordField.textboxKeyTyped(typedChar, keyCode);
        isAllValidInformation();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == saveButton) {
            WaypointProfile newWp = new WaypointProfile(
                    nameField.getText().trim(),
                    Integer.valueOf(xCoordField.getText().trim()), Integer.valueOf(yCoordField.getText().trim()), Integer.valueOf(zCoordField.getText().trim()),
                    getColor(), getWaypointType(), getZoomNeeded()
            );
            if (isUpdatingExisting) {
                MapConfig.Waypoints.INSTANCE.waypoints.set(MapConfig.Waypoints.INSTANCE.waypoints.indexOf(wp), newWp);
            } else {
                MapConfig.Waypoints.INSTANCE.waypoints.add(newWp);
            }
            MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());
            Utils.displayGuiScreen(previousGui == null ? new WorldMapUI() : previousGui);
        } else if (button == cancelButton) {
            Utils.displayGuiScreen(previousGui == null ? new WorldMapUI() : previousGui);
        } else if (button == waypointTypeNext) {
            setWaypointType(WaypointType.values()[(getWaypointType().ordinal() + 1) % WaypointType.values().length]);
        } else if (button == waypointTypeBack) {
            setWaypointType(WaypointType.values()[(getWaypointType().ordinal() + (WaypointType.values().length - 1)) % WaypointType.values().length]);
        } else if (button == defaultVisibilityButton) {
            setZoomNeeded(0);
        } else if (button == alwaysVisibleButton) {
            setZoomNeeded(MapWaypointIcon.ANY_ZOOM);
        } else if (button == hiddenButton) {
            setZoomNeeded(MapWaypointIcon.HIDDEN_ZOOM);
        }
    }

    private void isAllValidInformation() {
        if (!xCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { xCoordField.setTextColor(0xFF6666); } else { xCoordField.setTextColor(0xFFFFFF); }
        if (!yCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { yCoordField.setTextColor(0xFF6666); } else { yCoordField.setTextColor(0xFFFFFF); }
        if (!zCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { zCoordField.setTextColor(0xFF6666); } else { zCoordField.setTextColor(0xFFFFFF); }
        if (xCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && yCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && zCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && !nameField.getText().isEmpty() && getWaypointType() != null){
            saveButton.enabled = true;
        } else {
            saveButton.enabled = false;
        }
    }

    private class WaypointCreationMenuState {
        String nameField;
        String xCoordField;
        String yCoordField;
        String zCoordField;

        void putState(WaypointCreationMenu menu) {
            nameField = menu.nameField.getText();
            xCoordField = menu.xCoordField.getText();
            yCoordField = menu.yCoordField.getText();
            zCoordField = menu.zCoordField.getText();
        }

        void resetState(WaypointCreationMenu menu) {
            menu.nameField.setText(nameField);
            menu.xCoordField.setText(xCoordField);
            menu.yCoordField.setText(yCoordField);
            menu.zCoordField.setText(zCoordField);
        }
    }
}
