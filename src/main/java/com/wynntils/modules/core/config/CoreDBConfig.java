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

@SettingsInfo(name = "main", displayPath = "wynntils.config.core.display_path")
public class CoreDBConfig extends SettingsClass {
    public static CoreDBConfig INSTANCE;

    @Setting(upload = false)
    public String lastToken = "";

    @Setting(upload = false)
    public int lastSelectedClass = -1;

    @Setting(upload = false)
    public ClassType lastClass = ClassType.NONE;

    @Setting(displayName = "wynntils.config.core.update_stream.display_name", description = "wynntils.config.core.update_stream.description", upload = false)
    public UpdateStream updateStream = UpdateStream.STABLE;

    @Setting(displayName = "wynntils.config.core.scroll_direction.display_name", description = "wynntils.config.core.scroll_direction.description")
    public ScrollDirection scrollDirection = ScrollDirection.DOWN;

    @Setting(displayName = "wynntils.config.core.enable_changelog_on_update.display_name", description = "wynntils.config.core.enable_changelog_on_update.description")
    public boolean enableChangelogOnUpdate = true;

    @Setting(upload = false)
    public boolean showChangelogs = true;

    @Setting(upload = false)
    public String lastVersion = "0.0.0";

    @Setting(displayName = "Main Menu Wynncraft Button", description = "Should a button be added to the main menu that allows you to connect to Wynncraft directly?")
    public boolean addMainMenuButton = true;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("updateStream")) WebManager.checkForUpdates();
    }
}
