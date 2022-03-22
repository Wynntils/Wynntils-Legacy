package com.wynntils.webapi.profiles.ingredient.enums;

import com.google.gson.annotations.SerializedName;
import static net.minecraft.util.text.TextFormatting.*;

public enum IngredientTier {
    @SerializedName("0")
    TIER_0(0, GRAY + "[" + DARK_GRAY + "✫✫✫" + GRAY + "]"),
    @SerializedName("1")
    TIER_1(1, GOLD + "[" + YELLOW + "✫" + DARK_GRAY + "✫✫" + GOLD + "]"),
    @SerializedName("2")
    TIER_2(2, DARK_PURPLE + "[" + LIGHT_PURPLE + "✫✫" + DARK_GRAY + "✫" + DARK_PURPLE + "]"),
    @SerializedName("3")
    TIER_3(3, DARK_AQUA + "[" + AQUA + "✫✫✫" + DARK_AQUA + "]");

    private final int tierInt;
    private final String tierString;

    IngredientTier(int tierInt, String tierString) {
        this.tierInt = tierInt;
        this.tierString = tierString;
    }

    public int getTierInt() {
        return tierInt;
    }

    public String getTierString() {
        return tierString;
    }

    public static IngredientTier matchText(String tier) {
        switch (tier) {
            case "0":
                return TIER_0;
            case "1":
                return TIER_1;
            case "2":
                return TIER_2;
            case "3":
                return TIER_3;
            default:
                return null;
        }
    }
}
