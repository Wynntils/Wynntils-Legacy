/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.enums;

public enum MapButtonType {

    PLUS(0, 46, 14, 60, 0),
    PIN(14, 46, 26, 62, 1),
    PIN_2(66 ,46, 78, 62, 1),
    PENCIL(26, 46, 34, 62, 0),
    SEARCH(34 ,46, 50, 62, 2),
    CENTER(50, 46, 66, 62, 3),
    SHARE(78, 46, 94, 60, 4),
    BACK(94, 46, 110, 58, 1),
    INFO(110, 46, 120, 62, 5),
    BASE(0, 0, 144, 26, 0, true);

    int startX, startY;
    int endX, endY;
    int offsetX;

    boolean ignoreAction;

    MapButtonType(int startX, int startY, int endX, int endY, int offsetX, boolean ignoreAction) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.offsetX = offsetX;

        // check if positions are inverted

        this.ignoreAction = ignoreAction;
    }

    MapButtonType(int startX, int startY, int endX, int endY, int offsetX) {
        this(startX, startY, endX, endY, offsetX,false);
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getWidth() {
        return endX - startX;
    }

    public int getHeight() {
        return endY - startY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public boolean isIgnoreAction() {
        return ignoreAction;
    }

}
