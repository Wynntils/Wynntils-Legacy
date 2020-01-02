/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.utils.objects;

import java.util.Objects;

/**
 * The Pair Type Holds 1 field of type T and 1 field of type J
 */
public class Pair<T, J> {

    public T a;
    public J b;

    public Pair(T a, J b) {
        this.a = a;
        this.b = b;
    }

    public Pair() {
        this(null, null);
    }

    @Override
    public String toString() {
        return a.toString() + ", " + b.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair<?, ?> other = (Pair<?, ?>) obj;
        return Objects.deepEquals(a, other.a) && Objects.deepEquals(b, other.b);
    }

}
