/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.discordrpc;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

public class DiscordRichPresence {
    
    public static DiscordRPC discordInitialize() {
        DiscordRPC instance = DiscordRPC.INSTANCE;
        return instance;
    }
    
    public interface DiscordRPC extends Library {

        DiscordRPC INSTANCE = Native.loadLibrary("/assets/wynntils/native/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName("discord-rpc"), DiscordRPC.class);
        
        void Discord_Initialize(String applicationID, DiscordEventHandlers eventHandler, boolean autoRegister, String steamID);
        
        void Discord_UpdateConnection();
        
        void Discord_Shutdown();
        
        void Discord_UpdatePresence(DiscordRichPresenceStructure presence);
        
        void Discord_ClearPresence();
        
        void Discord_Respond(String userID, int reply);
        
        void Discord_RunCallbacks();
        
        void Discord_UpdateHandlers(DiscordEventHandlers eventHandler);
        
    }
    
    public static class DiscordRichPresenceStructure extends Structure {
        public String state;
        
        public String details;
        
        public long startTimestamp;
        
        public long endTimestamp;
        
        public String largeImageKey;
        
        public String largeImageText;
        
        public String smallImageKey;
        
        public String smallImageText;
        
        public String partyId;
        
        public int partySize;
        
        public int partyMax;
        
        public String matchSecret;
        
        public String joinSecret;
        
        public String spectateSecret;
        
        public byte instance;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "matchSecret", "joinSecret", "spectateSecret", "instance");
        }

    }
    
    public static class DiscordUser extends Structure {
        public String userId;
        
        public String username;
        
        public String discriminator;
        
        public String avatar;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("userId", "username", "discriminator", "avatar");
        }

    }
    
    public static class DiscordEventHandlers extends Structure {

        public interface OnReady extends Callback {
            void accept(DiscordUser user);
        }
        
        public interface OnDisconnected extends Callback {
            void accept(int errorCode, String message);
        }
        
        public interface OnErrored extends Callback {
            void accept(int errorCode, String message);
        }
        
        public interface OnJoinGame extends Callback {
            void accept(String joinSecret);
        }
        
        public interface OnSpectateGame extends Callback {
            void accept(String spectateSecret);
        }
        
        public interface OnJoinRequest extends Callback {
            void accept(DiscordUser user);
        }
        
        public OnReady ready;
        
        public OnDisconnected disconnected;
        
        public OnErrored errored;
        
        public OnJoinGame joinGame;
        
        public OnSpectateGame spectateGame;
        
        public OnJoinRequest joinRequest;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
        }
    }
}
