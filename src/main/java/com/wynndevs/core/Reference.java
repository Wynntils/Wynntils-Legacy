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
    public static String userWorld = null;
    public static boolean onWorld() { return onServer() && userWorld != null; }

}