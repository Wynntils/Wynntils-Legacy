/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.utils.Utils;
import net.minecraft.util.text.TextFormatting;

public enum ItemTier {

    NORMAL(0, TextFormatting.WHITE),
    SET(1, TextFormatting.GREEN),
    UNIQUE(2, TextFormatting.YELLOW),
    RARE(3, TextFormatting.LIGHT_PURPLE),
    LEGENDARY(4, TextFormatting.AQUA),
    FABLED(5, TextFormatting.RED),
    MYTHIC(6, TextFormatting.DARK_PURPLE);

    int priority; String color;

    ItemTier(int priority, TextFormatting color) {
        this.priority = priority; this.color = color.toString();
    }

    public int getPriority() {
        return priority;
    }

    public String getColor() {
        return color;
    }

    public String asLore() {
        return color + Utils.capitalizeFirst(toString().toLowerCase()) + " Item";
    }

}
