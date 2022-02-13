/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.chat.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.managers.ChatManager;
import com.wynntils.modules.chat.managers.HeldItemChatManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.modules.core.managers.PacketQueue;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.events.custom.QuestBookUpdateEvent;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        if (e.getGui() instanceof GuiChat) {
            if (e.getGui() instanceof ChatGUI) return;
            String defaultText = ReflectionFields.GuiChat_defaultInputFieldText.getValue(e.getGui());

            e.setGui(new ChatGUI(defaultText));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatReceived(ClientChatReceivedEvent e) {
        ITextComponent msg = e.getMessage();
        if (ChatConfig.INSTANCE.filterWynncraftInfo && McIf.getUnformattedText(msg).startsWith("[Info] ")) {
            e.setCanceled(true);
        } else if (ChatConfig.INSTANCE.filterJoinMessages && McIf.getFormattedText(msg).startsWith("\n                       " + TextFormatting.GOLD + TextFormatting.BOLD + "Welcome to Wynncraft!") &&
                !McIf.getFormattedText(msg).contains("n the Trade Market")) {
            e.setCanceled(true);
        } else if (ChatConfig.INSTANCE.filterTerritoryEnter && McIf.getFormattedText(msg).startsWith(TextFormatting.GRAY + "[You are now entering")) {
            e.setCanceled(true);
        } else if (ChatConfig.INSTANCE.filterTerritoryEnter && McIf.getFormattedText(msg).startsWith(TextFormatting.GRAY + "[You are now leaving")) {
            e.setCanceled(true);
        } else if (ChatConfig.INSTANCE.filterPartyFinder && McIf.getFormattedText(msg).startsWith(TextFormatting.DARK_PURPLE + "Party Finder: Hey ")) {
            e.setCanceled(true);
        } else if (ChatConfig.INSTANCE.filterEventMessages && McIf.getFormattedText(msg).startsWith(TextFormatting.GOLD + "[Event]")) {
            e.setCanceled(true);
        }
    }

    /**
     * Used for replacing commands by others, also knows as, creating aliases
     *
     * Replacements:
     * /tell -> /msg
     * /xp -> /guild xp
     *
     * /guild att/a -> attack
     *        def/d -> defend
     *        c -> contribute
     * /party j -> join
     *        i -> invite
     *        l -> leave
     *        c -> create
     */
    @SubscribeEvent
    public void commandReplacements(ClientChatEvent e) {
        if (e.getMessage().startsWith("/tell")) e.setMessage(e.getMessage().replaceFirst("/tell", "/msg"));
        else if (e.getMessage().startsWith("/xp")) e.setMessage(e.getMessage().replaceFirst("/xp", "/guild xp"));
        else if (e.getMessage().startsWith("/gu")) e.setMessage(e.getMessage().replaceFirst(" att$", " attack").replaceFirst(" a$", " attack").replaceFirst(" def$", " defend").replaceFirst(" d$", " defend").replaceFirst(" c$", " contribute"));
        else if (e.getMessage().startsWith("/pa")) e.setMessage(e.getMessage().replaceFirst(" j ", " join ").replaceFirst(" i ", " invite ").replaceFirst(" l$", " leave").replaceFirst(" c$", " create"));
    }


    @SubscribeEvent
    public void onWynnLogin(WynncraftServerEvent.Login e) {
        ReflectionFields.GuiIngame_persistantChatGUI.setValue(McIf.mc().ingameGUI, new ChatOverlay());
        TranslationManager.init();
    }

    @SubscribeEvent
    public void onWynnLogout(WynncraftServerEvent.Leave e) {
        TranslationManager.shutdown();
    }

    @SubscribeEvent
    public void onSendMessage(ClientChatEvent e) {
        if (e.getMessage().startsWith("/")) return;

        Pair<String, Boolean> message = ChatManager.applyUpdatesToServer(e.getMessage());
        e.setMessage(message.a);
        if (message.b || message.a.isEmpty() || message.a.trim().isEmpty()) {
            e.setCanceled(true);
            return;
        }

        if (!ChatOverlay.getChat().getCurrentTab().getAutoCommand().isEmpty())
            e.setMessage(ChatOverlay.getChat().getCurrentTab().getAutoCommand() + " " + e.getMessage());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld) return;

        HeldItemChatManager.onTick();
    }

    @SubscribeEvent
    public void onLeaveWorld(WynnWorldEvent.Leave e) {
        ChatManager.onLeave();
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        ChatManager.setDiscoveriesLoaded(false);
    }

    @SubscribeEvent
    public void onAnalyzeDiscoveries(QuestBookUpdateEvent.Partial e) {
        if (e.getAnalysed() == AnalysePosition.SECRET_DISCOVERIES && !ChatManager.getDiscoveriesLoaded()) {
            ChatManager.setDiscoveriesLoaded(true);
            ChatOverlay.getChat().processQueues();
        }
    }

    @SubscribeEvent
    public void onClickNPC(PacketEvent<CPacketUseEntity> e) {
        if (!ChatConfig.INSTANCE.rightClickDialogue) return;
        if (e.getPacket().getAction() != CPacketUseEntity.Action.INTERACT) return; // only right click
        if (!ChatManager.inDialogue) return;

        EntityPlayerSP player = McIf.player();
        Entity clicked = e.getPacket().getEntityFromWorld(player.world);

        if (clicked.getTeam() != null) return; // entity is another player
        String name = clicked.getName();
        if (!name.contains("NPC") && !name.contains("\u0001")) return; // (probably) not an NPC

        ChatManager.progressDialogue();
    }

}
