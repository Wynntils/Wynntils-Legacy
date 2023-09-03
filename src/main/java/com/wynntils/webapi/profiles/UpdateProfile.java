/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import com.wynntils.webapi.WebManager;

import java.util.HashMap;

public class UpdateProfile {

    static boolean updateDownloaded = false;

    boolean hasUpdate = false;
    boolean updateCheckFailed = false;
    String latestUpdate = Reference.VERSION;
    String downloadUrl = null;
    String downloadMD5 = null;
    String md5Installed = null;

    public UpdateProfile() {
        new Thread(() -> {
            try {
                HashMap<String, String> updateData = WebManager.getUpdateData(CoreDBConfig.INSTANCE.updateStream);
                latestUpdate = updateData.get("version").replace("v", "");
                downloadUrl = updateData.get("url");
                downloadMD5 = updateData.get("md5");

                md5Installed = new MD5Verification(ModCore.jarFile).getMd5();
                if (md5Installed == null) {
                    updateCheckFailed = true;
                    return;
                }

                if (!md5Installed.equals(downloadMD5)) {
                    Reference.LOGGER.info("Update found for version " + latestUpdate + " (" + downloadMD5 + ") Current version: " + Reference.VERSION + " (" + md5Installed + ")");
                    hasUpdate = true;
                    UpdateOverlay.reset();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                updateCheckFailed = true;
            }
        }, "wynntils-updateprofile").start();
    }

    public boolean hasUpdate() {
        return !updateDownloaded && hasUpdate;
    }

    public void updateDownloaded() {
        updateDownloaded = true;
    }

    public void forceUpdate() {
        UpdateOverlay.reset();
        hasUpdate = true;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getDownloadMD5() {
        return downloadMD5;
    }

    public String getMd5Installed() {
        return md5Installed;
    }

    public boolean updateCheckFailed() {
        return updateCheckFailed;
    }
}
