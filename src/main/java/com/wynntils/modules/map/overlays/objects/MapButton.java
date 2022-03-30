/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.overlays.enums.MapButtonIcon;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraft.client.renderer.GlStateManager.*;

public class MapButton {

    int startX, startY;
    int endX, endY;

    MapButtonIcon icon;
    List<String> hoverLore;
    BiConsumer<MapButton, Integer> onClick;
    Supplier<Integer> state;
    Function<Void, Boolean> isEnabled;

    ScreenRenderer renderer = new ScreenRenderer();

    public MapButton(int posX, int posY, MapButtonIcon icon, List<String> hoverLore, Function<Void, Boolean> isEnabled, BiConsumer<MapButton, Integer> onClick) {
        int halfWidth = icon.getWidth() / 2;
        int halfHeight = icon.getHeight() / 2;

        this.startX = posX - halfWidth;
        this.startY = posY - halfHeight;
        this.endX = posX + halfWidth;
        this.endY = posY + halfHeight;

        this.icon = icon;
        this.hoverLore = hoverLore;
        this.isEnabled = isEnabled;
        this.onClick = onClick;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        pushMatrix();
        {
            if (isEnabled.apply(null)) {
                color(1f, 1f, 1f, 1f);
            } else {
                color(.3f, .3f, .3f, 1f);
            }

            // icon itself
            renderer.drawRect(Textures.Map.map_buttons, startX, startY, endX, endY,
                    icon.getStartX(), icon.getStartY(), icon.getEndX(), icon.getEndY());
        }
        popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        McIf.mc().getSoundHandler().playSound(
                PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f)
        );

        onClick.accept(this, mouseButton);
    }

    public Boolean isEnabled() {
        return isEnabled.apply(null);
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

    public MapButtonIcon getIcon() {
        return icon;
    }

    public void editHoverLore(List<String> newLore) {
        this.hoverLore = newLore;
    }
}
