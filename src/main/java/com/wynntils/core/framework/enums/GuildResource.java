/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.enums;

import net.minecraft.util.text.TextFormatting;

public enum GuildResource {

    EMERALD(TextFormatting.GREEN, "Emeralds", ""),
    ORE(TextFormatting.WHITE, "Ores", "Ⓑ"),
    WOOD(TextFormatting.GOLD, "Wood", "Ⓒ"),
    FISH(TextFormatting.AQUA, "Fishes", "Ⓚ"),
    CROPS(TextFormatting.YELLOW, "Crops", "Ⓙ");

    TextFormatting color;
    String name;
    String symbol;

    GuildResource(TextFormatting color, String name, String symbol) {
        this.color = color;
        this.name = name;
        this.symbol = symbol;
    }

    public TextFormatting getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getPrettySymbol() {
        return color + symbol + (!symbol.isEmpty() ? " " : "");
    }

}
