/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.instances;

import java.io.File;

public class PlayerStatus {

    private boolean repeat = true;
    private boolean paused = false;
    private boolean fastSwitch = false;
    private boolean stopping = false;

    private float currentGain = 1f;

    private File currentSong = null;
    private File nextSong = null;

    public File getCurrentSong() {
        return currentSong;
    }

    public File getNextSong() {
        return nextSong;
    }

    public String getFormattedCurrentSongName() {
        return currentSong.getName().replace(".mp3", "");
    }

    public float getCurrentGain() {
        return currentGain;
    }

    public boolean isFastSwitch() {
        return fastSwitch;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isStopping() {
        return stopping;
    }

    public void setCurrentSong(File currentSong) {
        this.currentSong = currentSong;
    }

    public void setNextSong(File nextSong) {
        this.nextSong = nextSong;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setFastSwitch(boolean fastSwitch) {
        this.fastSwitch = fastSwitch;
    }

    public void setCurrentGain(float currentGain) {
        this.currentGain = currentGain;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

}
