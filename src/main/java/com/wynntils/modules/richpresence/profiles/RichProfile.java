/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import com.wynntils.modules.richpresence.discordrpc.DiscordRichPresence;
import com.wynntils.webapi.WebManager;

import java.time.OffsetDateTime;

public class RichProfile {

    final DiscordRichPresence.DiscordRPC rpc;
    Thread shutdown = new Thread(this::disconnectRichPresence);

    Thread callbacks;

    public RichProfile(String id) throws Exception {
        rpc = DiscordRichPresence.discordInitialize();

        DiscordRichPresence.DiscordEventHandlers handler = new DiscordRichPresence.DiscordEventHandlers();
        handler.ready = user -> {
            if(WebManager.getAccount() != null) WebManager.getAccount().updateDiscord(user.userId, user.username + "#" + user.discriminator);
            callbacks.interrupt();
        };

        rpc.Discord_Initialize(id, handler, true, null);

        //HeyZeer0: this handles the events, since we just want the ready one, and it is triggered only once, this thread is stopped after receiving it
        callbacks = new Thread(() -> {
            while(!Thread.interrupted()) {
                rpc.Discord_RunCallbacks();
            }

            try{ Thread.sleep(2000); }catch (Exception ignored) { }
        }, "Wynntils RP Callbacks");
        callbacks.start();
        // <--------->

        Runtime.getRuntime().addShutdownHook(shutdown);
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
        DiscordRichPresence.DiscordRichPresenceStructure richPresence = new DiscordRichPresence.DiscordRichPresenceStructure();
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
        DiscordRichPresence.DiscordRichPresenceStructure richPresence = new DiscordRichPresence.DiscordRichPresenceStructure();
        richPresence.state = state;
        richPresence.details = details;
        richPresence.largeImageKey = largeImg;
        richPresence.largeImageText = largText;
        richPresence.startTimestamp = date.toInstant().getEpochSecond();
        richPresence.smallImageKey = "wynn";
        rpc.Discord_UpdatePresence(richPresence);
    }

    public void disconnectRichPresence() {
        callbacks.interrupt();
        rpc.Discord_Shutdown();
    }

}
