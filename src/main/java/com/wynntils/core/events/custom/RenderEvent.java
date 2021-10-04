/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEvent extends Event {

    public static class DrawItemOverlay extends RenderEvent {

        protected final ItemStack stack;
        protected final int x, y;
        protected String overlayText;
        protected CustomColor overlayTextCol = CommonColors.WHITE;
        protected boolean overlayTextChanged = false;

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

        public CustomColor getOverlayTextColor() {
            return overlayTextCol;
        }

        public boolean isOverlayTextChanged() {
            return overlayTextChanged;
        }

        public static class Pre extends DrawItemOverlay {

            public Pre(ItemStack stack, int x, int y, String overlayText) {
                super(stack, x, y, overlayText);
            }

            @Override
            public boolean isCancelable() {
                return true;
            }

            public void setOverlayText(String text) {
                this.overlayText = text;
                this.overlayTextChanged = true;
            }

            public void setOverlayTextColor(CustomColor col) {
                this.overlayTextCol = col;
                this.overlayTextChanged = true;
            }
        }

        public static class Post extends DrawItemOverlay {

            public Post(ItemStack stack, int x, int y, String overlayText) {
                super(stack, x, y, overlayText);
            }

            @Override
            public boolean isCancelable() {
                return false;
            }
        }
    }

}
