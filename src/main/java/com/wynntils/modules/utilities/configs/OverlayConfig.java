/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer.TextAlignment;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.core.enums.OverlayRotation;
import com.wynntils.modules.utilities.overlays.hud.ObjectivesOverlay;
import com.wynntils.modules.utilities.overlays.hud.ScoreboardOverlay;
import com.wynntils.modules.utilities.overlays.hud.TerritoryFeedOverlay;
import net.minecraft.util.text.TextFormatting;

@SettingsInfo(name = "overlays", displayPath = "Utilities/Overlays")
public class OverlayConfig extends SettingsClass {
    public static OverlayConfig INSTANCE;


    @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
    public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Setting(displayName = "Action Bar Coordinates", description = "Should the action bar display your coordinates when there is nothing else to show?")
    public boolean actionBarCoordinates = true;

    @Setting(displayName = "Split Coordinates", description = "Should the coordinates be shown separately to the action bar?")
    public boolean splitCoordinates = false;

    @Setting(displayName = "Use Y position on Action Bar", description = "Should the action bar display the player Y position is instead of their orientation?")
    public boolean replaceDirection = false;

    @SettingsInfo(name = "health_settings", displayPath = "Utilities/Overlays/Health")
    public static class Health extends SettingsClass {
        public static Health INSTANCE;

        @Setting(displayName = "Health Bar Width", description = "How wide should the health bar be in pixels?\n\n§8This will be adjusted using Minecraft's scaling.")
        @Setting.Limitations.IntLimit(min = 0, max = 81)
        public int width = 81;

        @Setting(displayName = "Health Bar Orientation", description = "How orientated in degrees should the health bar be?\n\n§8Accompanied text will be removed.")
        public OverlayRotation overlayRotation = OverlayRotation.NORMAL;

        @Setting(displayName = "Low Health Vignette", description = "Should a red vignette be displayed when you're low on health?")
        public boolean healthVignette = true;

        @Setting(displayName = "Low Health Threshold", description = "At what percentage of health should a red vignette be displayed?")
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int lowHealthThreshold = 25;

        @Setting(displayName = "Low Health Animation", description = "Which animation should be used for the low health indicator?")
        public HealthVignetteEffect healthVignetteEffect = HealthVignetteEffect.Pulse;

        @Setting(displayName = "Health Texture", description = "What texture should be used for the health bar?")
        public HealthTextures healthTexture = HealthTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played?\n\n§8Set this to 0 for it to display instantly.")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        public enum HealthVignetteEffect {
            Pulse,
            Growing,
            Static
        }

        public enum HealthTextures {
            Wynn,
            Grune,
            Aether,
            Skull,
            Skyrim,
            Rune,
            a,
            b,
            c,
            d
            // following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "mana_settings", displayPath = "Utilities/Overlays/Mana")
    public static class Mana extends SettingsClass {
        public static Mana INSTANCE;

        @Setting(displayName = "Mana Bar Width", description = "How wide should the mana bar be in pixels?\n\n§8This will be adjusted using Minecraft's scaling.")
        @Setting.Limitations.IntLimit(min = 0, max = 81)
        public int width = 81;

        @Setting(displayName = "Mana Bar Orientation", description = "How orientated in degrees should the mana bar be?\n\n§8Accompanied text will be removed.")
        public OverlayRotation overlayRotation = OverlayRotation.NORMAL;

        @Setting(displayName = "Mana Texture", description = "What texture should be used for the mana bar?")
        public ManaTextures manaTexture = ManaTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played?\n\n§8Set this to 0 for it to display instantly.")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum ManaTextures {
            Wynn,
            Brune,
            Aether,
            Skull,
            Inverse,
            Skyrim,
            Rune,
            a,
            b,
            c,
            d
            // following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "hotbar_settings", displayPath = "Utilities/Overlays/Hotbar")
    public static class Hotbar extends SettingsClass {
        public static Hotbar INSTANCE;

        @Setting(displayName = "Hotbar Texture", description = "What texture should be used for the hotbar?")
        public HotbarTextures hotbarTexture = HotbarTextures.Resource_Pack;

        public enum HotbarTextures {
            Resource_Pack,
            Wynn
        }
    }

