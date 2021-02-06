/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

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

    public String getInGame() {
        return inGame;
    }

}
