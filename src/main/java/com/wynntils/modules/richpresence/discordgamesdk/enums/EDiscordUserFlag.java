/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

/**
 * <i>native declaration : line 90</i><br>
 * enum values
 */
public enum EDiscordUserFlag implements EnumBase {
    /** <i>native declaration : line 91</i> */
    DiscordUserFlag_Partner {
        @Override
        public int getOrdinal() {
            return 2;
        }
    },
    /** <i>native declaration : line 92</i> */
    DiscordUserFlag_HypeSquadEvents {
        @Override
        public int getOrdinal() {
            return 4;
        }
    },
    /** <i>native declaration : line 93</i> */
    DiscordUserFlag_HypeSquadHouse1 {
        @Override
        public int getOrdinal() {
            return 64;
        }
    },
    /** <i>native declaration : line 94</i> */
    DiscordUserFlag_HypeSquadHouse2 {
        @Override
        public int getOrdinal() {
            return 128;
        }
    },
    /** <i>native declaration : line 95</i> */
    DiscordUserFlag_HypeSquadHouse3 {
        @Override
        public int getOrdinal() {
            return 256;
        }
    };
}
