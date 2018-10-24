/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.managers;

import cf.wynntils.Reference;
import cf.wynntils.modules.music.configs.MusicConfig;
import cf.wynntils.modules.music.instances.MusicPlayer;
import cf.wynntils.modules.richpresence.RichPresenceModule;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadAction;
import cf.wynntils.webapi.profiles.MusicProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicManager {

    private static HashMap<Long, MusicProfile> availableMusics = new HashMap<>();
    private static MusicPlayer player = new MusicPlayer();

    private static final Pattern regex = Pattern.compile("\\(([^)]+)\\)");
    private static final Random r = new Random();

    private static boolean checked = false;

    public static void checkForUpdates() {
        if(!MusicConfig.INSTANCE.allowMusicModule) return;

        File musicFolder = new File(Reference.MOD_STORAGE_ROOT, "sounds");
        if(!musicFolder.exists() || !musicFolder.isDirectory()) musicFolder.mkdirs();

        File[] listOfFiles = musicFolder.listFiles();
        if(listOfFiles.length >= 1) {
            for(File f : listOfFiles) {
                availableMusics.put(f.length(), new MusicProfile(f));
            }
        }

        try{
            ArrayList<MusicProfile> updated = WebManager.getCurrentAvailableSongs();

            for(MusicProfile mp : updated) {
                if(!availableMusics.containsKey(mp.getSize())) {
                    DownloaderManager.queueDownload(mp.getNameWithoutMP3(), mp.getDownloadUrl(), musicFolder, DownloadAction.SAVE, c -> {
                        if(c) {
                            availableMusics.put(mp.getSize(), new MusicProfile(new File(musicFolder, mp.getName())));
                            if(availableMusics.size() >= updated.size()) checked = true;
                        }
                    });
                }
            }

            if(availableMusics.size() >= updated.size()) checked = true;
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void checkForMusic() {
        if(!MusicConfig.INSTANCE.allowMusicModule) return;

        if(!checked) checkForUpdates();

        String location = RichPresenceModule.getModule().getData().getLocation();

        MusicProfile selected = null;

        //direct search
        for(MusicProfile mp : availableMusics.values()) {
            if(mp.getName().contains("(") && mp.getName().contains(")")) {
                Matcher mc = regex.matcher(mp.getName());
                while(mc.find()) {
                    if(mc.group(1).replace("(", "").replace(")", "").equalsIgnoreCase(location)) {
                        selected = mp;
                        break;
                    }
                }
            }
        }

        //less possible search
        if(selected == null) {
            for(MusicProfile mp : availableMusics.values()) {
                if(mp.getName().contains("(") && mp.getName().contains(")")) {
                    Matcher mc = regex.matcher(mp.getName());
                    while(mc.find()) {
                        if(mc.group(1).replace("(", "").replace(")", "").startsWith(location)) {
                            selected = mp;
                            break;
                        }
                    }
                }
            }
        }

        //first word search
        if(selected == null) {
            for(MusicProfile mp : availableMusics.values()) {
                if(mp.getName().contains("(") && mp.getName().contains(")")) {
                    Matcher mc = regex.matcher(mp.getName());
                    String toSearch = location.contains(" ") ? location.split(" ")[0] : location;
                    while(mc.find()) {
                        if(mc.group(1).replace("(", "").replace(")", "").startsWith(toSearch)) {
                            selected = mp;
                            break;
                        }
                    }
                }
            }
        }

        player.play(selected.getFile().get());
    }

}
