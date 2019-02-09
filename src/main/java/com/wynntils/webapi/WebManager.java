/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynnGuildWarEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.webapi.account.WynntilsAccount;
import com.wynntils.webapi.profiles.*;
import com.wynntils.webapi.profiles.guild.GuildProfile;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
    private static PlayerStatsProfile playerProfile;
    private static HashMap<String, GuildProfile> guilds = new HashMap<>();

    private static ArrayList<UUID> helpers = new ArrayList<>();
    private static ArrayList<UUID> moderators = new ArrayList<>();
    private static ArrayList<UUID> premiums = new ArrayList<>();
    private static ArrayList<UUID> users = new ArrayList<>();
    private static ArrayList<UUID> donators = new ArrayList<>();

    private static ArrayList<UUID> ears = new ArrayList<>();
    private static ArrayList<UUID> elytras = new ArrayList<>();
    private static ArrayList<UUID> capes = new ArrayList<>();

    private static WynntilsAccount account = null;

    private static Gson gson = new Gson();

    private static Thread territoryUpdateThread;

    private static final int REQUEST_TIMEOUT_MILLIS = 5000;

    public static void reset() {
        apiUrls = null;

        territories = new HashMap<>();
        updateProfile = null;
        items = new HashMap<>();
        mapMarkers = new ArrayList<>();
        itemGuesses = new HashMap<>();
        playerProfile = null;
        guilds = new HashMap<>();

        helpers = new ArrayList<>();
        moderators = new ArrayList<>();
        premiums = new ArrayList<>();
        users = new ArrayList<>();

        ears = new ArrayList<>();
        elytras = new ArrayList<>();
        capes = new ArrayList<>();

        account = null;

        updateTerritoryThreadStatus(false);
    }

    public static void setupWebApi() {
        try{
            apiUrls = new WebReader("http://api.wynntils.com/webapi");
        }catch (Exception ex) { ex.printStackTrace(); return; }

        ProgressManager.ProgressBar progressBar = ProgressManager.push("Loading data from APIs", 7);

        progressBar.step("Territories");
        long ms = System.currentTimeMillis();
        updateTerritories();
        Reference.LOGGER.info("Territory list loaded in " + (System.currentTimeMillis() - ms) + "ms");

        try{
            progressBar.step("User roles");
            updateUsersRoles();
            progressBar.step("User models");
            updateUsersModels();

            progressBar.step("Items");
            ms = System.currentTimeMillis();
            updateItemList();
            Reference.LOGGER.info("Loaded " + items.size() + " items in " + (System.currentTimeMillis() - ms) + "ms");

            progressBar.step("Map Markers");
            ms = System.currentTimeMillis();
            updateMapMarkers();
            updateMapRefineries();
            Reference.LOGGER.info("Loaded " + mapMarkers.size() + " MapMarkers in " + (System.currentTimeMillis() - ms) + "ms");

            progressBar.step("Item Guesses");
            ms = System.currentTimeMillis();
            updateItemGuesses();
            Reference.LOGGER.info("Loaded " + itemGuesses.size() + " ItemGuesses in " + (System.currentTimeMillis() - ms) + "ms");

            progressBar.step("Player Stats");
            ms = System.currentTimeMillis();
            updatePlayerProfile();
            Reference.LOGGER.info("Loaded player stats in " + (System.currentTimeMillis() - ms) + "ms");
        }catch (Exception ex) { ex.printStackTrace(); }
        ProgressManager.pop(progressBar);

        updateTerritoryThreadStatus(true);
    }

    public static void checkForUpdates() {
        if (Reference.developmentEnvironment) {
            Reference.LOGGER.info("An update check would have occurred, but you are in a development environment.");
            return;
        }
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

    public static boolean isDonator(UUID uuid) {
        return donators.contains(uuid);
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

    public static void updateTerritoryThreadStatus(boolean start) {
        if(start) {
            if(territoryUpdateThread == null) {
                territoryUpdateThread = new TerritoryUpdateThread("Territory Update Thread");
                territoryUpdateThread.start();
                return;
            }
            return;
        }
        territoryUpdateThread.interrupt();
        territoryUpdateThread = null;
    }

    public static String getGuildTagFromName(String name) {
        if (!guilds.containsKey(name)) {
            try {
                guilds.put(name, getGuildProfile(name));
            } catch (Exception ex) {
                guilds.put(name, null);
            }
        }
        if (guilds.get(name) == null)
            return "ERROR";
        return guilds.get(name).getPrefix();
    }

    public static PlayerStatsProfile getPlayerProfile() {
        return playerProfile;
    }

    /**
     * Request a update to territories {@link ArrayList}
     */
    public static void updateTerritories() {
        Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {}.getType();
        JsonObject json = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("Territory")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                json = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "territories.json"))).getAsJsonObject();
            } else {
                useCache = true;
            }
        } catch (Exception ex) {
            useCache = true;
        }
        
        if (useCache) {
            Reference.LOGGER.warn("Error captured while trying to download territories data - attempting to load cached data");
            try {
                FileInputStream stream = recallApiResult("territories.json");
                json = new JsonParser().parse(IOUtils.toString(stream)).getAsJsonObject();
                Reference.LOGGER.info("Successfully loaded cached territory data!");
            } catch (Exception ex2) {
                Reference.LOGGER.warn("Unable to load backup territories data", ex2);
                return;
            }
        }
        
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
        Gson gson = builder.create();
        
        territories.putAll(gson.fromJson(json.get("territories"), type));
    }

    /**
     * Request all guild names to WynnAPI
     *
     * @return a {@link ArrayList} containing all guild names
     * @throws Exception
     */
    public static ArrayList<String> getGuilds() throws Exception {
        ArrayList<String> guilds = new ArrayList<>();
        JsonObject json = null;
        boolean useCache = false;

        try {
            URLConnection st = new URL(apiUrls.get("GuildList")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                json = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "guilds.json"))).getAsJsonObject();
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
            Reference.LOGGER.warn("Error captured while trying to download guild data - attempting to load cached data");
            json = new JsonParser().parse(IOUtils.toString(recallApiResult("guilds.json"))).getAsJsonObject();
            Reference.LOGGER.warn("Successfully loaded cached guild data!");
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
        URLConnection st = new URL(apiUrls.get("GuildInfo") + URLEncoder.encode(guild, "UTF-8")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OSX10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

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
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

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
        JsonArray main = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("ItemList")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "items.json"))).getAsJsonObject().getAsJsonArray("items");
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
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
        JsonArray jsonArray = null;
        ArrayList<MapMarkerProfile> markers = new ArrayList<>();
        boolean useCache = false;

        try {
            URLConnection st = new URL(apiUrls.get("MapMarkers")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                jsonArray = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "map_markers.json"))).getAsJsonObject().getAsJsonArray("locations");
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
            Reference.LOGGER.warn("Error downloading map marker data - attempting to use cached data");
            jsonArray = new JsonParser().parse(IOUtils.toString(recallApiResult("map_markers.json"))).getAsJsonObject().getAsJsonArray("locations");
            Reference.LOGGER.info("Successfully loaded cached map marker data!");
        }

        Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {
        }.getType();

        markers.addAll(gson.fromJson(jsonArray, type));

        mapMarkers = markers;
    }

    /**
     * Update all Refineries MapMarkers on the {@link HashMap} mapMarkers
     *
     * @throws Exception
     */
    public static void updateMapRefineries() throws Exception {
        JsonArray jsonArray = null;
        boolean useCache = false;

        try {
            URLConnection st = new URL(apiUrls.get("RefineryLocations")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            jsonArray = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "map_refineries.json"))).getAsJsonArray();
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }

        if (useCache) {
            Reference.LOGGER.warn("Error downloading map marker data - attempting to use cached data");
            jsonArray = new JsonParser().parse(IOUtils.toString(recallApiResult("map_refineries.json"))).getAsJsonArray();
            Reference.LOGGER.info("Successfully loaded cached map marker data!");
        }

        Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {}.getType();

        mapMarkers.addAll(gson.fromJson(jsonArray, type));
    }

    /**
     * Update all Wynn ItemGuesses on the {@link HashMap} itemGuesses
     *
     * @throws Exception
     */
    public static void updateItemGuesses() throws Exception {
        HashMap<String, ItemGuessProfile> guessers = new HashMap<>();
        String json = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("ItemGuesses")).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                json = IOUtils.toString(cacheApiResult(st.getInputStream(), "item_guesses.json"));
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
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

    public static void updatePlayerProfile() throws Exception {
        String json = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("PlayerStats") + ModCore.mc().getSession().getUsername()).openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                json = IOUtils.toString(cacheApiResult(st.getInputStream(), "player_stats.json"));
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }

        if (useCache) {
            Reference.LOGGER.warn("Error downloading player stats - attempting to use cached data");
            json = IOUtils.toString(recallApiResult("player_stats.json"));
            Reference.LOGGER.info("Successfully loaded cached player stats data!");
        }

        Type type = new TypeToken<PlayerStatsProfile>() {
        }.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, new PlayerStatsProfile.PlayerProfileDeserializer());
        Gson gson = gsonBuilder.create();

        playerProfile = gson.fromJson(json, type);
    }

    public static void updateUsersRoles() throws Exception {
        JsonObject main = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("UserAccount") + "getUsersRoles").openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "user_roles.json"))).getAsJsonObject();
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
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

        JsonArray donator = main.getAsJsonArray("donatorUsers");
        donators = gson.fromJson(donator, type);
    }

    public static void updateUsersModels() throws Exception {
        JsonObject main = null;
        boolean useCache = false;
        try {
            URLConnection st = new URL(apiUrls.get("UserAccount") + "getUserModels").openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                main = new JsonParser().parse(IOUtils.toString(cacheApiResult(st.getInputStream(), "user_models.json"))).getAsJsonObject();
            } else {
                useCache = true;
            }
        } catch (IOException | NullPointerException ex) {
            useCache = true;
        }
        
        if (useCache) {
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

    public static String getStableJarFileUrl() throws Exception {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return apiUrls.get("Jars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static String getStableJarFileMD5() throws Exception {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json?depth=2&tree=fingerprint[fileName,hash]{0,}").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return main.getAsJsonArray("fingerprint").get(0).getAsJsonObject().get("hash").getAsString();
    }

    public static String getStableJarVersion() throws Exception {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json?tree=artifacts[fileName]").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return main.getAsJsonObject().get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("fileName").getAsString().split("_")[0].split("-")[1];
    }

    public static String getCuttingEdgeJarFileUrl() throws Exception {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return apiUrls.get("DevJars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static String getCuttingEdgeJarFileMD5() throws Exception {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json?depth=2&tree=fingerprint[fileName,hash]{0,}").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return main.getAsJsonArray("fingerprint").get(0).getAsJsonObject().get("hash").getAsString();
    }

    public static int getCuttingEdgeBuildNumber() throws Exception {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json?tree=number").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
        return main.getAsJsonObject().get("number").getAsInt();
    }

    public static ArrayList<MusicProfile> getCurrentAvailableSongs() throws Exception {
        URLConnection st = new URL(apiUrls.get("WynnSounds")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

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
            apiCacheFolder.mkdirs();
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

    public static class TerritoryUpdateThread extends Thread {

        public TerritoryUpdateThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Thread.sleep(30000);
                    HashMap<String, TerritoryProfile> prevList = new HashMap<>(territories);
                    updateTerritories();
                    for (TerritoryProfile prevTerritory : prevList.values()) {
                        TerritoryProfile currentTerritory = territories.get(prevTerritory.getName());
                        if (!currentTerritory.getGuild().equals(prevTerritory.getGuild())) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getName(), currentTerritory.getGuild(), prevTerritory.getGuild(), getGuildTagFromName(currentTerritory.getGuild()), getGuildTagFromName(prevTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.CAPTURED));
                        } else if (prevTerritory.getAttacker() == null && currentTerritory.getAttacker() != null) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getName(), currentTerritory.getAttacker(), prevTerritory.getGuild(), getGuildTagFromName(currentTerritory.getAttacker()), getGuildTagFromName(prevTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.ATTACKED));
                        } else if (prevTerritory.getAttacker() != null && currentTerritory.getAttacker() == null) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getName(), prevTerritory.getAttacker(), currentTerritory.getGuild(), getGuildTagFromName(prevTerritory.getAttacker()), getGuildTagFromName(currentTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.DEFENDED));
                        }
                    }
                }
                Reference.LOGGER.info("Terminating territory update thread.");
            } catch (InterruptedException ignored) {
                Reference.LOGGER.info("Terminating territory update thread.");
            }
        }
    }

    /**
     * @return all api locations
     */
    public static WebReader getApiUrls() {
        return apiUrls;
    }
}
