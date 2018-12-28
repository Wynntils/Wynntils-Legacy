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

import java.io.*;
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
        Reference.LOGGER.info("Territory list loaded in " + (System.currentTimeMillis() - ms) + "ms");

        try{
            updateUsersRoles();
            updateUsersModels();

            ms = System.currentTimeMillis();
            updateItemList();
            Reference.LOGGER.info("Loaded " + items.size() + " items in " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            updateMapMarkers();
            Reference.LOGGER.info("Loaded " + mapMarkers.size() + " MapMarkers in " + (System.currentTimeMillis() - ms) + "ms");

            ms = System.currentTimeMillis();
            updateItemGuesses();
            Reference.LOGGER.info("Loaded " + itemGuesses.size() + " ItemGuesses in " + (System.currentTimeMillis() - ms) + "ms");
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
        Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {}.getType();
        try {
            URLConnection st = new URL(apiUrls.get("Territory")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
            Gson gson = builder.create();

            JsonObject json = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "territories.json"))).getAsJsonObject();
            territories.putAll(gson.fromJson(json.get("territories"), type));
        } catch (Exception ex) {
            Reference.LOGGER.warn("Error captured while trying to download territories data - attempting to load cached data", ex);
            try {
                FileInputStream stream = recallApiResult("territories.json");
                JsonObject json = new JsonParser().parse(IOUtils.toString(stream)).getAsJsonObject();
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
                Gson gson = builder.create();
                territories.putAll(gson.fromJson(json.get("territories"), type));
                Reference.LOGGER.info("Successfully loaded cached territory data!");
            } catch (Exception ex2) {
                Reference.LOGGER.warn("Unable to load backup territories data", ex2);
            }
        }
    }

    /**
     * Request all guild names to WynnAPI
     *
     * @return a {@link ArrayList} containing all guild names
     * @throws Exception
     */
    public static ArrayList<String> getGuilds() throws Exception {
        ArrayList<String> guilds = new ArrayList<>();
        JsonObject json;

        try {
            URLConnection st = new URL(apiUrls.get("GuildList")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            json = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "guilds.json"))).getAsJsonObject();
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error captured while trying to download guild data - attempting to load cached data", ex);
            json = new JsonParser().parse(IOUtils.toString(recallApiResult("guilds.json"))).getAsJsonObject();
            Reference.LOGGER.warn("Successfully loaded cached guild data!", ex);
        }

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

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

        JsonObject obj = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();

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

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
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
        JsonArray main;
        try {
            URLConnection st = new URL(apiUrls.get("ItemList")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "items.json"))).getAsJsonObject().getAsJsonArray("items");
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error downloading item data - attempting to use cached data");
            main = new JsonParser().parse(IOUtils.toString(recallApiResult("items.json"))).getAsJsonObject().getAsJsonArray("items");
            Reference.LOGGER.info("Successfully loaded cached item data!");
        }

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
        JsonArray jsonArray;
        ArrayList<MapMarkerProfile> markers = new ArrayList<>();

        try {
            URLConnection st = new URL(apiUrls.get("MapMarkers")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            jsonArray = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "map_markers.json"))).getAsJsonObject().getAsJsonArray("locations");
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error downloading map marker data - attempting to use cached data");
            jsonArray = new JsonParser().parse(IOUtils.toString(recallApiResult("items.json"))).getAsJsonObject().getAsJsonArray("items");
            Reference.LOGGER.info("Successfully loaded cached map marker data!");
        }

        Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {
        }.getType();

        markers.addAll(gson.fromJson(jsonArray, type));

        mapMarkers = markers;
    }

    /**
     * Update all Wynn ItemGuesses on the {@link HashMap} itemGuesses
     *
     * @throws Exception
     */
    public static void updateItemGuesses() throws Exception {
        HashMap<String, ItemGuessProfile> guessers = new HashMap<>();
        String json;

        try {
            URLConnection st = new URL(apiUrls.get("ItemGuesses")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            json = IOUtils.toString(cacheApiResult(st.getInputStream(), "item_guesses.json"));
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error downloading item guesses - attempting to use cached data");
            json = IOUtils.toString(recallApiResult("item_guesses.json"));
            Reference.LOGGER.info("Successfully loaded cached item guesses data!");
        }

        Type type = new TypeToken<HashMap<String, ItemGuessProfile>>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(HashMap.class, new ItemGuessProfile.ItemGuessDeserializer());
        Gson gson = gsonBuilder.create();

        guessers.putAll(gson.fromJson(json, type));

        itemGuesses = guessers;
    }

    public static void updateUsersRoles() throws Exception {
        JsonObject main;
        try {
            URLConnection st = new URL(apiUrls.get("UserAccount") + "getUsersRoles").openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "user_roles.json"))).getAsJsonObject();
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error downloading user roles - attempting to use cached data");
            main = new JsonParser().parse(IOUtils.toString(recallApiResult("user_roles.json"))).getAsJsonObject();
            Reference.LOGGER.info("Successfully loaded cached user role data!");
        }

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
        JsonObject main;
        try {
            URLConnection st = new URL(apiUrls.get("UserAccount") + "getUserModels").openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "user_models.json"))).getAsJsonObject();
        } catch (IOException ex) {
            Reference.LOGGER.warn("Error downloading user models - attempting to use cached data");
            main = new JsonParser().parse(IOUtils.toString(recallApiResult("user_models.json"))).getAsJsonObject();
            Reference.LOGGER.info("Successfully loaded cached user model data!");
        }

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

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return apiUrls.get("Jars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static ArrayList<MusicProfile> getCurrentAvailableSongs() throws Exception {
        URLConnection st = new URL(apiUrls.get("WynnSounds")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        ArrayList<MusicProfile> result = new ArrayList<>();
        JsonArray array = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonArray();
        for(int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            if(!obj.has("name") || !obj.has("download_url") || !obj.has("size")) continue;

            result.add(new MusicProfile(obj.get("name").getAsString(), obj.get("download_url").getAsString(), obj.get("size").getAsLong()));
        }

        return result;
    }

    /**
     * Attempt to store an {@link InputStream} to a file on disk
     *
     * @param stream The {@link InputStream} to read
     * @param fileName The filename to save to (file saved in /apicache directory)
     * @return A {@link InputStream} for the saved result
     * @throws IOException
     */
    public static FileInputStream cacheApiResult(InputStream stream, String fileName) throws IOException {
        File apiCacheFolder = new File(Reference.MOD_STORAGE_ROOT.getPath() + "/apicache");
        File apiCacheFile = new File(apiCacheFolder.getPath() + "/" + fileName);
        FileOutputStream cacheOutputStream;
        if (!apiCacheFolder.exists())
            apiCacheFolder.mkdir();
        if (!apiCacheFile.exists())
            apiCacheFile.createNewFile();
        cacheOutputStream = new FileOutputStream(apiCacheFile);
        IOUtils.copy(stream, cacheOutputStream);
        cacheOutputStream.close();
        stream.close();
        return new FileInputStream(apiCacheFile);
    }

    /**
     * Attempt to store an {@link InputStream} to a file on disk
     *
     * @param fileName The filename to load from - returns null if the file doesn't exist
     * @return A {@link InputStream} for the saved result
     * @throws IOException
     */
    public static FileInputStream recallApiResult(String fileName) throws IOException {
        File apiCacheFolder = new File(Reference.MOD_STORAGE_ROOT.getPath() + "/apicache");
        File apiCacheFile = new File(apiCacheFolder.getPath() + "/" + fileName);
        if (!apiCacheFolder.exists() || !apiCacheFolder.isDirectory())
            return null;
        if (!apiCacheFile.exists() || apiCacheFile.isDirectory())
            return null;
        return new FileInputStream(apiCacheFile);
    }


}