    @SettingsInfo(name = "toast_settings", displayPath = "Utilities/Overlays/Toasts")
    public static class ToastsSettings extends SettingsClass {
        public static ToastsSettings INSTANCE;

        @Setting(displayName = "Toast Messages", description = "Should certain messages be displayed in the form of rolling parchment?", order = 0)
        public boolean enableToast = true;

        @Setting(displayName = "Territory Enter Messages", description = "Should a toast be displayed to inform that you are entering a territory?")
        public boolean enableTerritoryEnter = true;

        @Setting(displayName = "Area Discovered Messages", description = "Should a toast be displayed to inform that you have discovered an area?")
        public boolean enableAreaDiscovered = true;

        @Setting(displayName = "Quest Completed Messages", description = "Should a toast be displayed to inform that you have completed a quest?")
        public boolean enableQuestCompleted = true;

        @Setting(displayName = "Discovery Found Messages", description = "Should a toast be displayed to inform that you have found a secret discovery?")
        public boolean enableDiscovery = true;

        @Setting(displayName = "Level Up Messages", description = "Should a toast be displayed to inform that you have leveled up?")
        public boolean enableLevelUp = true;

        @Setting(displayName = "Flip Toast Messages", description = "Should a toast display from the left to right?\n\n§8Some visual glitches may occur if Toast overlay isn't moved to either side of your screen.")
        public boolean flipToast = false;
    }

    @SettingsInfo(name = "exp_settings", displayPath = "Utilities/Overlays/Experience")
    public static class Exp extends SettingsClass {
        public static Exp INSTANCE;

        @Setting(displayName = "EXP Texture", description = "What texture should be used for the EXP bar?")
        public expTextures expTexture = expTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played?\n\n§8Set this to 0 for it to display instantly.")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum expTextures {
            Wynn,
            Liquid,
            Emerald,
            a,
            b,
            c
            // following the format, to add more textures, register them here with a name and create a special case in the render method
        }

    }

    @SettingsInfo(name = "bubbles_settings", displayPath = "Utilities/Overlays/Bubbles")
    public static class Bubbles extends SettingsClass {
        public static Bubbles INSTANCE;

        @Setting(displayName = "Bubbles Texture", description = "What texture should be used for the EXP bar when it acts as the air meter?")
        public BubbleTexture bubblesTexture = BubbleTexture.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the animation be played?\n\n§8Set this to 0 for it to display instantly.")
        public float animated = 2f;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "Bubble Vignette", description = "Should a blue vignette be displayed when you're underwater?")
        public boolean drowningVignette = true;

        public enum BubbleTexture {
            Wynn,
            Liquid,
            Sapphire,
            a,
            b,
            c
        }
    }

    @SettingsInfo(name = "leveling_settings", displayPath = "Utilities/Overlays/Leveling")
    public static class Leveling extends SettingsClass {
        public static Leveling INSTANCE;

        @Setting.Features.StringParameters(parameters = {"actual", "max", "percent", "needed", "actualg", "maxg", "neededg", "curlvl", "nextlvl"})
        @Setting(displayName = "Current Text", description = "How should the leveling text be displayed?")
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String levelingText = TextFormatting.GREEN + "(%actual%/%max%) " + TextFormatting.GOLD + "%percent%%";

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


    }

    @SettingsInfo(name = "game_update_settings", displayPath = "Utilities/Overlays/Update Ticker")
    public static class GameUpdate extends SettingsClass {
        public static GameUpdate INSTANCE;

        // Default settings designed for large ui scale @ 1080p
        // I personally use ui scale normal - but this works fine with that too

        @Setting(displayName = "Message Limit", description = "What should the maximum amount of ticker messages displayed in the game-update-list be?")
        @Setting.Limitations.IntLimit(min = 1, max = 20)
        public int messageLimit = 5;

        @Setting(displayName = "Align Text - Right", description = "Should the text align along the right side?")
        public boolean rightToLeft = true;

        @Setting(displayName = "Message Expiry Time", description = "How long (in seconds) should a ticker message remain on the screen?")
        @Setting.Limitations.FloatLimit(min = 0.2f, max = 20f, precision = 0.2f)
        public float messageTimeLimit = 10f;

