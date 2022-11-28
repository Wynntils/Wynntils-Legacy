/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

/**
 * <i>native declaration : line 190</i><br>
 * enum values
 */
public enum EDiscordSkuType implements EnumBase {
    /** <i>native declaration : line 191</i> */
    DiscordSkuType_Application {
        @Override
        public int getOrdinal() {
            return 1;
        }
    },
    /** <i>native declaration : line 192</i> */
    DiscordSkuType_DLC,
    /** <i>native declaration : line 193</i> */
    DiscordSkuType_Consumable,
    /** <i>native declaration : line 194</i> */
    DiscordSkuType_Bundle;
}
