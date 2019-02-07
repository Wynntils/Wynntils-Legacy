package com.wynntils.modules.map.overlays.ui;

import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldMapSettingsUI extends GuiScreen {
    private HashMap<String, Boolean> enabledMapIcons;

    public WorldMapSettingsUI() {
        enabledMapIcons = MapConfig.INSTANCE.enabledMapIcons;
    }

    @Override
    public void initGui() {
        int i = 0;

        for (Map.Entry<String, Boolean> icon: enabledMapIcons.entrySet()) {
            this.buttonList.add(new GuiCheckBox(i, 10 + (this.width-369)/2 + (i%3) * 122, 35 + (i/3) * 15, stripJargon(icon.getKey()), icon.getValue()));
            i++;
        }

        GuiButton cancelBtn = new GuiButton(100, this.width/2 - 71, Math.max(this.height-50, (50 + (i/3) * 15)), 45, 18, "Cancel");
        GuiButton resetBtn = new GuiButton(101, this.width/2 - 23, Math.max(this.height-50, (50 + (i/3) * 15)), 45, 18, "Reset");
        resetBtn.enabled = false;
        GuiButton saveBtn = new GuiButton(102, this.width/2 + 25, Math.max(this.height-50, (50 + (i/3) * 15)), 45, 18, "Save");
        this.buttonList.add(cancelBtn);
        this.buttonList.add(resetBtn);
        this.buttonList.add(saveBtn);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() || // DEFAULT: E
                keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) { //DEFAULT: M
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapOverlay());
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) {
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapOverlay());
        } else if (button.id == 102) {
            HashMap<String, Boolean> tempEnabled = new HashMap<String, Boolean>();
            for (GuiButton cb: this.buttonList) {
                if (cb instanceof GuiCheckBox) {
                    tempEnabled.put(addJargon(cb.displayString), ((GuiCheckBox) cb).isChecked());
                }
            }
            MapConfig.INSTANCE.enabledMapIcons.clear();
            MapConfig.INSTANCE.enabledMapIcons.putAll(tempEnabled);
            MapConfig.INSTANCE.saveSettings(MapModule.getModule());
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapOverlay());
        } else if (button.id == 101) {
            //TODO resetbutton
        }
    }

    private  String stripJargon(String s) {
        s = s.replace("Special_","§r§r");
        s = s.replace("Content_","§r");
        s = s.replace("NPC_","§f");
        s = s.replace("_", " ");
        return s;
    }

    private String addJargon(String s) {
        s = s.replace("§r§r", "Special_");
        s = s.replace("§r", "Content_");
        s = s.replace("§f", "NPC_");
        s = s.replace(" ", "_");
        return s;
    }
}