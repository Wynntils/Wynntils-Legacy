/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.objects;

import com.wynntils.core.utils.StringUtils;
import com.wynntils.webapi.profiles.item.enums.IdentificationType;
import org.apache.commons.lang3.math.Fraction;

public class IdentificationContainer {

    private IdentificationType type;
    private int baseValue;
    private boolean isFixed;

    private transient int min, max;
    private transient Fraction perfectChance;

    public IdentificationContainer(IdentificationType type, int baseValue, boolean isFixed) {
        this.type = type; this.baseValue = baseValue; this.isFixed = isFixed;
        calculateMinMax();
    }

    public void calculateMinMax() {
        if (isFixed) {
            min = max = baseValue;
            return;
        }

        min = (int) Math.round(baseValue * (baseValue < 0 ? 1.3 : 0.3));
        if (min == 0) min = (int) Math.signum(baseValue);

        max = (int) Math.round(baseValue * (baseValue < 0 ? 0.7 : 1.3));
        if (max == 0) max = (int) Math.signum(baseValue);
    }

    public IdentificationType getType() {
        return type;
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

    public String getAsLongName(String shortName) {
        if (shortName.startsWith("raw")) {
            shortName = shortName.substring(3);
            shortName = Character.toLowerCase(shortName.charAt(0)) + shortName.substring(1);
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (char c : shortName.toCharArray()) {
            if (Character.isUpperCase(c)) nameBuilder.append(" ").append(c);
            else nameBuilder.append(c);
        }

        return StringUtils.capitalizeFirst(nameBuilder.toString());
    }

    public static class ReidentificationChances {
        // All fractions 0 to 1
        public final Fraction decrease;  // Chance to decrease
        public final Fraction remain;  // Chance to remain the same (Usually 1/61 or 1/131)
        public final Fraction increase;  // Chance to increase

        public ReidentificationChances(Fraction decrease, Fraction remain, Fraction increase) {
            this.decrease = decrease; this.remain = remain; this.increase = increase;
        }
    }

    /**
     * Return the chances for this identification to decrease/remain the same/increase after reidentification
     *
     * @param currentValue The current value of this identification
     * @return A {@link ReidentificationChances} of the result (All from 0 to 1)
     */
    public strictfp ReidentificationChances getChances(int currentValue) {
        // Special case baseValue 1 because min == 1, even though round(1 * 0.3) == 0 (It's clamped to 1)
        // And baseValue 0 to avoid / 0, and baseValue -1 because it's faster
        if (isFixed || (-1 <= baseValue && baseValue <= 1)) {
            return new ReidentificationChances(
                currentValue > baseValue ? Fraction.ONE : Fraction.ZERO,
                currentValue == baseValue ? Fraction.ONE : Fraction.ZERO,
                currentValue < baseValue ? Fraction.ONE : Fraction.ZERO
            );
        }

        if (currentValue > max || currentValue < min) {
            return new ReidentificationChances(
                currentValue > max ? Fraction.ONE : Fraction.ZERO,
                Fraction.ZERO,
                currentValue < min ? Fraction.ONE : Fraction.ZERO
            );
        }

        int increaseDirection = baseValue > 0 ? +1 : -1;

        double lowerRawRollUnrounded = (currentValue * 100D - 50) / baseValue;
        int lowerRawRoll;
        if (baseValue > 0) {
            lowerRawRoll = (int) Math.floor(lowerRawRollUnrounded);
            if (lowerRawRollUnrounded == lowerRawRoll) --lowerRawRoll;
        } else {
            lowerRawRoll = (int) Math.ceil(lowerRawRollUnrounded);
            if (lowerRawRollUnrounded == lowerRawRoll) ++lowerRawRoll;
        }

        if (Math.round(baseValue * (lowerRawRoll / 100D)) >= currentValue) {
            lowerRawRoll -= increaseDirection;
        } else if (Math.round(baseValue * ((lowerRawRoll + increaseDirection) / 100D)) < currentValue) {
            lowerRawRoll += increaseDirection;
        }

        double higherRawRollUnrounded = (currentValue * 100D + 50) / baseValue;
        int higherRawRoll = baseValue > 0 ? (int) Math.ceil(higherRawRollUnrounded) : (int) Math.floor(higherRawRollUnrounded);

        if (Math.round(baseValue * (higherRawRoll / 100D)) < max) {
            higherRawRoll += increaseDirection;
        } else if (Math.round(baseValue * ((higherRawRoll - increaseDirection) / 100D)) >= max) {
            higherRawRoll -= increaseDirection;
        }

        Fraction decrease, increase;

        if (baseValue > 0) {
            // chance to be (<= lowerRawRoll) and (>= higherRawRoll)
            decrease = Fraction.getFraction(lowerRawRoll - 29, 101);
            increase = Fraction.getFraction(131 - higherRawRoll, 101);
        } else {
            decrease = Fraction.getFraction(131 - lowerRawRoll, 61);
            increase = Fraction.getFraction(higherRawRoll - 69, 61);
        }

        return new ReidentificationChances(
            decrease,
            Fraction.getFraction(Math.max(Math.abs(higherRawRoll - lowerRawRoll) - 1, 0), baseValue > 0 ? 101 : 61),
            increase
        );
    }

    /**
     * @return The chance for this identification to become perfect (From 0 to 1)
     */
    public strictfp Fraction getPerfectChance() {
        if (perfectChance != null) return perfectChance;

        if (isFixed || (-1 <= baseValue && baseValue <= 1)) {
            return perfectChance = Fraction.ONE;
        }

        int increaseDirection = baseValue > 0 ? +1 : -1;

        double perfectRawRollUnrounded = (max * 100D - 50) / baseValue;
        int perfectRawRoll = baseValue > 0 ? (int) Math.ceil(perfectRawRollUnrounded) : (int) Math.floor(perfectRawRollUnrounded);

        if (Math.round(baseValue * (perfectRawRoll / 100D)) < max) {
            perfectRawRoll += increaseDirection;
        } else if (Math.round(baseValue * ((perfectRawRoll - increaseDirection) / 100D)) >= max) {
            perfectRawRoll -= increaseDirection;
        }

        if (baseValue > 0) {
            return perfectChance = Fraction.getFraction(131 - perfectRawRoll, 101);
        }

        return perfectChance = Fraction.getFraction(perfectRawRoll - 69, 61);
    }

    /**
     * How good an identification is. 0 means the worst, 1 means perfect.
     *
     * @param currentValue The current value of this identification
     * @return A double from 0 to 1 of how good this identification is
     */
    public double getRelativeValue(int currentValue) {
        if (isFixed || baseValue == 0) return currentValue == baseValue ? 1 : 0;
        if (currentValue == max) return 1;
        if (max == min) return 0;
        return (currentValue - min) / (double) (max - min);
    }

    /**
     * @param currentValue Current value of this identification
     * @return true if this is a valid value (If false, the API is wrong)
     */
    public boolean isValidValue(int currentValue) {
        return getChances(currentValue).remain.compareTo(Fraction.ZERO) != 0;
    }

}
