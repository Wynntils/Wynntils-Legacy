/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientEvents {

    private static final UUID worldUUID = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");
    private boolean inClassSelection = false;
    private String lastWorld = "";
    private boolean acceptLeft = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.toLowerCase().contains("wynncraft")) {
            Reference.setUserWorld(null);
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Login());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if(Reference.onServer) {
            Reference.onServer = false;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Leave());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void triggerGameEvents(ClientChatReceivedEvent e) {
        if(e.getType() == ChatType.GAME_INFO) return;

        String message = e.getMessage().getUnformattedText();

        GameEvent toDispatch = null;
        if(message.startsWith("[New Quest Started:")) toDispatch = new GameEvent.QuestStarted(message.replace("[New Quest Started: ", "").replace("]", ""));
        else if(message.startsWith("[Quest Book Updated]")) toDispatch = new GameEvent.QuestUpdated();
        else if(message.contains("[Quest Completed]") && !message.contains(":")) toDispatch = new GameEvent.QuestCompleted();
        else if(message.contains("[Mini-Quest Completed]") && !message.contains(":")) toDispatch = new GameEvent.QuestCompleted();
        else if(message.contains("You are now combat level") && !message.contains(":")) toDispatch = new GameEvent.LevelUp(Minecraft.getMinecraft().player.experienceLevel-1, Minecraft.getMinecraft().player.experienceLevel);

        if(toDispatch == null) return;
        FrameworkManager.getEventBus().post(toDispatch);
    }

    @SubscribeEvent
    public void updateActionBar(ClientChatReceivedEvent event) {
        if(Reference.onServer && event.getType() == ChatType.GAME_INFO) {
            String text = event.getMessage().getUnformattedText();
            PlayerInfo.getPlayerInfo().updateActionBar(text);
            event.setMessage(new TextComponentString(""));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatEvent e) {
        if(Reference.onWorld && e.getMessage().startsWith("/class")) {
            inClassSelection = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void receiveTp(GuiScreenEvent.DrawScreenEvent.Post e) {
        if(inClassSelection) {
            PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
            inClassSelection = false;
        }
    }

    @SubscribeEvent
    public void onTabListChange(PacketEvent.TabListChangeEvent e) {
        if (!Reference.onServer) return;
        if(e.getPacket().getAction() != Action.UPDATE_DISPLAY_NAME && e.getPacket().getAction() != Action.REMOVE_PLAYER) return;

        List<String> partyMembers = new ArrayList<>();
        String partyOwner = "";

        for(SPacketPlayerListItem.AddPlayerData player : e.getPacket().getEntries()) {
            //world handling below
            if(player.getProfile().getId().equals(worldUUID)) {
                if(e.getPacket().getAction() == Action.UPDATE_DISPLAY_NAME) {
                    String name = player.getDisplayName().getUnformattedText();
                    String world = name.substring(name.indexOf("[") + 1, name.indexOf("]"));

                    if(world.equalsIgnoreCase(lastWorld)) continue;

                    Reference.setUserWorld(world);
                    FrameworkManager.getEventBus().post(new WynnWorldEvent.Join(world));
                    lastWorld = world;
                    acceptLeft = true;
                }else if (acceptLeft) {
                    acceptLeft = false;
                    lastWorld = "";
                    Reference.setUserWorld(null);
                    FrameworkManager.getEventBus().post(new WynnWorldEvent.Leave());
                    PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
                }
                continue;
            }
            if(player.getDisplayName() == null) continue;

            //party handling below
            if(player.getDisplayName().getUnformattedText().contains("/party create")) {
                PlayerInfo.getPlayerInfo().getPlayerParty().closeParty();
                continue;
            }

            String formatedName = player.getDisplayName().getFormattedText();

            if(!formatedName.contains("[") && formatedName.endsWith("§r") && !formatedName.contains("§l")) {
                if(formatedName.startsWith("§e")) partyMembers.add(Utils.stripColor(formatedName));
                else if(formatedName.startsWith("§c")) { partyOwner = Utils.stripColor(formatedName); }
            }
        }

        if(!partyOwner.isEmpty() || !partyMembers.isEmpty()) PlayerInfo.getPlayerInfo().getPlayerParty().updateParty(partyOwner, partyMembers);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleFrameworkEvents(Event e) {
        FrameworkManager.triggerEvent(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handleFrameworkPreHud(RenderGameOverlayEvent.Pre e) {
        FrameworkManager.triggerPreHud(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void handleFrameworkPostHud(RenderGameOverlayEvent.Post e) {
        FrameworkManager.triggerPostHud(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(TickEvent.ClientTickEvent e) {
        if(e.phase == TickEvent.Phase.START) return;

        ScreenRenderer.refresh();
        if(!Reference.onServer || Minecraft.getMinecraft().player == null) return;
        FrameworkManager.triggerHudTick(e);
        FrameworkManager.triggerKeyPress();
    }

}
