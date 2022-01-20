/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.helpers;

import com.wynntils.McIf;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Use this class if you want to receive a command response
 * Don't forget to call #executeCommand
 */
public class CommandResponse {

    final String command;
    final BiConsumer<Matcher, ITextComponent> whenReceive;
    final Pattern regex;

    ChatType chatType = ChatType.SYSTEM;
    boolean cancel = true;
    boolean formattedText = true;

    public CommandResponse(String command, BiConsumer<Matcher, ITextComponent> whenReceive, Pattern regex) {
        this.command = command; this.whenReceive = whenReceive; this.regex = regex;
    }

    public CommandResponse setChatType(ChatType chatType) {
        this.chatType = chatType;

        return this;
    }

    public CommandResponse setCancel(boolean cancel) {
        this.cancel = cancel;

        return this;
    }

    public CommandResponse setFormattedText(boolean formattedText) {
        this.formattedText = formattedText;

        return this;
    }

    public void executeCommand() {
        FrameworkManager.getEventBus().register(this);

        McIf.player().sendChatMessage(command);
    }

    @SubscribeEvent
    public void onMessageReceive(ClientChatReceivedEvent e) {
        if (e.getType() != chatType) return;

        String message = formattedText ? McIf.getFormattedText(e.getMessage()) : McIf.getUnformattedText(e.getMessage());
        Matcher matcher = regex.matcher(message);

        if (!matcher.find()) return;

        whenReceive.accept(matcher, e.getMessage());
        FrameworkManager.getEventBus().unregister(this);

        e.setCanceled(cancel);
    }

}
