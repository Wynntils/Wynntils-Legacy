/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.events.custom;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatEvent extends Event {

    protected ITextComponent message;
    protected int chatLineId;

    protected ChatEvent(ITextComponent message, int chatLineId) {
        this.message = message;
        this.chatLineId = chatLineId;
    }

    public ITextComponent getMessage() {
        return message;
    }

    public int getChatLineId() {
        return chatLineId;
    }

    public static class Pre extends ChatEvent {

        private final boolean isDialogue;

        public Pre(ITextComponent message, int chatLineId, boolean isDialogue) {
            super(message, chatLineId);
            this.isDialogue = isDialogue;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        public void setMessage(ITextComponent newMessage) {
            this.message = newMessage;
        }

        public void setChatLineId(int newChatLineId) {
            this.chatLineId = newChatLineId;
        }

        public boolean isDialogue() {
            return isDialogue;
        }
    }

    public static class Post extends ChatEvent {

        public Post(ITextComponent message, int chatLineId) {
            super(message, chatLineId);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }

    }

}
