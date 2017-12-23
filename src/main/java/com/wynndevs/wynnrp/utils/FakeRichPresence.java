package com.wynndevs.wynnrp.utils;

import com.jagrosh.discordipc.entities.RichPresence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.OffsetDateTime;

public class FakeRichPresence extends RichPresence {

    private String state;
    private String details;
    private OffsetDateTime startTimestamp;
    private OffsetDateTime endTimestamp;
    private String largeImageKey;
    private String largeImageText;
    private String smallImageKey;
    private String smallImageText;
    private String partyId;
    private int partySize;
    private int partyMax;
    private String matchSecret;
    private String joinSecret;
    private String spectateSecret;
    private boolean instance;

    public FakeRichPresence(String state, String details, OffsetDateTime startTimestamp, OffsetDateTime endTimestamp, String largeImageKey, String largeImageText, String smallImageKey, String smallImageText, String partyId, int partySize, int partyMax, String matchSecret, String joinSecret, String spectateSecret, boolean instance) {
        super(state, details, startTimestamp, endTimestamp, largeImageKey, largeImageText, smallImageKey, smallImageText, partyId, partySize, partyMax, matchSecret, joinSecret, spectateSecret, instance);
        this.state = state; this.details = details; this.startTimestamp = startTimestamp; this.endTimestamp = endTimestamp; this.largeImageKey = largeImageKey; this.largeImageText = largeImageText; this.smallImageKey = smallImageKey;
        this.smallImageText = smallImageText; this.instance = instance;
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject()
                .put("state", state)
                .put("details", details)
                .put("timestamps", new JSONObject()
                        .put("start", startTimestamp==null ? null : startTimestamp.toEpochSecond())
                        .put("end", endTimestamp==null ? null : endTimestamp.toEpochSecond()))
                .put("assets", new JSONObject()
                        .put("large_image", largeImageKey)
                        .put("large_text", largeImageText)
                        .put("small_image", smallImageKey)
                        .put("small_text", smallImageText))
                .put("secrets", new JSONObject()
                        .put("join", joinSecret)
                        .put("spectate", spectateSecret)
                        .put("match", matchSecret))
                .put("instance", instance);
    }

}
