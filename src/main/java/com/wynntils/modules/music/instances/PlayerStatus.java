/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.instances;

import java.io.File;

public class PlayerStatus {

    private boolean paused = false;
    private boolean stopping = false;

    private float currentGain = 1f;

    private QueuedTrack currentSong = null;
    private QueuedTrack nextSong = null;

    public QueuedTrack getCurrentSong() {
        return currentSong;
    }

    public QueuedTrack getNextSong() {
        return nextSong;
    }

    public float getCurrentGain() {
        return currentGain;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopping() {
        return stopping;
    }

    public void setCurrentSong(QueuedTrack currentSong) {
        this.currentSong = currentSong;
    }

    public void setNextSong(QueuedTrack nextSong) {
        this.nextSong = nextSong;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setCurrentGain(float currentGain) {
        this.currentGain = currentGain;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

}
