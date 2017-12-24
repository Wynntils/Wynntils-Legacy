package com.wynndevs.modules.expansion.misc;

import com.mojang.authlib.GameProfile;
import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.overrides.EntityPlayerEXP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketChat;

public class NetworkFilter {

    private Minecraft mc;

    public NetworkFilter(Minecraft mc) {
        this.mc = mc;
    }

    public static class NetworkPlayFilter extends NetHandlerPlayClient {
        private final NetHandlerPlayClient original;

        public NetworkPlayFilter(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn,
                                 GameProfile profileIn, NetHandlerPlayClient original) {
            super(mcIn, p_i46300_2_, networkManagerIn, profileIn);
            this.original = original;

            Reference.copyClassDeep(NetworkPlayFilter.class, original, this);
        }

        @Override
        public void handleChat(SPacketChat packetIn) {
            if (packetIn.getType() == 2) {
                EntityPlayerEXP player = (EntityPlayerEXP) ModCore.mc().player;
                if (player != null) {
                    player.sendStatusMessage(packetIn.getChatComponent());
                    return;
                }
            }

            super.handleChat(packetIn);
        }
    }
}
