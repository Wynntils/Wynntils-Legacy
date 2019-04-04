/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.utilities.overlays.hud.TerritoryFeedOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.util.text.TextFormatting;

@SettingsInfo(name = "overlays", displayPath = "wynntils.config.overlay.display_path")
public class OverlayConfig extends SettingsClass {
    public static OverlayConfig INSTANCE;


    @Setting(displayName = "wynntils.config.overlay.text_shadow.display_name", description = "wynntils.config.overlay.text_shadow.description")
    public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Setting(displayName = "wynntils.config.overlay.actionbar_coords.display_name", description = "wynntils.config.overlay.actionbar_coords.description")
    public boolean actionBarCoordinates = true;

    @Setting(displayName = "wynntils.config.overlay.split_coords.display_name", description = "wynntils.config.overlay.split_coords.description")
    public boolean splitCoordinates = false;

    @SettingsInfo(name = "health_settings", displayPath = "wynntils.config.overlay.health.display_path")
    public static class Health extends SettingsClass {
        public static Health INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.health.texture.display_name", description = "wynntils.config.overlay.health.texture.description")
        public HealthTextures healthTexture = HealthTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "wynntils.config.overlay.health.animation_speed.display_name", description = "wynntils.config.overlay.health.animation_speed.description")
        public float animated = 2f;

