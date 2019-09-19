package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.configs.MapConfig.IconTexture;
import com.wynntils.modules.map.overlays.objects.MapApiIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;

public class WorldMapSettingsUI extends GuiScreen {

    private HashMap<String, Boolean> enabledMapIcons, enabledMinimapIcons;
    private int page = 0;
    private int maxPage;
    private ArrayList<Button> settingButtons = new ArrayList<>();
    private GuiButton textureButton, nextPageButton, previousPageButton;

    public WorldMapSettingsUI() {
        enabledMapIcons = MapConfig.INSTANCE.resetMapIcons(false);
        enabledMinimapIcons = MapConfig.INSTANCE.resetMapIcons(true);
        for (String key : enabledMapIcons.keySet()) {
            Boolean fromMapConfig = MapConfig.INSTANCE.enabledMapIcons.getOrDefault(key, null);
            Boolean fromMinimapConfig = MapConfig.INSTANCE.enabledMinimapIcons.getOrDefault(key, null);
            if (fromMapConfig != null) enabledMapIcons.put(key, fromMapConfig);
            if (fromMinimapConfig != null) enabledMinimapIcons.put(key, fromMinimapConfig);
        }
    }

    @Override
    public void initGui() {
        int rightAlign = 7 + (this.width-399)/2;
        int yOffset = 35;
        int maxHeight = Math.max(this.height - 90, yOffset + 17);
        page = 0;
        maxPage = 0;

        settingButtons.clear();
        settingButtons.ensureCapacity(2 * enabledMapIcons.size());

        ArrayList<String> keys = new ArrayList<>(enabledMapIcons.keySet());
        keys.sort(Comparator.<String, Float>comparing(i -> {
            MapApiIcon icon = MapApiIcon.getFree(i, IconTexture.Classic);
            return -icon.getSizeX() * icon.getSizeZ();
        }).thenComparing(i -> StringUtils.countMatches(i, ' ')).thenComparing(StringUtils::reverse));

        for (int i = 0, keysSize = keys.size(); i < keysSize; ++i) {
            int x = rightAlign + (i%3) * 137;
            int y = yOffset + (i/3) * 20;
            if (y + 16 > maxHeight) {
                yOffset = 35 - (i/3) * 20;
                y = 35;
                ++maxPage;
            }
            String key = keys.get(i);
            Button button = new Button(i, x, y, 132, 16, key, key, maxPage, MapConfig.INSTANCE.iconTexture, enabledMapIcons.get(key), enabledMinimapIcons.get(key));
            this.buttonList.add(button);
            this.settingButtons.add(button);
        }

        this.buttonList.add(textureButton = new GuiButton(99, rightAlign + 120, this.height-65, 55, 18, MapConfig.INSTANCE.iconTexture.name()));
        this.buttonList.add(new GuiButton(100, this.width/2 - 71, this.height-40, 45, 18, "Cancel"));
        this.buttonList.add(new GuiButton(101, this.width/2 - 23, this.height-40, 45, 18, "Default"));
        this.buttonList.add(new GuiButton(102, this.width/2 + 25, this.height-40, 45, 18, "Save"));
        this.buttonList.add(nextPageButton = new GuiButton(103, this.width/2 + 2, this.height - 90, 20, 20, ">"));
        this.buttonList.add(previousPageButton = new GuiButton(104, this.width/2 - 22, this.height - 90, 20, 20, "<"));
        nextPageButton.enabled = maxPage > 0;
        previousPageButton.enabled = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int topY = this.buttonList.get(0).y;
        this.fontRenderer.drawString(TextFormatting.WHITE + "Enable/Disable Map Icons", (this.width - 349) / 2, topY - 15, 0xffFFFFFF);
        this.fontRenderer.drawString(TextFormatting.WHITE + "Map Icon Textures:", (this.width - 349) / 2, this.height-60, 0xffFFFFFF);

        // Draw labels rotated 45 degrees
        GlStateManager.pushMatrix();
        GlStateManager.translate((this.width-399)/2 + 286, 29f, 0f);
        GlStateManager.rotate(-45, 0, 0, 1);
        this.fontRenderer.drawString("Main map", 0, 0, 0xFFFFFFFF);
        // This rotate->translate->rotate could become one translate
        // but I'm too lazy to do linear algebra
        GlStateManager.rotate(45, 0, 0, 1);
        GlStateManager.translate(14f, 3f, 0f);
        GlStateManager.rotate(-45, 0, 0, 1);
        this.fontRenderer.drawString("Minimap", 0, 0, 0xFFFFFFFF);
        GlStateManager.popMatrix();

        ScreenRenderer.beginGL(0, 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScreenRenderer.endGL();

        for (Button btn : settingButtons) {
            if (btn.isMouseOver()) {
                String visiblePrefix = TextFormatting.GREEN + "Visible";
                String invisiblePrefix = TextFormatting.GRAY + "Not visible";
                drawHoveringText(Arrays.asList(
                    btn.displayString,
                    (btn.onMainMap() ? visiblePrefix : invisiblePrefix) + TextFormatting.RESET + " on main map",
                    (btn.onMinimap() ? visiblePrefix : invisiblePrefix) + TextFormatting.RESET + " on minimap"
                ), mouseX, mouseY, fontRenderer);
            }
        }
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 || mouseButton == 1) {
            for (Button btn : settingButtons) {
                if (btn.mousePressed(mc, mouseX, mouseY)) {
                    btn.toggle(mouseX, mouseY, mouseButton == 1);
                    selectedButton = btn;
                    return;
                }
            }

            if (textureButton.mousePressed(mc, mouseX, mouseY)) {
                selectedButton = textureButton;
                int delta = mouseButton == 0 ? 1 : IconTexture.values().length - 1;
                IconTexture newTexture = IconTexture.values()[(IconTexture.valueOf(textureButton.displayString).ordinal() + delta) % IconTexture.values().length];
                textureButton.displayString = newTexture.name();
                for (Button btn : settingButtons) {
                    btn.updateTexture(newTexture);
                }
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 100) {
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button.id == 102) {
            MapConfig.INSTANCE.enabledMapIcons = MapConfig.INSTANCE.resetMapIcons(false);
            MapConfig.INSTANCE.enabledMinimapIcons = MapConfig.INSTANCE.resetMapIcons(true);
            for (GuiButton cb : this.buttonList) {
                if (cb instanceof Button) {
                    MapConfig.INSTANCE.enabledMapIcons.put(((Button) cb).key, ((Button) cb).onMainMap());
                    MapConfig.INSTANCE.enabledMinimapIcons.put(((Button) cb).key, ((Button) cb).onMinimap());
                } else if (cb.id == 99) {
                    MapConfig.INSTANCE.iconTexture = IconTexture.values()[IconTexture.valueOf(cb.displayString).ordinal()];
                }
            }
            MapConfig.INSTANCE.saveSettings(MapModule.getModule());
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (button.id == 101) {
            this.enabledMapIcons = MapConfig.INSTANCE.resetMapIcons(false);
            this.enabledMinimapIcons = MapConfig.INSTANCE.resetMapIcons(true);
            page = 0;
            for (GuiButton b : this.buttonList) {
                if (b instanceof Button) {
                    Button btn = (Button) b;
                    btn.updatePage(0);
                    btn.updateTexture(IconTexture.Classic);
                    btn.setOnMainMap(enabledMapIcons.get(btn.key));
                    btn.setOnMinimap(enabledMinimapIcons.get(btn.key));
                } else if (b.id == 99) {
                    b.displayString = "Classic";
                }
            }
        } else if (button == nextPageButton || button == previousPageButton) {
            changePage(button == nextPageButton ? 1 : -1);
        }
    }

    private void changePage(int by) {
        page = Math.max(Math.min(page + by, maxPage), 0);
        nextPageButton.enabled = page != maxPage;
        previousPageButton.enabled = page != 0;
        for (Button btn : settingButtons) {
            btn.updatePage(page);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mDwehll = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
        if (mDwehll != 0) {
            changePage(mDwehll < 0 ? +1 : -1);
        }
    }

    private static final int ON_MAINMAP = 0b01;
    private static final int ON_MINIMAP = 0b10;

    private static class Button extends GuiButton {

        static final ScreenRenderer renderer = new ScreenRenderer();

        String key;
        int selectionState = 0;
        int page;
        MapApiIcon icon;
        float mainScale;
        float miniScale;
        CustomColor disabledColour = new CustomColor(CommonColors.GRAY).setA(0.5f);

        Button(int id, int xPos, int yPos, int width, int height, String displayString, String key, int page, IconTexture tex, boolean onMainMap, boolean onMiniMap) {
            super(id, xPos, yPos, width, height, displayString);
            this.key = key;
            this.page = page;
            if (onMainMap) selectionState |= ON_MAINMAP;
            if (onMiniMap) selectionState |= ON_MINIMAP;
            updatePage(0);
            updateTexture(tex);
        }

        boolean onMainMap() {
            return (selectionState & ON_MAINMAP) != 0;
        }

        boolean onMinimap() {
            return (selectionState & ON_MINIMAP) != 0;
        }

        void setOnMainMap(boolean onMainMap) {
            if (onMainMap) {
                selectionState |= ON_MAINMAP;
            } else {
                selectionState &= ~ON_MAINMAP;
            }
        }

        void setOnMinimap(boolean onMinimap) {
            if (onMinimap) {
                selectionState |= ON_MINIMAP;
            } else {
                selectionState &= ~ON_MINIMAP;
            }
        }

        void toggle(int mouseX, int mouseY, boolean isRightClick) {
            int relX = mouseX - this.x;
            if (relX <= height) {
                // Clicked on main map icon
                setOnMainMap(!onMainMap());
                return;
            }
            if (relX <= height * 1.75f) {
                // Clicked on mini map icon
                setOnMinimap(!onMinimap());
                return;
            }

            // Clicked on text
            if (isRightClick) {
                selectionState = selectionState == 0 ? 0b11 : 0;
            } else {
                selectionState = selectionState == 0b11 ? 0 : 0b11;
            }
        }

        void updatePage(int newPage) {
            this.visible = newPage == page;
        }

        void updateTexture(IconTexture newTexture) {
            icon = MapApiIcon.getFree(key, newTexture);
            mainScale = height * 0.5f / Math.max(icon.getSizeX(), icon.getSizeZ());
            miniScale = (height * 0.25f) / Math.max(icon.getSizeX(), icon.getSizeZ());
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) {
                this.hovered = false;
                return;
            }

            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            if (!onMainMap()) {
                disabledColour.applyColor();
            }
            icon.renderAt(renderer, x + (height / 2f), y + (height / 2f), mainScale, 1);

            (onMinimap() ? CommonColors.WHITE : disabledColour).applyColor();
            icon.renderAt(renderer, x + height + (height * 0.375f), y + (height / 2f), miniScale, 1);
            CommonColors.WHITE.applyColor();

            this.drawString(mc.fontRenderer, displayString, this.x + (int) (height * 1.75f) + 2, this.y + (this.height - mc.fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFFFF);
        }

    }

}
