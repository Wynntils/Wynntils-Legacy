/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.utils.objects;

public class Functions {

    /**
     * f(x) = a
     */
    public static final class Constant implements Function {
        public final double a;

        public Constant() {
            this(0);
        }

        public Constant(double a) {
            this.a = a;
        }

        @Override
        public double applyAsDouble(double x) {
            return a;
        }

        @Override
        public Constant derivative() {
            return new Constant();
        }
    }

    /**
     * f(x) = b * x + a
     */
    public static final class Linear implements Function {
        public final double a, b;

        public Linear(double a, double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double applyAsDouble(double x) {
            return b * x + a;
        }

        @Override
        public Constant derivative() {
            return new Constant(b);
        }
    }

    /**
     * f(x) = c * x*x + b * x + a
     */
    public static final class Quadratic implements Function {
        public final double a, b, c;

        public Quadratic(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public double applyAsDouble(double x) {
            return (c * x + b) * x + a;
        }

        @Override
        public Linear derivative() {
            return new Linear(b, 2 * c);
        }
    }

    /**
     * f(x) = d * x*x*x + c * x*x + b * x + a
     */
    public static final class Cubic implements Function {
        public final double a, b, c, d;

        public Cubic(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        @Override
        public double applyAsDouble(double t) {
            return (((d * t) + c) * t + b) * t + a;
        }

        @Override
        public Quadratic derivative() {
            return new Quadratic(b, 2 * c, 3 * d);
        }
    }

    /**
     * f(x) = e * x*x*x*x + d * x*x*x + c * x*x + b * x + a
     */
    public static final class Quartic implements Function {
        public final double a, b, c, d, e;

        public Quartic(double a, double b, double c, double d, double e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        @Override
        public double applyAsDouble(double t) {
            return ((((e * t) + d * t) + c) * t + b) * t + a;
        }

        @Override
        public Cubic derivative() {
            return new Cubic(b, 2 * c, 3 * d, 4 * e);
        }
    }

}