        @Setting(displayName = "wynntils.config.overlay.health.text_shadow.display_name", description = "wynntils.config.overlay.health.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum HealthTextures {
            Wynn("wynntils.config.overlay.health.enum.health_texture.wynn"),
            Grune("wynntils.config.overlay.health.enum.health_texture.grune"),
            Aether("wynntils.config.overlay.health.enum.health_texture.aether"),
            Skull("wynntils.config.overlay.health.enum.health_texture.skull"),
            Skyrim("wynntils.config.overlay.health.enum.health_texture.skyrim"),
            a("wynntils.config.overlay.health.enum.health_texture.a"),
            b("wynntils.config.overlay.health.enum.health_texture.b"),
            c("wynntils.config.overlay.health.enum.health_texture.c"),
            d("wynntils.config.overlay.health.enum.health_texture.d");
            //following the format, to add more textures, register them here with a name and create a special case in the render method

            public String displayName;

            HealthTextures(String displayName) {
                this.displayName = displayName;
            }
        }

    }


    @SettingsInfo(name = "mana_settings", displayPath = "wynntils.config.overlay.mana.display_path")
    public static class Mana extends SettingsClass {
        public static Mana INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.mana.texture.display_name", description = "wynntils.config.overlay.mana.texture.description")
        public ManaTextures manaTexture = ManaTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "wynntils.config.overlay.mana.animation_speed.display_name", description = "wynntils.config.overlay.mana.animation_speed.description")
        public float animated = 2f;

        @Setting(displayName = "wynntils.config.overlay.mana.text_shadow.display_name", description = "wynntils.config.overlay.mana.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum ManaTextures {
            Wynn("wynntils.config.overlay.mana.enum.mana_texture.wynn"),
            Brune("wynntils.config.overlay.mana.enum.mana_texture.brune"),
            Aether("wynntils.config.overlay.mana.enum.mana_texture.aether"),
            Skull("wynntils.config.overlay.mana.enum.mana_texture.skull"),
            Inverse("wynntils.config.overlay.mana.enum.mana_texture.inverse"),
            Skyrim("wynntils.config.overlay.mana.enum.mana_texture.skyrim"),
            a("wynntils.config.overlay.mana.enum.mana_texture.a"),
            b("wynntils.config.overlay.mana.enum.mana_texture.b"),
            c("wynntils.config.overlay.mana.enum.mana_texture.c"),
            d("wynntils.config.overlay.mana.enum.mana_texture.d");
            //following the format, to add more textures, register them here with a name and create a special case in the render method

            public String displayName;

            ManaTextures(String displayName) {
                this.displayName = displayName;
            }
        }

    }

    @SettingsInfo(name = "hotbar_settings", displayPath = "wynntils.config.overlay.hotbar.display_path")
    public static class Hotbar extends SettingsClass {
        public static Hotbar INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.hotbar.texture.display_name", description = "wynntils.config.overlay.hotbar.texture.description")
        public HotbarTextures hotbarTexture = HotbarTextures.Resource_Pack;

        public enum HotbarTextures {
            Resource_Pack("wynntils.config.overlay.hotbar.enum.hotbar_texture.resource_pack"),
            Wynn("wynntils.config.overlay.hotbar.enum.hotbar_texture.wynn");

            public String displayName;

            HotbarTextures(String displayName) {
                this.displayName = displayName;
            }
        }
    }

    @SettingsInfo(name = "toast_settings", displayPath = "wynntils.config.overlay.toast.display_path")
    public static class ToastsSettings extends SettingsClass {
        public static ToastsSettings INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.toast.enable.display_name", description = "wynntils.config.overlay.toast.enable.description", order = 0)
        public boolean enableToast = true;

        @Setting(displayName = "wynntils.config.overlay.toast.territory_enter.display_name", description = "wynntils.config.overlay.toast.territory_enter.description")
        public boolean enableTerritoryEnter = true;

        @Setting(displayName = "wynntils.config.overlay.toast.area_discovered.display_name", description = "wynntils.config.overlay.toast.area_discovered.description")
        public boolean enableAreaDiscovered = true;

        @Setting(displayName = "wynntils.config.overlay.toast.quest_completed.display_name", description = "wynntils.config.overlay.toast.quest_completed.description")
        public boolean enableQuestCompleted = true;

        @Setting(displayName = "wynntils.config.overlay.toast.discovery_found.display_name", description = "wynntils.config.overlay.toast.discovery_found.description")
        public boolean enableDiscovery = true;

        @Setting(displayName = "wynntils.config.overlay.toast.flip.display_name", description = "wynntils.config.overlay.toast.flip.description")
        public boolean flipToast = false;
    }

    @SettingsInfo(name = "exp_settings", displayPath = "wynntils.config.overlay.exp.display_path")
    public static class Exp extends SettingsClass {
        public static Exp INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.exp.texture.display_name", description = "wynntils.config.overlay.exp.texture.description")
        public expTextures expTexture = expTextures.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "wynntils.config.overlay.exp.animation_speed.display_name", description = "wynntils.config.overlay.exp.animation_speed.description")
        public float animated = 2f;

        @Setting(displayName = "wynntils.config.overlay.exp.text_shadow.display_name", description = "wynntils.config.overlay.exp.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;


        public enum expTextures {
            Wynn("wynntils.config.overlay.exp.enum.exp_texture.wynn"),
            Liquid("wynntils.config.overlay.exp.enum.exp_texture.liquid"),
            Emerald("wynntils.config.overlay.exp.enum.exp_texture.emerald"),
            a("wynntils.config.overlay.exp.enum.exp_texture.a"),
            b("wynntils.config.overlay.exp.enum.exp_texture.b"),
            c("wynntils.config.overlay.exp.enum.exp_texture.c");
            //following the format, to add more textures, register them here with a name and create a special case in the render method

            public String displayName;

            expTextures(String displayName) {
                this.displayName = displayName;
            }
        }

    }

    @SettingsInfo(name = "bubbles_settings", displayPath = "wynntils.config.overlay.bubbles.display_path")
    public static class Bubbles extends SettingsClass {
        public static Bubbles INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.bubbles.texture.display_name", description = "wynntils.config.overlay.bubbles.texture.description")
        public BubbleTexture bubblesTexture = BubbleTexture.a;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "wynntils.config.overlay.bubbles.animation_speed.display_name", description = "wynntils.config.overlay.bubbles.animation_speed.description")
        public float animated = 2f;

        @Setting(displayName = "wynntils.config.overlay.bubbles.text_shadow.display_name", description = "wynntils.config.overlay.bubbles.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "wynntils.config.overlay.bubbles.drowning_vignette.display_name", description = "wynntils.config.overlay.bubbles.drowning_vignette.description")
        public boolean drowningVignette = true;

        public enum BubbleTexture {
            Wynn("wynntils.config.overlay.bubbles.enum.bubble_texture.wynn"),
            Liquid("wynntils.config.overlay.bubbles.enum.bubble_texture.liquid"),
            Saphire("wynntils.config.overlay.bubbles.enum.bubble_texture.saphire"),
            a("wynntils.config.overlay.bubbles.enum.bubble_texture.a"),
            b("wynntils.config.overlay.bubbles.enum.bubble_texture.b"),
            c("wynntils.config.overlay.bubbles.enum.bubble_texture.c");

            public String displayName;

            BubbleTexture(String displayName) {
                this.displayName = displayName;
            }
        }
    }


    @SettingsInfo(name = "leveling_settings", displayPath = "wynntils.config.overlay.leveling.display_path")
    public static class Leveling extends SettingsClass {
        public static Leveling INSTANCE;

        @Setting.Features.StringParameters(parameters = {"actual", "max", "percent", "needed", "actualg", "maxg", "neededg", "curlvl", "nextlvl"})
        @Setting(displayName = "wynntils.config.overlay.leveling.text.display_name", description = "wynntils.config.overlay.leveling.text.description")
        @Setting.Limitations.StringLimit(maxLength = 200)
        public String levelingText = TextFormatting.GREEN + "(%actual%/%max%) " + TextFormatting.GOLD + "%percent%%";

        @Setting(displayName = "wynntils.config.overlay.leveling.text_shadow.display_name", description = "wynntils.config.overlay.leveling.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }

    @SettingsInfo(name = "game_update_settings", displayPath = "wynntils.config.overlay.ticker.display_path")
    public static class GameUpdate extends SettingsClass {
        public static GameUpdate INSTANCE;

        // Default settings designed for large ui scale @ 1080p
        // I personally use ui scale normal - but this works fine with that too

        @Setting(displayName = "wynntils.config.overlay.ticker.message_limit.display_name", description = "wynntils.config.overlay.ticker.message_limit.description")
        @Setting.Limitations.IntLimit(min = 1, max = 20)
        public int messageLimit = 5;

        @Setting(displayName = "wynntils.config.overlay.ticker.expiry_time.display_name", description = "wynntils.config.overlay.ticker.expiry_time.description")
        @Setting.Limitations.FloatLimit(min = 0.2f, max = 20f, precision = 0.2f)
        public float messageTimeLimit = 10f;

        @Setting(displayName = "wynntils.config.overlay.ticker.fadeout_time.display_name", description = "wynntils.config.overlay.ticker.fadeout_time.description")
        @Setting.Limitations.FloatLimit(min = 10f, max = 60f, precision = 1f)
        public float messageFadeOut = 30f;

        @Setting(displayName = "wynntils.config.overlay.ticker.invert_growth.display_name", description = "wynntils.config.overlay.ticker.invert_growth.description")
        public boolean invertGrowth = true;

        @Setting(displayName = "wynntils.config.overlay.ticker.max_message_length.display_name", description = "wynntils.config.overlay.ticker.max_message_length.description")
        @Setting.Limitations.IntLimit(min = 0, max = 100)
        public int messageMaxLength = 0;

        @Setting(displayName = "wynntils.config.overlay.ticker.text_shadow.display_name", description = "wynntils.config.overlay.ticker.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting(displayName = "wynntils.config.overlay.ticker.new_message_override.display_name", description = "wynntils.config.overlay.ticker.new_message_override.description")
        public boolean overrideNewMessages = true;

        @SettingsInfo(name = "game_update_exp_settings", displayPath = "wynntils.config.overlay.ticker.exp.display_path")
        public static class GameUpdateEXPMessages extends SettingsClass {
            public static GameUpdateEXPMessages INSTANCE;

            @Setting(displayName = "wynntils.config.overlay.ticker.exp.enable.display_name", description = "wynntils.config.overlay.ticker.exp.enable.description", order = 0)
            public boolean enabled = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.exp.update_rate.display_name", description = "wynntils.config.overlay.ticker.exp.update_rate.description")
            @Setting.Limitations.FloatLimit(min = 0.2f, max = 10f, precision = 0.2f)
            public float expUpdateRate = 1f;

            @Setting(displayName = "wynntils.config.overlay.ticker.exp.format.display_name", description = "wynntils.config.overlay.ticker.exp.format.description")
            @Setting.Features.StringParameters(parameters = {"xo", "xn", "xc", "po", "pn", "pc"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String expMessageFormat = TextFormatting.DARK_GREEN + "+%xc%XP (" + TextFormatting.GOLD + "+%pc%%" + TextFormatting.DARK_GREEN + ")";
        }

        @SettingsInfo(name = "game_update_inventory_settings", displayPath = "wynntils.config.overlay.ticker.inv.display_path")
        public static class GameUpdateInventoryMessages extends SettingsClass {
            public static GameUpdateInventoryMessages INSTANCE;

            @Setting(displayName = "wynntils.config.overlay.ticker.inv.enable.display_name", description = "wynntils.config.overlay.ticker.inv.enable.description")
            public boolean enabled = false;

            @Setting(displayName = "wynntils.config.overlay.ticker.inv.update_rate.display_name", description = "wynntils.config.overlay.ticker.inv.update_rate.description")
            @Setting.Limitations.FloatLimit(min = 5f, max = 60f, precision = 5f)
            public float inventoryUpdateRate = 10f;

            @Setting(displayName = "wynntils.config.overlay.ticker.inv.format.display_name", description = "wynntils.config.overlay.ticker.inv.format.description")
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String inventoryMessageFormat = TextFormatting.DARK_RED + "Your inventory is full";
        }

        @SettingsInfo(name = "game_update_redirect_settings", displayPath = "wynntils.config.overlay.ticker.redir.display_path")
        public static class RedirectSystemMessages extends SettingsClass {
            public static RedirectSystemMessages INSTANCE;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.combat.display_name", description = "wynntils.config.overlay.ticker.redir.combat.description")
            public boolean redirectCombat = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.horse.display_name", description = "wynntils.config.overlay.ticker.redir.horse.description")
            public boolean redirectHorse = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.local_login.display_name", description = "wynntils.config.overlay.ticker.redir.local_login.description")
            public boolean redirectLoginLocal = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.friend_login.display_name", description = "wynntils.config.overlay.ticker.redir.friend_login.description")
            public boolean redirectLoginFriend = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.guild_login.display_name", description = "wynntils.config.overlay.ticker.redir.guild_login.description")
            public boolean redirectLoginGuild = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.merchant.display_name", description = "wynntils.config.overlay.ticker.redir.merchant.description")
            public boolean redirectMerchants = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.other.display_name", description = "wynntils.config.overlay.ticker.redir.other.description")
            public boolean redirectOther = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.server.display_name", description = "wynntils.config.overlay.ticker.redir.server.description")
            public boolean redirectServer = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.quest.display_name", description = "wynntils.config.overlay.ticker.redir.quest.description")
            public boolean redirectQuest = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.redir.soul_point.display_name", description = "wynntils.config.overlay.ticker.redir.soul_point.description")
            public boolean redirectSoulPoint = true;
        }

        @SettingsInfo(name = "game_update_territory_settings", displayPath = "wynntils.config.overlay.ticker.territory.display_path")
        public static class TerritoryChangeMessages extends SettingsClass {
            public static TerritoryChangeMessages INSTANCE;

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.enable.display_name", description = "wynntils.config.overlay.ticker.territory.enable.description")
            public boolean enabled = false;

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.enter.display_name", description = "wynntils.config.overlay.ticker.territory.enter.description")
            public boolean enter = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.leave.display_name", description = "wynntils.config.overlay.ticker.territory.leave.description")
            public boolean leave = false;

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.music_change.display_name", description = "wynntils.config.overlay.ticker.territory.music_change.description")
            public boolean musicChange = true;

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.enter_format.display_name", description = "wynntils.config.overlay.ticker.territory.enter_format.description")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryEnterFormat = TextFormatting.GRAY + "Now Entering [%t%]";

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.leave_format.display_name", description = "wynntils.config.overlay.ticker.territory.leave_format.description")
            @Setting.Features.StringParameters(parameters = {"t"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String territoryLeaveFormat = TextFormatting.GRAY + "Now Leaving [%t%]";

            @Setting(displayName = "wynntils.config.overlay.ticker.territory.music_change_format.display_name", description = "wynntils.config.overlay.ticker.territory.music_change_format.description")
            @Setting.Features.StringParameters(parameters = {"np"})
            @Setting.Limitations.StringLimit(maxLength = 100)
            public String musicChangeFormat = TextFormatting.GRAY + "♫ %np%";
        }
    }
    
    @SettingsInfo(name = "war_timer_settings", displayPath = "wynntils.config.overlay.wartimer.display_path")
    public static class WarTimer extends SettingsClass {
        public static WarTimer INSTANCE;
        
        @Setting(displayName = "wynntils.config.overlay.wartimer.text_shadow.display_name", description = "wynntils.config.overlay.wartimer.text_shadow.description")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;
    }

    @SettingsInfo(name = "territory_feed_settings", displayPath = "wynntils.config.overlay.territoryfeed.display_path")
    public static class TerritoryFeed extends SettingsClass {
        public static TerritoryFeed INSTANCE;

        @Setting(displayName = "wynntils.config.overlay.territoryfeed.enabled.display_name" ,description = "wynntils.config.overlay.territoryfeed.enabled.description", order = 0)
        public boolean enabled = true;

        @Setting(displayName = "wynntils.config.overlay.territoryfeed.animation_length.display_name", description = "wynntils.config.overlay.territoryfeed.animation_length.description")
        @Setting.Limitations.IntLimit(min = 1, max = 60)
        public int animationLength = 20;

        @Setting(displayName = "wynntils.config.overlay.territoryfeed.display_mode.display_name", description = "wynntils.config.overlay.territoryfeed.display_mode.description")
        public TerritoryFeedDisplayMode displayMode = TerritoryFeedDisplayMode.DISTINGUISH_OWN_GUILD;

        @Setting(displayName = "wynntils.config.overlay.territoryfeed.short_message.display_name", description = "wynntils.config.overlay.territoryfeed.short_message.description", order = 1)
        public boolean shortMessages = false;

        @Setting(displayName = "wynntils.config.overlay.territoryfeed.guild_tag.display_name", description = "wynntils.config.overlay.territoryfeed.guild_tag.description", order = 2)
        public boolean useTag = false;

        @Override
        public void onSettingChanged(String name) {
            if (name.equals("enabled")) {
                WebManager.updateTerritoryThreadStatus(enabled);
                TerritoryFeedOverlay.clearQueue();
            }
        }

        public enum TerritoryFeedDisplayMode {
            NORMAL("wynntils.config.overlay.territoryfeed.enum.display_mode.normal"),
            DISTINGUISH_OWN_GUILD("wynntils.config.overlay.territoryfeed.enum.display_mode.distinguish_own_guild"),
            ONLY_OWN_GUILD("wynntils.config.overlay.territoryfeed.enum.display_mode.only_own_guild");

            public String displayName;

            TerritoryFeedDisplayMode(String displayName) {
                this.displayName = displayName;
            }
        }
    }
}
