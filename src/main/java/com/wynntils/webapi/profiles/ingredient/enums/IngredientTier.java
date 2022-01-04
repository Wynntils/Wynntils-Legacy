package com.wynntils.webapi.profiles.ingredient.enums;

import net.minecraft.util.text.TextFormatting;

public enum IngredientTier {
    TIER_0(0, TextFormatting.DARK_GRAY, TextFormatting.GRAY),
    TIER_1(1, TextFormatting.YELLOW, TextFormatting.GOLD),
    TIER_2(2, TextFormatting.LIGHT_PURPLE, TextFormatting.DARK_PURPLE),
    TIER_3(3, TextFormatting.AQUA, TextFormatting.DARK_AQUA);

    private final int tierInt;
    private final TextFormatting starColor;
    private final TextFormatting bracketColor;

    IngredientTier(int tierInt, TextFormatting starColor, TextFormatting bracketColor) {
        this.tierInt = tierInt;
        this.starColor = starColor;
        this.bracketColor = bracketColor;
    }

    public int getTierInt() {
        return tierInt;
    }

    public TextFormatting getStarColor() {
        return starColor;
    }

    public TextFormatting getBracketColor() {
        return bracketColor;
    }

    //Much faster than IngredientTier.values()[x], tho bit messier
    public static IngredientTier fromInteger(int x) {
        switch(x) {
            case 0:
                return TIER_0;
            case 1:
                return TIER_1;
            case 2:
                return TIER_2;
            case 3:
                return TIER_3;
        }
        return null;
    }
}
