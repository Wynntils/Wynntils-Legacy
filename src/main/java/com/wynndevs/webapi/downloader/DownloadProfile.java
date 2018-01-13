package com.wynndevs.webapi.downloader;

import java.io.File;
import java.util.function.Consumer;

public class DownloadProfile {

    String title; String url; File location; Consumer<Boolean> onFinish;

    public DownloadProfile(String title, String url, File location, Consumer<Boolean> onFinish) {
        this.title = title; this.url = url; this.location = location; this.onFinish = onFinish;
    }

    public String getUrl() {
        return url;
    }

    public File getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

}
