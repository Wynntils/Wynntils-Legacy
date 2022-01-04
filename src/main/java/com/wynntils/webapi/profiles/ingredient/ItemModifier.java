package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.webapi.profiles.ingredient.enums.ItemModifierType;
import net.minecraft.util.text.TextFormatting;

public class ItemModifier {
    ItemModifierType type;
    int modifierValue;

    ItemModifier(ItemModifierType type, int modifierValue) {
        this.type = type;
        this.modifierValue = modifierValue;
    }

    public String getFormattedModifierText() {
        if (type == ItemModifierType.DURATION || type == ItemModifierType.CHARGES || type == ItemModifierType.DURABILITY)
            if (modifierValue > 0)
                return TextFormatting.GREEN + "+" + modifierValue + " " + type.getEffectString();
            else
                return TextFormatting.RED.toString() + modifierValue + " " + type.getEffectString();
        else if (modifierValue > 0)
            return TextFormatting.RED + "+" + modifierValue + " " + type.getEffectString();

        return TextFormatting.GREEN.toString() + modifierValue + " " + type.getEffectString();
    }
}
