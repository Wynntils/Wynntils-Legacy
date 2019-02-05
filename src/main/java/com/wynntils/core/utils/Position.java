/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

public class Position {

    public int drawingX = -1, drawingY = -1;
    public int offsetX = 0, offsetY = 0;
    public float anchorX = 0.0f, anchorY = 0.0f;

    public int getDrawingX() {
        return drawingX;
    }

    public int getDrawingY() {
        return drawingY;
    }

    public void refresh(ScaledResolution screen) {
        if(screen == null) return;
        drawingX = offsetX + MathHelper.fastFloor(anchorX*screen.getScaledWidth());
        drawingY = offsetY + MathHelper.fastFloor(anchorY*screen.getScaledHeight());
    }

    public void refresh() {
        refresh(ScreenRenderer.screen);
    }

    public void copy(Position position) {
        this.anchorX = position.anchorX;
        this.anchorY = position.anchorY;
        this.offsetX = position.offsetX;
        this.offsetY = position.offsetY;
        this.drawingX = position.drawingX;
        this.drawingY = position.drawingY;
    }

}
