/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles.item.enums;

import net.minecraft.util.text.TextFormatting;

public enum ItemAttackSpeed {

    SUPER_FAST("Super Fast Attack Speed"),
    VERY_FAST("Very Fast Attack Speed"),
    FAST("Fast Attack Speed"),
    NORMAL("Normal Attack Speed"),
    SLOW("Slow Attack Speed"),
    VERY_SLOW("Very Slow Attack Speed"),
    SUPER_SLOW("Super Slow Attack Speed");

    String name;

    ItemAttackSpeed(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String asLore() {
        return TextFormatting.GRAY + name;
    }

}
