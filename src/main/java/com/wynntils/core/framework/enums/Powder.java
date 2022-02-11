/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public enum Powder {
    EARTH('✤', 10, 2, TextFormatting.DARK_GREEN, TextFormatting.GREEN), // light and dark colors are swapped
    THUNDER('✦', 11, 14, TextFormatting.YELLOW, TextFormatting.GOLD),
    WATER('❉', 12, 6, TextFormatting.AQUA, TextFormatting.DARK_AQUA),
    FIRE('✹', 9, 1, TextFormatting.RED, TextFormatting.DARK_RED),
    AIR('❋', 8, 7, TextFormatting.WHITE, TextFormatting.GRAY);

    final char symbol;
    final int lowTierDamage, highTierDamage;
    final TextFormatting lightColor, darkColor;

    Powder(char symbol, int lowTierDamage, int highTierDamage, TextFormatting lightColor, TextFormatting darkColor) {
        this.symbol = symbol;
        this.lowTierDamage = lowTierDamage;
        this.highTierDamage = highTierDamage;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public static Pattern POWDER_NAME_PATTERN = Pattern.compile("§[2ebcf8].? ?(Earth|Thunder|Water|Fire|Air|Blank) Powder ([IV]{1,3})");

    public char getSymbol() {
        return symbol;
    }

    public String getColor() {
        return lightColor.toString();
    }

    public TextFormatting getRawColor() {
        return lightColor;
    }

    public static MinecraftChatColors determineChatColor(String type) {
        switch (type.toLowerCase()) { // make it case insensitive
            default:
                return null;
            case "earth":
                return MinecraftChatColors.fromTextFormatting(Powder.EARTH.getRawColor());
            case "thunder":
                return MinecraftChatColors.fromTextFormatting(Powder.THUNDER.getRawColor());
            case "water":
                return MinecraftChatColors.fromTextFormatting(Powder.WATER.getRawColor());
            case "fire":
                return MinecraftChatColors.fromTextFormatting(Powder.FIRE.getRawColor());
            case "air":
                return MinecraftChatColors.fromTextFormatting(Powder.AIR.getRawColor());
            case "blank":
                // Powder enum doesn't have blank
                return MinecraftChatColors.GRAY; // Dark gray is too hard to see, use normal instead
        }
    }

    public String getColoredSymbol() {
        return lightColor.toString() + symbol;
    }

    public String getLetterRepresentation() {
        return this.name().substring(0, 1).toLowerCase();
    }

    public static List<Powder> findPowders(String input) {
        List<Powder> foundPowders = new LinkedList<>();
        input.chars().forEach(ch -> {
            for (Powder powder : values()) {
                if (ch == powder.getSymbol()) {
                    foundPowders.add(powder);
                }
            }
        });

        return foundPowders;
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

    public String getName() {
        return StringUtils.capitalizeFirst(this.name().toLowerCase(Locale.ROOT));
    }

    public Powder getOpposingElement() {
        switch (this) {
            case EARTH:
                return Powder.AIR;
            case THUNDER:
                return Powder.EARTH;
            case WATER:
                return Powder.THUNDER;
            case FIRE:
                return Powder.WATER;
            case AIR:
                return Powder.FIRE;
        }
        return null;
    }
}
