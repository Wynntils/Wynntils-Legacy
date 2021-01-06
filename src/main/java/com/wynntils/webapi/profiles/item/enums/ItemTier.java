/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.function.IntUnaryOperator;

public enum ItemTier {
    NORMAL    (0, TextFormatting.WHITE,        MinecraftChatColors.WHITE,        new CustomColor(1f, 1f, 1f),      0,  1.0f, (lvl) -> 0),
    UNIQUE    (1, TextFormatting.YELLOW,       MinecraftChatColors.YELLOW,       new CustomColor(1f, 1f, 0f),      5,  0.5f, (lvl) -> (int)Math.ceil(5d + lvl * 0.5)),
    RARE      (2, TextFormatting.LIGHT_PURPLE, MinecraftChatColors.LIGHT_PURPLE, new CustomColor(1f, 0f, 1f),     15,  1.2f, (lvl) -> (int)Math.ceil(15d + lvl * 1.2)),
    SET       (3, TextFormatting.GREEN,        MinecraftChatColors.GREEN,        new CustomColor(0f, 1f, 0f),     12,  1.6f, (lvl) -> (int)Math.ceil(12d + lvl * 1.6)),
    LEGENDARY (4, TextFormatting.AQUA,         MinecraftChatColors.AQUA,         new CustomColor(0f, 1f, 1f),     35,  4.8f, (lvl) -> (int)Math.ceil(35d + (4.8d * lvl))),
    FABLED    (5, TextFormatting.RED,          MinecraftChatColors.RED,          new CustomColor(1, 1/3f, 1/3f),  60, 12.0f, (lvl) -> (lvl + 5) * 60),
    MYTHIC    (6, TextFormatting.DARK_PURPLE,  MinecraftChatColors.DARK_PURPLE,  new CustomColor(0.3f, 0, 0.3f),  90, 18.0f, (lvl) -> (lvl + 5) * 18),
    CRAFTED   (7, TextFormatting.DARK_AQUA,    MinecraftChatColors.DARK_AQUA,    new CustomColor(0, .545f, .545f), 0,  1.0f, (lvl) -> lvl);

    int priority;
    TextFormatting textColor;
    MinecraftChatColors chatColor;
    CustomColor highlightColor;
    int baseCost;
    float costMultiplier;
    IntUnaryOperator rerollFormula;

    ItemTier(int priority, TextFormatting textColor, MinecraftChatColors chatColor, CustomColor highlightColor,
             int baseCost, float costMultiplier, IntUnaryOperator rerollFormula) {
        this.priority = priority;
        this.textColor = textColor;
        this.chatColor = chatColor;
        this.highlightColor = highlightColor;
        this.baseCost = baseCost;
        this.costMultiplier = costMultiplier;
        this.rerollFormula = rerollFormula;
    }

    public static ItemTier fromTextColoredString(String text) {
        for (ItemTier tier : ItemTier.values()) {
            if (text.startsWith(tier.getTextColor())) return tier;
        }

        return null;
    }

    public static ItemTier fromColorCodeString(String text) {
        for (ItemTier tier : ItemTier.values()) {
            if (text.startsWith(Character.toString(tier.getColorCode()))) return tier;
        }

        return null;
    }

    public String asCapitalizedName() {
        return StringUtils.capitalizeFirst(toString().toLowerCase());
    }

    public String asFormattedName() {
        return textColor + asCapitalizedName();
    }

    public String asLore() {
        return asFormattedName() + " Item";
    }

    public int getPriority() {
        return priority;
    }

    public String getTextColor() {
        return textColor.toString();
    }

    public char getColorCode() {
        return textColor.toString().charAt(1);
    }

    public MinecraftChatColors getChatColor() {
        return chatColor;
    }

    public CustomColor getHighlightColor() {
        return highlightColor;
    }

    public int getItemCost(int level) {
        return this.baseCost + (int) Math.ceil(level*this.costMultiplier);
    }

    public int getRerollPrice(int level, int rolledAmount) {
        int basePrice = rerollFormula.applyAsInt(level);

        return basePrice * (int)Math.pow(5, rolledAmount);
    }
}
