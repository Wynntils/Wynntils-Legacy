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
        String requirementString = getRequirementStringFromModifier(itemModifierName);
        if (itemModifierName.equals("duration") || itemModifierName.equals("charges") || itemModifierName.equals("durability")) {
            if (modifierValue > 0)
                return TextFormatting.GREEN + "+" + modifierValue + " " + requirementString;
            else
                return TextFormatting.RED.toString() + modifierValue + " " + requirementString;
        }
        else if (modifierValue > 0) {
            return TextFormatting.RED + "+" + modifierValue + " " + requirementString;
        }
        return TextFormatting.GREEN.toString() + modifierValue + " " + requirementString;
    }

    private static String getRequirementStringFromModifier(String itemModifierName) {
        switch (itemModifierName) {
            case "duration":
            case "charges":
            case "durability":
                return itemModifierName;
            case "strength":
            case "intelligence":
            case "dexterity":
            case "agility":
            case "defense":
                return itemModifierName.substring(0, 1).toUpperCase(Locale.ROOT) + itemModifierName.substring(1) + " Min.";
        }

        return null;
    }

    public boolean anyExists() {
        return durability != 0 || duration != 0 || charges != 0 || strength != 0 || dexterity != 0 || intelligence != 0 || defense != 0 || agility != 0;
    }

    public List<String> generateAllLoreLines() {
        List<String> itemLore = new ArrayList<>();

        if (durability != 0 && duration != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("durability", durability) + TextFormatting.GRAY + " or " + IngredientItemModifiers.getFormattedModifierText("duration", duration));
        else if (durability != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("durability", durability));
        else if (duration != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("duration", duration));

        if (charges != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("charges", charges));
        if (strength != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("strength", strength));
        if (dexterity != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("dexterity", dexterity));
        if (intelligence != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("intelligence", intelligence));
        if (defense != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("defense", defense));
        if (agility != 0)
            itemLore.add(IngredientItemModifiers.getFormattedModifierText("agility", agility));

        return itemLore;
    }
}
