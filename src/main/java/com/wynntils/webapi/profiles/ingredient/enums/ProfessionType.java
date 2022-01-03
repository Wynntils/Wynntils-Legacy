package com.wynntils.webapi.profiles.ingredient.enums;

public enum ProfessionType {
    //TODO: Add icons
    WEAPONSMITHING("Weaponsmithing", ""),
    WOODWORKING("Woodworking", ""),
    ARMOURING("Armouring", ""),
    TAILORING("Tailoring", ""),
    JEWELING("Jeweling", ""),
    COOKING("Cooking", ""),
    ALCHEMISM("Alchemism", ""),
    SCRIBING("Scribing", "");

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
}
