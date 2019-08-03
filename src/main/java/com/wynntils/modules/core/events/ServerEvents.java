/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.core.instances.PacketIncomingFilter;
import com.wynntils.modules.core.instances.PacketOutgoingFilter;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import com.wynntils.modules.core.overlays.ui.ChangelogUI;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.downloader.DownloaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.util.HashSet;

public class ServerEvents implements Listener {

    /**
     * Does 4 different things and is triggered when the user joins Wynncraft:
     *  - Register the pipeline that intercepts INCOMING Packets
     *  @see PacketIncomingFilter
     *  - Register the pipline that intercepts OUTGOING Packets
     *  @see PacketOutgoingFilter
     *  - Check if the mod has an update available
     *  - Check if there is anything on the download queue
     *
     * @param e Represents the event
     */
    @SubscribeEvent
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        e.getManager().channel().pipeline().addBefore("fml:packet_handler", Reference.MOD_ID + ":packet_filter", new PacketIncomingFilter());
        e.getManager().channel().pipeline().addBefore("fml:packet_handler", Reference.MOD_ID + ":outgoingFilter", new PacketOutgoingFilter());

        WebManager.checkForUpdates();
        DownloaderManager.startDownloading();
    }

    boolean waitingForFriendList = false;
    boolean waitingForGuildList = false;

    /**
     * Called when the user joins a Wynncraft World, used to register some stuff:
     *  - Make the player use the command /friend list in order to gatter the user friend list
     *  - Check if the last user class was registered if not, make the player execute /class to register it
     *  - Updates the last class
     *
     * @param e Represents the event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void joinWorldEvent(WynnWorldEvent.Join e) {
        if(PlayerInfo.getPlayerInfo().getClassId() == -1 || CoreDBConfig.INSTANCE.lastClass == ClassType.NONE) Minecraft.getMinecraft().player.sendChatMessage("/class");
        if(CoreDBConfig.INSTANCE.lastClass != ClassType.NONE) PlayerInfo.getPlayerInfo().updatePlayerClass(CoreDBConfig.INSTANCE.lastClass);

        waitingForFriendList = true;

        if(WebManager.getPlayerProfile() != null && WebManager.getPlayerProfile().getGuildName() != null) {
            waitingForGuildList = true;
            Minecraft.getMinecraft().player.sendChatMessage("/guild list");
        }
        Minecraft.getMinecraft().player.sendChatMessage("/friends list");
    }

    /**
     * Detects and register the current friend list of the user
     * Called when the client receives a chat message
     *
     * Also detects the guild list messages to register the guild members
     *
     * @param e Represents the Event
     */
    @SubscribeEvent
    public void chatMessage(ClientChatReceivedEvent e) {
        if(e.isCanceled() || e.getType() != ChatType.SYSTEM) {
            return;
        }
        if(e.getMessage().getUnformattedText().startsWith(Minecraft.getMinecraft().player.getName() + "'")) {
            String[] splited = e.getMessage().getUnformattedText().split(":");

            String[] friends;
            if(splited[1].contains(",")) {
                friends = splited[1].substring(1).split(", ");
            }else{ friends = new String[] {splited[1].substring(1)}; }

            PlayerInfo.getPlayerInfo().setFriendList(new HashSet<>(Arrays.asList(friends)));

            if(waitingForFriendList) e.setCanceled(true);
            waitingForFriendList = false;
            return;
        }
        if(e.getMessage().getUnformattedText().startsWith("#") && e.getMessage().getUnformattedText().contains(" XP -")) {
            if(waitingForGuildList) e.setCanceled(true);

            String[] messageSplitted = e.getMessage().getUnformattedText().split(" ");
            PlayerInfo.getPlayerInfo().getGuildList().add(messageSplitted[1]);
            return;
        }
        if(!e.getMessage().getUnformattedText().startsWith("[") && e.getMessage().getUnformattedText().contains("guild") && e.getMessage().getUnformattedText().contains(" ")) {
            String[] splittedText = e.getMessage().getUnformattedText().split(" ");
            if(!splittedText[1].equalsIgnoreCase("has")) return;

            if(splittedText[2].equalsIgnoreCase("joined")) PlayerInfo.getPlayerInfo().getGuildList().add(splittedText[0]);
            else if(splittedText[2].equalsIgnoreCase("kicked")) PlayerInfo.getPlayerInfo().getGuildList().remove(splittedText[3]);
        }
    }

    /**
     * Detects if the user added or removed a user from their friend list
     * Called when the user execute /friend add or /friend remove
     *
     * Also detects the guild list command used to parse the entire guild list
     *
     * @param e Represents the Event
     */
    @SubscribeEvent
    public void addFriend(ClientChatEvent e) {
        if(e.getMessage().startsWith("/friend add ")) {
            PlayerInfo.getPlayerInfo().getFriendList().add(e.getMessage().replace("/friend add ", ""));
        }else if(e.getMessage().startsWith("/friend remove ")) {
            PlayerInfo.getPlayerInfo().getFriendList().remove(e.getMessage().replace("/friend remove ", ""));
        }else if(e.getMessage().startsWith("/guild list")) {
            waitingForGuildList = false;
        }
    }

    /**
     * Detects when the user enters the Wynncraft Server
     * Used for displaying the Changelog UI
     *
     * @param e
     */
    @SubscribeEvent
    public void onJoinLobby(RenderPlayerEvent.Post e) {
        if(CoreDBConfig.INSTANCE.enableChangelogOnUpdate && CoreDBConfig.INSTANCE.showChangelogs) {
            if(UpdateOverlay.isDownloading() || DownloaderManager.isRestartOnQueueFinish() || Minecraft.getMinecraft().world == null) return;

            CoreDBConfig.INSTANCE.showChangelogs = false;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());

            boolean major = !CoreDBConfig.INSTANCE.lastVersion.equals(Reference.VERSION) || CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE;
            Minecraft.getMinecraft().displayGuiScreen(new ChangelogUI(WebManager.getChangelog(major), major));
        }
    }

    static BlockPos currentSpawn = null;

    @SubscribeEvent
    public void onCompassChange(PacketEvent.SpawnPosition e) {
        currentSpawn = e.getPacket().getSpawnPos();
        if (CompassManager.getCompassLocation() != null) {
            e.setCanceled(true);
        }
    }

    public static BlockPos getCurrentSpawnPosition() {
        return currentSpawn;
    }

}
