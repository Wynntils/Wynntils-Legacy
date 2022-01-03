package com.wynntils.webapi.profiles.ingredient.enums;

public enum IngredientModifierType {
    LEFT("to the left of"),
    RIGHT("to the right of"),
    ABOVE("above"),
    UNDER("under"),
    TOUCHING("touching"),
    NOT_TOUCHING("not touching");

    final String displayValue;

    IngredientModifierType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
