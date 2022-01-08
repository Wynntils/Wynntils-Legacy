package com.wynntils.webapi.profiles.ingredient.enums;

import com.google.gson.annotations.SerializedName;

public enum IngredientTier {
    @SerializedName("0")
    TIER_0(0, "§7[§8✫✫✫§7]"),
    @SerializedName("1")
    TIER_1(1, "§6[§e✫§8✫✫§6]"),
    @SerializedName("2")
    TIER_2(2, "§5[§d✫✫§8✫§5]"),
    @SerializedName("3")
    TIER_3(3, "§3[§b✫✫✫§3]");

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
}
