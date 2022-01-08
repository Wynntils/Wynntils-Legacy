package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.webapi.profiles.ingredient.enums.ItemModifierType;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

public class IngredientItemModifiers {

    int durability = 0;
    int duration = 0;
    int charges = 0;
    int strength = 0;
    int dexterity = 0;
    int intelligence = 0;
    int defense = 0;
    int agility = 0;

    public IngredientItemModifiers() {}

    public int getAgility() {
        return agility;
    }

    public int getDefense() {
        return defense;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getStrength() {
        return strength;
    }

    public int getDurability() {
        return durability;
    }

    public int getDuration() {
        return duration;
    }

    public int getCharges() {
        return charges;
    }

    public static ItemModifierType getItemModifierTypeFromString(String itemModifierName) {
        return ItemModifierType.valueOf(itemModifierName.toUpperCase(Locale.ROOT));
    }

    public static String getFormattedModifierText(String itemModifierName, int modifierValue) {
        ItemModifierType type = getItemModifierTypeFromString(itemModifierName);
        if (type == ItemModifierType.DURATION || type == ItemModifierType.CHARGES || type == ItemModifierType.DURABILITY)
            if (modifierValue > 0)
                return TextFormatting.GREEN + "+" + modifierValue + " " + type.getEffectString();
            else
                return TextFormatting.RED.toString() + modifierValue + " " + type.getEffectString();
        else if (modifierValue > 0)
            return TextFormatting.RED + "+" + modifierValue + " " + type.getEffectString();

        return TextFormatting.GREEN.toString() + modifierValue + " " + type.getEffectString();
    }

    public boolean anyExists() {
        return durability != 0 || duration != 0 || charges != 0 || strength != 0 || dexterity != 0 || intelligence != 0 || defense != 0 || agility != 0;
    }
}
