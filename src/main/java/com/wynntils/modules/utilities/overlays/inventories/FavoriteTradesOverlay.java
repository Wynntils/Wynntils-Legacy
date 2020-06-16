/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoriteTradesOverlay implements Listener {

    private final ArrayList<String> favorites_trade_items_lore = new ArrayList<>();
    private boolean opened_market;

    @SubscribeEvent
    public void keyPressOnTrade(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;
        if (e.getKeyCode() == KeyManager.getFavoriteTradeKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory != e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getStack());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (!Reference.onWorld) return;
        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            if (s.slotNumber >= e.getGui().getLowerInv().getSizeInventory()) continue;
            if (isNotMarketItem(s.getStack())) continue;
            renderFavoriteItem(s, e.getGui().getGuiLeft(), e.getGui().getGuiTop());
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onInventory(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;
        if (!Reference.onWorld) return;
        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        if (current instanceof ChestReplacer) {
            ChestReplacer gc = (ChestReplacer) current;
            if (gc.getLowerInv().getDisplayName().getFormattedText().contains("Marketplace")) {
                if (!opened_market) {
                    opened_market = true;
                }
            }
        } else if (opened_market) {
            opened_market = false;
            if (!favorites_trade_items_lore.isEmpty())
                favorites_trade_items_lore.clear();
        }
    }

    private boolean isNotMarketItem(ItemStack it) {
        List<String> lore = ItemUtils.getLore(it);
        if (lore.size() < 3) return true;
        char[] c = lore.get(2).toCharArray();
        for (char cc : c)
            if (cc == EmeraldSymbols.E)
                return false;
        return true;
    }

    private void renderFavoriteItem(Slot s, int guiLeft, int guiTop) {
        ItemStack it = s.getStack();
        ItemIdentificationOverlay.replaceLore(it);
        String lore = Arrays.toString(ItemUtils.getLore(it).toArray());
        if (!favorites_trade_items_lore.contains(lore)) return;

        ScreenRenderer.beginGL(0, 0);

        // HeyZeer0: this will make the lock appear over the item
        GlStateManager.translate(0, 0, 260);

        ScreenRenderer r = new ScreenRenderer();
        RenderHelper.disableStandardItemLighting();
        ScreenRenderer.scale(0.5f);
        r.drawRect(Textures.UIs.hud_overlays, (int)((guiLeft + s.xPos) / 0.5) + 20, (int)((guiTop + s.yPos) / 0.5) - 3, 51, 0, 17, 16);
        ScreenRenderer.endGL();
    }

    private void checkLockState(ItemStack it) {
        if (!Reference.onWorld) return;
        if (isNotMarketItem(it)) return;
        ItemIdentificationOverlay.replaceLore(it);
        String lore = Arrays.toString(ItemUtils.getLore(it).toArray());
        if (!favorites_trade_items_lore.remove(lore)) {
            favorites_trade_items_lore.add(lore);
        }
    }

}
