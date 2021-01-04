/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.enums;

import net.minecraft.util.text.TextFormatting;

public enum DiscoveryType {

    TERRITORY(0, TextFormatting.WHITE),
    WORLD(1, TextFormatting.YELLOW),
    SECRET(2, TextFormatting.AQUA);

    int order;
    TextFormatting colour;

    DiscoveryType(int order, TextFormatting colour) {
        this.order = order;
        this.colour = colour;
    }

    public int getOrder() {
        return order;
    }

    public TextFormatting getColour() {
        return colour;
    }

}
