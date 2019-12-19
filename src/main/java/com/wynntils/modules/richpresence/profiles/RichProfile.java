/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.modules.richpresence.discordgamesdk.*;
import com.wynntils.modules.richpresence.events.RPCJoinHandler;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.WebRequestHandler;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraft.client.gui.GuiScreen;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.function.IntConsumer;

public class RichProfile {

    public static final File GAME_SDK_FILE = new File(Reference.PLATFORM_NATIVES_ROOT, System.mapLibraryName("discord_game_sdk"));

    private IDiscordCore discordCore = null;
    private OverlayManager overlayManager = null;
    private IDiscordActivityManager activityManager = null;
    private Thread shutdown = new Thread(this::disconnectRichPresence);

    private SecretContainer joinSecret = null;

    private DiscordActivity lastStructure = null;

    private boolean disabled = false;
    private boolean ready = false;
    private DiscordActivity activityToUseWhenReady = null;
    private boolean isBlankGuiOpen = false;

    private boolean updatedDiscordUser = false;

    private long applicationID = 0;

    public RichProfile(long id) {
        WebRequestHandler handler = new WebRequestHandler();
        String apiRoot = WebManager.getApiUrls() == null ? null : WebManager.getApiUrls().get("RichPresence");
        String url = apiRoot == null ? null : apiRoot + "versioning.php";
        handler.addRequest(new WebRequestHandler.Request(url, "richpresence_versioning")
            .cacheTo(new File(Reference.NATIVES_ROOT, "richpresence_versioning.txt"))
            .handleWebReader(reader -> {
                String md5 = reader.get(Platform.RESOURCE_PREFIX);
                if (md5 != null && GAME_SDK_FILE.exists() && new MD5Verification(GAME_SDK_FILE).equals(md5)) {
                    setup(id);
                    return true;
                }

                if (md5 == null || apiRoot == null) {
                    // No sdk on this platform or webapi is down
                    setDisabled();
                    return true;
                }

                DownloaderManager.queueDownload("Discord Game SDK", apiRoot + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("discord_game_sdk"), Reference.PLATFORM_NATIVES_ROOT, DownloadAction.SAVE, (success) -> {
                    if (!success) {
                        setDisabled();
                        return;
                    }

                    ModCore.mc().addScheduledTask(() -> {
                        setup(id);
                    });
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
                if (opened && ModCore.mc().currentScreen == null) {
                    ModCore.mc().displayGuiScreen(new GuiScreen() {
                        public void onGuiClosed() {
                            isBlankGuiOpen = false;
                        }
                    });
                    isBlankGuiOpen = true;
                } else if (!opened && isBlankGuiOpen) {
                    ModCore.mc().displayGuiScreen(null);
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
            createParams.flags = DiscordGameSDKLibrary.EDiscordCreateFlags.DiscordCreateFlags_NoRequireDiscord;

            overlayManager = null;
            IDiscordCore.ByReference[] array = new IDiscordCore.ByReference[] { new IDiscordCore.ByReference() };
            gameSDK.DiscordCreate(DiscordGameSDKLibrary.DISCORD_VERSION, createParams, array);
            discordCore = array[0];
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
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
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
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
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
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
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
            DiscordPartySize partySize = new DiscordPartySize();
            partySize.current_size = PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
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
                lastStructure.party.size.current_size = PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
                lastStructure.party.size.max_size = 15;
            } else {
                lastStructure.secrets.join = toBytes(null, 128);
                lastStructure.party.id = toBytes(null, 128);
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

    public boolean validSecrent(String secret) {
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
            Reference.LOGGER.error("[RichPresence] Truncating string because it would be too long (From {} to {} bytes)", encoded.length + 1, size);
            encoded[size - 1] = 0;
        }
        return Arrays.copyOf(encoded, size);
    }

    private static Memory toCString(String string) {
        if (string == null) return null;
        byte[] asBytes = string.getBytes(StandardCharsets.UTF_8);
        Memory cString = new Memory(asBytes.length + 1);
        cString.write(0, asBytes, 0, asBytes.length);
        cString.setByte(asBytes.length, (byte) 0);
        return cString;
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
         * @param callback Takes a value from {@link com.wynntils.modules.richpresence.discordgamesdk.DiscordGameSDKLibrary.EDiscordResult}
         * @return true if attempting to open the invite
         */
        public boolean openGuildInvite(String code, IntConsumer callback) {
            if (callback == null) return openGuildInvite(code, c -> {});
            if (cantCreate()) return false;
            overlayManager.open_guild_invite.apply(overlayManager, toCString(code), Pointer.NULL, (null_pointer, result) -> callback.accept(result));
            return true;
        }

    }

}
