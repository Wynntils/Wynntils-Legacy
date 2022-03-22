package com.wynntils.webapi.profiles.ingredient.enums;

import java.util.Locale;

public enum ProfessionType {
    WEAPONSMITHING("Weaponsmithing", "Ⓖ"),
    WOODWORKING("Woodworking", "Ⓘ"),
    ARMOURING("Armouring", "Ⓗ"),
    TAILORING("Tailoring", "Ⓕ"),
    JEWELING("Jeweling", "Ⓓ"),
    COOKING("Cooking", "Ⓐ"),
    ALCHEMISM("Alchemism", "Ⓛ"),
    SCRIBING("Scribing", "Ⓔ");

    final String professionName;
    final String professionIconChar;

    ProfessionType(String professionName, String professionIconChar) {
        this.professionName = professionName;
        this.professionIconChar = professionIconChar;
    }

    public String getDisplayName() {
        return professionName;
    }

    public String getProfessionIconChar() {
        return professionIconChar;
    }

    public static ProfessionType from(String type) {
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
