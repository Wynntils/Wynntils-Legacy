/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.objects;

import java.util.function.DoubleUnaryOperator;

public interface Function extends DoubleUnaryOperator {

    double applyAsDouble(double x);

    default Function derivative() {
        throw new UnsupportedOperationException();
    }

}
