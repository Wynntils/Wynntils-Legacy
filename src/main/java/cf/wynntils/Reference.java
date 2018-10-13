package cf.wynntils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Reference {

    public static final String MOD_ID = "wynntils";
    public static final String NAME = "Wynntils";
    public static final String MINECRAFT_VERSIONS = "1.11,1.12.2";
    public static String VERSION = "";
    public static final File MOD_STORAGE_ROOT = new File("wynntils");
    public static final File MOD_ASSETS_ROOT = new File(MOD_STORAGE_ROOT + "\\assets");
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    private static String userWorld = null;

    public static void setUserWorld(String uw) {
        onServer = true;
        userWorld = uw;

        onWorld = onServer && userWorld != null;
        onNether = onWorld && userWorld.contains("N");//Find a better thing to use than checking for "N" maybe, haven't seen what the actual text for nether is  --SHCM
        onWars = onWorld && userWorld.contains("WAR");
        onLobby = onServer && !onWorld;
    }

    public static String getUserWorld() {
        return userWorld;
    }

    public static boolean onServer = false;
    public static boolean onWorld = false;
    public static boolean onNether = false;
    public static boolean onWars = false;
    public static boolean onLobby = false;

}
