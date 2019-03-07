package com.wynntils.modules.core.enums;

public enum ScrollDirection {

    UP(-1),
    DOWN(1);

    private int scrollDirection;

    ScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public int getScrollDirection() {
        return scrollDirection;
    }

}
