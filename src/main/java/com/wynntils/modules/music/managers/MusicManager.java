/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.instances.MusicPlayer;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import com.wynntils.webapi.profiles.MusicProfile;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicManager {

    private static HashMap<String, MusicProfile> downloadedMusics = new HashMap<>();
    private static HashMap<String, MusicProfile> availableMusics = new HashMap<>();

    private static final Pattern regex = Pattern.compile("\\(([^)]+)\\)");
    private static MusicPlayer player = new MusicPlayer();

    private static boolean isListUpdated = false;
    private static File musicFolder = null;

    public static MusicPlayer getPlayer() {
        return player;
    }

    public static void checkForUpdates() {
        if(!MusicConfig.INSTANCE.allowMusicModule) return;

        musicFolder = new File(Reference.MOD_STORAGE_ROOT, "sounds");
        if(!musicFolder.exists() || !musicFolder.isDirectory()) musicFolder.mkdirs();

        File[] listOfFiles = musicFolder.listFiles();
        if(listOfFiles.length >= 1) {
            for(File f : listOfFiles) {
                downloadedMusics.put(Utils.toMD5(f.getName() + f.length()), new MusicProfile(f));
            }
        }

        try{
            WebManager.getCurrentAvailableSongs().forEach(c -> availableMusics.put(c.getAsHash(), c));

            isListUpdated = true;
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void checkForMusic(String location) {
        if(!MusicConfig.INSTANCE.allowMusicModule) return;

        if(!isListUpdated) {
            checkForUpdates();
            return;
        }

        MusicProfile selected = null;

        Optional<MusicProfile> direct = Optional.empty();
        Optional<MusicProfile> firstWord = Optional.empty();
        Optional<MusicProfile> lessPossible = Optional.empty();

        for(MusicProfile mp : availableMusics.values()) {
            if(mp.getName().contains("(") && mp.getName().contains(")")) {
                Matcher mc = regex.matcher(mp.getName());
                while(mc.find()) {
                    String value = mc.group(1).replace("(", "").replace(")", "");
                    String toSearch = location.contains(" ") ? location.split(" ")[0] : location;
                    if(value.equalsIgnoreCase(location)) { //perfect match
                        direct = Optional.of(mp);
                    }else if(value.startsWith(toSearch)) { //match by first word
                        firstWord = Optional.of(mp);
                    }else if(value.startsWith(location)) { //match by entire location
                        lessPossible = Optional.of(mp);
                    }
                }
            }
        }

        if(direct.isPresent()) { selected = direct.get();
        }else if(firstWord.isPresent()) { selected = firstWord.get();
        }else if(lessPossible.isPresent()) { selected = lessPossible.get(); }

        if(selected == null) return;

        if(downloadedMusics.containsKey(selected.getAsHash()) && downloadedMusics.get(selected.getAsHash()).getFile().isPresent()) {
            player.play(downloadedMusics.get(selected.getAsHash()).getFile().get());
        }else{
            final MusicProfile toDownlaod = selected;
            downloadedMusics.put(toDownlaod.getAsHash(), toDownlaod);
            DownloaderManager.queueDownload(selected.getNameWithoutMP3(), selected.getDownloadUrl(), musicFolder, DownloadAction.SAVE, c -> {
                if(c) {
                    downloadedMusics.replace(toDownlaod.getAsHash(), new MusicProfile(new File(musicFolder, toDownlaod.getName())));
                    checkForMusic(location);
                }
            });
        }
    }

}
