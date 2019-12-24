package com.wynntils.core.utils;

import com.wynntils.Reference;
import com.wynntils.core.utils.reflections.ReflectionFields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.Locale;
import java.util.Objects;

public class ServerUtils {

    public static void connect(ServerData serverData) {
        connect(serverData, true);
    }

    public static void connect(ServerData serverData, boolean unloadCurrentServerResourcePack) {
        connect(new GuiMultiplayer(new GuiMainMenu()), serverData, unloadCurrentServerResourcePack);
    }

    public static void connect(GuiScreen backGui, ServerData serverData) {
        connect(backGui, serverData, false);
    }

    /**
     * Connect to a server, possibly disconnecting if already on a world.
     *
     * @param backGui GUI to return to on failure
     * @param serverData The server to connect to
     * @param unloadCurrentServerResourcePack If false, retain the same server resource pack between disconnecting and connecting
     */
    public static void connect(GuiScreen backGui, ServerData serverData, boolean unloadCurrentServerResourcePack) {
        disconnect(false, unloadCurrentServerResourcePack);
        FMLClientHandler.instance().connectToServer(backGui, serverData);
    }

    public static void disconnect() {
        disconnect(true);
    }

    public static void disconnect(boolean switchGui) {
        disconnect(switchGui, false);
    }

    /**
     * Disconnect from the current server
     *
     * @param switchGui If true, the current gui is changed (to the main menu in singleplayer, or multiplayer gui)
     * @param unloadServerPack if false, disconnect without refreshing resources by unloading the server resource pack
     */
    public static void disconnect(boolean switchGui, boolean unloadServerPack) {
        Minecraft mc = Minecraft.getMinecraft();

        WorldClient world = mc.world;
        if (world == null) return;

        boolean singlePlayer = mc.isIntegratedServerRunning();
        boolean realms = !singlePlayer && mc.isConnectedToRealms();

        world.sendQuittingDisconnectingPacket();
        if (unloadServerPack) {
            mc.loadWorld(null);
        } else {
            loadWorldWithoutUnloadingServerResourcePack(null);
        }

        if (!switchGui) return;
        if (singlePlayer) {
            mc.displayGuiScreen(new GuiMainMenu());
        } else if (realms) {
            // Should not be possible because Wynntils will
            // never be running on the latest version of Minecraft
            new RealmsBridge().switchToRealms(new GuiMainMenu());
        } else {
            mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
        }
    }

    public static void loadWorldWithoutUnloadingServerResourcePack(WorldClient world) {
        loadWorldWithoutUnloadingServerResourcePack(world, "");
    }

    private static ResourcePackRepository wontUnload = null;

    public static synchronized void loadWorldWithoutUnloadingServerResourcePack(WorldClient world, String loadingMessage) {
        ResourcePackRepository original = Minecraft.getMinecraft().getResourcePackRepository();
        if (wontUnload == null) {
            wontUnload = new ResourcePackRepository(original.getDirResourcepacks(), null, null, null, new GameSettings()) {
                @Override
                public void clearResourcePack() {
                    // Don't
                }
            };
        }
        ReflectionFields.Minecraft_resourcePackRepository.setValue(Minecraft.getMinecraft(), wontUnload);
        Minecraft.getMinecraft().loadWorld(world, loadingMessage);
        ReflectionFields.Minecraft_resourcePackRepository.setValue(Minecraft.getMinecraft(), original);
    }

    public static ServerData getWynncraftServerData(boolean addNew) {
        return getWynncraftServerData(new ServerList(Minecraft.getMinecraft()), addNew, Reference.ServerIPS.us);
    }

    public static ServerData getWynncraftServerData(ServerList serverList, boolean addNew) {
        return getWynncraftServerData(serverList, addNew, Reference.ServerIPS.us);
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
