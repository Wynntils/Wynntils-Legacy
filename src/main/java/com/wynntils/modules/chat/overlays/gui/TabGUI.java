/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat.overlays.gui;

import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.managers.TabManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.gui.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;


public class TabGUI extends GuiScreen {

    int id;
    ChatTab tab;

    public TabGUI(int id) {
        this.id = id;

        if(id != -2)
            tab = TabManager.getTabById(id);
    }

    //ui things
    GuiButton saveButton;
    GuiButton deleteButton;
    GuiButton closeButton;
    GuiCheckBox lowPriority;
    GuiTextField nameTextField;
    GuiTextField regexTextField;
    GuiTextField autoCommandField;
    GuiTextField orderNbField;

    //labels
    GuiLabel nameLabel;
    GuiLabel regexLabel;
    GuiLabel autoCommand;
    GuiLabel orderNb;

    @Override
    public void initGui() {
        labelList.clear();

        int x = width / 2; int y = height / 2;

        buttonList.add(saveButton = new GuiButton(0, x - 90, y + 30, 40, 20, TextFormatting.GREEN + "Save"));
        buttonList.add(deleteButton = new GuiButton(1, x - 45, y + 30, 40, 20, TextFormatting.DARK_RED + "Delete"));
        buttonList.add(closeButton = new GuiButton(2, x + 50, y + 30, 40, 20, TextFormatting.WHITE + "Close"));

        deleteButton.enabled = (id != -2) && TabManager.getAvailableTabs().size() > 1;

        buttonList.add(lowPriority = new GuiCheckBox(3, x - 90, y + 7, "Low Priority", false));

        nameTextField = new GuiTextField(3, mc.fontRenderer, x - 90, y - 70, 80, 20);
        nameTextField.setVisible(true);
        nameTextField.setEnabled(true);
        nameTextField.setEnableBackgroundDrawing(true);
        nameTextField.setMaxStringLength(10);

        autoCommandField = new GuiTextField(3, mc.fontRenderer, x + 10, y - 70, 80, 20);
        autoCommandField.setVisible(true);
        autoCommandField.setEnabled(true);
        autoCommandField.setEnableBackgroundDrawing(true);
        autoCommandField.setMaxStringLength(10);

        orderNbField = new GuiTextField(3, mc.fontRenderer, x + 65, y + 5, 25, 16);
        orderNbField.setVisible(true);
        orderNbField.setEnabled(true);
        orderNbField.setEnableBackgroundDrawing(true);
        orderNbField.setMaxStringLength(2);

        regexTextField = new GuiTextField(3, mc.fontRenderer, x - 90, y - 25, 180, 20);
        regexTextField.setVisible(true);
        regexTextField.setEnabled(true);
        regexTextField.setEnableBackgroundDrawing(true);
        regexTextField.setMaxStringLength(400);

        if(tab != null) {
            nameTextField.setText(tab.getName());
            regexTextField.setText(tab.getRegex().replace("§", "&"));
            lowPriority.setIsChecked(tab.isLowPriority());
            autoCommandField.setText(tab.getAutoCommand());
            orderNbField.setText(Integer.toString(tab.getOrderNb()));
            checkIfRegexIsValid();
        }

        labelList.add(nameLabel = new GuiLabel(mc.fontRenderer, 0, x - 90, y - 85, 10, 10, 0xFFFFFF));
        nameLabel.addLine("Name " + TextFormatting.RED + "*");
        labelList.add(regexLabel = new GuiLabel(mc.fontRenderer, 1, x - 90, y - 40, 10, 10, 0xFFFFFF));
        regexLabel.addLine("Regex " + TextFormatting.RED + "*");
        labelList.add(autoCommand = new GuiLabel(mc.fontRenderer, 2, x + 10, y - 85, 10, 10, 0xFFFFFF));
        autoCommand.addLine("Auto Command");
        labelList.add(orderNb = new GuiLabel(mc.fontRenderer, 3, x + 22, y + 9, 10, 10, 0xFFFFFF));
        orderNb.addLine("Order #");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if(button == closeButton) mc.displayGuiScreen(new ChatGUI());
        else if(button == saveButton) {
            if (id == -2) {
                TabManager.registerNewTab(new ChatTab(nameTextField.getText(), regexTextField.getText(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.valueOf(orderNbField.getText()) : 0));
            } else {
                TabManager.updateTab(id, nameTextField.getText(), regexTextField.getText(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.valueOf(orderNbField.getText()) : 0);
            }
            mc.displayGuiScreen(new ChatGUI());
        }else if(button == deleteButton) {
            mc.displayGuiScreen(new GuiYesNo((result, cc) -> {
                if(result) {
                    int c = TabManager.deleteTab(id);
                    if(ChatOverlay.getChat().getCurrentTabId() == id) ChatOverlay.getChat().setCurrentTab(c);
                    mc.displayGuiScreen(new ChatGUI());
                }else{
                    mc.displayGuiScreen(this);
                }
            }, TextFormatting.WHITE + (TextFormatting.BOLD + "Do you really want to delete this chat tab?"), TextFormatting.RED + "This action is irreversible!", 0));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(nameTextField != null) nameTextField.drawTextBox();
        if(regexTextField != null) regexTextField.drawTextBox();
        if(autoCommandField != null) autoCommandField.drawTextBox();
        if(orderNbField != null) orderNbField.drawTextBox();

        if(mouseX >= nameTextField.x && mouseX < nameTextField.x + nameTextField.width && mouseY >= nameTextField.y && mouseY < nameTextField.y + nameTextField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Name"), TextFormatting.GRAY + "This is how your tab", TextFormatting.GRAY + "will be named", "", TextFormatting.RED + "Required"), mouseX, mouseY);

        if(mouseX >= regexTextField.x && mouseX < regexTextField.x + regexTextField.width && mouseY >= regexTextField.y && mouseY < regexTextField.y + regexTextField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "RegEx"), TextFormatting.GRAY + "This will parse the chat", " ", TextFormatting.GREEN + "You can learn RegEx at", TextFormatting.GOLD + "https://regexr.com/", "", TextFormatting.RED + "Required"), mouseX, mouseY);

        if(mouseX >= autoCommandField.x && mouseX < autoCommandField.x + autoCommandField.width && mouseY >= autoCommandField.y && mouseY < autoCommandField.y + autoCommandField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Auto Command"), TextFormatting.GRAY + "This will automatically", TextFormatting.GRAY + "put this command before", TextFormatting.GRAY + "any message.", "", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if(mouseX >= orderNbField.x && mouseX < orderNbField.x + orderNbField.width && mouseY >= orderNbField.y && mouseY < orderNbField.y + orderNbField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Order number"), TextFormatting.GRAY + "This determines the", TextFormatting.GRAY + "arrangement of the", TextFormatting.GRAY + "chat tabs.", TextFormatting.DARK_GRAY + "(lowest to highest)", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if(mouseX >= lowPriority.x && mouseX < lowPriority.x + lowPriority.width && mouseY >= lowPriority.y && mouseY < lowPriority.y + lowPriority.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Low priority"), TextFormatting.GRAY + "If selected, messages", TextFormatting.GRAY + "will attempt to match", TextFormatting.GRAY + "with other tabs first.", "", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if(saveButton.enabled && mouseX >= saveButton.x && mouseX < saveButton.x + saveButton.width && mouseY >= saveButton.y && mouseY < saveButton.y + saveButton.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Save"), TextFormatting.GRAY + "Click here to save", TextFormatting.GRAY + "this chat tab."), mouseX, mouseY);

        if(deleteButton.enabled && mouseX >= deleteButton.x && mouseX < deleteButton.x + deleteButton.width && mouseY >= deleteButton.y && mouseY < deleteButton.y + deleteButton.height)
            drawHoveringText(Arrays.asList(TextFormatting.DARK_RED + (TextFormatting.BOLD + "Delete"), TextFormatting.GRAY + "Click here to delete", TextFormatting.GRAY + "this chat tab.", "", TextFormatting.RED + "Irreversible action"), mouseX, mouseY);

        saveButton.enabled = !regexTextField.getText().isEmpty() && regexValid && !nameTextField.getText().isEmpty();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        regexTextField.mouseClicked(mouseX, mouseY, mouseButton);
        nameTextField.mouseClicked(mouseX, mouseY, mouseButton);
        autoCommandField.mouseClicked(mouseX, mouseY, mouseButton);
        orderNbField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        nameTextField.textboxKeyTyped(typedChar, keyCode);
        autoCommandField.textboxKeyTyped(typedChar, keyCode);
        orderNbField.textboxKeyTyped(typedChar, keyCode);
        if(regexTextField.textboxKeyTyped(typedChar, keyCode)) checkIfRegexIsValid();
    }

    boolean regexValid = false;

    private void checkIfRegexIsValid() {
        try{
            Pattern.compile(regexTextField.getText());
            regexTextField.setTextColor(0x55FF55);
            regexValid = true;
            return;
        }catch (Exception ignored) { }

        regexTextField.setTextColor(0xFF5555);
        regexValid = false;
    }
}
