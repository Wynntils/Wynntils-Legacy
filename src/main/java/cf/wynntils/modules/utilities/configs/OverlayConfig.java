package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "overlays", displayPath = "Overlays")
public class OverlayConfig extends SettingsClass {
    public static OverlayConfig INSTANCE;


    @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
    public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Setting(displayName = "Action bar Overwrite Coords", description = "Should the coords be overwritten by the action bar")
    public boolean overwrite = true;


    @SettingsInfo(name = "health_settings", displayPath = "Overlays/Health")
    public static class Health extends SettingsClass {
        public static Health INSTANCE;

        @Setting(displayName = "Health Texture", description = "What texture to use for the health bar")
        public HealthTextures healthTexture = HealthTextures.a;

        @Setting(displayName = "Enabled", description = "Should the health bar display?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen(0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum HealthTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }


    @SettingsInfo(name = "mana_settings", displayPath = "Overlays/Mana")
    public static class Mana extends SettingsClass {
        public static Mana INSTANCE;

        @Setting(displayName = "Mana Texture", description = "What texture to use for the mana bar")
        public ManaTextures manaTexture = ManaTextures.a;

        @Setting(displayName = "Enabled", description = "Should the mana bar display?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen(0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum ManaTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "exp_settings", displayPath = "Overlays/Exp")
    public static class Exp extends SettingsClass {
        public static Exp INSTANCE;

        @Setting(displayName = "Exp Texture", description = "What texture to use for the exp bar")
        public expTextures expTexture = expTextures.a;

        @Setting(displayName = "Enabled", description = "Should the exp bar display?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen(0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum expTextures {
            Wynn,
            a,
            b,
            c
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }


    @SettingsInfo(name = "leveling_settings", displayPath = "Overlays/Leveling")
    public static class Leveling extends SettingsClass {
        public static Leveling INSTANCE;

        @Setting.Features.StringParameters(parameters = {"actual", "max", "percent"})
        @Setting(displayName = "Current Text", description = "What will be showed at the Leveling Text")
        public String levelingText = "§a(%actual%/%max%) §6%percent%%";

        @Setting(displayName = "Enabled", description = "Should the mana bar display?")
        public boolean enabled = true;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


    }
}
