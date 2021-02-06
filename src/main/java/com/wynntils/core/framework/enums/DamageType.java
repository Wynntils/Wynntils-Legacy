/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums;

import net.minecraft.util.text.TextFormatting;

import java.util.regex.Pattern;

public enum DamageType {

    NEUTRAL("❤", TextFormatting.DARK_RED),
    EARTH("✤", TextFormatting.DARK_GREEN),
    FIRE("✹", TextFormatting.RED),
    WATER("❉", TextFormatting.AQUA),
    THUNDER("✦", TextFormatting.YELLOW),
    AIR("❋", TextFormatting.WHITE);

    private final String symbol;
    private final TextFormatting color;

    DamageType(String symbol, TextFormatting color) {
        this.symbol = symbol;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public TextFormatting getColor() {
        return color;
    }

    public static DamageType fromSymbol(String symbol) {
        for (DamageType type : values()) {
            if (type.symbol.equals(symbol))
                return type;
        }
        return null;
    }

    public static Pattern compileDamagePattern() {
        StringBuilder damageTypes = new StringBuilder();

        for (DamageType type : values()) {
            damageTypes.append(type.getSymbol());
        }

        return Pattern.compile("-(.*?) ([" + damageTypes.toString() + "])");
    }

}
