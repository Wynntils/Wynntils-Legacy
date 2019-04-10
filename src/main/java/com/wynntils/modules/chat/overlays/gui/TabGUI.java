/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat.overlays.gui;

import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.managers.TabManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
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
        buttonList.add(saveButton = new GuiButton(0, x - 100, y + 80, 95, 20, TextFormatting.GREEN + I18n.format("wynntils.chat.tabgui.button.save")));
        buttonList.add(deleteButton = new GuiButton(1, x + 5, y + 80, 95, 20, TextFormatting.DARK_RED + I18n.format("wynntils.chat.tabgui.button.delete")));
        buttonList.add(closeButton = new GuiButton(2, x - 25, y + 105, 50, 20, TextFormatting.WHITE + I18n.format("wynntils.chat.tabgui.button.close")));
        buttonList.add(advancedButton = new GuiButton(4, x - 100, y - 20, 200, 20, I18n.format("wynntils.chat.tabgui.button.advanced_show")));

        deleteButton.enabled = (id != -2) && TabManager.getAvailableTabs().size() > 1;

        nameTextField = new GuiTextField(3, mc.fontRenderer, x - 110, y - 90, 220, 20);
        nameTextField.setVisible(true);
        nameTextField.setEnabled(true);
        nameTextField.setEnableBackgroundDrawing(true);
        nameTextField.setMaxStringLength(10);

        autoCommandField = new GuiTextField(3, mc.fontRenderer, x - 110, y - 50, 185, 20);
        autoCommandField.setVisible(true);
        autoCommandField.setEnabled(true);
        autoCommandField.setEnableBackgroundDrawing(true);
        autoCommandField.setMaxStringLength(10);

        orderNbField = new GuiTextField(3, mc.fontRenderer, x + 85, y - 50, 25, 20);
        orderNbField.setVisible(true);
        orderNbField.setEnabled(true);
        orderNbField.setEnableBackgroundDrawing(true);
        orderNbField.setMaxStringLength(2);

        buttonList.add(lowPriority = new GuiCheckBox(3, x - 100, y + 62, I18n.format("wynntils.chat.tabgui.button.low_priority"), true));

        //Simple
        labelList.add(simpleSettings = new GuiLabel(mc.fontRenderer, 4, x - 100, y + 5, 10, 10, 0xFFFFFF));
        simpleSettings.addLine(I18n.format("wynntils.chat.tabgui.button.message_types") + TextFormatting.RED + " *");

        simpleRegexSettings.add(allRegex = new GuiCheckBox(10, x - 100, y + 15, I18n.format("wynntils.chat.tabgui.button.message_type.all"), false));
        simpleRegexSettings.add(localRegex = new GuiCheckBox(11, x, y + 15, I18n.format("wynntils.chat.tabgui.button.message_type.local"), false));
        simpleRegexSettings.add(guildRegex = new GuiCheckBox(12, x - 100, y + 30, I18n.format("wynntils.chat.tabgui.button.message_type.guild"), false));
        simpleRegexSettings.add(partyRegex = new GuiCheckBox(13,  x , y + 30, I18n.format("wynntils.chat.tabgui.button.message_type.party"), false));
        simpleRegexSettings.add(shoutsRegex = new GuiCheckBox(14, x - 100, y + 45, I18n.format("wynntils.chat.tabgui.button.message_type.shouts"), false));
        simpleRegexSettings.add(pmRegex = new GuiCheckBox(15, x, y + 45, I18n.format("wynntils.chat.tabgui.button.message_type.pms"), false));
        buttonList.addAll(simpleRegexSettings);
        applyRegexSettings();
        //Advanced
        regexTextField = new GuiTextField(3, mc.fontRenderer, x - 100, y + 20, 200, 20);
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
        nameLabel.addLine(I18n.format("wynntils.chat.tabgui.button.name") + TextFormatting.RED + " *");
        labelList.add(regexLabel = new GuiLabel(mc.fontRenderer, 1, x - 100, y + 5, 10, 10, 0xFFFFFF));
        regexLabel.addLine(I18n.format("wynntils.chat.tabgui.button.regex") + TextFormatting.RED + " *");
        regexLabel.visible = false;
        labelList.add(autoCommand = new GuiLabel(mc.fontRenderer, 2, x - 110, y - 65, 10, 10, 0xFFFFFF));
        autoCommand.addLine(I18n.format("wynntils.chat.tabgui.button.auto_command"));
        labelList.add(orderNb = new GuiLabel(mc.fontRenderer, 3, x + 85, y - 65, 10, 10, 0xFFFFFF));
        orderNb.addLine(I18n.format("wynntils.chat.tabgui.button.order_number"));
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
            }, TextFormatting.WHITE + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.menu.confirm_delete_main")), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.confirm_delete_sub"), 0));
        } else if (button == advancedButton) {
            boolean simple;
            if (button.displayString.equals(I18n.format("wynntils.chat.tabgui.button.advanced_show"))) {
                button.displayString = I18n.format("wynntils.chat.tabgui.button.advanced_hide");
                simple = false;
            } else {
                button.displayString = I18n.format("wynntils.chat.tabgui.button.advanced_show");
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
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.name")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.name.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.name.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.name.tooltip_3"), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.generic.required")), mouseX, mouseY);

        if(regexTextField.getVisible() && mouseX >= regexTextField.x && mouseX < regexTextField.x + regexTextField.width && mouseY >= regexTextField.y && mouseY < regexTextField.y + regexTextField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.regex")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.regex.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.regex.tooltip_2"), TextFormatting.GREEN + I18n.format("wynntils.chat.tabgui.button.regex.tooltip_3"), TextFormatting.GOLD + I18n.format("wynntils.chat.tabgui.button.regex.tooltip_4"), "", TextFormatting.RED + I18n.format("wynntils.chat.tabgui.generic.required")), mouseX, mouseY);

        if(mouseX >= autoCommandField.x && mouseX < autoCommandField.x + autoCommandField.width && mouseY >= autoCommandField.y && mouseY < autoCommandField.y + autoCommandField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.auto_command")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.auto_command.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.auto_command.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.auto_command.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.auto_command.tooltip_4"), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.generic.optional")), mouseX, mouseY);

        if(mouseX >= orderNbField.x && mouseX < orderNbField.x + orderNbField.width && mouseY >= orderNbField.y && mouseY < orderNbField.y + orderNbField.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.order_number_alt")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.order_number.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.order_number.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.order_number.tooltip_3"), TextFormatting.DARK_GRAY + I18n.format("wynntils.chat.tabgui.button.order_number.tooltip_4"), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.generic.optional")), mouseX, mouseY);

        if(mouseX >= lowPriority.x && mouseX < lowPriority.x + lowPriority.width && mouseY >= lowPriority.y && mouseY < lowPriority.y + lowPriority.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.low_priority")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_4"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_5"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_6"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.low_priority.tooltip_7"), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.generic.optional")), mouseX, mouseY);

        if (advancedButton.displayString.equals(I18n.format("wynntils.chat.tabgui.button.advanced_show"))) {
            if (mouseX >= allRegex.x && mouseX < allRegex.x + allRegex.width && mouseY >= allRegex.y && mouseY < allRegex.y + allRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.all.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.all.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.all.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.all.tooltip_4")), mouseX, mouseY);
            } else if (mouseX >= localRegex.x && mouseX < localRegex.x + localRegex.width && mouseY >= localRegex.y && mouseY < localRegex.y + localRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.local.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.local.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.local.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.local.tooltip_4")), mouseX, mouseY);
            } else if (mouseX >= guildRegex.x && mouseX < guildRegex.x + guildRegex.width && mouseY >= guildRegex.y && mouseY < guildRegex.y + guildRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.guild.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.guild.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.guild.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.guild.tooltip_4")), mouseX, mouseY);
            } else if (mouseX >= partyRegex.x && mouseX < partyRegex.x + partyRegex.width && mouseY >= partyRegex.y && mouseY < partyRegex.y + partyRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.party.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.party.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.party.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.party.tooltip_4")), mouseX, mouseY);
            } else if (mouseX >= shoutsRegex.x && mouseX < shoutsRegex.x + shoutsRegex.width && mouseY >= shoutsRegex.y && mouseY < shoutsRegex.y + shoutsRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.shouts.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.shouts.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.shouts.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.shouts.tooltip_4")), mouseX, mouseY);
            } else if (mouseX >= pmRegex.x && mouseX < pmRegex.x + pmRegex.width && mouseY >= pmRegex.y && mouseY < pmRegex.y + pmRegex.height) {
                drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.message_type.pms.tooltip_1")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.pms.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.pms.tooltip_3"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.message_type.pms.tooltip_4")), mouseX, mouseY);
            }
        }

        if(saveButton.enabled && mouseX >= saveButton.x && mouseX < saveButton.x + saveButton.width && mouseY >= saveButton.y && mouseY < saveButton.y + saveButton.height)
            drawHoveringText(Arrays.asList(TextFormatting.GREEN + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.save")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.save.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.save.tooltip_2")), mouseX, mouseY);

        if(deleteButton.enabled && mouseX >= deleteButton.x && mouseX < deleteButton.x + deleteButton.width && mouseY >= deleteButton.y && mouseY < deleteButton.y + deleteButton.height)
            drawHoveringText(Arrays.asList(TextFormatting.DARK_RED + (TextFormatting.BOLD + I18n.format("wynntils.chat.tabgui.button.delete")), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.delete.tooltip_1"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.delete.tooltip_2"), TextFormatting.GRAY + I18n.format("wynntils.chat.tabgui.button.delete.tooltip_3"), TextFormatting.RED + I18n.format("wynntils.chat.tabgui.button.delete.tooltip_4")), mouseX, mouseY);

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
        if (advancedButton.displayString.equals(I18n.format("wynntils.chat.tabgui.button.advanced_hide"))) return null;

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
        if (advancedButton.displayString.equals(I18n.format("wynntils.chat.tabgui.button.advanced_hide"))) return "";

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
