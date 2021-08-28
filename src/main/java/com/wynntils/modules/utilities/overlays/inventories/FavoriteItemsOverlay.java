/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraft.util.text.TextFormatting.*;

public class FavoriteItemsOverlay implements Listener {

    private static final ScreenRenderer renderer = new ScreenRenderer();

    private static void renderFavorites(GuiContainer gui) {
        if (!Reference.onWorld) return;

        for (Slot s : gui.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName() || !stack.hasTagCompound()) continue;
            NBTTagCompound nbt = stack.getTagCompound();
            if (!nbt.getBoolean("wynntilsFavorite")) continue;

            // draw star
            ScreenRenderer.beginGL(gui.getGuiLeft() + s.xPos + 10, gui.getGuiTop() + s.yPos - 5);
            GlStateManager.translate(0, 0, 300);

            RenderHelper.disableStandardItemLighting();
            ScreenRenderer.scale(0.5f);
            renderer.drawRect(Textures.Map.map_icons, 0, 0, 208, 36, 18, 18);
            ScreenRenderer.endGL();
        }
    }

    private static void checkFavorites(GuiContainer gui) {
        if (!Reference.onWorld) return;

        for (Slot s : gui.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName() || !stack.hasTagCompound()) continue;

            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("wynntilsFavorite")) continue; // already been checked

            String itemName = StringUtils.normalizeBadString(getTextWithoutFormattingCodes(stack.getDisplayName()));
            if (!itemName.contains("Unidentified")) {
                nbt.setBoolean("wynntilsFavorite", false);
                continue; // don't care about identified items
            }

            String items = ItemIdentificationOverlay.getItemsFromBox(stack);
            if (items == null) continue;

            for (String possibleItem : items.split(", ")) {
                ItemProfile itemProfile = WebManager.getItems().get(possibleItem);
                if (itemProfile == null) continue;

                if (itemProfile.isFavorited()) {
                    nbt.setBoolean("wynntilsFavorite", true);
                    break;
                }
            }

            if (!nbt.hasKey("wynntilsFavorite")) // wasn't favorited
                nbt.setBoolean("wynntilsFavorite", false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        checkFavorites(e.getGui());
        renderFavorites(e.getGui());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInventoryGui(GuiOverlapEvent.InventoryOverlap.HoveredToolTip.Pre e) {
        checkFavorites(e.getGui());
        renderFavorites(e.getGui());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHorseGui(GuiOverlapEvent.HorseOverlap.HoveredToolTip.Pre e) {
        checkFavorites(e.getGui());
        renderFavorites(e.getGui());
    }

}
