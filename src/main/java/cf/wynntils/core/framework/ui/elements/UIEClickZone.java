package cf.wynntils.core.framework.ui.elements;

import cf.wynntils.ModCore;
import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.ui.UIElement;

import java.util.function.Consumer;

public class UIEClickZone extends UIElement {
    protected Consumer<MouseButton> onClick;
    public int width, height;

    public boolean active;

    protected boolean hovering = false;
    public boolean isHovering() { return hovering; }

    public UIEClickZone(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, boolean active, Consumer<MouseButton> onClick) {
        super(anchorX, anchorY, offsetX, offsetY);
        this.onClick = onClick;
        this.width = width;
        this.height = height;
        this.active = active;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;

        if(ModCore.DEBUG)
            drawRect(new CustomColor(1f,0f,0f,0.2f),position.getDrawingX(),position.getDrawingY(),position.getDrawingX()+width,position.getDrawingY()+height);
    }

    public void click(int mouseX, int mouseY, MouseButton button) {
        hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
        if(active && hovering)
            onClick.accept(button);
    }
}
