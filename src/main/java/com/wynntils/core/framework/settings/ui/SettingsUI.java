/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.settings.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
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
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.framework.ui.UIElement;
import com.wynntils.core.framework.ui.elements.*;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class SettingsUI extends UI {
    private static final SettingsUI INSTANCE = new SettingsUI();
    static {
        UI.setupUI(INSTANCE);
    }

    private GuiScreen parentScreen;

    private String currentSettingsPath = "";
    private Map<String, SettingsContainer> registeredSettings = new HashMap<>();
    private List<String> sortedSettings = new ArrayList<>();
    private Set<String> changedSettings = new HashSet<>();
    private List<String> searchText = Collections.emptyList();

    public UIEList holders = new UIEList(0.5f, 0.5f, -170, -87);
    public UIEList settings = new UIEList(0.5f, 0.5f, 5, -90);

    public UIESlider holdersScrollbar = new UIESlider.Vertical(null, Textures.UIs.button_scrollbar, 0.5f, 0.5f, -178, -88, 161, false, -85, 1, 1f, 0, null, 0, 0, 5, 27);
    public UIESlider settingsScrollbar = new UIESlider.Vertical(CommonColors.LIGHT_GRAY, Textures.UIs.button_scrollbar, 0.5f, 0.5f, 185, -100, 200, true, -95, -150, 1f, 0, null, 0, 0, 5, 27);

    private int resetButtonCount = 4;
    private final int gameRestartCD = 7;
    private String resetCountText = "Click " + resetButtonCount + " more times to reset and save.\n§8Your game will be closed on reset.";
    public UIEButton resetButton = new UIEButton("R", Textures.UIs.button_a, 0.5f, 0.5f, -187, 85, -10, true, (ui, mouseButton) -> {
        if (resetButtonCount == 0) {
            // button has already been zero'ed, do nothing
            return;
        } else {
            resetButtonCount--;
        }
        if (resetButtonCount == 0) {
            registeredSettings.forEach((k, v) -> {
                try {
                    v.resetValues();
                    v.saveSettings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // make timer counting down from gameRestartCD seconds
            for (int i = gameRestartCD; i > 0; i--) {
                int finalI = i;
                String seconds = (finalI == 1) ? " second" : " seconds";
                new Delay(() -> resetCountText = "All settings reset, game will be closed in " + finalI + seconds, (gameRestartCD - finalI) * 20);
            }
            new Delay(() -> McIf.mc().shutdown(), gameRestartCD * 20);
        } else {
            resetCountText = "Click " + resetButtonCount + " more times to reset and save.\n§8Your game will be closed on reset.";
        }
    }, 0, 0, 17, 45);
    public UIEButton cancelButton = new UIEButton("Cancel", Textures.UIs.button_a, 0.5f, 0.5f, -170, 85, -10, true, (ui, mouseButton) -> {
        changedSettings.forEach(c -> { try { registeredSettings.get(c).tryToLoad(); } catch (Exception e) { e.printStackTrace(); } });
        onClose();
    }, 0, 0, 17, 45);
    public UIEButton applyButton = new UIEButton("Save", Textures.UIs.button_a, 0.5f, 0.5f, -130, 85, -10, true, (ui, mouseButton) -> {
        Reference.LOGGER.info("Queuing " + changedSettings.size() + " settings to be saved");
        changedSettings.forEach(c -> { try { registeredSettings.get(c).saveSettings(); } catch (Exception e) { e.printStackTrace(); } });
        onClose();
    }, 0, 0, 17, 45);
    public UIETextBox searchField = new UIETextBox(0.5f, 0.5f, -90, 82, 85, true, "Search...", true, (ui, oldText) -> {
        updateSearchText();
    });

    private SettingsUI() { }

    public static SettingsUI getInstance(GuiScreen parentScreen) {
        INSTANCE.parentScreen = parentScreen;
        return INSTANCE;
    }

    @Override
    public void onInit() {
        this.holders.visible = false;
        this.settings.visible = false;

        for (ModuleContainer mcn : FrameworkManager.getAvailableModules().values()) {
            for (SettingsContainer scn : mcn.getRegisteredSettings().values()) {
                if (!(scn.getHolder() instanceof Overlay)) {
                    if (!scn.getDisplayPath().equals("")) {
                        registeredSettings.put(scn.getDisplayPath(), scn);
                        sortedSettings.add(scn.getDisplayPath());
                    }
                }
            }
        }

        Collections.sort(sortedSettings);
        holdersScrollbar.max = holdersScrollbar.min;
        for (String path : sortedSettings) {
            holders.add(new HolderButton(path));
            holdersScrollbar.max -= 11;
        }
        if (holdersScrollbar.min - holdersScrollbar.max > 160) {
            holders.position.offsetY = (int) holdersScrollbar.getValue();
            holdersScrollbar.active = true;
        } else {
            holders.position.offsetY = (int) holdersScrollbar.min;
            holdersScrollbar.active = false;
            holdersScrollbar.progress = 0f;
        }
        holdersScrollbar.max += 160;

        searchField.setText("");
        updateSearchText();
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);

        McIf.mc().currentScreen = null;
        McIf.mc().displayGuiScreen(parentScreen);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (settingsScrollbar.active) {
            float i = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
            if (i != 0) {
                i = MathHelper.clamp(i, -1, 1) * settingsScrollbar.precision * 16;

                if (mouseX >= screenWidth / 2 + 5 && mouseX < screenWidth / 2 + 185 && mouseY >= screenHeight / 2 - 100 && mouseY < screenHeight / 2 + 100) {
                    settingsScrollbar.setValue(settingsScrollbar.getValue() + i);
                }
            }
        }
        if (holdersScrollbar.active) {
            float i = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
            if (i != 0) {
                if (mouseX <= screenWidth / 2 - 5 && mouseX > screenWidth / 2 - 185 && mouseY >= screenHeight / 2 - 100 && mouseY < screenHeight / 2 + 100) {
                    i = MathHelper.clamp(i, -1, 1) * holdersScrollbar.precision * 16;
                    holdersScrollbar.setValue(holdersScrollbar.getValue() + i);
                }
            }
        }
    }

    @Override
    public void onRenderPreUIE(ScreenRenderer render) {
        drawDefaultBackground();
        CommonUIFeatures.drawBook();
        CommonUIFeatures.drawScrollArea();

        settings.position.offsetY = (int)settingsScrollbar.getValue();
        holders.position.offsetY = (int)holdersScrollbar.getValue();

        holders.elements.forEach(el -> {
            int y = el.position.offsetY + holders.position.offsetY;
            el.visible = -99 <= y && y <= +73;
        });
        ScreenRenderer.enableScissorTest(screenWidth / 2 - 165, screenHeight / 2 - 88, 140, 161);
        holders.render(mouseX, mouseY);
        // ScreenRenderer.disableScissorTest();  // reenabled on the next line, no need to disable unless this changes

        ScreenRenderer.enableScissorTest(screenWidth / 2 + 5, screenHeight / 2 - 100, 180, 200);
        settings.elements.forEach(setting_ -> {
            SettingElement setting = (SettingElement) setting_;
            setting.position.anchorX = settings.position.anchorX;
            setting.position.anchorY = settings.position.anchorY;
            setting.position.offsetX += settings.position.offsetX;
            setting.position.offsetY += settings.position.offsetY;
            setting.position.refresh();
            if (setting.visible = setting.position.getDrawingY() < screenHeight/2+100 && setting.position.getDrawingY() > screenHeight/2-100-settingHeight) {
                setting.elements.forEach(settingElement -> {
                    settingElement.position.anchorX = settings.position.anchorX;
                    settingElement.position.anchorY = settings.position.anchorY;
                    settingElement.position.offsetX += setting.position.offsetX;
                    settingElement.position.offsetY += setting.position.offsetY;
                    settingElement.position.refresh();
                    settingElement.position.offsetX -= setting.position.offsetX;
                    settingElement.position.offsetY -= setting.position.offsetY;
                    settingElement.render(mouseX, mouseY);
                });
                if (setting != settings.elements.get(0))
                    render.drawRect(CommonColors.LIGHT_GRAY, setting.position.getDrawingX(), setting.position.getDrawingY() - 4, setting.position.getDrawingX() + 175, setting.position.getDrawingY() -3);
                ScreenRenderer.scale(0.8f);
                String name = setting.field.info.displayName();
                render.drawString(
                    name,
                    (setting.position.getDrawingX() + 43f) / 0.8f, (setting.position.getDrawingY() + 4.5f) / 0.8f,
                    !searchText.isEmpty() && !setting.isSearched ? CommonColors.GRAY : CommonColors.BLACK,
                    SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE
                );
                ScreenRenderer.resetScale();
                if (setting.isSearched) {
                    int y = (int) (setting.position.getDrawingY() + 4.5f + fontRenderer.FONT_HEIGHT * 0.8f);
                    int x = setting.position.getDrawingX() + 43;
                    render.drawRect(CommonColors.BLACK, x, y, x + (int) (ScreenRenderer.fontRenderer.getStringWidth(name) * 0.8f) + 1, y + 1);
                }
            }
            setting.position.offsetX -= settings.position.offsetX;
            setting.position.offsetY -= settings.position.offsetY;
        });
        ScreenRenderer.disableScissorTest();
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {
        ScreenRenderer.scale(0.7f);
        String path = this.currentSettingsPath.replace("/", " > ");
        render.drawString(path, (screenWidth/2f+10)/0.7f, (screenHeight/2f-103)/0.7f, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        if (Reference.developmentEnvironment) {
            SettingsContainer scn = registeredSettings.get(currentSettingsPath);
            if (scn != null) {
                String saveFile = scn.getSaveFile();
                if (saveFile != null) {
                    render.drawString(saveFile, (screenWidth/2f-10)/0.7f, (screenHeight/2f-103)/0.7f, CommonColors.BLACK, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                }
            }
        }
        ScreenRenderer.resetScale();
        settings.elements.forEach(setting -> {
            if (setting.visible && mouseX >= screenWidth/2+5 && mouseX < screenWidth/2+185 && mouseY > screenHeight/2-100 && mouseY < screenHeight/2+100 && mouseY >= setting.position.getDrawingY() && mouseY < setting.position.getDrawingY() + settingHeight) {
                List<String> lines = Arrays.asList(((SettingElement) setting).field.info.description().split("_nl"));
//                GuiUtils.drawHoveringText(lines, setting.position.getDrawingX()-10, screenHeight/2-100, 0, screenHeight, 170, render.fontRenderer);
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, 0, screenHeight, 170, ScreenRenderer.fontRenderer);
            }
        });

        // hover text for reset button
        if (resetButton.isHovering()) {
            GuiUtils.drawHoveringText(Arrays.asList("Reset all settings to default", resetCountText),
                    mouseX, mouseY, 0, screenHeight, 170, ScreenRenderer.fontRenderer);
        }
    }

    @Override
    public void onWindowUpdate() {
        Keyboard.enableRepeatEvents(true);
    }

    public void setCurrentSettingsPath(String path) {
        currentSettingsPath = path;
        settings.elements.clear();
        settingsScrollbar.max = settingsScrollbar.min;
        try {
            List<SettingsContainer.SettingField> sorted = new ArrayList<>(registeredSettings.get(path).getValues().keySet());
            sorted.removeIf(c -> c.info.displayName().isEmpty());
            sorted.sort(Comparator.<SettingsContainer.SettingField>comparingInt(o -> o.info.order()).thenComparing(o -> o.field.getName()));

            for (SettingsContainer.SettingField field : sorted) {
                SettingElement newSetting = new SettingElement(field);
                newSetting.isSearched = doesMatchSearch(newSetting);
                settings.add(newSetting);
                settingsScrollbar.max -= settingHeight;
            }
            if (settingsScrollbar.min - settingsScrollbar.max > 185) {
                settings.position.offsetY = (int)settingsScrollbar.getValue();
                settingsScrollbar.active = true;
            }
            else {
                settings.position.offsetY = (int) settingsScrollbar.min;
                settingsScrollbar.active = false;
                settingsScrollbar.progress = 0f;
            }
            settingsScrollbar.max += 185;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSearchText() {
        String newText = searchField.getText();
        if (newText == null) newText = "";
        String[] words = StringUtils.split(newText);
        searchText = new ArrayList<>(words.length);
        for (String word : words) {
            if (!word.isEmpty()) searchText.add(word);
        }
        settings.elements.forEach(s -> ((SettingElement) s).isSearched = doesMatchSearch((SettingElement) s));
        holders.elements.forEach(h -> ((HolderButton) h).isSearched = doesMatchSearch((HolderButton) h));
    }

    private boolean doesStringMatchSearch(String s) {
        if (searchText.isEmpty()) return false;
        for (String word : searchText) {
            if (StringUtils.containsIgnoreCase(s, word)) return true;
        }
        return false;
    }

    private boolean doesMatchSearch(Setting setting) {
        if (setting == null || searchText.isEmpty()) return false;
        return doesStringMatchSearch(setting.displayName()) || doesStringMatchSearch(setting.description());
    }

    private boolean doesMatchSearch(SettingElement setting) {
        return doesMatchSearch(setting.field.info);
    }

    private boolean doesMatchSearch(String settingPath) {
        if (searchText.isEmpty()) return false;
        if (doesStringMatchSearch(settingPath)) {
            return true;
        }
        Set<SettingsContainer.SettingField> settings;
        try {
            settings = registeredSettings.get(settingPath).getValues().keySet();
        } catch (Exception ignored) {
            return false;
        }
        for (SettingsContainer.SettingField setting : settings) {
            if (doesMatchSearch(setting.info)) {
                return true;
            }
        }
        return false;
    }

    private boolean doesMatchSearch(HolderButton settingPath) {
        return doesMatchSearch(settingPath.path);
    }

    private static final CustomColor TEXTCOLOR_UNSEARCHED = CustomColor.fromInt(0xe6e6e6, 1f);

    private class HolderButton extends UIEButton {
        String path;
        boolean isSearched = false;
        int textWidth;

        public HolderButton(String path) {
            super("", null, 0f, 0f, 0, 0, -1, true, null, 0, 0, 0, 0);
            String[] paths = path.split("/");
            this.height = 9;
            this.path = path;
            this.text = paths[paths.length-1];
            this.position.offsetY = 11*holders.elements.size();
            this.position.offsetX = 10*paths.length;
            this.textWidth = fontRenderer.getStringWidth(this.text);
        }

        @Override
        public void render(int mouseX, int mouseY) {
            if (!visible) return;
            hovering = mouseX >= position.getDrawingX() && mouseX < position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY < position.getDrawingY()+height;
            active = !currentSettingsPath.equals(this.path);
            width = Math.max(this.setWidth < 0 ? (int)getStringWidth(text) - this.setWidth : this.setWidth, 0);

            CustomColor color = !active ? TEXTCOLOR_NOTACTIVE : hovering ? TEXTCOLOR_HOVERING : (!searchText.isEmpty() && !isSearched) ? TEXTCOLOR_UNSEARCHED : TEXTCOLOR_NORMAL;
            drawString(text, this.position.getDrawingX()+width/2f, this.position.getDrawingY()+height/2f-4f, color, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

            if (isSearched) {
                int x = (int) (this.position.getDrawingX()+(width - textWidth)/2f);
                int y = (int) (this.position.getDrawingY()+height/2f-4f) + fontRenderer.FONT_HEIGHT;
                drawRect(CommonColors.BLACK, x + 1, y + 1, x + textWidth + 1, y + 2);
                drawRect(color, x, y, x + textWidth, y + 1);
            }
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            if (visible && active && hovering) {
                if (clickSound != null)
                    McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound, 1f));
                setCurrentSettingsPath(path);
            }
        }
    }
    public static final int settingHeight = 45;
    private class SettingElement extends UIEList {
        public SettingsContainer.SettingField field;
        public UIElement valueElement;
        public boolean isSearched = false;

        public SettingElement(SettingsContainer.SettingField field) {
            super(0f, 0f, 0, 0);
            this.field = field;

            this.position.offsetY = settingHeight * settings.elements.size();

            add(new UIEButton(" reset ", Textures.UIs.button_a, 0f, 0f, 0, 0, -5, true, (ui, mouseButton) -> {
                try {
                    registeredSettings.get(currentSettingsPath).resetValue(field);
                    changedSettings.add(currentSettingsPath);
                    setCurrentSettingsPath(currentSettingsPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 0, 17, 45));

            updateValue();
        }

        private void updateValue() {
            if (valueElement != null)
                return;

            try {
                Object value = registeredSettings.get(currentSettingsPath).getValues().get(field);
                Class<?> type = field.field.getType();
                if (value instanceof String) {
                    String text = ((String) value).replace("§", "&");
                    valueElement = new UIETextBox(0f, 0f, 0, 16, 170, true, text, false, (ui, oldString) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, ((UIETextBox) valueElement).getText().replace("&", "§"));
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    // ((UIETextBox) valueElement).textField.setEnableBackgroundDrawing(false);
                    Setting.Limitations.StringLimit limit = field.field.getAnnotation(Setting.Limitations.StringLimit.class);
                    if (limit != null)
                        ((UIETextBox) valueElement).textField.setMaxStringLength(limit.maxLength());
                    else ((UIETextBox) valueElement).textField.setMaxStringLength(120);
                    // Set text again in case it was over default max length of 32
                    ((UIETextBox) valueElement).setText(text);
                } else if (type.isAssignableFrom(boolean.class)) {
                    valueElement = new UIEButton.Toggle(TextFormatting.GREEN + "Enabled", Textures.UIs.button_b, TextFormatting.RED + "Disabled", Textures.UIs.button_b, (boolean) value, 0f, 0f, 0, 15, -10, true, (ui, mouseButton) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, ((UIEButton.Toggle) valueElement).value);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 0, 0, 17, 60);
                } else if (value instanceof Enum) {
                    valueElement = new UIEButton.Enum(s -> s, Textures.UIs.button_b, (Class<? extends Enum>) type, (Enum) value, 0f, 0f, 0, 15, -10, true, (ui, mouseButton) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, ((UIEButton.Enum) valueElement).value);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, 0, 0, 17, 60);
                } else if (type.isAssignableFrom(int.class)) {
                    Setting.Limitations.IntLimit limit = field.field.getAnnotation(Setting.Limitations.IntLimit.class);
                    if (limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY, Textures.UIs.button_a, 0f, 0f, 0, 15, 175, true, limit.min(), limit.max(), limit.precision(), 0, (ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, (int)((UIESlider)valueElement).getValue());
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 0, 0, 17, 45);
                        ((UIESlider)valueElement).setValue((int)value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#");
                    }
                } else if (type.isAssignableFrom(float.class)) {
                    Setting.Limitations.FloatLimit limit = field.field.getAnnotation(Setting.Limitations.FloatLimit.class);
                    if (limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY, Textures.UIs.button_a, 0f, 0f, 0, 15, 175, true, limit.min(), limit.max(), limit.precision(), 0, (ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, ((UIESlider) valueElement).getValue());
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 0, 0, 17, 45);
                        ((UIESlider)valueElement).setValue((float)value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#.#");
                    }
                } else if (type.isAssignableFrom(double.class)) {
                    Setting.Limitations.DoubleLimit limit = field.field.getAnnotation(Setting.Limitations.DoubleLimit.class);
                    if (limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY, Textures.UIs.button_a, 0f, 0f, 0, 15, 175, true, (float)limit.min(), (float)limit.max(), (float)limit.precision(), 0, (ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, (double)((UIESlider)valueElement).getValue());
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 0, 0, 17, 45);
                        ((UIESlider)valueElement).setValue((float)(double) value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#.#");
                    }
                } else if (type.isAssignableFrom(CustomColor.class)) {
                    valueElement = new UIEColorWheel(0, 0, 0, 17, 20, 20, true, (color) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValueWithoutSaving(field.field, color);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }, SettingsUI.this);
                    Setting.Features.CustomColorFeatures features = field.field.getAnnotation(Setting.Features.CustomColorFeatures.class);
                    if (features != null) {
                        if (features.allowAlpha()) {
                            ((UIEColorWheel) valueElement).allowAlpha();
                        }
                    }
                    ((UIEColorWheel) valueElement).setColor((CustomColor)value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            elements.add(valueElement);
        }
    }
}