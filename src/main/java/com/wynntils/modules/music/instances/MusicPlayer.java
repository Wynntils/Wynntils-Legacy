/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.instances;

import com.wynntils.core.events.custom.MusicPlayerEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.modules.music.configs.MusicConfig;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.lwjgl.opengl.Display;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MusicPlayer {

    private final PlayerStatus STATUS = new PlayerStatus();

    Thread musicThread = null;
    AdvancedPlayer player = null;

    public void play(File f, boolean fastSwitch) {
        if (STATUS.getCurrentSong() != null && STATUS.getCurrentSong().getName().equalsIgnoreCase(f.getName())) return;

        STATUS.setFastSwitch(fastSwitch);
        STATUS.setStopping(false);
        STATUS.setNextSong(f);

        FrameworkManager.getEventBus().post(new MusicPlayerEvent.Playback.Start(f.getName().replace(".mp3", "")));
    }

    public void update() {
        if (STATUS.getCurrentSong() != null && !isPlaying()) {
            initialize();
            return;
        }

        if (STATUS.isStopping() || STATUS.isPaused()) {
            if (STATUS.getCurrentGain() < -30f) { // song already vanished off
                if (STATUS.isStopping()) {
                    STATUS.setStopping(false);
                    clear();
                }

                kill();
                return;
            }

            STATUS.setCurrentGain(STATUS.getCurrentGain() - 1f);
            setGain(STATUS.getCurrentGain());
            return;
        }

        if (STATUS.getNextSong() != null) {
            if (STATUS.getCurrentGain() < -30f) { // current song already vanished off
                // resetting statuses
                STATUS.setCurrentSong(STATUS.getNextSong());
                STATUS.setNextSong(null);
                STATUS.setFastSwitch(false);

                // restarting the player
                kill();
                return;
            }

            STATUS.setCurrentGain(STATUS.getCurrentGain() - (STATUS.isFastSwitch() ? 1f : 0.2f));
            setGain(STATUS.getCurrentGain());
            return;
        }

        // update the volume
        float expectedGain = Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume;

        if (STATUS.getCurrentGain() > expectedGain) {
            STATUS.setCurrentGain(Math.max(STATUS.getCurrentGain() - 0.2f, expectedGain));
        } else if (STATUS.getCurrentGain() < expectedGain) {
            STATUS.setCurrentGain(Math.min(STATUS.getCurrentGain() + 0.2f, expectedGain));
        }

        setGain(STATUS.getCurrentGain());
    }

    public void stop() {
        clear();
        kill();
    }

    public boolean isPlaying() {
        return player != null || musicThread != null;
    }

    public PlayerStatus getStatus() {
        return STATUS;
    }

    private void setGain(float gain) {
        if (player == null) return;
        if (player.getAudioDevice() == null || !(player.getAudioDevice() instanceof JavaSoundAudioDevice)) return;

        JavaSoundAudioDevice dv = (JavaSoundAudioDevice) player.getAudioDevice();
        dv.setLineGain(gain);
    }

    private void clear() {
        if (!isPlaying()) return;

        STATUS.setNextSong(null);
        STATUS.setCurrentSong(null);
    }

    private void kill() {
        if (!isPlaying()) return;

        player.stop();
        player = null;

        musicThread.interrupt();
        musicThread = null;
    }

    private void initialize() {
        if (player != null) kill();
        if (STATUS.isPaused() || STATUS.getCurrentSong() == null) return; // we don't want the player to play if paused

        musicThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(STATUS.getCurrentSong());
                BufferedInputStream bis = new BufferedInputStream(fis);

                player = new AdvancedPlayer(bis);
                player.setPlayBackListener(new PlaybackListener() {
                    // starts the song muted
                    public void playbackStarted(PlaybackEvent var1) {
                        STATUS.setCurrentGain(-30f);
                    }

                    // handles the replay
                    public void playbackFinished(PlaybackEvent var1) {
                        if (!STATUS.isRepeat() || STATUS.isPaused()) return;

                        FrameworkManager.getEventBus().post(new MusicPlayerEvent.Playback.End(STATUS.getFormattedCurrentSongName()));
                        STATUS.setNextSong(STATUS.getCurrentSong());
                        STATUS.setCurrentSong(null);
                    }
                });
                player.play();

                fis.close();
                bis.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        musicThread.setName("Wynntils - Music Player");
        musicThread.start();
    }

}
