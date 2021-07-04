/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes;

public class DiscoveryInfo {

    private ItemStack originalStack;

    private String name;
    private DiscoveryType type;
    private List<String> lore;
    private String description;
    private int minLevel;
    private TerritoryProfile guildTerritory = null;

    private String friendlyName;

    boolean valid = false;
    boolean discovered = false;

    public DiscoveryInfo(ItemStack originalStack, boolean discovered) {
        this.originalStack = originalStack;

        lore = ItemUtils.getLore(originalStack);

        // simple parameters
        name = originalStack.getDisplayName();
        name = StringUtils.normalizeBadString(name.substring(0, name.length() - 1));
        minLevel = Integer.parseInt(getTextWithoutFormattingCodes(lore.get(0)).replace("✔ Combat Lv. Min: ", ""));

        // type
        type = null;
        if (name.charAt(1) == 'e') type = DiscoveryType.WORLD;
        else if (name.charAt(1) == 'f') type = DiscoveryType.TERRITORY;
        else if (name.charAt(1) == 'b') type = DiscoveryType.SECRET;
        else return;

        // flat description
        StringBuilder descriptionBuilder = new StringBuilder();
        for (int x = 2; x < lore.size(); x++) {
            descriptionBuilder.append(getTextWithoutFormattingCodes(lore.get(x)));
        }
        description = descriptionBuilder.toString();

        friendlyName = name.substring(4);

        // Guild territory profile
        if (type == DiscoveryType.TERRITORY || type == DiscoveryType.WORLD) {
            String apiName = TextFormatting.getTextWithoutFormattingCodes(name);
            guildTerritory = WebManager.getTerritories().get(apiName);
            if (guildTerritory == null) {
                guildTerritory = WebManager.getTerritories().get(apiName.replace('\'', '’'));
            }
        }

        lore.add(0, this.name);
        this.discovered = discovered;
        valid = true;
    }

    public DiscoveryInfo(String name, DiscoveryType type, int minLevel, boolean discovered) {
        this.name = name;
        this.friendlyName = name;

        this.lore = new ArrayList<>();
        lore.add(type.getColour() + "" + TextFormatting.BOLD + this.name);
        lore.add((minLevel <= PlayerInfo.get(CharacterData.class).getLevel() ? TextFormatting.GREEN + "✔" : TextFormatting.RED + "✖") + TextFormatting.GRAY + " Combat Lv. Min: " + minLevel);
        lore.add("");

        this.minLevel = minLevel;
        this.type = type;

        // Guild territory profile
        if (type == DiscoveryType.TERRITORY || type == DiscoveryType.WORLD) {
            String apiName = TextFormatting.getTextWithoutFormattingCodes(name);
            guildTerritory = WebManager.getTerritories().get(apiName);
            if (guildTerritory == null) {
                guildTerritory = WebManager.getTerritories().get(apiName.replace('\'', '’'));
            }
        }

        this.originalStack = ItemStack.EMPTY;
        this.discovered = discovered;
    }

    public String getName() {
        return name;
    }

    public DiscoveryType getType() {
        return type;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getDescription() {
        return description;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ItemStack getOriginalStack() {
        return originalStack;
    }

    public TerritoryProfile getGuildTerritoryProfile() {
        return guildTerritory;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean wasDiscovered() {
        return discovered;
    }

}
