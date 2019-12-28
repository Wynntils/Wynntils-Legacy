package com.wynntils.core.framework.enums;

import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Pattern;

public enum SkillPoint {

    STRENGTH("✤", TextFormatting.DARK_GREEN),
    DEXTERITY("✦", TextFormatting.YELLOW),
    INTELLIGENCE("❉", TextFormatting.AQUA),
    DEFENCE("✹", TextFormatting.RED),
    AGILITY("❋", TextFormatting.WHITE);

    String symbol, color;
    Pattern regexMatcher;

    SkillPoint(String symbol, TextFormatting color) {
        this.symbol = symbol;
        this.color = color.toString();

        regexMatcher = Pattern.compile(".*?(" + symbol + " " + StringUtils.capitalizeFirst(toString().toLowerCase()) + ").*?");
    }

    public String getSymbol() {
        return symbol;
    }

    public String getAsName() {
        return color + symbol + " " + StringUtils.capitalizeFirst(name().toLowerCase());
    }

    public static SkillPoint findSkillPoint(String input) {
        for(SkillPoint point : values()) {
            if(!point.regexMatcher.matcher(input).matches()) continue;

            return point;
        }

        return null;
    }

}
