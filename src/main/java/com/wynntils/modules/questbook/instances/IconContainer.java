package com.wynntils.modules.questbook.instances;

public enum IconContainer {

    questPageIcon(0, 24, 221, 255, true),
    settingsPageIcon(283, 304, 221, 263, true),
    itemGuideIcon(307, 325, 221, 261, true),
    hudConfigIcon(262, 283, 261, 303, true),
    discoveriesIcon(206, 221, 252, 267, false);

    private int x1, x2, y1, y2;
    private boolean highlightVariant;

    IconContainer(int x1, int x2, int y1, int y2, boolean highlightVariant) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.highlightVariant = highlightVariant;
    }

    public int getX1() {
        return x1;
    }

    public int getY1(boolean hovered) {
        return y1 + (hovered && highlightVariant ? getHeight() : 0);
    }

    public int getWidth() {
        return x2 - x1;
    }

    public int getHeight() {
        return highlightVariant ? (y2 - y1)/2 : y2 - y1;
    }
}
