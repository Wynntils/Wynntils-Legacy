/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.framework.ui.UIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundEvent;

import java.util.function.BiConsumer;

public class UIEClickZone extends UIElement {
    protected BiConsumer<UI, MouseButton> onClick;
    public int width, height;
    public SoundEvent clickSound;

    public boolean active;

    protected boolean hovering = false;
    public boolean isHovering() { return hovering; }

    public UIEClickZone(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, boolean active, BiConsumer<UI,MouseButton> onClick) {
        super(anchorX, anchorY, offsetX, offsetY);
        this.onClick = onClick;
        this.width = width;
        this.height = height;
        this.active = active;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        hovering = mouseX >= position.getDrawingX() && mouseX < position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY < position.getDrawingY()+height;
    }

    @Override
    public void tick(long ticks) {

    }

    public void click(boolean hovering, MouseButton button, UI ui) {
        if(active && hovering) {
            if(clickSound != null)
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound,1f));
            if(onClick != null)
                onClick.accept(ui,button);
        }
    }
    public void click(int mouseX, int mouseY, MouseButton button, UI ui) {
        hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
        if(active && hovering) {
            if(clickSound != null)
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(clickSound,1f));
            if(onClick != null)
                onClick.accept(ui,button);
        }
    }

    public void release(int mouseX, int mouseY, MouseButton button, UI ui) {
        hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
    }

    public void clickMove(int mouseX, int mouseY, MouseButton button, long timeSinceLastClick, UI ui) {
        hovering = mouseX >= position.getDrawingX() && mouseX <= position.getDrawingX()+width && mouseY >= position.getDrawingY() && mouseY <= position.getDrawingY()+height;
    }
}
