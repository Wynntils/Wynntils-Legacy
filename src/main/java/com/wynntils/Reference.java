/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils;

import com.sun.jna.Platform;
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
    public static final File MOD_STORAGE_ROOT = new File(ModCore.mc().gameDir, "wynntils");
    public static final File NATIVES_ROOT = new File(Reference.MOD_STORAGE_ROOT, "natives");
    public static final File PLATFORM_NATIVES_ROOT = new File(NATIVES_ROOT, Platform.RESOURCE_PREFIX);
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    private static String userWorld = null;

    public static synchronized void setUserWorld(String uw) {
        ServerData currentServer = ModCore.mc().getCurrentServerData();
        String lowerIP = currentServer == null || currentServer.serverIP == null ? null : currentServer.serverIP.toLowerCase(Locale.ROOT);
        onServer = !ModCore.mc().isSingleplayer() && lowerIP != null && !currentServer.isOnLAN() && lowerIP.contains("wynncraft");
        onEuServer = onServer && lowerIP.startsWith("eu");
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
