/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.settings.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.SettingsContainer;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.framework.ui.elements.UIEButton;
import com.wynntils.core.framework.ui.elements.UIEClickZone;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlayPositionsUI extends UI {

    private GuiScreen parentScreen;

    private List<OverlayButton> registeredOverlaySettings = new ArrayList<>();

    private StringDrawing stringToDrawOnTop;

    private OverlayButton toClick;

    private OverlayButton selected = null;

    private long clickTime = 0;

    private boolean reloadButtons, shiftDown;

    public int GRID_SIZE_VERTICAL, GRID_SIZE_HORIZONTAL;

    public UIEButton cancelButton = new UIEButton("Cancel", Textures.UIs.button_a, 0.5f, 0.5f, 13, 0, -10, true, (ui, mouseButton) -> {
        for (OverlayButton settingsContainer : registeredOverlaySettings) {
            try {
                settingsContainer.getOverlaySettings().tryToLoad();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        onClose();
    }, 0, 0, 17, 45);

    public UIEButton applyButton = new UIEButton("Apply", Textures.UIs.button_a, 0.5f, 0.5f, -48, 0, -10, true, (ui, mouseButton) -> {
        for (OverlayButton settingsContainer : registeredOverlaySettings) {
            if (!settingsContainer.getOverlaySettings().isReset()) {
                // Convert offset -> anchor, then save
                Overlay overlay = ((Overlay) settingsContainer.getOverlaySettings().getHolder());
                overlay.position.anchorX = (float) (((overlay.position.anchorX * ScreenRenderer.screen.getScaledWidth_double()) + (double) overlay.position.offsetX) / ScreenRenderer.screen.getScaledWidth_double());
                overlay.position.anchorY = (float) (((overlay.position.anchorY * ScreenRenderer.screen.getScaledHeight_double()) + (double) overlay.position.offsetY) / ScreenRenderer.screen.getScaledHeight_double());
                overlay.position.offsetX = 0;
                overlay.position.offsetY = 0;
            }
            try {
                settingsContainer.getOverlaySettings().saveSettings();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        onClose();
    }, 0, 0, 17, 45);

    public UIEButton resetButton = new UIEButton("Default", Textures.UIs.button_a, 0.5f, 0.5f, -22, 15, -10, true, (ui, mouseButton) -> {
        for (OverlayButton settingsContainer : registeredOverlaySettings) {
            try {
                settingsContainer.getOverlaySettings().resetValues();
                reloadButtons = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }, 0, 0, 17, 45);

    public OverlayPositionsUI(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void onInit() {
        registeredOverlaySettings.clear();

        for (ModuleContainer moduleContainer : FrameworkManager.getAvailableModules().values()) {
            for (SettingsContainer settingsContainer : moduleContainer.getRegisteredSettings().values()) {
                if (settingsContainer.getHolder() instanceof Overlay) {
                    if (((Overlay) settingsContainer.getHolder()).growth != null) {
                        registeredOverlaySettings.add(new OverlayButton(settingsContainer));
                    }
                }
            }
        }

        reloadButtons = false;
        shiftDown = false;
        GRID_SIZE_VERTICAL = ScreenRenderer.screen.getScaledHeight() / 50;
        GRID_SIZE_HORIZONTAL = ScreenRenderer.screen.getScaledWidth() / 50;
    }

    @Override
    public void onClose() {
        McIf.mc().currentScreen = null;
        McIf.mc().displayGuiScreen(parentScreen);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onRenderPreUIE(ScreenRenderer render) {
        drawDefaultBackground();

        for (UIEClickZone zone : registeredOverlaySettings) {
            zone.render(mouseX, mouseY);
        }
        if (stringToDrawOnTop != null) {
            if (stringToDrawOnTop.y > ScreenRenderer.screen.getScaledHeight()) {
                stringToDrawOnTop.y = ScreenRenderer.screen.getScaledHeight() - 12;
            } else if (stringToDrawOnTop.y < 0) {
                stringToDrawOnTop.y = 1;
            }
            int widthOfString = ScreenRenderer.fontRenderer.getStringWidth(stringToDrawOnTop.string);
            if (stringToDrawOnTop.x - widthOfString / 2 < 0) {
                stringToDrawOnTop.x = 1;
                stringToDrawOnTop.alignment = SmartFontRenderer.TextAlignment.LEFT_RIGHT;
            } else if (stringToDrawOnTop.x + (widthOfString / 2) > ScreenRenderer.screen.getScaledWidth()) {
                stringToDrawOnTop.x = ScreenRenderer.screen.getScaledWidth() - 1;
                stringToDrawOnTop.alignment = SmartFontRenderer.TextAlignment.RIGHT_LEFT;
            }
            render.drawString(stringToDrawOnTop.string, stringToDrawOnTop.x, stringToDrawOnTop.y, stringToDrawOnTop.color, stringToDrawOnTop.alignment, stringToDrawOnTop.textShadow);
        }
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {
        if (reloadButtons || (shiftDown && !Keyboard.isKeyDown(42)))
            onInit();
    }

    @Override
    public void onWindowUpdate() {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton > 3) {
            mouseButton = 3; // Set to UNKNOWN
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (toClick != null) {
            clickTime = System.currentTimeMillis();
            selected = null;
            toClick.click(mouseX, mouseY, MouseButton.values()[mouseButton], this);
        } else {
            selected = null;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state > 3) {
            state = 3; // Set to UNKNOWN
        }
        super.mouseReleased(mouseX, mouseY, state);
        for (OverlayButton button : registeredOverlaySettings) {
            if (button.isMouseButtonHeld()) {
                button.release(mouseX, mouseY, MouseButton.values()[state], this);
                if (System.currentTimeMillis() - clickTime < 200) {
                    selected = button;
                }
                break;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton > 3) {
            clickedMouseButton = 3; // Set to UNKNOWN
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (OverlayButton button : registeredOverlaySettings) {
            if (button.isMouseButtonHeld()) {
                button.clickMove(mouseX, mouseY, MouseButton.values()[clickedMouseButton], timeSinceLastClick, this);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            for (OverlayButton settingsContainer : registeredOverlaySettings) {
                try {
                    settingsContainer.getOverlaySettings().tryToLoad();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        super.keyTyped(typedChar, keyCode);
        if (selected != null) {
            if (keyCode == 200) {
                selected.position.offsetY -= 1;
                ((Overlay) selected.getOverlaySettings().getHolder()).position.offsetY -= 1;
            } else if (keyCode == 208) {
                selected.position.offsetY += 1;
                ((Overlay) selected.getOverlaySettings().getHolder()).position.offsetY += 1;
            } else if (keyCode == 203) {
                selected.position.offsetX -= 1;
                ((Overlay) selected.getOverlaySettings().getHolder()).position.offsetX -= 1;
            } else if (keyCode == 205) {
                selected.position.offsetX += 1;
                ((Overlay) selected.getOverlaySettings().getHolder()).position.offsetX += 1;
            }
        }
        if (keyCode == 42) {
            shiftDown = true;
        }
    }

    private class OverlayButton extends UIEClickZone {

        public SettingsContainer overlaySettings;

        boolean mouseButtonHeld = false;

        int mouseXPrevious, mouseYPrevious;

        public OverlayButton(SettingsContainer overlaySettings) {
            super(((Overlay) overlaySettings.getHolder()).position.anchorX,
                    ((Overlay) overlaySettings.getHolder()).position.anchorY,
                    ((Overlay) overlaySettings.getHolder()).position.offsetX,
                    ((Overlay) overlaySettings.getHolder()).position.offsetY,
                    ((Overlay) overlaySettings.getHolder()).staticSize.x,
                    ((Overlay) overlaySettings.getHolder()).staticSize.y,
                    true,
                    null);
            this.overlaySettings = overlaySettings;
            Overlay overlay = ((Overlay) overlaySettings.getHolder());
            if (overlay.growth == Overlay.OverlayGrowFrom.TOP_CENTRE) {
                this.position.offsetX -= width / 2;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.TOP_RIGHT) {
                this.position.offsetX -= width;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_LEFT) {
                this.position.offsetY -= height / 2;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_CENTRE) {
                this.position.offsetX -= width / 2;
                this.position.offsetY -= height / 2;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_RIGHT) {
                this.position.offsetX -= width;
                this.position.offsetY -= height / 2;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_LEFT) {
                this.position.offsetY -= height;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_CENTRE) {
                this.position.offsetX -= width / 2;
                this.position.offsetY -= height;
            } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_RIGHT) {
                this.position.offsetX -= width;
                this.position.offsetY -= height;
            }
        }

        @Override
        public void render(int mouseX, int mouseY) {
            super.render(mouseX, mouseY);
            position.refresh();
            // Clickable box
            CustomColor color;
            Overlay overlay = (Overlay) overlaySettings.getHolder();
            if (selected != null && selected.equals(this)) {
                color = CommonColors.BLUE;
            } else if (mouseButtonHeld && shiftDown) {
                color = CommonColors.PURPLE;
            } else if (mouseButtonHeld) {
                color = CommonColors.ORANGE;
            } else if (hovering) {
                color = CommonColors.YELLOW;
            } else if (overlay.active) {
                color = CommonColors.GREEN;
            } else {
                color = CommonColors.RED;
            }
            drawRect(color, position.getDrawingX(), position.getDrawingY(), position.getDrawingX() + width, position.getDrawingY() + 1);
            drawRect(color, position.getDrawingX(), position.getDrawingY() + height, position.getDrawingX() + width, position.getDrawingY() + height - 1);
            drawRect(color, position.getDrawingX(), position.getDrawingY(), position.getDrawingX() + 1, position.getDrawingY() + height);
            drawRect(color, position.getDrawingX() + width, position.getDrawingY(), position.getDrawingX() + width - 1, position.getDrawingY() + height);
            // Text
            if (hovering && !mouseButtonHeld) {
                stringToDrawOnTop = new StringDrawing(overlay.displayName,
                        position.getDrawingX() + (overlay.staticSize.x / 2),
                        position.getDrawingY() + (overlay.staticSize.y / 2) - 4,
                        CommonColors.PURPLE,
                        SmartFontRenderer.TextAlignment.MIDDLE,
                        SmartFontRenderer.TextShadow.OUTLINE);
                toClick = this;
            } else {
                if (stringToDrawOnTop != null && stringToDrawOnTop.getString().equals(overlay.displayName)) {
                    stringToDrawOnTop = null;
                    toClick = null;
                }
            }
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            super.click(mouseX, mouseY, button, ui);
            if (button == MouseButton.LEFT) {
                mouseButtonHeld = true;
                mouseXPrevious = mouseX;
                mouseYPrevious = mouseY;
            } else if (button == MouseButton.RIGHT) {
                ((Overlay) overlaySettings.getHolder()).active = !((Overlay) overlaySettings.getHolder()).active;
            }
        }

        @Override
        public void release(int mouseX, int mouseY, MouseButton button, UI ui) {
            super.release(mouseX, mouseY, button, ui);
            if (button == MouseButton.LEFT) {
                mouseButtonHeld = false;
            }
        }

        @Override
        public void clickMove(int mouseX, int mouseY, MouseButton button, long timeSinceLastClick, UI ui) {
            super.clickMove(mouseX, mouseY, button, timeSinceLastClick, ui);
            position.offsetX = position.offsetX + (mouseX - mouseXPrevious);
            position.offsetY = position.offsetY + (mouseY - mouseYPrevious);
            mouseXPrevious = mouseX;
            mouseYPrevious = mouseY;
            Overlay overlay = (Overlay) overlaySettings.getHolder();
            if (!Keyboard.isKeyDown(42)) {
                if (overlay.growth == Overlay.OverlayGrowFrom.TOP_LEFT) {
                    overlay.position.offsetX = position.offsetX;
                    overlay.position.offsetY = position.offsetY;
                } else if (overlay.growth == Overlay.OverlayGrowFrom.TOP_CENTRE) {
                    overlay.position.offsetX = position.offsetX + (overlay.staticSize.x / 2);
                    overlay.position.offsetY = position.offsetY;
                } else if (overlay.growth == Overlay.OverlayGrowFrom.TOP_RIGHT) {
                    overlay.position.offsetX = position.offsetX + overlay.staticSize.x;
                    overlay.position.offsetY = position.offsetY;
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_LEFT) {
                    overlay.position.offsetX = position.offsetX;
                    overlay.position.offsetY = position.offsetY + (overlay.staticSize.y / 2);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_CENTRE) {
                    overlay.position.offsetX = position.offsetX + (overlay.staticSize.x / 2);
                    overlay.position.offsetY = position.offsetY + (overlay.staticSize.y / 2);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_RIGHT) {
                    overlay.position.offsetX = position.offsetX + overlay.staticSize.x;
                    overlay.position.offsetY = position.offsetY + (overlay.staticSize.y / 2);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_LEFT) {
                    overlay.position.offsetX = position.offsetX;
                    overlay.position.offsetY = position.offsetY + overlay.staticSize.y;
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_CENTRE) {
                    overlay.position.offsetX = position.offsetX + (overlay.staticSize.x / 2);
                    overlay.position.offsetY = position.offsetY + overlay.staticSize.y;
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_RIGHT) {
                    overlay.position.offsetX = position.offsetX + overlay.staticSize.x;
                    overlay.position.offsetY = position.offsetY + overlay.staticSize.y;
                }
            } else {
                if (overlay.growth == Overlay.OverlayGrowFrom.TOP_LEFT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.TOP_CENTRE) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + (overlay.staticSize.x / 2.0f));
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.TOP_RIGHT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + overlay.staticSize.x);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_LEFT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + (overlay.staticSize.y / 2.0f));
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_CENTRE) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + (overlay.staticSize.x / 2.0f));
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + (overlay.staticSize.y / 2.0f));
                } else if (overlay.growth == Overlay.OverlayGrowFrom.MIDDLE_RIGHT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + overlay.staticSize.x);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + (overlay.staticSize.y / 2.0f));
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_LEFT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + overlay.staticSize.y);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_CENTRE) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + (overlay.staticSize.x / 2.0f));
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + overlay.staticSize.y);
                } else if (overlay.growth == Overlay.OverlayGrowFrom.BOTTOM_RIGHT) {
                    overlay.position.offsetX = roundToGridPosHorizontal(position.offsetX + overlay.staticSize.x);
                    overlay.position.offsetY = roundToGridPosVertical(position.offsetY + overlay.staticSize.y);
                }
            }
        }

        public SettingsContainer getOverlaySettings() {
            return overlaySettings;
        }

        public boolean isMouseButtonHeld() {
            return mouseButtonHeld;
        }

        public int roundToGridPosHorizontal(float toRound) {
            return GRID_SIZE_HORIZONTAL * (Math.round(toRound / GRID_SIZE_HORIZONTAL));
        }

        public int roundToGridPosVertical(float toRound) {
            return GRID_SIZE_VERTICAL * (Math.round(toRound / GRID_SIZE_VERTICAL));
        }
    }

    private static class StringDrawing {
        public String string;
        public int x;
        public int y;
        public CustomColor color;
        SmartFontRenderer.TextAlignment alignment;
        SmartFontRenderer.TextShadow textShadow;

        public StringDrawing(String string, int x, int y, CustomColor color, SmartFontRenderer.TextAlignment alignment, SmartFontRenderer.TextShadow textShadow) {
            this.string = string;
            this.x = x;
            this.y = y;
            this.color = color;
            this.alignment = alignment;
            this.textShadow = textShadow;
        }

        public String getString() {
            return string;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public CustomColor getColor() {
            return color;
        }

        public SmartFontRenderer.TextAlignment getAlignment() {
            return alignment;
        }

        public SmartFontRenderer.TextShadow getTextShadow() {
            return textShadow;
        }
    }
}
