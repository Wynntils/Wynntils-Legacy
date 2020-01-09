/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import net.minecraft.item.ItemStack;

import java.util.List;

import static net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes;

public class DiscoveryInfo {

    private ItemStack originalStack;

    private String name;
    private DiscoveryType type;
    private List<String> lore;
    private String description;
    private int minLevel;

    private String friendlyName;

    boolean valid = false;

    public DiscoveryInfo(ItemStack originalStack) {
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
        if (friendlyName.length() > 22) {
            friendlyName = friendlyName.substring(0, 19);
            friendlyName += "...";
        }

        lore.add(0, this.name);
        valid = true;
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

    public boolean isValid() {
        return valid;
    }

}
