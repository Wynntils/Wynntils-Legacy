package cf.wynntils.modules.wynnsounds;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.exceptions.NotImplementedException;
import javazoom.jlgui.basicplayer.BasicPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class WynnMusicPlayer {
    public static final File MUSIC_ROOT_FOLDER = new File(Reference.MOD_ASSETS_ROOT + "\\wynnsounds");

    private static int status = -1;
    private static File curSong = null;

    private static boolean engineRunning = false;
    private static Thread runningPlayer;

    static void startEngine() {
        if(engineRunning) return;
        status = -1;
        curSong = null;
        runningPlayer = new Thread(new RunningPlayer());
        runningPlayer.start();
        engineRunning = true;
    }

    static void stopEngine() {
        if(!engineRunning) return;
        status = -1;
        curSong = null;
        runningPlayer.interrupt();
        runningPlayer = null;
        engineRunning = false;
    }

    private static void noSongPlaying() {

    }

    public static void playSong(String name) {
        if(name == null || name.equals("")) return;
        if(ModCore.DEBUG) System.out.println("Now playing: " + name);
        curSong = new File(MUSIC_ROOT_FOLDER + "\\" + name);
    }

    public static class MusicManager {
        static Random random = new Random();

        public static String getRandomSongName() {
            if (Files.notExists(MUSIC_ROOT_FOLDER.toPath())) return "";
            String[] songs = MUSIC_ROOT_FOLDER.list();
            return new File(songs[random.nextInt(songs.length)]).getName();
        }

        public static String getSongFromArea(String area) {
            throw new NotImplementedException();
        }
    }
    public static class RunningPlayer implements Runnable {
        BasicPlayer player;
        @Override
        public synchronized void run() {
            System.out.println("Wynnsounds Music Engine starting");
            player = new BasicPlayer();
            File currentlyPlaying = null;
            double currentVolume = -1d;
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    if(status == -999) {
                        player.stop();
                    } else {
                        if (curSong != null && (currentlyPlaying == null || !currentlyPlaying.equals(curSong))) {
                            player.stop();
                            player.open(curSong);
                            player.play();
                            currentlyPlaying = curSong;
                        }
                        double v = (double) (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MUSIC)*Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER));
                        if (v != currentVolume) {
                            player.setGain(v);
                            currentVolume = v;
                        }
                        status = player.getStatus();
                        if(status != 0)
                            noSongPlaying();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Wynnsounds Music Engine shutting down");
            try{ player.stop(); } catch (Exception ignored) { }
        }
    }
}
