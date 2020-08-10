/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.instances;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.MusicManager;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.lwjgl.opengl.Display;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class MusicPlayer {

    Thread musicPlayer;
    boolean active = false;

    float currentVolume = 1;
    File currentMusic;
    File nextMusic = null;
    AdvancedPlayer currentPlayer;

    boolean paused = false;
    boolean fastSwitch = false;

    public void play(File f, boolean fastSwitch) {
        if (currentMusic != null && currentMusic.getName().equalsIgnoreCase(f.getName())) return;

        // Queue the music change to the game update ticker
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChange) {
            GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChangeFormat
                    .replace("%np%", f.getName().replace(".mp3", "")));
        }

        this.fastSwitch = fastSwitch;
        nextMusic = f;
        updatePlayer();
    }

    public void stop() {
        if (!active || currentPlayer == null) return;

        currentPlayer.stop();
        active = false;
        currentMusic = null;
        nextMusic = null;
    }

    private void checkForTheEnd() {
        if (!active) return;

        nextMusic = currentMusic;
        currentMusic = null;
    }

    public File getCurrentMusic() {
        return currentMusic;
    }

    public void setGain(float gain) {
        if (!active || currentPlayer == null) return;
        if (currentPlayer.getAudioDevice() == null) return;
        if (!(currentPlayer.getAudioDevice() instanceof JavaSoundAudioDevice)) return;

        JavaSoundAudioDevice dv = (JavaSoundAudioDevice) currentPlayer.getAudioDevice();
        dv.setLineGain(gain);
        currentVolume = gain;
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    public boolean isPaused() {
        return paused;
    }

    public void changePausedState() {
        paused = !paused;


        if (!paused) MusicManager.checkForMusic(PlayerInfo.getPlayerInfo().getLocation());
        if (paused) stop();
    }

    public void updatePlayer() {
        active = true;

        if (nextMusic != null) {
            if (currentMusic == null || currentVolume < -30) {
                currentMusic = nextMusic;
                nextMusic = null;

                fastSwitch = false;

                startReproduction();
                return;
            }

            setGain(getCurrentVolume() - (fastSwitch ? 1f : 0.2f));
            return;
        }

        if (getCurrentVolume() > (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
            if (getCurrentVolume() - 0.2f < (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                setGain((Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume));
            } else { setGain(getCurrentVolume() - 0.2f); }
        } else if (getCurrentVolume() < (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
            if (getCurrentVolume() + 0.2f > (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                setGain((Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume));
            } else { setGain(getCurrentVolume() + 0.2f); }
        }
    }

    @SuppressWarnings("deprecation")
    private void startReproduction() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            if (musicPlayer != null && musicPlayer.isAlive()) musicPlayer.stop();
        }

        if (paused) return;

        musicPlayer = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(currentMusic);
                BufferedInputStream bis = new BufferedInputStream(fis);
                currentPlayer = new AdvancedPlayer(bis);
                currentPlayer.setPlayBackListener(new PlaybackListener() {
                    public void playbackStarted(PlaybackEvent var1) {
                        setGain(-30);
                    }
                    public void playbackFinished(PlaybackEvent var1) { checkForTheEnd(); }
                });

                currentPlayer.play();

                fis.close();
                bis.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        musicPlayer.setName("Wynntils - Music Player"); musicPlayer.start();
    }

}
