package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "main")
public class UtilitiesConfig extends SettingsHolder {

    @Setting(displayName = "Acessory Highlight on Inventories", description = "Do you want accessories to be highlighted on inventories?")
    public boolean accesoryHighlightChest = true;

    @Setting(displayName = "Hotbar Highlight on Inventories", description = "Do you want hotbar to be highlighted on inventories?")
    public boolean hotbarHighlightChest = true;

    @Setting(displayName = "Main Highlight on Inventories", description = "Do you want main to be highlighted on inventories?")
    public boolean mainHighlightChest = true;

    @Setting(displayName = "Acessory Highlight on the Player Inventory", description = "Do you want accessories to be highlighted on the player inventory?")
    public boolean accesoryHighlightInventory = true;

    @Setting(displayName = "Hotbar Highlight on the Player Inventory", description = "Do you want hotbar to be highlighted on the player inventory?")
    public boolean hotbarHighlightInventory = true;

    @Setting(displayName = "Main Highlight on the Player Inventory", description = "Do you want main to be highlighted on the player inventory?")
    public boolean mainHighlightInventory = true;

    @Setting(displayName = "Armor Highlight on the Player Inventory", description = "Do you want armor to be highlighted on the player inventory?")
    public boolean armorHighlightInventory = true;

    @Setting(displayName = "Mythic Items Highlight on the Player Inventory", description = "Do you want mythic items to be highlighted on the player inventory?")
    public boolean mythicHighlightInventory = true;

    @Setting(displayName = "Legendary Items Highlight on the Player Inventory", description = "Do you want legendary items to be highlighted on the player inventory?")
    public boolean legendaryHighlightInventory = true;

    @Setting(displayName = "Rare Items Highlight on the Player Inventory", description = "Do you want rare items to be highlighted on the player inventory?")
    public boolean rareHighlightInventory = true;

    @Setting(displayName = "Unique Items Highlight on the Player Inventory", description = "Do you want unique items to be highlighted on the player inventory?")
    public boolean uniqueHighlightInventory = true;

    @Setting(displayName = "Set Items Highlight on the Player Inventory", description = "Do you want set items to be highlighted on the player inventory?")
    public boolean setHighlightInventory = true;

    @Setting(displayName = "Mythic Items Highlight on inventories", description = "Do you want mythic items to be highlighted on inventories?")
    public boolean mythicHighlightChest = true;

    @Setting(displayName = "Legendary Items Highlight on inventories", description = "Do you want legendary items to be highlighted on inventories?")
    public boolean legendaryHighlightChest = true;

    @Setting(displayName = "Rare Items Highlight on inventories", description = "Do you want rare items to be highlighted on inventories?")
    public boolean rareHighlightChest = true;

    @Setting(displayName = "Unique Items Highlight on inventories", description = "Do you want unique items to be highlighted on inventories?")
    public boolean uniqueHighlightChest = true;

    @Setting(displayName = "Set Items Highlight on inventories", description = "Do you want set items to be highlighted on inventories?")
    public boolean setHighlightChest = true;

    @Setting(displayName = "Godly effects Highlight on inventories", description = "Do you want godly effects to be highlighted on inventories?")
    public boolean godlyEffectsHighlightChest = true;

    @Setting(displayName = "Epic effects Highlight on inventories", description = "Do you want epic effects to be highlighted on inventories?")
    public boolean epicEffectsHighlightChest = true;

    @Setting(displayName = "Rare effects Highlight on inventories", description = "Do you want rare effects to be highlighted on inventories?")
    public boolean rareEffectsHighlightChest = true;

    @Setting(displayName = "Common effects Highlight on inventories", description = "Do you want common effects to be highlighted on inventories?")
    public boolean commonEffectsHighlightChest = true;

    @Setting(displayName = "Allow emerald count on inventories", description = "Do you want to allow emerald count on inventories?")
    public boolean allowEmeraldCountChest = true;

    @Setting(displayName = "Allow emerald count on the Player Inventory", description = "Do you want to allow emerald count on the Player Inventory?")
    public boolean allowEmeraldCountInventory = true;

    @Setting(displayName = "Show World TPS on TAB", description = "Do you want to see the world TPS on tab?")
    public boolean showTPSCount = true;

    @Setting(displayName = "Daily Chest Reminder", description = "Do you want to receive daily chest reminders?")
    public boolean dailyReminder = true;

    @Override
    public void onSettingChanged(String name) {

    }

}
