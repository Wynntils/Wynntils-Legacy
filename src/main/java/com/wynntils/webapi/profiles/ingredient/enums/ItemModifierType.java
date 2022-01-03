package com.wynntils.webapi.profiles.ingredient.enums;

public enum ItemModifierType {
    DURABILITY("Durability"),
    DURATION("Duration"),
    CHARGES("Charges"),
    STRENGTH("Strength Min."),
    INTELLIGENCE("Intelligence Min."),
    DEXTERITY("Dexterity Min."),
    AGILITY("Agility Min."),
    DEFENSE("Defense Min.");

    final String effectString;

    ItemModifierType(String effectString) {
        this.effectString = effectString;
    }

    public String getEffectString() {
        return effectString;
    }
}
