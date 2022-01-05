package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientModifierType;
import net.minecraft.util.text.TextFormatting;

public class IngredientModifier {
    IngredientModifierType type;
    int modifierValue;

    IngredientModifier(IngredientModifierType type, int modifierValue) {
        this.type = type;
        this.modifierValue = modifierValue;
    }

    public String[] getLoreLines() {
        if (modifierValue > 0)
            return new String[] {
                    TextFormatting.GREEN + "+" + modifierValue + "%" + TextFormatting.GRAY + " Ingredient Effectiveness",
                    TextFormatting.GRAY + "(To ingredients " + type.getDisplayValue() + " this one)"
            };
        else
            return new String[] {
                    TextFormatting.RED.toString() + modifierValue + "%" + TextFormatting.GRAY + " Ingredient Effectiveness",
                    TextFormatting.GRAY + "(To ingredients " + type.getDisplayValue() + " this one)"
            };
    }
}
