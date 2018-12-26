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
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen (0 for instant)")
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

    @SettingsInfo(name = "bubbles_settings", displayPath = "Overlays/Bubbles")
    public static class Bubbles extends SettingsClass {
        public static Bubbles INSTANCE;

        @Setting(displayName = "Bubbles Texture", description = "What texture to use for the exp bar")
        public BubbleTexture bubblesTexture = BubbleTexture.a;

        @Setting(displayName = "Enabled", description = "Should the exp bar display?")
        public boolean enabled = true;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen(0 for instant)")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "Bubble Vignette", description = "Should the drowning viggnete apears")
        public boolean drowningVignette = true;

        public enum BubbleTexture {
            Wynn,
            a,
            b,
            c
        }
    }


    @SettingsInfo(name = "leveling_settings", displayPath = "Overlays/Leveling")
    public static class Leveling extends SettingsClass {
        public static Leveling INSTANCE;

        @Setting.Features.StringParameters(parameters = {"actual", "max", "percent"})
        @Setting(displayName = "Current Text", description = "What will be showed at the Leveling Text")
        public String levelingText = "§a(%actual%/%max%) §6%percent%%";

        @Setting(displayName = "Enabled", description = "Should the lvl bar display?")
        public boolean enabled = true;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


    }

    @SettingsInfo(name = "game_update_settings", displayPath = "Overlays/Update Ticker")
    public static class GameUpdate extends SettingsClass {
        public static GameUpdate INSTANCE;

        // Default settings designed for large gui scale @ 1080p
        // I personally use gui scale normal - but this works fine with that too

        @Setting(displayName = "Message Limit", description = "The maximum amount of ticker messages to display in the game update list")
        @Setting.Limitations.IntLimit(min = 1, max = 20)
        public int messageLimit = 5;

        @Setting(displayName = "Message Expire Time", description = "The amount of time (in seconds) that a ticker message will remain on-screen")
        @Setting.Limitations.FloatLimit(min = 0.2f, max = 20f, precision = 0.2f)
        public float messageTimeLimit = 10f;

        @Setting(displayName = "Invert Growth", description = "Invert the way that the ticker messages grow")
        public boolean invertGrowth = false;

        @Setting(displayName = "Enabled", description = "Should the game update ticker be displayed")
        public boolean enabled = true;

        @Setting(displayName = "Offset X", description = "How far the ticker should be offset on the X axis")
        @Setting.Limitations.IntLimit(min = -300, max = 10)
        public int offsetX = 0;

        @Setting(displayName = "Offset Y", description = "How far the ticker should be offset on the Y axis")
        @Setting.Limitations.IntLimit(min = -300, max = 10)
        public int offsetY = -70;

        @Setting(displayName = "Max message length", description = "The maximum length of messages in the game update ticker. Messages longer than this value will be truncated. (0 = unlimited)")
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int messageMaxLength = 0;

        @Setting(displayName = "Text Shadow", description = "The text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "New message override", description = "Should new messages force out the oldest previous ones. If disabled, ticker messages will be queued and appear when a previous message disappears.")
        public boolean overrideNewMessages = true;

        @SettingsInfo(name = "game_update_exp_settings", displayPath = "Overlays/Update Ticker/Experience")
        public static class GameUpdateEXPMessages extends SettingsClass {
            public static GameUpdateEXPMessages INSTANCE;

            @Setting(displayName = "Enable EXP messages", description = "Should EXP messages be displayed in the game update ticker")
            public boolean enabled = false;

            @Setting(displayName = "EXP message update rate", description = "How often EXP change messages (in seconds) should be added to the game update ticker")
            @Setting.Limitations.FloatLimit(min = 0.2f, max = 10f, precision = 0.2f)
            public float expUpdateRate = 1f;

            @Setting(displayName = "EXP message format", description = "The format of EXP messages")
            @Setting.Features.StringParameters(parameters = {"xo", "xn", "xc", "po", "pn", "pc"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String expMessageFormat = "§2+%xc%XP (§6+%pc%%§2)";
        }

        @SettingsInfo(name = "game_update_redirect_settings", displayPath = "Overlays/Update Ticker/Redirect Messages")
        public static class RedirectSystemMessages extends SettingsClass {
            public static RedirectSystemMessages INSTANCE;

            @Setting(displayName = "Redirect combat messages", description = "Should combat chat messages be redirected to game update ticker")
            public boolean redirectCombat = false;

            @Setting(displayName = "Redirect horse messages", description = "Should messages related to the horse be redirected game update ticker")
            public boolean redirectHorse = false;

            @Setting(displayName = "Redirect local login messages", description = "Should local login messages (for people with ranks) be redirected to the game update ticker")
            public boolean redirectLoginLocal = false;

            @Setting(displayName = "Redirect friend login messages", description = "Should login messages for friends by redirected to the game update ticker")
            public boolean redirectLoginFriend = false;

            @Setting(displayName = "Redirect merchant messages", description = "Should item buyer & identifier messages be redirected to the game update ticker")
            public boolean redirectMerchants = false;

            @Setting(displayName = "Redirect other messages", description = "Should skill point; other user level up; and identification required messages be redirected to the game update ticker")
            public boolean redirectOther = false;

            @Setting(displayName = "Redirect server status", description = "Should server shutdown messages be redirected to the game update ticker")
            public boolean redirectServer = false;

            @Setting(displayName = "Redirect quest messages", description = "Redirect quest started and questbook updated messages to the game update ticker")
            public boolean redirectQuest = false;
        }

        @SettingsInfo(name = "game_update_territory_settings", displayPath = "Overlays/Update Ticker/Territory Change")
        public static class TerritoryChangeMessages extends SettingsClass {
            public static TerritoryChangeMessages INSTANCE;

            @Setting(displayName = "Enable territory change", description = "Should territory change messages be displayed in the game update ticker")
            public boolean enabled = false;

            @Setting(displayName = "Enable territory enter", description = "Should territory enter messages be displayed in the game update ticker")
            public boolean enter = true;

            @Setting(displayName = "Enable territory leave", description = "Should territory leave messages be displayed in the game update ticker")
            public boolean leave = false;

            @Setting(displayName = "Enable music change", description = "Should music change messages be displayed in the game update ticker (no effect if the music module is disabled)")
            public boolean musicChange = false;

            @Setting(displayName = "Territory enter format", description = "The format of territory enter ticker messages")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryEnterFormat = "§7Now Entering [%t%]";

            @Setting(displayName = "Territory leave format", description = "The format of territory leave ticker messages")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryLeaveFormat = "§7Now Leaving [%t%]";

            @Setting(displayName = "Music change format", description = "The format of music change ticker messages")
            @Setting.Features.StringParameters(parameters = {"np"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String musicChangeFormat = "§7♫ %np%";
        }
    }
    
    @SettingsInfo(name = "war_timer_settings", displayPath = "Overlays/War Timer")
    public static class WarTimer extends SettingsClass {
        public static WarTimer INSTANCE;
        
        @Setting(displayName = "Enabled", description = "Should the war timer display?")
        public boolean enabled = true;
        
        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }
}
