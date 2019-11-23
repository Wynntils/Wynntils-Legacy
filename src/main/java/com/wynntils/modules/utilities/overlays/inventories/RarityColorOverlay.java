/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.glTexEnvi;

public class RarityColorOverlay implements Listener {

    private static String professionFilter = "-";

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        drawChest(e.getGuiInventory(), e.getGuiInventory().getLowerInv(), e.getGuiInventory().getUpperInv(), true, true);
    }

    @SubscribeEvent
    public void onHorseInventory(GuiOverlapEvent.HorseOverlap.DrawGuiContainerForegroundLayer e) {
        drawChest(e.getGuiInventory(), e.getGuiInventory().getUpperInv(), e.getGuiInventory().getLowerInv(), true, false);
    }

    @SubscribeEvent
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawGuiContainerForegroundLayer e) {
        for (Slot s : e.getGuiInventory().inventorySlots.inventorySlots) {
            if (!UtilitiesConfig.Items.INSTANCE.accesoryHighlight && s.slotNumber >= 9 && s.slotNumber <= 12)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.hotbarHighlight && s.slotNumber >= 36 && s.slotNumber <= 41)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.armorHighlight && s.slotNumber >= 5 && s.slotNumber <= 8)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.mainHighlightInventory && s.slotNumber >= 13 && s.slotNumber <= 35)
                continue;

            ItemStack is = s.getStack();
            String lore = Utils.getStringLore(is);
            String name = is.getDisplayName();
            float r, g, b;

            if (is.isEmpty()) {
                continue;
            } else if (lore.contains("Reward") || StringUtils.containsIgnoreCase(lore, "rewards")) {
                continue;
            } else if (lore.contains(TextFormatting.RED + "Fabled") && UtilitiesConfig.Items.INSTANCE.fabledHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.b;
            } else if (lore.contains(TextFormatting.AQUA + "Legendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.b;
            } else if (lore.contains(TextFormatting.DARK_PURPLE + "Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.b;
            } else if (lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.b;
            } else if (lore.contains(TextFormatting.YELLOW + "Unique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.b;
            } else if (lore.contains(TextFormatting.GREEN + "Set") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.setHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.setHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.setHighlightColor.b;
            } else if (lore.contains(TextFormatting.WHITE + "Normal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.b;
            } else if (name.matches("^(" + TextFormatting.DARK_AQUA + ".*%.*)$")) {
                r = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.b;
            } else if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫" + TextFormatting.DARK_GRAY + "✫✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.b;
            } else if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.b;
            } else if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.b;
            } else if (isPowder(is) && UtilitiesConfig.Items.INSTANCE.powderHighlight) {
                if (getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier)
                    continue;
                r = getPowderColor(is)[0];
                g = getPowderColor(is)[1];
                b = getPowderColor(is)[2];
            } else {
                continue;
            }

            //start rendering
            ScreenRenderer renderer = new ScreenRenderer();
            ScreenRenderer.beginGL(0, 0); {
                color(r, g, b, 1.0f);
                glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                RenderHelper.disableStandardItemLighting();

                renderer.drawRect(Textures.UIs.rarity, s.xPos - 1, s.yPos - 1, 0, 0, 18, 18);

                glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                color(1.0f, 1.0f, 1.0f, 1.0f);
            } ScreenRenderer.endGL();
        }
    }

    public void drawChest(GuiContainer guiContainer, IInventory lowerInv, IInventory upperInv, boolean emeraldsUpperInv, boolean emeraldsLowerInv) {
        int playerInvSlotNumber = 0;

        for (Slot s : guiContainer.inventorySlots.inventorySlots) {
            if (s.inventory.getDisplayName().equals(ModCore.mc().player.inventory.getDisplayName())) {
                playerInvSlotNumber++;
                if (playerInvSlotNumber <= 4 && playerInvSlotNumber >= 1 && !UtilitiesConfig.Items.INSTANCE.accesoryHighlight)
                    continue;
                if (playerInvSlotNumber <= 27 && playerInvSlotNumber >= 5 && !UtilitiesConfig.Items.INSTANCE.mainHighlightInventory)
                    continue;
                if (playerInvSlotNumber <= 36 && playerInvSlotNumber >= 28 && !UtilitiesConfig.Items.INSTANCE.hotbarHighlight)
                    continue;
            } else {
                if (!UtilitiesConfig.Items.INSTANCE.mainHighlightChest)
                    continue;
            }

            ItemStack is = s.getStack();
            String lore = Utils.getStringLore(is);
            String name = is.getDisplayName();
            float r, g, b;

            if (is.isEmpty()) {
                continue;
            } else if (UtilitiesConfig.Items.INSTANCE.filterEnabled && !professionFilter.equals("-") && lore.contains(professionFilter)) {
                r = 0.078f; g = 0.35f; b = 0.8f;
            } else if (lore.contains(TextFormatting.RED + "Fabled") && UtilitiesConfig.Items.INSTANCE.fabledHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor.b;
            } else if (lore.contains(TextFormatting.AQUA + "Legendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor.b;
            } else if (lore.contains(TextFormatting.DARK_PURPLE + "Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor.b;
            } else if (lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.rareHighlightColor.b;
            } else if (lore.contains(TextFormatting.YELLOW + "Unique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor.b;
            } else if (lore.contains(TextFormatting.GREEN + "Set") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.setHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.setHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.setHighlightColor.b;
            } else if (lore.contains(TextFormatting.WHITE + "Normal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
                r = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.normalHighlightColor.b;
            } else if (UtilitiesConfig.Items.INSTANCE.highlightCosmeticDuplicates && guiContainer.getSlotUnderMouse() != null && lore.contains("Reward") && guiContainer.getSlotUnderMouse().slotNumber != s.slotNumber && guiContainer.getSlotUnderMouse().getStack().getDisplayName().equals(is.getDisplayName())) {
                r = 0f; g = 1f; b = 0f;
            } else if (lore.contains(TextFormatting.GOLD + "Epic") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.epicEffectsHighlight) {
                r = 1; g = 0.666f; b = 0;
            } else if (lore.contains(TextFormatting.RED + "Godly") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.godlyEffectsHighlight) {
                r = 1; g = 0; b = 0;
            } else if (lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.rareEffectsHighlight) {
                r = 1; g = 0; b = 1;
            } else if (lore.contains(TextFormatting.WHITE + "Common") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.commonEffectsHighlight) {
                r = 1; g = 1; b = 1;
            } else if (lore.contains(TextFormatting.DARK_RED + " Black Market") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.blackMarketEffectsHighlight) {
                r = 0; g = 0; b = 0;
            } else if (name.matches("^(" + TextFormatting.DARK_AQUA + ".*%.*)$")) {
                r = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.craftedHighlightColor.b;
            } else if (name.contains(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫" + TextFormatting.DARK_GRAY + "✫✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor.b;
            } else if (name.contains(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor.b;
            } else if (name.contains(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                r = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.r; g = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.g; b = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor.b;
            } else if (isPowder(is) && UtilitiesConfig.Items.INSTANCE.powderHighlight) {
                if (getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier)
                    continue;
                r = getPowderColor(is)[0];
                g = getPowderColor(is)[1];
                b = getPowderColor(is)[2];
            } else {
                continue;
            }

            //start rendering
            ScreenRenderer renderer = new ScreenRenderer();
            ScreenRenderer.beginGL(0, 0); {
                color(r, g, b, 1F);
                glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                RenderHelper.disableStandardItemLighting();

                renderer.drawRect(Textures.UIs.rarity, s.xPos - 1, s.yPos - 1, 0, 0, 18, 18);

                glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                color(1F, 1F, 1F, 1F);
            } ScreenRenderer.endGL();
        }
    }

    private boolean isPowder(ItemStack is) {
        return (is.hasDisplayName() && is.getDisplayName().contains("Powder") && TextFormatting.getTextWithoutFormattingCodes(Utils.getStringLore(is)).contains("Effect on Weapons"));
    }

    private int getPowderTier(ItemStack is) {
        if (is.getDisplayName().endsWith("III")) {
            return 3;
        } else if (is.getDisplayName().endsWith("IV")) {
            return 4;
        } else if (is.getDisplayName().endsWith("VI")) {
            return 6;
        } else if (is.getDisplayName().endsWith("V")) {
            return 5;
        } else if (is.getDisplayName().endsWith("II")) {
            return 2;
        } else {
            return 1;
        }
    }

    private float[] getPowderColor(ItemStack is) {
        float[] returnVal;
        if (is.getDisplayName().startsWith(TextFormatting.YELLOW.toString())) {
            // Lightning
            returnVal = new float[]{1f, 1f, 0.333f};
        } else if (is.getDisplayName().startsWith(TextFormatting.AQUA.toString())) {
            // Water
            returnVal = new float[]{0.333f, 1f, 1f};
        } else if (is.getDisplayName().startsWith(TextFormatting.WHITE.toString())) {
            // Air
            returnVal = new float[]{1f, 1f, 1f};
        } else if (is.getDisplayName().startsWith(TextFormatting.DARK_GREEN.toString())) {
            // Earth
            returnVal = new float[]{0f, 0.666f, 0f};
        } else {
            // Fire
            returnVal = new float[]{1f, 0.333f, 0.333f};
        }

        return returnVal;
    }

    public static void setProfessionFilter(String s) {
        professionFilter = s;
    }

    public static String getProfessionFilter() {
        return professionFilter;
    }

}
