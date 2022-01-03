package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientModifierType;

public class IngredientModifier {
    IngredientModifierType type;
    int modifierValue;

    IngredientModifier(IngredientModifierType type, int modifierValue) {
        this.type = type;
        this.modifierValue = modifierValue;
    }

    public String[] getLoreLines() {
        return new String[] {
                MinecraftChatColors.GREEN.toString() + modifierValue + "%" + MinecraftChatColors.GRAY + " Ingredient Effectiveness",
                MinecraftChatColors.GRAY + "(To ingredients " + type.getDisplayValue() + " this one)"
        };
    }
}
