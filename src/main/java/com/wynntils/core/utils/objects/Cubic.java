package com.wynntils.core.utils.objects;

import java.util.function.DoubleUnaryOperator;

/**
 * Represents f(t) = d * t*t*t + c * t*t + b * t + a
 */
public final class Cubic implements DoubleUnaryOperator {
    public final double a, b, c, d;

    public Cubic(double a, double b, double c, double d) {
        this.a = a; this.b = b; this.c = c; this.d = d;
    }

    @Override
    public double applyAsDouble(double t) {
        return (((d * t) + c) * t + b) * t + a;
    }
}
