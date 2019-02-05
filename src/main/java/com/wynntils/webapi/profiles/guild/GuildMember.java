/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.guild;

import java.util.Date;

public class GuildMember {

    String name;
    String rank;
    int contributed;
    String joinedFriendly;
    Date joined;

    public GuildMember(String name, String rank, int contributed, String joinedFriendly, Date joined) {
        this.name = name; this.rank = rank; this.contributed = contributed; this.joinedFriendly = joinedFriendly; this.joined = joined;
    }

    public String getName() {
        return name;
    }

    public int getContributed() {
        return contributed;
    }

    public String getRank() {
        return rank;
    }

    public Date getJoined() {
        return joined;
    }

    public String getJoinedFriendly() {
        return joinedFriendly;
    }

    public static enum Rank {

        RECRUIT,

        RECRUITER,

        CAPTAIN,

        CHIEF,

        OWNER;
    }

}
