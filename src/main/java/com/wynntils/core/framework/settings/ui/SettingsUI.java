/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings.ui;

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
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SettingsUI extends UI {
    private GuiScreen parentScreen;

    private String currentSettingsPath = "";
    private Map<String, SettingsContainer> registeredSettings = new HashMap<>();
    private List<String> sortedSettings = new ArrayList<>();

    public UIEList holders = new UIEList(0.5f,0.5f,-170,-87);
    public UIEList settings = new UIEList(0.5f,0.5f,5,-90);

    public UIESlider holdersScrollbar = new UIESlider.Vertical(null, Textures.UIs.button_scrollbar,0.5f,0.5f,-178,-88, 161,false,-85,1,1f,0,null);
    public UIESlider settingsScrollbar = new UIESlider.Vertical(CommonColors.LIGHT_GRAY, Textures.UIs.button_scrollbar,0.5f,0.5f,185,-100, 200,true,-95,-150,1f,0,null);

    public SettingsUI thisScreen = this;

    HashSet<String> changedSettings = new HashSet<>();

    public UIEButton cancelButton = new UIEButton("Cancel",Textures.UIs.button_a,0.5f,0.5f,-170,85,-10,true,(ui, mouseButton) -> {
        changedSettings.forEach(c -> { try { registeredSettings.get(c).tryToLoad(); } catch (Exception e) { e.printStackTrace(); } });
        onClose();
    });
    public UIEButton applyButton = new UIEButton("Apply",Textures.UIs.button_a,0.5f,0.5f,-120,85,-10,true,(ui, mouseButton) -> {
        changedSettings.forEach(c -> { try { registeredSettings.get(c).saveSettings(); } catch (Exception e) { e.printStackTrace(); } });
        onClose();
    });

    public SettingsUI(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void onInit() {
        this.holders.visible = false;
        this.settings.visible = false;

        for(ModuleContainer mcn : FrameworkManager.availableModules.values()) {
            for(SettingsContainer scn : mcn.getRegisteredSettings().values()) {
                if(!(scn.getHolder() instanceof Overlay)) {
                    if (!scn.getDisplayPath().equals("")) {
                        registeredSettings.put(scn.getDisplayPath(), scn);
                        sortedSettings.add(scn.getDisplayPath());
                    }
                }
            }
        }

        Collections.sort(sortedSettings);
        holdersScrollbar.max = holdersScrollbar.min;
        for(String path : sortedSettings) {
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
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if(settingsScrollbar.active) {
            float i = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
            if (i != 0) {
                i = MathHelper.clamp(i, -1, 1) * settingsScrollbar.precision * 8;

                if (mouseX >= screenWidth / 2 + 5 && mouseX < screenWidth / 2 + 185 && mouseY >= screenHeight / 2 - 100 && mouseY < screenHeight / 2 + 100) {
                    settingsScrollbar.setValue(settingsScrollbar.getValue() + i);
                }
            }
        }
        if (holdersScrollbar.active) {
            float i = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
            if (i != 0) {
                if (mouseX <= screenWidth / 2 - 5 && mouseX > screenWidth / 2 - 185 && mouseY >= screenHeight / 2 - 100 && mouseY < screenHeight / 2 + 100) {
                    i = MathHelper.clamp(i, -1, 1) * holdersScrollbar.precision * 8;
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

        ScreenRenderer.createMask(Textures.Masks.full, screenWidth / 2 - 165, screenHeight / 2 - 88, screenWidth / 2 - 25, screenHeight / 2 + 73);
        holders.render(mouseX,mouseY);
        ScreenRenderer.clearMask();

        ScreenRenderer.createMask(Textures.Masks.full, screenWidth / 2 + 5, screenHeight / 2 - 100, screenWidth / 2 + 185, screenHeight / 2 + 100);
        settings.elements.forEach(setting -> {
            setting.position.anchorX = settings.position.anchorX;
            setting.position.anchorY = settings.position.anchorY;
            setting.position.offsetX += settings.position.offsetX;
            setting.position.offsetY += settings.position.offsetY;
            setting.position.refresh();
            if(setting.visible = setting.position.getDrawingY() < screenHeight/2+100 && setting.position.getDrawingY() > screenHeight/2-100-settingHeight){
                ((UIEList) setting).elements.forEach(settingElement -> {
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
                    render.drawRect(CommonColors.LIGHT_GRAY, setting.position.getDrawingX(), setting.position.getDrawingY() - 1, setting.position.getDrawingX() + 175, setting.position.getDrawingY());
                ScreenRenderer.scale(0.8f);
                render.drawString(((SettingElement) setting).info.displayName(), (setting.position.getDrawingX() + 33f) / 0.8f, (setting.position.getDrawingY() + 7) / 0.8f, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                ScreenRenderer.resetScale();
            }
            setting.position.offsetX -= settings.position.offsetX;
            setting.position.offsetY -= settings.position.offsetY;
        });
        ScreenRenderer.clearMask();
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {
        ScreenRenderer.scale(0.7f);
        render.drawString(this.currentSettingsPath.replace('/','>'),(screenWidth/2f+10)/0.7f,(screenHeight/2f-106)/0.7f, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        ScreenRenderer.resetScale();
        settings.elements.forEach(setting -> {
            if(setting.visible && mouseX >= screenWidth/2+5 && mouseX < screenWidth/2+185 && mouseY > screenHeight/2-100 && mouseY < screenHeight/2+100 && mouseY >= setting.position.getDrawingY() && mouseY < setting.position.getDrawingY() + settingHeight) {
                List<String> lines = Arrays.asList(((SettingElement) setting).info.description().split("_nl"));
//                GuiUtils.drawHoveringText(lines, setting.position.getDrawingX()-10, screenHeight/2-100, 0, screenHeight, 170, render.fontRenderer);
                GuiUtils.drawHoveringText(lines, mouseX, mouseY, 0, screenHeight, 170, ScreenRenderer.fontRenderer);
            }
        });
    }

    @Override
    public void onWindowUpdate() {

    }

    public void setCurrentSettingsPath(String path) {
        currentSettingsPath = path;
        settings.elements.clear();
        settingsScrollbar.max = settingsScrollbar.min;
        try {
            List<Field> notSorted = new ArrayList<>(registeredSettings.get(path).getValues().keySet());
            List<Field> sorted = notSorted.stream().filter(c -> c.getAnnotation(Setting.class) != null && !c.getAnnotation(Setting.class).displayName().isEmpty()).sorted(Comparator.comparing(o -> o.getAnnotation(Setting.class).displayName())).sorted(Comparator.comparingInt(o -> o.getAnnotation(Setting.class).order())).collect(Collectors.toList());

            for (Field field : sorted) {
                try {
                    settings.add(new SettingElement(field));
                    settingsScrollbar.max -= settingHeight;
                } catch (Exception ignored) {
                    //no @Setting
                }
            }
            if(settingsScrollbar.min - settingsScrollbar.max > 185) {
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

    private class HolderButton extends UIEButton {
        public String path;

        public HolderButton(String path) {
            super("", null, 0f, 0f, 0, 0, -1, true, null);
            String[] paths = path.split("/");
            this.height = 9;
            this.path = path;
            this.text = paths[paths.length-1];
            this.position.offsetY = 11*holders.elements.size();
            this.position.offsetX = 10*paths.length;
        }

        @Override
        public void render(int mouseX, int mouseY) {
            if(!visible) return;
            hovering = mouseX >= position.getDrawingX() && mouseX < position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY < position.getDrawingY()+height;
            active = !currentSettingsPath.equals(this.path);
            width = Math.max( this.setWidth < 0 ? (int)getStringWidth(text) - this.setWidth : this.setWidth, 0);

            if (!active) {
                drawString(text,this.position.getDrawingX()+width/2f,this.position.getDrawingY()+height/2f-4f, TEXTCOLOR_NOTACTIVE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            } else if (hovering) {
                drawString(text,this.position.getDrawingX()+width/2f,this.position.getDrawingY()+height/2f-4f, TEXTCOLOR_HOVERING, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            } else {
                drawString(text,this.position.getDrawingX()+width/2f,this.position.getDrawingY()+height/2f-4f, TEXTCOLOR_NORMAL, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            }
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            if(active && hovering) {
                if(clickSound != null)
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound,1f));
                setCurrentSettingsPath(path);
            }
        }
    }
    public static final int settingHeight = 45;
    private class SettingElement extends UIEList {
        public Field field;
        public Setting info;
        public UIElement valueElement;

        public SettingElement(Field field) throws NullPointerException {
            super(0f, 0f, 0, 0);
            this.field = field;

            this.info = field.getAnnotation(Setting.class);
            if (info == null) throw new NullPointerException();

            this.position.offsetY = settingHeight * settings.elements.size();

            add(new UIEButton("reset", Textures.UIs.button_a, 0f, 0f, 0, 0, -5, true, (ui, mouseButton) -> {
                try {
                    registeredSettings.get(currentSettingsPath).resetValue(field);

                    setCurrentSettingsPath(currentSettingsPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

            updateValue();
        }

        private void updateValue() {
            if(valueElement != null)
                return;

            try {
                Object value = registeredSettings.get(currentSettingsPath).getValues().get(field);
                if (value instanceof String) {
                    valueElement = new UIETextBox(0f, 0f, 0, 16, 170, true, ((String) value).replace("§", "&"), false, (ui, oldString) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValue(field, ((UIETextBox) valueElement).getText().replace("&", "§"), false);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    //((UIETextBox) valueElement).textField.setEnableBackgroundDrawing(false);
                    Setting.Limitations.StringLimit limit = field.getAnnotation(Setting.Limitations.StringLimit.class);
                    if(limit != null)
                        ((UIETextBox) valueElement).textField.setMaxStringLength(limit.maxLength());
                    else ((UIETextBox) valueElement).textField.setMaxStringLength(120);
                } else if (field.getType().isAssignableFrom(boolean.class)) {
                    valueElement = new UIEButton.Toggle("Enabled", Textures.UIs.button_b, "Disabled", Textures.UIs.button_b, (boolean) value, 0f, 0f, 0, 15, -10, true, (ui, mouseButton) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValue(field, ((UIEButton.Toggle) valueElement).value, false);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else if (value instanceof Enum) {
                    valueElement = new UIEButton.Enum(s -> s, Textures.UIs.button_b, (Class<? extends Enum>) field.getType(), value, 0f, 0f, 0, 15, -10, true, (ui, mouseButton) -> {
                        try {
                            registeredSettings.get(currentSettingsPath).setValue(field, ((UIEButton.Enum) valueElement).value, false);
                            changedSettings.add(currentSettingsPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else if (field.getType().isAssignableFrom(int.class)) {
                    Setting.Limitations.IntLimit limit = field.getAnnotation(Setting.Limitations.IntLimit.class);
                    if(limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY,Textures.UIs.button_a,0f,0f,0,15,175,true,limit.min(),limit.max(),limit.precision(),0,(ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValue(field, (int)((UIESlider)valueElement).getValue(), false);
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        ((UIESlider)valueElement).setValue((int)value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#");
                    }
                } else if (field.getType().isAssignableFrom(float.class)) {
                    Setting.Limitations.FloatLimit limit = field.getAnnotation(Setting.Limitations.FloatLimit.class);
                    if(limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY,Textures.UIs.button_a,0f,0f,0,15,175,true,limit.min(),limit.max(),limit.precision(),0,(ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValue(field, ((UIESlider) valueElement).getValue(), false);
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        ((UIESlider)valueElement).setValue((float)value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#.#");
                    }
                } else if (field.getType().isAssignableFrom(double.class)) {
                    Setting.Limitations.DoubleLimit limit = field.getAnnotation(Setting.Limitations.DoubleLimit.class);
                    if(limit != null) {
                        valueElement = new UIESlider.Horizontal(CommonColors.GRAY,Textures.UIs.button_a,0f,0f,0,15,175,true,(float)limit.min(),(float)limit.max(),(float)limit.precision(),0,(ui, aFloat) -> {
                            try {
                                registeredSettings.get(currentSettingsPath).setValue(field, (double)((UIESlider)valueElement).getValue(), false);
                                changedSettings.add(currentSettingsPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        ((UIESlider)valueElement).setValue((float)(double) value);
                        ((UIESlider)valueElement).decimalFormat = new DecimalFormat("#.#");
                    }
                } else if(field.getType().isAssignableFrom(CustomColor.class)) {
                    valueElement = new UIEColorWheel(0, 0, 0, 17, 20, 20, true, (color) -> {
                        try{
                            registeredSettings.get(currentSettingsPath).setValue(field, color, false);
                            changedSettings.add(currentSettingsPath);
                        }catch (Exception ex) { ex.printStackTrace(); }
                    }, thisScreen);
                    ((UIEColorWheel) valueElement).setColor((CustomColor)value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            elements.add(valueElement);
        }
    }
}
