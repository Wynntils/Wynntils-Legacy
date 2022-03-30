/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums;

public enum SortDirection {

    ASCENDING("^") {
        @Override
        public int modifyComparison(int cmp) {
            return cmp;
        }
    },

    DESCENDING("$") {
        @Override
        public int modifyComparison(int cmp) {
            return -cmp;
        }
    },

    NONE("") {
        @Override
        public int modifyComparison(int cmp) {
            return 0;
        }
    };

    public final String prefix;

    SortDirection(String prefix) {
        this.prefix = prefix;
    }

    public abstract int modifyComparison(int cmp);

}
