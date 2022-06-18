/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.instances.containers;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.WynnSocialEvent;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Contains all the player currently party status
 * also can be called if the player is not on a party
 */
public class PartyContainer {

    Set<String> partyMembers = new HashSet<>();
    String owner = "";

    /**
     * Check if the player is at a party
     *
     * @return if the player is at a party
     */
    public boolean isPartying() {
        return !partyMembers.isEmpty();
    }

    /**
     * Add a member to the party list
     *
     * @param userName the member userName
     */
    public void addMember(String userName) {
        partyMembers.add(userName);

        FrameworkManager.getEventBus().post(new WynnSocialEvent.Party.Join(userName, partyMembers.size() == 1));
    }

    /**
     * Add multiple members to the party
     *
     * @param members the list with all the members that you want to add
     */
    public void addMembers(List<String> members) {
        partyMembers.addAll(members);

        members.forEach(userName -> FrameworkManager.getEventBus().post(new WynnSocialEvent.Party.Join(userName, partyMembers.size() == 1)));
    }

    /**
     * Remove a member from the party list
     *
     * @param userName  the member userName
     */
    public void removeMember(String userName) {
        FrameworkManager.getEventBus().post(new WynnSocialEvent.Party.Leave(userName));

        if (userName.equalsIgnoreCase(McIf.player().getName())) {
            partyMembers.clear();
            owner = "";
            return;
        }

        partyMembers.remove(userName);
    }

    /**
     * Remove multiple members from the party list
     *
     * @param members the list with all the members that you want to remove
     */
    public void removeMembers(List<String> members) {
        members.forEach(userName -> FrameworkManager.getEventBus().post(new WynnSocialEvent.Party.Leave(userName)));

        if (members.contains(McIf.player().getName())) {
            partyMembers.clear();
            owner = "";
            return;
        }

        partyMembers.removeAll(members);
    }

    /**
     * Set the party owner.
     * This does not add the owner to the partyMembers list, you must also call {@link #addMember(String username)}
     * @param owner party owner username
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets all the party members
     *
     * @return the party members list
     */
    public Set<String> getPartyMembers() {
        return partyMembers;
    }

    /**
     * Gets the party owner
     *
     * @return the party owner
     */
    public String getOwner() {
        return owner;
    }

}
