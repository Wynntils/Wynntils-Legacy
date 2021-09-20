package com.wynntils.modules.items.instances;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.items.managers.ItemStackTransformManager;

import java.util.ArrayList;
import java.util.List;

public class StringNormalizationTransformer extends ItemStackTransformManager.ItemConsumer {
    public StringNormalizationTransformer() {
        super(stack -> {
            List<String> lore = ItemUtils.getLore(stack);
            String name = StringUtils.normalizeBadString(stack.getDisplayName());

            // name and lore fixing
            stack.setStackDisplayName(name);
            List<String> fixedLore = new ArrayList<>();
            for (String line : lore) {
                fixedLore.add(StringUtils.normalizeBadString(line));
            }
            ItemUtils.replaceLore(stack, fixedLore);
        });
    }
}
