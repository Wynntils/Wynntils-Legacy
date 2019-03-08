/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class UIEColorWheel extends UIEClickZone {

    private static final Pattern hexChecker = Pattern.compile("(^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$)");

    CustomColor color = new CustomColor(1, 1, 1);
    GuiScreen backGui;
    Consumer<CustomColor> onAccept;
    UIETextBox textBox;

    public UIEColorWheel(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, boolean active, Consumer<CustomColor> onAccept, GuiScreen backGui) {
        super(anchorX, anchorY, offsetX, offsetY, width, height, active, (ui, mouse) -> {});

        this.backGui = backGui;
        this.clickSound = SoundEvents.UI_BUTTON_CLICK;
        this.onAccept = onAccept;

        textBox = new UIETextBox(0, 0, 0, 16, 120, true, String.format("#%02X%02X%02X", (int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255)), false, (ui, t) -> {
            if(!hexChecker.matcher(textBox.getText()).find()) {
                textBox.setColor(0xFF5555);
                return;
            }

            textBox.setColor(0x55FF55);
            color = CustomColor.fromString(textBox.getText().replace("#", ""), 1);
            onAccept.accept(color);
        });

        textBox.setColor(0x55FF55);
    }

    public void setColor(CustomColor color) {
        this.color = color;

        textBox.setText(String.format("#%02X%02X%02X", (int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255)));
    }

    @Override
    public void tick(long ticks) {
        textBox.tick(ticks);

        super.tick(ticks);
    }

    @Override
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        textBox.click(mouseX, mouseY, button, ui);

        if(hovering) mc.displayGuiScreen(new ColorPickerGUI());
    }

    public void keyTyped(char c, int i, UI ui) {
        textBox.keyTyped(c, i, ui);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        drawRect(CommonColors.BLACK, position.drawingX, position.drawingY, position.drawingX + width+2, position.drawingY + height+2); // HeyZeer0: this makes a black box behind the color
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

        int clickedPosX, clickedPosY = 0;

        String text = "";

        public ColorPickerGUI() {
            toChange = color;
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if(button == applyButton) {
                color = toChange;

                mc.displayGuiScreen(backGui);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound,1f));
                onAccept.accept(color);
                textBox.setText(String.format("#%02X%02X%02X", (int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255)));
            }else if(button == cancelButton) {
                mc.displayGuiScreen(backGui);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound,1f));
            }
        }

        @Override
        public void initGui() {
            buttonList.add(applyButton = new GuiButton(0, width/2 - 65, height/2 + 95, 50, 20, TextFormatting.GREEN + "Apply"));
            buttonList.add(cancelButton = new GuiButton(1, (width/2) + 15, height/2 + 95, 50, 20, TextFormatting.RED + "Cancel"));

            super.initGui();
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            int circleRadius = 160/2;
            float mousePosX = ((mouseX - ((width/2f)-80)) / circleRadius) - 1;
            float mousePosY = ((mouseY - ((height/2f)-80)) / circleRadius) - 1;

            double s = Math.sqrt(Math.pow(mousePosX,2) + Math.pow(mousePosY, 2));
            if(s > 1.0) {
                super.mouseClicked(mouseX, mouseY, mouseButton);
                return;
            }

            clickedPosX = mouseX; clickedPosY = mouseY;
            double h = Math.atan2(-mousePosY, mousePosX) / (2*Math.PI);
            if(h < 0) h += 1;

            text = h + "";

            toChange = CustomColor.fromHSV((float)h, (float)s, 1, 1);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();

            beginGL(0, 0);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            drawRectF(Textures.UIs.color_wheel, (width/2f)-80, (height/2f)-80, (width/2f)+80, (height/2f)+80, 0, 0, 256, 256); //rgb wheel
            drawRectF(Textures.UIs.color_wheel, clickedPosX - 4.5f, clickedPosY - 4.5f, clickedPosX + 4.5f, clickedPosY + 4.5f, 256, 0, 265, 9); //cursor
            drawRectF(CommonColors.BLACK, (width/2f)-11, (height/2f)+94, (width/2f)+11, (height/2f)+116); //current color back
            drawRectF(toChange, (width/2f)-10, (height/2f)+95, (width/2f)+10, (height/2f)+115); //current color

            drawCenteredString(mc.fontRenderer, "Color Wheel", (width/2), (height/2)-110, 0xFFFFFF);
            drawCenteredString(mc.fontRenderer, "Click to pick a color!", (width/2), (height/2)-100, 0xFFFFFF);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

    }
}
