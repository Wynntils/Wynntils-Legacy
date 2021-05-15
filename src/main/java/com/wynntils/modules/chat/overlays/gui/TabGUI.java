/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.chat.overlays.gui;

import com.wynntils.McIf;
import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.managers.TabManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.gui.*;
import static net.minecraft.util.text.TextFormatting.*;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


public class TabGUI extends GuiScreen {

    int id;
    ChatTab tab;

    public TabGUI(int id) {
        this.id = id;

        if (id != -2)
            tab = TabManager.getTabById(id);
    }
    List<GuiCheckBox> simpleRegexSettings = new ArrayList<>();

    // ui things
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

    // labels
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

        // General
        buttonList.add(saveButton = new GuiButton(0, x - 90, y + 40, 40, 20, GREEN + "Save"));
        buttonList.add(deleteButton = new GuiButton(1, x - 45, y + 40, 40, 20, DARK_RED + "Delete"));
        buttonList.add(closeButton = new GuiButton(2, x + 50, y + 40, 40, 20, WHITE + "Close"));
        buttonList.add(advancedButton = new GuiButton(4, x - 65, y - 60, 130, 20, "Show Advanced Settings"));

        deleteButton.enabled = (id != -2) && TabManager.getAvailableTabs().size() > 1;

        nameTextField = new GuiTextField(3, McIf.mc().fontRenderer, x - 110, y - 90, 80, 20);
        nameTextField.setVisible(true);
        nameTextField.setEnabled(true);
        nameTextField.setEnableBackgroundDrawing(true);
        nameTextField.setMaxStringLength(10);

        autoCommandField = new GuiTextField(3, McIf.mc().fontRenderer, x - 12, y - 90, 80, 20);
        autoCommandField.setVisible(true);
        autoCommandField.setEnabled(true);
        autoCommandField.setEnableBackgroundDrawing(true);
        autoCommandField.setMaxStringLength(10);

        orderNbField = new GuiTextField(3, McIf.mc().fontRenderer, x + 85, y - 90, 25, 20);
        orderNbField.setVisible(true);
        orderNbField.setEnabled(true);
        orderNbField.setEnableBackgroundDrawing(true);
        orderNbField.setMaxStringLength(2);

        buttonList.add(lowPriority = new GuiCheckBox(3, x - 100, y + 22, "Low Priority", true));

        // Simple
        labelList.add(simpleSettings = new GuiLabel(McIf.mc().fontRenderer, 4, x - 100, y - 35, 10, 10, 0xFFFFFF));
        simpleSettings.addLine("Message types " + RED + "*");

        simpleRegexSettings.add(allRegex = new GuiCheckBox(10, x - 100, y - 25, "All", false));
        simpleRegexSettings.add(localRegex = new GuiCheckBox(11, x - 50, y - 25, "Local", false));
        simpleRegexSettings.add(guildRegex = new GuiCheckBox(12, x, y - 25, "Guild", false));
        simpleRegexSettings.add(partyRegex = new GuiCheckBox(13, x + 50, y - 25, "Party", false));
        simpleRegexSettings.add(shoutsRegex = new GuiCheckBox(14, x - 100, y - 10, "Shouts", false));
        simpleRegexSettings.add(pmRegex = new GuiCheckBox(15, x - 50, y - 10, "PMs", false));
        buttonList.addAll(simpleRegexSettings);
        applyRegexSettings();
        // Advanced
        regexTextField = new GuiTextField(3, McIf.mc().fontRenderer, x - 100, y - 20, 200, 20);
        regexTextField.setVisible(false);
        regexTextField.setEnabled(true);
        regexTextField.setEnableBackgroundDrawing(true);
        regexTextField.setMaxStringLength(400);

        if (tab != null) {
            nameTextField.setText(tab.getName());
            regexTextField.setText(tab.getRegex().replace("§", "&"));
            lowPriority.setIsChecked(tab.isLowPriority());
            autoCommandField.setText(tab.getAutoCommand());
            orderNbField.setText(Integer.toString(tab.getOrderNb()));
            checkIfRegexIsValid();
        }

