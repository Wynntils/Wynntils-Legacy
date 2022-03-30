/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.richpresence.profiles;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.modules.richpresence.discordgamesdk.*;
import com.wynntils.modules.richpresence.discordgamesdk.converters.EnumConverter;
import com.wynntils.modules.richpresence.discordgamesdk.enums.*;
import com.wynntils.modules.richpresence.events.RPCJoinHandler;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RichProfile {

    private static final String GAME_SDK_VERSION = "v3.1.0";
    public static final File GAME_SDK_FILE = new File(new File(new File(Reference.PLATFORM_NATIVES_ROOT, "discord_game_sdk"), GAME_SDK_VERSION), System.mapLibraryName("discord_game_sdk"));
    public static final DefaultTypeMapper TYPE_MAPPER = new DefaultTypeMapper();
    public static final Map<String, Object> OPTIONS = new HashMap<>();

    private IDiscordCore discordCore = null;
    private OverlayManager overlayManager = null;
    private IDiscordActivityManager activityManager = null;
    private Thread shutdown = new Thread(this::disconnectRichPresence, "wynntils-disconnect-richpresence");

    private SecretContainer joinSecret = null;

    private DiscordActivity lastStructure = null;

    private boolean disabled = false;
    private boolean ready = false;
    private DiscordActivity activityToUseWhenReady = null;
    private boolean isBlankGuiOpen = false;

    private long applicationID = 0;

    public RichProfile(long id) {
        setupTypeMapper();
        if (WebManager.getApiUrls() == null) {
            return;
        }
        String apiRoot = WebManager.getApiUrls().get("RichPresenceRoot") + GAME_SDK_VERSION;
        String url = apiRoot + "/versioning.php";
        RequestHandler handler = new RequestHandler();

        handler.addRequest(new Request(url, "richpresence_versioning_" + GAME_SDK_VERSION)
                .cacheTo(new File(Reference.NATIVES_ROOT, "richpresence_versioning_" + GAME_SDK_VERSION + ".txt"))
                .handleWebReader(reader -> {
                    String md5 = reader.get(Platform.RESOURCE_PREFIX);
                    if (md5 != null && GAME_SDK_FILE.exists() && new MD5Verification(GAME_SDK_FILE).equals(md5)) {
                        setup(id);
                        return true;
                    }

                    if (md5 == null) {
                        // No sdk on this platform or webapi is down
                        setDisabled();
                        return true;
                    }

                    DownloaderManager.queueDownload("Discord Game SDK", apiRoot + "/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("discord_game_sdk"), RichProfile.GAME_SDK_FILE.getParentFile(), DownloadAction.SAVE, (success) -> {
                        if (!success) {
                            setDisabled();
                            return;
                        }

                        McIf.mc().addScheduledTask(() -> setup(id));
                    });
                    return true;
                }));
        handler.dispatchAsync();
    }

    private void setup(long id) {
        try {
            applicationID = id;
            DiscordGameSDKLibrary gameSDK = DiscordGameSDKLibrary.INSTANCE;

            IDiscordUserEvents.ByReference userEvents = new IDiscordUserEvents.ByReference();
            userEvents.on_current_user_update = eventData -> {
                if (WebManager.getAccount() == null) return;
                DiscordUser user = new DiscordUser();
                IDiscordUserManager userManager = discordCore.get_user_manager.apply(discordCore);
                userManager.get_current_user.apply(userManager, user);
                WebManager.getAccount().updateDiscord(Long.toString(user.id), bytesToString(user.username) + "#" + bytesToString(user.discriminator));
            };
            IDiscordActivityEvents.ByReference activityEvents = new IDiscordActivityEvents.ByReference();
            activityEvents.on_activity_join = new RPCJoinHandler();
            IDiscordOverlayEvents.ByReference overlayEvents = new IDiscordOverlayEvents.ByReference();
            overlayEvents.on_toggle = (callbackData, closedAsByte) -> {
                boolean opened = closedAsByte == 0;
                if (opened && McIf.mc().currentScreen == null) {
                    McIf.mc().displayGuiScreen(new GuiScreen() {
                        public void onGuiClosed() {
                            isBlankGuiOpen = false;
                        }
                    });
                    isBlankGuiOpen = true;
                } else if (!opened && isBlankGuiOpen) {
                    McIf.mc().displayGuiScreen(null);
                }
            };

            DiscordCreateParams createParams = new DiscordCreateParams();
            createParams.application_version = DiscordGameSDKLibrary.DISCORD_APPLICATION_MANAGER_VERSION;
            createParams.user_version = DiscordGameSDKLibrary.DISCORD_USER_MANAGER_VERSION;
            createParams.image_version = DiscordGameSDKLibrary.DISCORD_IMAGE_MANAGER_VERSION;
            createParams.activity_version = DiscordGameSDKLibrary.DISCORD_ACTIVITY_MANAGER_VERSION;
            createParams.relationship_version = DiscordGameSDKLibrary.DISCORD_RELATIONSHIP_MANAGER_VERSION;
            createParams.lobby_version = DiscordGameSDKLibrary.DISCORD_LOBBY_MANAGER_VERSION;
            createParams.network_version = DiscordGameSDKLibrary.DISCORD_NETWORK_MANAGER_VERSION;
            createParams.overlay_version = DiscordGameSDKLibrary.DISCORD_OVERLAY_MANAGER_VERSION;
            createParams.storage_version = DiscordGameSDKLibrary.DISCORD_STORAGE_MANAGER_VERSION;
            createParams.store_version = DiscordGameSDKLibrary.DISCORD_STORE_MANAGER_VERSION;
            createParams.voice_version = DiscordGameSDKLibrary.DISCORD_VOICE_MANAGER_VERSION;
            createParams.achievement_version = DiscordGameSDKLibrary.DISCORD_ACHIEVEMENT_MANAGER_VERSION;
            createParams.client_id = id;
            createParams.user_events = userEvents;
            createParams.activity_events = activityEvents;
            createParams.overlay_events = overlayEvents;
            createParams.flags = EDiscordCreateFlags.DiscordCreateFlags_NoRequireDiscord.getOrdinal();

            overlayManager = null;
            IDiscordCore.ByReference[] array = new IDiscordCore.ByReference[] { new IDiscordCore.ByReference() };
            EDiscordResult result = gameSDK.DiscordCreate(3, createParams, array);
            discordCore = array[0];
            if (discordCore == null || discordCore.get_user_manager == null || result != EDiscordResult.DiscordResult_Ok) {
                Reference.LOGGER.warn("[RichPresence] Unable co connect to Discord: " + result);
                // Discord client not running
                setDisabled();
                return;
            }
            // get_user_manager is needed so the current user update is fired
            discordCore.get_user_manager.apply(discordCore);
            activityManager = discordCore.get_activity_manager.apply(discordCore);
            getOverlayManager().overlayManager = discordCore.get_overlay_manager.apply(discordCore);

            Runtime.getRuntime().addShutdownHook(shutdown);
            ready = true;
            if (activityToUseWhenReady != null) {
                activityManager.update_activity.apply(activityManager, activityToUseWhenReady, null, null);
                activityToUseWhenReady = null;
            }
        } catch (UnsatisfiedLinkError e) {
            Reference.LOGGER.error("Unable to open Discord Game SDK Library.");
            e.printStackTrace();
            setDisabled();
        }
    }

    private void setupTypeMapper() {
        TYPE_MAPPER.addTypeConverter(EDiscordActivityActionType.class, new EnumConverter<>(EDiscordActivityActionType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordActivityJoinRequestReply.class, new EnumConverter<>(EDiscordActivityJoinRequestReply.class));
        TYPE_MAPPER.addTypeConverter(EDiscordActivityPartyPrivacy.class, new EnumConverter<>(EDiscordActivityPartyPrivacy.class));
        TYPE_MAPPER.addTypeConverter(EDiscordActivityType.class, new EnumConverter<>(EDiscordActivityType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordCreateFlags.class, new EnumConverter<>(EDiscordCreateFlags.class));
        TYPE_MAPPER.addTypeConverter(EDiscordEntitlementType.class, new EnumConverter<>(EDiscordEntitlementType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordImageType.class, new EnumConverter<>(EDiscordImageType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordInputModeType.class, new EnumConverter<>(EDiscordInputModeType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordKeyVariant.class, new EnumConverter<>(EDiscordKeyVariant.class));
        TYPE_MAPPER.addTypeConverter(EDiscordLobbySearchCast.class, new EnumConverter<>(EDiscordLobbySearchCast.class));
        TYPE_MAPPER.addTypeConverter(EDiscordLobbySearchComparison.class, new EnumConverter<>(EDiscordLobbySearchComparison.class));
        TYPE_MAPPER.addTypeConverter(EDiscordLobbySearchDistance.class, new EnumConverter<>(EDiscordLobbySearchDistance.class));
        TYPE_MAPPER.addTypeConverter(EDiscordLobbyType.class, new EnumConverter<>(EDiscordLobbyType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordLogLevel.class, new EnumConverter<>(EDiscordLogLevel.class));
        TYPE_MAPPER.addTypeConverter(EDiscordMouseButton.class, new EnumConverter<>(EDiscordMouseButton.class));
        TYPE_MAPPER.addTypeConverter(EDiscordPremiumType.class, new EnumConverter<>(EDiscordPremiumType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordRelationshipType.class, new EnumConverter<>(EDiscordRelationshipType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordResult.class, new EnumConverter<>(EDiscordResult.class));
        TYPE_MAPPER.addTypeConverter(EDiscordSkuType.class, new EnumConverter<>(EDiscordSkuType.class));
        TYPE_MAPPER.addTypeConverter(EDiscordStatus.class, new EnumConverter<>(EDiscordStatus.class));
        TYPE_MAPPER.addTypeConverter(EDiscordUserFlag.class, new EnumConverter<>(EDiscordUserFlag.class));

        OPTIONS.put("type-mapper", TYPE_MAPPER);
    }

    private void setDisabled() {
        activityToUseWhenReady = null;
        disabled = true;
    }

    /**
     * Cleans user current RichPresence
     */
    public void stopRichPresence() {
        if (disabled) return;
        if (ready) {
            activityManager.clear_activity.apply(activityManager, null, null);
        } else {
            activityToUseWhenReady = null;
        }
    }

    /**
     * update user RichPresence
     *
     * @param state
     *        RichPresence state string
     * @param details
     *        RichPresence details string
     * @param largText
     *        RichPresence large Text
     * @param date
     *        RichPresence Date
     */
    public void updateRichPresence(String state, String details, String largText, OffsetDateTime date) {
        if (disabled) return;
        DiscordActivity richPresence = new DiscordActivity();
        richPresence.state = toBytes(state, 128);
        richPresence.details = toBytes(details, 128);
        richPresence.application_id = applicationID;
        DiscordActivityAssets richPresenceAssets = new DiscordActivityAssets();
        richPresenceAssets.large_text = toBytes(largText, 128);
        richPresenceAssets.large_image = toBytes("wynn", 128);
        DiscordActivityTimestamps richPresenceTimestamps = new DiscordActivityTimestamps();
        richPresenceTimestamps.start = date.toInstant().getEpochSecond();
        DiscordActivitySecrets richPresenceSecrets = new DiscordActivitySecrets();
        DiscordActivityParty richPresenceParty = new DiscordActivityParty();

        if (joinSecret != null) {
            richPresenceSecrets.join = toBytes(joinSecret.toString(), 128);
            richPresenceParty.id = toBytes(joinSecret.id, 128);
            richPresenceParty.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size();
            partySize.max_size = 15;
            richPresenceParty.size = partySize;
        }
        richPresence.assets = richPresenceAssets;
        richPresence.timestamps = richPresenceTimestamps;
        richPresence.secrets = richPresenceSecrets;
        richPresence.party = richPresenceParty;

        lastStructure = richPresence;

        if (ready) {
            activityManager.update_activity.apply(activityManager, richPresence, null, null);
        } else {
            activityToUseWhenReady = richPresence;
        }
    }

    /**
     * update user RichPresence
     *
     * @param state
     *        RichPresence state string
     * @param details
     *        RichPresence details string
     * @param largText
     *        RichPresence large Text
     * @param largeImg
     *        RichPresence large image key
     * @param date
     *        RichPresence Date
     */
    public void updateRichPresence(String state, String details, String largeImg, String largText, OffsetDateTime date) {
        if (disabled) return;
        DiscordActivity richPresence = new DiscordActivity();
        richPresence.state = toBytes(state, 128);
        richPresence.details = toBytes(details, 128);
        richPresence.application_id = applicationID;
        DiscordActivityAssets richPresenceAssets = new DiscordActivityAssets();
        richPresenceAssets.large_image = toBytes(largeImg, 128);
        richPresenceAssets.large_text = toBytes(largText, 128);
        DiscordActivityTimestamps richPresenceTimestamps = new DiscordActivityTimestamps();
        richPresenceTimestamps.start = date.toInstant().getEpochSecond();
        DiscordActivitySecrets richPresenceSecrets = new DiscordActivitySecrets();
        DiscordActivityParty richPresenceParty = new DiscordActivityParty();

        if (joinSecret != null) {
            richPresenceSecrets.join = toBytes(joinSecret.toString(), 128);
            richPresenceParty.id = toBytes(joinSecret.id, 128);
            richPresenceParty.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size();
            partySize.max_size = 15;
            richPresenceParty.size = partySize;
        }
        richPresence.assets = richPresenceAssets;
        richPresence.timestamps = richPresenceTimestamps;
        richPresence.secrets = richPresenceSecrets;
        richPresence.party = richPresenceParty;

        lastStructure = richPresence;

        if (ready) {
            activityManager.update_activity.apply(activityManager, richPresence, null, null);
        } else {
            activityToUseWhenReady = richPresence;
        }
    }

    /**
     * update user RichPresence
     *
     * @param state
     *        RichPresence state string
     * @param details
     *        RichPresence details string
     * @param largText
     *        RichPresence large Text
     * @param date
     *        RichPresence End Date
     */
    public void updateRichPresenceEndDate(String state, String details, String largText, OffsetDateTime date) {
        if (disabled) return;
        DiscordActivity richPresence = new DiscordActivity();
        richPresence.state = toBytes(state, 128);
        richPresence.details = toBytes(details, 128);
        richPresence.application_id = applicationID;
        DiscordActivityAssets richPresenceAssets = new DiscordActivityAssets();
        richPresenceAssets.large_image = toBytes("wynn", 128);
        richPresenceAssets.large_text = toBytes(largText, 128);
        DiscordActivityTimestamps richPresenceTimestamps = new DiscordActivityTimestamps();
        richPresenceTimestamps.end = date.toInstant().getEpochSecond();
        DiscordActivitySecrets richPresenceSecrets = new DiscordActivitySecrets();
        DiscordActivityParty richPresenceParty = new DiscordActivityParty();

        if (joinSecret != null) {
            richPresenceSecrets.join = toBytes(joinSecret.toString(), 128);
            richPresenceParty.id = toBytes(joinSecret.id, 128);
            richPresenceParty.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size();
            partySize.max_size = 15;
            richPresenceParty.size = partySize;
        }
        richPresence.assets = richPresenceAssets;
        richPresence.timestamps = richPresenceTimestamps;
        richPresence.secrets = richPresenceSecrets;
        richPresence.party = richPresenceParty;

        lastStructure = richPresence;

        if (ready) {
            activityManager.update_activity.apply(activityManager, richPresence, null, null);
        } else {
            activityToUseWhenReady = richPresence;
        }
    }

    /**
     * update user RichPresence
     *
     * @param state
     *        RichPresence state string
     * @param details
     *        RichPresence details string
     * @param largText
     *        RichPresence large Text
     * @param largeImg
     *        RichPresence large image key
     * @param date
     *        RichPresence End Date
     */
    public void updateRichPresenceEndDate(String state, String details, String largeImg, String largText, OffsetDateTime date) {
        if (disabled) return;
        DiscordActivity richPresence = new DiscordActivity();
        richPresence.state = toBytes(state, 128);
        richPresence.details = toBytes(details, 128);
        richPresence.application_id = applicationID;
        DiscordActivityAssets richPresenceAssets = new DiscordActivityAssets();
        richPresenceAssets.large_image = toBytes(largeImg, 128);
        richPresenceAssets.large_text = toBytes(largText, 128);
        DiscordActivityTimestamps richPresenceTimestamps = new DiscordActivityTimestamps();
        richPresenceTimestamps.end = date.toInstant().getEpochSecond();
        DiscordActivitySecrets richPresenceSecrets = new DiscordActivitySecrets();
        DiscordActivityParty richPresenceParty = new DiscordActivityParty();

        if (joinSecret != null) {
            richPresenceSecrets.join = toBytes(joinSecret.toString(), 128);
            richPresenceParty.id = toBytes(joinSecret.id, 128);
            richPresenceParty.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size();
            partySize.max_size = 15;
            richPresenceParty.size = partySize;
        }
        richPresence.assets = richPresenceAssets;
        richPresence.timestamps = richPresenceTimestamps;
        richPresence.secrets = richPresenceSecrets;
        richPresence.party = richPresenceParty;

        lastStructure = richPresence;

        if (ready) {
            activityManager.update_activity.apply(activityManager, richPresence, null, null);
        } else {
            activityToUseWhenReady = richPresence;
        }
    }

    /**
     * Runs all the callbacks from the RPC
     */
    public void runCallbacks() {
        if (disabled || !ready) return;

        discordCore.run_callbacks.apply(discordCore);
    }

    /**
     * Updates the Join Secret
     *
     * @param joinSecret the join secret
     */
    public void setJoinSecret(SecretContainer joinSecret) {
        if (disabled) return;
        this.joinSecret = joinSecret;

        if (lastStructure != null) {
            if (joinSecret != null) {
                lastStructure.secrets.join = toBytes(joinSecret.toString(), 128);
                lastStructure.party.id = toBytes(joinSecret.id, 128);
                lastStructure.party.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
                lastStructure.party.size.current_size = PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size();
                lastStructure.party.size.max_size = 15;
            } else {
                lastStructure.secrets.join = toBytes(null, 128);
                lastStructure.party.id = toBytes(null, 128);
                lastStructure.party.privacy = EDiscordActivityPartyPrivacy.DiscordActivityParty_Privacy_Private;
                lastStructure.party.size.current_size = 0;
                lastStructure.party.size.max_size = 0;
            }
            if (ready) {
                activityManager.update_activity.apply(activityManager, lastStructure, null, null);
            } else {
                activityToUseWhenReady = lastStructure;
            }
        }
    }

    public boolean validSecret(String secret) {
        if (disabled) return false;
        return joinSecret != null && joinSecret.getRandomHash().equals(secret);
    }

    /**
     * Shutdown the RPC
     */
    public void disconnectRichPresence() {
        if (disabled) return;
        discordCore.destroy.apply(discordCore);
        discordCore = null;
        disabled = true;
    }

    /**
     * Gets the join secret container
     *
     * @return the join secret container
     */
    public SecretContainer getJoinSecret() {
        if (disabled) return null;
        return joinSecret;
    }

    public boolean isDisabled() {
        return disabled;
    }

    private static byte[] toBytes(String string, int size) {
        if (string == null) {
            return new byte[size];
        }
        byte[] encoded = string.getBytes(StandardCharsets.UTF_8);
        if (encoded.length >= size) {
            Reference.LOGGER.error("[RichPresence] Truncating string because it would be too long (From " + (encoded.length + 1) + " to " + size + " bytes)");
            encoded[size - 1] = 0;
        }
        return Arrays.copyOf(encoded, size);
    }

    private static String bytesToString(byte[] bytes) {
        return Native.toString(bytes, StandardCharsets.UTF_8.name());
    }

    public OverlayManager getOverlayManager() {
        if (disabled || discordCore == null) return overlayManager = null;
        if (overlayManager == null) overlayManager = new OverlayManager();
        return overlayManager;
    }

    public class OverlayManager {

        private IDiscordOverlayManager overlayManager = null;

        private OverlayManager() { }

        private boolean cantCreate() {
            if (RichProfile.this.disabled || discordCore == null || !ready) {
                overlayManager = null;
                return true;
            }
            return overlayManager == null;
        }

        /**
         * @see <a href="https://discordapp.com/developers/docs/game-sdk/overlay#isenabled">Docs</a>
         *
         * @return null if the discord SDK is disabled, otherwise true if overlays can appear,
         *         else false (they will open in the main Discord client)
         */
        public Boolean isEnabled() {
            if (cantCreate()) return null;
            IntByReference ref = new IntByReference();
            overlayManager.is_enabled.apply(overlayManager, ref.getPointer());
            return ref.getValue() != 0;
        }

        public boolean openGuildInvite(String code) {
            return openGuildInvite(code, null);
        }

        /**
         *
         * @see <a href="https://discordapp.com/developers/docs/game-sdk/overlay#openguildinvite">Docs</a>
         *
         * @param code End part of the discord guild invite link (e.g., "foo" for "discord.gg/foo")
         * @param callback Takes a value from {@link com.wynntils.modules.richpresence.discordgamesdk.enums.EDiscordResult}
         * @return true if attempting to open the invite
         */
        public boolean openGuildInvite(String code, Consumer<EDiscordResult> callback) {
            if (callback == null) return openGuildInvite(code, c -> {});
            if (cantCreate()) return false;
            overlayManager.open_guild_invite.apply(overlayManager, code, Pointer.NULL, (null_pointer, result) -> callback.accept(result));
            return true;
        }

    }

}
