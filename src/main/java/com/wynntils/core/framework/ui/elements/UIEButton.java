/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Texture;
import com.wynntils.core.framework.ui.UI;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class UIEButton extends UIEClickZone {

    public static CustomColor TEXTCOLOR_NORMAL = CustomColor.fromInt(0xffffff, 1f);
    public static CustomColor TEXTCOLOR_HOVERING = CustomColor.fromInt(0xffff6b, 1f);
    public static CustomColor TEXTCOLOR_NOTACTIVE = CustomColor.fromInt(0xb2b2b2, 1f);

    public Texture texture;
    public String text;
    public int setWidth;
    public int tx1;
    public int ty1;
    public int tx2;
    public int ty2;

    public UIEButton(String text, Texture texture, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick, int tx1, int ty1, int tx2, int ty2) {
        super(anchorX, anchorY, offsetX, offsetY, setWidth, texture == null ? 1 : (ty2 - ty1) / 3, active, onClick);
        this.clickSound = net.minecraft.init.SoundEvents.UI_BUTTON_CLICK;
        this.text = text;
        this.texture = texture;
        this.setWidth = setWidth;
        this.tx1 = tx1;
        this.tx2 = tx2;
        this.ty1 = ty1;
        this.ty2 = ty2;
    }

    @Override
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        if (button == MouseButton.LEFT)
            super.click(mouseX, mouseY, button, ui);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        if (!visible) return;

        beginGL(0, 0);
        {
            width = (int) Math.max(this.setWidth < 0 ? (int) getStringWidth(text) - this.setWidth : this.setWidth, texture == null ? 0 : tx2 - tx1);

            if (!active) {
                if (texture != null) {
                    drawRect(texture, this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, (tx1 + tx2) / 2, (ty2 - ty1) / 3 * 2 + ty1, (tx1 + tx2) / 2, ty2);
                    drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, tx1, (ty2 - ty1) / 3 * 2 + ty1, (tx1 + tx2) / 2, ty2);
                    drawRect(texture, this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, (tx1 + tx2) / 2, (ty2 - ty1) / 3 * 2 + ty1, tx2, ty2);
                }
                if (text != null && !text.equals(""))
                    drawString(text, this.position.getDrawingX() + width / 2.0f, this.position.getDrawingY() + height / 2.0f - 4.0f, TEXTCOLOR_NOTACTIVE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            } else if (hovering) {
                if (texture != null) {
                    drawRect(texture, this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, (tx1 + tx2) / 2, (ty2 - ty1) / 3 + ty1, (tx1 + tx2) / 2, (ty2 - ty1) / 3 * 2 + ty1);
                    drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, tx1, (ty2 - ty1) / 3 + ty1, (tx1 + tx2) / 2, (ty2 - ty1) / 3 * 2 + ty1);
                    drawRect(texture, this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, (tx1 + tx2) / 2, (ty2 - ty1) / 3 + ty1, tx2, (ty2 - ty1) / 3 * 2 + ty1);
                }
                if (text != null && !text.equals(""))
                    drawString(text, this.position.getDrawingX() + width / 2.0f, this.position.getDrawingY() + height / 2.0f - 4.0f, TEXTCOLOR_HOVERING, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            } else {
                if (texture != null) {
                    drawRect(texture, this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, (tx1 + tx2) / 2, ty1, (tx1 + tx2) / 2, (ty2 - ty1) / 3 + ty1);
                    drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) ((tx1 + tx2) / 2), this.position.getDrawingY() + height, tx1, ty1, (tx1 + tx2) / 2, (ty2 - ty1) / 3 + ty1);
                    drawRect(texture, this.position.getDrawingX() + width - (int) ((tx1 + tx2) / 2), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, (tx1 + tx2) / 2, ty1, tx2, (ty2 - ty1) / 3 + ty1);
                }
                if (text != null && !text.equals(""))
                    drawString(text, this.position.getDrawingX() + width / 2.0f, this.position.getDrawingY() + height / 2.0f - 4.0f, TEXTCOLOR_NORMAL, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            }
        }
        endGL();
    }

    public static class Toggle extends UIEButton {
        public boolean value;
        public String trueText, falseText;
        public Texture trueTexture, falseTexture;

        public Toggle(String trueText, Texture trueTexture, String falseText, Texture falseTexture, boolean value, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick, int tx1, int ty1, int tx2, int ty2) {
            super(value ? trueText : falseText, value ? trueTexture : falseTexture, anchorX, anchorY, offsetX, offsetY, setWidth, active, onClick, tx1, ty1, tx2, ty2);
            this.trueText = trueText;
            this.trueTexture = trueTexture;
            this.falseText = falseText;
            this.falseTexture = falseTexture;
            this.value = value;
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            if (active && hovering && (button == MouseButton.LEFT || button == MouseButton.RIGHT)) {
                this.value = !value;
                super.click(true, button, ui);
            }
        }

        @Override
        public void render(int mouseX, int mouseY) {
            this.text = value ? trueText : falseText;
            this.texture = value ? trueTexture : falseTexture;
            super.render(mouseX, mouseY);
        }
    }

    public static class Enum extends UIEButton {
        public Class<? extends java.lang.Enum> e;
        public java.lang.Enum value;
        public Function<String, String> displayTextFunc;

        public Enum(Function<String, String> displayTextFunc, Texture texture, Class<? extends java.lang.Enum> e, java.lang.Enum value, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick, int tx1, int ty1, int tx2, int ty2) {
            super("", texture, anchorX, anchorY, offsetX, offsetY, setWidth, active, onClick, tx1, ty1, tx2, ty2);
            this.displayTextFunc = displayTextFunc;
            this.e = e;
            this.value = value;
            this.text = displayTextFunc.apply(getValueDisplayName());
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            int delta = 0;
            if (active && hovering) {
                if (button == MouseButton.LEFT) {
                    delta = +1;
                } else if (button == MouseButton.RIGHT) {
                    delta = -1;
                }
            }
            if (delta != 0) {
                java.lang.Enum[] eArr = e.getEnumConstants();
                int oldIndex = ArrayUtils.indexOf(eArr, value);
                int i = oldIndex == -1 ? 0 : oldIndex + delta;
                if (i == -1) i = eArr.length - 1;
                this.value = eArr[i >= eArr.length ? 0 : i];
                this.text = displayTextFunc.apply(getValueDisplayName());
                super.click(true, button, ui);
            }
        }

        public String getValueDisplayName() {
            for (Field f : e.getFields())
                if (f.getType().isAssignableFrom(String.class) && f.getName().equals("displayName"))  // This might be flipped
                    try {
                        return (String) f.get(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            return value.name();
        }
    }
}
