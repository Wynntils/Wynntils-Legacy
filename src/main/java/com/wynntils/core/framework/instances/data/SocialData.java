/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import java.util.HashSet;
import java.util.Set;

import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.framework.instances.containers.PlayerData;

public class SocialData extends PlayerData {

    private Set<String> friendList = new HashSet<>();
    private Set<String> guildMembersList = new HashSet<>();
    private final PartyContainer playerParty = new PartyContainer();

    public SocialData() { }

    public boolean addFriend(String name) {
       return friendList.add(name);
    }

    public boolean removeFriend(String name) {
        return friendList.remove(name);
    }

    public boolean addGuildMember(String name) {
        return guildMembersList.add(name);
    }

    public boolean removeGuildMember(String name) {
        return guildMembersList.remove(name);
    }

    public boolean isFriend(String name) {
        return friendList.contains(name);
    }

    public boolean isGuildMember(String name) {
        return guildMembersList.contains(name);
    }

    public void setFriendList(Set<String> friends) {
        friendList = friends;
    }

    public void setGuildMembersList(Set<String> members) {
        guildMembersList = members;
    }

    public PartyContainer getPlayerParty() {
        return playerParty;
    }

    public Set<String> getFriendList() {
        return friendList;
    }

    public Set<String> getGuildMembersList() {
        return guildMembersList;
    }

}
