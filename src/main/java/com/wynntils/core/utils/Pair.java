/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

/**
 * The Pair Type Holds 1 field of type T and 1 field of type J
 */
public class Pair<T,J> {
    public T a;
    public J b;

    public Pair(T a, J b) {
        this.a = a;
        this.b = b;
    }
    public Pair() {
        this(null,null);
    }

    @Override
    public String toString() {
        return a.toString() + ", " + b.toString();
    }
}

