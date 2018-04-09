package cf.wynntils.webapi;

import cf.wynntils.Reference;
import cf.wynntils.webapi.account.WynntilsAccount;
import cf.wynntils.webapi.profiles.MapMarkerProfile;
import cf.wynntils.webapi.profiles.TerritoryProfile;
import cf.wynntils.webapi.profiles.UpdateProfile;
import cf.wynntils.webapi.profiles.guild.GuildMember;
import cf.wynntils.webapi.profiles.guild.GuildProfile;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class WebManager {

    public static WebReader apiUrls;

    private static ArrayList<TerritoryProfile> territories = new ArrayList<>();
    private static UpdateProfile updateProfile;
    private static HashMap<String, ItemProfile> items = new HashMap<>();
    private static ArrayList<MapMarkerProfile> mapMarkers = new ArrayList<>();
    private static HashMap<String, ItemGuessProfile> itemGuesses = new HashMap<>();

    private static WynntilsAccount account = null;

    public static void setupWebApi() {
        updateProfile = new UpdateProfile();

        try{
            apiUrls = new WebReader("http://api.wynntils.cf/webapi");
        }catch (Exception ex) { ex.printStackTrace(); return; }

        long ms = System.currentTimeMillis();
        updateTerritories();
        Reference.LOGGER.info("Territory list loaded on " + (System.currentTimeMillis() - ms) + "ms");

        try{
            ms = System.currentTimeMillis();
            updateItemList();
            Reference.LOGGER.info("Loaded " + items.size() + " items on " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            updateMapMarkers();
            Reference.LOGGER.info("Loaded " + mapMarkers.size() + " MapMarkers on " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            updateItemGuesses();
            Reference.LOGGER.info("Loaded " + itemGuesses.size() + " ItemGuesses on " + (System.currentTimeMillis() - ms) + "ms");

        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void setupUserAccount() {
        account = new WynntilsAccount();
    }

    public static WynntilsAccount getAccount() {
        return account;
    }

    public static ArrayList<TerritoryProfile> getTerritories() {
        return territories;
    }

    public static HashMap<String, ItemProfile> getItems() {
        return items;
    }

    public static ArrayList<MapMarkerProfile> getMapMarkers() {
        return mapMarkers;
    }

    public static UpdateProfile getUpdate() {
        return updateProfile;
    }

    public static HashMap<String, ItemGuessProfile> getItemGuesses() { return itemGuesses; }

    /**
     * Request a update to territories {@link ArrayList}
     */
    public static void updateTerritories() {
        new Thread(() -> {
            try{
                URLConnection st = new URL(apiUrls.get("Territory")).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject main = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONObject("territories");

                for(String key : main.keySet()) {
                    if(main.getJSONObject(key).has("location")) {
                        JSONObject value = main.getJSONObject(key);
                        JSONObject loc = value.getJSONObject("location");
                        territories.add(new TerritoryProfile(key, loc.getInt("startX"), loc.getInt("startY"), loc.getInt("endX"), loc.getInt("endY"), value.getString("guild"), (value.isNull("attacker") ? null : value.getString("attacker")), value.getString("acquired")));
                    }
                }

                territories.add(new TerritoryProfile("Rodoroc", 965, -5238, 1265, -5067, null, null, null));

            }catch (Exception ex) {
                Reference.LOGGER.warn("Error captured while trying to connect to Wynncraft Territory API", ex);}

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

        URLConnection st = new URL(apiUrls.get("GuildList")).openConnection();
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
        URLConnection st = new URL(apiUrls.get("GuildInfo") + guild).openConnection();
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

        URLConnection st = new URL(apiUrls.get("OnlinePlayers")).openConnection();
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

    /**
     * Update all Wynn items on the {@link HashMap} items
     *
     * @throws Exception
     */
    public static void updateItemList() throws Exception {
        HashMap<String, ItemProfile> citems = new HashMap<>();

        URLConnection st = new URL(apiUrls.get("ItemList")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONArray main = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONArray("items");

        for(int i = 0; i < main.length(); i++) {
            JSONObject item = main.getJSONObject(i);
            String name = item.getString("name");
            ItemProfile pf = new ItemProfile();

            for(String key : item.keySet()) {
                if(!item.isNull(key)) {
                    if(key.equals("material") && (item.get("material").getClass() == int.class || item.get("material").getClass() == Integer.class)) {
                        pf.getClass().getField(key).set(pf, String.valueOf(item.get(key)));
                    }else{
                        if(key.equals("droptype") || key.equals("sropType")) {
                            pf.getClass().getField("dropType").set(pf, item.get(key));
                        }else{
                            pf.getClass().getField(key).set(pf, item.get(key));
                        }
                    }
                }
            }

            citems.put(name, pf);
        }

        items = citems;
    }

    /**
     * Update all Wynn MapMarkers on the {@link HashMap} mapMarkers
     *
     * @throws Exception
     */
    public static void updateMapMarkers() throws Exception {
        ArrayList<MapMarkerProfile> markers = new ArrayList<>();

        URLConnection st = new URL(apiUrls.get("MapMarkers")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONArray main = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONArray("locations");

        for(int i = 0; i < main.length(); i++) {
            JSONObject loc = main.getJSONObject(i);

            markers.add(new MapMarkerProfile(loc.getString("name"), loc.getInt("x"), loc.getInt("y") + 3, loc.getInt("z"), loc.getString("icon").replace(".png", "")));
        }

        mapMarkers = markers;
    }

    /**
     * Update all Wynn ItemGuesses on the {@link HashMap} itemGuesses
     *
     * @throws Exception
     */
    public static void updateItemGuesses() throws Exception {
        HashMap<String, ItemGuessProfile> guessers = new HashMap<>();

        URLConnection st = new URL(apiUrls.get("ItemGuesses")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONObject main = new JSONObject(IOUtils.toString(st.getInputStream()));

        for(String range : main.keySet()) {
            ItemGuessProfile pf = new ItemGuessProfile(range);
            for (String pieces : main.getJSONObject(range).keySet()) {
                HashMap<String, String> parts = new HashMap<>();

                for (String rarity : main.getJSONObject(range).getJSONObject(pieces).keySet()) {
                    parts.put(rarity, main.getJSONObject(range).getJSONObject(pieces).getString(rarity));
                }

                pf.addItems(pieces, parts);
            }

            guessers.put(range, pf);
        }

        itemGuesses = guessers;
    }

    public static String getLatestJarFileUrl() throws Exception {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONObject main = new JSONObject(IOUtils.toString(st.getInputStream()));
        return apiUrls.get("Jars") + "artifact/" + main.getJSONObject("artifacts").getJSONObject("0").getString("relativePath");
    }

    public static ArrayList<String> getAllUsersWithCapes() throws Exception {
        URLConnection st = new URL(apiUrls.get("UserAccount") + "/getUsersCapes").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JSONArray main = new JSONObject(IOUtils.toString(st.getInputStream())).getJSONArray("usersWithCapes");
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < main.length(); i++ ) {
            result.add(main.getString(i));
        }

        return result;
    }

}
