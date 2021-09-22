/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.items;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.core.events.ClientEvents;
import com.wynntils.modules.core.events.ServerEvents;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.modules.items.instances.SkillPointTransformer;
import com.wynntils.modules.items.instances.StringNormalizationTransformer;
import com.wynntils.modules.items.instances.ItemIdentificationTransformer;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import com.wynntils.modules.items.overlays.*;

@ModuleInfo(name = "item", displayName = "items")
public class ItemModule extends Module {

    private static ItemModule module;

    public void onEnable() {
        registerEvents(new ClientEvents());
        registerEvents(new ServerEvents());

        registerSettings(ItemsConfig.Identifications.class);
        registerSettings(ItemsConfig.ItemHighlights.class);

        //overlays
        registerEvents(new ItemIdentificationOverlay());
        registerEvents(new RarityColorOverlay());
        registerEvents(new ItemSpecificationOverlay());
        registerEvents(new ServerSelectorOverlay());
        registerEvents(new ItemLevelOverlay());
        registerEvents(new EmeraldCountOverlay());
        registerEvents(new FavoriteItemsOverlay());
        registerEvents(new SkillPointOverlay());
        registerEvents(new ItemLockOverlay());
        registerEvents(new FavoriteTradesOverlay());
        registerEvents(new EmeraldCountOverlay());
        registerEvents(new LoreChangerOverlay());

        //transformers
        ItemStackTransformManager.registerGlobalTransform(new StringNormalizationTransformer());
        ItemStackTransformManager.registerEntityTransform(new ItemIdentificationTransformer());
        ItemStackTransformManager.registerGlobalTransform(new ItemStackTransformManager.ItemConsumer(s -> s.setStackDisplayName("test")));
        ItemStackTransformManager.registerGuiTransform(new SkillPointTransformer());

        module = this;
    }

    public static ItemModule getModule() {
        return module;
    }


}
