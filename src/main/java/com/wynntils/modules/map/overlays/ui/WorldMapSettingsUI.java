package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldMapSettingsUI extends GuiScreen {
    private HashMap<String, Boolean> enabledMapIcons, availableMapIcons;
    private int offsetY, realOffsetY = 0;
    private final int SETTINGS_HEIGHT = -40;
    private MapConfig.IconTexture selectedTexture;

    public WorldMapSettingsUI() {
        enabledMapIcons = MapConfig.INSTANCE.enabledMapIcons;
        availableMapIcons = MapConfig.INSTANCE.resetMapIcons();
        selectedTexture = MapConfig.INSTANCE.iconTexture;
    }

    @Override
    public void initGui() {
        int i = 0;
        int rightAlign = 10 + (this.width - 369) / 2;

        for (Map.Entry<String, Boolean> icon : availableMapIcons.entrySet()) {
            this.buttonList.add(new GuiCheckBox(i, rightAlign + (i % 3) * 122, 35 + (i / 3) * 15, icon.getKey(), enabledMapIcons.containsKey(icon.getKey()) ? enabledMapIcons.get(icon.getKey()) : icon.getValue()));
            i++;
        }

        this.buttonList.add(new GuiButton(99, rightAlign + 120, 205, 100, 18, I18n.format(selectedTexture.displayName)));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 200, this.height - 40, 120, 18, I18n.format("wynntils.map.ui.world_map_settings.buttons.cancel")));
        this.buttonList.add(new GuiButton(101, this.width / 2 - 70, this.height - 40, 120, 18, I18n.format("wynntils.map.ui.world_map_settings.buttons.default")));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 60, this.height - 40, 120, 18, I18n.format("wynntils.map.ui.world_map_settings.buttons.save")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        for (GuiButton b : this.buttonList) {
            if (b.id < 100) {
                b.y += offsetY;
                if (b.y < 10 || b.y > this.height - 50) {
                    b.visible = false;
                } else {
                    b.visible = true;
                }
            }
        }
        int topY = this.buttonList.get(0).y;
        if (topY - 15 > 10 && topY - 15 < this.height - 50) {
            this.fontRenderer.drawString(TextFormatting.WHITE + I18n.format("wynntils.map.ui.world_map_settings.floating.enable_or_disable"), (this.width - 349) / 2, topY - 15, 0xffFFFFFF);
        }
        if (topY + 172 > 10 && topY + 172 < this.height - 50) {
            this.fontRenderer.drawString(TextFormatting.WHITE + I18n.format("wynntils.map.ui.world_map_settings.floating.textures"), (this.width - 349) / 2, topY + 175, 0xffFFFFFF);
        }
        realOffsetY += offsetY;
        offsetY = 0;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() || // DEFAULT: E
                keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) { //DEFAULT: M
            Utils.displayGuiScreen(new MainWorldMapUI());
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 99) {
            selectedTexture = MapConfig.IconTexture.values()[(selectedTexture.ordinal() + 1) % MapConfig.IconTexture.values().length];
            button.displayString = I18n.format(selectedTexture.displayName);
        } else if (button.id == 100) {
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button.id == 102) {
            MapConfig.INSTANCE.enabledMapIcons = MapConfig.INSTANCE.resetMapIcons();
            for (GuiButton cb : this.buttonList) {
                if (cb instanceof GuiCheckBox && MapConfig.INSTANCE.enabledMapIcons.containsKey(cb.displayString)) {
                    MapConfig.INSTANCE.enabledMapIcons.replace(cb.displayString, ((GuiCheckBox) cb).isChecked());
                } else if (cb.id == 99) {
                    selectedTexture = MapConfig.IconTexture.values()[MapConfig.IconTexture.valueOf(cb.displayString).ordinal()];
                }
            }
            MapConfig.INSTANCE.iconTexture = selectedTexture;
            MapConfig.INSTANCE.saveSettings(MapModule.getModule());
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button.id == 101) {
            this.enabledMapIcons = MapConfig.INSTANCE.resetMapIcons();
            for (GuiButton b : this.buttonList) {
                if (b instanceof GuiCheckBox && this.enabledMapIcons.containsKey(b.displayString)) {
                    ((GuiCheckBox) b).setIsChecked(this.enabledMapIcons.get(b.displayString));
                } else if (b.id == 99) {
                    selectedTexture = MapConfig.IconTexture.Classic;
                    b.displayString = I18n.format(selectedTexture.displayName);
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        float i = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
        if (i != 0) {
            i = MathHelper.clamp(i, -1, 1) * 10;
            offsetY = (int) i;
            if ((realOffsetY + offsetY) < SETTINGS_HEIGHT || (realOffsetY + offsetY) > 0) {
                offsetY = 0;
            }
        }
    }
}