        @Setting(displayName = "Message Fadeout Animation", description = "How long should the fadeout animation be played?")
        @Setting.Limitations.FloatLimit(min = 10f, max = 60f, precision = 1f)
        public float messageFadeOut = 30f;

        @Setting(displayName = "Invert Growth", description = "Should the way ticker messages appear be inverted?")
        public boolean invertGrowth = true;

        @Setting(displayName = "New Messages Display Prominently", description = "Should new messages appear before old messages in the direction of growth, pushing older messages further away?")
        public boolean newMessagesFirst = false;

        @Setting(displayName = "Max Message Length", description = "What should the maximum length of messages in the game-update-ticker be?\n\n§8Messages longer than this set value will be truncated. Set this to 0 for no maximum length.")
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int messageMaxLength = 0;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "New Message Override", description = "Should new messages force out the oldest previous messages?\n\n§8If disabled, ticker messages will be queued and appear when a previous message disappears.")
        public boolean overrideNewMessages = true;

        @SettingsInfo(name = "game_update_exp_settings", displayPath = "Utilities/Overlays/Update Ticker/Experience")
        public static class GameUpdateEXPMessages extends SettingsClass {
            public static GameUpdateEXPMessages INSTANCE;

            @Setting(displayName = "Enable EXP Messages", description = "Should EXP messages be displayed in the game-update-ticker?", order = 0)
            public boolean enabled = true;

            @Setting(displayName = "EXP Message Update Rate", description = "How often should the EXP change messages (in seconds) be added to the game update ticker?")
            @Setting.Limitations.FloatLimit(min = 0.2f, max = 10f, precision = 0.2f)
            public float expUpdateRate = 1f;

            @Setting(displayName = "EXP Message Format", description = "How should the format of EXP messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"xo", "xn", "xc", "po", "pn", "pc"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String expMessageFormat = TextFormatting.DARK_GREEN + "+%xc%XP (" + TextFormatting.GOLD + "+%pc%%" + TextFormatting.DARK_GREEN + ")";
        }

        @SettingsInfo(name = "game_update_inventory_settings", displayPath = "Utilities/Overlays/Update Ticker/Inventory")
        public static class GameUpdateInventoryMessages extends SettingsClass {
            public static GameUpdateInventoryMessages INSTANCE;

            @Setting(displayName = "Enable Full Inventory Messages", description = "Should messages be displayed in the game-update-ticker when your inventory is full?")
            public boolean enabled = false;

            @Setting(displayName = "Full Inventory Update Rate", description = "How often should the inventory full message (in seconds) be displayed in the game update ticker?")
            @Setting.Limitations.FloatLimit(min = 5f, max = 60f, precision = 5f)
            public float inventoryUpdateRate = 10f;

            @Setting(displayName = "Inventory Full Message Format", description = "What message should be displayed when your inventory is full?")
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String inventoryMessageFormat = TextFormatting.DARK_RED + "Your inventory is full";
        }

        @SettingsInfo(name = "game_update_redirect_settings", displayPath = "Utilities/Overlays/Update Ticker/Redirect Messages")
        public static class RedirectSystemMessages extends SettingsClass {
            public static RedirectSystemMessages INSTANCE;

            @Setting(displayName = "Redirect Combat Messages", description = "Should combat chat messages be redirected to the game update ticker?")
            public boolean redirectCombat = true;

            @Setting(displayName = "Redirect Horse Messages", description = "Should messages related to your horse be redirected to the game update ticker?")
            public boolean redirectHorse = true;

            @Setting(displayName = "Redirect Resource Pack Messages", description = "Should wynnpack and loading reasource pack messages be disabled or redirected (depending on whether you can see them in classs screen)?")
            public boolean redirectResourcePack = false;

            @Setting(displayName = "Redirect Class Messages", description = "Should class messages be redirected to the game update ticker?")
            public boolean redirectClass = true;

            @Setting(displayName = "Redirect Local Login Messages", description = "Should local login messages (for people with ranks) be redirected to the game update ticker?")
            public boolean redirectLoginLocal = true;

            @Setting(displayName = "Redirect Friend Login Messages", description = "Should login messages for friends be redirected to the game update ticker?")
            public boolean redirectLoginFriend = true;

