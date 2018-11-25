package cf.wynntils.modules.richpresence.discordrpc;

import com.sun.jna.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class DiscordRichPresence {
    
    private static File file = null;
    
    public static void loadDiscordRPC() {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            String os = (getNativeLibraryResourcePath(Platform.getOSType(), System.getProperty("os.arch"), System.getProperty("os.name"))).replace("/com/sun/jna/", "assets/wynntils/native/") + "/";
            
            String libraryName = System.mapLibraryName("discord-rpc");
            
            inputStream = DiscordRichPresence.class.getClassLoader().getResourceAsStream(os + libraryName);
            file = File.createTempFile("rpc", Platform.isWindows() ? ".dll" : null);
            file.deleteOnExit();
            outputStream = new FileOutputStream(file);
            
            int fileByte;
            while ((fileByte = inputStream.read()) > -1) {
                outputStream.write(fileByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static DiscordRPC discordInitialize() {
        loadDiscordRPC();
        DiscordRPC instance = DiscordRPC.INSTANCE;
        return instance;
    }
    
    public static interface DiscordRPC extends Library {
        
        DiscordRPC INSTANCE = (DiscordRPC) Native.loadLibrary(file.getAbsolutePath(), DiscordRPC.class);
        
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

        public static interface OnReady extends Callback {
            void accept(DiscordUser user);
        }
        
        public static interface OnDisconnected extends Callback {
            void accept(int errorCode, String message);
        }
        
        public static interface OnErrored extends Callback {
            void accept(int errorCode, String message);
        }
        
        public static interface OnJoinGame extends Callback {
            void accept(String joinSecret);
        }
        
        public static interface OnSpectateGame extends Callback {
            void accept(String spectateSecret);
        }
        
        public static interface OnJoinRequest extends Callback {
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

    private static String getNativeLibraryResourcePath(int osType, String arch, String name) {
        String osPrefix;
        arch = arch.toLowerCase();
        if ("powerpc".equals(arch)) {
            arch = "ppc";
        }
        else if ("powerpc64".equals(arch)) {
            arch = "ppc64";
        }
        switch(osType) {
            case Platform.WINDOWS:
                if ("i386".equals(arch))
                    arch = "x86";
                osPrefix = "win32-" + arch;
                break;
            case Platform.WINDOWSCE:
                osPrefix = "w32ce-" + arch;
                break;
            case Platform.MAC:
                osPrefix = "darwin";
                break;
            case Platform.LINUX:
                if ("x86".equals(arch)) {
                    arch = "i386";
                }
                else if ("x86_64".equals(arch)) {
                    arch = "amd64";
                }
                osPrefix = "linux-" + arch;
                break;
            case Platform.SOLARIS:
                osPrefix = "sunos-" + arch;
                break;
            default:
                osPrefix = name.toLowerCase();
                if ("x86".equals(arch)) {
                    arch = "i386";
                }
                if ("x86_64".equals(arch)) {
                    arch = "amd64";
                }
                int space = osPrefix.indexOf(" ");
                if (space != -1) {
                    osPrefix = osPrefix.substring(0, space);
                }
                osPrefix += "-" + arch;
                break;
        }
        return "/com/sun/jna/" + osPrefix;
    }
}
