/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.music.instances;

import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.MusicManager;
import com.wynntils.modules.richpresence.RichPresenceModule;
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

    public void play(File f) {
        if(currentMusic != null && currentMusic.getName().equalsIgnoreCase(f.getName())) return;

        // Queue the music change to the game update ticker
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChange) {
            GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChangeFormat
                    .replace("%np%", f.getName().replace(".mp3", "")));
        }

        nextMusic = f;
        setupController();
    }

    public void stop() {
        if(!active || currentPlayer == null) return;

        currentPlayer.stop();
        active = false;
        currentMusic = null;
        nextMusic = null;
    }

    private void checkForTheEnd() {
        if(!active) return;

        nextMusic = currentMusic;
        currentMusic = null;
    }

    public File getCurrentMusic() {
        return currentMusic;
    }

    public void setVolume(float volume) {
        if(!active || currentPlayer == null) return;
        if(currentPlayer.getAudioDevice() == null) return;

        if(currentPlayer.getAudioDevice() instanceof JavaSoundAudioDevice) {
            JavaSoundAudioDevice dv = (JavaSoundAudioDevice) currentPlayer.getAudioDevice();
            dv.setLineGain(volume);
            currentVolume = volume;
        }
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    public boolean isPaused() {
        return paused;
    }

    public void changePausedState() {
        paused = !paused;


        if(!paused) MusicManager.checkForMusic(RichPresenceModule.getModule().getData().getLocation());
        if(paused) stop();
    }

    public void setupController() {
        active = true;

        if(nextMusic != null) {
            if(currentMusic == null) {
                currentMusic = nextMusic;
                nextMusic = null;
                startReproduction();
            }else{
                if(currentVolume <= -30) {
                    currentMusic = nextMusic;
                    nextMusic = null;
                    startReproduction();
                }else{
                    setVolume(getCurrentVolume() - 0.2f);
                }
            }
        }else{
            if(getCurrentVolume() > (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                if(getCurrentVolume() - 0.2f < (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                    setVolume((Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume));
                }else{ setVolume(getCurrentVolume() - 0.2f); }
            }else if(getCurrentVolume() < (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                if(getCurrentVolume() + 0.2f > (Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume)) {
                    setVolume((Display.isActive() ? MusicConfig.INSTANCE.baseVolume : MusicConfig.INSTANCE.focusVolume));
                }else{ setVolume(getCurrentVolume() + 0.2f); }
            }
        }
    }

    private void startReproduction() {
        if(currentPlayer != null) {
            currentPlayer.stop();
            if(musicPlayer != null && musicPlayer.isAlive()) musicPlayer.stop();
        }

        if(paused) return;

        musicPlayer = new Thread(() -> {
            try{
                FileInputStream fis = new FileInputStream(currentMusic);
                BufferedInputStream bis = new BufferedInputStream(fis);
                currentPlayer = new AdvancedPlayer(bis);
                currentPlayer.setPlayBackListener(new PlaybackListener() {
                    public void playbackStarted(PlaybackEvent var1) {
                        setVolume(-30);
                    }
                    public void playbackFinished(PlaybackEvent var1) { checkForTheEnd(); }
                });

                currentPlayer.play();

                fis.close();
                bis.close();
            }catch (Exception ex) { ex.printStackTrace(); }
        });
        musicPlayer.setName("Wynntils - Music Reproducer"); musicPlayer.start();
    }

}