            @Setting(displayName = "Redirect Guild Login Messages", description = "Should login messages for guild members be redirected to the game update ticker?")
            public boolean redirectLoginGuild = true;

            @Setting(displayName = "Redirect Merchant Messages", description = "Should item buyer and identifier messages be redirected to the game update ticker?")
            public boolean redirectMerchants = true;

            @Setting(displayName = "Redirect Other Messages", description = "Should skill points, price of identifying items, and other users' level up messages be redirected to the game update ticker?")
            public boolean redirectOther = true;

            @Setting(displayName = "Redirect Server Restart Messages", description = "Should server restart messages be redirected to the game update ticker?")
            public boolean redirectServer = true;

            @Setting(displayName = "Redirect Quest Messages", description = "Should messages relating to the progress of a quest be redirected to the game update ticker?")
            public boolean redirectQuest = true;

            @Setting(displayName = "Redirect Soul Point Messages", description = "Should messages about regaining soul points be redirected to the game update ticker?")
            public boolean redirectSoulPoint = true;

            @Setting(displayName = "Redirect Cooldown", description = "Should messages about needing to wait be redirected to the game update ticker?")
            public boolean redirectCooldown = true;

            @Setting(displayName = "Redirect AFK Messages", description = "Should messages about AFK Protection be redirected to the game update ticker?")
            public boolean redirectAfk = true;

            @Setting(displayName = "Redirect pouch Messages", description = "Should messages about ingredients being added to your pouch be redirected to the game update ticker?")
            public boolean redirectPouch = true;

            @Setting(displayName = "Redirect Gathering Tool Messages", description = "Should messages about your gathering tool durability be redirected to the game update ticker?")
            public boolean redirectGatheringDura = true;

            @Setting(displayName = "Redirect Crafted Item Messages", description = "Should messages about crafted item durability be redirected to the game update ticker?")
            public boolean redirectCraftedDura = true;
        }

        @SettingsInfo(name = "game_update_territory_settings", displayPath = "Utilities/Overlays/Update Ticker/Territory Change")
        public static class TerritoryChangeMessages extends SettingsClass {
            public static TerritoryChangeMessages INSTANCE;

            @Setting(displayName = "Enable Territory Change", description = "Should territory change messages be displayed in the game update ticker?")
            public boolean enabled = false;

            @Setting(displayName = "Enable Territory Enter", description = "Should territory enter messages be displayed in the game update ticker?")
            public boolean enter = true;

            @Setting(displayName = "Enable Territory Leave", description = "Should territory leave messages be displayed in the game update ticker?")
            public boolean leave = false;

            @Setting(displayName = "Enable Music Change", description = "Should music change messages be displayed in the game update ticker?\n\n§8This has no effect if the Music module is disabled.")
            public boolean musicChange = true;

