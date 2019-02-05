/*
 *  * Copyright © Wynntils - 2019.
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

    public static CustomColor TEXTCOLOR_NORMAL = CustomColor.fromString("ffffff",1f);
    public static CustomColor TEXTCOLOR_HOVERING = CustomColor.fromString("ffff6b",1f);
    public static CustomColor TEXTCOLOR_NOTACTIVE = CustomColor.fromString("b2b2b2",1f);

    public Texture texture;
    public String text;
    public int setWidth;

    public boolean visible = true;

    public UIEButton(String text, Texture texture, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick) {
        super(anchorX, anchorY, offsetX, offsetY, setWidth, texture == null ? 1 : (int)texture.height/3, active, onClick);
        this.clickSound = net.minecraft.init.SoundEvents.UI_BUTTON_CLICK;
        this.text = text;
        this.texture = texture;
        this.setWidth = setWidth;
    }

    @Override
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        if(button == MouseButton.LEFT)
            super.click(mouseX, mouseY, button, ui);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        if(!visible) return;

        width = (int) Math.max( this.setWidth < 0 ? (int)getStringWidth(text) - this.setWidth : this.setWidth, texture == null ? 0 : texture.width);

        if (!active) {
            if(texture != null) {
                drawRect(texture, this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0.5f, (2.0f / 3.0f), 0.5f, 1f);
                drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0f, (2.0f / 3.0f), 0.5f, 1f);
                drawRect(texture, this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, (2.0f / 3.0f), 1f, 1f);
            }
            if(text != null && !text.equals(""))
                drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_NOTACTIVE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        } else if (hovering) {
            if(texture != null) {
                drawRect(texture, this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0.5f, (1.0f / 3.0f), 0.5f, (2.0f / 3.0f));
                drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0f, (1.0f / 3.0f), 0.5f, (2.0f / 3.0f));
                drawRect(texture, this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, (1.0f / 3.0f), 1f, (2.0f / 3.0f));
            }
            if(text != null && !text.equals(""))
                drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_HOVERING, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        } else {
            if(texture != null) {
                drawRect(texture, this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0.5f, 0f, 0.5f, (1.0f / 3.0f));
                drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int) (texture.width * 0.5f), this.position.getDrawingY() + height, 0f, 0f, 0.5f, (1.0f / 3.0f));
                drawRect(texture, this.position.getDrawingX() + width - (int) (texture.width * 0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, 0f, 1f, (1.0f / 3.0f));
            }
            if(text != null && !text.equals(""))
                drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_NORMAL, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        }
    }

    public static class Toggle extends UIEButton {
        public boolean value = true;
        public String trueText, falseText;
        public Texture trueTexture, falseTexture;

        public Toggle(String trueText, Texture trueTexture, String falseText, Texture falseTexture, boolean value, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick) {
            super(value ? trueText : falseText, value ? trueTexture : falseTexture, anchorX, anchorY, offsetX, offsetY, setWidth, active, onClick);
            this.trueText = trueText;
            this.trueTexture = trueTexture;
            this.falseText = falseText;
            this.falseTexture = falseTexture;
            this.value = value;
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            if(active && hovering && button == MouseButton.LEFT) {
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
        public Object value;
        public Function<String, String> displayTextFunc;

        public Enum(Function<String, String> displayTextFunc, Texture texture, Class<? extends java.lang.Enum> e, Object value, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick) {
            super("", texture, anchorX, anchorY, offsetX, offsetY, setWidth, active, onClick);
            this.displayTextFunc = displayTextFunc;
            this.e = e;
            this.value = value;
            this.text = displayTextFunc.apply(getValueDisplayName());
        }

        @Override
        public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
            hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
            if(active && hovering && button == MouseButton.LEFT) {
                Object[] eArr = e.getEnumConstants();
                int i = ArrayUtils.indexOf(eArr,value) + 1;
                this.value = eArr[i >= eArr.length ? 0 : i];
                this.text = displayTextFunc.apply(getValueDisplayName());
                super.click(true, button, ui);
            }
        }

        public String getValueDisplayName() {
            for(Field f : value.getClass().getFields())
                if(f.getType().isAssignableFrom(String.class) && f.getName().equals("displayName")) //This might be flipped
                    try {
                        return (String) f.get(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            return ((java.lang.Enum)value).name();
        }
    }
}
