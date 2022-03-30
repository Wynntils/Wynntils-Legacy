/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.music.instances;

import java.io.File;

public class QueuedTrack {

    File track;
    boolean fadeIn, fadeOut, fastSwitch, repeat, lockQueue, quiet;

    public QueuedTrack(File track, boolean fadeIn, boolean fadeOut, boolean fastSwitch, boolean repeat, boolean lockQueue, boolean quiet) {
        this.track = track;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.fastSwitch = fastSwitch;
        this.repeat = repeat;
        this.lockQueue = lockQueue;
        this.quiet = quiet;
    }

    public boolean isFadeIn() {
        return fadeIn;
    }

    public boolean isFadeOut() {
        return fadeOut;
    }

    public boolean isFastSwitch() {
        return fastSwitch;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isLockQueue() {
        return lockQueue;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public File getTrack() {
        return track;
    }

    public String getName() {
        return track.getName().replace(".mp3", "");
    }

    public boolean equals(QueuedTrack track) {
        return getName().equals(track.getName());
    }

}
