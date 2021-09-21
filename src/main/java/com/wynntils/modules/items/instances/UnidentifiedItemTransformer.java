package com.wynntils.modules.items.instances;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.entity.Entity;
import org.apache.commons.lang3.StringUtils;

import static net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes;

public class UnidentifiedItemTransformer extends ItemStackTransformManager.ConditionalTransformer<Entity> {
    public UnidentifiedItemTransformer() {
        super(new ItemStackTransformManager.ItemConsumer(stack -> {
            String itemName = WebManager.getTranslatedItemName(getTextWithoutFormattingCodes(stack.getDisplayName())).replace("֎", "");

            if (!ItemUtils.isUnidentified(stack)) return;

            if (stack.getDisplayName().contains("Unidentified"))  return;

            if (WebManager.getItems().get(itemName) != null) {
                ItemProfile item = WebManager.getItems().get(itemName);
                ItemType type = item.getItemInfo().getType();
                stack.setStackDisplayName(ItemTier.fromBoxDamage(stack.getItemDamage()) + "Unidentified " + StringUtils.capitalize(type.name().toLowerCase()));
                return;
            }

            stack.setStackDisplayName(ItemTier.fromBoxDamage(stack.getItemDamage()) + "Unidentified Item");
        }), (e) -> true);
    }
}
