/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.reflections;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum ReflectionMethods {

    Minecraft$setWindowIcon(Minecraft.class, "setWindowIcon", "func_175594_ao"),
    SPacketPlayerListItem$AddPlayerData_getProfile(ReflectionClasses.SPacketPlayerListItem$AddPlayerData.clazz, "getProfile", "func_179962_a"),
    SPacketPlayerListItem$AddPlayerData_getDisplayName(ReflectionClasses.SPacketPlayerListItem$AddPlayerData.clazz, "getDisplayName", "func_179961_d");

    final Method method;

    ReflectionMethods(Class<?> holdingClass, String deobf, String obf, Class<?>... parameterTypes) {
        this.method = ReflectionHelper.findMethod(holdingClass, deobf, obf, parameterTypes);
    }

    public Object invoke(Object obj, Object... parameters) {
        try {
            return method.invoke(obj, parameters);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
