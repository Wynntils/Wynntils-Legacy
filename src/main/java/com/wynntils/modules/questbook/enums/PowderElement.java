package com.wynntils.modules.questbook.enums;

import net.minecraft.util.text.TextFormatting;

public enum PowderElement {
    EARTH("✤", 10, 2, TextFormatting.DARK_GREEN, TextFormatting.GREEN), // light and dark colors are swapped
    THUNDER("✦", 11, 14, TextFormatting.YELLOW, TextFormatting.GOLD),
    WATER("❉", 12, 6, TextFormatting.AQUA, TextFormatting.DARK_AQUA),
    FIRE("✹", 9, 1, TextFormatting.RED, TextFormatting.DARK_RED),
    AIR("❋", 8, 7, TextFormatting.WHITE, TextFormatting.GRAY);

    final String symbol;
    final int lowTierDamage, highTierDamage;
    final TextFormatting lightColor, darkColor;

    PowderElement(String symbol, int lowTierDamage, int highTierDamage, TextFormatting lightColor, TextFormatting darkColor) {
        this.symbol = symbol;
        this.lowTierDamage = lowTierDamage;
        this.highTierDamage = highTierDamage;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getLowTierDamage() {
        return lowTierDamage;
    }

    public int getHighTierDamage() {
        return highTierDamage;
    }

    public TextFormatting getLightColor() {
        return lightColor;
    }

    public TextFormatting getDarkColor() {
        return darkColor;
    }
}
