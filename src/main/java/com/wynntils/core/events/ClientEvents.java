/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.events;

import com.mojang.authlib.GameProfile;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.framework.instances.GuiTickableScreen;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.core.utils.reflections.ReflectionMethods;
import com.wynntils.modules.core.managers.GuildAndFriendManager;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.item.ItemStack;
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
    private static final Pattern SPELL_CAST = Pattern.compile("^§7(.*) spell cast!§3 \\[§b-([0-9]+) ✺§3\\]$");

    private static String heldItem = "";
    private String lastWorld = "";
    private boolean acceptLeft = false;
    public static String statusMsg;

    public static void setLoadingStatusMsg(String msg) {
        statusMsg = msg;
    }

    @SubscribeEvent
    public void onConnectScreen(GuiOpenEvent e) {
        if (!(e.getGui() instanceof GuiConnecting)) return;

        setLoadingStatusMsg("Trying to connect...");
    }

    @SubscribeEvent
    public void onScreenDraw(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!(e.getGui() instanceof GuiConnecting)) return;

        e.getGui().drawCenteredString(McIf.mc().fontRenderer, statusMsg, e.getGui().width / 2, e.getGui().height / 2 - 20, 16777215);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        setLoadingStatusMsg("Connected...");
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

        String message = McIf.getUnformattedText(e.getMessage());

        if (message.contains("\u27a4")) return;  // Whisper from a player


        GameEvent toDispatch = null;
        if (isNextQuestCompleted) {
            isNextQuestCompleted = false;

            String questName = message.trim().replace("À", "");
            if (McIf.getFormattedText(e.getMessage()).contains(TextFormatting.GREEN.toString()))
                toDispatch = new GameEvent.QuestCompleted.MiniQuest(questName);
            else
                toDispatch = new GameEvent.QuestCompleted(questName);
        }

        // by message
        else if (message.equals("You have died...") && e.getType()==ChatType.SYSTEM)
            toDispatch = new GameEvent.PlayerDeath();
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
            toDispatch = new GameEvent.LevelUp(Integer.parseInt(message.substring(message.indexOf("level")+6)));
        else if (message.contains("Area Discovered")) {
            for (ITextComponent part : e.getMessage()) {
                if (part.getStyle().getColor() == TextFormatting.GRAY) {
                    toDispatch = new GameEvent.DiscoveryFound();
                } else if (part.getStyle().getColor() == TextFormatting.GOLD) {
                    toDispatch = new GameEvent.DiscoveryFound.World();
                }
            }
        } else if (message.contains("[Discovery Found]") && !message.contains(":") || message.contains("Secret Discovery"))
            toDispatch = new GameEvent.DiscoveryFound.Secret();

        //using regex
        Matcher m = PROF_LEVEL_UP.matcher(message);
        if (m.find()) {
            int currentLevel = Integer.parseInt(m.group(1));
            toDispatch = new GameEvent.LevelUp.Profession(ProfessionType.fromMessage(m.group(2)), currentLevel);
        }

        if (toDispatch == null) return;
        FrameworkManager.getEventBus().post(toDispatch);
    }

    // class selection stuff
    boolean loadingClassSelection = false;

    // this is not triggered if autojoin is disabled!
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void detectClassCommand(ClientChatEvent e) {
        if (!Reference.onWorld || !e.getMessage().startsWith("/class")) return;

        loadingClassSelection = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void openClassSelection(GuiScreenEvent.DrawScreenEvent.Post e) {
        if (!loadingClassSelection) return;

        // updates the user class to NONE since it's not using a class anymore
        PlayerInfo.get(CharacterData.class).updatePlayerClass(ClassType.NONE, false);
        loadingClassSelection = false;
    }

    //
    @SubscribeEvent
    public void onTabListChange(PacketEvent<SPacketPlayerListItem> e) {
        if (!Reference.onServer) return;
        if (e.getPacket().getAction() != Action.UPDATE_DISPLAY_NAME && e.getPacket().getAction() != Action.REMOVE_PLAYER) return;

        // DO NOT remove cast or reflection otherwise the build will fail
        for (Object player : (List<?>) e.getPacket().getEntries()) {
            // world handling below
            GameProfile profile = (GameProfile) ReflectionMethods.SPacketPlayerListItem$AddPlayerData_getProfile.invoke(player);
            if (profile.getId().equals(WORLD_UUID)) {
                if (e.getPacket().getAction() == Action.UPDATE_DISPLAY_NAME) {
                    ITextComponent nameComponent = (ITextComponent) ReflectionMethods.SPacketPlayerListItem$AddPlayerData_getDisplayName.invoke(player);
                    if (nameComponent == null) continue;
                    String name = McIf.getUnformattedText(nameComponent);
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
                    PlayerInfo.get(CharacterData.class).updatePlayerClass(ClassType.NONE, false);
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
        if (!Reference.onServer || McIf.player() == null) return;

        FrameworkManager.triggerHudTick(e);
        FrameworkManager.triggerKeyPress();
        FrameworkManager.triggerNaturalSpawn();

        if (McIf.mc().currentScreen == null) return;

        // Tick gui screens that requires ticks
        GuiScreen screen = McIf.mc().currentScreen;
        if (!(screen instanceof GuiTickableScreen)) return;
        ((GuiTickableScreen) screen).onTick();
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

    @SubscribeEvent
    public void checkSpellCast(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld) return;

        int remainingHighlightTicks = ReflectionFields.GuiIngame_remainingHighlightTicks.getValue(McIf.mc().ingameGUI);
        ItemStack highlightingItemStack = ReflectionFields.GuiIngame_highlightingItemStack.getValue(McIf.mc().ingameGUI);

        if (remainingHighlightTicks == 0 || highlightingItemStack.isEmpty()) {
            heldItem = "";
        } else {
            String newHeldItem = highlightingItemStack.getDisplayName();
            if (!heldItem.equals(newHeldItem)) {
                heldItem = newHeldItem;
                Matcher m = SPELL_CAST.matcher(heldItem);
                if (m.find()) {
                    String spellName = m.group(1);
                    int manaCost = Integer.parseInt(m.group(2));

                    FrameworkManager.getEventBus().post(new SpellEvent.Cast(spellName, manaCost));
                }
            }
        }
    }

}
