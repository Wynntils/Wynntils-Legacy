/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles.guild;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GuildProfile {

    List <GuildMember> members;

    String name;
    String prefix;
    double xp;
    int level;
    Date created;
    String createdFriendly;
    int territories;

    public GuildProfile(String name, String prefix, double xp, int level, Date created, String createdFriendly, int territories, List<GuildMember> members) {
        this.name = name; this.prefix = prefix; this.xp = xp; this.level = level; this.created = created; this.createdFriendly = createdFriendly; this.territories = territories;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<GuildMember> getMembers() {
        return members;
    }

    public double getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getTerritories() {
        return territories;
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedFriendly() {
        return createdFriendly;
    }

    public String getPrefix() {
        return prefix;
    }

}
