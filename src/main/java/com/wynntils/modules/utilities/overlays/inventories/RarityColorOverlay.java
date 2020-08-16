/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.PI;
import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.glTexEnvi;
import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

public class RarityColorOverlay implements Listener {

    public static final float MAX_LEVEL = 105.0f;
    public static final float MAX_CIRCLE_STEPS = 16.0f;
    private static String professionFilter = "-";

    @SubscribeEvent
    public void onChestClosed(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        resetCount(e.getGui());
    }

    @SubscribeEvent
    public void onHorseClosed(GuiOverlapEvent.HorseOverlap.GuiClosed e) {
        resetCount(e.getGui());
    }

    @SubscribeEvent
    public void onInventoryClosed(GuiOverlapEvent.InventoryOverlap.GuiClosed e) {
        resetCount(e.getGui());
    }

    private void resetCount(GuiContainer guiContainer) {
        for (Slot s : guiContainer.inventorySlots.inventorySlots) {
            ItemStack is = s.getStack();
            String lore = ItemUtils.getStringLore(is);
            int level = getLevel(lore);

            if (level != -1) {
                is.setCount(1);
            }
        }
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerBackgroundLayer e) {
        drawChest(e.getGui(), e.getGui().getLowerInv(), e.getGui().getUpperInv(), true, true);
    }

    @SubscribeEvent
    public void onHorseInventory(GuiOverlapEvent.HorseOverlap.DrawGuiContainerBackgroundLayer e) {
        drawChest(e.getGui(), e.getGui().getUpperInv(), e.getGui().getLowerInv(), true, false);
    }

