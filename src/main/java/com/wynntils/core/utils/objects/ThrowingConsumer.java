/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.objects;

@FunctionalInterface
public interface ThrowingConsumer<T, Exception extends Throwable> {

    void accept(T t) throws Exception;

}
