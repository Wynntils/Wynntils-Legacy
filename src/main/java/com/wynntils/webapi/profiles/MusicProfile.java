/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.utils.Utils;

import java.io.File;
import java.util.Optional;

public class MusicProfile {

    String name, downloadUrl, nameWithoutMP3;
    long size;

    File f = null;

    public MusicProfile(String name, String downloadUrl, long size) {
        this.name = name; this.downloadUrl = downloadUrl; this.size = size;

        this.nameWithoutMP3 = name.replace(".mp3", "");
    }

    public MusicProfile(File f) {
        this.name = f.getName(); this.downloadUrl = null; this.size = f.length();
        this.nameWithoutMP3 = name.replace(".mp3", "");

        this.f = f;
    }

    public String getName() {
        return name;
    }

    public String getNameWithoutMP3() {
        return nameWithoutMP3;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public long getSize() {
        return size;
    }

    public Optional<File> getFile() {
        return Optional.of(f);
    }

    public String getAsHash() {
        return Utils.toMD5(name + size);
    }

    public boolean equalsTo(MusicProfile mp) {
        return this.size == mp.getSize();
    }

}
