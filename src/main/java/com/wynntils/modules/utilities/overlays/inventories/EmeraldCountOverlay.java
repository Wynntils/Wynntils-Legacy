/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.InventoryData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Texture;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;

public class EmeraldCountOverlay implements Listener {

    private static final CustomColor textColor = new CustomColor(77f / 255f, 77f / 255f, 77f / 255f, 1);

    private final ScreenRenderer renderer = new ScreenRenderer();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld || !UtilitiesConfig.Items.INSTANCE.emeraldCountInventory) return;

        if (UtilitiesConfig.Items.INSTANCE.emeraldCountText) {
            drawTextMoneyAmount(170, 7, PlayerInfo.get(InventoryData.class).getMoney(), renderer, textColor);
            return;
        }
        drawIconsMoneyAmount(178, 0, PlayerInfo.get(InventoryData.class).getMoney(), renderer);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld || !(UtilitiesConfig.Items.INSTANCE.emeraldCountInventory || UtilitiesConfig.Items.INSTANCE.emeraldCountChest)) return;

        IInventory lowerInv = e.getGui().getLowerInv();
        if (lowerInv.getName().contains("Quests") || lowerInv.getName().contains("points")) return;

        IInventory upperInv = e.getGui().getUpperInv();

        if (UtilitiesConfig.Items.INSTANCE.emeraldCountText) {
            if (UtilitiesConfig.Items.INSTANCE.emeraldCountInventory)
                drawTextMoneyAmount(170, -10, ItemUtils.countMoney(lowerInv), renderer, CommonColors.WHITE);
            if (UtilitiesConfig.Items.INSTANCE.emeraldCountChest)
                drawTextMoneyAmount(170, 2 * (lowerInv.getSizeInventory() + 10), ItemUtils.countMoney(upperInv), renderer, textColor);
            return;
        }
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountInventory)
            drawIconsMoneyAmount(178, 0, ItemUtils.countMoney(lowerInv), renderer);
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountChest)
            drawIconsMoneyAmount(178, 2 * (lowerInv.getSizeInventory() + 10), ItemUtils.countMoney(upperInv), renderer);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.HorseOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld || !UtilitiesConfig.Items.INSTANCE.emeraldCountInventory) return;

        IInventory lowerInv = e.getGui().getLowerInv();

        if (UtilitiesConfig.Items.INSTANCE.emeraldCountText) {
            drawTextMoneyAmount(170, 7, ItemUtils.countMoney(lowerInv), renderer, textColor);
            return;
        }

        drawIconsMoneyAmount(178, 0, ItemUtils.countMoney(lowerInv), renderer);
    }

    /**
     * Renders the money amount on the specified x and y as text
     *
     * @param x the X position in the cartesian plane
     * @param y the Y position in the cartesian plane
     * @param moneyAmount the money amount
     * @param renderer the renderer
     */
    private static void drawTextMoneyAmount(int x, int y, int moneyAmount, ScreenRenderer renderer, CustomColor color) {
        // rendering setup
        disableLighting();
        color(1F, 1F, 1F, 1F);

        // generating text
        String moneyText = "";
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {  // plain text
            moneyText = formatAmount(moneyAmount) + EmeraldSymbols.EMERALDS;
        } else {  // sliced text
            int[] moneySlices = calculateMoneyAmount(moneyAmount);

            moneyText += formatAmount(moneySlices[2]) + EmeraldSymbols.LE + " ";  // liquid emeralds
            moneyText += formatAmount(moneySlices[1]) + EmeraldSymbols.BLOCKS + " ";  // emerald blocks
            moneyText += formatAmount(moneySlices[0]) + EmeraldSymbols.EMERALDS;  // emeralds
        }

        // rendering
        ScreenRenderer.beginGL(x, y);
        {
            renderer.drawString(moneyText, 0, 0, color, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
        }
        ScreenRenderer.endGL();
    }

    private static final Texture inventoryTexture = new AssetsTexture(new ResourceLocation("textures/gui/container/inventory.png"), false);
    private static final Item EMERALD_BLOCK = Item.getItemFromBlock(Blocks.EMERALD_BLOCK);

    /**
     * Renders the money amount as 0 to 3 icons (LEs, blocks and emeralds) with numbers in potion effect boxes
     *
     * @param x x of first box
     * @param y y of first box
     * @param moneyAmount amount of money to render
     * @param renderer the renderer
     */
    private static void drawIconsMoneyAmount(int x, int y, int moneyAmount, ScreenRenderer renderer) {
        String emeraldAmount = null;
        String blocksAmount = null;
        String leAmount = null;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            // Alternative render: Amount after converting all to one type (Including fractional blocks / LE)
            emeraldAmount = formatAmount((double) moneyAmount);
            blocksAmount = formatAmount(moneyAmount / 64D);
            leAmount = formatAmount(moneyAmount / (64 * 64D));
        } else {
            int[] amounts = calculateMoneyAmount(moneyAmount);
            if (amounts[0] != 0) {
                emeraldAmount = formatAmount(amounts[0]);
            }
            if (amounts[1] != 0) {
                blocksAmount = formatAmount(amounts[1]);
            }
            if (amounts[2] != 0) {
                leAmount = formatAmount(amounts[2]);
            }
        }

        if (emeraldAmount == null && blocksAmount == null && leAmount == null) return;

        if (!inventoryTexture.loaded) inventoryTexture.load();

        ScreenRenderer.beginGL(0, 0);
        {
            if (leAmount != null) {
                drawOneIcon(Items.EXPERIENCE_BOTTLE, x, y, leAmount, renderer);
                y += 24;
            }
            if (blocksAmount != null) {
                drawOneIcon(EMERALD_BLOCK, x, y, blocksAmount, renderer);
                y += 24;
            }
            if (emeraldAmount != null) {
                drawOneIcon(Items.EMERALD, x, y, emeraldAmount, renderer);
            }
        }
        ScreenRenderer.endGL();
    }

    private static void drawOneIcon(Item i, int x, int y, String text, ScreenRenderer renderer) {
        int textureHeightScale = (int) inventoryTexture.height / 256;
        int textureWidthScale = (int) inventoryTexture.width / 256;
        renderer.drawRect(inventoryTexture, x, y, x + 24, y + 24, textureWidthScale * 141, textureHeightScale * 190, textureWidthScale * 165, textureHeightScale * 166);

        int textWidth = ScreenRenderer.fontRenderer.getStringWidth(text);
        renderer.drawItemStack(new ItemStack(i), x + 4, y + 4, textWidth > 18 ? "" : text);
        if (textWidth <= 18) return;
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x + 4 + 17, y + 4 + 18, 0);
            GlStateManager.scale(18f / textWidth, 18f / textWidth, 1);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            ScreenRenderer.fontRenderer.drawStringWithShadow(text, -textWidth, -ScreenRenderer.fontRenderer.FONT_HEIGHT, 0xFFFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
        }
        GlStateManager.popMatrix();
    }

    private static String formatAmount(int value) {
        return ItemIdentificationOverlay.decimalFormat.format(value);
    }

    private static final String[] suffixes = { "", "k", "m", "b", "t" };  // kilo, million, billion, trillion (short scale)
    private static final DecimalFormat fractionalFormat = new DecimalFormat("#.#");

    /**
     * Format a value using decimal suffixes to 1 decimal place (To be displayed as count)
     *
     * E.g.:
     *     1.0 -> "1"
     *     1.23 -> "1.2"
     *     0.75 -> "0.8"
     *     0.74 -> null
     *     0 -> null
     *     100.0 -> "100"
     *     1000.0 -> "1k"
     *     800.0 -> "0.8k"
     *     749_000.0 -> "750k"
     *     750_000.0 -> "0.8m"
     *     1_000_000.0 -> "1m"
     *     1_200_000.0 -> "1.2m"
     */
    private static String formatAmount(double value) {
        if (value < 0.75) return null;

        int suffix = 0;
        while (suffix < suffixes.length && value >= 750) {
            value /= 1000;
            ++suffix;
        }

        return fractionalFormat.format(value) + suffixes[suffix];
    }

    /**
     * Calculates the amount of emeralds, emerald blocks and liquid emeralds in the player inventory
     *
     * @param money the amount of money to process
     * @return an array with the values in the respective order of emeralds[0], emerald blocks[1], liquid emeralds[2]
     */
    private static int[] calculateMoneyAmount(int money) {
        return new int[] { money % 64, (money / 64) % 64, money / 4096 };
    }

}
