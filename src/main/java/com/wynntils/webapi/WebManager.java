/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynnGuildWarEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.overlays.objects.MapApiIcon;
import com.wynntils.webapi.account.WynntilsAccount;
import com.wynntils.webapi.profiles.DiscoveryProfile;
import com.wynntils.webapi.profiles.LeaderboardProfile;
import com.wynntils.webapi.profiles.LocationProfile;
import com.wynntils.webapi.profiles.MapLabelProfile;
import com.wynntils.webapi.profiles.MapMarkerProfile;
import com.wynntils.webapi.profiles.MusicProfile;
import com.wynntils.webapi.profiles.SeaskipperProfile;
import com.wynntils.webapi.profiles.ServerProfile;
import com.wynntils.webapi.profiles.TerritoryProfile;
import com.wynntils.webapi.profiles.UpdateProfile;
import com.wynntils.webapi.profiles.guild.GuildProfile;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;
import com.wynntils.webapi.profiles.music.MusicLocationsProfile;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WebManager {

    public static final File API_CACHE_ROOT = new File(Reference.MOD_STORAGE_ROOT, "apicache");

    private static @Nullable WebReader apiUrls;

    private static HashMap<String, TerritoryProfile> territories = new HashMap<>();
    private static HashMap<String, CustomColor> guildColors = new HashMap<>();
    private static UpdateProfile updateProfile;
    private static boolean ignoringJoinUpdate = false;

    private static HashMap<String, ItemProfile> items = new HashMap<>();
    private static Collection<ItemProfile> directItems = new ArrayList<>();
    private static HashMap<String, String> translatedReferences = new HashMap<>();
    private static HashMap<String, String> internalIdentifications = new HashMap<>();
    private static HashMap<ItemType, String[]> materialTypes = new HashMap<>();
    private static HashMap<String, ItemGuessProfile> itemGuesses = new HashMap<>();
    private static HashMap<String, MajorIdentification> majorIds = new HashMap<>();

    private static HashMap<String, IngredientProfile> ingredients = new HashMap<>();
    private static Collection<IngredientProfile> directIngredients = new ArrayList<>();
    private static HashMap<String, String> ingredientHeadTextures = new HashMap<>();

    private static ArrayList<MapMarkerProfile> mapMarkers = new ArrayList<>();
    private static ArrayList<MapLabelProfile> mapLabels = new ArrayList<>();
    private static ArrayList<LocationProfile> npcLocations = new ArrayList<>();

    private static PlayerStatsProfile playerProfile;
    private static HashMap<String, GuildProfile> guilds = new HashMap<>();
    private static String currentSplash = "";

    private static ArrayList<DiscoveryProfile> discoveries = new ArrayList<>();
    private static ArrayList<SeaskipperProfile> seaskipperLocations = new ArrayList<>();

    private static MusicLocationsProfile musicLocations = new MusicLocationsProfile();

    private static WynntilsAccount account = null;

    private static Gson gson = new Gson();

    private static Thread territoryUpdateThread;
    private static RequestHandler handler = new RequestHandler();

    private static final int REQUEST_TIMEOUT_MILLIS = 16000;

    private static final String USER_AGENT = String.format("Wynntils\\%s-%d (%s)",
                Reference.VERSION,
                Reference.BUILD_NUMBER,
                Reference.developmentEnvironment ? "dev" : "client");

    public static void reset() {
        apiUrls = null;

        territories = new HashMap<>();
        updateProfile = null;
        items = new HashMap<>();
        mapMarkers = new ArrayList<>();
        itemGuesses = new HashMap<>();
        playerProfile = null;
        guilds = new HashMap<>();

        account = null;

        updateTerritoryThreadStatus(false);
    }

    public static void setupWebApi(boolean withProgress) {
        if (apiUrls == null) {
            tryReloadApiUrls(false, true);
        }

        ProgressManager.ProgressBar progressBar;
        if (withProgress) {
            progressBar = ProgressManager.push("Loading data from " + (apiUrls != null ? "APIs" : "cache"), 0);
        } else {
            progressBar = null;
        }

        updateTerritories(handler);
        updateGuildColors(handler);
        updateItemList(handler);
        updateIngredientList(handler);
        updateMapLocations(handler);
        updateItemGuesses(handler);
        updatePlayerProfile(handler);
        updateDiscoveries(handler);
        updateMusicLocations(handler);
        updateSeaskipperLocations(handler);
        updateCurrentSplash();

        handler.dispatchAsync();

        if (progressBar != null) {
            ProgressManager.pop(progressBar);
        }

        if (isAthenaOnline())
            updateTerritoryThreadStatus(true);
    }

    public static void checkForUpdatesOnJoin() {
        if (ignoringJoinUpdate) {
            ignoringJoinUpdate = false;
            return;
        }
        checkForUpdates();
    }

    public static void checkForUpdates() {
        if (Reference.developmentEnvironment) {
            Reference.LOGGER.info("An update check would have occurred, but you are in a development environment.");
            return;
        }

        updateProfile = new UpdateProfile();
    }

    public static void skipJoinUpdate() {
        UpdateOverlay.ignore();
        ignoringJoinUpdate = true;
    }

    public static void setupUserAccount() {
        tryReloadApiUrls(false, true);
        account = new WynntilsAccount();
        account.login();
    }

    public static boolean isAthenaOnline() {
        return (account != null && account.isConnected());
    }

    public static WynntilsAccount getAccount() {
        return account;
    }

    public static RequestHandler getHandler() {
        return handler;
    }

    public static String getCurrentSplash() {
        return currentSplash;
    }

    public static HashMap<String, TerritoryProfile> getTerritories() {
        return territories;
    }

    public static HashMap<String, CustomColor> getGuildColors() {
        return guildColors;
    }

    public static HashMap<String, ItemProfile> getItems() {
        return items;
    }

    public static ArrayList<MapMarkerProfile> getMapMarkers() {
        return mapMarkers;
    }

    public static ArrayList<MapLabelProfile> getMapLabels() {
        return mapLabels;
    }

    public static ArrayList<LocationProfile> getNpcLocations() {
        return npcLocations;
    }

    public static Iterable<MapMarkerProfile> getNonIgnoredApiMarkers() {
        return mapMarkers.stream().filter(o -> !o.isIgnored()).collect(Collectors.toList());
    }

    public static UpdateProfile getUpdate() {
        return updateProfile;
    }

    public static HashMap<String, ItemGuessProfile> getItemGuesses() {
        return itemGuesses;
    }

    public static Collection<ItemProfile> getDirectItems() {
        return directItems;
    }

    public static HashMap<ItemType, String[]> getMaterialTypes() {
        return materialTypes;
    }

    public static ArrayList<DiscoveryProfile> getDiscoveries() {
        return discoveries;
    }

    public static MusicLocationsProfile getMusicLocations() {
        return musicLocations;
    }

    public static ArrayList<SeaskipperProfile> getSeaskipperLocations() {
        return seaskipperLocations;
    }

    public static Map<String, IngredientProfile> getIngredients() {
        return ingredients;
    }

    public static Collection<IngredientProfile> getDirectIngredients() {
        return directIngredients;
    }

    public static HashMap<String, String> getIngredientHeadTextures() {
        return ingredientHeadTextures;
    }

    public static String getTranslatedItemName(String name) {
        return translatedReferences.getOrDefault(name, name);
    }

    public static String getIDFromInternal(String id) {
        return internalIdentifications.get(id);
    }

    public static void updateTerritoryThreadStatus(boolean start) {
        if (start) {
            if (territoryUpdateThread == null) {
                territoryUpdateThread = new TerritoryUpdateThread("Territory Update Thread");
                territoryUpdateThread.start();
                return;
            }
            return;
        }
        if (territoryUpdateThread != null) {
            territoryUpdateThread.interrupt();
        }
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
    public static void updateTerritories(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Athena") + "/cache/get/territoryList";
        handler.addRequest(new Request(url, "territory")
                .cacheTo(new File(API_CACHE_ROOT, "territories.json"))
                .handleJsonObject(json -> {
                    if (!json.has("territories")) return false;

                    Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {
                    }.getType();

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
                    Gson gson = builder.create();

                    territories = gson.fromJson(json.get("territories"), type);
                    return true;
                })
        );
    }

    public static void updateGuildColors(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Athena") + "/cache/get/guildListWithColors";
        handler.addRequest(new Request(url, "guildColors")
                .cacheTo(new File(API_CACHE_ROOT, "guildColors.json"))
                .handleJsonObject(json -> {
                    if (!json.has("0")) return false;
                    // json is {"0": {data}}, {"1": {data}}, etc.
                    // we need to convert it to {data}, {data}, etc.
                    guildColors = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                        JsonObject data = entry.getValue().getAsJsonObject();
                        // data is now {"_id":"Kingdom Foxes","prefix":"Fox","color":"#FF8200"}

                        String colorString = entry.getValue().getAsJsonObject().get("color").getAsString();
                        if (colorString.length() != 7 && colorString.length() != 4 && colorString.length() != 3) continue;

                        guildColors.put( // name, CustomColor
                                entry.getValue().getAsJsonObject().get("_id").getAsString(),
                                CustomColor.fromString(colorString, 0f)
                        );
                    }
                    return true;
                })
        );
    }

    public static void updateCurrentSplash() {
        if (apiUrls == null || apiUrls.getList("Splashes") == null) return;

        List<String> splashes = apiUrls.getList("Splashes");
        currentSplash = splashes.get(Utils.getRandom().nextInt(splashes.size()));
    }

    /**
     * Request all guild names to WynnAPI
     *
     * @return a {@link ArrayList} containing all guild names, or an empty list if an error occurred
     */
    public static List<String> getGuilds() {
        class ResultHolder {
            private ArrayList<String> result;
        }
        ResultHolder resultHolder = new ResultHolder();

        if (apiUrls == null) return Collections.emptyList();
        String url = apiUrls.get("GuildList");
        handler.addRequest(new Request(url, "guild_list")
                .cacheTo(new File(API_CACHE_ROOT, "guilds.json"))
                .handleJsonObject(json -> {
                    if (!json.has("guilds")) return false;
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    resultHolder.result = gson.fromJson(json.get("guilds"), type);
                    return true;
                })
        );
        handler.dispatch();

        if (resultHolder.result == null) {
            return new ArrayList<>();
        }
        return resultHolder.result;
    }

    /**
     * Request a guild info to WynnAPI
     *
     * @param guild Name of the guild
     * @return A wrapper for all guild info
     * @throws IOException thrown by URLConnection
     */
    public static GuildProfile getGuildProfile(String guild) throws IOException {
        if (apiUrls == null) return null;

        URLConnection st = new URL(apiUrls.get("GuildInfo") + Utils.encodeUrl(guild)).openConnection();
        st.setRequestProperty("User-Agent", USER_AGENT);
        st.setRequestProperty("apikey", apiUrls.get("WynnApiKey"));
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject obj = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

        if (obj.has("error")) {
            return null;
        }

        return gson.fromJson(obj, GuildProfile.class);
    }

    /**
     * Request the current leaderboard to Athena
     *
     * @param onReceive Consumes a hashmap containing as key the profession type and as value the Leaderboard Data
     * @see LeaderboardProfile
     */
    public static void getLeaderboard(Consumer<HashMap<UUID, LeaderboardProfile>> onReceive) {
        if (apiUrls == null || !isAthenaOnline()) return;
        String url = apiUrls.get("Athena") + "/cache/get/leaderboard";

        handler.addAndDispatch(
                new Request(url, "leaderboard")
                        .handleJsonObject((json) -> {
                            HashMap<UUID, LeaderboardProfile> result = new HashMap<>();

                            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                                result.put(UUID.fromString(entry.getKey()), gson.fromJson(entry.getValue(), LeaderboardProfile.class));
                            }

                            onReceive.accept(result);
                            return true;
                        })
                , true);
    }

    /**
     * Request the server list to Athena
     *
     * @param onReceive Consumes a hashmap containing as key the server name and as value the ServerProfile
     * @see ServerProfile
     */
    public static void getServerList(Consumer<HashMap<String, ServerProfile>> onReceive) {
        if (apiUrls == null || !isAthenaOnline()) return;
        String url = apiUrls.get("Athena") + "/cache/get/serverList";

        handler.addAndDispatch(
                new Request(url, "serverList")
                        .handleJsonObject((con, json) -> {
                            JsonObject servers = json.getAsJsonObject("servers");
                            HashMap<String, ServerProfile> result = new HashMap<>();

                            long serverTime = Long.parseLong(con.getHeaderField("timestamp"));
                            for (Map.Entry<String, JsonElement> entry : servers.entrySet()) {
                                ServerProfile profile = gson.fromJson(entry.getValue(), ServerProfile.class);
                                profile.matchTime(serverTime);

                                result.put(entry.getKey(), profile);
                            }

                            onReceive.accept(result);
                            return true;
                        })
                , true);
    }

    /**
     * Request all online players to WynnAPI
     *
     * @return a {@link HashMap} who the key is the server and the value is an array containing all players on it
     * @throws IOException thrown by URLConnection
     */
    public static HashMap<String, List<String>> getOnlinePlayers() throws IOException {
        if (apiUrls == null) return new HashMap<>();

        URLConnection st = new URL(apiUrls.get("OnlinePlayers")).openConnection();
        st.setRequestProperty("User-Agent", USER_AGENT);
        st.setRequestProperty("apikey", apiUrls.get("WynnApiKey"));
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        if (!main.has("message")) {
            main.remove("request");

            Type type = new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {
            }.getType();

            return gson.fromJson(main, type);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Update all Wynn items on the {@link HashMap} items
     */
    public static void updateItemList(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Athena") + "/cache/get/itemList";
        handler.addRequest(new Request(url, "itemList")
                .cacheTo(new File(API_CACHE_ROOT, "item_list.json"))
                .cacheMD5Validator(() -> getAccount().getMD5Verification("itemList"))
                .handleJsonObject(j -> {
                    translatedReferences = gson.fromJson(j.getAsJsonObject("translatedReferences"), HashMap.class);
                    internalIdentifications = gson.fromJson(j.getAsJsonObject("internalIdentifications"), HashMap.class);
                    Type majorIdsType = new TypeToken<HashMap<String, MajorIdentification>>() {
                    }.getType();
                    majorIds = gson.fromJson(j.getAsJsonObject("majorIdentifications"), majorIdsType);
                    Type materialTypesType = new TypeToken<HashMap<ItemType, String[]>>() {
                    }.getType();
                    materialTypes = gson.fromJson(j.getAsJsonObject("materialTypes"), materialTypesType);
                    IdentificationOrderer.INSTANCE = gson.fromJson(j.getAsJsonObject("identificationOrder"), IdentificationOrderer.class);

                    ItemProfile[] gItems = gson.fromJson(j.getAsJsonArray("items"), ItemProfile[].class);

                    HashMap<String, ItemProfile> citems = new HashMap<>();
                    for (ItemProfile prof : gItems) {
                        prof.getStatuses().forEach((n, p) -> p.calculateMinMax(n));
                        prof.addMajorIds(majorIds);
                        citems.put(prof.getDisplayName(), prof);
                    }

                    citems.values().forEach(ItemProfile::registerIdTypes);

                    directItems = citems.values();
                    items = citems;

                    return true;
                })
        );
    }

    /**
     * Update all Wynn ingredients on the {@link HashMap} ingredients
     */
    public static void updateIngredientList(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Athena") + "/cache/get/ingredientList";
        handler.addRequest(new Request(url, "ingredientList")
                .cacheTo(new File(API_CACHE_ROOT, "ingredient_list.json"))
                .cacheMD5Validator(() -> getAccount().getMD5Verification("ingredientList"))
                .handleJsonObject(j -> {
                    ingredientHeadTextures = gson.fromJson(j.getAsJsonObject("headTextures"), HashMap.class);

                    IngredientProfile[] gItems = gson.fromJson(j.getAsJsonArray("ingredients"), IngredientProfile[].class);
                    HashMap<String, IngredientProfile> cingredients = new HashMap<>();

                    for (IngredientProfile prof : gItems) {
                        cingredients.put(prof.getDisplayName(), prof);
                    }

                    ingredients = cingredients;
                    directIngredients = cingredients.values();

                    return true;
                })
        );
    }

    /**
     * Update all Wynn MapLocation on the {@link HashMap} mapMarkers and {@link HashMap} mapLabels
     */
    public static void updateMapLocations(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Athena") + "/cache/get/mapLocations";
        handler.addRequest(new Request(url, "map_locations")
                .cacheTo(new File(API_CACHE_ROOT, "map_locations.json"))
                .cacheMD5Validator(() -> getAccount().getMD5Verification("mapLocations"))
                .handleJsonObject(main -> {
                    JsonArray locationArray = main.getAsJsonArray("locations");
                    Type locationType = new TypeToken<ArrayList<MapMarkerProfile>>() {
                    }.getType();

                    mapMarkers = gson.fromJson(locationArray, locationType);
                    mapMarkers.removeIf(m -> m.getName().equals("~~~~~~~~~") && m.getIcon().equals(""));
                    mapMarkers.forEach(MapMarkerProfile::ensureNormalized);

                    JsonArray labelArray = main.getAsJsonArray("labels");
                    Type labelType = new TypeToken<ArrayList<MapLabelProfile>>() {
                    }.getType();

                    mapLabels = gson.fromJson(labelArray, labelType);

                    JsonArray npcLocationArray = main.getAsJsonArray("npc-locations");
                    Type npcLocationType = new TypeToken<ArrayList<LocationProfile>>() {
                    }.getType();

                    npcLocations = gson.fromJson(npcLocationArray, npcLocationType);

                    MapApiIcon.resetApiMarkers();
                    return true;
                })
        );
    }

    /**
     * Update all Wynn ItemGuesses on the {@link HashMap} itemGuesses
     */
    public static void updateItemGuesses(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("ItemGuesses");
        handler.addRequest(new Request(url, "item_guesses")
                .cacheTo(new File(API_CACHE_ROOT, "item_guesses.json"))
                .handleJsonObject(json -> {
                    Type type = new TypeToken<HashMap<String, ItemGuessProfile>>() {
                    }.getType();

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeHierarchyAdapter(HashMap.class, new ItemGuessProfile.ItemGuessDeserializer());
                    Gson gson = gsonBuilder.create();

                    itemGuesses = new HashMap<>(gson.fromJson(json, type));
                    return true;
                })
        );
    }

    public static void updatePlayerProfile(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("PlayerStatsv2") + McIf.mc().getSession().getProfile().getId() + "/stats";
        handler.addRequest(new Request(url, "player_profile")
                .cacheTo(new File(API_CACHE_ROOT, "player_stats.json"))
                .addHeader("apikey", apiUrls.get("WynnApiKey"))
                .handleJsonObject(json -> {
                    Type type = new TypeToken<PlayerStatsProfile>() {
                    }.getType();

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(type, new PlayerStatsProfile.PlayerStatsProfileDeserializer());
                    Gson gson = gsonBuilder.create();

                    playerProfile = gson.fromJson(json, type);
                    return true;
                })
        );
    }

    public static void updateDiscoveries(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Discoveries");
        handler.addRequest(new Request(url, "discoveries")
                .cacheTo(new File(API_CACHE_ROOT, "discoveries.json"))
                .handleJsonArray(discoveriesJson -> {
                    Type type = new TypeToken<ArrayList<DiscoveryProfile>>() {
                    }.getType();

                    discoveries = gson.fromJson(discoveriesJson, type);
                    return true;
                }));
    }

    public static void updateMusicLocations(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("MusicLocations");
        handler.addRequest(new Request(url, "musicLocations")
                .cacheTo(new File(API_CACHE_ROOT, "musicLocations.json"))
                .handleJsonObject(json -> {
                    musicLocations = gson.fromJson(json, MusicLocationsProfile.class);
                    return true;
                })
        );
    }

    public static void updateSeaskipperLocations(RequestHandler handler) {
        if (apiUrls == null) return;
        String url = apiUrls.get("Seaskipper");
        handler.addRequest(new Request(url, "seaskipper")
                .cacheTo(new File(API_CACHE_ROOT, "seaskipper.json"))
                .handleJsonArray(seaskipperJson -> {
                    Type type = new TypeToken<ArrayList<SeaskipperProfile>>() {
                    }.getType();

                    seaskipperLocations = gson.fromJson(seaskipperJson, type);
                    return true;
                }));
    }

    public static HashMap<String, String> getUpdateData(UpdateStream stream) throws IOException {
        String streamName = stream == UpdateStream.STABLE ? "re" : "ce";
        URLConnection st = new URL(apiUrls.get("Athena") + "/version/latest/" + streamName).openConnection();
        st.setRequestProperty("User-Agent", USER_AGENT);
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return new HashMap<String, String>() {{
            put("version", main.get("version").getAsString());
            put("url", main.get("url").getAsString());
            put("md5", main.get("md5").getAsString());
            put("changelog", main.get("changelog").getAsString());
        }};
    }

    public static ArrayList<MusicProfile> getCurrentAvailableSongs() throws IOException {
        URLConnection st = new URL(apiUrls.get("WynnSounds")).openConnection();
        st.setRequestProperty("User-Agent", USER_AGENT);
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        ArrayList<MusicProfile> result = new ArrayList<>();
        JsonArray array = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            if (!obj.has("name") || !obj.has("download_url") || !obj.has("size")) continue;

            result.add(new MusicProfile(obj.get("name").getAsString(), obj.get("download_url").getAsString(), obj.get("size").getAsLong()));
        }

        return result;
    }

    public static boolean blockHeroBetaStable() {
        if (apiUrls == null) return true;
        return apiUrls.get("BlockHeroBetaStable").equalsIgnoreCase("true");
    }

    public static boolean warnHeroBetaStable() {
        if (apiUrls == null) return true;
        return apiUrls.get("WarnHeroBetaStable").equalsIgnoreCase("true");
    }

    public static boolean blockHeroBetaCuttingEdge() {
        if (apiUrls == null) return true;
        return apiUrls.get("BlockHeroBetaCuttingEdge").equalsIgnoreCase("true");
    }

    public static boolean warnHeroBetaCuttingEdge() {
        if (apiUrls == null) return true;
        return apiUrls.get("WarnHeroBetaCuttingEdge").equalsIgnoreCase("true");
    }

    /**
     * Attempt to store an {@link InputStream} to a file on disk
     *
     * @param stream   The {@link InputStream} to read
     * @param fileName The filename to save to (file saved in /apicache directory)
     * @return A {@link InputStream} for the saved result
     */
    public static JsonElement handleCache(InputStream stream, String fileName, boolean forceRecall) {
        File apiCacheFolder = new File(Reference.MOD_STORAGE_ROOT.getPath(), "apicache");
        File apiCacheFile = new File(apiCacheFolder.getPath(), fileName);
        if (!forceRecall) {
            try {
                if (!apiCacheFolder.exists())
                    apiCacheFolder.mkdirs();
                if (!apiCacheFile.exists())
                    apiCacheFile.createNewFile();
                String raw = IOUtils.toString(stream, StandardCharsets.UTF_8);
                stream.close();
                JsonElement element = new JsonParser().parse(raw);
                FileUtils.writeStringToFile(apiCacheFile, raw, StandardCharsets.UTF_8);
                return element;
            } catch (Exception ex) {
                Reference.LOGGER.warn("Error running API request result for file " + fileName + " - attempting to use cache", ex);
            }
        }
        if (!apiCacheFolder.exists() || !apiCacheFolder.isDirectory() || !apiCacheFile.exists() || apiCacheFile.isDirectory()) {
            Reference.LOGGER.error("API cache file " + fileName + " doesn't exist");
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(apiCacheFile)) {
            JsonElement element = new JsonParser().parse(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
            Reference.LOGGER.info("Successfully loaded cache file " + fileName);
            return element;
        } catch (Exception ex) {
            Reference.LOGGER.error("Unable to load cache file " + fileName, ex);
            return null;
        }
    }

    public static class TerritoryUpdateThread extends Thread {
        public TerritoryUpdateThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            RequestHandler handler = new RequestHandler();

            try {
                Thread.sleep(30000);
                while (!isInterrupted()) {
                    HashMap<String, TerritoryProfile> prevList = new HashMap<>(territories);
                    updateTerritories(handler);
                    handler.dispatch();
                    for (TerritoryProfile prevTerritory : prevList.values()) {
                        TerritoryProfile currentTerritory = territories.get(prevTerritory.getName());
                        // TODO: Find out why sometimes the Unicode apostrophes are turned into unknown
                        // Unicode characters
                        // It seems to happen at random and seems to only happen to 1 territory at a
                        // time and when it does both the field name and the territory name field inside
                        // is turned into unknown Unicode characters (might have something to do with
                        // the list of territories on the server)
                        if (currentTerritory == null) {
                            continue;
                        } else if (!currentTerritory.getGuild().equals(prevTerritory.getGuild())) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getFriendlyName(), currentTerritory.getGuild(), prevTerritory.getGuild(), getGuildTagFromName(currentTerritory.getGuild()), getGuildTagFromName(prevTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.CAPTURED));
                        } else if (prevTerritory.getAttacker() == null && currentTerritory.getAttacker() != null) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getFriendlyName(), currentTerritory.getAttacker(), prevTerritory.getGuild(), getGuildTagFromName(currentTerritory.getAttacker()), getGuildTagFromName(prevTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.ATTACKED));
                        } else if (prevTerritory.getAttacker() != null && currentTerritory.getAttacker() == null) {
                            FrameworkManager.getEventBus().post(new WynnGuildWarEvent(prevTerritory.getFriendlyName(), prevTerritory.getAttacker(), currentTerritory.getGuild(), getGuildTagFromName(prevTerritory.getAttacker()), getGuildTagFromName(currentTerritory.getGuild()), WynnGuildWarEvent.WarUpdateType.DEFENDED));
                        }
                    }
                    Thread.sleep(30000);
                }
            } catch (InterruptedException ignored) {
            }
            Reference.LOGGER.info("Terminating territory update thread.");
        }
    }

    private static final Comparator<String> SEM_VER_COMPARATOR = (a, b) -> {
        String[] aParts = StringUtils.split(a, '.');
        String[] bParts = StringUtils.split(b, '.');
        for (int i = 0, sz = Math.min(aParts.length, bParts.length); i < sz; ++i) {
            String aPartS = aParts[i];
            String bPartS = bParts[i];
            boolean aValid = !aPartS.startsWith("-") && Utils.StringUtils.isValidInteger(aPartS);
            boolean bValid = !bPartS.startsWith("-") && Utils.StringUtils.isValidInteger(bPartS);
            if (!aValid || !bValid) {
                return aValid ? +1 : bValid ? -1 : 0;
            }
            int aPart = Integer.parseInt(aPartS);
            int bPart = Integer.parseInt(bPartS);
            if (aPart != bPart) {
                return aPart - bPart;
            }
        }
        return aParts.length - bParts.length;
    };

    /**
     * Fetches the changelog from the API for the current version of Wynntils
     *
     * @return an ArrayList of ChangelogProfile's
     */
    public static Map<String, String> getChangelog(boolean forceLatest) {
        if (apiUrls == null) return null;

        String version = "v" + Reference.VERSION;

        if (forceLatest) {
            version = "latest";
        }

        String url = apiUrls.get("Athena") + "/version/changelog/" + version;
        Reference.LOGGER.info("Requesting changelog from " + url);
        try {
            URLConnection st = new URL(url).openConnection();
            st.setRequestProperty("User-Agent", USER_AGENT);
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

            JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

            return new HashMap<String, String>() {{
                put("version", main.get("version").getAsString());
                put("changelog", main.get("changelog").getAsString());
            }};
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Reference.LOGGER.warn("Error while fetching changelog");
        return null;
    }

    /**
     * Tries to reload apiUrls if it was null
     */
    public static void tryReloadApiUrls(boolean async) {
        tryReloadApiUrls(async, false);
    }

    private static void tryReloadApiUrls(boolean async, boolean inSetup) {
        if (apiUrls == null) {
            handler.addRequest(new Request("https://api.wynntils.com/webapi", "webapi")
                    .cacheTo(new File(API_CACHE_ROOT, "webapi.txt"))
                    .handleWebReader(reader -> {
                        apiUrls = reader;
                        if (!inSetup) {
                            WebManager.setupWebApi(false);
                            MapModule.getModule().getMainMap().updateMap();
                        }
                        return true;
                    })
            );
            if (async) {
                handler.dispatchAsync();
            } else {
                handler.dispatch();
            }
        }

    }

    /**
     * @return all api locations
     */
    public static @Nullable WebReader getApiUrls() {
        return apiUrls;
    }

    public static String getApiUrl(String key) {
        if (apiUrls == null) return null;

        return apiUrls.get(key);
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}
