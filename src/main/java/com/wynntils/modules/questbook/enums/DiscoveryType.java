/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.enums;

public enum DiscoveryType {

    TERRITORY(0), WORLD(1), SECRET(2);

    int order;

    DiscoveryType(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

}
