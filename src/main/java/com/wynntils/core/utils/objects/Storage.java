/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.utils.objects;

public class Storage {

    int current;
    int max;

    public Storage(int current, int max) {
        this.current = current;
        this.max = max;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "current=" + current +
                ", max=" + max +
                '}';
    }

}
