/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.config;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;

@SettingsInfo(name = "main", displayPath = "Core")
public class CoreDBConfig extends SettingsClass {

    public static CoreDBConfig INSTANCE;

    public String lastToken = "";
    public int lastSelectedClass = -1;
    public ClassType lastClass = ClassType.NONE;

    @Setting(displayName = "Update Stream", description = "The update stream to use.\n" +
            "§2Stable: Only update when a new version is released, will generally by the more stable than cutting edge.\n" +
            "§4Cutting Edge: Update whenever a new build is release, will include features currently in development.")
    public UpdateStream updateStream = UpdateStream.STABLE;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("updateStream"))
            WebManager.checkForUpdates();
    }
}
