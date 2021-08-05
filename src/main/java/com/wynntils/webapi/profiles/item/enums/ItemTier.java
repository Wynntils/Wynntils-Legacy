/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.util.text.TextFormatting;

import java.util.concurrent.Callable;

public enum ItemTier {
    NORMAL    (0, TextFormatting.WHITE,        MinecraftChatColors.WHITE,        new CustomColor(1f, 1f, 1f),      () -> UtilitiesConfig.Items.INSTANCE.normalHighlightColor,     0,  1.0f),
    UNIQUE    (1, TextFormatting.YELLOW,       MinecraftChatColors.YELLOW,       new CustomColor(1f, 1f, 0f),      () -> UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor,     3,  0.5f),
    RARE      (2, TextFormatting.LIGHT_PURPLE, MinecraftChatColors.LIGHT_PURPLE, new CustomColor(1f, 0f, 1f),      () -> UtilitiesConfig.Items.INSTANCE.rareHighlightColor,       8,  1.2f),
    SET       (3, TextFormatting.GREEN,        MinecraftChatColors.GREEN,        new CustomColor(0f, 1f, 0f),      () -> UtilitiesConfig.Items.INSTANCE.setHighlightColor,        8,  1.5f),
    LEGENDARY (4, TextFormatting.AQUA,         MinecraftChatColors.AQUA,         new CustomColor(0f, 1f, 1f),      () -> UtilitiesConfig.Items.INSTANCE.legendaryHighlightColor, 12,  4.5f),
    FABLED    (5, TextFormatting.RED,          MinecraftChatColors.RED,          new CustomColor(1, 1/3f, 1/3f),   () -> UtilitiesConfig.Items.INSTANCE.fabledHighlightColor,    26, 12.0f),
    MYTHIC    (6, TextFormatting.DARK_PURPLE,  MinecraftChatColors.DARK_PURPLE,  new CustomColor(0.3f, 0, 0.3f),   () -> UtilitiesConfig.Items.INSTANCE.mythicHighlightColor,    90, 18.0f),
    CRAFTED   (7, TextFormatting.DARK_AQUA,    MinecraftChatColors.DARK_AQUA,    new CustomColor(0, .545f, .545f), () -> UtilitiesConfig.Items.INSTANCE.craftedHighlightColor,    0,  1.0f);

    int priority;
    TextFormatting textColor;
    MinecraftChatColors chatColor;
    CustomColor defaultHighlightColor;
    Callable<CustomColor> customizedHighlightColor;
    int baseCost;
    float costMultiplier;

    ItemTier(int priority, TextFormatting textColor, MinecraftChatColors chatColor, CustomColor defaultHighlightColor,
             Callable<CustomColor> customizedHighlightColor, int baseCost, float costMultiplier) {
        this.priority = priority;
        this.textColor = textColor;
        this.chatColor = chatColor;
        this.defaultHighlightColor = defaultHighlightColor;
        this.customizedHighlightColor = customizedHighlightColor;
        this.baseCost = baseCost;
        this.costMultiplier = costMultiplier;
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

    public static ItemTier fromBoxDamage(int damage) {
        if (damage > 6) return NORMAL;
        return ItemTier.values()[damage];
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

    public CustomColor getDefaultHighlightColor() {
        return defaultHighlightColor;
    }

    public CustomColor getCustomizedHighlightColor() {
        try {
            return customizedHighlightColor.call();
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomColor(1f, 1f, 1f, 1f);
        }
    }

    public int getItemIdentificationCost(int level) {
        return this.baseCost + (int) Math.ceil(level*this.costMultiplier);
    }

    public int getRerollPrice(int level, int rolledAmount) {
        int basePrice = getItemIdentificationCost(level);

        return basePrice * (int)Math.pow(5, rolledAmount);
    }

    public static ItemTier matchText(String tierStr) {
        switch (tierStr) {
            case "normal":
            case "n":
            case "common":
            case "c":
                return NORMAL;
            case "set":
            case "s":
                return SET;
            case "unique":
            case "u":
                return UNIQUE;
            case "rare":
            case "r":
                return RARE;
            case "legendary":
            case "l":
                return LEGENDARY;
            case "fabled":
            case "f":
                return FABLED;
            case "mythic":
            case "m":
                return MYTHIC;
            default:
                return null;
        }
    }

}
