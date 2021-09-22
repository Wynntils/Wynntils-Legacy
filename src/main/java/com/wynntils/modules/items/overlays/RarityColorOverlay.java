/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.modules.items.configs.ItemsConfig;
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
            if (!ItemsConfig.ItemHighlights.INSTANCE.accesoryHighlight && s.slotNumber >= 9 && s.slotNumber <= 12)
                continue;
            if (!ItemsConfig.ItemHighlights.INSTANCE.hotbarHighlight && s.slotNumber >= 36 && s.slotNumber <= 41)
                continue;
            if (!ItemsConfig.ItemHighlights.INSTANCE.armorHighlight && s.slotNumber >= 5 && s.slotNumber <= 8)
                continue;
            if (!ItemsConfig.ItemHighlights.INSTANCE.mainHighlightInventory && s.slotNumber >= 13 && s.slotNumber <= 35)
                continue;

            drawItemSlot(guiContainer, false, s);
        }
    }

    public static void drawChest(GuiContainer guiContainer, IInventory lowerInv, IInventory upperInv, boolean emeraldsUpperInv, boolean emeraldsLowerInv) {
        int playerInvSlotNumber = 0;

        for (Slot s : guiContainer.inventorySlots.inventorySlots) {
            if (s.inventory.getDisplayName().equals(McIf.player().inventory.getDisplayName())) {
                playerInvSlotNumber++;
                if (playerInvSlotNumber <= 4 && playerInvSlotNumber >= 1 && !ItemsConfig.ItemHighlights.INSTANCE.accesoryHighlight)
                    continue;
                if (playerInvSlotNumber <= 27 && playerInvSlotNumber >= 5 && !ItemsConfig.ItemHighlights.INSTANCE.mainHighlightInventory)
                    continue;
                if (playerInvSlotNumber <= 36 && playerInvSlotNumber >= 28 && !ItemsConfig.ItemHighlights.INSTANCE.hotbarHighlight)
                    continue;
            } else {
                if (!ItemsConfig.ItemHighlights.INSTANCE.mainHighlightChest)
                    continue;
            }

            drawItemSlot(guiContainer, true, s);
        }
    }

    private static void drawItemSlot(GuiContainer guiContainer, boolean isChest, Slot s) {
        ItemStack is = s.getStack();
        String lore = ItemUtils.getStringLore(is);
        String name = is.getDisplayName();

        // start rendering
        drawLevelArc(guiContainer, s, ItemUtils.getLevel(lore));
        drawHighlightColor(guiContainer, s, getHighlightColor(s, is, lore, name, isChest, guiContainer.getSlotUnderMouse()));
        drawDurabilityArc(guiContainer, s, getDurability(lore));
    }

    private static CustomColor getHighlightColor(Slot s, ItemStack is, String lore, String name, boolean isChest, Slot slotUnderMouse) {
        if (is.isEmpty()) {
            return null;
        }
        if (!isChest && (lore.contains("Reward") || containsIgnoreCase(lore, "rewards")) && !lore.contains("Raid Reward")) {
            return null;
        }
        if (isChest) {
            if (ItemsConfig.ItemHighlights.INSTANCE.filterEnabled && !professionFilter.equals("-") && lore.contains(professionFilter)) {
                return new CustomColor(0.078f, 0.35f, 0.8f);
            }
            if (ItemsConfig.ItemHighlights.INSTANCE.highlightCosmeticDuplicates && slotUnderMouse != null && lore.contains("Reward") && !lore.contains("Raid Reward") && slotUnderMouse.slotNumber != s.slotNumber && slotUnderMouse.getStack().getDisplayName().equals(name)) {
                return new CustomColor(0f, 1f, 0f);
            }
            if (lore.contains("Reward")) {
                if (lore.contains(TextFormatting.GOLD + "Epic") && ItemsConfig.ItemHighlights.INSTANCE.epicEffectsHighlight) {
                    return new CustomColor(1f, 0.666f, 0f);
                }
                if (lore.contains(TextFormatting.RED + "Godly") && ItemsConfig.ItemHighlights.INSTANCE.godlyEffectsHighlight) {
                    return new CustomColor(1f, 0f, 0f);
                }
                if (lore.contains(TextFormatting.LIGHT_PURPLE + "Rare") && ItemsConfig.ItemHighlights.INSTANCE.rareEffectsHighlight) {
                    return new CustomColor(1f, 0f, 1f);
                }
                if (lore.contains(TextFormatting.WHITE + "Common") && ItemsConfig.ItemHighlights.INSTANCE.commonEffectsHighlight) {
                    return new CustomColor(1f, 1f, 1f);
                }
                if (lore.contains(TextFormatting.DARK_RED + " Black Market") && ItemsConfig.ItemHighlights.INSTANCE.blackMarketEffectsHighlight) {
                    return new CustomColor(0f, 0f, 0f);
                }
            }
        }
        if (lore.contains(ItemTier.NORMAL.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.normalHighlight) {
            return ItemTier.NORMAL.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.UNIQUE.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.uniqueHighlight) {
            return ItemTier.UNIQUE.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.RARE.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.rareHighlight) {
            return ItemTier.RARE.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.SET.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.setHighlight) {
            return ItemTier.SET.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.LEGENDARY.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.legendaryHighlight) {
            return ItemTier.LEGENDARY.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.FABLED.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.fabledHighlight) {
            return ItemTier.FABLED.getCustomizedHighlightColor();
        }
        if (lore.contains(ItemTier.MYTHIC.asFormattedName()) && ItemsConfig.ItemHighlights.INSTANCE.mythicHighlight) {
            return ItemTier.MYTHIC.getCustomizedHighlightColor();
        }
        if (name.matches("^(" + TextFormatting.DARK_AQUA + ".*%.*)$")) {
            return ItemTier.CRAFTED.getCustomizedHighlightColor();
        }
        if (ItemsConfig.ItemHighlights.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
            if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫" + TextFormatting.DARK_GRAY + "✫✫" + TextFormatting.GOLD + "]")) {
                return ItemsConfig.ItemHighlights.INSTANCE.ingredientOneHighlightColor;
            }
            if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.GOLD + "]") ||
                    name.endsWith(TextFormatting.DARK_PURPLE + " [" + TextFormatting.LIGHT_PURPLE + "✫✫" + TextFormatting.DARK_GRAY + "✫" + TextFormatting.DARK_PURPLE + "]")) {
                return ItemsConfig.ItemHighlights.INSTANCE.ingredientTwoHighlightColor;
            }
            if (name.endsWith(TextFormatting.GOLD + " [" + TextFormatting.YELLOW + "✫✫✫" + TextFormatting.GOLD + "]") ||
                    name.endsWith(TextFormatting.DARK_AQUA + " [" + TextFormatting.AQUA + "✫✫✫" + TextFormatting.DARK_AQUA + "]")) {
                return ItemsConfig.ItemHighlights.INSTANCE.ingredientThreeHighlightColor;
            }
        }
        if (isPowder(is)) {
            if (ItemsConfig.ItemHighlights.INSTANCE.minPowderTier == 0 || getPowderTier(is) < ItemsConfig.ItemHighlights.INSTANCE.minPowderTier) {
                return null;
            } else {
                return getPowderColor(is);
            }
        }
        return null;
    }

    private static float getDurability(String lore) {
    	Matcher m = DURABILITY_PATTERN.matcher(lore);
    	if(m.find()) {
    		return Float.parseFloat(m.group(1)) / Float.parseFloat(m.group(2));
    	}
    	return -1;

    }

    private static void drawDurabilityArc(GuiContainer guiContainer, Slot s, float durability){
    	if (!ItemsConfig.ItemHighlights.INSTANCE.craftedDurabilityBars) return;
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
        int radius = (ItemsConfig.ItemHighlights.INSTANCE.itemLevelArc) ? 7 : 8;
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
        if (!ItemsConfig.ItemHighlights.INSTANCE.itemLevelArc) return;
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
            color(colour.r, colour.g, colour.b, ItemsConfig.ItemHighlights.INSTANCE.inventoryAlpha / 100);
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
        String name = is.getDisplayName();
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
