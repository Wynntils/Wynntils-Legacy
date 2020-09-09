package com.wynntils.core.framework.ui.elements;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldWynn extends GuiTextField {

    private static final Color TEXT_FIELD_COLOR_1 = new Color(87, 65, 51);
    private static final Color TEXT_FIELD_COLOR_2 = new Color(120, 90, 71);

    public GuiTextFieldWynn(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);

        this.setEnableBackgroundDrawing(false);
    }

    @Override
    public void drawTextBox() {
        drawRect(this.x - 2, this.y - 1, this.x + this.width - 1, this.y + this.height - 1, TEXT_FIELD_COLOR_1.getRGB());
        drawRect(this.x -1, this.y, this.x + this.width - 2, this.y + this.height - 2, TEXT_FIELD_COLOR_2.getRGB());
        super.drawTextBox();
    }

}
