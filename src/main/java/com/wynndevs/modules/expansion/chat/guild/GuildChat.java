package com.wynndevs.modules.expansion.chat.guild;

import net.minecraft.client.Minecraft;

public class GuildChat {

    private Minecraft mc;

    private GuiGuild guild;

    private GuiGuildChat chat;

    private GuiGuildChatbox chatbox;

    public GuildChat(Minecraft mc, GuiGuild guild) {
        this.mc = mc;
        this.guild = guild;
        this.guild.setChat(this);
        this.chat = new GuiGuildChat(mc, this);
        this.chatbox = new GuiGuildChatbox(this);
    }

    public GuiGuild getGuild() {
        return this.guild;
    }

    public GuiGuildChat getChat() {
        return this.chat;
    }

    public GuiGuildChatbox getChatBox() {
        return this.chatbox;
    }

    public boolean chatOpen() {
        return this.mc.currentScreen == this.chatbox;
    }

    public boolean chatClosed() {
        return !chatOpen();
    }

    public boolean openChat() {
        if (this.mc.currentScreen == this.chatbox) return false;
        this.mc.displayGuiScreen(this.chatbox);
        return true;
    }

    public boolean closeChat() {
        if (this.mc.currentScreen != this.chatbox) return false;
        this.mc.displayGuiScreen(null);
        return true;
    }

}
