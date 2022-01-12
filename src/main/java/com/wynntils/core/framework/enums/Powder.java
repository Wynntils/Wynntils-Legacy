/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public enum Powder {

    EARTH('✤', TextFormatting.DARK_GREEN),
    THUNDER('✦', TextFormatting.YELLOW),
    WATER('❉', TextFormatting.AQUA),
    FIRE('✹', TextFormatting.RED),
    AIR('❋', TextFormatting.WHITE);

    char symbol;
    TextFormatting rawColor;
    String color;

    Powder(char symbol, TextFormatting color) {
        this.symbol = symbol;
        this.rawColor = color;
        this.color = color.toString();
    }

    public static Pattern POWDER_NAME_PATTERN = Pattern.compile("§[2ebcf8].? ?(Earth|Thunder|Water|Fire|Air|Blank) Powder ([IV]{1,3})");

    public char getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }

    public TextFormatting getRawColor() {
        return rawColor;
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
        return color + symbol;
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

}
