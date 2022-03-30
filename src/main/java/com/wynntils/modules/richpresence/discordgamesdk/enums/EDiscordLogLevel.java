/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

/**
 * <i>native declaration : line 83</i><br>
 * enum values
 */
public enum EDiscordLogLevel implements EnumBase {
    /** <i>native declaration : line 84</i> */
    DiscordLogLevel_Error {
        @Override
        public int getOrdinal() {
            return 1;
        }
    },
    /** <i>native declaration : line 85</i> */
    DiscordLogLevel_Warn,
    /** <i>native declaration : line 86</i> */
    DiscordLogLevel_Info,
    /** <i>native declaration : line 87</i> */
    DiscordLogLevel_Debug;
}
