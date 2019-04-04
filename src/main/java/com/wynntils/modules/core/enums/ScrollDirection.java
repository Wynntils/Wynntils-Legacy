package com.wynntils.modules.core.enums;

public enum ScrollDirection {

    UP(-1, "wynntils.config.core.enum.scroll_direction.up"),
    DOWN(1, "wynntils.config.core.enum.scroll_direction.down");

    private int scrollDirection;
    public String displayName;

    ScrollDirection(int scrollDirection, String displayName) {
        this.scrollDirection = scrollDirection;
        this.displayName = displayName;
    }

    public int getScrollDirection() {
        return scrollDirection;
    }

}
