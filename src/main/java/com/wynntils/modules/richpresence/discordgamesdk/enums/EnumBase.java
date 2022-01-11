/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.enums;

public interface EnumBase {
    /**
     * @return the native ordinal
     */
    public default int getOrdinal() {
        Enum<?> enumValue = ((Enum<?>) this);
        int javaOrdinal = enumValue.ordinal();
        return enumValue.ordinal() != 0 ? this.getClass().getEnumConstants()[javaOrdinal - 1].getOrdinal() + 1 : 0;
    }
}
