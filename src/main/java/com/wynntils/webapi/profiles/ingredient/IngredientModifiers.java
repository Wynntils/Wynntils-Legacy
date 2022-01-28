package com.wynntils.webapi.profiles.ingredient;

import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public boolean anyExists() {
        return left != 0 || right != 0 || under != 0 || above != 0 || touching != 0 || notTouching != 0;
    }

    public static String[] getLoreLines(String modifierName, int modifierValue) {
        String readableModifier = modifierName;

        switch (modifierName) {
            case "left":
                readableModifier = "to the left of";
                break;
            case "right":
                readableModifier = "to the right of";
                break;
            case "notTouching":
                readableModifier =  "not touching";
                break;
        }

        return new String[] {
                (modifierValue > 0 ? TextFormatting.GREEN + "+" : TextFormatting.RED.toString()) + modifierValue + "%" + TextFormatting.GRAY + " Ingredient Effectiveness",
                TextFormatting.GRAY + "(To ingredients " + readableModifier + " this one)"
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
