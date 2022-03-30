/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.discordgamesdk.converters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.wynntils.modules.richpresence.discordgamesdk.enums.EnumBase;

public class EnumConverter<T extends Enum<T> & EnumBase> implements TypeConverter {

    private BiMap<Integer, T> map = HashBiMap.create();

    public EnumConverter(Class<T> clazz) {
        for (T enumValue : clazz.getEnumConstants()) {
            map.put(enumValue.getOrdinal(), enumValue);
        }
    }

    @Override
    public Object fromNative(Object nativeValue, FromNativeContext context) {
        return map.get(nativeValue);
    }

    @Override
    public Class<?> nativeType() {
        return Integer.class;
    }

    @Override
    public Object toNative(Object value, ToNativeContext context) {
        return map.inverse().get(value);
    }

}
