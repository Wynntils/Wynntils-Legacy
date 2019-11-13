/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

import java.util.HashMap;
import java.util.HashSet;

@SettingsInfo(name = "main", displayPath = "wynntils.config.utils.display_path")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;


    @Setting(displayName = "wynntils.config.utils.daily_reminder.display_name", description = "wynntils.config.utils.daily_reminder.description")
    public boolean dailyReminder = true;

    @Setting(displayName = "wynntils.config.utils.afk_collision_block.display_name", description = "wynntils.config.utils.afk_collision_block.description")
    public boolean blockAfkPushs = true;

    @Setting(displayName = "wynntils.config.utils.hide_potion.display_name", description = "wynntils.config.utils.hide_potion.description")
    public boolean hidePotionGui = true;

    @Setting(displayName = "wynntils.config.utils.add_class_server.display_name", description = "wynntils.config.utils.add_class_server.description")
    public boolean addClassServer = true;

    @Setting(displayName = "wynntils.config.utils.hide_nametags_through_blocks.display_name", description = "wynntils.config.utils.hide_nametags_through_blocks.description")
    public boolean hideNametags = true;

    @Setting(displayName = "wynntils.config.utils.hide_nametag_box.display_name", description = "wynntils.config.utils.hide_nametag_box.description")
    public boolean hideNametagBox = true;

    @Setting(displayName = "wynntils.config.utils.show_armor.display_name", description = "wynntils.config.utils.show_armor.description")
    public boolean showArmors = false;

    @Setting(displayName = "wynntils.config.utils.prevent_mythic.display_name", description = "wynntils.config.utils.prevent_mythic.description")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "wynntils.config.utils.prevent_slot_click.display_name", description = "wynntils.config.utils.prevent_slot_click.description")
    public boolean preventSlotClicking = false;

    @Setting(displayName = "Disable FOV Changes with Speed Effect", description = "Should your FOV remain unchanged when you have speed?")
    public boolean disableFovChanges = false;

    @Setting(displayName = "Auto Mount Horse", description = "Should you mount your horse automatically when it is spawned?")
    public boolean autoMount = false;

    @Setting(displayName = "Block Health Potions When at Full Health", description = "Should the mod prevent you from using your health potions when you are at full health?")
    public boolean blockHealingPots = true;

    @Setting(displayName = "Apply Wynncraft Resource Pack", description = "Should the Wynncraft server resource pack be applied when joining the server instead of when picking your class?")
    public boolean autoResource = true;

    @Setting(displayName = "Display GUI Confirmation for Purchasing Bank Pages", description = "Should Wynntils display a GUI confirmation when buying bank pages?")
    public boolean addBankConfirmation = true;

    @Setting(upload = false)
    public String lastServerResourcePack = "";

    @Setting(upload = false)
    public String lastServerResourcePackHash = "";

    //HeyZeer0: Do not add @Setting here, or it will be displayed on the configuration
    @Setting(upload = true)
    public HashMap<Integer, HashSet<Integer>> locked_slots = new HashMap<>();

    @SettingsInfo(name = "wars", displayPath = "wynntils.config.utils.war.display_path")
    public static class Wars extends SettingsClass {
        public static Wars INSTANCE;

        @Setting(displayName = "wynntils.config.utils.war.entity_filter.display_name", description = "wynntils.config.utils.war.entity_filter.description")
        public boolean allowEntityFilter = true;

        @Setting(displayName = "wynntils.config.utils.war.warrer_health_bar.display_name", description = "wynntils.config.utils.war.warrer_health_bar.description")
        public boolean warrerHealthBar = true;

    }

    @SettingsInfo(name = "data", displayPath = "")
    public static class Data extends SettingsClass {
        public static Data INSTANCE;

        public long dailyReminder = 0L;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "item_highlights", displayPath = "wynntils.config.utils.item_highlights.display_path")
    public static class Items extends SettingsClass {
        public static Items INSTANCE;

        @Setting(displayName = "wynntils.config.utils.item_highlights.ingredient_filter.display_name", description = "wynntils.config.utils.item_highlights.ingredient_filter.description")
        public boolean filterEnabled = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_container.display_name", description = "wynntils.config.utils.item_highlights.highlight_container.description")
        public boolean mainHighlightChest = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_inventory.display_name", description = "wynntils.config.utils.item_highlights.highlight_inventory.description")
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_accessory.display_name", description = "wynntils.config.utils.item_highlights.highlight_accessory.description")
        public boolean accesoryHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_hotbar.display_name", description = "wynntils.config.utils.item_highlights.highlight_hotbar.description")
        public boolean hotbarHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_armor.display_name", description = "wynntils.config.utils.item_highlights.highlight_armor.description")
        public boolean armorHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_mythic.display_name", description = "wynntils.config.utils.item_highlights.highlight_mythic.description")
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight Fabled", description = "Should fabled items be highlighted?")
        public boolean fabledHighlight = true;  // TODO: localise

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_legendary.display_name", description = "wynntils.config.utils.item_highlights.highlight_legendary.description")
        public boolean legendaryHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_rare.display_name", description = "wynntils.config.utils.item_highlights.highlight_rare.description")
        public boolean rareHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_unique.display_name", description = "wynntils.config.utils.item_highlights.highlight_unique.description")
        public boolean uniqueHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_set.display_name", description = "wynntils.config.utils.item_highlights.highlight_set.description")
        public boolean setHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_normal.display_name", description = "wynntils.config.utils.item_highlights.highlight_normal.description")
        public boolean normalHighlight = false;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_black_market_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_black_market_cosmetic.description")
        public boolean blackMarketEffectsHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_godly_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_godly_cosmetic.description")
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_epic_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_epic_cosmetic.description")
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_rare_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_rare_cosmetic.description")
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_common_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_common_cosmetic.description")
        public boolean commonEffectsHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.emeralds_container.display_name", description = "wynntils.config.utils.item_highlights.emeralds_container.description")
        public boolean emeraldCountChest = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.emeralds_inventory.display_name", description = "wynntils.config.utils.item_highlights.emeralds_inventory.description")
        public boolean emeraldCountInventory = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_powder.display_name", description = "wynntils.config.utils.item_highlights.highlight_powder.description")
        public boolean powderHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_crafting.display_name", description = "wynntils.config.utils.item_highlights.highlight_crafting.description")
        public boolean ingredientHighlight = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.highlight_dupe_cosmetic.display_name", description = "wynntils.config.utils.item_highlights.highlight_dupe_cosmetic.description")
        public boolean highlightCosmeticDuplicates = true;

        @Setting(displayName = "wynntils.config.utils.item_highlights.minimum_powder_tier.display_name", description = "wynntils.config.utils.item_highlights.minimum_powder_tier.description")
        @Setting.Limitations.IntLimit(min = 1, max = 6)
        public int minPowderTier = 4;

        @Setting(displayName = "wynntils.config.utils.item_highlights.legendary_color.display_name", description = "wynntils.config.utils.item_highlights.legendary_color.description")
        public CustomColor lengendaryHighlightColor = new CustomColor(0, 1, 1);

        @Setting(displayName = "wynntils.config.utils.item_highlights.mythic_color.display_name", description = "wynntils.config.utils.item_highlights.mythic_color.description")
        public CustomColor mythicHighlightColor = new CustomColor(0.3f, 0, 0.3f);

        @Setting(displayName = "Fabled Item Highlight Colour", description = "What colour should the highlight for fabled items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor fabledHighlightColor = new CustomColor(1, .58f, .49f);  // TODO: localise

        @Setting(displayName = "wynntils.config.utils.item_highlights.rare_color.display_name", description = "wynntils.config.utils.item_highlights.rare_color.description")
        public CustomColor rareHighlightColor = new CustomColor(1, 0, 1);

        @Setting(displayName = "wynntils.config.utils.item_highlights.unique_color.display_name", description = "wynntils.config.utils.item_highlights.unique_color.description")
        public CustomColor uniqueHighlightColor = new CustomColor(1, 1, 0);

        @Setting(displayName = "wynntils.config.utils.item_highlights.set_color.display_name", description = "wynntils.config.utils.item_highlights.set_color.description")
        public CustomColor setHighlightColor = new CustomColor(0, 1, 0);

        @Setting(displayName = "wynntils.config.utils.item_highlights.normal_color.display_name", description = "wynntils.config.utils.item_highlights.normal_color.description")
        public CustomColor normalHighlightColor = new CustomColor(1, 1, 1);

        @Setting(displayName = "wynntils.config.utils.item_highlights.crafted_color.display_name", description = "wynntils.config.utils.item_highlights.crafted_color.description")
        public CustomColor craftedHighlightColor = new CustomColor(0, .545f, .545f);

        @Setting(displayName = "wynntils.config.utils.item_highlights.onestar_color.display_name", description = "wynntils.config.utils.item_highlights.onestar_color.description")
        public CustomColor ingredientOneHighlightColor = new CustomColor(1, 0.97f, 0.6f);

        @Setting(displayName = "wynntils.config.utils.item_highlights.twostar_color.display_name", description = "wynntils.config.utils.item_highlights.twostar_color.description")
        public CustomColor ingredientTwoHighlightColor = new CustomColor(1, 1, 0);

        @Setting(displayName = "wynntils.config.utils.item_highlights.threestar_color.display_name", description = "wynntils.config.utils.item_highlights.threestar_color.description")
        public CustomColor ingredientThreeHighlightColor = new CustomColor(0.9f, .3f, 0);

        @Setting(displayName = "wynntils.config.utils.highlight_on_hotbar.display_name", description = "wynntils.config.utils.highlight_on_hotbar.description", order = 0)
        public boolean highlighItemsInHotbar = true;


        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "market", displayPath = "Main/Market")
    public static class Market extends SettingsClass {
        public static Market INSTANCE;

        @Setting(displayName = "Display Market Prices in a Custom Format", description = "Should market prices be displayed in a custom format?")
        public boolean displayInCustomFormat = true;

        @Setting(displayName = "Market Prices Format", description = "What format should market prices be displayed in?\n\n§8Brackets indicate all parameters inside must not be 0.")
        @Setting.Features.StringParameters(parameters = { "les", "ebs", "es", "stx", "le", "eb", "e" })
        public String customFormat = "(%stx%stx )(%le%%les% )(%eb%%ebs% )(%e%%es%)";
    }

    @Override
    public void onSettingChanged(String name) {

    }

}
