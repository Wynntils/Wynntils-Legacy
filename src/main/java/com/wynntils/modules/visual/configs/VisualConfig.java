/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.visual.configs;

import com.wynntils.Reference;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.visual.managers.CachedChunkManager;

@SettingsInfo(name = "visual", displayPath = "Visual")
public class VisualConfig extends SettingsClass {
    public static VisualConfig INSTANCE;

    @SettingsInfo(name = "damageSplash", displayPath = "Visual/Damage Splash")
    public static class DamageSplash extends SettingsClass {
        public static DamageSplash INSTANCE;

        @Setting(displayName = "Replace Damage Splashes", description = "Should Wynncraft damage splashes be replaced?")
        public boolean enabled = true;

        @Setting(displayName = "Splash Duration", description = "In ticks, how long should splashes last for?")
        @Setting.Limitations.IntLimit(min = 50, max = 800)
        public int maxLiving = 150;

        @Setting(displayName = "Initial Splash Scale", description = "How large should the initial size of the splash be?")
        @Setting.Limitations.FloatLimit(min = 1f, max = 4f)
        public float initialScale = 2.5f;
    }

    @SettingsInfo(name = "fireflies", displayPath = "Visual/Fireflies")
    public static class Fireflies extends SettingsClass {
        public static Fireflies INSTANCE;

        @Setting(displayName = "Visual Fireflies", description = "Should fireflies be rendered in certain areas?")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "In ticks, up to how long should fireflies live for?")
        @Setting.Limitations.IntLimit(min = 50, max = 1500)
        public int maxLiving = 1000;

        @Setting(displayName = "Path Duration", description = "In ticks, up to how long should fireflies take for them to change directions?")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int maxGoal = 300;

        @Setting(displayName = "Spawn Limit", description = "Up to how many fireflies can spawn around you?")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 200;

        @Setting(displayName = "Spawn Rate", description = "How frequently should fireflies spawn?\n\n§8The higher the number is set to, the longer it takes for fireflies to spawn.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 500;

        @Setting(displayName = "Scale", description = "Up to how big should fireflies be?")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 1f, precision = 0.01f)
        public float scale = 0.04f;

        @Setting(displayName = "3D Fireflies", description = "Should fireflies be rendered in 3D?")
        public boolean threeDimensions = false;

    }

    @SettingsInfo(name = "ashes", displayPath = "Visual/Ashes")
    public static class Ashes extends SettingsClass {
        public static Ashes INSTANCE;

        @Setting(displayName = "Visual Ashes", description = "Should ashes be rendered in certain areas?")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "In ticks, up to how long should ashes appear for?")
        @Setting.Limitations.IntLimit(min = 50, max = 5000, precision = 5)
        public int maxLiving = 2200;

        @Setting(displayName = "Spawn Limit", description = "Up to how many ashes can spawn around you?")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 175;

        @Setting(displayName = "Max Scale", description = "Up to how big should ashes be?")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.5f, precision = 0.01f)
        public float maxScale = 0.2f;

        @Setting(displayName = "Max Grey Scale", description = "Up to how grey should ashes appear?")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f, precision = 0.1f)
        public float maxGrayScale = 0.8f;

        @Setting(displayName = "Spawn Rate", description = "How frequently should ashes appear?\n\n§8The higher the number is set to, the longer it takes for ashes to appear.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 375;

    }

    @SettingsInfo(name = "snowflakes", displayPath = "Visual/Snowflakes")
    public static class Snowflakes extends SettingsClass {
        public static Snowflakes INSTANCE;

        @Setting(displayName = "Visual Snowflakes", description = "Should snowflakes be rendered in certain areas?")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "In ticks, up to how long should snowflakes appear for?")
        @Setting.Limitations.IntLimit(min = 50, max = 5000, precision = 5)
        public int maxLiving = 1000;

        @Setting(displayName = "Spawn Limit", description = "Up to how many snowflakes can spawn around you?")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 200;

        @Setting(displayName = "Max Scale", description = "Up to how big should snowflakes be?")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.5f, precision = 0.01f)
        public float maxScale = 0.1f;

        @Setting(displayName = "Max White Scale", description = "Up to how white should snowflakes appear?")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f, precision = 0.1f)
        public float maxWhiteScale = 0.8f;

        @Setting(displayName = "Spawn Rate", description = "How frequently should snowflakes appear?\n\n§8The higher the number is set to, the longer it takes for snowflakes to appear.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 300;

    }

    @SettingsInfo(name = "flames", displayPath = "Visual/Flames")
    public static class Flames extends SettingsClass {
        public static Flames INSTANCE;

        @Setting(displayName = "Visual Flames", description = "Should flames be rendered in certain areas?")
        public boolean enabled = true;

        @Setting(displayName = "Max Duration", description = "In ticks, up to how long should flames appear for?")
        @Setting.Limitations.IntLimit(min = 50, max = 5000, precision = 5)
        public int maxLiving = 1000;

        @Setting(displayName = "Spawn Limit", description = "Up to how many flames can spawn around you?")
        @Setting.Limitations.IntLimit(min = 50, max = 500)
        public int spawnLimit = 200;

        @Setting(displayName = "Spawn Rate", description = "How frequently should flames appear?\n\n§8The higher the number is set to, the longer it takes for flames to appear.")
        @Setting.Limitations.IntLimit(min = 100, max = 1000)
        public int spawnRate = 200;

        @Setting(displayName = "Max Scale", description = "Up to how big should flames be?")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.5f, precision = 0.01f)
        public float maxScale = 0.11f;

        @Setting(displayName = "Velocity", description = "How fast should flames be?")
        @Setting.Limitations.FloatLimit(min = 0.01f, max = 0.1f, precision = 0.01f)
        public float velocity = 0.05f;

    }

    @SettingsInfo(name = "customSelector", displayPath = "Visual/Custom Selector")
    public static class CustomSelector extends SettingsClass {
        public static CustomSelector INSTANCE;

        @Setting(displayName = "Custom Character Selector", description = "Should the custom character selector be enabled?")
        public boolean characterSelector = true;

        @Setting(displayName = "Custom Seaskipper Selector", description = "Should the Seaskipper gui be replaced with a map?")
        public boolean seaskipperSelector = true;

    }

    @SettingsInfo(name = "cachedChunks", displayPath = "Visual/Cached Chunks")
    public static class CachedChunks extends SettingsClass {
        public static CachedChunks INSTANCE;

        @Setting(displayName = "Enable Cached Chunks", description = "Should Wynntils cache the server chunks in order to fullfill your game render distance?\n\n§cEnabling this feature will cause the game to use more disk space.", upload = false)
        public boolean enabled = false;
        
        
        @Setting(displayName = "Delete Cached Chunks", description = "Toggling this to true will delete all cached chunks.", upload = false)
        public boolean deleteChunks = false;

        @Override
        public void onSettingChanged(String name) {
            if (name.equals("deleteChunks") && deleteChunks) {
                CachedChunkManager.deleteCache();
                deleteChunks = false;
            }
        }

    }

}
