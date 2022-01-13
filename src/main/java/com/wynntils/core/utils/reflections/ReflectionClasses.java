/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

public enum ReflectionClasses {

    SPacketPlayerListItem$AddPlayerData("net.minecraft.network.play.server.SPacketPlayerListItem$AddPlayerData");

    public final Class<?> clazz;

    ReflectionClasses(String... names) {
        this.clazz = ReflectionHelper.getClass(ReflectionClasses.class.getClassLoader(), names);
    }

    public Object construct() {
        return construct(new Class<?>[0]);
    }

    public <T1> Object construct(Class<T1> type1, T1 arg1) {
        return construct(new Class<?>[] { type1 }, arg1);
    }

    public <T1, T2> Object construct(Class<T1> type1, Class<T2> type2, T1 arg1, T2 arg2) {
        return construct(new Class<?>[] { type1, type2 }, arg1, arg2);
    }

    public <T1, T2, T3> Object construct(Class<T1> type1, Class<T2> type2, Class<T3> type3, T1 arg1, T2 arg2, T3 arg3) {
        return construct(new Class<?>[] { type1, type2, type3 }, arg1, arg2, arg3);
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
