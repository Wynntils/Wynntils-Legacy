package cf.wynntils.core.framework.settings.ui;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.SettingsContainer;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.core.framework.ui.elements.UIEButton;
import cf.wynntils.core.framework.ui.elements.UIEClickZone;
import net.minecraft.client.gui.GuiScreen;

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

    private boolean reloadButtons;

    public UIEButton cancelButton = new UIEButton("Cancel", Textures.UIs.button_a,0.5f,0.5f,13,0,-10,true,(ui, mouseButton) -> {
        for(OverlayButton settingsContainer : registeredOverlaySettings) {
            try {
                settingsContainer.getOverlaySettings().tryToLoad();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        onClose();
    });

    public UIEButton applyButton = new UIEButton("Apply", Textures.UIs.button_a,0.5f,0.5f,-48,0,-10,true,(ui, mouseButton) -> {
        for(OverlayButton settingsContainer : registeredOverlaySettings) {
            try {
                settingsContainer.getOverlaySettings().saveSettings();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        onClose();
    });

    public UIEButton resetButton = new UIEButton("Default", Textures.UIs.button_a,0.5f,0.5f,-22,15,-10,true,(ui, mouseButton) -> {
        for(OverlayButton settingsContainer : registeredOverlaySettings) {
            try {
                settingsContainer.getOverlaySettings().resetValues();
                reloadButtons = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });

    public OverlayPositionsUI(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void onInit() {
        registeredOverlaySettings.clear();
        for (String moduleName : FrameworkManager.availableModules.keySet()) {
            ModuleContainer moduleContainer = FrameworkManager.availableModules.get(moduleName);
            for (String settingsName : moduleContainer.getRegisteredSettings().keySet()) {
                SettingsContainer settingsContainer = moduleContainer.getRegisteredSettings().get(settingsName);
                if (settingsContainer.getHolder() instanceof Overlay) {
                    if (((Overlay) settingsContainer.getHolder()).configurable) {
                        registeredOverlaySettings.add(new OverlayButton(settingsContainer));
                        System.out.println(((Overlay) settingsContainer.getHolder()).displayName);
                    }
                }
            }
        }
        reloadButtons = false;
    }

    @Override
    public void onClose() {
        mc.currentScreen = null;
        mc.displayGuiScreen(parentScreen);
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
        if(reloadButtons)
            onInit();
    }

    @Override
    public void onWindowUpdate() {

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
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
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (OverlayButton button : registeredOverlaySettings) {
            if (button.isMouseButtonHeld()) {
                button.clickMove(mouseX, mouseY, MouseButton.values()[clickedMouseButton], timeSinceLastClick, this);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (selected == null)
            return;
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
            } else if (hovering && !mouseButtonHeld) {
                color = CommonColors.YELLOW;
            } else if (hovering) {
                color = CommonColors.ORANGE;
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
        }

        public SettingsContainer getOverlaySettings() {
            return overlaySettings;
        }

        public boolean isMouseButtonHeld() {
            return mouseButtonHeld;
        }
    }

    private class StringDrawing {
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
