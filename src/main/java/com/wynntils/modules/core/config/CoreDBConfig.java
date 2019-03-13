/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.config;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.core.enums.ScrollDirection;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;

@SettingsInfo(name = "main", displayPath = "Core")
public class CoreDBConfig extends SettingsClass {
    public static CoreDBConfig INSTANCE;

    @Setting(upload = false)
    public String lastToken = "";

    @Setting(upload = false)
    public int lastSelectedClass = -1;

    @Setting(upload = false)
    public ClassType lastClass = ClassType.NONE;

    @Setting(displayName = "Update Stream", description = "Which update stream should the mod be on?\n\n" +
            "§2Stable: §rThe mod will only update when a new version is released. Stable versions are generally more stable than Cutting Edge builds.\n\n" +
            "§4Cutting Edge: §rThe mod will update whenever a new build is release. Cutting Edge builds will include features that are currently in development, but may also be less stable than Stable versions.", upload = false)
    public UpdateStream updateStream = UpdateStream.STABLE;

    @Setting(displayName = "Scroll Direction", description = "Which direction would you like to scroll your mouse in order for the page to scroll down?")
    public ScrollDirection scrollDirection = ScrollDirection.DOWN;

    @Setting(displayName = "Show Changelog", description = "Should the changelog of your recent update be displayed once you log in after updating?")
    public boolean enableChangelogOnUpdate = true;
    public boolean justUpdates = false;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("updateStream")) WebManager.checkForUpdates();
    }
}
