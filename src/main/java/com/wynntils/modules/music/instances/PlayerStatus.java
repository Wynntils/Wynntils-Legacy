/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.music.instances;

public class PlayerStatus {

    private boolean paused = false;
    private boolean pauseAfter = false;
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

    public boolean isPauseAfter() {
        return pauseAfter;
    }

    public boolean isStopping() {
        return stopping;
    }

    public boolean isCurrentQuiet() {
        return currentSong != null && currentSong.isQuiet();
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

    public void setPauseAfter(boolean pauseAfter) {
        this.pauseAfter = pauseAfter;
    }

    public void setCurrentGain(float currentGain) {
        this.currentGain = currentGain;
    }

    public void setStopping(boolean stopping) {
        this.stopping = stopping;
    }

}
