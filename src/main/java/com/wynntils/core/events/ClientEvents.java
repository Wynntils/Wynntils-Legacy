/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.events;

import com.mojang.authlib.GameProfile;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.reflections.ReflectionMethods;
import com.wynntils.modules.core.managers.GuildAndFriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientEvents {

    private static final UUID WORLD_UUID = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");
    private static final Pattern PROF_LEVEL_UP = Pattern.compile("You are now level ([0-9]*) in (.*)");

    private boolean inClassSelection = false;
    private String lastWorld = "";
    private boolean acceptLeft = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        Reference.setUserWorld(null);

        if (Reference.onServer) MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Login());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if (Reference.onServer) {
            if (Reference.onWorld) {
                Reference.setUserWorld(null);
                MinecraftForge.EVENT_BUS.post(new WynnWorldEvent.Leave());
            }
            Reference.onServer = false;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Leave());
        }
    }

    boolean isNextQuestCompleted = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void triggerGameEvents(ClientChatReceivedEvent e) {
        if (e.getType() == ChatType.GAME_INFO) return;

        String message = e.getMessage().getUnformattedText();

        if (message.contains("\u27a4")) return;  // Whisper from a player


        GameEvent toDispatch = null;
        if (isNextQuestCompleted) {
            isNextQuestCompleted = false;

            String questName = message.trim().replace("À", "");
            if (e.getMessage().getFormattedText().contains(TextFormatting.GREEN.toString()))
                toDispatch = new GameEvent.QuestCompleted.MiniQuest(questName);
            else
                toDispatch = new GameEvent.QuestCompleted(questName);
        }

        // by message
        else if (message.startsWith("[New Quest Started:"))
            toDispatch = new GameEvent.QuestStarted(message.replace("[New Quest Started: ", "").replace("]", ""));
        else if (message.startsWith("[Mini-Quest Started:"))
            toDispatch = new GameEvent.QuestStarted.MiniQuest(message.replace("[Mini-Quest Started: ", "").replace("]", ""));
        else if (message.startsWith("[Quest Book Updated]"))
            toDispatch = new GameEvent.QuestUpdated();
        else if (message.contains("[Quest Completed]") && !message.contains(":"))
            isNextQuestCompleted = true;
        else if (message.contains("[Mini-Quest Completed]") && !message.contains(":"))
            isNextQuestCompleted = true;
        else if (message.contains("You are now combat level") && !message.contains(":"))
            toDispatch = new GameEvent.LevelUp(Minecraft.getMinecraft().player.experienceLevel - 1, Minecraft.getMinecraft().player.experienceLevel);
        else if (message.contains("[Area Discovered]") && !message.contains(":"))
            toDispatch = new GameEvent.DiscoveryFound();
        else if (message.contains(TextFormatting.AQUA.toString()) && message.contains("[Discovery Found]") && !message.contains(":"))
            toDispatch = new GameEvent.DiscoveryFound.Secret();
        else if (message.contains(TextFormatting.GOLD.toString()) && message.contains("[Area Discovered]") && !message.contains(":"))
            toDispatch = new GameEvent.DiscoveryFound.World();

        //using regex
        Matcher m = PROF_LEVEL_UP.matcher(message);
        if (m.find()) {
            int currentLevel = Integer.parseInt(m.group(1));
            toDispatch = new GameEvent.LevelUp.Profession(ProfessionType.fromMessage(m.group(2)), currentLevel - 1, currentLevel);
        }

        if (toDispatch == null) return;
        FrameworkManager.getEventBus().post(toDispatch);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatEvent e) {
        if (Reference.onWorld && e.getMessage().startsWith("/class")) {
            inClassSelection = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void receiveTp(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (inClassSelection) {
            PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
            inClassSelection = false;
        }
    }

    @SubscribeEvent
    public void onTabListChange(PacketEvent<SPacketPlayerListItem> e) {
        if (!Reference.onServer) return;
        if (e.getPacket().getAction() != Action.UPDATE_DISPLAY_NAME && e.getPacket().getAction() != Action.REMOVE_PLAYER) return;

        for (Object player : (List<?>) e.getPacket().getEntries()) {
            // world handling below
            GameProfile profile = (GameProfile) ReflectionMethods.SPacketPlayerListItem$AddPlayerData_getProfile.invoke(player);
            if (profile.getId().equals(WORLD_UUID)) {
                if (e.getPacket().getAction() == Action.UPDATE_DISPLAY_NAME) {
                    ITextComponent nameComponent = (ITextComponent) ReflectionMethods.SPacketPlayerListItem$AddPlayerData_getDisplayName.invoke(player);
                    if (nameComponent == null) continue;
                    String name = nameComponent.getUnformattedText();
                    String world = name.substring(name.indexOf("[") + 1, name.indexOf("]"));

                    if (world.equalsIgnoreCase(lastWorld)) continue;

                    Reference.setUserWorld(world);
                    FrameworkManager.getEventBus().post(new WynnWorldEvent.Join(world));
                    lastWorld = world;
                    acceptLeft = true;
                } else if (acceptLeft) {
                    acceptLeft = false;
                    lastWorld = "";
                    Reference.setUserWorld(null);
                    FrameworkManager.getEventBus().post(new WynnWorldEvent.Leave());
                    PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
                }
            }
            // Add uuid of newly joined player
            GuildAndFriendManager.tryResolveName(profile.getId(), profile.getName());
        }
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
        if (e.phase != TickEvent.Phase.END) return;

        ScreenRenderer.refresh();
        if (!Reference.onServer || Minecraft.getMinecraft().player == null) return;
        FrameworkManager.triggerHudTick(e);
        FrameworkManager.triggerKeyPress();
    }

    @SubscribeEvent
    public void onWorldLeave(GuiOpenEvent e) {
        if (Reference.onServer && e.getGui() instanceof GuiDisconnected) {
            if (Reference.onWorld) {
                Reference.setUserWorld(null);
                MinecraftForge.EVENT_BUS.post(new WynnWorldEvent.Leave());
            }
            Reference.onServer = false;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Leave());
        }
    }

}
