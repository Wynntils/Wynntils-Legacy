/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.enums;

public enum UpdateStream {

    STABLE("wynntils.config.core.enum.update_stream.stable"),
    CUTTING_EDGE("wynntils.config.core.enum.update_stream.cutting_edge");

    public String displayName;

    UpdateStream(String displayName) {
        this.displayName = displayName;
    }
}
