/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils;

import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Locale;

public class Reference {

    public static final String MOD_ID = "wynntils";
    public static final String NAME = "Wynntils";
    public static final String MINECRAFT_VERSIONS = "1.12,1.12.2";
    public static String VERSION = "";
    public static int BUILD_NUMBER = -1;
    public static final File MOD_STORAGE_ROOT = new File("wynntils");
    public static final File MOD_ASSETS_ROOT = new File(MOD_STORAGE_ROOT, "assets");
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    private static String userWorld = null;

    public static synchronized void setUserWorld(String uw) {
        ServerData currentServer = ModCore.mc().getCurrentServerData();
        onServer = !ModCore.mc().isSingleplayer() && currentServer != null && currentServer.serverIP.toLowerCase(Locale.ROOT).contains("wynncraft");
        onEuServer = onServer && currentServer.serverIP.toLowerCase(Locale.ROOT).startsWith("eu");
        userWorld = uw;

        onWorld = onServer && userWorld != null;
        onNether = onWorld && userWorld.contains("N");
        onWars = onWorld && userWorld.contains("WAR");
        onBeta = onWorld && userWorld.contains("HB");
        onLobby = onServer && !onWorld;
    }

    public static String getUserWorld() {
        return userWorld;
    }

    public static boolean onServer = false;
    public static boolean onEuServer = false;

    public static boolean onWorld = false;
    public static boolean onNether = false;
    public static boolean onWars = false;
    public static boolean onBeta = false;
    public static boolean onLobby = false;

    public static boolean developmentEnvironment = false;

    public static class ServerIPS {

        public static final String us = "play.wynncraft.com";
        public static final String eu = "eu.wynncraft.com";

    }

}
