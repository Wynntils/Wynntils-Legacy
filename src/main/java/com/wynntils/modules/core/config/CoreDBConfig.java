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

    @Setting(displayName = "Update Stream", description = "Which update stream should the mod be on?\n" +
            "§2Stable: The mod will only update when a new version is released. Stable versions are generally more stable than Cutting Edge builds.\n" +
            "§4Cutting Edge: The mod will update whenever a new build is release. Cutting Edge builds will include features that are currently in development, but may also be less stable than Stable versions.")
    public UpdateStream updateStream = UpdateStream.STABLE;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("updateStream"))
            WebManager.checkForUpdates();
    }
}
