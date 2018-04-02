package cf.wynntils.core.framework.ui.elements;

import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Texture;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.ui.UI;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UIEButton extends UIEClickZone {

    public static CustomColor TEXTCOLOR_NORMAL = CustomColor.fromString("ffffff",1f);
    public static CustomColor TEXTCOLOR_HOVERING = CustomColor.fromString("ffff6b",1f);
    public static CustomColor TEXTCOLOR_NOTACTIVE = CustomColor.fromString("b2b2b2",1f);

    public Texture texture;
    public String text;
    public int setWidth;

    public boolean visible = true;

    public UIEButton(String text, Texture texture, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI,MouseButton> onClick) {
        super(anchorX, anchorY, offsetX, offsetY, setWidth, (int)texture.height/3, active, onClick);
        this.clickSound = net.minecraft.init.SoundEvents.UI_BUTTON_CLICK;
        this.text = text;
        this.texture = texture;
        this.setWidth = setWidth;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        if(!visible) return;

        width = (int) Math.max( this.setWidth < 0 ? (int)getStringWidth(text) - this.setWidth : this.setWidth, texture.width);

        if (!active) {
            drawRect(texture, this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0.5f, (2.0f/3.0f), 0.5f, 1f);
            drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0f, (2.0f/3.0f), 0.5f, 1f);
            drawRect(texture, this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, (2.0f/3.0f), 1f, 1f);
            drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_NOTACTIVE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        } else if (hovering) {
            drawRect(texture, this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0.5f, (1.0f/3.0f), 0.5f, (2.0f/3.0f));
            drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0f, (1.0f/3.0f), 0.5f, (2.0f/3.0f));
            drawRect(texture, this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, (1.0f/3.0f), 1f, (2.0f/3.0f));
            drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_HOVERING, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        } else {
            drawRect(texture, this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0.5f, 0f, 0.5f, (1.0f/3.0f));
            drawRect(texture, this.position.getDrawingX(), this.position.getDrawingY(), this.position.getDrawingX() + (int)(texture.width*0.5f), this.position.getDrawingY() + height, 0f, 0f, 0.5f, (1.0f/3.0f));
            drawRect(texture, this.position.getDrawingX() + width - (int)(texture.width*0.5f), this.position.getDrawingY(), this.position.getDrawingX() + width, this.position.getDrawingY() + height, 0.5f, 0f, 1f, (1.0f/3.0f));
            drawString(text,this.position.getDrawingX()+width/2,this.position.getDrawingY()+height/2-4f, TEXTCOLOR_NORMAL, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        }
    }
}
