/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "visual", displayPath = "Visual")
public class VisualConfig extends SettingsClass {
    public static VisualConfig INSTANCE;

    @SettingsInfo(name = "damageSplash", displayPath = "Visual/Damage Splash")
    public static class DamageSplash extends SettingsClass {
        public static DamageSplash INSTANCE;

        @Setting(displayName = "Replace Wynncraft Damage Splashes", description = "Should the Wynncraft Damage splashes be replaced.")
        public boolean enabled = true;

        @Setting(displayName = "Splash Duration", description = "How much ticks should the splash live.")
        @Setting.Limitations.IntLimit(min = 50, max = 800)
        public int maxLiving = 150;

        @Setting(displayName = "Initial Splash Scale", description = "The initial size of the splash.")
        @Setting.Limitations.FloatLimit(min = 1f, max = 4f)
        public float initialScale = 2.5f;
    }

    @SettingsInfo(name = "fireflies", displayPath = "Visual/Fireflies")
    public static class Fireflies extends SettingsClass {
        public static Fireflies INSTANCE;

        @Setting(displayName = "Visual Fireflies", description = "Should fireflies be rendered in certain areas.")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "How much ticks should the firefly live in max.")
        @Setting.Limitations.IntLimit(min = 50, max = 1500)
        public int maxLiving = 1000;

        @Setting(displayName = "Path Duration", description = "How much ticks should take in max to the firefly change direction.")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int maxGoal = 300;

        @Setting(displayName = "Spawn Limit", description = "How much Fireflies in max should spawn around you.")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 200;

        @Setting(displayName = "Spawn Rate", description = "How frequently fireflies should spawn. Bigger = lesser.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 500;

        @Setting(displayName = "Scale", description = "How big fireflies should be.")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 1f, precision = 0.01f)
        public float scale = 0.04f;

    }

}
