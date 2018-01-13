package com.wynndevs.core;

import com.wynndevs.ModCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Reference {

    public static final String MOD_ID = "wynntils";
    public static final String NAME = "Wynntils";
    public static final String VERSION = "1.0.0";
    public static final File MOD_STORAGE_ROOT = new File("wynntils");
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);
    public static String userWorld = null;
    public static boolean onServer() { return !ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && ModCore.mc().getCurrentServerData().serverIP.contains("wynncraft"); }
    public static boolean onWorld() { return onServer() && userWorld != null; }
    public static boolean onNether() {return onWorld() && userWorld.contains("N"); }
    public static boolean onWars() {return onWorld() && userWorld.contains("WAR"); }

}