package cf.wynntils.core.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.events.custom.WynnWorldLeftEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ClientEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.contains("wynncraft")) {
            Reference.onServer = true;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Login());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if(Reference.onServer) {
            Reference.onServer = false;
            MinecraftForge.EVENT_BUS.post(new WynncraftServerEvent.Leave());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void updateActionBar(ClientChatReceivedEvent event) {
        if(Reference.onServer && event.getType() == 2) {
            String text = event.getMessage().getUnformattedText();
            PlayerInfo.getPlayerInfo().updateActionBar(text);
            event.setMessage(new TextComponentString(""));
        }
    }

    boolean inClassSelection = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onChat(ClientChatEvent e) {
        if(Reference.onWorld && e.getMessage().startsWith("/class")) {
            inClassSelection = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void receiveTp(GuiScreenEvent.DrawScreenEvent.Post e) {
        if(inClassSelection) {
            PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
            MinecraftForge.EVENT_BUS.post(new WynnClassChangeEvent(PlayerInfo.getPlayerInfo().getCurrentClass(), ClassType.NONE));
            inClassSelection = false;
            acceptClass = true;
        }
    }

    private static String lastWorld = "";
    private boolean acceptsLeft = false;
    private static boolean acceptClass = true;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldJoin(EntityJoinWorldEvent e) {
        if(!Reference.onServer) return;


        Collection<NetworkPlayerInfo> tab = Objects.requireNonNull(ModCore.mc().getConnection()).getPlayerInfoMap();
        String world = null;
        for(NetworkPlayerInfo pl : tab) {
            String name = ModCore.mc().ingameGUI.getTabList().getPlayerName(pl);
            if(name.contains("Global") && name.contains("[") && name.contains("]")) {
                world = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
                break;
            }
        }

        Reference.setUserWorld(world);

        if(world == null && acceptsLeft) {
            acceptsLeft = false;
            MinecraftForge.EVENT_BUS.post(new WynnWorldLeftEvent());
            PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.NONE);
            MinecraftForge.EVENT_BUS.post(new WynnClassChangeEvent(PlayerInfo.getPlayerInfo().getCurrentClass(), ClassType.NONE));
        }else if(world != null && !acceptsLeft && !lastWorld.equalsIgnoreCase(world)) {
            acceptsLeft = true;
            acceptClass = true;
            MinecraftForge.EVENT_BUS.post(new WynnWorldJoinEvent(world));
        }

        lastWorld = world == null ? "" : world;

        Minecraft mc = Minecraft.getMinecraft();
        if(acceptClass) {
            if (mc.player.experienceLevel > 0) {
                try {
                    ItemStack book = mc.player.inventory.getStackInSlot(7);
                    if (book.hasDisplayName() && book.getDisplayName().contains("Quest Book")) {
                        for (int i=0;i<36;i++) {
                            try {
                                ItemStack ItemTest = mc.player.inventory.getStackInSlot(i);
                                NBTTagList Lore = ItemTest.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                                for (int j = 1; j < Lore.tagCount(); j++) {
                                    String ClassTest = Lore.get(j).toString();
                                    if (ClassTest.contains("Class Req:") && ClassTest.charAt(2) == 'a'){
                                        ClassType newClass = ClassType.valueOf(ClassTest.substring(18,ClassTest.lastIndexOf('/')).toUpperCase());

                                        PlayerInfo.getPlayerInfo().updatePlayerClass(newClass);
                                        MinecraftForge.EVENT_BUS.post(new WynnClassChangeEvent(PlayerInfo.getPlayerInfo().getCurrentClass(), newClass));

                                        acceptClass = false;
                                    }
                                }
                            }
                            catch (Exception ignored){
                            }
                        }
                    }
                }
                catch (Exception ignored) {
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SideOnly(Side.CLIENT)
    public void handleFrameworkEvents(Event e) {
        FrameworkManager.triggerEvent(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void handleFrameworkPreHud(RenderGameOverlayEvent.Pre e) {
        FrameworkManager.triggerPreHud(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void handleFrameworkPostHud(RenderGameOverlayEvent.Post e) {
        FrameworkManager.triggerPostHud(e);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent e) {
        ScreenRenderer.refresh();
        if(!Reference.onServer || Minecraft.getMinecraft().player == null) return;
        FrameworkManager.triggerHudTick(e);
        FrameworkManager.triggerKeyPress();
    }

}
