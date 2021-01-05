/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.function.IntUnaryOperator;

public enum ItemTier {

    NORMAL    (0, TextFormatting.WHITE,        (lvl) -> 0),
    UNIQUE    (1, TextFormatting.YELLOW,       (lvl) -> (int)Math.ceil(5d + lvl * 0.5)),
    RARE      (2, TextFormatting.LIGHT_PURPLE, (lvl) -> (int)Math.ceil(15d + lvl * 1.2)),
    SET       (3, TextFormatting.GREEN,        (lvl) -> (int)Math.ceil(12d + lvl * 1.6)),
    LEGENDARY (4, TextFormatting.AQUA,         (lvl) -> (int)Math.ceil(35d + (4.8d * lvl))),
    FABLED    (5, TextFormatting.RED,          (lvl) -> (lvl + 5) * 60),
    MYTHIC    (6, TextFormatting.DARK_PURPLE,  (lvl) -> (lvl + 5) * 18),
    CRAFTED   (7, TextFormatting.DARK_AQUA,    (lvl) -> lvl);

    int priority; String color; IntUnaryOperator rerollFormula;

    ItemTier(int priority, TextFormatting color, IntUnaryOperator rerollFormula) {
        this.priority = priority; this.color = color.toString(); this.rerollFormula = rerollFormula;
    }

    public int getPriority() {
        return priority;
    }

    public String getColor() {
        return color;
    }

    public int getRerollPrice(int level, int rolledAmount) {
        int basePrice = rerollFormula.applyAsInt(level);

        return basePrice * (int)Math.pow(55, rolledAmount);
    }

    public char getColorCode() { return color.charAt(1); }

    public String asLore() {
        return color + StringUtils.capitalizeFirst(toString().toLowerCase()) + " Item";
    }

}