    @SubscribeEvent
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawGuiContainerBackgroundLayer e) {
        GuiContainer guiContainer = e.getGui();

        for (Slot s : guiContainer.inventorySlots.inventorySlots) {
            if (!UtilitiesConfig.Items.INSTANCE.accesoryHighlight && s.slotNumber >= 9 && s.slotNumber <= 12)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.hotbarHighlight && s.slotNumber >= 36 && s.slotNumber <= 41)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.armorHighlight && s.slotNumber >= 5 && s.slotNumber <= 8)
                continue;
            if (!UtilitiesConfig.Items.INSTANCE.mainHighlightInventory && s.slotNumber >= 13 && s.slotNumber <= 35)
                continue;

            drawItemSlot(guiContainer, false, s);
        }
    }

    public static void drawChest(GuiContainer guiContainer, IInventory lowerInv, IInventory upperInv, boolean emeraldsUpperInv, boolean emeraldsLowerInv) {
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

            drawItemSlot(guiContainer, true, s);
        }
    }

    private static void drawItemSlot(GuiContainer guiContainer, boolean isChest, Slot s) {
        ItemStack is = s.getStack();
        String lore = ItemUtils.getStringLore(is);
        String name = StringUtils.normalizeBadString(is.getDisplayName());

        CustomColor colour = getHighlightColor(s, is, lore, name, isChest, guiContainer.getSlotUnderMouse());
        int level = getLevel(lore);

        if (level != -1) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                is.setCount(level);
            } else {
                is.setCount(1);
            }
        }

        // start rendering
        drawLevelArc(guiContainer, s, level);
        drawHighlightColor(guiContainer, s, colour);
    }

    private static CustomColor getHighlightColor(Slot s, ItemStack is, String lore, String name, boolean isChest, Slot slotUnderMouse) {
        if (is.isEmpty()) {
            return null;
        } else if (!isChest && (lore.contains("Reward") || containsIgnoreCase(lore, "rewards"))) {
            return null;
        } else if (isChest && UtilitiesConfig.Items.INSTANCE.filterEnabled && !professionFilter.equals("-") && lore.contains(professionFilter)) {
            return new CustomColor(0.078f, 0.35f, 0.8f);
        } else if (isChest && UtilitiesConfig.Items.INSTANCE.highlightCosmeticDuplicates && slotUnderMouse != null && lore.contains("Reward") && slotUnderMouse.slotNumber != s.slotNumber && slotUnderMouse.getStack().getDisplayName().equals(name)) {
            return new CustomColor(0f, 1f, 0f);
        } else if (isChest && lore.contains(TextFormatting.GOLD + "Epic") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.epicEffectsHighlight) {
            return new CustomColor(1f, 0.666f, 0f);
        } else if (isChest && lore.contains(TextFormatting.RED + "Godly") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.godlyEffectsHighlight) {
            return new CustomColor(1f, 0f, 0f);
        } else if (isChest && lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.rareEffectsHighlight) {
            return new CustomColor(1f, 0f, 1f);
        } else if (isChest && lore.contains(TextFormatting.WHITE + "Common") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.commonEffectsHighlight) {
            return new CustomColor(1f, 1f, 1f);
        } else if (isChest && lore.contains(TextFormatting.DARK_RED + " Black Market") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.blackMarketEffectsHighlight) {
            return new CustomColor(0f, 0f, 0f);
        } else if (lore.contains(TextFormatting.RED + "Fabled") && UtilitiesConfig.Items.INSTANCE.fabledHighlight) {
            return UtilitiesConfig.Items.INSTANCE.fabledHighlightColor;
        } else if (lore.contains(TextFormatting.AQUA + "Legendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
            return UtilitiesConfig.Items.INSTANCE.lengendaryHighlightColor;
        } else if (lore.contains(TextFormatting.DARK_PURPLE + "Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
            return UtilitiesConfig.Items.INSTANCE.mythicHighlightColor;
        } else if (lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
            return UtilitiesConfig.Items.INSTANCE.rareHighlightColor;
        } else if (lore.contains(TextFormatting.YELLOW + "Unique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
            return UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor;
        } else if (lore.contains(TextFormatting.GREEN + "Set") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
            return UtilitiesConfig.Items.INSTANCE.setHighlightColor;
        } else if (lore.contains(TextFormatting.WHITE + "Normal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
            return UtilitiesConfig.Items.INSTANCE.normalHighlightColor;
        } else if (name.matches("^(" + TextFormatting.DARK_AQUA + ".*%.*)$")) {
            return UtilitiesConfig.Items.INSTANCE.craftedHighlightColor;
        } else if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫" + TextFormatting.DARK_GRAY + "✫✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
            return UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor;
        } else if ((name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.GOLD + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight || name.endsWith(TextFormatting.DARK_PURPLE + " [" + TextFormatting.LIGHT_PURPLE + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.DARK_PURPLE + "]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) && !(is.getCount() == 0)) {
            return UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor;
        } else if ((name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫✫" + TextFormatting.GOLD + "]") || name.endsWith(TextFormatting.DARK_AQUA + " [" + TextFormatting.AQUA + "✫✫✫" + TextFormatting.DARK_AQUA + "]")) && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
            return UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor;
        } else if (isPowder(is)) {
            if (UtilitiesConfig.Items.INSTANCE.minPowderTier == 0 || getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier) {
                return null;
            } else {
                return getPowderColor(is);
            }
        } else {
            return null;
        }
    }

    private static int getLevel(String lore) {
        Pattern p = Pattern.compile("Combat Lv. Min: ([0-9]+)");
        Matcher m = p.matcher(lore);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        } else {
            Pattern p2 = Pattern.compile("Lv. Range: " + TextFormatting.WHITE.toString() + "([0-9]+)-([0-9]+)");
            Matcher m2 = p2.matcher(lore);
            if (m2.find()) {
                int lowLevel =  Integer.parseInt(m2.group(1));
                int highLevel =  Integer.parseInt(m2.group(2));
                return (lowLevel + highLevel) / 2;
            }
        }
        return -1;
    }

    private static void drawArc(BufferBuilder renderer, int x, int y, int level, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int numSteps = (int)((level / MAX_LEVEL) * MAX_CIRCLE_STEPS);
        for (int i = 0; i <= numSteps; i++) {
            float angle = 2 * (float) PI * i / (MAX_CIRCLE_STEPS - 1.0f);
            renderer.pos(x + sin(angle) * 8.0F + 8, y - cos(angle) * 8.0F + 8, 0.0D).color(red, green, blue, alpha).endVertex();
        }
        Tessellator.getInstance().draw();
    }

    private static void drawLevelArc(GuiContainer guiContainer, Slot s, int level) {
        if (!UtilitiesConfig.Items.INSTANCE.itemLevelArc) return;
        if (level == -1) return;

        int x = guiContainer.getGuiLeft() + s.xPos;
        int y = guiContainer.getGuiTop() + s.yPos;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.glLineWidth(4.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        drawArc(bufferbuilder, x, y, level, 0, 0, 0, 120);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private static void drawHighlightColor(GuiContainer guiContainer, Slot s, CustomColor colour) {
        if (colour == null) return;

        ScreenRenderer renderer = new ScreenRenderer();
        ScreenRenderer.beginGL(guiContainer.getGuiLeft() + s.xPos, guiContainer.getGuiTop() + s.yPos);
        {
            color(colour.r, colour.g, colour.b, UtilitiesConfig.Items.INSTANCE.inventoryAlpha / 100);
            glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
            RenderHelper.disableStandardItemLighting();

            renderer.drawRect(Textures.UIs.rarity, -1, -1, 0, 0, 18, 18);

            glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            color(1.0f, 1.0f, 1.0f, 1.0f);
        }
        ScreenRenderer.endGL();
    }

    private static boolean isPowder(ItemStack is) {
        return (is.getItem() == Items.DYE && is.hasDisplayName() && is.getDisplayName().contains("Powder") &&
                TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(is)).contains("Effect on Weapons"));
    }

    private static int getPowderTier(ItemStack is) {
        String name = StringUtils.normalizeBadString(is.getDisplayName());
        if (name.endsWith("III")) {
            return 3;
        } else if (name.endsWith("IV")) {
            return 4;
        } else if (name.endsWith("VI")) {
            return 6;
        } else if (name.endsWith("V")) {
            return 5;
        } else if (name.endsWith("II")) {
            return 2;
        } else {
            return 1;
        }
    }

    private static final Map<Character, CustomColor> POWDER_COLOUR_MAP = new HashMap<>(10);
    static {
        // Lightning
        POWDER_COLOUR_MAP.put(TextFormatting.YELLOW.toString().charAt(1), new CustomColor(1, 1, 1 / 3f));
        // Water
        POWDER_COLOUR_MAP.put(TextFormatting.AQUA.toString().charAt(1), new CustomColor(1 / 3f, 1, 1));
        // Air
        POWDER_COLOUR_MAP.put(TextFormatting.WHITE.toString().charAt(1), new CustomColor(1f, 1f, 1f));
        // Earth
        POWDER_COLOUR_MAP.put(TextFormatting.DARK_GREEN.toString().charAt(1), new CustomColor(0, 2 / 3f, 0));
        // Fire
        POWDER_COLOUR_MAP.put(TextFormatting.RED.toString().charAt(1), new CustomColor(1, 1 / 3f, 1 / 3f));
    }

    private static CustomColor getPowderColor(ItemStack is) {
        String name = is.getDisplayName();
        if (name.length() < 2 || name.charAt(0) != '§') return null;
        return POWDER_COLOUR_MAP.get(name.charAt(1));
    }

    public static void setProfessionFilter(String s) {
        professionFilter = s;
    }

    public static String getProfessionFilter() {
        return professionFilter;
    }

}
