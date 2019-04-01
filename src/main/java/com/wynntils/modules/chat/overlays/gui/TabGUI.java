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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;


public class TabGUI extends GuiScreen {

    int id;
    ChatTab tab;

    public TabGUI(int id) {
        this.id = id;

        if(id != -2)
            tab = TabManager.getTabById(id);
    }
    ArrayList<GuiCheckBox> simpleRegexSettings = new ArrayList<>();

    //ui things
    GuiButton saveButton;
    GuiButton deleteButton;
    GuiButton advancedButton;
    GuiButton closeButton;
    GuiCheckBox lowPriority;
    GuiCheckBox allRegex;
    GuiCheckBox localRegex;
    GuiCheckBox guildRegex;
    GuiCheckBox partyRegex;
    GuiCheckBox shoutsRegex;
    GuiCheckBox pmRegex;
    GuiTextField nameTextField;
    GuiTextField regexTextField;
    GuiTextField autoCommandField;
    GuiTextField orderNbField;

    //labels
    GuiLabel nameLabel;
    GuiLabel regexLabel;
    GuiLabel autoCommand;
    GuiLabel orderNb;
    GuiLabel simpleSettings;

    @Override
    public void initGui() {
        labelList.clear();
        simpleRegexSettings.clear();

        int x = width / 2; int y = height / 2;

        //General
        buttonList.add(saveButton = new GuiButton(0, x - 90, y + 40, 40, 20, TextFormatting.GREEN + "Save"));
        buttonList.add(deleteButton = new GuiButton(1, x - 45, y + 40, 40, 20, TextFormatting.DARK_RED + "Delete"));
        buttonList.add(closeButton = new GuiButton(2, x + 50, y + 40, 40, 20, TextFormatting.WHITE + "Close"));
        buttonList.add(advancedButton = new GuiButton(4, x - 65, y - 60, 130, 20, "Show Advanced Settings"));

        deleteButton.enabled = (id != -2) && TabManager.getAvailableTabs().size() > 1;

        nameTextField = new GuiTextField(3, mc.fontRenderer, x - 110, y - 90, 80, 20);
        nameTextField.setVisible(true);
        nameTextField.setEnabled(true);
        nameTextField.setEnableBackgroundDrawing(true);
        nameTextField.setMaxStringLength(10);

        autoCommandField = new GuiTextField(3, mc.fontRenderer, x - 12, y - 90, 80, 20);
        autoCommandField.setVisible(true);
        autoCommandField.setEnabled(true);
        autoCommandField.setEnableBackgroundDrawing(true);
        autoCommandField.setMaxStringLength(10);

        orderNbField = new GuiTextField(3, mc.fontRenderer, x + 85, y - 90, 25, 20);
        orderNbField.setVisible(true);
        orderNbField.setEnabled(true);
        orderNbField.setEnableBackgroundDrawing(true);
        orderNbField.setMaxStringLength(2);

        buttonList.add(lowPriority = new GuiCheckBox(3, x - 100, y + 22, "Low Priority", true));

        //Simple
        labelList.add(simpleSettings = new GuiLabel(mc.fontRenderer, 4, x - 100, y - 35, 10, 10, 0xFFFFFF));
        simpleSettings.addLine("Message types " + TextFormatting.RED + "*");

        simpleRegexSettings.add(allRegex = new GuiCheckBox(10,x - 100, y - 25, "All", false));
        simpleRegexSettings.add(localRegex = new GuiCheckBox(11,x - 50, y - 25, "Local", false));
        simpleRegexSettings.add(guildRegex = new GuiCheckBox(12,x, y - 25, "Guild", false));
        simpleRegexSettings.add(partyRegex = new GuiCheckBox(13, x + 50 , y - 25, "Party", false));
        simpleRegexSettings.add(shoutsRegex = new GuiCheckBox(14,x - 100, y - 10, "Shouts", false));
        simpleRegexSettings.add(pmRegex = new GuiCheckBox(15,x - 50, y - 10, "PMs", false));
        buttonList.addAll(simpleRegexSettings);
        applyRegexSettings();
        //Advanced
        regexTextField = new GuiTextField(3, mc.fontRenderer, x - 100, y - 20, 200, 20);
        regexTextField.setVisible(false);
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

        labelList.add(nameLabel = new GuiLabel(mc.fontRenderer, 0, x - 110, y - 105, 10, 10, 0xFFFFFF));
        nameLabel.addLine("Name " + TextFormatting.RED + "*");
        labelList.add(regexLabel = new GuiLabel(mc.fontRenderer, 1, x - 100, y - 35, 10, 10, 0xFFFFFF));
        regexLabel.addLine("Regex " + TextFormatting.RED + "*");
        regexLabel.visible = false;
        labelList.add(autoCommand = new GuiLabel(mc.fontRenderer, 2, x - 12, y - 105, 10, 10, 0xFFFFFF));
        autoCommand.addLine("Auto Command");
        labelList.add(orderNb = new GuiLabel(mc.fontRenderer, 3, x + 85, y - 105, 10, 10, 0xFFFFFF));
        orderNb.addLine("Order #");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if(button == closeButton) mc.displayGuiScreen(new ChatGUI());
        else if(button == saveButton) {
            if (id == -2) {
                TabManager.registerNewTab(new ChatTab(nameTextField.getText(), regexTextField.getText(), regexSettingsCreator(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.valueOf(orderNbField.getText()) : 0));
            } else {
                TabManager.updateTab(id, nameTextField.getText(), regexTextField.getText(), regexSettingsCreator(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.valueOf(orderNbField.getText()) : 0);
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
        } else if (button == advancedButton) {
            boolean simple;
            if (button.displayString.equals("Show Advanced Settings")) {
                button.displayString = "Hide Advanced Settings";
                simple = false;
            } else {
                button.displayString = "Show Advanced Settings";
                simple = true;
            }
            regexTextField.setVisible(!simple);
            regexLabel.visible = !simple;
            simpleSettings.visible = simple;
            simpleRegexSettings.forEach(b -> {
                b.visible = simple;
            });
        } else if (button == allRegex) {
            simpleRegexSettings.forEach(b -> {
                b.setIsChecked(((GuiCheckBox) button).isChecked());
            });
        }
        if (button.id >= 10 && button.id <= 16) {
            regexTextField.setText(regexCreator());
            checkIfRegexIsValid();
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

        if(regexTextField.getVisible() && mouseX >= regexTextField.x && mouseX < regexTextField.x + regexTextField.width && mouseY >= regexTextField.y && mouseY < regexTextField.y + regexTextField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "RegEx"), TextFormatting.GRAY + "This will parse the chat", " ", TextFormatting.GREEN + "You can learn RegEx at", TextFormatting.GOLD + "https://regexr.com/", "", TextFormatting.RED + "Required"), mouseX, mouseY);

        if(mouseX >= autoCommandField.x && mouseX < autoCommandField.x + autoCommandField.width && mouseY >= autoCommandField.y && mouseY < autoCommandField.y + autoCommandField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Auto Command"), TextFormatting.GRAY + "This will automatically", TextFormatting.GRAY + "put this command before", TextFormatting.GRAY + "any message.", "", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if(mouseX >= orderNbField.x && mouseX < orderNbField.x + orderNbField.width && mouseY >= orderNbField.y && mouseY < orderNbField.y + orderNbField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Order number"), TextFormatting.GRAY + "This determines the", TextFormatting.GRAY + "arrangement of the", TextFormatting.GRAY + "chat tabs.", TextFormatting.DARK_GRAY + "(lowest to highest)", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if(mouseX >= lowPriority.x && mouseX < lowPriority.x + lowPriority.width && mouseY >= lowPriority.y && mouseY < lowPriority.y + lowPriority.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Low priority"), TextFormatting.GRAY + "If selected, messages", TextFormatting.GRAY + "will attempt to match", TextFormatting.GRAY + "with other tabs first.", "", TextFormatting.GRAY + "This will also duplicate", TextFormatting.GRAY + "messages across other", TextFormatting.GRAY + "low priority tabs.", TextFormatting.RED + "Optional"), mouseX, mouseY);

        if (advancedButton.displayString.equals("Show Advanced Settings")) {
            if (mouseX >= allRegex.x && mouseX < allRegex.x + allRegex.width && mouseY >= allRegex.y && mouseY < allRegex.y + allRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: All"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "messages, except those", TextFormatting.GRAY + "deselected to this tab."), mouseX, mouseY);
            } else if (mouseX >= localRegex.x && mouseX < localRegex.x + localRegex.width && mouseY >= localRegex.y && mouseY < localRegex.y + localRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: Local"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "messages send by nearby", TextFormatting.GRAY + "players to this tab."), mouseX, mouseY);
            } else if (mouseX >= guildRegex.x && mouseX < guildRegex.x + guildRegex.width && mouseY >= guildRegex.y && mouseY < guildRegex.y + guildRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: Guild"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "messages send by guild", TextFormatting.GRAY + "members to this tab."), mouseX, mouseY);
            } else if (mouseX >= partyRegex.x && mouseX < partyRegex.x + partyRegex.width && mouseY >= partyRegex.y && mouseY < partyRegex.y + partyRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: Party"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "messages send by party", TextFormatting.GRAY + "members to this tab."), mouseX, mouseY);
            } else if (mouseX >= shoutsRegex.x && mouseX < shoutsRegex.x + shoutsRegex.width && mouseY >= shoutsRegex.y && mouseY < shoutsRegex.y + shoutsRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: Shouts"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "shouts messages", TextFormatting.GRAY + "to this tab."), mouseX, mouseY);
            } else if (mouseX >= pmRegex.x && mouseX < pmRegex.x + pmRegex.width && mouseY >= pmRegex.y && mouseY < pmRegex.y + pmRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + "Message Type: PMs"), TextFormatting.GRAY + "This will send all", TextFormatting.GRAY + "private messages", TextFormatting.GRAY + "to this tab."), mouseX, mouseY);
            }
        }

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

    private HashMap<String, Boolean> regexSettingsCreator() {
        if (advancedButton.displayString.equals("Hide Advanced Settings")) return null;

        HashMap<String, Boolean> r = new HashMap<>();
        simpleRegexSettings.forEach(b->{
            r.put(b.displayString, b.isChecked());
        });
        return r;
    }

    private void applyRegexSettings() {
        if (tab == null || tab.getRegexSettings() == null) return;
        tab.getRegexSettings().forEach((k, v) -> {
            for (GuiCheckBox cb: simpleRegexSettings) {
                if (cb.displayString.equals(k)) {
                    cb.setIsChecked(v);
                }
            }
        });
    }

    private String regexCreator() {
        if (advancedButton.displayString.equals("Hide Advanced Settings")) return "";

        HashMap<String, Boolean> regexSettings = regexSettingsCreator();
        ArrayList<String> result = new ArrayList<>();
        boolean allIsPresent = regexSettings.get("All");

        regexSettings.forEach((k,v) -> {
            if ((v && !allIsPresent) || (allIsPresent && !v)) {
                switch (k) {
                    case "Local":
                        result.add("^&7\\[\\d+\\*?\\/\\w{2}");
                        break;
                    case "Guild":
                        result.add(TabManager.DEFAULT_GUILD_REGEX);
                        break;
                    case "Party":
                        result.add(TabManager.DEFAULT_PARTY_REGEX);
                        break;
                    case "Shouts":
                        result.add("(^&3.*shouts:)");
                        break;
                    case "PMs":
                        result.add("(&7\\[.*\u27A4.*&7\\])");
                        break;
                }
            }
        });

        if (allIsPresent && result.size() > 0) {
            return String.format("^((?!%s).)*$", String.join("|", result));
        } else if (allIsPresent) {
            return ".*";
        } else {
            return String.join("|", result);
        }
    }
}
