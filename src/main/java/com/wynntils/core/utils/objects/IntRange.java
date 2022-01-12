/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.objects;

public class IntRange {

    private final int min, max;

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntRange(int level) {
        this(level, level);
    }

    public int getAverage() {
        return (min + max) / 2;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public String toString() {
        return min == max ? Integer.toString(max) : String.format("%d-%d", min, max);
    }

}
