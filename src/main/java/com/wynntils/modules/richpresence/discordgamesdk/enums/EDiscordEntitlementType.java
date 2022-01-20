/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

/**
 * <i>native declaration : line 180</i><br>
 * enum values
 */
public enum EDiscordEntitlementType implements EnumBase {
    /** <i>native declaration : line 181</i> */
    DiscordEntitlementType_Purchase {
        @Override
        public int getOrdinal() {
            return 1;
        }
    },
    /** <i>native declaration : line 182</i> */
    DiscordEntitlementType_PremiumSubscription,
    /** <i>native declaration : line 183</i> */
    DiscordEntitlementType_DeveloperGift,
    /** <i>native declaration : line 184</i> */
    DiscordEntitlementType_TestModePurchase,
    /** <i>native declaration : line 185</i> */
    DiscordEntitlementType_FreePurchase,
    /** <i>native declaration : line 186</i> */
    DiscordEntitlementType_UserGift,
    /** <i>native declaration : line 187</i> */
    DiscordEntitlementType_PremiumPurchase;
}
