/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.events.custom;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.framework.instances.data.SocialData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Collection;
import java.util.Set;

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
        return member.equalsIgnoreCase(McIf.player().getName());
    }

    public static class Party extends WynnSocialEvent {

        public Party(String member) {
            super(member);
        }

        public PartyContainer getParty() {
            return PlayerInfo.get(SocialData.class).getPlayerParty();
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

        public Set<String> getGuildList() {
            return PlayerInfo.get(SocialData.class).getGuildMembersList();
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

    public static class FriendList extends Event {
        Collection<String> members;
        public final boolean isSingular;

        public FriendList(Collection<String> members, boolean isSingular) {
            this.members = members;
            this.isSingular = isSingular;
        }

        public Collection<String> getMembers() {
            return members;
        }

        /**
         * Called when multiple friends are "added" (discovered) when friends list is read with `members`,
         * and when /friend add is run, with a collection of names.
         */
        public static class Add extends FriendList {
            public Add(Collection<String> members, boolean isSingular) {
                super(members, isSingular);
            }
        }

        /**
         * As {@link Add}, but when friend(s) is removed.
         */
        public static class Remove extends FriendList {
            public Remove(Collection<String> members, boolean isSingular) {
                super(members, isSingular);
            }
        }
    }

}
