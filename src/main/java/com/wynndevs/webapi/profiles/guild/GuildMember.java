package com.wynndevs.webapi.profiles.guild;

public class GuildMember {

    String name;
    String rank;
    int contributed;
    String joinedFriendly;
    String joined;

    public GuildMember(String name, String rank, int contributed, String joinedFriendly, String joined) {
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

    public String getJoined() {
        return joined;
    }

    public String getJoinedFriendly() {
        return joinedFriendly;
    }

}
