/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.util.UUIDTypeAdapter;
import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynnGuildWarEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.overlays.objects.MapApiIcon;
import com.wynntils.webapi.account.WynntilsAccount;
import com.wynntils.webapi.profiles.MapMarkerProfile;
import com.wynntils.webapi.profiles.MusicProfile;
import com.wynntils.webapi.profiles.TerritoryProfile;
import com.wynntils.webapi.profiles.UpdateProfile;
import com.wynntils.webapi.profiles.guild.GuildProfile;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile;
import net.minecraftforge.fml.common.ProgressManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WebManager {

    private static @Nullable WebReader apiUrls;

    private static HashMap<String, TerritoryProfile> territories = new HashMap<>();
    private static UpdateProfile updateProfile;
    private static boolean ignoringJoinUpdate = false;
    private static HashMap<String, ItemProfile> items = new HashMap<>();
    private static ArrayList<ItemProfile> directItems = new ArrayList<>();
    private static ArrayList<MapMarkerProfile> mapMarkers = new ArrayList<>();
    private static ArrayList<MapMarkerProfile> refineryMapMarkers = new ArrayList<>();
    private static HashMap<String, ItemGuessProfile> itemGuesses = new HashMap<>();
    private static PlayerStatsProfile playerProfile;
    private static HashMap<String, GuildProfile> guilds = new HashMap<>();
    private static String currentSplash = "";

    private static ArrayList<UUID> helpers = new ArrayList<>();
    private static ArrayList<UUID> moderators = new ArrayList<>();
    private static ArrayList<UUID> content_team = new ArrayList<>();
    private static ArrayList<UUID> donators = new ArrayList<>();

    private static ArrayList<UUID> ears = new ArrayList<>();
    private static ArrayList<UUID> elytras = new ArrayList<>();
    private static ArrayList<UUID> capes = new ArrayList<>();

    private static WynntilsAccount account = null;

    private static Gson gson = new Gson();

    private static Thread territoryUpdateThread;
    private static WebRequestHandler handler = new WebRequestHandler();

    private static final int REQUEST_TIMEOUT_MILLIS = 16000;
    private static final File apiCacheFolder = new File(Reference.MOD_STORAGE_ROOT.getPath(), "apicache");

    public static void reset() {
        apiUrls = null;

        territories = new HashMap<>();
        updateProfile = null;
        items = new HashMap<>();
        mapMarkers = new ArrayList<>();
        refineryMapMarkers = new ArrayList<>();
        itemGuesses = new HashMap<>();
        playerProfile = null;
        guilds = new HashMap<>();

        helpers = new ArrayList<>();
        moderators = new ArrayList<>();
        content_team = new ArrayList<>();

        ears = new ArrayList<>();
        elytras = new ArrayList<>();
        capes = new ArrayList<>();

        account = null;

        updateTerritoryThreadStatus(false);
    }

    public static void setupWebApi(boolean withProgress) {
        if (apiUrls == null) {
            tryReloadApiUrls(false, true);
        }


        ProgressManager.ProgressBar progressBar = withProgress ? ProgressManager.push(apiUrls != null ? "Loading data from APIs" : "Loading data from cache", 0) : null;

        updateTerritories(handler);
        updateUsersRoles(handler);
        updateUsersModels(handler);
        updateItemList(handler);
        updateMapMarkers(handler);
        updateMapRefineries(handler);
        updateItemGuesses(handler);
        updatePlayerProfile(handler);
        updateCurrentSplash();

        handler.dispatchAsync();

        if (withProgress) ProgressManager.pop(progressBar);

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

    public static WynntilsAccount getAccount() {
        return account;
    }

    public static String getCurrentSplash() {
        return currentSplash;
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

    public static ArrayList<MapMarkerProfile> getRefineryMapMarkers() {
        return refineryMapMarkers;
    }

    public static Iterable<MapMarkerProfile> getApiMarkers() {
        return Iterables.concat(mapMarkers, refineryMapMarkers);
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

    public static boolean isContentTeam(UUID uuid) { return content_team.contains(uuid); }

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

    public static void updateTerritoryThreadStatus(boolean start) {
        if(start) {
            if(territoryUpdateThread == null) {
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
    public static void updateTerritories(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("Territory");
        handler.addRequest(new WebRequestHandler.Request(url, "territory")
            .cacheTo(new File(apiCacheFolder, "territories.json"))
            .handleJsonObject(json -> {
                if (!json.has("territories")) return false;

                Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {}.getType();

                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeHierarchyAdapter(TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
                Gson gson = builder.create();

                territories.clear();
                territories.putAll(gson.fromJson(json.get("territories"), type));
                return true;
            })
        );
    }

    public static void updateCurrentSplash() {
        if(apiUrls == null || apiUrls.getList("Splashes") == null) return;

        List<String> splashes = apiUrls.getList("Splashes");
        currentSplash = splashes.get(Utils.getRandom().nextInt(splashes.size()));
    }

    /**
     * Request all guild names to WynnAPI
     *
     * @return a {@link ArrayList} containing all guild names, or an empty list if an error occurred
     */
    public static ArrayList<String> getGuilds()  {
        class ResultHolder {
            private ArrayList<String> result;
        }
        ResultHolder resultHolder = new ResultHolder();

        String url = apiUrls == null ? null : apiUrls.get("GuildList");
        handler.addRequest(new WebRequestHandler.Request(url, "guild_list")
            .cacheTo(new File(apiCacheFolder, "guilds.json"))
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
     *
     * @return A wrapper for all guild info
     * @throws IOException thrown by URLConnection
     */
    public static GuildProfile getGuildProfile(String guild) throws IOException {
        if (apiUrls == null) return null;

        URLConnection st = new URL(apiUrls.get("GuildInfo") + URLEncoder.encode(guild, "UTF-8")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OSX10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject obj = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

        if(obj.has("error")) {
            return null;
        }

        return gson.fromJson(obj, GuildProfile.class);
    }

    /**
     * Request all online players to WynnAPI
     *
     * @return a {@link HashMap} who the key is the server and the value is an array containing all players on it
     * @throws IOException thrown by URLConnection
     */
    public static HashMap<String, ArrayList<String>> getOnlinePlayers() throws IOException {
        if (apiUrls == null) return new HashMap<>();

        URLConnection st = new URL(apiUrls.get("OnlinePlayers")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        main.remove("request");

        Type type = new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {
        }.getType();

        return gson.fromJson(main, type);
    }

    /**
     * Update all Wynn items on the {@link HashMap} items
     */
    public static void updateItemList(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("ItemList");
        handler.addRequest(new WebRequestHandler.Request(url, "item_list")
            .cacheTo(new File(apiCacheFolder, "items.json"))
            .cacheMD5Validator(() -> getAccount().getMD5Verification("itemList"))
            .handleJsonObject(j -> {
                if (!j.has("items") || !j.get("items").isJsonArray()) return false;
                JsonArray main = j.getAsJsonArray("items");

                Type type = new TypeToken<HashMap<String, ItemProfile>>() {
                }.getType();

                HashMap<String, ItemProfile> citems = ItemProfile.GSON.fromJson(main, type);
                directItems.addAll(citems.values());

                items = citems;
                return true;
            })
        );
    }

    /**
     * Update all Wynn MapMarkers on the {@link HashMap} mapMarkers
     */
    public static void updateMapMarkers(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("MapMarkers");
        handler.addRequest(new WebRequestHandler.Request(url, "map_markers")
            .cacheTo(new File(apiCacheFolder, "map_markers.json"))
            .cacheMD5Validator(() -> getAccount().getMD5Verification("mapLocations"))
            .handleJsonObject(main -> {
                JsonArray jsonArray = main.getAsJsonArray("locations");
                Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {
                }.getType();

                mapMarkers = gson.fromJson(jsonArray, type);
                mapMarkers.removeIf(m -> m.getName().equals("~~~~~~~~~") && m.getIcon().equals(""));
                mapMarkers.forEach(MapMarkerProfile::ensureNormalized);
                MapApiIcon.resetApiMarkers();
                return true;
            })
        );
    }

    /**
     * Update all Refineries MapMarkers on the {@link HashMap} mapMarkers
     */
    public static void updateMapRefineries(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("RefineryLocations");
        handler.addRequest(new WebRequestHandler.Request(url, "map_markers.refineries")
            .cacheTo(new File(apiCacheFolder, "map_refineries.json"))
            .handleJson(j -> {
                if (!j.isJsonArray()) return false;
                JsonArray jsonArray = j.getAsJsonArray();

                Type type = new TypeToken<ArrayList<MapMarkerProfile>>() {}.getType();

                refineryMapMarkers = gson.fromJson(jsonArray, type);
                refineryMapMarkers.forEach(MapMarkerProfile::ensureNormalized);
                MapApiIcon.resetApiMarkers();
                return true;
            })
        );
    }

    /**
     * Update all Wynn ItemGuesses on the {@link HashMap} itemGuesses
     */
    public static void updateItemGuesses(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("ItemGuesses");
        handler.addRequest(new WebRequestHandler.Request(url, "item_guesses")
            .cacheTo(new File(apiCacheFolder, "item_guesses.json"))
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

    public static void updatePlayerProfile(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("PlayerStatsv2") + ModCore.mc().getSession().getProfile().getId() + "/stats";
        handler.addRequest(new WebRequestHandler.Request(url, "player_profile")
            .cacheTo(new File(apiCacheFolder, "player_stats.json"))
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

    public static void updateUsersRoles(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("UserAccount") + "getUsersRoles";
        handler.addRequest(new WebRequestHandler.Request(url, "user_account.roles")
            .cacheTo(new File(apiCacheFolder, "user_roles.json"))
            .handleJsonObject(main -> {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeHierarchyAdapter(UUID.class, new UUIDTypeAdapter());
                Gson gson = builder.create();

                Type type = new TypeToken<ArrayList<UUID>>() {
                }.getType();

                JsonArray helper = main.getAsJsonArray("helperUsers");
                helpers = gson.fromJson(helper, type);

                JsonArray moderator = main.getAsJsonArray("moderatorUsers");
                moderators = gson.fromJson(moderator, type);

                JsonArray contentTeam = main.getAsJsonArray("contentTeamUsers");
                content_team = gson.fromJson(contentTeam, type);

                JsonArray donator = main.getAsJsonArray("donatorUsers");
                donators = gson.fromJson(donator, type);
                return true;
            })
        );
    }

    public static void updateUsersModels(WebRequestHandler handler) {
        String url = apiUrls == null ? null : apiUrls.get("UserAccount") + "getUserModels";
        handler.addRequest(new WebRequestHandler.Request(url, "user_account.models")
            .cacheTo(new File(apiCacheFolder, "user_models.json"))
            .handleJsonObject(main -> {
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
                return true;
            })
        );
    }

    public static String getStableJarFileUrl() throws IOException {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return apiUrls.get("Jars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static String getStableJarFileMD5() throws IOException {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json?depth=2&tree=fingerprint[fileName,hash]{0,}").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return main.getAsJsonArray("fingerprint").get(0).getAsJsonObject().get("hash").getAsString();
    }

    public static String getStableJarVersion() throws IOException {
        URLConnection st = new URL(apiUrls.get("Jars") + "api/json?tree=artifacts[fileName]").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return main.getAsJsonObject().get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("fileName").getAsString().split("_")[0].split("-")[1];
    }

    public static String getCuttingEdgeJarFileUrl() throws IOException {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return apiUrls.get("DevJars") + "artifact/" + main.getAsJsonArray("artifacts").get(0).getAsJsonObject().get("relativePath").getAsString();
    }

    public static String getCuttingEdgeJarFileMD5() throws IOException {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json?depth=2&tree=fingerprint[fileName,hash]{0,}").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return main.getAsJsonArray("fingerprint").get(0).getAsJsonObject().get("hash").getAsString();
    }

    public static int getCuttingEdgeBuildNumber() throws IOException {
        URLConnection st = new URL(apiUrls.get("DevJars") + "api/json?tree=number").openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        JsonObject main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        return main.getAsJsonObject().get("number").getAsInt();
    }

    public static ArrayList<MusicProfile> getCurrentAvailableSongs() throws IOException {
        URLConnection st = new URL(apiUrls.get("WynnSounds")).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        ArrayList<MusicProfile> result = new ArrayList<>();
        JsonArray array = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
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
        try {
            FileInputStream inputStream = new FileInputStream(apiCacheFile);
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
            WebRequestHandler handler = new WebRequestHandler();

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
            } catch (InterruptedException ignored) {}
            Reference.LOGGER.info("Terminating territory update thread.");
        }
    }

    /**
     * Fetches a hand written changelog from the Wynntils API (if download stream is set to stable)
     * Fetches current build changes from Jenkins (Wynntils-DEV)
     *
     * @return an ArrayList of ChangelogProfile's
     */
    public static ArrayList<String> getChangelog(boolean major) {
        if (apiUrls == null) return null;

        JsonObject main = null;
        boolean failed = false;

        if (major) {
            try {
                URLConnection st = new URL(apiUrls.get("Changelog")).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
                st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
                main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                failed = true;
            }

            if (failed) {
                Reference.LOGGER.warn("Error while fetching changelog");
                return null;
            }

            Type type = new TypeToken<ArrayList<String>>() { }.getType();
            return gson.fromJson(main.getAsJsonArray(Reference.VERSION), type);
        }

        ArrayList<String> changelog = new ArrayList<>();
        try {
            URLConnection st = new URL(apiUrls.get("DevJars") + "api/json?tree=changeSet[items[msg]]").openConnection();
            st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
            st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);
            if (st.getContentType().contains("application/json")) {
                main = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();

                JsonArray changesArray = main.getAsJsonObject().get("changeSet").getAsJsonObject().get("items").getAsJsonArray();
                for(int i = 0; i < changesArray.size(); i++) {
                    JsonObject obj = changesArray.get(i).getAsJsonObject();

                    changelog.add(obj.get("msg").getAsString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return changelog;
    }

    /**
     * Tries to reload apiUrls if it was null
     */
    public static void tryReloadApiUrls(boolean async) {
        tryReloadApiUrls(async, false);
    }

    private static void tryReloadApiUrls(boolean async, boolean inSetup) {
        if (apiUrls == null) {
            handler.addRequest(new WebRequestHandler.Request("https://api.wynntils.com/webapi", "webapi")
                .cacheTo(new File(apiCacheFolder, "webapi.txt"))
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
}