        labelList.add(nameLabel = new GuiLabel(McIf.mc().fontRenderer, 0, x - 110, y - 105, 10, 10, 0xFFFFFF));
        nameLabel.addLine("Name " + RED + "*");
        labelList.add(regexLabel = new GuiLabel(McIf.mc().fontRenderer, 1, x - 100, y - 35, 10, 10, 0xFFFFFF));
        regexLabel.addLine("Regex " + RED + "*");
        regexLabel.visible = false;
        labelList.add(autoCommand = new GuiLabel(McIf.mc().fontRenderer, 2, x - 12, y - 105, 10, 10, 0xFFFFFF));
        autoCommand.addLine("Auto Command");
        labelList.add(orderNb = new GuiLabel(McIf.mc().fontRenderer, 3, x + 85, y - 105, 10, 10, 0xFFFFFF));
        orderNb.addLine("Order #");

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button == closeButton) McIf.mc().displayGuiScreen(new ChatGUI());
        else if (button == saveButton) {
            if (id == -2) {
                TabManager.registerNewTab(new ChatTab(nameTextField.getText(), regexTextField.getText(), regexSettingsCreator(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.parseInt(orderNbField.getText()) : 0));
            } else {
                TabManager.updateTab(id, nameTextField.getText(), regexTextField.getText(), regexSettingsCreator(), autoCommandField.getText(), lowPriority.isChecked(), orderNbField.getText().matches("[0-9]+") ? Integer.parseInt(orderNbField.getText()) : 0);
            }
            McIf.mc().displayGuiScreen(new ChatGUI());
        } else if (button == deleteButton) {
            McIf.mc().displayGuiScreen(new GuiYesNo((result, cc) -> {
                if (result) {
                    int c = TabManager.deleteTab(id);
                    if (ChatOverlay.getChat().getCurrentTabId() == id) ChatOverlay.getChat().setCurrentTab(c);
                    McIf.mc().displayGuiScreen(new ChatGUI());
                } else {
                    McIf.mc().displayGuiScreen(this);
                }
            }, WHITE + (BOLD + "Do you really want to delete this chat tab?"), RED + "This action is irreversible!", 0));
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
            simpleRegexSettings.forEach(b -> b.visible = simple);
        } else if (button == allRegex) {
            simpleRegexSettings.forEach(b -> b.setIsChecked(((GuiCheckBox) button).isChecked()));
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

        if (nameTextField != null) nameTextField.drawTextBox();
        if (regexTextField != null) regexTextField.drawTextBox();
        if (autoCommandField != null) autoCommandField.drawTextBox();
        if (orderNbField != null) orderNbField.drawTextBox();

        if (mouseX >= nameTextField.x && mouseX < nameTextField.x + nameTextField.width && mouseY >= nameTextField.y && mouseY < nameTextField.y + nameTextField.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "Name"), GRAY + "This is how your tab", GRAY + "will be named", "", RED + "Required"), mouseX, mouseY);

        if (regexTextField.getVisible() && mouseX >= regexTextField.x && mouseX < regexTextField.x + regexTextField.width && mouseY >= regexTextField.y && mouseY < regexTextField.y + regexTextField.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "RegEx"), GRAY + "This will parse the chat", " ", GREEN + "You can learn RegEx at", GOLD + "https://regexr.com/", "", RED + "Required"), mouseX, mouseY);

        if (mouseX >= autoCommandField.x && mouseX < autoCommandField.x + autoCommandField.width && mouseY >= autoCommandField.y && mouseY < autoCommandField.y + autoCommandField.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "Auto Command"), GRAY + "This will automatically", GRAY + "put this command before", GRAY + "any message.", "", RED + "Optional"), mouseX, mouseY);

        if (mouseX >= orderNbField.x && mouseX < orderNbField.x + orderNbField.width && mouseY >= orderNbField.y && mouseY < orderNbField.y + orderNbField.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "Order number"), GRAY + "This determines the", GRAY + "arrangement of the", GRAY + "chat tabs.", DARK_GRAY + "(lowest to highest)", RED + "Optional"), mouseX, mouseY);

        if (mouseX >= lowPriority.x && mouseX < lowPriority.x + lowPriority.width && mouseY >= lowPriority.y && mouseY < lowPriority.y + lowPriority.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "Low priority"), GRAY + "If selected, messages", GRAY + "will attempt to match", GRAY + "with other tabs first.", "", GRAY + "This will also duplicate", GRAY + "messages across other", GRAY + "low priority tabs.", RED + "Optional"), mouseX, mouseY);

        if (advancedButton.displayString.equals("Show Advanced Settings")) {
            if (mouseX >= allRegex.x && mouseX < allRegex.x + allRegex.width && mouseY >= allRegex.y && mouseY < allRegex.y + allRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: All"), GRAY + "This will send all", GRAY + "messages, except those", GRAY + "deselected to this tab."), mouseX, mouseY);
            } else if (mouseX >= localRegex.x && mouseX < localRegex.x + localRegex.width && mouseY >= localRegex.y && mouseY < localRegex.y + localRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: Local"), GRAY + "This will send all", GRAY + "messages send by nearby", GRAY + "players to this tab."), mouseX, mouseY);
            } else if (mouseX >= guildRegex.x && mouseX < guildRegex.x + guildRegex.width && mouseY >= guildRegex.y && mouseY < guildRegex.y + guildRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: Guild"), GRAY + "This will send all", GRAY + "messages send by guild", GRAY + "members to this tab."), mouseX, mouseY);
            } else if (mouseX >= partyRegex.x && mouseX < partyRegex.x + partyRegex.width && mouseY >= partyRegex.y && mouseY < partyRegex.y + partyRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: Party"), GRAY + "This will send all", GRAY + "messages send by party", GRAY + "members to this tab."), mouseX, mouseY);
            } else if (mouseX >= shoutsRegex.x && mouseX < shoutsRegex.x + shoutsRegex.width && mouseY >= shoutsRegex.y && mouseY < shoutsRegex.y + shoutsRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: Shouts"), GRAY + "This will send all", GRAY + "shouts messages", GRAY + "to this tab."), mouseX, mouseY);
            } else if (mouseX >= pmRegex.x && mouseX < pmRegex.x + pmRegex.width && mouseY >= pmRegex.y && mouseY < pmRegex.y + pmRegex.height) {
                drawHoveringText(Arrays.asList(GREEN + (BOLD + "Message Type: PMs"), GRAY + "This will send all", GRAY + "private messages", GRAY + "to this tab."), mouseX, mouseY);
            }
        }

        if (saveButton.enabled && mouseX >= saveButton.x && mouseX < saveButton.x + saveButton.width && mouseY >= saveButton.y && mouseY < saveButton.y + saveButton.height)
            drawHoveringText(Arrays.asList(GREEN + (BOLD + "Save"), GRAY + "Click here to save", GRAY + "this chat tab."), mouseX, mouseY);

        if (deleteButton.enabled && mouseX >= deleteButton.x && mouseX < deleteButton.x + deleteButton.width && mouseY >= deleteButton.y && mouseY < deleteButton.y + deleteButton.height)
            drawHoveringText(Arrays.asList(DARK_RED + (BOLD + "Delete"), GRAY + "Click here to delete", GRAY + "this chat tab.", "", RED + "Irreversible action"), mouseX, mouseY);

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
        if (regexTextField.textboxKeyTyped(typedChar, keyCode)) checkIfRegexIsValid();
    }

    boolean regexValid = false;

    private void checkIfRegexIsValid() {
        try {
            Pattern.compile(regexTextField.getText());
            regexTextField.setTextColor(0x55FF55);
            regexValid = true;
            return;
        } catch (Exception ignored) { }

        regexTextField.setTextColor(0xFF5555);
        regexValid = false;
    }

    private Map<String, Boolean> regexSettingsCreator() {
        if (advancedButton.displayString.equals("Hide Advanced Settings")) return null;

        Map<String, Boolean> r = new HashMap<>();
        simpleRegexSettings.forEach(b-> r.put(b.displayString, b.isChecked()));
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

        Map<String, Boolean> regexSettings = regexSettingsCreator();
        List<String> result = new ArrayList<>();
        boolean allIsPresent = regexSettings.get("All");

        regexSettings.forEach((k, v) -> {
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
