/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FavoriteItemsOverlay implements Listener {
    private static final ScreenRenderer renderer = new ScreenRenderer();

    private static void checkFavorites(ItemStack stack) {
        if (!Reference.onWorld) return;

        if (stack.isEmpty() || !stack.hasDisplayName() || !stack.hasTagCompound()) return;

        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey("wynntilsFavorite")) { //Calculate if favorite
            if (!ItemUtils.isUnidentified(stack)) {
                nbt.setBoolean("wynntilsFavorite", false);
                return; // don't care about identified items
            }

            String items = ItemIdentificationOverlay.getItemsFromBox(stack);
            if (items == null) return;

            for (String possibleItem : items.split(", ")) {
                ItemProfile itemProfile = WebManager.getItems().get(possibleItem);
                if (itemProfile == null) return;

                if (itemProfile.isFavorited()) {
                    nbt.setBoolean("wynntilsFavorite", true);
                }
            }

            if (!nbt.hasKey("wynntilsFavorite")) // wasn't favorited
                nbt.setBoolean("wynntilsFavorite", false);
        }

        if (!nbt.getBoolean("wynntilsFavorite")) return;

        // draw star
        GlStateManager.translate(10, -5, 0);

        RenderHelper.disableStandardItemLighting();
        ScreenRenderer.scale(0.5f);
        renderer.drawRect(Textures.Map.map_icons, 0, 0, 208, 36, 18, 18);
        GlStateManager.translate(-10, 5, 0);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawItem(RenderEvent.RenderItem e) {
        checkFavorites(e.getStack());
    }
}
