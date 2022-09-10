/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.EmeraldPouchManager;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    private static final Pattern DURABILITY_PATTERN = Pattern.compile("\\[([0-9]+)/([0-9]+) Durability\\]");
    private static String professionFilter = "-";

    private static final ScreenRenderer renderer = new ScreenRenderer();

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
            if (s.inventory.getDisplayName().equals(McIf.player().inventory.getDisplayName())) {
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

        // start rendering
        drawLevelArc(guiContainer, s, ItemUtils.getLevel(lore));
        drawHighlightColor(guiContainer, s, getHighlightColor(s, is, lore, name, isChest, guiContainer.getSlotUnderMouse()));
        drawDurabilityArc(guiContainer, s, getDurability(lore));
        drawPouchUsageArc(guiContainer, s, getPouchUsage(is));
    }

    private static CustomColor getHighlightColor(Slot s, ItemStack is, String lore, String name, boolean isChest, Slot slotUnderMouse) {
        if (is.isEmpty()) {
            return null;
        }
        if (!isChest && (lore.contains("Reward") || containsIgnoreCase(lore, "rewards")) && !lore.contains("Raid Reward")) {
            return null;
        }
        if (isChest) {
            if (is.getDisplayName().contains("§2 [Enabled]") && lore.contains("§7Requires")) { // Highlights for hardcore/ironman/hunted/crafted modifiers in class creation
                return new CustomColor(0f, 1f, 0f);
            }
            if (UtilitiesConfig.Items.INSTANCE.filterEnabled && !professionFilter.equals("-") && lore.contains(professionFilter)) {
                return UtilitiesConfig.Items.INSTANCE.professionFilterHighlightColor;
            }
            if (UtilitiesConfig.Items.INSTANCE.emeraldHighlightInChest && is.getItem() == Items.EMERALD && s.inventory.getDisplayName().getUnformattedText().startsWith("Loot Chest")) {
                return UtilitiesConfig.Items.INSTANCE.emeraldHighlightColor;
            }
            if (UtilitiesConfig.Items.INSTANCE.highlightCosmeticDuplicates && slotUnderMouse != null && lore.contains("Reward") && !lore.contains("Raid Reward") && slotUnderMouse.slotNumber != s.slotNumber && slotUnderMouse.getStack().getDisplayName().equals(name)) {
                return new CustomColor(0f, 1f, 0f);
            }
            if (lore.contains("Reward")) {
                if (UtilitiesConfig.Items.INSTANCE.epicEffectsHighlight && lore.contains(TextFormatting.GOLD + "Epic")) {
                    return new CustomColor(1f, 0.666f, 0f);
                }
                if (UtilitiesConfig.Items.INSTANCE.godlyEffectsHighlight && lore.contains(TextFormatting.RED + "Godly")) {
                    return new CustomColor(1f, 0f, 0f);
                }
                if (UtilitiesConfig.Items.INSTANCE.rareEffectsHighlight && lore.contains(TextFormatting.LIGHT_PURPLE + "Rare")) {
                    return new CustomColor(1f, 0f, 1f);
                }
                if (UtilitiesConfig.Items.INSTANCE.commonEffectsHighlight && lore.contains(TextFormatting.WHITE + "Common")) {
                    return new CustomColor(1f, 1f, 1f);
                }
                if (UtilitiesConfig.Items.INSTANCE.blackMarketEffectsHighlight && lore.contains(TextFormatting.DARK_RED + " Black Market")) {
                    return new CustomColor(0f, 0f, 0f);
                }
            }
        }
        if (UtilitiesConfig.Items.INSTANCE.normalHighlight && lore.contains(ItemTier.NORMAL.asFormattedName())) {
            return ItemTier.NORMAL.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.uniqueHighlight && lore.contains(ItemTier.UNIQUE.asFormattedName())) {
            return ItemTier.UNIQUE.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.rareHighlight && lore.contains(ItemTier.RARE.asFormattedName())) {
            return ItemTier.RARE.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.setHighlight && lore.contains(ItemTier.SET.asFormattedName())) {
            return ItemTier.SET.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.legendaryHighlight && lore.contains(ItemTier.LEGENDARY.asFormattedName())) {
            return ItemTier.LEGENDARY.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.fabledHighlight && lore.contains(ItemTier.FABLED.asFormattedName())) {
            return ItemTier.FABLED.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.mythicHighlight && lore.contains(ItemTier.MYTHIC.asFormattedName())) {
            return ItemTier.MYTHIC.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.craftedHighlight && name.matches("^(" + TextFormatting.DARK_AQUA + ".*%.*)$")) {
            return ItemTier.CRAFTED.getCustomizedHighlightColor();
        }
        if (UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
            if (UtilitiesConfig.Items.INSTANCE.minCraftingIngredientHighlightTier <= 1
                    && name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫" + TextFormatting.DARK_GRAY + "✫✫" + TextFormatting.GOLD + "]")) {
                return UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor;
            }
            if (UtilitiesConfig.Items.INSTANCE.minCraftingIngredientHighlightTier <= 2
                    && (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.GOLD + "]") ||
                    name.endsWith(TextFormatting.DARK_PURPLE + " [" + TextFormatting.LIGHT_PURPLE + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.DARK_PURPLE + "]"))) {
                return UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor;
            }
            if (UtilitiesConfig.Items.INSTANCE.minCraftingIngredientHighlightTier <= 3
                    && (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫✫" + TextFormatting.GOLD + "]") ||
                    name.endsWith(TextFormatting.DARK_AQUA + " [" + TextFormatting.AQUA + "✫✫✫" + TextFormatting.DARK_AQUA + "]"))) {
                return UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor;
            }
        }
        if (isPowder(is)) {
            if (UtilitiesConfig.Items.INSTANCE.minPowderTier == 0 || getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier) {
                return null;
            } else {
                return getPowderColor(is);
            }
        }
        return null;
    }

    private static float getPouchUsage(ItemStack is) {
        if (!EmeraldPouchManager.isEmeraldPouch(is)) {
            return -1;
        }
        int usage = EmeraldPouchManager.getPouchUsage(is);
        int capacity = EmeraldPouchManager.getPouchCapacity(is);
        return (float) usage / capacity;
    }

    private static void drawPouchUsageArc(GuiContainer guiContainer, Slot s, float usage) {
        if (!UtilitiesConfig.Items.INSTANCE.emeraldPouchArc || usage == -1) return;

        // This is 100% stolen from the function immediately below
        int x = guiContainer.getGuiLeft() + s.xPos;
        int y = guiContainer.getGuiTop() + s.yPos;
        // So (float usage) returns a number from 0-1 representing the percentage used of the emerald pouch
        // When the pouch is full, we want red; when empty, green. The hue scale does this perfectly - green is 120', red is 0'.
        // (MathHelper.hsvToRGB) takes a float input from 0-1; green is 0.33333, red is still 0.
        // Since we don't like manually typing out infinitely repeating decimals, we can do (120 * inverseUsage) / 360.
        // In this case, when usage is 1 (100%), it'll be 120 * 0 = 0, 0 / 360 = 0 (red), correct
        // When usage is 0 (0%), it'll be 120 * 1 = 120, 120 / 360 = 0.333333 (green), correct
        // Below, all this is simplified into inverseUsage / 3.0F
        int arcColor = MathHelper.hsvToRGB((1 - usage) / 3.0F, 1.0F, 1.0F);
        int radius = (UtilitiesConfig.Items.INSTANCE.itemLevelArc) ? 7 : 8;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.glLineWidth(4.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        drawArc(bufferbuilder, x, y, usage, radius, arcColor >> 16 & 255, arcColor >> 8 & 255, arcColor & 255, 160);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private static float getDurability(String lore) {
        Matcher m = DURABILITY_PATTERN.matcher(lore);
        if (m.find()) {
            return Float.parseFloat(m.group(1)) / Float.parseFloat(m.group(2));
        }
        return -1;

    }

    private static void drawDurabilityArc(GuiContainer guiContainer, Slot s, float durability) {
        if (!UtilitiesConfig.Items.INSTANCE.craftedDurabilityBars) return;
        if (durability == -1) return;

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
        int arcColor = MathHelper.hsvToRGB(Math.max(0.0F, durability) / 3.0F, 1.0F, 1.0F);
        int radius = (UtilitiesConfig.Items.INSTANCE.itemLevelArc) ? 7 : 8;
        drawArc(bufferbuilder, x, y, durability, radius, arcColor >> 16 & 255, arcColor >> 8 & 255, arcColor & 255, 160);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private static void drawArc(BufferBuilder renderer, int x, int y, float fill, int radius, int red, int green, int blue, int alpha) {
        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        int numSteps = (int) Math.min(fill * MAX_CIRCLE_STEPS, MAX_CIRCLE_STEPS - 1); //otherwise arc can overlap itself
        for (int i = 0; i <= numSteps; i++) {
            float angle = 2 * (float) PI * i / (MAX_CIRCLE_STEPS - 1.0f);
            renderer.pos(x + sin(angle) * radius + 8, y - cos(angle) * radius + 8, 0.0D).color(red, green, blue, alpha).endVertex();
        }
        Tessellator.getInstance().draw();
    }

    private static void drawLevelArc(GuiContainer guiContainer, Slot s, IntRange level) {
        if (!UtilitiesConfig.Items.INSTANCE.itemLevelArc) return;
        if (level == null) return;

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
        float arcFill = (level.getAverage() / MAX_LEVEL);
        drawArc(bufferbuilder, x, y, arcFill, 8, 0, 0, 0, 120);

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private static void drawHighlightColor(GuiContainer guiContainer, Slot s, CustomColor colour) {
        if (colour == null) return;

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
