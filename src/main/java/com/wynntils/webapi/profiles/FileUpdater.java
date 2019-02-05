/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.utils.Utils;
import com.wynntils.webapi.WebReader;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUpdater {

    String main_url;
    File location;
    Runnable runnable;

    WebReader reader;

    public FileUpdater(String main_url, File location) throws Exception {
        this.main_url = main_url;
        this.location = location;

        this.location.mkdirs();

        this.reader = new WebReader(main_url);
    }

    public FileUpdater whenUpdateComplete(Runnable runnable) {
        this.runnable = runnable;

        return this;
    }

    public FileUpdater startUpdating() throws NullPointerException {
        if(reader == null) throw new NullPointerException("Web reader is null");
        if(runnable == null) throw new NullPointerException("There is no specified runnable");

        HashMap<String, String> values = reader.getValues();

        ArrayList<String> localFiles = new ArrayList<>();
        for(File f : location.listFiles()) {
            localFiles.add(f.getName());
        }

        int count = 0;
        boolean hasUpdatedSomething = false;
        for(String fileName : values.keySet()) {
            count++;

            File f = new File(location, fileName);
            if(f.exists()) {
                if(Utils.toMD5(String.valueOf(f.length())).equals(values.get(fileName))) {
                    localFiles.remove(fileName);
                    continue;
                }

                hasUpdatedSomething = true;
                if(count == values.size()) {
                    DownloaderManager.queueDownload(fileName, main_url + "/" + fileName, location, DownloadAction.SAVE, (b) -> runnable.run());
                }else{
                    DownloaderManager.queueDownload(fileName, main_url + "/" + fileName, location, DownloadAction.SAVE, (b) -> {});
                }

                localFiles.remove(fileName);
                continue;
            }

            hasUpdatedSomething = true;
            if(count == values.size()) {
                DownloaderManager.queueDownload(fileName, main_url + "/" + fileName, location, DownloadAction.SAVE, (b) -> runnable.run());
            }else{
                DownloaderManager.queueDownload(fileName, main_url + "/" + fileName, location, DownloadAction.SAVE, (b) -> {});
            }
        }

        if(localFiles.size() > 0) {
            for(String toRemove : localFiles) {
                new File(location, toRemove).delete();
            }
        }

        if(!hasUpdatedSomething) {
            runnable.run();
        }

        return this;
    }

}
