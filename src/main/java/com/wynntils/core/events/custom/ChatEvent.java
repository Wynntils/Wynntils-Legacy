/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatEvent extends Event {

    ITextComponent message;

    public ChatEvent(ITextComponent message) {
        this.message = message;
    }

    public ITextComponent getMessage() {
        return message;
    }

    public static class Pre extends ChatEvent {

        public Pre(ITextComponent message) {
            super(message);
        }

        public boolean isCancelable() {
            return true;
        }

    }

    public static class Pos extends ChatEvent {

        public Pos(ITextComponent message) {
            super(message);
        }

        public boolean isCancelable() {
            return true;
        }

    }

}
