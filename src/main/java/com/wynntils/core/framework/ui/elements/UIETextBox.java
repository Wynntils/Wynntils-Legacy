/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.ui.UI;
import net.minecraft.client.gui.GuiTextField;

import java.util.function.BiConsumer;

public class UIETextBox extends UIEClickZone {
    public GuiTextField textField;
    public boolean textDisappearsOnNextClick;
    public BiConsumer<UI, String> onTextChanged;

    public UIETextBox(float anchorX, float anchorY, int offsetX, int offsetY, int width, boolean active, String text, boolean textDisappearsOnNextClick, BiConsumer<UI, String> onTextChanged) {
        super(anchorX, anchorY, offsetX, offsetY, width, SmartFontRenderer.CHAR_HEIGHT, active, null);
        this.textField = new GuiTextField(this.getId(), ScreenRenderer.fontRenderer, this.position.getDrawingX(), this.position.getDrawingY(), width,20);
        this.textField.setText(text);
        this.textDisappearsOnNextClick = textDisappearsOnNextClick;
        this.onTextChanged = onTextChanged;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);

        this.textField.x = this.position.getDrawingX();
        this.textField.y = this.position.getDrawingY();
        this.textField.setEnabled(active);
        this.textField.drawTextBox();
    }

    public void keyTyped(char c, int i, UI ui) {
        String old = textField.getText();
        this.textField.textboxKeyTyped(c,i);
        this.onTextChanged.accept(ui,old);
    }

    @Override
    public void tick(long ticks) {
        this.textField.updateCursorCounter();
    }

    @Override
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        this.textField.mouseClicked(mouseX,mouseY,button.ordinal());
        if(textDisappearsOnNextClick && (mouseX >= this.textField.x && mouseX < this.textField.x + this.textField.width && mouseY >= this.textField.y && mouseY < this.textField.y + this.textField.height) && button == MouseButton.LEFT) {
            textField.setText("");
            textDisappearsOnNextClick = false;
        }
    }

    public void setColor(int color) {
        textField.setTextColor(color);
    }

    public void setText(String textIn) {
        textField.setText(textIn);
    }

    public String getText() {
        return textField.getText();
    }

    public void writeText(String textToWrite) {
        textField.writeText(textToWrite);
    }
}
