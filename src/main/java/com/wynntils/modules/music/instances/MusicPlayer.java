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
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import org.lwjgl.opengl.Display;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MusicPlayer {

    private final PlayerStatus STATUS = new PlayerStatus();

    Thread musicThread = null;
    AdvancedPlayer player = null;

    public void play(File f, boolean fadeIn, boolean fadeOut, boolean fastSwitch, boolean repeat, boolean lockQueue) {
        QueuedTrack track = new QueuedTrack(f, fadeIn, fadeOut, fastSwitch, repeat, lockQueue);
        if (STATUS.getCurrentSong() != null && (STATUS.getCurrentSong().isLockQueue() || STATUS.getCurrentSong().equals(track))) return;
        if (STATUS.getNextSong() != null && STATUS.getNextSong().equals(track)) return;

        STATUS.setStopping(false);
        STATUS.setNextSong(track);

        FrameworkManager.getEventBus().post(new MusicPlayerEvent.Playback.Start(f.getName()));
    }

    public void play(File f, boolean fastSwitch) {
        play(f, true, true, fastSwitch, true, false);
    }

    public void play(File f, boolean fastSwitch, boolean lockQueue) {
        play(f, true, true, fastSwitch, true, lockQueue);
    }

    public void update() {
        if (STATUS.getCurrentSong() != null && !isPlaying()) {
            initialize();
            return;
        }

        // stopping/pause transition
        if (STATUS.isStopping() || STATUS.isPaused()) {
            if (STATUS.getCurrentGain() < -36f) { // song already vanished off
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

        // next song transition
        if (STATUS.getNextSong() != null) {
            if (!STATUS.getNextSong().fadeOut || STATUS.getCurrentGain() < -36f) { // current song already vanished off
                // resetting statuses
                STATUS.setCurrentSong(STATUS.getNextSong());
                STATUS.setNextSong(null);

                // restarting the player
                kill();
                return;
            }

            STATUS.setCurrentGain(STATUS.getCurrentGain() - (STATUS.getNextSong().isFastSwitch() ? 1f : MusicConfig.INSTANCE.switchJump));
            setGain(STATUS.getCurrentGain());
            return;
        }

        // update the volume
        float baseVolume = -36 + (36 * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS));
        float expectedGain = Display.isActive() ? baseVolume : Math.max(-30, baseVolume + MusicConfig.INSTANCE.focusOffset);

        if (STATUS.getCurrentGain() > expectedGain) {
            STATUS.setCurrentGain(Math.max(STATUS.getCurrentGain() - 0.2f, expectedGain));
        } else if (STATUS.getCurrentGain() < expectedGain) {
            STATUS.setCurrentGain(Math.min(STATUS.getCurrentGain() + 0.2f, expectedGain));
        }

        setGain(STATUS.getCurrentGain());
    }

    public void stop() {
        STATUS.setStopping(true);
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

        if (player != null) {
            player.stop();
            player = null;
        }

        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
    }

    private void initialize() {
        if (player != null) kill();
        if (STATUS.isPaused() || STATUS.getCurrentSong() == null) return; // we don't want the player to play if paused

        musicThread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(STATUS.getCurrentSong().getTrack());
                BufferedInputStream bis = new BufferedInputStream(fis);

                player = new AdvancedPlayer(bis);
                player.setPlayBackListener(new PlaybackListener() {
                    // starts the song muted
                    public void playbackStarted(PlaybackEvent var1) {
                        float baseVolume = -32 + (32 * Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS));
                        STATUS.setCurrentGain(STATUS.getCurrentSong().isFadeIn() ? -30f : baseVolume);
                    }

                    // handles the replay
                    public void playbackFinished(PlaybackEvent var1) {
                        FrameworkManager.getEventBus().post(new MusicPlayerEvent.Playback.End(STATUS.getCurrentSong().getName()));
                        if (!STATUS.getCurrentSong().isRepeat() || STATUS.isPaused()) {
                            STATUS.setCurrentSong(null);
                            return;
                        }

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

    public boolean isPlaying() {
        return player != null || musicThread != null;
    }

    public PlayerStatus getStatus() {
        return STATUS;
    }

}
