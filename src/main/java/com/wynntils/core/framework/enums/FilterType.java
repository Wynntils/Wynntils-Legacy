/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums;

import java.util.function.BiPredicate;

public enum FilterType implements BiPredicate<String, String> {

    CONTAINS(String::contains),
    EQUALS(String::equals),
    EQUALS_IGNORE_CASE(String::equalsIgnoreCase),
    STARTS_WITH(String::startsWith);

    BiPredicate<String, String> predicate;

    FilterType(BiPredicate<String, String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(String itemName, String expectedName) {
        return predicate.test(itemName, expectedName);
    }

}
