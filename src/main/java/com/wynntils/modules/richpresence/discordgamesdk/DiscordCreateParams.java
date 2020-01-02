package com.wynntils.modules.richpresence.discordgamesdk;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

/**
 * <i>native declaration : line 586</i><br>
 * This file was autogenerated by
 * <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that
 * <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a
 * few opensource projects.</a>.<br>
 * For help, please visit
 * <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> ,
 * <a href="http://rococoa.dev.java.net/">Rococoa</a>, or
 * <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class DiscordCreateParams extends Structure {
    /** C type : DiscordClientId */
    public long client_id;
    public long flags;
    /** C type : IDiscordCoreEvents* */
    public PointerByReference events;
    /** C type : void* */
    public Pointer event_data;
    /** C type : IDiscordApplicationEvents* */
    public PointerByReference application_events;
    /** C type : DiscordVersion */
    public int application_version;
    /** C type : IDiscordUserEvents* */
    public IDiscordUserEvents.ByReference user_events;
    /** C type : DiscordVersion */
    public int user_version;
    /** C type : IDiscordImageEvents* */
    public PointerByReference image_events;
    /** C type : DiscordVersion */
    public int image_version;
    /** C type : IDiscordActivityEvents* */
    public IDiscordActivityEvents.ByReference activity_events;
    /** C type : DiscordVersion */
    public int activity_version;
    /** C type : IDiscordRelationshipEvents* */
    public IDiscordRelationshipEvents.ByReference relationship_events;
    /** C type : DiscordVersion */
    public int relationship_version;
    /** C type : IDiscordLobbyEvents* */
    public IDiscordLobbyEvents.ByReference lobby_events;
    /** C type : DiscordVersion */
    public int lobby_version;
    /** C type : IDiscordNetworkEvents* */
    public IDiscordNetworkEvents.ByReference network_events;
    /** C type : DiscordVersion */
    public int network_version;
    /** C type : IDiscordOverlayEvents* */
    public IDiscordOverlayEvents.ByReference overlay_events;
    /** C type : DiscordVersion */
    public int overlay_version;
    /** C type : IDiscordStorageEvents* */
    public PointerByReference storage_events;
    /** C type : DiscordVersion */
    public int storage_version;
    /** C type : IDiscordStoreEvents* */
    public IDiscordStoreEvents.ByReference store_events;
    /** C type : DiscordVersion */
    public int store_version;
    /** C type : IDiscordVoiceEvents* */
    public IDiscordVoiceEvents.ByReference voice_events;
    /** C type : DiscordVersion */
    public int voice_version;
    /** C type : IDiscordAchievementEvents* */
    public IDiscordAchievementEvents.ByReference achievement_events;
    /** C type : DiscordVersion */
    public int achievement_version;

    public DiscordCreateParams() {
        super();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("client_id", "flags", "events", "event_data", "application_events", "application_version", "user_events", "user_version", "image_events", "image_version", "activity_events", "activity_version", "relationship_events", "relationship_version", "lobby_events", "lobby_version", "network_events", "network_version", "overlay_events", "overlay_version", "storage_events", "storage_version", "store_events", "store_version", "voice_events", "voice_version", "achievement_events", "achievement_version");
    }

    public DiscordCreateParams(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends DiscordCreateParams implements Structure.ByReference {

    };

    public static class ByValue extends DiscordCreateParams implements Structure.ByValue {

    };
}
