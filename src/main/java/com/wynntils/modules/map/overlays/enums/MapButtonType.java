/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.enums;

public enum MapButtonType {

    PLUS(0, 46, 14, 60),
    PIN(14, 46, 26, 62),
    PIN_2(66 ,46, 78, 62),
    PENCIL(26, 46, 34, 62),
    SEARCH(34 ,46, 50, 62),
    CENTER(50, 46, 66, 62),
    SHARE(78, 46, 94, 60),
    BACK(94, 46, 110, 58),
    INFO(110, 46, 120, 62),
    BOAT(120, 46, 135, 62),
    BASE(0, 0, 144, 26, true);

    int startX, startY;
    int endX, endY;

    boolean ignoreAction;

    MapButtonType(int startX, int startY, int endX, int endY, boolean ignoreAction) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;

        this.ignoreAction = ignoreAction;
    }

    MapButtonType(int startX, int startY, int endX, int endY) {
        this(startX, startY, endX, endY, false);
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

    public boolean isIgnoreAction() {
        return ignoreAction;
    }

}
