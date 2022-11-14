/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles.item.objects;

import java.util.HashMap;
import java.util.Map;

import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import org.apache.commons.lang3.math.Fraction;

import com.wynntils.core.utils.StringUtils;
import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;

public class IdentificationContainer {

    private static Map<String, IdentificationModifier> typeMap = new HashMap<>();

    protected IdentificationModifier type;
    private int baseValue;
    protected boolean isFixed;

    private transient boolean isInverted;
    private transient int min, max;
    private transient Fraction minChance;
    private transient Fraction maxChance;

    public IdentificationContainer(IdentificationModifier type, int baseValue, boolean isFixed) {
        this.type = type; this.baseValue = baseValue; this.isFixed = isFixed;
    }

    public void calculateMinMax(String shortId) {
        isInverted = IdentificationOrderer.INSTANCE.isInverted(shortId);

        if (isFixed || (-1 <= baseValue && baseValue <= 1)) {
            min = max = baseValue;
            return;
        }

        boolean positive = (baseValue > 0) ^ isInverted;
        min = (int) Math.round(baseValue * (positive ? 0.3 : 1.3));
        max = (int) Math.round(baseValue * (positive ? 1.3 : 0.7));
    }

    public void registerIdType(String name) {
        if (typeMap.containsKey(name)) return;
        typeMap.put(name, type);
    }

    public IdentificationModifier getType() {
        return type;
    }

    public boolean isInverted() {
        return isInverted;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public boolean hasConstantValue() {
        return isFixed || min == max;
    }

    public static String getAsLongName(String shortName) {
        if (shortName.startsWith("raw")) {
            shortName = shortName.substring(3);
            shortName = Character.toLowerCase(shortName.charAt(0)) + shortName.substring(1);
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (char c : shortName.toCharArray()) {
            if (Character.isUpperCase(c)) nameBuilder.append(" ").append(c);
            else nameBuilder.append(c);
        }

        return StringUtils.capitalizeFirst(nameBuilder.toString()).replaceAll("\\bXp\\b", "XP");
    }

    public static IdentificationModifier getTypeFromName(String name) {
        return typeMap.get(name);
    }

    public static class ReidentificationChances {
        // All fractions 0 to 1; decrease + remain + increase = 1
        public final Fraction decrease;  // Chance to decrease
        public final Fraction remain;  // Chance to remain the same (Usually 1/61 or 1/131)
        public final Fraction increase;  // Chance to increase

        public ReidentificationChances(Fraction decrease, Fraction remain, Fraction increase) {
            this.decrease = decrease; this.remain = remain; this.increase = increase;
        }

        private ReidentificationChances flip() {
            return new ReidentificationChances(increase, remain, decrease);
        }
    }

    /**
     * Return the chances for this identification to decrease/remain the same/increase after reidentification
     *
     * @param currentValue The current value of this identification
     * @return A {@link ReidentificationChances} of the result (All from 0 to 1)
     */
    public strictfp ReidentificationChances getChances(int currentValue) {
        if (hasConstantValue()) {
            return new ReidentificationChances(
                currentValue > baseValue ? Fraction.ONE : Fraction.ZERO,
                currentValue == baseValue ? Fraction.ONE : Fraction.ZERO,
                currentValue < baseValue ? Fraction.ONE : Fraction.ZERO
            );
        }

        if (isInvalidValue(currentValue)) {
            return new ReidentificationChances(
                currentValue > max ? Fraction.ONE : Fraction.ZERO,
                Fraction.ZERO,
                currentValue < min ? Fraction.ONE : Fraction.ZERO
            );
        }

        int minRoll = baseValue > 0 ^ isInverted ? 29 : 69;
        int maxRoll = 131;

        int lowerRawRoll;

        if (currentValue != (isInverted ? max : min)) {
            double lowerRawRollUnrounded = (currentValue * 100D - 50) / baseValue;
            if (baseValue > 0) {
                lowerRawRoll = (int) Math.floor(lowerRawRollUnrounded);
                if (lowerRawRollUnrounded == lowerRawRoll) --lowerRawRoll;
            } else {
                lowerRawRoll = (int) Math.ceil(lowerRawRollUnrounded);
                if (lowerRawRollUnrounded == lowerRawRoll) ++lowerRawRoll;
            }
        } else {
            lowerRawRoll = baseValue > 0 ? minRoll : maxRoll;
        }

        int higherRawRoll;

        if (currentValue != (isInverted ? min : max)) {
            double higherRawRollUnrounded = (currentValue * 100D + 50) / baseValue;
            if (baseValue > 0) {
                higherRawRoll = (int) Math.ceil(higherRawRollUnrounded);
                if (higherRawRollUnrounded == higherRawRoll) ++higherRawRoll;
            } else {
                higherRawRoll = (int) Math.floor(higherRawRollUnrounded);
                if (higherRawRollUnrounded == higherRawRoll) --higherRawRoll;
            }
        } else {
            higherRawRoll = baseValue > 0 ? maxRoll : minRoll;
        }

        Fraction decrease, increase;
        int denom = baseValue > 0 ^ isInverted ? 101 : 61;

        if (baseValue > 0) {
            // chance to be (<= lowerRawRoll) and (>= higherRawRoll)
            decrease = getFraction(lowerRawRoll - minRoll, denom);
            increase = getFraction(maxRoll - higherRawRoll, denom);
        } else {
            decrease = getFraction(maxRoll - lowerRawRoll, denom);
            increase = getFraction(higherRawRoll - minRoll, denom);
        }

        int remainNumerator = Math.abs(higherRawRoll - lowerRawRoll) - 1;

        return new ReidentificationChances(
            isInverted ? increase : decrease,
            getFraction(remainNumerator, denom),
            isInverted ? decrease : increase
        );
    }

    /**
     * @return The chance for this identification to become perfect (From 0 to 1)
     */
    public Fraction getPerfectChance() {
        return maxChance == null ? (minChance = getChances(max).remain) : minChance;
    }

    /**
     * @param currentValue Current value of this identification
     * @return true if this is an invalid value (meaning the API is probably wrong)
     */
    public boolean isInvalidValue(int currentValue) {
        return isInverted ? (currentValue > min || currentValue < max) : (currentValue > max || currentValue < min);
    }

    private static Fraction[] fraction61Cache = new Fraction[62];
    private static Fraction[] fraction101Cache = new Fraction[102];

    static {
        for (int i = 0; i < 62; ++i) {
            fraction61Cache[i] = Fraction.getFraction(i, 61);
        }
        for (int i = 0; i < 102; ++i) {
            fraction101Cache[i] = Fraction.getFraction(i, 101);
        }
    }

    private static Fraction getFraction(int num, int denom) {
        if (0 <= num && num <= denom) {
            if (denom == 61) return fraction61Cache[num];
            if (denom == 101) return fraction101Cache[num];
        }
        return Fraction.getFraction(num, denom);
    }

}
