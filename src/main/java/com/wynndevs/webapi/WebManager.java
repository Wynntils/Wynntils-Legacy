package com.wynndevs.webapi;

import com.wynndevs.ModCore;
import com.wynndevs.webapi.profiles.TerritoryProfile;
import com.wynndevs.webapi.profiles.UpdateProfile;
import com.wynndevs.webapi.profiles.guild.GuildMember;
import com.wynndevs.webapi.profiles.guild.GuildProfile;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class WebManager {

    private static ArrayList<TerritoryProfile> territories = new ArrayList<>();
    private static UpdateProfile updateProfile;

    public static void init() {
        updateProfile = new UpdateProfile();

        updateTerritories();
    }

    public static ArrayList<TerritoryProfile> getTerritories() {
        return territories;
    }

    public static UpdateProfile getUpdate() {
        return updateProfile;
    }

    /**
     * Request a update to territories {@link ArrayList}
     */
    public static void updateTerritories() {
        new Thread(() -> {
            try{
                URLConnection st = new URL("https://api.wynncraft.com/public_api.php?action=territoryList").openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject main = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONObject("territories");

                for(String key : main.keySet()) {
                    if(main.getJSONObject(key).has("location")) {
                        JSONObject value = main.getJSONObject(key);
                        JSONObject loc = value.getJSONObject("location");
                        territories.add(new TerritoryProfile(key, loc.getInt("startX"), loc.getInt("startY"), loc.getInt("endX"), loc.getInt("endY"), value.getString("guild"), (value.isNull("attacker") ? null : value.getString("attacker")), value.getString("acquired")));
                    }
                }

                territories.add(new TerritoryProfile("Rodoroc", 1009, -5231, 1263, -5057, null, null, null));

            }catch (Exception ex) {
                ModCore.logger.warn("Error captured while trying to connect to Wynncraft Territory API", ex);}

        }).start();
    }

    /**
     * Request all guild names to WynnAPI
     *
     * @return a {@link ArrayList} containing all guild names
     * @throws Exception
     */
    public static ArrayList<String> getGuilds() throws Exception {
        ArrayList<String> guilds = new ArrayList<>();

        URLConnection st = new URL("https://api.wynncraft.com/public_api.php?action=guildList").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONArray array = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONArray("guilds");

        for(int i = 0; i < array.length(); i++) {
            guilds.add(array.getString(i));
        }

        return guilds;
    }

    /**
     * Request a guild info to WynnAPI
     *
     * @param guild Name of the guild
     *
     * @return A wrapper for all guild info
     * @throws Exception
     */
    public static GuildProfile getGuildProfile(String guild) throws Exception {
        URLConnection st = new URL("https://api.wynncraft.com/public_api.php?action=guildStats&command=" + guild).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

        if(obj.has("error")) {
            return null;
        }

        ArrayList<GuildMember> gmembers = new ArrayList<>();
        JSONArray members = obj.getJSONArray("members");
        if(members.length() >= 1) {
            for(int i = 0; i < members.length(); i++) {
                JSONObject member = members.getJSONObject(i);

                gmembers.add(new GuildMember(member.getString("name"), member.getString("rank"), member.getInt("contributed"), member.getString("joinedFriendly"), member.getString("joined")));
            }
        }

        return new GuildProfile(obj.getString("name"), obj.getString("prefix"),  obj.getDouble("xp"), obj.getInt("level"), obj.getString("created"), obj.getString("createdFriendly"), obj.getInt("territories"), gmembers);
    }

    /**
     * Request all online players to WynnAPI
     *
     * @return a {@link HashMap} who the key is the server and the value is an array containing all players on it
     * @throws Exception
     */
    public static HashMap<String, ArrayList<String>> getOnlinePlayers() throws Exception {
        HashMap<String, ArrayList<String>> servers = new HashMap<>();

        URLConnection st = new URL("https://api.wynncraft.com/public_api.php?action=onlinePlayers").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONObject main = new JSONObject(IOUtils.toString(st.getInputStream()));

        for(String key : main.keySet()) {
            if(key.equalsIgnoreCase("request")) {
                continue;
            }

            ArrayList<String> players = new ArrayList<>();
            JSONArray array = main.getJSONArray(key);
            if(array.length() >= 1) {
                for(int i = 0; i < array.length(); i++) {
                    players.add(array.getString(i));
                }
            }

            servers.put(key, players);
        }

        return servers;
    }

}
