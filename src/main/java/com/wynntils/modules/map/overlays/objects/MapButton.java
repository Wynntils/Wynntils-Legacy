/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.overlays.enums.MapButtonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.client.renderer.GlStateManager.*;

public class MapButton extends GuiScreen {

    int startX, startY;
    int endX, endY;

    MapButtonType type;
    List<String> hoverLore;
    Consumer<Integer> onClick;

    ScreenRenderer renderer = new ScreenRenderer();

    public MapButton(int posX, int posY, MapButtonType type, List<String> hoverLore, Consumer<Integer> onClick) {
        int halfWidth = type.getWidth() / 2;
        int halfHeight = type.getHeight() / 2;

        this.startX = posX - halfWidth;
        this.startY = posY - halfHeight;
        this.endX = posX + halfWidth;
        this.endY = posY + halfHeight;

        this.type = type;
        this.hoverLore = hoverLore;
        this.onClick = onClick;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        pushMatrix();
        {
            color(1f, 1f, 1f, 1f);

            // icon itself
            renderer.drawRect(Textures.Map.map_buttons, startX, startY, endX, endY,
                    type.getStartX(), type.getStartY(), type.getEndX(), type.getEndY());
        }
        popMatrix();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (type.isIgnoreAction()) return;

        Minecraft.getMinecraft().getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f)
        );

        onClick.accept(mouseButton);
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public List<String> getHoverLore() {
        return hoverLore;
    }

    public MapButtonType getType() {
        return type;
    }

}
