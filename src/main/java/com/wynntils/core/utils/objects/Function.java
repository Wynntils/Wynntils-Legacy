package com.wynntils.core.utils.objects;

import java.util.function.DoubleUnaryOperator;

public interface Function extends DoubleUnaryOperator {

    double applyAsDouble(double x);

    Function derivative();

}
