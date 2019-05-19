package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.instances.WaypointProfile.WaypointType;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class WaypointCreationMenu extends GuiScreen {

    private ScreenRenderer renderer = new ScreenRenderer();
    private GuiLabel nameFieldLabel;
    private GuiTextField nameField;
    private GuiLabel xCoordFieldLabel;
    private GuiTextField xCoordField;
    private GuiLabel yCoordFieldLabel;
    private GuiTextField yCoordField;
    private GuiLabel zCoordFieldLabel;
    private GuiTextField zCoordField;
    private GuiLabel coordinatesLabel;
    private GuiCheckBox alwaysVisible;
    private GuiButton saveButton;
    private GuiButton cancelButton;
    private GuiButton waypointTypeNext;
    private GuiButton waypointTypeBack;
    private WaypointType waypointType = WaypointType.FLAG;

    private boolean isUpdatingExisting;
    private WaypointProfile wp;
    private GuiScreen previousGui;


    public WaypointCreationMenu(GuiScreen previousGui) {
        this.previousGui = previousGui;
    }

    public WaypointCreationMenu(WaypointProfile wp, GuiScreen previousGui) {
        this(previousGui);
        this.wp = wp;
        isUpdatingExisting = true;
    }

    @Override
    public void initGui() {
        nameField = new GuiTextField(0, mc.fontRenderer, this.width/2 - 80, this.height/2 - 70, 160, 20);
        xCoordField = new GuiTextField(1, mc.fontRenderer, this.width/2 - 65, this.height/2 - 30, 40, 20);
        zCoordField = new GuiTextField(2, mc.fontRenderer, this.width/2 - 5, this.height/2 - 30, 40, 20);
        yCoordField = new GuiTextField(3, mc.fontRenderer, this.width/2 + 55, this.height/2 - 30, 25, 20);
        buttonList.add(waypointTypeNext = new GuiButton(97, this.width/2 - 40, this.height/2 + 10, 18, 18, ">"));
        buttonList.add(waypointTypeBack = new GuiButton(98, this.width/2 - 80, this.height/2 + 10, 18, 18, "<"));
        buttonList.add(alwaysVisible = new GuiCheckBox(99, this.width/2 - 29,this.height/2 + 40,"Always Visible",false));
        buttonList.add(cancelButton = new GuiButton(100, this.width/2 - 71, this.height - 80, 45, 18, "Cancel"));
        buttonList.add(saveButton = new GuiButton(101, this.width/2 + 25, this.height - 80, 45, 18, "Save"));
        saveButton.enabled = false;

        xCoordField.setText(Integer.toString(Minecraft.getMinecraft().player.getPosition().getX()));
        zCoordField.setText(Integer.toString(Minecraft.getMinecraft().player.getPosition().getZ()));
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

        if (wp != null) {
            nameField.setText(wp.getName());
            xCoordField.setText(Integer.toString((int) wp.getX()));
            yCoordField.setText(Integer.toString((int) wp.getY()));
            zCoordField.setText(Integer.toString((int) wp.getZ()));
            alwaysVisible.setIsChecked(wp.getZoomNeeded() == -1000);
            waypointType = wp.getType();
            isAllValidInformation();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (nameField != null) nameField.drawTextBox();
        if (xCoordField != null) xCoordField.drawTextBox();
        if (yCoordField != null) yCoordField.drawTextBox();
        if (zCoordField != null) zCoordField.drawTextBox();

        nameFieldLabel.drawLabel(mc, mouseX, mouseY);
        xCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        yCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        zCoordFieldLabel.drawLabel(mc, mouseX, mouseY);
        coordinatesLabel.drawLabel(mc, mouseX, mouseY);

        int tx1 = 0; int tx2 = 0; int ty1 = 0; int ty2 = 0;
        switch (waypointType) {
            case LOOTCHEST_T1:
                tx1 = 136; ty1 = 35;
                tx2 = 154; ty2 = 53;
                break;
            case LOOTCHEST_T2:
                tx1 = 118; ty1 = 35;
                tx2 = 136; ty2 = 53;
                break;
            case LOOTCHEST_T3:
                tx1 = 82; ty1 = 35;
                tx2 = 100; ty2 = 53;
                break;
            case LOOTCHEST_T4:
                tx1 = 100; ty1 = 35;
                tx2 = 118; ty2 = 53;
                break;
            case DIAMOND:
                tx1 = 172; ty1 = 37;
                tx2 = 190; ty2 = 55;
                break;
            case FLAG:
                //TODO handle colours
                tx1 = 154; ty1 = 36;
                tx2 = 172; ty2 = 54;
                break;
            case SIGN:
                tx1 = 190; ty1 = 36;
                tx2 = 208; ty2 = 54;
                break;
            case STAR:
                tx1 = 208; ty1 = 36;
                tx2 = 226; ty2 = 54;
                break;
            case TURRET:
                tx1 = 226; ty1 = 36;
                tx2 = 244; ty2 = 54;
                break;
        }

        fontRenderer.drawString("Icon:", this.width/2 - 80,this.height/2,0xFFFFFF, true);
        fontRenderer.drawString("Colour:", this.width/2,this.height/2,0xFFFFFF, true);
        fontRenderer.drawString( "[" + TextFormatting.GRAY +"In Development"+ TextFormatting.RESET + "]", this.width/2, this.height/2 + 15, 0x808080, true);
        ScreenRenderer.beginGL(0, 0);
        renderer.drawRect(Textures.Map.map_icons, (this.width/2 - 60), (this.height/2 + 10), (this.width/2 - 60) + 18, (this.height/2 + 10) + 18, tx1, ty1, tx2, ty2);
        ScreenRenderer.endGL();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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
            if (isUpdatingExisting) {
                MapConfig.Waypoints.INSTANCE.waypoints.set(MapConfig.Waypoints.INSTANCE.waypoints.indexOf(wp), new WaypointProfile(nameField.getText().trim(), Integer.valueOf(xCoordField.getText().trim()), Integer.valueOf(yCoordField.getText().trim()), Integer.valueOf(zCoordField.getText().trim()), CommonColors.WHITE, waypointType, alwaysVisible.isChecked() ? -1000 : 0));
            } else {
                MapConfig.Waypoints.INSTANCE.waypoints.add(new WaypointProfile(nameField.getText().trim(), Integer.valueOf(xCoordField.getText().trim()), Integer.valueOf(yCoordField.getText().trim()), Integer.valueOf(zCoordField.getText().trim()), CommonColors.WHITE, waypointType, alwaysVisible.isChecked() ? -1000 : 0));
            }
            MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());
            Minecraft.getMinecraft().displayGuiScreen(previousGui == null ? new WorldMapUI() : previousGui);
        } else if (button == cancelButton) {
            Minecraft.getMinecraft().displayGuiScreen(previousGui == null ? new WorldMapUI() : previousGui);
        } else if (button == waypointTypeNext) {
            waypointType = WaypointType.values()[(waypointType.ordinal() + 1) % WaypointType.values().length];
        } else if (button == waypointTypeBack) {
            waypointType = WaypointType.values()[(waypointType.ordinal() + (WaypointType.values().length - 1)) % WaypointType.values().length];
        }
    }

    private void isAllValidInformation() {
        if (!xCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { xCoordField.setTextColor(0xFF6666); } else { xCoordField.setTextColor(0xFFFFFF); }
        if (!yCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { yCoordField.setTextColor(0xFF6666); } else { yCoordField.setTextColor(0xFFFFFF); }
        if (!zCoordField.getText().trim().matches("(-?(?!0)\\d+)|0")) { zCoordField.setTextColor(0xFF6666); } else { zCoordField.setTextColor(0xFFFFFF); }
        if (xCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && yCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && zCoordField.getText().trim().matches("(-?(?!0)\\d+)|0") && !nameField.getText().isEmpty()){
            saveButton.enabled = true;
        } else {
            saveButton.enabled = false;
        }
    }
}