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

    PlayerExecutor executor = new PlayerExecutor();

    public void play(File f) {
        executor.play(f);
    }

    public void stop() {
        executor.stop();
    }

    public class PlayerExecutor {

        File f;
        AdvancedPlayer currentPlayer;

        boolean playing = false;

        public void play(File f) {
            if(this.f != null && this.f.getName().equalsIgnoreCase(f.getName())) return;
            if(playing) stop();

            this.f = f;
            run();
        }

        public void stop() {
            if(!playing) return;

            currentPlayer.stop();
        }

        public void checkForTheEnd() {
            if(!playing) return;

            run();
        }

        private void run() {
            new Thread(() -> {
                try{
                    FileInputStream fis = new FileInputStream(f);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    currentPlayer = new AdvancedPlayer(bis);
                    currentPlayer.setPlayBackListener(new PlaybackListener() {
                        public void playbackStarted(PlaybackEvent playbackEvent) { playing = true; }
                        public void playbackFinished(PlaybackEvent var1) { checkForTheEnd(); }
                    });

                    currentPlayer.play();
                    fis.close();
                    bis.close();
                }catch (Exception ex) { }
            }).start();
        }
    }

}
