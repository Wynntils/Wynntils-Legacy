/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.cosmetics.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "cosmetics", displayPath = "Cosmetics")
public class CosmeticsConfig extends SettingsClass {
    public static CosmeticsConfig INSTANCE;

    @Setting(displayName = "Force Cape Rendering", description = "Should capes be rendered even if Minecraft cape rendering is turned off?")
    public boolean forceCapes = false;
}
