package com.wynntils.modules.core.enums;

public enum OverlayRotation {
    NORMAL(0),
    ONE_QUARTER(90),
    THREE_QUARTERS(270);

    private int degrees;

    OverlayRotation(int degrees) {
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }
}
