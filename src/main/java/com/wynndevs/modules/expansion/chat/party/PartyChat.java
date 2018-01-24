package com.wynndevs.modules.expansion.chat.party;

import net.minecraft.client.Minecraft;

public class PartyChat {

    private Minecraft mc;

    private GuiParty party;

    private GuiPartyChat chat;

    private GuiPartyChatbox chatbox;

    public PartyChat(Minecraft mc, GuiParty party) {
        this.mc = mc;
        this.party = party;
        this.party.setChat(this);
        this.chat = new GuiPartyChat(mc, this);
        this.chatbox = new GuiPartyChatbox(this);
    }

    public GuiParty getparty() {
        return this.party;
    }

    public GuiPartyChat getChat() {
        return this.chat;
    }

    public GuiPartyChatbox getChatBox() {
        return this.chatbox;
    }

    public boolean chatOpen() {
        return this.mc.currentScreen == this.chatbox;
    }

    public boolean chatClosed() {
        return !chatOpen();
    }

    public void openChat(){
        if (this.mc.currentScreen == this.chatbox) return;
        this.mc.displayGuiScreen(this.chatbox);
    }

    public boolean closeChat() {
        if (this.mc.currentScreen != this.chatbox) return false;
        this.mc.displayGuiScreen(null);
        return true;
    }

}
