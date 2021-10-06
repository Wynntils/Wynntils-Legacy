/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums;

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
