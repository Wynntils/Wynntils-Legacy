package com.wynntils.modules.questbook.instances;

public enum IconContainer {

    questPageIcon(0, 24, 221, 255),
    settingsPageIcon(283, 304, 221, 263),
    itemGuideIcon(307, 325, 221, 261),
    hudConfigIcon(262, 283, 261, 303);

    private int x1, x2, y1, y2;

    IconContainer(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1(boolean hovered) {
        return y1 + (hovered ? getHeight() : 0);
    }

    public int getWidth() {
        return (x2 - x1)/2;
    }

    public int getHeight() {
        return (y2 - y1)/2;
    }
}
