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

        @Setting(displayName = "3D Fireflies", description = "Should the fireflies be 3D.")
        public boolean threeDimensions = true;

    }

    @SettingsInfo(name = "ashes", displayPath = "Visual/Ashes")
    public static class Ashes extends SettingsClass {
        public static Ashes INSTANCE;

        @Setting(displayName = "Visual Ashes", description = "Should ashes be rendered in certain areas.")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "How much ticks should an ashe live in max.")
        @Setting.Limitations.IntLimit(min = 50, max = 5000, precision = 5)
        public int maxLiving = 2200;

        @Setting(displayName = "Spawn Limit", description = "How much ashes in max should spawn around you.")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 175;

        @Setting(displayName = "Max Scale", description = "How big ashes should be in max.")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.5f, precision = 0.01f)
        public float maxScale = 0.2f;

        @Setting(displayName = "Max Gray Scale", description = "How white in max should ashes be.")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f, precision = 0.1f)
        public float maxGrayScale = 0.8f;

        @Setting(displayName = "Spawn Rate", description = "How frequently ashes should spawn. Bigger = lesser.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 375;

    }

    @SettingsInfo(name = "snowflakes", displayPath = "Visual/Snowflakes")
    public static class Snowflakes extends SettingsClass {
        public static Snowflakes INSTANCE;

        @Setting(displayName = "Visual Snowflakes", description = "Should snowflakes be rendered in certain areas.")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "How much ticks should an snowflake live in max.")
        @Setting.Limitations.IntLimit(min = 50, max = 5000, precision = 5)
        public int maxLiving = 1000;

        @Setting(displayName = "Spawn Limit", description = "How much snowflakes in max should spawn around you.")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 200;

        @Setting(displayName = "Max Scale", description = "How big snowflakes should be in max.")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.5f, precision = 0.01f)
        public float maxScale = 0.1f;

        @Setting(displayName = "Max White Scale", description = "How white in max should snowflakes be.")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f, precision = 0.1f)
        public float maxWhiteScale = 0.8f;

        @Setting(displayName = "Spawn Rate", description = "How frequently snowflakes should spawn. Bigger = lesser.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 300;

    }

}
