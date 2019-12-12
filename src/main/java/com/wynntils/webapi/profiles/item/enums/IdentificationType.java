/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.enums;

public enum IdentificationType {

    INTEGER(""),
    PERCENTAGE("%"),
    FOUR_SECONDS("/4s"),
    THREE_SECONDS("/3s"),
    TIER(" tier");

    String inGame;

    IdentificationType(String inGame) {
        this.inGame = inGame;
    }

    public String getInGame() {
        return inGame;
    }

}
