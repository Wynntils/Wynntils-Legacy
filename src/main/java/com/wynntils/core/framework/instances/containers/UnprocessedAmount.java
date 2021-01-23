/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances.containers;

public class UnprocessedAmount {

    private int current;
    private int maximum;

    public UnprocessedAmount(int current, int maximum) {
        this.current = current;
        this.maximum = maximum;
    }

    public int getCurrent() {
        return current;
    }

    public int getMaximum() {
        return maximum;
    }

}
