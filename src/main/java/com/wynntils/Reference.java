/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils;

import java.io.File;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Platform;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;

import net.minecraft.client.multiplayer.ServerData;

public class Reference {

    public static final String MOD_ID = "wynntils";
    public static final String NAME = "Wynntils";
    public static final String MINECRAFT_VERSIONS = "1.12,1.12.2";
    public static String VERSION = "";
    public static int BUILD_NUMBER = -1;
    public static final File MOD_STORAGE_ROOT = new File(McIf.mc().gameDir, MOD_ID);
    public static final File NATIVES_ROOT = new File(Reference.MOD_STORAGE_ROOT, "natives");
    public static final File PLATFORM_NATIVES_ROOT = new File(NATIVES_ROOT, Platform.RESOURCE_PREFIX);
    public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

    private static String userWorld = null;

    public static synchronized void setUserWorld(String uw) {
        if ("-".equals(uw)) {
            inStream = true;
            return;
        }

        inStream = false;

        ServerData currentServer = McIf.mc().getCurrentServerData();
        String lowerIP = currentServer == null || currentServer.serverIP == null ? null : currentServer.serverIP.toLowerCase(Locale.ROOT);
        onServer = !McIf.mc().isSingleplayer() && lowerIP != null && !currentServer.isOnLAN() && lowerIP.contains("wynncraft");
        onServer &= lowerIP != null && !lowerIP.startsWith("beta.") || (!WebManager.blockHeroBetaCuttingEdge() && CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE) || (!WebManager.blockHeroBetaStable() && CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE);
        userWorld = uw;

        onWorld = onServer && userWorld != null;
        onWars = onWorld && userWorld.contains("WAR");
        onBeta = onWorld && userWorld.contains("HB") || onServer && lowerIP.startsWith("beta.");
        onLobby = onServer && !onWorld;
    }

    public static synchronized String getUserWorld() {
        return userWorld;
    }

    public static boolean inClassSelection() {
        return onWorld && !PlayerInfo.get(CharacterData.class).isLoaded();
    }

    public static boolean onServer = false;

    public static boolean onWorld = false;
    public static boolean onWars = false;
    public static boolean onBeta = false;
    public static boolean onLobby = false;

    public static boolean inStream = false;

    public static boolean developmentEnvironment = false;

    public static class ServerIPS {
        public static final String GAME = "play.wynncraft.com";
    }

}
