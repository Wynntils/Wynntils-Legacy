package com.wynntils.webapi.profiles;

import com.google.gson.*;
import com.wynntils.Reference;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerStatsProfile {

    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private PlayerRank rank;
    private PlayerTag tag;
    private boolean displayTag;
    private boolean veteran;
    private int playtime;
    private Date first_join;
    private Date last_join;
    private int global_items_identified;
    private int global_mobs_killed;
    private int global_pvp_kills;
    private int global_pvp_deaths;
    private int global_chests_found;
    private int global_blocks_walked;
    private int global_logins;
    private int global_deaths;
    private int global_total_level;
    private int ranking_pvp;
    private int ranking_player;
    private int ranking_guild;
    private String guild_name;
    private GuildRank guild_rank;

    public PlayerStatsProfile(PlayerRank rank, PlayerTag tag, boolean displayTag, boolean veteran, int playtime, Date first_join, Date last_join, int global_items_identified, int global_mobs_killed, int global_pvp_kills, int global_pvp_deaths, int global_chests_found, int global_blocks_walked, int global_logins, int global_deaths, int global_total_level, int ranking_pvp, int ranking_player, int ranking_guild, String guild_name, GuildRank guild_rank) {
        this.rank = rank;
        this.tag = tag;
        this.displayTag = displayTag;
        this.veteran = veteran;
        this.playtime = playtime;
        this.first_join = first_join;
        this.last_join = last_join;
        this.global_items_identified = global_items_identified;
        this.global_mobs_killed = global_mobs_killed;
        this.global_pvp_kills = global_pvp_kills;
        this.global_pvp_deaths = global_pvp_deaths;
        this.global_chests_found = global_chests_found;
        this.global_blocks_walked = global_blocks_walked;
        this.global_logins = global_logins;
        this.global_deaths = global_deaths;
        this.global_total_level = global_total_level;
        this.ranking_pvp = ranking_pvp;
        this.ranking_player = ranking_player;
        this.ranking_guild = ranking_guild;
        this.guild_name = guild_name;
        this.guild_rank = guild_rank;
    }

    public static SimpleDateFormat getApiDateFormat() {
        return API_DATE_FORMAT;
    }

    public PlayerRank getRank() {
        return rank;
    }

    public void setRank(PlayerRank rank) {
        this.rank = rank;
    }

    public PlayerTag getTag() {
        return tag;
    }

    public void setTag(PlayerTag tag) {
        this.tag = tag;
    }

    public boolean isDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(boolean displayTag) {
        this.displayTag = displayTag;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public Date getFirstJoin() {
        return first_join;
    }

    public void setFirstJoin(Date first_join) {
        this.first_join = first_join;
    }

    public Date getLastJoin() {
        return last_join;
    }

    public void setLastJoin(Date last_join) {
        this.last_join = last_join;
    }

    public int getGlobalItemsIdentified() {
        return global_items_identified;
    }

    public void setGlobalItemsIdentified(int global_items_identified) {
        this.global_items_identified = global_items_identified;
    }

    public int getGlobalMobsKilled() {
        return global_mobs_killed;
    }

    public void setGlobalMobsKilled(int global_mobs_killed) {
        this.global_mobs_killed = global_mobs_killed;
    }

    public int getGlobalPvpKills() {
        return global_pvp_kills;
    }

    public void setGlobalPvpKills(int global_pvp_kills) {
        this.global_pvp_kills = global_pvp_kills;
    }

    public int getGlobalPvpDeaths() {
        return global_pvp_deaths;
    }

    public void setGlobalPvpDeaths(int global_pvp_deaths) {
        this.global_pvp_deaths = global_pvp_deaths;
    }

    public int getGlobalChestsFound() {
        return global_chests_found;
    }

    public void setGlobalChestsFound(int global_chests_found) {
        this.global_chests_found = global_chests_found;
    }

    public int getGlobalBlocksWalked() {
        return global_blocks_walked;
    }

    public void setGlobalBlocksWalked(int global_blocks_walked) {
        this.global_blocks_walked = global_blocks_walked;
    }

    public int getGlobalLogins() {
        return global_logins;
    }

    public void setGlobalLogins(int global_logins) {
        this.global_logins = global_logins;
    }

    public int getGlobalDeaths() {
        return global_deaths;
    }

    public void setGlobalDeaths(int global_deaths) {
        this.global_deaths = global_deaths;
    }

    public int getGlobalTotalLevel() {
        return global_total_level;
    }

    public void setGlobalTotalLevel(int global_total_level) {
        this.global_total_level = global_total_level;
    }

    public int getRankingPvp() {
        return ranking_pvp;
    }

    public void setRankingPvp(int ranking_pvp) {
        this.ranking_pvp = ranking_pvp;
    }

    public int getRankingPlayer() {
        return ranking_player;
    }

    public void setRankingPlayer(int ranking_player) {
        this.ranking_player = ranking_player;
    }

    public int getRankingGuild() {
        return ranking_guild;
    }

    public void setRankingGuild(int ranking_guild) {
        this.ranking_guild = ranking_guild;
    }

    public String getGuildName() {
        return guild_name;
    }

    public void setGuildName(String guild_name) {
        this.guild_name = guild_name;
    }

    public GuildRank getGuildRank() {
        return guild_rank;
    }

    public void setGuildRank(GuildRank guild_rank) {
        this.guild_rank = guild_rank;
    }

    public enum PlayerRank {
        Player,
        Moderator,
        Administrator
    }

    public enum PlayerTag {
        NONE,
        VIP,
        VIPPLUS,
        HERO
    }

    public enum GuildRank {
        OWNER("★★★★"),
        CHIEF("★★★"),
        CAPTAIN("★★"),
        RECRUITER("★"),
        RECRUIT(""),
        NONE("");

        private String stars;

        GuildRank(String stars) {
            this.stars = stars;
        }

        public String getStars() {
            return stars;
        }
    }

    public static class PlayerProfileDeserializer implements JsonDeserializer<PlayerStatsProfile> {

        @Override
        public PlayerStatsProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject playerProfile = json.getAsJsonObject();
            PlayerRank rank = PlayerRank.valueOf(playerProfile.get("rank").getAsString());

            PlayerTag tag;
            if(playerProfile.get("tag").getAsString().isEmpty()) tag = PlayerTag.NONE;
            else tag = PlayerTag.valueOf(playerProfile.get("tag").getAsString());

            boolean displayTag = playerProfile.get("displayTag").getAsBoolean();
            boolean veteran = playerProfile.get("veteran").getAsBoolean();
            int playtime = playerProfile.get("playtime").getAsInt();
            Date first_join;
            Date last_join;
            try {
                first_join = API_DATE_FORMAT.parse(playerProfile.get("first_join").getAsString());
                last_join = API_DATE_FORMAT.parse(playerProfile.get("last_join").getAsString());
            } catch (ParseException ex) {
                Reference.LOGGER.warn("Unable to parse join dates from Wynncraft's API.", ex);
                first_join = new Date();
                last_join = new Date();
            }
            JsonObject globalStats = playerProfile.getAsJsonObject("global");
            int global_items_identified = globalStats.get("items_identified").getAsInt();
            int global_mobs_killed = globalStats.get("mobs_killed").getAsInt();
            int global_pvp_kills = globalStats.get("pvp_kills").getAsInt();
            int global_pvp_deaths = globalStats.get("pvp_deaths").getAsInt();
            int global_chests_found = globalStats.get("chests_found").getAsInt();
            int global_blocks_walked = globalStats.get("blocks_walked").getAsInt();
            int global_logins = globalStats.get("logins").getAsInt();
            int global_deaths = globalStats.get("deaths").getAsInt();
            int global_total_levels = globalStats.get("total_level").getAsInt();

            JsonObject rankings = playerProfile.getAsJsonObject("rankings");

            int ranking_pvp = 0;
            if(!rankings.get("pvp").isJsonNull()) ranking_pvp = rankings.get("pvp").getAsInt();

            int ranking_player = 0;
            if(!rankings.get("player").isJsonNull()) ranking_player = rankings.get("player").getAsInt();

            int ranking_guild = 0;
            if(!rankings.get("guild").isJsonNull()) ranking_guild = rankings.get("guild").getAsInt();

            JsonObject guild = playerProfile.getAsJsonObject("guild");
            String guild_name = guild.get("name").getAsString();

            GuildRank guild_rank;
            if(guild.get("rank").getAsString().isEmpty()) guild_rank = GuildRank.NONE;
            else guild_rank = GuildRank.valueOf(guild.get("rank").getAsString().toUpperCase());

            return new PlayerStatsProfile(rank, tag, displayTag, veteran, playtime, first_join, last_join, global_items_identified, global_mobs_killed, global_pvp_kills, global_pvp_deaths, global_chests_found, global_blocks_walked, global_logins, global_deaths, global_total_levels, ranking_pvp, ranking_player, ranking_guild, guild_name, guild_rank);
        }
    }
}
