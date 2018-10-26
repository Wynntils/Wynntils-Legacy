/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.instances;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MusicPlayer {

    private static PlayerExecutor executor = new PlayerExecutor();

    public static void play(File f) {
        executor.play(f);
    }

    public static void stop() {
        executor.stop();
    }

    public static class PlayerExecutor {

        File f;
        AdvancedPlayer currentPlayer;
        Thread th;

        boolean playing = false;

        public void play(File f) {
            if(this.f != null && this.f.getName().equalsIgnoreCase(f.getName())) return;
            if(playing || (th != null && th.isAlive())) stop();

            playing = true;
            this.f = f;
            run();
        }

        public void stop() {
            if(!playing) return;

            currentPlayer.stop();
            if(th != null && th.isAlive()) th.stop();
        }

        public void checkForTheEnd() {
            if(!playing) return;

            run();
        }

        private void run() {
           th = new Thread(() -> {
                try{
                    FileInputStream fis = new FileInputStream(f);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    currentPlayer = new AdvancedPlayer(bis);
                    currentPlayer.setPlayBackListener(new PlaybackListener() {
                        public void playbackFinished(PlaybackEvent var1) { checkForTheEnd(); }
                    });

                    currentPlayer.play();
                    fis.close();
                    bis.close();
                }catch (Exception ex) { ex.printStackTrace(); }
            });
           th.setName("Wynntils - Music Module"); th.start();
        }
    }

}
