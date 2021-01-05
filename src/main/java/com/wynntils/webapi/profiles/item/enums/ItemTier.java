/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.function.IntUnaryOperator;

public enum ItemTier {
    NORMAL    (0, TextFormatting.WHITE,        new CustomColor(1f, 1f, 1f), (lvl) -> 0),
    UNIQUE    (1, TextFormatting.YELLOW,       new CustomColor(1f, 1f, 0f), (lvl) -> (int)Math.ceil(5d + lvl * 0.5)),
    RARE      (2, TextFormatting.LIGHT_PURPLE, new CustomColor(1f, 0f, 1f), (lvl) -> (int)Math.ceil(15d + lvl * 1.2)),
    SET       (3, TextFormatting.GREEN,        new CustomColor(0f, 1f, 0f), (lvl) -> (int)Math.ceil(12d + lvl * 1.6)),
    LEGENDARY (4, TextFormatting.AQUA,         new CustomColor(0f, 1f, 1f), (lvl) -> (int)Math.ceil(35d + (4.8d * lvl))),
    FABLED    (5, TextFormatting.RED,          new CustomColor(1, 1/3f, 1/3f), (lvl) -> (lvl + 5) * 60),
    MYTHIC    (6, TextFormatting.DARK_PURPLE,  new CustomColor(0.3f, 0, 0.3f), (lvl) -> (lvl + 5) * 18),
    CRAFTED   (7, TextFormatting.DARK_AQUA,    new CustomColor(0, .545f, .545f), (lvl) -> lvl);

    int priority;
    TextFormatting textColor;

    CustomColor highlightColor;
    IntUnaryOperator rerollFormula;

    ItemTier(int priority, TextFormatting textColor, CustomColor highlightColor, IntUnaryOperator rerollFormula) {
        this.priority = priority;
        this.textColor = textColor;
        this.highlightColor = highlightColor;
        this.rerollFormula = rerollFormula;
    }

    public int getPriority() {
        return priority;
    }

    public String getColor() {
        return textColor.toString();
    }

    public char getColorCode() {
        return textColor.toString().charAt(1);
    }

    public CustomColor getHighlightColor() {
        return highlightColor;
    }

    public int getRerollPrice(int level, int rolledAmount) {
        int basePrice = rerollFormula.applyAsInt(level);

        return basePrice * (int)Math.pow(5, rolledAmount);
    }

    public String asFormattedName() {
        return textColor + StringUtils.capitalizeFirst(toString().toLowerCase());
    }

    public String asLore() {
        return asFormattedName() + " Item";
    }

}
