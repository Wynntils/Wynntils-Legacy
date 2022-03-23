package com.wynntils.webapi.profiles.ingredient;

import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
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

    public static String getFormattedModifierText(String itemModifierName, int modifierValue) {
        if (itemModifierName.equals("Duration") || itemModifierName.equals("Charges") || itemModifierName.equals("Durability")) {
            return (modifierValue > 0 ? TextFormatting.GREEN + "+" : TextFormatting.RED.toString()) + modifierValue + " " + itemModifierName;
        }
        return (modifierValue > 0 ? TextFormatting.RED + "+" : TextFormatting.GREEN.toString()) + modifierValue + " " + itemModifierName;
    }

    public boolean anyExists() {
        return durability != 0 || duration != 0 || charges != 0 || strength != 0 || dexterity != 0 || intelligence != 0 || defense != 0 || agility != 0;
    }

    public List<String> generateAllLoreLines() {
        List<String> itemLore = new ArrayList<>();

        if (durability != 0 && duration != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Durability", durability) + TextFormatting.GRAY + " or " + IngredientItemModifiers.getFormattedModifierText("Duration", duration));
        else if (durability != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Durability", durability));
        else if (duration != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Duration", duration));

        if (charges != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Charges", charges));
        if (strength != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Strength Min.", strength));
        if (dexterity != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Dexterity Min.", dexterity));
        if (intelligence != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Intelligence Min.", intelligence));
        if (defense != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Defense Min.", defense));
        if (agility != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("Agility Min.", agility));

        return itemLore;
    }
}
