package cf.wynntils.modules.chat.overlays.gui;

import cf.wynntils.modules.chat.instances.ChatTab;
import cf.wynntils.modules.chat.managers.TabManager;
import cf.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;
import java.util.regex.Pattern;


public class TabGUI extends GuiScreen {

    int id;
    ChatTab tab;

    public TabGUI(int id) {
        this.id = id;

        if(id != -2)
            tab = TabManager.getTabById(id);
    }

    //gui things
    GuiButton saveButton;
    GuiButton deleteButton;
    GuiButton closeButton;
    GuiCheckBox lowPriority;
    GuiTextField nameTextField;
    GuiTextField regexTextField;
    GuiTextField autoCommandField;

    //labels
    GuiLabel nameLabel;
    GuiLabel regexLabel;
    GuiLabel autoCommand;

    @Override
    public void initGui() {
        labelList.clear();

        int x = width / 2; int y = height / 2;

        buttonList.add(saveButton = new GuiButton(0, x - 90, y + 15, 40, 20, "§aSave"));
        buttonList.add(deleteButton = new GuiButton(1, x - 45, y + 15, 40, 20, "§4Delete"));
        buttonList.add(closeButton = new GuiButton(2, x + 50, y + 15, 40, 20, "§fClose"));

        deleteButton.enabled = (id != -2) && TabManager.getAvailableTabs().size() > 1;

        buttonList.add(lowPriority = new GuiCheckBox(3, x - 90, y, "Low Priority", false));

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

            checkIfRegexIsValid();
        }

        labelList.add(nameLabel = new GuiLabel(mc.fontRenderer, 0, x - 90, y - 85, 10, 10, 0xFFFFFF));
        nameLabel.addLine("Name");
        labelList.add(regexLabel = new GuiLabel(mc.fontRenderer, 1, x - 90, y - 40, 10, 10, 0xFFFFFF));
        regexLabel.addLine("Regex");
        labelList.add(autoCommand = new GuiLabel(mc.fontRenderer, 2, x + 10, y - 85, 10, 10, 0xFFFFFF));
        autoCommand.addLine("Auto Command");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if(button == closeButton) mc.displayGuiScreen(new ChatGUI());
        else if(button == saveButton) {
            if (id == -2)
                TabManager.registerNewTab(new ChatTab(nameTextField.getText(), regexTextField.getText(), autoCommandField.getText(), lowPriority.isChecked()));
            else
                TabManager.updateTab(id, nameTextField.getText(), regexTextField.getText(), autoCommandField.getText(), lowPriority.isChecked());
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
            }, "§f§lDo you really want to delete this chat tab?", "§cThis action is irreversible!", 0));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(nameTextField != null) nameTextField.drawTextBox();
        if(regexTextField != null) regexTextField.drawTextBox();
        if(autoCommandField != null) autoCommandField.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        regexTextField.mouseClicked(mouseX, mouseY, mouseButton);
        nameTextField.mouseClicked(mouseX, mouseY, mouseButton);
        autoCommandField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        nameTextField.textboxKeyTyped(typedChar, keyCode);
        autoCommandField.textboxKeyTyped(typedChar, keyCode);
        if(regexTextField.textboxKeyTyped(typedChar, keyCode)) checkIfRegexIsValid();
    }


    private void checkIfRegexIsValid() {
        try{
            Pattern.compile(regexTextField.getText());
            regexTextField.setTextColor(0x55FF55);
            saveButton.enabled = true;
            return;
        }catch (Exception ignored) { }

        regexTextField.setTextColor(0xFF5555);
        saveButton.enabled = false;
    }
}
