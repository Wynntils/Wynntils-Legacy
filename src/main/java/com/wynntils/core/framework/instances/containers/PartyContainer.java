/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.instances.containers;

import com.wynntils.core.events.custom.WynnSocialEvent;
import com.wynntils.core.framework.FrameworkManager;

import java.util.HashSet;
import java.util.List;

/**
 * Contains all the player currently party status
 * also can be called if the player is not on a party
 *
 * BE AWARE THAT THIS THING IS NOT PERFECT SINCE THE DATA IS COLLECTED
 * FROM THE TAB LIST, AND TAB LIST CAN BE INCOMPLETE/MISSING SOME LETTERS
 */
public class PartyContainer {

    HashSet<String> partyMembers = new HashSet<>();
    String owner = "";

    boolean inParty = false;

    public PartyContainer() {}

    /**
     * Check if the player is at a party
     *
     * @return if the player is at a party
     */
    public boolean isPartying() {
        return inParty;
    }

    /**
     * Don't call this anyways, this is just for framework handling
     */
    public void closeParty() {
        if(!inParty) return;

        partyMembers.clear();
        owner = "";
        inParty = false;

        FrameworkManager.getEventBus().post(new WynnSocialEvent.PartyLeave());
    }

    /**
     * Add a member to the party
     *
     * @param username the party username
     */
    public void addMember(String username) {
        partyMembers.add(username);
    }

    /**
     * Add multiple members to the party
     *
     * @param members the list with all the members
     */
    public void addMembers(List<String> members) {
        partyMembers.addAll(members);
    }

    /**
     * Set the party owner
     *
     * @param owner party owner username
     */
    public void setOwner(String owner) {
        this.owner = owner;

        if(!owner.isEmpty() && !inParty) {
            inParty = true;
            FrameworkManager.getEventBus().post(new WynnSocialEvent.PartyJoin());
        }
    }

    /**
     * Used for handling over the framework
     *
     * @param owner party owner name
     * @param members party members list
     */
    public void updateParty(String owner, List<String> members) {
        partyMembers.clear();
        addMembers(members);

        setOwner(owner);
    }

    /**
     * Gets all the party members
     *
     * @return the party members list
     */
    public HashSet<String> getPartyMembers() {
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
