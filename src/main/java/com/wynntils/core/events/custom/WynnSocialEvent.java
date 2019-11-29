/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashSet;

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

    public static class Party extends WynnSocialEvent {

        public Party(String member) {
            super(member);
        }

        public PartyContainer getParty() {
            return PlayerInfo.getPlayerInfo().getPlayerParty();
        }

        /**
         * Triggered when one player leaves the Party
         * (including yourself)
         */
        public static class Leave extends Party {

            public Leave(String member) {
                super(member);
            }

        }

        /**
         * Triggered when one player joins the Party
         * (including yourself)
         */
        public static class Join extends Party {

            private boolean createdNow;

            public Join(String member, boolean createdNow) {
                super(member);

                this.createdNow = createdNow;
            }

            public boolean wasCreatedNow() {
                return createdNow;
            }

        }

    }

    public static class Guild extends WynnSocialEvent {

        public Guild(String member) {
            super(member);
        }

        public HashSet<String> getGuildList() {
            return PlayerInfo.getPlayerInfo().getGuildList();
        }

        /** (Not used yet)
         * Triggered when one player leaves the guild
         * (including yourself)
         */
        public static class Leave extends Guild {

            public Leave(String member) {
                super(member);
            }

        }

        /** (Not used yet)
         * Triggered when one player joins the guild
         * (including yourself)
         */
        public static class Join extends Guild {

            public Join(String member) {
                super(member);
            }

        }

    }


    public static class FriendList extends WynnSocialEvent {
        public FriendList(String member) {
            super(member);
        }

        public static class Add extends FriendList {
            public Add(String member) {
                super(member);
            }
        }

        public static class Remove extends FriendList {
            public Remove(String member) {
                super(member);
            }
        }
    }

}
