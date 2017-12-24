package com.wynndevs.core;

import com.wynndevs.ModCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reference {

    public static final String MOD_ID = "wynnexp";
    public static final String NAME = "WynncraftExpansion";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);
    public static boolean onServer() { return !ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData().serverIP.contains("wynncraft"); }


    public static void copyClass(Class <?> c, Object src, Object dest) {
        Field[] fields = c.getDeclaredFields();
        Field field;
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            if ((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL && (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
                continue;
            field.setAccessible(false);
            field.setAccessible(true);
            try {
                field.set(dest, field.get(src));
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    public static void copyClassDeep(Class <?> c, Object src, Object dest) {
        Class clazz = c;

        do {
            copyClass(clazz, src, dest);
        } while (!(clazz = clazz.getSuperclass()).equals(Object.class));
    }

    public static <T> T getField(Class <?> clazz, int id, Object owner, Object... nested) {
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