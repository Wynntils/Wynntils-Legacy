/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.EmeraldSymbols;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

import static net.minecraft.client.renderer.GlStateManager.color;
import static net.minecraft.client.renderer.GlStateManager.disableLighting;

public class EmeraldCountOverlay implements Listener {

    private static final CustomColor textColor = new CustomColor(77f / 255f, 77f / 255f, 77f / 255f, 1);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld || !UtilitiesConfig.Items.INSTANCE.emeraldCountInventory) return;

        drawMoneyAmount(170, 7, PlayerInfo.getPlayerInfo().getMoney(), new ScreenRenderer(), textColor);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if(!Reference.onWorld) return;

        IInventory lowerInv = e.getGuiInventory().getLowerInv();
        if (lowerInv.getName().contains("Quests") || lowerInv.getName().contains("points")) return;

        IInventory upperInv = e.getGuiInventory().getUpperInv();

        ScreenRenderer renderer = new ScreenRenderer();
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountInventory)
            drawMoneyAmount(170, -10, Utils.countMoney(lowerInv), renderer, CommonColors.WHITE);
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountChest)
            drawMoneyAmount(170, 2 * (lowerInv.getSizeInventory() + 10), Utils.countMoney(upperInv), renderer, textColor);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.HorseOverlap.DrawGuiContainerForegroundLayer e) {
        if(!Reference.onWorld) return;

        IInventory lowerInv = e.getGuiInventory().getLowerInv();
        IInventory upperInv = e.getGuiInventory().getUpperInv();

        ScreenRenderer renderer = new ScreenRenderer();
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountInventory)
            drawMoneyAmount(190, -10, Utils.countMoney(lowerInv), renderer, CommonColors.WHITE);
        if (UtilitiesConfig.Items.INSTANCE.emeraldCountChest)
            drawMoneyAmount(190, 2 * (lowerInv.getSizeInventory() + 10), Utils.countMoney(upperInv), renderer, textColor);
    }

    /**
     * Renders the money amount on the specified x and y
     *
     * @param x the X position in the cartesian plane
     * @param y the Y position in the cartesian plane
     * @param moneyAmount the money amount
     * @param renderer the renderer
     */
    private static void drawMoneyAmount(int x, int y, int moneyAmount, ScreenRenderer renderer, CustomColor color) {
        //rendering setup
        disableLighting();
        color(1F, 1F, 1F, 1F);

        DecimalFormat format = ItemIdentificationOverlay.decimalFormat;

        //generating text
        String moneyText = "";
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) { //plain text
            moneyText = format.format(moneyAmount) + EmeraldSymbols.EMERALDS;
        }else{ //sliced text
            int[] moneySlices = calculateMoneyAmount(moneyAmount);

            moneyText += format.format(moneySlices[2]) + EmeraldSymbols.LE + " "; //liquid emeralds
            moneyText += format.format(moneySlices[1]) + EmeraldSymbols.BLOCKS + " "; //emerald blocks
            moneyText += format.format(moneySlices[0]) + EmeraldSymbols.EMERALDS; //emeralds
        }

        //rendering
        renderer.beginGL(x, y); {
            renderer.drawString(moneyText, 0, 0, color, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
        } renderer.endGL();
    }

    /**
     * Calculates the amount of emeralds, emerald blocks and liquid emeralds in the player inventory
     *
     * @param money the amount of money to proccess
     * @return an array with the values in the respective order of emeralds[0], emerald blocks[1], liquid emeralds[2]
     */
    private static int[] calculateMoneyAmount(int money) {
        return new int[] { money % 64, (money / 64) % 64, (money / 4096) % 4096 };
    }

}
