/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEvent extends Event {

    public static class DrawItemOverlay extends RenderEvent {

        private final ItemStack stack;
        private final int x, y;
        private String overlayText;
        private CustomColor overlayTextCol = CommonColors.WHITE;
        private boolean overlayTextChanged = false;

        public DrawItemOverlay(ItemStack stack, int x, int y, String overlayText) {
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.overlayText = overlayText;
        }

        public ItemStack getStack() {
            return stack;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getOverlayText() {
            return overlayText;
        }

        public void setOverlayText(String text) {
            this.overlayText = text;
            this.overlayTextChanged = true;
        }

        public CustomColor getOverlayTextColor() {
            return overlayTextCol;
        }

        public void setOverlayTextColor(CustomColor col) {
            this.overlayTextCol = col;
            this.overlayTextChanged = true;
        }

        public boolean isOverlayTextChanged() {
            return overlayTextChanged;
        }

    }

}