            @Setting(displayName = "Territory Enter Format", description = "How should the format of the territory enter ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryEnterFormat = TextFormatting.GRAY + "Now Entering [%t%]";

            @Setting(displayName = "Territory Leave Format", description = "How should the format of the territory leave ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryLeaveFormat = TextFormatting.GRAY + "Now Leaving [%t%]";

            @Setting(displayName = "Music Change Format", description = "How should the format of the music change ticker messages be displayed?")
            @Setting.Features.StringParameters(parameters = {"np"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String musicChangeFormat = TextFormatting.GRAY + "♫ %np%";
        }
    }

    @SettingsInfo(name = "war_timer_settings", displayPath = "Utilities/Overlays/War Timer")
    public static class WarTimer extends SettingsClass {
        public static WarTimer INSTANCE;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }

    @SettingsInfo(name = "territory_feed_settings", displayPath = "Utilities/Overlays/Territory Feed")
    public static class TerritoryFeed extends SettingsClass {
        public static TerritoryFeed INSTANCE;

        @Setting(displayName = "Territory Feed", description = "Should the territory feed be displayed?", order = 0)
        public boolean enabled = true;

        @Setting(displayName = "Animation Length", description = "How long (in seconds) should messages on the territory feed be displayed?")
        @Setting.Limitations.IntLimit(min = 1, max = 60)
        public int animationLength = 20;

        @Setting(displayName = "Territory Messages Mode", description = "What messages should be displayed in the territory feed?\n\n" +
                "Normal: Display all territory messages.\n\n" +
                "Distinguish Own Guild: Display all territory messages, but messages relating to your guild will be displayed in different colours. (§2Gained territory §r& §4lost territory§r)\n\n" +
                "Only Own Guild: Display only territory messages that relate to your guild.")
        public TerritoryFeedDisplayMode displayMode = TerritoryFeedDisplayMode.DISTINGUISH_OWN_GUILD;

        @Setting(displayName = "Shorten Messages", description = "Should territory feed messages be shortened?", order = 1)
        public boolean shortMessages = false;

        @Setting(displayName = "Use Guild Tags", description = "Should guild tags be displayed rather than names?", order = 2)
        public boolean useTag = false;

        @Override
        public void onSettingChanged(String name) {
            if (name.equals("enabled")) {
                TerritoryFeedOverlay.clearQueue();
            }
        }

        public enum TerritoryFeedDisplayMode {
            NORMAL,
            DISTINGUISH_OWN_GUILD,
            ONLY_OWN_GUILD
        }
    }

    @SettingsInfo(name = "info_overlays_settings", displayPath = "Utilities/Overlays/Info")
    public static class InfoOverlays extends SettingsClass {
        public static InfoOverlays INSTANCE;

        @Setting.Features.StringParameters(parameters = {"x", "y", "z", "dir", "fps", "class", "lvl"})
        @Setting(displayName = "Info 1 Text", description = "What should the first box display?", order = 1)
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String info1Format = "";

        @Setting(displayName = "Info 1 Alignment", description = "How should the text in the first box be aligned?", order = 2)
        public TextAlignment info1Alignment = TextAlignment.MIDDLE;

        @Setting.Features.StringParameters(parameters = {"x", "y", "z", "dir", "fps", "class", "lvl"})
        @Setting(displayName = "Info 2 Text", description = "What should the second box display?", order = 3)
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String info2Format = "";

        @Setting(displayName = "Info 2 Alignment", description = "How should the text in the second box be aligned?", order = 4)
        public TextAlignment info2Alignment = TextAlignment.MIDDLE;

        @Setting.Features.StringParameters(parameters = {"x", "y", "z", "dir", "fps", "class", "lvl"})
        @Setting(displayName = "Info 3 Text", description = "What should the third box display?", order = 5)
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String info3Format = "";

        @Setting(displayName = "Info 3 Alignment", description = "How should the text in the third box be aligned?", order = 6)
        public TextAlignment info3Alignment = TextAlignment.MIDDLE;

        @Setting.Features.StringParameters(parameters = {"x", "y", "z", "dir", "fps", "class", "lvl"})
        @Setting(displayName = "Info 4 Text", description = "What should the fourth box display?", order = 7)
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String info4Format = "";

        @Setting(displayName = "Info 4 Alignment", description = "How should the text in the fourth box be aligned?", order = 8)
        public TextAlignment info4Alignment = TextAlignment.MIDDLE;

        @Setting(displayName = "Presets", description = "Click on the button below to cycle through various formats. The formats will automatically be copied to your clipboard for you to paste in the above fields.", upload = false, order = 9)
        public Presets preset = Presets.CLICK_ME;

        @Setting(displayName = "Variables", description = "Click on the button below to cycle through various variables. The variables will automatically be copied to your clipboard for you to paste in the above fields.", upload = false, order = 10)
        public Variables variables = Variables.CLICK_ME;

        @Setting(displayName = "Symbols/Escaped Characters ", description = "Click on the button below to cycle through various characters. The characters will automatically be copied to your clipboard for you to paste in the above fields.", upload = false, order = 11)
        public Escaped escapedChars = Escaped.CLICK_ME;

        @Setting(displayName = "Background Opacity", description = "How dark should the background box be?", order = 12)
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int opacity = 0;

        @Setting(displayName = "Background Color", description = "What color should the text shadow be?\n\n§aClick the coloured box to open the colour wheel.", order = 13)
        public CustomColor backgroundColor = CustomColor.fromInt(0x000000, 0);

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        @Override
        public void onSettingChanged(String name) {
            backgroundColor.setA(opacity / 100f);

            if (name.contentEquals("preset")) {
                if (!(McIf.mc().currentScreen instanceof SettingsUI)) {
                    preset = Presets.CLICK_ME;
                } else if (preset.value != null) {
                    Utils.copyToClipboard(preset.value);
                }
            } else if (name.contentEquals("variables")) {
                if (!(McIf.mc().currentScreen instanceof SettingsUI)) {
                    variables = Variables.CLICK_ME;
                } else if (variables.value != null) {
                    Utils.copyToClipboard(variables.value);
                }
            } else if (name.contentEquals("escapedChars")) {
                if (!(McIf.mc().currentScreen instanceof SettingsUI)) {
                    escapedChars = Escaped.CLICK_ME;
                } else if (escapedChars.value != null) {
                    Utils.copyToClipboard(escapedChars.value);
                }
            }
        }

        public enum Presets {
            CLICK_ME("Click me to copy to clipboard", null),
            COORDS("Coordinates", "%x% %z% (%y%)"),
            ACTIONBAR_COORDS("Actionbar Coordinates", "&7%x% &a%dir% &7%z%"),
            FPS("FPS Counter", "FPS: %fps%"),
            CLASS("Class", "%Class%\\nLevel %lvl%"),
            LOCATION("Location", "[%world%] %location%"),
            BALANCE("Balance", "%le%\\L\\E %blocks%\\E\\B %emeralds%\\E (%money%\\E)"),
            SOULPOINTS("Soul points", "%sp%/%sp_max%SP (%sp_timer%)"),
            LEVEL("Level", "Lv. %level%  (%xp_pct%\\%)"),
            HORSE_INFO("Horse information", "%horse_level%/%horse_level_max% (%horse_xp%\\%)"),
            POUCH("Ingredient pouch", "%pouch%"),
            HEALH("Health bar", "%health% \\H %health_max%"),
            MANA("Mana bar", "%mana% \\M %mana_max%"),
            MEMORY_USAGE("Memory usage", "%mem_pct%\\% %mem_used%/%mem_max%MB"),
            PING("Ping", "%ping%ms/15s"),
            BLOCKSPERSECOND("Blocks Per Second", "%bps% bps"),
            BLOCKSPERMINUTE("Blocks Per Minute", "%bpm% bpm"),
            AREA_DPS("Area Damage Per Second", "Area DPS: ❤ %adps%");

            public final String displayName;
            public final String value;

            Presets(String displayName, String value) {
                this.displayName = displayName;
                this.value = value;
            }
        }

        public enum Escaped {
            CLICK_ME("Click me to copy to clipboard", null),
            NEW_LINE("New line", "\\n"),
            SLASH("Back Slash (\\)", "\\\\"),
            PERCENT("Percent (%)", "\\%"),
            E("Emerald (" + EmeraldSymbols.E_STRING + ")", "\\E"),
            EB("EB (" + EmeraldSymbols.B_STRING + ")", "\\B"),
            LE("LE (" + EmeraldSymbols.L_STRING + ")", "\\L"),
            M("Mana (✺)", "\\M"),
            H("Heart (❤)", "\\H");

            public final String displayName;
            public final String value;

            Escaped(String displayName, String value) {
                this.displayName = displayName;
                this.value = value;
            }
        }

        public enum Variables {
            CLICK_ME("Click me to copy to clipboard", null),
            BPS("Blocks per second"),
            BPM("Blocks per minute"),
            KMPH("Kilometers per hour"),
            X("X Coordinate"),
            Y("Y Coordinate"),
            Z("Z Coordinate"),
            DIR("The facing direction"),
            FPS("Frames per second"),
            WORLD("The current world/server"),
            PING("The current ping"),
            CLOCK("The current time"),
            CLOCKM("The current time (24h)"),
            MANA("Current mana"),
            MANA_MAX("Max mana"),
            HEALTH("Current health"),
            HEALTH_MAX("Max health"),
            HEALTH_PCT("Current health percentage"),
            XP("Current XP (Formatted)"),
            XP_RAW("Current XP (Raw)"),
            XP_REQ("Required XP to level up (Formatted)"),
            XP_REQ_RAW("Required XP to level up (Raw)"),
            XP_PCT("Percentage of XP through the current level"),
            POUCH("Current number of items in ingredient pouch)"),
            POUCH_FREE("Number of free slots in the ingredient pouch"),
            POUCH_SLOTS("Number of used slots in the ingredient pouch"),
            INV_FREE("Number of free slots in the inventory"),
            INV_SLOTS("Number of used slots in the inventory"),
            LOCATION("Current location"),
            LEVEL("Current level"),
            SP_TIMER("Time until next soul point (Formatted)"),
            SP_TIMER_M("Time until next soul point (Minutes only)"),
            SP_TIMER_S("Time until next soul point (Seconds only)"),
            SP("Current soul points"),
            SP_MAX("Max soul points"),
            MONEY("Total amount of money in inventory (Raw emerald count)"),
            MONEY_DESC("Total amount of money in inventory (as formatted string)"),
            LE("Amount of full liquid emeralds in the inventory"),
            EB("Amount of full emerald blocks in the inventory (excluding LE count)"),
            E("Amount of emeralds in the inventory (excluding LE and EB count)"),
            CLASS("Current class (Capitalisation dependant)", "%Class%"),
            MEM_MAX("Total allocated memory"),
            MEM_USED("Total used memory"),
            MEM_PCT("Percentage of memory used"),
            HORSE_LEVEL("Current horse level"),
            HORSE_LEVEL_MAX("Max horse level"),
            HORSE_XP("Current horse xp"),
            HORSE_TIER("Current horse tier"),
            POTIONS_HEALTH("Amount of health potions in inventory"),
            POTIONS_MANA("Amount of mana potions in inventory"),
            PARTY_COUNT("Amount of members in the players party"),
            PARTY_OWNER("Owner of the current party"),
            UNPROCESSED("Current amount of unprocessed materials"),
            UNPROCESSED_MAX("Max amount of unprocessed materials"),
            ADPS("Current area damage per second");

            public final String displayName;
            public final String value;

            Variables(String displayName, String value) {
                this.displayName = displayName;
                this.value = value;
            }

            Variables(String displayName) {
                this.displayName = displayName;
                this.value = "%" + this.name().toLowerCase() + "%";
            }
        }
    }

    @SettingsInfo(name = "player_info_settings", displayPath = "Utilities/Overlays/Player Info")
    public static class PlayerInfo extends SettingsClass {
        public static PlayerInfo INSTANCE;

        @Setting(displayName = "Replace Vanilla Player List", description = "Should the vanilla player list be replaced with Wynntils' custom list?", order = 1)
        public boolean replaceVanilla = true;

        @Setting(displayName = "Player List Transparency", description = "How transparent should the custom player list be?", order = 2)
        @Setting.Limitations.FloatLimit(min = .0f, max = 1f)
        public float backgroundAlpha = 0.3f;

        @Setting(displayName = "Player List Opening Duration", description = "How long should the opening animation of the custom player list last?", order = 3)
        @Setting.Limitations.DoubleLimit(min = 0D, max = 500D, precision = 5D)
        public double openingDuration = 125D;

    }

    @SettingsInfo(name = "consumable_timer_settings", displayPath = "Utilities/Overlays/Consumable Timer")
    public static class ConsumableTimer extends SettingsClass {
        public static ConsumableTimer INSTANCE;

        @Setting(displayName = "Effects", description = "Should active effects be displayed in the overlay?")
        public boolean showEffects = true;

        @Setting(displayName = "Loot Chest Cooldown", description = "Should loot chests cooldown be displayed as a timer?")
        public boolean showCooldown = true;

        @Setting(displayName = "Spell Effects", description = "Should spell effects be displayed?")
        public boolean showSpellEffects = true;

        @Setting(displayName = "Track Totem (Experimental)", description = "Should shaman's totem be displayed?")
        public boolean trackTotem = false;

        @Setting(displayName = "Server Restart", description = "Should server restart countdown be displayed?")
        public boolean showServerRestart = false;

        @Setting(displayName = "Text Alignment", description = "What alignment should the overlay use?")
        public TextAlignment textAlignment = TextAlignment.RIGHT_LEFT;

        @Setting(displayName = "Text Shadow", description = "What shadow should the text use?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }

    @SettingsInfo(name = "tracked_quest_info_settings", displayPath = "Utilities/Overlays/Tracked Quest Info")
    public static class TrackedQuestInfo extends SettingsClass {
        public static TrackedQuestInfo INSTANCE;

        @Setting(displayName = "Display Quest Name", description = "Should the quest name be shown in the overlay?")
        public boolean displayQuestName = false;

        @Setting(displayName = "Text Alignment", description = "What alignment should the overlay use?")
        public TextAlignment textAlignment = TextAlignment.LEFT_RIGHT;

        @Setting(displayName = "Text Shadow", description = "What shadow should the text use?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }

    @SettingsInfo(name = "objectives_settings", displayPath = "Utilities/Overlays/Objectives")
    public static class Objectives extends SettingsClass {
        public static Objectives INSTANCE;

        @Setting(displayName = "Enable Objectives Overlay", description = "Should the sidebar scoreboard be replaced by this overlay?\n\n§8This overlay works best if the scoreboard overlay is enabled as well.", order = 0)
        public boolean enableObjectives = true;

        @Setting(displayName = "Hide on Inactivity", description = "Should the overlay be hidden unless the objective has been updated?", order = 1)
        public boolean hideOnInactivity = false;

        @Setting(displayName = "Enable Objectives Bar", description = "Should the objectives progress be shown as a bar?", order = 2)
        public boolean enableProgressBar = true;

        @Setting(displayName = "Objectives Transparency", description = "How transparent should the text and progress bar be?", order = 3)
        @Setting.Limitations.FloatLimit(min = 0.0f, max = 1.0f)
        public float objectivesAlpha = 0.8f;

        @Setting(displayName = "Grow From Bottom", description = "Should the list of objectives grow from the bottom?")
        public boolean growFromBottom = true;

        @Setting(displayName = "Objectives Bar Texture", description = "What texture should be used for the objectives bar?")
        public objectivesTextures objectivesTexture = objectivesTextures.a;

        @Setting(displayName = "Text Colour", description = "What colour should the objective text be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor textColour = CommonColors.GREEN;

        @Setting(displayName = "Text Shadow", description = "What should the text shadow look like?")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Override
        public void onSettingChanged(String name) {
            if (name.equals("hideOnInactivity")) {
                ObjectivesOverlay.refreshAllTimestamps();
            }
        }

        // We're reusing the exp textures
        public enum objectivesTextures {
            Wynn,
            Liquid,
            Emerald,
            a,
            b,
            c
        }
    }

    @SettingsInfo(name = "scoreboard_settings", displayPath = "Utilities/Overlays/Scoreboard")
    public static class Scoreboard extends SettingsClass {
        public static Scoreboard INSTANCE;

        @Setting(displayName = "Enable Scoreboard Overlay", description = "Should the custom Wynntils scoreboard be used?", order = 0)
        public boolean enableScoreboard = true;

        @Setting(displayName = "Show Scoreboard Numbers", description = "Should the numbers on the right side of the scoreboard be shown?", order = 1)
        public boolean showNumbers = false;

        @Setting(displayName = "Show Scoreboard Title", description = "Should the title at the top of the scoreboard be shown?", order = 2)
        public boolean showTitle = true;

        @Setting(displayName = "Show Compass Reminder", description = "Should the compass text be removed from the tracked quest section?", order = 3)
        public boolean showCompass = false;

        @Setting(displayName = "Grow From Top", description = "Should the scoreboard grow downward from the top of its overlay position?", order = 4)
        public boolean growFromTop = false;

        @Setting(displayName = "Background Opacity", description = "How dark should the background box be?", order = 10)
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int opacity = 20;

        @Setting(displayName = "Background Color", description = "What color should the text shadow be?\n\n§aClick the coloured box to open the colour wheel.", order = 11)
        public CustomColor backgroundColor = CustomColor.fromInt(0x000000, 0.2f);

        @Override
        public void onSettingChanged(String name) {
            backgroundColor.setA(opacity/100f);

            if (name.equals("enableScoreboard")) {
                ScoreboardOverlay.enableCustomScoreboard(enableScoreboard);
            }
        }
    }
}
