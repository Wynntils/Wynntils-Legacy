/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.McIf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

/**
 * Fixes net.minecraft.client.gui.GuiButtonImage so that it highlights properly
 * and can also be scaled
 */
public class GuiButtonImageBetter extends GuiButtonImage {
    private static final GuiButton highlightFixHovering = new GuiButton(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 1, 1, "");
    private static final GuiButton highlightFixNoHovering = new GuiButton(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0, "");

    private float scaleFactor;
    private float scaleFromX;
    private float scaleFromY;
    private int scaledStartX;
    private int scaledStartY;
    private int scaledEndX;
    private int scaledEndY;
    private boolean highlight;

    public GuiButtonImageBetter(int buttonId, int x, int y, int width, int height, int textureOffsetX, int textureOffsetY, int hoverImageYOffset, ResourceLocation resource) {
        super(buttonId, x, y, width, height, textureOffsetX, textureOffsetY, hoverImageYOffset, resource);
        setScaleFactor(1, width / 2f, height / 2f);
        highlight = true;
    }

    public GuiButtonImageBetter(int buttonId, int x, int y, int width, int height, int textureOffsetX, int textureOffsetY, ResourceLocation resource) {
        this(buttonId, x, y, width, height, textureOffsetX, textureOffsetY, 0, resource);
    }

    public GuiButtonImageBetter setScaleFactor(float scaleFactor, float originX, float originY) {
        this.scaleFactor = scaleFactor;
        this.scaleFromX = originX + x;
        this.scaleFromY = originY + y;
        float scaledStartX = scaleFromX - (scaleFromX - x) * scaleFactor;
        float scaledStartY = scaleFromY - (scaleFromY - y) * scaleFactor;
        this.scaledStartX = MathHelper.floor(scaledStartX);
        this.scaledStartY = MathHelper.floor(scaledStartY);
        this.scaledEndX = MathHelper.ceil(scaledStartX + width * scaleFactor);
        this.scaledEndY = MathHelper.ceil(scaledStartY + width * scaleFactor);
        return this;
    }

    public GuiButtonImageBetter setScaleFactor(float scaleFactor) {
        setScaleFactor(scaleFactor, scaleFromX - x, scaleFromY - y);
        return this;
    }

    public GuiButtonImageBetter setScaleOrigin(float originX, float originY) {
        setScaleFactor(scaleFactor, originX, originY);
        return this;
    }

    /**
     * If `doHighlight(false)` is called, doesn't highlight (yellow tint) when hovering
     */
    public GuiButtonImageBetter doHighlight(boolean highlight) {
        this.highlight = highlight;
        return this;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        setColour(this.highlight && mouseX >= scaledStartX && mouseY >= scaledStartY && mouseX < scaledEndX && mouseY < scaledEndY, this.enabled);

        if (scaleFactor != 1f) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-scaleFromX, -scaleFromY, 0);
            GlStateManager.scale(scaleFactor, scaleFactor, 1);
            GlStateManager.translate(scaleFromX, scaleFromY, 0);
        }
        super.drawButton(minecraft, mouseX, mouseY, partialTicks);
        if (scaleFactor != 1f) {
            GlStateManager.popMatrix();
        }

        setColour(false, true);
    }

    public static void setColour(boolean hovering, boolean enabled) {
        if (hovering) {
            highlightFixHovering.enabled = enabled;
            highlightFixHovering.drawButton(McIf.mc(), Integer.MIN_VALUE, Integer.MIN_VALUE, 0);
        } else {
            highlightFixNoHovering.enabled = enabled;
            highlightFixNoHovering.drawButton(McIf.mc(), 0, 0, 0);
        }
    }
}
