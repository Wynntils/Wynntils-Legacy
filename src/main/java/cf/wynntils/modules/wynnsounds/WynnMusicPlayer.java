package cf.wynntils.modules.wynnsounds;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.ActionResult;
import cf.wynntils.core.utils.NanoTracker;
import com.sun.istack.internal.NotNull;
import javazoom.jlgui.basicplayer.BasicPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class WynnMusicPlayer {
    public static BasicPlayer player;
    public static final File MUSIC_ROOT_FOLDER = new File(Reference.MOD_ASSETS_ROOT + "\\wynnsounds");

    static File curSong = null;

    static Runnable readThread = new Runnable() {
        @Override
        public synchronized void run() {
            try {
                NanoTracker.report(true);
                if(player != null) {
                    if(player.getStatus() == 0) player.stop();
                    player = null;
                }
                NanoTracker.report(true);
                BasicPlayer splayer = new BasicPlayer();
                NanoTracker.report(true);
                splayer.open(curSong);
                NanoTracker.report(true);
                splayer.play();
                NanoTracker.report(true);
                player = splayer;
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    };

    public static boolean updateVolume() {
        if(player != null && player.getStatus() != 0) player = null;
        try {
            float v = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MUSIC);
            player.setGain(v == 1d ? player.getMaximumGain() : (v == 0d ? player.getMinimumGain() : v));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public static void playSong(@NotNull String name) {
        NanoTracker.start();
        File songFile = new File(MUSIC_ROOT_FOLDER + "\\" + name);
        NanoTracker.report(true);
        if (name.equals("")) return;
        NanoTracker.report(true);
        curSong = songFile;
        readThread.run();
        NanoTracker.report(true);
    }

    public static class MusicManager {
        static Random random = new Random();

        public static String getRandomSongName() {
            if(Files.notExists(MUSIC_ROOT_FOLDER.toPath())) return "";
            String[] songs = MUSIC_ROOT_FOLDER.list();
            return new File(songs[random.nextInt(songs.length)]).getName();
        }
    }
}
