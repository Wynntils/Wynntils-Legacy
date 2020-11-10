/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.framework.enums;

import net.minecraft.util.text.TextFormatting;

public enum CharacterGameMode {

    HARDCORE('☠', TextFormatting.RED, "Most of your items will drop upon death and your icon will be changed to gray."),
    IRONMAN('❂', TextFormatting.GOLD, "You will not be able to trade with other players, including the trade market."),
    CRAFTSMAN('✿', TextFormatting.DARK_AQUA, "You will only be allowed to use crafted items."),
    HUNTED('⚔', TextFormatting.DARK_PURPLE, "You will be in permanent hunted mode (PvP on).");

    char symbol;
    TextFormatting color;
    String description;

    CharacterGameMode(char symbol, TextFormatting color, String description) {
        this.symbol = symbol;
        this.color = color;
        this.description = description;
    }

    public char getSymbol() {
        return symbol;

    }

    public String getDescription() {
        return description;
    }

    public TextFormatting getColor() {
        return color;
    }

    public static CharacterGameMode fromSymbol(char symbol) {
        for (CharacterGameMode gm : values()) {
            if (gm.symbol != symbol) continue;

            return gm;
        }

        return null;
    }

}
