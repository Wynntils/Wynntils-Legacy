/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.managers.ChatManager;
import com.wynntils.modules.chat.managers.HeldItemChatManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
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
            String defaultText = (String) ReflectionFields.GuiChat_defaultInputFieldText.getValue(e.getGui());

            e.setGui(new ChatGUI(defaultText));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatRecieved(ClientChatReceivedEvent e) {
        if (e.getMessage().getUnformattedText().startsWith("[Info] ") && ChatConfig.INSTANCE.filterWynncraftInfo) {
            e.setCanceled(true);
        } else if (e.getMessage().getFormattedText().startsWith(TextFormatting.GRAY + "[You are now entering") && ChatConfig.INSTANCE.filterTerritoryEnter) {
            e.setCanceled(true);
        }
    }

    /**
     * Used for replacing commands by others, also knows as, creating aliases
     *
     * Replacements:
     * /tell -> /msg
     * /xp -> /guild xp
     */
    @SubscribeEvent
    public void commandReplacements(ClientChatEvent e) {
        if (e.getMessage().startsWith("/tell")) e.setMessage(e.getMessage().replaceFirst("/tell", "/msg"));
        else if (e.getMessage().startsWith("/xp")) e.setMessage(e.getMessage().replaceFirst("/xp", "/guild xp"));
    }


    @SubscribeEvent
    public void onWynnLogin(WynncraftServerEvent.Login e) {
        ReflectionFields.GuiIngame_persistantChatGUI.setValue(Minecraft.getMinecraft().ingameGUI, new ChatOverlay());
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
        if (message.b || message.a.isEmpty() || message.a.equalsIgnoreCase(" ")) {
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

}
