/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import com.wynntils.core.utils.Utils;
import com.wynntils.modules.richpresence.discordrpc.DiscordRichPresence;
import com.wynntils.webapi.WebManager;

import java.time.OffsetDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RichProfile {

    final DiscordRichPresence.DiscordRPC rpc;
    Thread shutdown = new Thread(this::disconnectRichPresence);

    ScheduledFuture callbacks;

    public RichProfile(String id) {
        rpc = DiscordRichPresence.discordInitialize();

        DiscordRichPresence.DiscordEventHandlers handler = new DiscordRichPresence.DiscordEventHandlers();
        handler.ready = user -> {
            System.out.println("DISCORD READY"); //HeyZeer0: for a random reason, it doesn't seems to work without this println
            if(WebManager.getAccount() != null) WebManager.getAccount().updateDiscord(user.userId, user.username + "#" + user.discriminator);
            callbacks.cancel(true);
        };

        rpc.Discord_Initialize(id, handler, true, null);

        //HeyZeer0: this handles the events, since we just want the ready one, and it is triggered only once, this executorals is stopped after receiving it
        callbacks = Utils.runTaskTimer(rpc::Discord_RunCallbacks, TimeUnit.SECONDS, 2);

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
        callbacks.cancel(true);
        rpc.Discord_Shutdown();
    }

}
