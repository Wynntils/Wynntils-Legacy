package cf.wynntils.webapi;

import cf.wynntils.Reference;
import cf.wynntils.webapi.account.WynntilsAccount;
import cf.wynntils.webapi.profiles.MapMarkerProfile;
import cf.wynntils.webapi.profiles.MusicProfile;
import cf.wynntils.webapi.profiles.TerritoryProfile;
import cf.wynntils.webapi.profiles.UpdateProfile;
import cf.wynntils.webapi.profiles.guild.GuildProfile;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WebManager {

    public static WebReader apiUrls;

    private static HashMap<String, TerritoryProfile> territories = new HashMap<>();
    private static UpdateProfile updateProfile;
    private static HashMap<String, ItemProfile> items = new HashMap<>();
    private static ArrayList<ItemProfile> directItems = new ArrayList<>();
    private static ArrayList<MapMarkerProfile> mapMarkers = new ArrayList<>();
    private static HashMap<String, ItemGuessProfile> itemGuesses = new HashMap<>();

    private static ArrayList<UUID> helpers = new ArrayList<>();
    private static ArrayList<UUID> moderators = new ArrayList<>();
    private static ArrayList<UUID> premiums = new ArrayList<>();
    private static ArrayList<UUID> users = new ArrayList<>();

    private static ArrayList<UUID> ears = new ArrayList<>();
    private static ArrayList<UUID> elytras = new ArrayList<>();
    private static ArrayList<UUID> capes = new ArrayList<>();

    private static WynntilsAccount account = null;

    private static Gson gson = new Gson();

    public static void reset() {
        apiUrls = null;

        territories = new HashMap<>();
        updateProfile = null;
        items = new HashMap<>();
        mapMarkers = new ArrayList<>();
        itemGuesses = new HashMap<>();

        helpers = new ArrayList<>();
        moderators = new ArrayList<>();
        premiums = new ArrayList<>();
        users = new ArrayList<>();

        ears = new ArrayList<>();
        elytras = new ArrayList<>();
        capes = new ArrayList<>();

        account = null;
    }

    public static void setupWebApi() {
        updateProfile = new UpdateProfile();

        try{
            apiUrls = new WebReader("http://api.wynntils.cf/webapi");
        }catch (Exception ex) { ex.printStackTrace(); return; }

        long ms = System.currentTimeMillis();
        updateTerritories();
        Reference.LOGGER.info("Territory list loaded on " + (System.currentTimeMillis() - ms) + "ms");

        try{
            updateUsersRoles();
            updateUsersModels();

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

    public static void checkForUpdates() {
        updateProfile = new UpdateProfile();
    }

    public static void setupUserAccount() {
        account = new WynntilsAccount();
    }

    public static WynntilsAccount getAccount() {
        return account;
    }

    public static HashMap<String, TerritoryProfile> getTerritories() {
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

    public static ArrayList<ItemProfile> getDirectItems() {
        return directItems;
    }

    public static boolean isHelper(UUID uuid) {
        return helpers.contains(uuid);
    }

    public static boolean isModerator(UUID uuid) {
        return moderators.contains(uuid);
    }

    public static boolean isPremium(UUID uuid) {
        return premiums.contains(uuid);
    }

    public static boolean hasElytra(UUID uuid) {
        return elytras.contains(uuid);
    }

    public static boolean hasEars(UUID uuid) {
        return ears.contains(uuid);
    }

    public static boolean hasCape(UUID uuid) {
        return capes.contains(uuid);
    }

    public static boolean isUser(UUID uuid) {
        return users.contains(uuid);
    }

    /**
     * Request a update to territories {@link ArrayList}
     */
    public static void updateTerritories() {
        new Thread(() -> {
            try{
                URLConnection st = new URL(apiUrls.get("Territory")).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
                Gson gson = builder.create();

                Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {
                }.getType();

                JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();
                territories.putAll(gson.fromJson(json.get("territories"), type));

                territories.put("Rodoroc", new TerritoryProfile("Rodoroc", 965, -5238, 1265, -5067, null, null, null));

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

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        JsonObject json = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();
        guilds.addAll(gson.fromJson(json.get("guilds"), type));

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
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OSX10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject obj = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();

        if(obj.has("error")) {
            return null;
        }

        return gson.fromJson(obj, GuildProfile.class);
    }

    /**
     * Request all online players to WynnAPI
     *
     * @return a {@link HashMap} who the key is the server and the value is an array containing all players on it
     * @throws Exception
     */
    public static HashMap<String, ArrayList<String>> getOnlinePlayers() throws Exception {
        URLConnection st = new URL(apiUrls.get("OnlinePlayers")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();
        main.remove("request");

        Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {
        }.getType();

        return gson.fromJson(main, type);
    }

    /**
     * Update all Wynn items on the {@link HashMap} items
     *
     * @throws Exception
     */
    public static void updateItemList() throws Exception {
        URLConnection st = new URL(apiUrls.get("ItemList")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonArray main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject().getAsJsonArray("items");

        Type type = new TypeToken<HashMap<String, ItemProfile>>() {
        }.getType();

        HashMap<String, ItemProfile> citems = ItemProfile.GSON.fromJson(main, type);
        directItems.addAll(citems.values());

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

        Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {
        }.getType();

        JsonArray json = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject().getAsJsonArray("locations");
        markers.addAll(gson.fromJson(json, type));

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

        String json = IOUtils.toString(st.getInputStream(), "UTF-8");

        Type type = new TypeToken<HashMap<String, ItemGuessProfile>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(HashMap.class, new ItemGuessProfile.ItemGuessDeserializer());
        Gson gson = gsonBuilder.create();

        guessers.putAll(gson.fromJson(json, type));

        itemGuesses = guessers;
    }

    public static void updateUsersRoles() throws Exception {
        URLConnection st = new URL(apiUrls.get("UserAccount") + "getUsersRoles").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(UUID.class, new UUIDTypeAdapter());
        Gson gson = builder.create();

        Type type = new TypeToken<ArrayList<UUID>>() {
        }.getType();

        JsonArray helper = main.getAsJsonArray("helperUsers");
        helpers = gson.fromJson(helper, type);

        JsonArray moderator = main.getAsJsonArray("moderatorUsers");
        moderators = gson.fromJson(moderator, type);

        JsonArray premium = main.getAsJsonArray("premiumUsers");
        premiums = gson.fromJson(premium, type);

        JsonArray user = main.getAsJsonArray("normalUsers");
        users = gson.fromJson(user, type);
    }

    public static void updateUsersModels() throws Exception {
        URLConnection st = new URL(apiUrls.get("UserAccount") + "getUserModels").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(UUID.class, new UUIDTypeAdapter());
        Gson gson = builder.create();

        Type type = new TypeToken<ArrayList<UUID>>() {
        }.getType();

        JsonArray ear = main.getAsJsonArray("earsActive");
        ears = gson.fromJson(ear, type);

        JsonArray elytra = main.getAsJsonArray("elytraActive");
        elytras = gson.fromJson(elytra, type);

        JsonArray cape = main.getAsJsonArray("capeActive");
        capes = gson.fromJson(cape, type);
    }

    public static String getLatestJarFileUrl() throws Exception {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonObject();
        return apiUrls.get("Jars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static ArrayList<MusicProfile> getCurrentAvailableSongs() throws Exception {
        URLConnection st = new URL(apiUrls.get("WynnSounds")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        ArrayList<MusicProfile> result = new ArrayList<>();
        JsonArray array = new JsonParser().parse(IOUtils.toString(st.getInputStream(), "UTF-8")).getAsJsonArray();
        for(int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            if(!obj.has("name") || !obj.has("download_url") || !obj.has("size")) continue;

            result.add(new MusicProfile(obj.get("name").getAsString(), obj.get("download_url").getAsString(), obj.get("size").getAsLong()));
        }

        return result;
    }

}
