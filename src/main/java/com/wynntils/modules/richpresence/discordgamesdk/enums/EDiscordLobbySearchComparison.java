/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

/**
 * <i>native declaration : line 147</i><br>
 * enum values
 */
public enum EDiscordLobbySearchComparison implements EnumBase {
    /** <i>native declaration : line 148</i> */
    DiscordLobbySearchComparison_LessThanOrEqual {
        @Override
        public int getOrdinal() {
            return -2;
        }
    },
    /** <i>native declaration : line 149</i> */
    DiscordLobbySearchComparison_LessThan,
    /** <i>native declaration : line 150</i> */
    DiscordLobbySearchComparison_Equal,
    /** <i>native declaration : line 151</i> */
    DiscordLobbySearchComparison_GreaterThan,
    /** <i>native declaration : line 152</i> */
    DiscordLobbySearchComparison_GreaterThanOrEqual,
    /** <i>native declaration : line 153</i> */
    DiscordLobbySearchComparison_NotEqual;
}
