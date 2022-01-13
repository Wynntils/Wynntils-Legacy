/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles;

import java.io.File;
import java.util.Optional;

import com.wynntils.core.utils.StringUtils;

public class MusicProfile {

    String name, downloadUrl, formattedName;
    long size;

    File f = null;

    public MusicProfile(String name, String downloadUrl, long size) {
        this.name = name; this.downloadUrl = downloadUrl; this.size = size;

        this.formattedName = name.replace(".mp3", "");
    }

    public MusicProfile(File f) {
        this.name = f.getName(); this.downloadUrl = null; this.size = f.length();
        this.formattedName = name.replace(".mp3", "");

        this.f = f;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public long getSize() {
        return size;
    }

    public Optional<File> getFile() {
        return Optional.ofNullable(f);
    }

    public String getAsHash() {
        return StringUtils.toMD5(name + size);
    }

    public boolean equalsTo(MusicProfile mp) {
        return this.size == mp.getSize();
    }

}
