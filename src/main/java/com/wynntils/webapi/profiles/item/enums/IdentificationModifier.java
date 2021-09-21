/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.modules.items.configs.ItemsConfig;

public enum IdentificationModifier {

    INTEGER(""),
    PERCENTAGE("%"),
    FOUR_SECONDS("/4s"),
    THREE_SECONDS("/3s"),
    TIER(" tier");

    String inGame;

    IdentificationModifier(String inGame) {
        this.inGame = inGame;
    }

    public String getInGame(String name) {
        if (this != FOUR_SECONDS || ItemsConfig.Identifications.INSTANCE.legacyIds) return inGame;

        if (name.equals("manaRegen")) return "/5s";
        return "/3s";
    }

}
