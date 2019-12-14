/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.utils.Utils;
import net.minecraft.util.text.TextFormatting;

import java.util.function.IntUnaryOperator;

public enum ItemTier {

    NORMAL    (0, TextFormatting.WHITE,        (lvl) -> 0),
    SET       (1, TextFormatting.GREEN,        (lvl) -> (int)Math.ceil(12d + lvl * 1.6)),
    UNIQUE    (2, TextFormatting.YELLOW,       (lvl) -> (int)Math.ceil(5d + lvl * 0.5)),
    RARE      (3, TextFormatting.LIGHT_PURPLE, (lvl) -> (int)Math.ceil(15d + lvl * 1.2)),
    LEGENDARY (4, TextFormatting.AQUA,         (lvl) -> (int)Math.ceil(40d + lvl * 5.2)),
    FABLED    (5, TextFormatting.RED,          (lvl) -> (lvl + 5) * 60),
    MYTHIC    (6, TextFormatting.DARK_PURPLE,  (lvl) -> (lvl + 5) * 18);

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

        return rolledAmount == 0 ? basePrice : basePrice * (int)Math.pow(5, rolledAmount);
    }

    public String asLore() {
        return color + Utils.capitalizeFirst(toString().toLowerCase()) + " Item";
    }

}
