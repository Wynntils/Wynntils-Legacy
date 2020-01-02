/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Triggered for Wynncraft Social Events
 * That includes: Party handling, Guild handling
 */
public class WynnSocialEvent extends Event {

    String member;

    public WynnSocialEvent(String member) {
        this.member = member;
    }

    public String getMember() {
        return member;
    }

    public boolean isYou() {
        return member.equalsIgnoreCase(Minecraft.getMinecraft().player.getName());
    }

    /**
     * Triggered when one player leaves the Party
     * (including yourself)
     */
    public static class PartyLeave extends WynnSocialEvent {

        public PartyLeave(String member) {
            super(member);
        }

        public boolean isYou() {
            return member.equalsIgnoreCase(Minecraft.getMinecraft().player.getName());
        }

        public PartyContainer getParty() {
            return PlayerInfo.getPlayerInfo().getPlayerParty();
        }

    }

    /**
     * Triggered when one player joins the Party
     * (including yourself)
     */
    public static class PartyJoin extends WynnSocialEvent {

        boolean createdNow;

        public PartyJoin(String member, boolean createdNow) {
            super(member);

            this.createdNow = createdNow;
        }

        public boolean wasCreatedNow() {
            return createdNow;
        }

        public PartyContainer getParty() {
            return PlayerInfo.getPlayerInfo().getPlayerParty();
        }

    }

}
