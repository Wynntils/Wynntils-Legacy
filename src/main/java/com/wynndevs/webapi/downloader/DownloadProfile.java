package com.wynndevs.webapi.downloader;

import java.io.File;
import java.util.function.Consumer;

class DownloadProfile {

    String url; File location; Consumer<Boolean> onFinish;

    public DownloadProfile(String url, File location, Consumer<Boolean> onFinish) {
        this.url = url; this.location = location; this.onFinish = onFinish;
    }

    public String getUrl() {
        return url;
    }

    public File getLocation() {
        return location;
    }

}
