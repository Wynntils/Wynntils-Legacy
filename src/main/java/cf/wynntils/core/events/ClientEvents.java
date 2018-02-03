package cf.wynntils.core.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if(!ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData() != null && Objects.requireNonNull(ModCore.mc().getCurrentServerData()).serverIP.contains("wynncraft")) {
            Reference.onServer = true;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        if(Reference.onServer()) {
            Reference.onServer = false;
        }
    }

    private static String lastWorld = "";
    private boolean acceptsLeft = false;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldJoin(EntityJoinWorldEvent e) {
        if(!Reference.onServer()) {
            return;
        }

        Collection<NetworkPlayerInfo> tab = Objects.requireNonNull(ModCore.mc().getConnection()).getPlayerInfoMap();
        String world = null;
        for(NetworkPlayerInfo pl : tab) {
            String name = ModCore.mc().ingameGUI.getTabList().getPlayerName(pl);
            if(name.contains("Global") && name.contains("[") && name.contains("]")) {
                world = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
                break;
            }
        }

        Reference.userWorld = world;

        if(world == null && acceptsLeft) {
            acceptsLeft = false;
            //onWorldLeft.forEach(Runnable::run);
        }else if(world != null && !acceptsLeft && !lastWorld.equalsIgnoreCase(world)) {
            acceptsLeft = true;
            //onWorldJoin.forEach(Runnable::run);
        }

        lastWorld = world == null ? "" : world;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
        FrameworkManager.triggerKeyPress();
    }

}
