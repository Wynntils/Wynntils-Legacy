package com.wynntils.core.utils;

import com.wynntils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.Locale;
import java.util.Objects;

public class ServerUtils {

    public static void connect(ServerData serverData) {
        connect(new GuiMultiplayer(new GuiMainMenu()), serverData);
    }

    /**
     * Connect to a server, possibly disconnecting if already on a world.
     *
     * @param backGui GUI to return to on failure
     * @param serverData The server to connect to
     */
    public static void connect(GuiScreen backGui, ServerData serverData) {
        disconnect(false);
        FMLClientHandler.instance().connectToServer(backGui, serverData);
    }

    /**
     * Disconnect from the current server
     *
     * @param switchGui If true, the current gui is changed (to the main menu in singleplayer, or multiplayer gui)
     */
    public static void disconnect(boolean switchGui) {
        Minecraft mc = Minecraft.getMinecraft();

        WorldClient world = mc.world;
        if (world == null) return;

        world.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);

        if (!switchGui) return;
        if (mc.isIntegratedServerRunning()) {
            mc.displayGuiScreen(new GuiMainMenu());
        } else if (mc.isConnectedToRealms()) {
            // Should not be possible because Wynntils will
            // never be running on the latest version of Minecraft
            new RealmsBridge().switchToRealms(new GuiMainMenu());
        } else {
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }

    public static ServerData getWynncraftServerData(boolean addNew) {
        return getWynncraftServerData(new ServerList(Minecraft.getMinecraft()), addNew, Reference.ServerIPS.us);
    }

    public static ServerData getWynncraftServerData(ServerList serverList, boolean addNew) {
        return getWynncraftServerData(serverList, addNew, Reference.ServerIPS.eu);
    }

    /**
     * @param serverList A ServerList
     * @param addNew If true and no server data is found, the newly created server data will be added and saved.
     * @param ip The ip to use if not found
     * @return The server data in the given serverList for Wynncraft (Or a new one if none are found)
     */
    public static ServerData getWynncraftServerData(ServerList serverList, boolean addNew, String ip) {
        ServerData server = null;

        int i = 0, count = serverList.countServers();
        for (; i < count; ++i) {
            server = serverList.getServerData(i);
            if (server.serverIP.toLowerCase(Locale.ROOT).contains("wynncraft")) {
                break;
            }
        }

        if (i >= count) {
            server = new ServerData("Wynncraft", ip, false);
            if (addNew) {
                serverList.addServerData(server);
                serverList.saveServerList();
            }
        }

        return server;
    }

    public static ServerData changeServerIP(ServerData serverData, String newIp, String defaultName) {
        return changeServerIP(new ServerList(Minecraft.getMinecraft()), serverData, newIp, defaultName);
    }

    /**
     * Change the serverIP of a ServerData and save the results
     *
     * @param list The server list
     * @param serverData The old server data
     * @param newIp The ip to change to
     * @param defaultName The name to set to if the server data is not found
     * @return The newly saved ServerData
     */
    public static ServerData changeServerIP(ServerList list, ServerData serverData, String newIp, String defaultName) {
        if (serverData == null) {
            list.addServerData(serverData = new ServerData(defaultName, newIp, false));
            list.saveServerList();
            return serverData;
        }

        for (int i = 0, length = list.countServers(); i < length; ++i) {
            ServerData fromList = list.getServerData(i);
            if (Objects.equals(fromList.serverIP, serverData.serverIP) && Objects.equals(fromList.serverName, serverData.serverName)) {
                // Found the server data; Replace the ip
                fromList.serverIP = newIp;
                list.saveServerList();
                return fromList;
            }
        }

        // Not found
        list.addServerData(serverData = new ServerData(defaultName, newIp, false));
        list.saveServerList();
        return serverData;
    }

}
