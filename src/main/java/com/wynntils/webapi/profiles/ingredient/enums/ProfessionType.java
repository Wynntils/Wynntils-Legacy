package com.wynntils.webapi.profiles.ingredient.enums;

import java.util.Locale;

public enum ProfessionType {
    WEAPONSMITHING("Weaponsmithing", "Ⓖ", 16 * 4, 0),
    WOODWORKING("Woodworking", "Ⓘ", 16 * 5, 0),
    ARMOURING("Armouring", "Ⓗ", 16 * 6, 0),
    TAILORING("Tailoring", "Ⓕ", 16 * 7, 0),
    JEWELING("Jeweling", "Ⓓ", 16 * 8, 0),
    COOKING("Cooking", "Ⓐ", 16 * 11, 0),
    ALCHEMISM("Alchemism", "Ⓛ", 16 * 10, 0),
    SCRIBING("Scribing", "Ⓔ", 16 * 9, 0);

    final String professionName;
    final String professionIconChar;

    final int textureX;
    final int textureY;

    ProfessionType(String professionName, String professionIconChar, int textureX, int textureY) {
        this.professionName = professionName;
        this.professionIconChar = professionIconChar;
        this.textureX = textureX;
        this.textureY = textureY;
    }

    public String getDisplayName() {
        return professionName;
    }

    public String getProfessionIconChar() {
        return professionIconChar;
    }

    public int getTextureX() {
        return textureX;
    }

    public int getTextureY() {
        return textureY;
    }

    public static ProfessionType fromString(String type) {
        switch (type.toLowerCase(Locale.ROOT)) {
            case "weaponsmithing":
                return WEAPONSMITHING;
            case "woodworking":
                return WOODWORKING;
            case "armouring":
                return ARMOURING;
            case "tailoring":
                return TAILORING;
            case "jeweling":
                return JEWELING;
            case "cooking":
                return COOKING;
            case "alchemism":
                return ALCHEMISM;
            case "scribing":
                return SCRIBING;
            default:
                return null;
        }
    }
}
