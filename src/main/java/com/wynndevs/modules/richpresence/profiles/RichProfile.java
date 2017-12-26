package com.wynndevs.modules.richpresence.profiles;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;

import java.time.OffsetDateTime;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class RichProfile {

    IPCClient client;
    boolean ready;

    public RichProfile(long id, DiscordBuild build) throws Exception {
        client = new IPCClient(id);
        client.connect(build);

        ready = true;
    }

    /**
     * Cleans user current RichPresence
     */
    public void stopRichPresence() {
        client.sendRichPresence(null);
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
        client.sendRichPresence(new RichPresence(state, details, date, null, "wynn", largText, null, null, null, 0, 0, null, null, null, false));
    }

    /**
     * Return if the RichClient is ready to go
     * @return if the RichClient is ready
     */
    public boolean isReady() {
        return ready;
    }

}
