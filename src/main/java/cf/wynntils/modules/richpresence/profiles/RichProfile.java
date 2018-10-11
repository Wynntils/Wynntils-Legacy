package cf.wynntils.modules.richpresence.profiles;

import cf.wynntils.modules.richpresence.discordrpc.DiscordRichPresence;
import cf.wynntils.modules.richpresence.discordrpc.DiscordRichPresence.DiscordRPC;
import cf.wynntils.modules.richpresence.discordrpc.DiscordRichPresence.DiscordRichPresenceStructure;

import java.time.OffsetDateTime;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class RichProfile {

    final DiscordRPC rpc = DiscordRichPresence.discordInitialize();
    boolean ready = false;
    Thread shutdown = new Thread(() -> {
        rpc.Discord_Shutdown();
    });

    public RichProfile(String id) throws Exception {
        rpc.Discord_Initialize(id, null, false, null);
        Runtime.getRuntime().addShutdownHook(shutdown);

        ready = true;
    }

    /**
     * Cleans user current RichPresence
     */
    public void stopRichPresence() {
        rpc.Discord_ClearPresence();
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
        DiscordRichPresenceStructure richPresence = new DiscordRichPresenceStructure();
        richPresence.state = state;
        richPresence.details = details;
        richPresence.largeImageText = largText;
        richPresence.startTimestamp = date.toInstant().getEpochSecond();
        richPresence.largeImageKey = "wynn";
        rpc.Discord_UpdatePresence(richPresence);
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
        DiscordRichPresenceStructure richPresence = new DiscordRichPresenceStructure();
        richPresence.state = state;
        richPresence.details = details;
        richPresence.largeImageKey = largeImg;
        richPresence.largeImageText = largText;
        richPresence.startTimestamp = date.toInstant().getEpochSecond();
        richPresence.smallImageKey = "wynn";
        rpc.Discord_UpdatePresence(richPresence);
    }

    public void disconnectRichPresence() {
        rpc.Discord_Shutdown();
    }

    /**
     * Return if the RichClient is ready to go
     * @return if the RichClient is ready
     */
    public boolean isReady() {
        return ready;
    }

}
