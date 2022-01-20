/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums;

public enum Priority {

    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;

    public static Priority valueOf(int i) {
        return Priority.values()[i];
    }

}
