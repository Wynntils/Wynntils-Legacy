package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.webapi.profiles.ingredient.enums.IngredientModifierType;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IngredientModifiers {
    int left = 0;
    int right = 0;
    int above = 0;
    int under = 0;
    int touching = 0;
    int notTouching = 0;

    public IngredientModifiers() {}

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getAbove() {
        return above;
    }

    public int getUnder() {
        return under;
    }

    public int getTouching() {
        return touching;
    }

    public int getNotTouching() {
        return notTouching;
    }

    public static IngredientModifierType getModifierTypeFromString(String modifierName) {
        if (modifierName.equals("notTouching"))
            return IngredientModifierType.NOT_TOUCHING;
        return IngredientModifierType.valueOf(modifierName.toUpperCase(Locale.ROOT));
    }

    public boolean anyExists() {
        return left != 0 || right != 0 || under != 0 || above != 0 || touching != 0 || notTouching != 0;
    }

    public static String[] getLoreLines(String modifierName, int modifierValue) {
        IngredientModifierType type = getModifierTypeFromString(modifierName);
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

    public List<String> generateAllLoreLines() {
        List<String> itemLore = new ArrayList<>();
        if (this.left != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("left", left)));
        if (this.right != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("right", right)));
        if (this.above != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("above", above)));
        if (this.under != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("under", under)));
        if (this.touching != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("touching", touching)));
        if (this.notTouching != 0)
            itemLore.addAll(Arrays.asList(IngredientModifiers.getLoreLines("notTouching", notTouching)));

        return itemLore;
    }
}
