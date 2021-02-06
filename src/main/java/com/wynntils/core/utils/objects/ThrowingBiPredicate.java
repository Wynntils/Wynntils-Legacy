/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.objects;

@FunctionalInterface
public interface ThrowingBiPredicate<T, U, Exception extends Throwable> {

    boolean test(T t, U u) throws Exception;

}
