package com.wynntils.core.utils.reflections;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;

public enum ReflectionMethods {

    ;

    final Method method;

    ReflectionMethods(Class<?> holdingClass, String deobf, String obf) {
        this.method = ReflectionHelper.findMethod(holdingClass, deobf, obf);
        this.method.setAccessible(true);
    }

    public void invoke(Object parent, Object... fields) {
        try{
            method.invoke(parent, fields);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
