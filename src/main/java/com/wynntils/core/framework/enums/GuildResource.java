/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.enums;

import net.minecraft.util.text.TextFormatting;

public enum GuildResource {

    EMERALD(TextFormatting.GREEN, ""),
    ORE(TextFormatting.WHITE, "Ⓑ"),
    WOOD(TextFormatting.GOLD, "Ⓒ"),
    FISH(TextFormatting.AQUA, "Ⓚ"),
    CROPS(TextFormatting.YELLOW, "Ⓙ");

    TextFormatting color;
    String symbol;

    GuildResource(TextFormatting color, String symbol) {
        this.color = color;
    }

    public TextFormatting getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

}
