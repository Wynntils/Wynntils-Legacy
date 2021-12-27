package com.wynntils.core.events.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import com.wynntils.modules.utilities.managers.KeyManager;

public class KeyInputEventHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyManager.GuildMenuKeyBind.isPressed()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            ServerData server = minecraft.getCurrentServerData();

            if (server != null && server.serverIP.toLowerCase().contains("wynncraft")) {
                EntityPlayerSP playerSP = minecraft.player;
                int lastItem = playerSP.inventory.currentItem;
                playerSP.inventory.currentItem = 6;

                minecraft.playerController.processRightClick(playerSP, playerSP.world, EnumHand.MAIN_HAND);
                
                playerSP.inventory.currentItem = lastItem;
                OnGuiOpenEvent.CompassOpened = true;
            }
        }
    }
}
