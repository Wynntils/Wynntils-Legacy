/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.instances.MusicPlayer;
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
    private static boolean fastSwitchNext = false;
    private static File musicFolder = null;

    /**
     * @see MusicPlayer
     * @return the current MusicPlayer instance
     */
    public static MusicPlayer getPlayer() {
        return player;
    }

    /**
     * Requires the next song to be fast switched
     */
    public static void setFastSwitchNext() {
        fastSwitchNext = true;
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
     */
    private static void playSong(MusicProfile song, boolean fastSwitch) {
        if (!MusicConfig.INSTANCE.enabled || song == null) return;

        // updates the song list if possible
        if (!isListUpdated) {
            updateSongList();
            return;
        }

        // check if it was already downloaded or in download
        if (downloadedMusics.containsKey(song.getAsHash())) {
            if (!downloadedMusics.get(song.getAsHash()).getFile().isPresent()) return; // available to play (downloaded)

            fastSwitchNext = false;
            player.play(downloadedMusics.get(song.getAsHash()).getFile().get(), fastSwitch);
            return;
        }

        // queue the file downloading
        final MusicProfile toDownload = song;
        downloadedMusics.put(toDownload.getAsHash(), toDownload);
        DownloaderManager.queueDownload(song.getFormattedName(), song.getDownloadUrl(), musicFolder, DownloadAction.SAVE, success -> {
            if (!success) return;

            downloadedMusics.replace(toDownload.getAsHash(), new MusicProfile(new File(musicFolder, toDownload.getName())));
            playSong(song, fastSwitch);
        });
    }

    /**
     * Tries to fiend a song track based on it's name
     *
     * @param fullName the track name
     * @param fastSwitch if it should fast switch to the song
     */
    public static void findTrack(String fullName, boolean fastSwitch) {
        if (!MusicConfig.INSTANCE.enabled || fullName == null) return;

        MusicProfile selected = null;
        for (MusicProfile mp : availableMusics.values()) {
            if (!mp.getFormattedName().equalsIgnoreCase(fullName)) continue;

            selected = mp;
            break;
        }

        playSong(selected, fastSwitch || fastSwitchNext);
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
            Matcher mc = TERRITORY_NAME_REGEX.matcher(mp.getName());
            while (mc.find()) {
                String value = mc.group(1).replace("(", "").replace(")", "");
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

       playSong(selected, fastSwitchNext);
    }

}
