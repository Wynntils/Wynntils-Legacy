package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.webapi.profiles.ingredient.enums.ItemModifierType;

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
                return MinecraftChatColors.GREEN + "+" + modifierValue + " " + type.getEffectString();
            else
                return MinecraftChatColors.RED + "-" + modifierValue + " " + type.getEffectString();
        else if (modifierValue > 0)
            return MinecraftChatColors.RED + "+" + modifierValue + " " + type.getEffectString();

        return MinecraftChatColors.GREEN + "-" + modifierValue + " " + type.getEffectString();
    }
}
