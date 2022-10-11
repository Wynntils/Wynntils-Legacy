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

    public UpdateProfile() {
        new Thread(() -> {
            try {
                MD5Verification md5Installed = new MD5Verification(ModCore.jarFile);
                HashMap<String, String> updateData = WebManager.getUpdateData(CoreDBConfig.INSTANCE.updateStream);

                if (!md5Installed.equals(updateData.get("md5"))) {
                    hasUpdate = true;
                    latestUpdate = updateData.get("version");
                    downloadUrl = updateData.get("url");
                    UpdateOverlay.reset();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                updateCheckFailed = true;
            }
        }, "wynntils-updateprofile").start();
    }

    public boolean hasUpdate() {
        return (!updateDownloaded && hasUpdate);
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

    public boolean updateCheckFailed() {
        return updateCheckFailed;
    }
}
