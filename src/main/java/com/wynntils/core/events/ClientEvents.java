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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class ClientEvents {

    private static final UUID worldUUID = UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af");
    private boolean inClassSelection = false;
    private String lastWorld = "";
    private boolean acceptLeft = false;
    private static boolean guisClosed = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.toLowerCase(Locale.ROOT).contains("wynncraft")) {
            Reference.setUserWorld(null);
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Login());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if(Reference.onServer) {
            if (Reference.onWorld) {
                Reference.setUserWorld(null);
                MinecraftForge.EVENT_BUS.post(new WynnWorldEvent.Leave());
            }
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
    public void onTabListChange(PacketEvent<SPacketPlayerListItem> e) {
        if (!Reference.onServer) return;
        if(e.getPacket().getAction() != Action.UPDATE_DISPLAY_NAME && e.getPacket().getAction() != Action.REMOVE_PLAYER) return;

        for(SPacketPlayerListItem.AddPlayerData player : e.getPacket().getEntries()) {
            //world handling below
            if(player.getProfile().getId().equals(worldUUID)) {
                if(e.getPacket().getAction() == Action.UPDATE_DISPLAY_NAME) {
                    ITextComponent nameComponent = player.getDisplayName();
                    if (nameComponent == null) continue;
                    String name = nameComponent.getUnformattedText();
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
            }
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
        if(e.phase != TickEvent.Phase.END) return;

        if (Reference.onWorld && guisClosed && ModCore.mc().currentScreen == null) {
            Keyboard.enableRepeatEvents(true);
        }
        guisClosed = false;

        ScreenRenderer.refresh();
        if (!Reference.onServer || Minecraft.getMinecraft().player == null) return;
        FrameworkManager.triggerHudTick(e);
        FrameworkManager.triggerKeyPress();
    }

    @SubscribeEvent
    public void onWorldLeave(GuiOpenEvent e) {
        if (e.getGui() instanceof GuiDisconnected && Reference.onServer) {
            if (Reference.onWorld) {
                Reference.setUserWorld(null);
                MinecraftForge.EVENT_BUS.post(new WynnWorldEvent.Leave());
            }
            Reference.onServer = false;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Leave());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiClosed(GuiOpenEvent e) {
        if (Reference.onWorld) {
            // Enable repeat events when no GUIs are open
            boolean closingGui = e.getGui() == null;
            boolean openingGui = ModCore.mc().currentScreen == null;
            if (closingGui && !openingGui) {
                // Closing all GUIs
                guisClosed = true;
            } else if (openingGui && !closingGui) {
                // Opening first GUI
                Keyboard.enableRepeatEvents(false);
            }
        }
    }

}
