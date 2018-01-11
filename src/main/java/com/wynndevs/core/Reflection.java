package com.wynndevs.core;

import java.lang.reflect.Field;

public class Reflection {
    public static <T> T getField(Class <?> clazz, int id, Object owner, Object... nested){
        Field field = clazz.getDeclaredFields()[id];
        boolean a = field.isAccessible();
        field.setAccessible(true);
        Object obj = null;
        try {
            obj = field.get(owner);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
        field.setAccessible(a);
        if (nested != null && nested.length > 2) {
            int len = nested.length / 3;
            for (int i = 0; i < len; i++) {
                Class <?> nclazz = null;
                int nid = -1;
                Object nowner = null;
                try {
                    nclazz = (Class <?>) nested[i * 3 + 0];
                    nid = (int) nested[i * 3 + 1];
                    nowner = nested[i * 3 + 2];
                } catch (ClassCastException e) {
                    return null;
                }
                Field nfield = nclazz.getDeclaredFields()[nid];
                boolean na = nfield.isAccessible();
                nfield.setAccessible(true);
                try {
                    obj = nfield.get(nowner);
                } catch (IllegalArgumentException e) {
                    return null;
                } catch (IllegalAccessException e) {
                    return null;
                }
                nfield.setAccessible(na);
            }
        }
        T t = null;
        try {
            t = (T) obj;
        } catch (ClassCastException e) {
        }
        return t;
    }
}
