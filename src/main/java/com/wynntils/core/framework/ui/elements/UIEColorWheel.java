/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UIEColorWheel extends UIEClickZone {

    private static final Pattern hexChecker = Pattern.compile("#(?:[0-9A-Fa-f]{3}){1,2}");
    private static final Pattern hexAndAlphaChecker = Pattern.compile("^#((?:[0-9A-Fa-f]{3}){1,2}),\\s+(\\d*(?:\\.\\d*)?)%$");

    CustomColor color = new CustomColor(1f, 1f, 1f);
    GuiScreen backGui;
    Consumer<CustomColor> onAccept;
    boolean allowAlpha;
    public UIETextBox textBox;

    public UIEColorWheel(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, boolean active, Consumer<CustomColor> onAccept, GuiScreen backGui) {
        super(anchorX, anchorY, offsetX, offsetY, width, height, active, (ui, mouse) -> {});

        this.backGui = backGui;
        this.clickSound = SoundEvents.UI_BUTTON_CLICK;
        this.onAccept = onAccept;

        textBox = new UIETextBox(0, 0, 0, 16, 120, true, formatColourName(color), false, (ui, t) -> {
            String text = textBox.getText().trim();
            Matcher m;
            if (hexChecker.matcher(text).matches()) {
                color = CustomColor.fromString(text.replace("#", ""), 1);
            } else if (allowAlpha && (m = hexAndAlphaChecker.matcher(text)).matches()) {
                float a;
                try {
                    a = Float.parseFloat(m.group(2)) / 100;
                } catch (NumberFormatException e) {
                    a = -1;
                }
                if (!(0 <= a && a <= 1)) {
                    textBox.setColor(0xFF5555);
                    return;
                }
                color = CustomColor.fromString(m.group(1), a);
            } else if (CommonColors.set.has(text)) {
                color = CommonColors.set.fromName(text);
            } else if (MinecraftChatColors.set.has(text)) {
                color = MinecraftChatColors.set.fromName(text);
            } else {
                textBox.setColor(0xFF5555);
                return;
            }

            textBox.setColor(0x55FF55);
            if (!allowAlpha && color.a != 1) {
                color = new CustomColor(color.r, color.g, color.b);
            }
            onAccept.accept(color);
        });

        textBox.setColor(0x55FF55);
    }

    public void allowAlpha() {
        this.allowAlpha = true;
    }

    public void setColor(CustomColor color) {
        this.color = color;

        textBox.setText(formatColourName(color));
    }

    private String formatColourName(CustomColor color) {
        if (!allowAlpha || color.a == 1) {
            return String.format("#%02X%02X%02X", (int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255));
        }
        return String.format("#%02X%02X%02X, %d%%", (int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255), (int) (color.a * 100));
    }

    @Override
    public void tick(long ticks) {
        textBox.tick(ticks);

        super.tick(ticks);
    }

    @Override
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        textBox.click(mouseX, mouseY, button, ui);

        if (hovering) mc.displayGuiScreen(new ColorPickerGUI());
    }

    public void keyTyped(char c, int i, UI ui) {
        textBox.keyTyped(c, i, ui);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        drawRect(CommonColors.BLACK, position.drawingX, position.drawingY, position.drawingX + width+2, position.drawingY + height+2);  // HeyZeer0: this makes a black box behind the color
        drawRect(color, position.drawingX+1, position.drawingY+1, position.drawingX + width+1, position.drawingY + height+1);

        textBox.position.drawingX = position.drawingX + width + 5;
        textBox.position.drawingY = position.drawingY + 1;
        textBox.render(mouseX, mouseY);

        super.render(mouseX, mouseY);
    }

    public class ColorPickerGUI extends GuiScreen {

        CustomColor toChange;

        GuiButton applyButton;
        GuiButton cancelButton;
        GuiSlider valueSlider;
        GuiSlider alphaSlider;

        int clickedPosX, clickedPosY = 0;

        static final int circleRadius = 160/2;

        String colorText = null;

        boolean wheelSelected = false;

        public ColorPickerGUI() {
            toChange = new CustomColor(color);
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button == applyButton) {
                color = toChange;

                mc.displayGuiScreen(backGui);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound, 1f));
                onAccept.accept(color);
                if (colorText == null) {
                    textBox.setText(formatColourName(color));
                } else {
                    textBox.setText(colorText);
                }
            } else if (button == cancelButton) {
                mc.displayGuiScreen(backGui);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound, 1f));
            }
        }

        @Override
        public void initGui() {
            buttonList.add(applyButton = new GuiButton(0, width/2 - 65, height/2 + 95, 50, 20, TextFormatting.GREEN + "Apply"));
            buttonList.add(cancelButton = new GuiButton(1, (width/2) + 15, height/2 + 95, 50, 20, TextFormatting.RED + "Cancel"));
            buttonList.add(valueSlider = new GuiSlider(new GuiPageButtonList.GuiResponder() {
                @Override public void setEntryValue(int id, boolean value) {}
                @Override public void setEntryValue(int id, String value) {}
                @Override public void setEntryValue(int id, float value) {
                    float[] hsv = toChange.toHSV();
                    toChange = CustomColor.fromHSV(hsv[0], hsv[1], value, hsv[3]);
                    colorText = null;
                }
            }, 2, this.width/2 - (allowAlpha ? 155 : 75), this.height/2+71, "Brightness", 0, 1, toChange.toHSV()[2], (id, name, value) -> String.format("Brightness: %d%%", (int) (value * 100))));
            if (allowAlpha) {
                buttonList.add(alphaSlider = new GuiSlider(new GuiPageButtonList.GuiResponder() {
                    @Override public void setEntryValue(int id, boolean value) {}
                    @Override public void setEntryValue(int id, String value) {}
                    @Override public void setEntryValue(int id, float value) {
                        toChange.setA(value);
                        colorText = null;
                    }
                }, 3, this.width/2 + 5, this.height/2+71, "Opacity", 0, 1, toChange.a, (id, name, value) -> String.format("Opacity: %d%%", (int) (value * 100))));
            }
            setColor(toChange);

            super.initGui();
        }

        private void setColor(CustomColor c) {
            toChange = new CustomColor(c);
            float[] hsv = toChange.toHSV();
            float h = hsv[0];
            float s = hsv[1];
            float v = hsv[2];
            double theta = h * 2D * Math.PI;
            double r = s * (double) circleRadius;
            clickedPosX = width/2 + (int) Math.round(r * Math.cos(theta));
            clickedPosY = height/2 - 13 - (int) Math.round(r * Math.sin(theta));
            valueSlider.setSliderValue(v, false);
            if (allowAlpha) {
                alphaSlider.setSliderValue(c.a, false);
            }
        }

        private boolean changeColor(int mouseX, int mouseY, boolean move) {
            if ((move && !wheelSelected) || valueSlider.isMouseDown) return false;

            CustomColor fromSet = null;
            String name = null;
            if (!move && mouseX >= this.width/2 + 85) {
                int col = (mouseX - (this.width/2 + 85)) / 20;
                int row = (mouseY - (this.height/2 - 93)) / 20;
                if (0 <= col && col <= 1 && 0 <= row && row <= 7) {
                    fromSet = CommonColors.set.fromCode(row + col * 8);
                    name = CommonColors.set.getName(row + col * 8);
                }
            } else if (!move && mouseX <= this.width/2 - 85) {
                int col = (mouseX - (this.width/2 - 125)) / 20;
                int row = (mouseY - (this.height/2 - 93)) / 20;
                if (0 <= col && col <= 1 && 0 <= row && row <= 7) {
                    fromSet = MinecraftChatColors.set.fromCode(row + col * 8);
                    name = MinecraftChatColors.set.getName(row + col * 8);
                    if (CommonColors.set.has(name) && !CommonColors.set.fromName(name).equals(fromSet)) {
                        // Conflicting colour
                        name = null;
                        for (String alias : MinecraftChatColors.set.getAliases(row + col * 8)) {
                            if (alias.charAt(0) == '§' || alias.charAt(0) == '&') continue;
                            if (!CommonColors.set.has(alias) || CommonColors.set.fromName(alias).equals(fromSet)) {
                                name = alias; break;
                            }
                        }
                        if (name == null) name = "&" + Integer.toString(row + col * 8, 16);
                    }
                }
            }

            if (fromSet != null) {
                wheelSelected = false;
                setColor(new CustomColor(fromSet));
                colorText = WordUtils.capitalizeFully(name.replace('_', ' '));
                return true;
            }

            float mousePosX = (mouseX - width/2f) / circleRadius;
            float mousePosY = (height/2f - 13 - mouseY) / circleRadius;

            double s = Math.sqrt(mousePosX * mousePosX + mousePosY * mousePosY);
            if (s > 1.0) {
                if (!move) {
                    return false;
                }
                mouseX = (int) Math.round(mousePosX / s * circleRadius + width/2f);
                mouseY = (int) Math.round(-(mousePosY / s * circleRadius - height/2f + 13));
                s = 1.0;
            }

            wheelSelected = true;

            clickedPosX = mouseX; clickedPosY = mouseY;
            double h = Math.atan2(mousePosY, mousePosX) / (2*Math.PI);
            if (h < 0) h += 1;

            toChange = CustomColor.fromHSV((float)h, (float)s, valueSlider.getSliderValue(), getAlpha());
            colorText = null;
            return true;
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            if (mouseButton == 0 && changeColor(mouseX, mouseY, false)) return;
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
            if (clickedMouseButton == 0 && changeColor(mouseX, mouseY, true)) return;
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }

        @Override
        protected void mouseReleased(int mouseX, int mouseY, int state) {
            if (state == 0) {
                wheelSelected = false;
            }
            super.mouseReleased(mouseX, mouseY, state);
        }

        @Override
        public void handleMouseInput() throws IOException {
            int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
            if (mDWheel > 0) {
                valueSlider.setSliderValue(Math.min(valueSlider.getSliderValue() + 0.1f, 1), true);
            } else if (mDWheel < 0) {
                valueSlider.setSliderValue(Math.max(valueSlider.getSliderValue() - 0.1f, 0), true);
            }

            super.handleMouseInput();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();

            beginGL(0, 0);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            float v = valueSlider.getSliderValue();
            float a = getAlpha();
            if (v != 1 || a != 1) {
                GlStateManager.color(v, v, v, a);
            }
            drawRectF(Textures.UIs.color_wheel, (width/2f)-80, (height/2f)-93, (width/2f)+80, (height/2f)+67, 0, 0, 256, 256);  // rgb wheel
            if (v != 1 || a != 1) {
                GlStateManager.color(1, 1, 1, 1);
            }
            drawRectF(Textures.UIs.color_wheel, clickedPosX - 4.5f, clickedPosY - 4.5f, clickedPosX + 4.5f, clickedPosY + 4.5f, 256, 0, 265, 9);  // cursor
            drawRectF(CommonColors.BLACK, (width/2f)-11, (height/2f)+94, (width/2f)+11, (height/2f)+116);  // current color back
            drawRectF(toChange, (width/2f)-10, (height/2f)+95, (width/2f)+10, (height/2f)+115);  // current color

            drawCenteredString(mc.fontRenderer, "Click to pick a color!", (width/2), (height/2)-110, 0xFFFFFF);

            for (int i = 0; i < 16; ++i) {
                int col = i / 8;
                int row = i % 8;

                // Draw common colours on the right
                int x = this.width/2 + 85 + 20*col;
                int y = this.height/2 - 93 + 20*row;
                drawRect(x + 2, y + 2, x + 18, y + 18, 0xFF000000);
                drawRect(x + 3, y + 3, x + 17, y + 17, CommonColors.set.fromCode(i).toInt());

                // Draw Minecraft chat colours on the left
                x = this.width/2 - 125 + 20*col;
                drawRect(x + 2, y + 2, x + 18, y + 18, 0xFF000000);
                drawRect(x + 3, y + 3, x + 17, y + 17, MinecraftChatColors.set.fromCode(i).toInt());
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        private float getAlpha() {
            return allowAlpha ? alphaSlider.getSliderValue() : 1;
        }

    }
}
