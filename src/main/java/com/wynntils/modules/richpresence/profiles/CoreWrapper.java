package com.wynntils.modules.richpresence.profiles;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.modules.richpresence.events.JoinHandler;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.OverlayManager;
import de.jcm.discordgamesdk.activity.Activity;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class CoreWrapper {

    private Core core;
    private Activity activity;

    private boolean init = false;
    private boolean enabled = false;

    private SecretContainer joinSecret;

    private JoinHandler joinHandler;

    public CoreWrapper(long id) {
        File discordLibrary = null;
        try {
            discordLibrary = downloadDiscordLibrary();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (discordLibrary == null) return;
        Core.init(discordLibrary);
        CreateParams params = new CreateParams();
        joinHandler = new JoinHandler();
        params.setClientID(id);
        params.setFlags(CreateParams.getDefaultFlags());
        params.registerEventHandler(new DiscordEventAdapter() {
            @Override
            public void onActivityJoin(String secret) {
                joinHandler.apply(secret);
            }
        });
        core = new Core(params);
        activity = new Activity();
        init = true;
        enabled = true;
    }

    public OverlayManager getOverlayManager() {
        return core.overlayManager();
    }

    public void setJoinSecret(SecretContainer joinSecret) {
        if (!enabled) return;
        this.joinSecret = joinSecret;

        if (joinSecret != null) {
            activity.secrets().setJoinSecret(joinSecret.toString());
            activity.party().setID(joinSecret.id);

            //Happens if you accept a Discord invite and setting to 0 causes the SDK to throw an error during runCallback
            if (PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size() == 0) {
                activity.party().size().setCurrentSize(1);
            } else {
                activity.party().size().setCurrentSize(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size());
            }
            activity.party().size().setMaxSize(15);
        } else {
            activity.secrets().setJoinSecret("");
            activity.party().setID("");
            activity.party().size().setCurrentSize(0);
            activity.party().size().setMaxSize(0);
        }
        core.activityManager().updateActivity(activity);
    }

    public void runCallbacks() {
        if (!enabled) return;
        try {
            core.runCallbacks();
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
    }

    public boolean validSecret(String secret) {
        if (!enabled) return false;
        return joinSecret != null && joinSecret.getRandomHash().equals(secret);
    }

    public void stopRichPresence() {
        if (!enabled) return;
        core.activityManager().clearActivity();
    }

    public void updateRichPresence(String state, String details, String largText, OffsetDateTime date) {
        if (!enabled) return;
        activity.setState((state != null) ? state : "");
        activity.setDetails((details != null) ? details : "");
        activity.assets().setLargeText((largText != null) ? largText : "");
        activity.assets().setLargeImage("wynn");
        activity.timestamps().setStart(date.toInstant());

        core.activityManager().updateActivity(activity);
    }

    public void updateRichPresence(String state, String details, String largeImg, String largText, OffsetDateTime date) {
        if (!enabled) return;
        activity.setState((state != null) ? state : "");
        activity.setDetails((details != null) ? details : "");
        activity.assets().setLargeImage((largeImg != null) ? largeImg : "wynn");
        activity.assets().setLargeText((largText != null) ? largText : "");
        activity.timestamps().setStart(date.toInstant());

        core.activityManager().updateActivity(activity);
    }

    public void updateRichPresenceEndDate(String state, String details, String largText, OffsetDateTime date) {
        if (!enabled) return;
        activity.setState((state != null) ? state : "");
        activity.setDetails((details != null) ? details : "");
        activity.assets().setLargeImage("wynn");
        activity.assets().setLargeText((largText != null) ? largText : "");
        activity.timestamps().setEnd(date.toInstant());

        core.activityManager().updateActivity(activity);
    }

    public void updateRichPresenceEndDate(String state, String details, String largeImg, String largText, OffsetDateTime date) {
        if (!enabled) return;
        activity.setState((state != null) ? state : "");
        activity.setDetails((details != null) ? details : "");
        activity.assets().setLargeImage((largeImg != null) ? largeImg : "wynn");
        activity.assets().setLargeText((largText != null) ? largText : "");
        activity.timestamps().setEnd(date.toInstant());

        core.activityManager().updateActivity(activity);
    }

    // Copied from https://github.com/JnCrMx/discord-game-sdk4j/blob/master/examples/DownloadNativeLibrary.java
    private static File downloadDiscordLibrary() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else if (osName.contains("mac os")) {
            suffix = ".dylib";
        } else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }

		/*
		Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
		At this point we need the "x86_64" version, as this one is used in the ZIP.
		 */
        if (arch.equals("amd64"))
            arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/" + arch + "/" + name + suffix;

        // Open the URL as a ZipInputStream
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestProperty("User-Agent", "discord-game-sdk4j (https://github.com/JnCrMx/discord-game-sdk4j)");
        ZipInputStream zin = new ZipInputStream(connection.getInputStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!tempDir.mkdir())
                    throw new IOException("Cannot create temporary directory");
                tempDir.deleteOnExit();

                // Create a temporary file inside our directory (with a "normal" name)
                File temp = new File(tempDir, name + suffix);
                temp.deleteOnExit();

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                return temp;
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return null;
    }
}
