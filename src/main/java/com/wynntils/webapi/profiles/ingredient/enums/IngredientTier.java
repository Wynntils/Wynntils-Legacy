package com.wynntils.webapi.profiles.ingredient.enums;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;

public enum IngredientTier {
    TIER_0(0, MinecraftChatColors.GRAY, MinecraftChatColors.DARK_GRAY),
    TIER_1(1, MinecraftChatColors.YELLOW, MinecraftChatColors.GOLD),
    TIER_2(2, MinecraftChatColors.LIGHT_PURPLE, MinecraftChatColors.DARK_PURPLE),
    TIER_3(3, MinecraftChatColors.AQUA, MinecraftChatColors.DARK_AQUA);

    private final int tierInt;
    private final MinecraftChatColors starColor;
    private final MinecraftChatColors bracketColor;

    IngredientTier(int tierInt, MinecraftChatColors starColor, MinecraftChatColors bracketColor) {
        this.tierInt = tierInt;
        this.starColor = starColor;
        this.bracketColor = bracketColor;
    }

    public int getTierInt() {
        return tierInt;
    }

    public MinecraftChatColors getStarColor() {
        return starColor;
    }

    public MinecraftChatColors getBracketColor() {
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
