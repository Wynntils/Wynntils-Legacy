/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.reflections;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum ReflectionClasses {

    SPacketPlayerListItem$AddPlayerData("net.minecraft.network.play.server.SPacketPlayerListItem$AddPlayerData");

    public final Class<?> clazz;

    ReflectionClasses(String... names) {
        this.clazz = ReflectionHelper.getClass(ReflectionClasses.class.getClassLoader(), names);
    }

    public Object construct(Class<?>[] parameterTypes, Object... arguments) {
        assert parameterTypes.length == arguments.length;

        Constructor<?> cons;
        try {
            cons = clazz.getDeclaredConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
        cons.setAccessible(true);
        try {
            return cons.newInstance(arguments);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
