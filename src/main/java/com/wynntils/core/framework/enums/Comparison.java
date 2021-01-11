package com.wynntils.core.framework.enums;

import java.util.function.IntPredicate;

public enum Comparison implements IntPredicate {

    LESS_THAN("<", a -> a < 0),
    LESS_THAN_OR_EQUAL("<=", a -> a <= 0),
    EQUAL("=", a -> a == 0),
    GREATER_THAN_OR_EQUAL(">=", a -> a >= 0),
    GREATER_THAN(">", a -> a > 0),
    NOT_EQUAL("!=", a -> a != 0);

    public final String symbol;

    private final IntPredicate check;

    Comparison(String symbol, IntPredicate check) {
        this.symbol = symbol;
        this.check = check;
    }


    @Override
    public boolean test(int value) {
        return check.test(value);
    }

    public <T extends Comparable<T>> boolean test(T a, T b) {
        return test(a.compareTo(b));
    }

}
