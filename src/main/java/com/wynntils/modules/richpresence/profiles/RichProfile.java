/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.modules.richpresence.discordrpc.DiscordRichPresence;
import com.wynntils.modules.richpresence.events.RPCJoinHandler;
import com.wynntils.webapi.WebManager;

import java.time.OffsetDateTime;

public class RichProfile {

    final DiscordRichPresence.DiscordRPC rpc;
    Thread shutdown = new Thread(this::disconnectRichPresence);

    SecretContainer joinSecret = null;

    DiscordRichPresence.DiscordRichPresenceStructure lastStructure = null;

    public RichProfile(String id) {
        rpc = DiscordRichPresence.discordInitialize();

        DiscordRichPresence.DiscordEventHandlers handler = new DiscordRichPresence.DiscordEventHandlers();
        handler.ready = user -> {
            System.out.println("DISCORD READY"); //HeyZeer0: for a random reason, it doesn't seems to work without this println
            if(WebManager.getAccount() != null) WebManager.getAccount().updateDiscord(user.userId, user.username + "#" + user.discriminator);
        };
        handler.joinGame = new RPCJoinHandler();

        rpc.Discord_Initialize(id, handler, true, null);

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

        if(joinSecret != null) {
            richPresence.joinSecret = joinSecret.toString();
            richPresence.partyId = joinSecret.id;
            richPresence.partySize = 1 + PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
            richPresence.partyMax = 15;
        }

        lastStructure = richPresence;

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

        if(joinSecret != null) {
            richPresence.joinSecret = joinSecret.toString();
            richPresence.partyId = joinSecret.id;
            richPresence.partySize = 1 + PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().size();
            richPresence.partyMax = 15;
        }

        lastStructure = richPresence;

        rpc.Discord_UpdatePresence(richPresence);
    }

    /**
     * Runs all the callbacks from the RPC
     */
    public void runCallbacks() {
        if(rpc == null) return;

        rpc.Discord_RunCallbacks();
    }

    /**
     * Updates the Join Secret
     *
     * @param joinSecret the join secret
     */
    public void setJoinSecret(SecretContainer joinSecret) {
        this.joinSecret = joinSecret;

        if(lastStructure != null) updateRichPresence(lastStructure.state, lastStructure.details, lastStructure.largeImageKey, lastStructure.largeImageText, OffsetDateTime.now());
    }

    public boolean validSecrent(String secret) {
        return joinSecret != null && joinSecret.getRandomHash().equals(secret);
    }

    /**
     * Shutdown the RPC
     */
    public void disconnectRichPresence() {
        rpc.Discord_Shutdown();
    }

    /**
     * Gets the join secret container
     *
     * @return the join secret container
     */
    public SecretContainer getJoinSecret() {
        return joinSecret;
    }

}
