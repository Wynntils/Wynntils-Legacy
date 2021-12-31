/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.instances.MusicPlayer;
import com.wynntils.modules.music.instances.QueuedTrack;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import com.wynntils.webapi.profiles.MusicProfile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoundTrackManager {

    private static final Pattern TERRITORY_NAME_REGEX = Pattern.compile("\\(([^)]+)\\)");

    private static final Map<String, MusicProfile> downloadedMusics = new HashMap<>();
    private static final Map<String, MusicProfile> availableMusics = new HashMap<>();

    private static final MusicPlayer player = new MusicPlayer();

    private static boolean isListUpdated = false;
    private static File musicFolder = null;

    /**
     * @see MusicPlayer
     * @return the current MusicPlayer instance
     */
    public static MusicPlayer getPlayer() {
        return player;
    }

    /**
     * Tries to update the available song list
     */
    public static void updateSongList() {
        if (!MusicConfig.INSTANCE.enabled) return;

        musicFolder = new File(Reference.MOD_STORAGE_ROOT, "sounds");
        if (!musicFolder.exists() || !musicFolder.isDirectory()) musicFolder.mkdirs();

        File[] listOfFiles = musicFolder.listFiles();
        if (listOfFiles.length >= 1) {
            for (File f : listOfFiles) {
                downloadedMusics.put(StringUtils.toMD5(f.getName() + f.length()), new MusicProfile(f));
            }
        }

        try {
            WebManager.getCurrentAvailableSongs().forEach(c -> availableMusics.put(c.getAsHash(), c));

            isListUpdated = true;
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Either queue or downloads the song based on its profile.
     * @see MusicProfile
     *
     * @param song the track profile
     * @param fastSwitch if it should fast switch to the song
     * @param fadeIn if the song should start with a fade in
     * @param fadeOut if while transitioning the old song should fadeOut
     * @param repeat if the song should repeat until changed
     * @param lockQueue no more songs will be allowed until the provided ends
     * @param quiet if the song should use default or offocus volume
     * @param force if the song should be played even if the player is paused
     */
    private static void playSong(MusicProfile song, boolean fastSwitch, boolean fadeIn, boolean fadeOut, boolean repeat, boolean lockQueue, boolean quiet, boolean force) {
        if (!force && !MusicConfig.INSTANCE.enabled || song == null) return;

        // updates the song list if possible
        if (!isListUpdated) {
            updateSongList();
            return;
        }

        // check if it was already downloaded or in download
        if (downloadedMusics.containsKey(song.getAsHash())) {
            Optional<File> songFile = downloadedMusics.get(song.getAsHash()).getFile();
            if (!songFile.isPresent()) return; // available to play (downloaded)

            player.play(songFile.get(), fadeIn, fadeOut, fastSwitch, repeat, lockQueue, quiet, force);
            return;
        }

        // queue the file downloading
        final MusicProfile toDownload = song;
        downloadedMusics.put(toDownload.getAsHash(), toDownload);
        DownloaderManager.queueDownload(song.getFormattedName(), song.getDownloadUrl(), musicFolder, DownloadAction.SAVE, success -> {
            if (!success) return;

            downloadedMusics.replace(toDownload.getAsHash(), new MusicProfile(new File(musicFolder, toDownload.getName())));
            playSong(song, fastSwitch, fadeIn, fadeOut, repeat, lockQueue, quiet, force);
        });
    }

    /**
     * Tries to fiend a song track based on it's name
     *
     * @param fullName the track name
     * @param fastSwitch if it should fast switch to the song
     * @param fadeIn if the song should start with a fade in
     * @param fadeOut if while transitioning the old song should fadeOut
     * @param repeat if the song should repeat until changed
     * @param lockQueue no more songs will be allowed until the provided ends
     * @param quiet if the song should use default or offocus volume
     * @param force if the song should be played even if the player is paused
     */
    public static void findTrack(String fullName, boolean fastSwitch, boolean fadeIn, boolean fadeOut, boolean repeat, boolean lockQueue, boolean quiet, boolean force) {
        if (!force && !MusicConfig.INSTANCE.enabled || fullName == null) return;

        MusicProfile selected = null;
        for (MusicProfile mp : availableMusics.values()) {
            if (!mp.getFormattedName().equalsIgnoreCase(fullName)) continue;

            selected = mp;
            break;
        }

        playSong(selected, fastSwitch, fadeIn, fadeOut, repeat, lockQueue, quiet, force);
    }

    /**
     * Tries to fiend a song track based on it's name
     *
     * @param fullName the track name
     * @param fastSwitch if it should fast switch to the song
     */
    public static void findTrack(String fullName, boolean fastSwitch) {
        findTrack(fullName, fastSwitch, true, true, true, false, false, false);
    }

    /**
     * Tries to fiend a song track based on it's name
     *
     * @param fullName the track name
     * @param fastSwitch if it should fast switch to the song
     * @param quiet if the song should use default or offocus volume
     */
    public static void findTrack(String fullName, boolean fastSwitch, boolean quiet) {
        findTrack(fullName, fastSwitch, true, true, true, false, quiet, false);
    }


    /**
     * Tries to find a song track based on the provied Territory Name
     * Matches and prioritizes by:
     *  - equalsIgnoreCase - PERFECT MATCH
     *  - startsWith - by first word
     *  - contains - the entire location
     *
     * @see com.wynntils.webapi.profiles.TerritoryProfile
     *
     * @param territoryName the territory name to search for
     */
    public static void findTrack(String territoryName) {
        if (!MusicConfig.INSTANCE.enabled || !MusicConfig.INSTANCE.replaceJukebox) {
            player.stop();
            return;
        }

        MusicProfile selected = null;

        Optional<MusicProfile> direct = Optional.empty();
        Optional<MusicProfile> firstWord = Optional.empty();
        Optional<MusicProfile> lessPossible = Optional.empty();

        for (MusicProfile mp : availableMusics.values()) {
            if (!mp.getName().contains("(") || !mp.getName().contains(")")) continue;
            Matcher matcher = TERRITORY_NAME_REGEX.matcher(mp.getName());
            while (matcher.find()) {
                String value = matcher.group(1).replace("(", "").replace(")", "");
                String toSearch = territoryName.contains(" ") ? territoryName.split(" ")[0] : territoryName;

                if (value.equalsIgnoreCase(territoryName)) {  // perfect match
                    direct = Optional.of(mp);
                    continue;
                }
                if (value.startsWith(toSearch)) {  // match by first word
                    firstWord = Optional.of(mp);
                    continue;
                }
                if (!value.startsWith(territoryName)) continue; // entire location

                lessPossible = Optional.of(mp);
            }
        }

        // priorities
        if (direct.isPresent()) { selected = direct.get();
        } else if (firstWord.isPresent()) { selected = firstWord.get();
        } else if (lessPossible.isPresent()) { selected = lessPossible.get(); }

       playSong(selected, false, false, true, true, false, false, false);
    }

    /**
     * @return the current song that is being played or null if none is playing
     */
    public static QueuedTrack getCurrentSong() {
        if (player == null || player.getStatus() == null) return null;

        return player.getStatus().getCurrentSong();
    }

}
