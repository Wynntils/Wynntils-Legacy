/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.overlays;

import java.util.HashMap;
import java.util.Map;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.instances.WindowedResolution;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.managers.CharacterReorderManager;
import com.wynntils.modules.visual.overlays.ui.CharacterSelectorUI;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OverlayEvents implements Listener {

    private CharacterSelectorUI fakeCharacterSelector;
    private int selectedSlot = -1;
    private boolean receivedItems = true;
    private Map<Integer, ItemStack> characterStacks = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initClassMenu(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!e.getGui().getLowerInv().getName().contains("Select a Class") && !e.getGui().getLowerInv().getName().contains("Reorder Classes")) return;
        
        receivedItems = false;
        
        // enable custom UI
        if (!VisualConfig.CharacterSelector.INSTANCE.enabled) return;
        if (CharacterReorderManager.isReordering()) return;
        
        WindowedResolution res = new WindowedResolution(480, 254);
        fakeCharacterSelector = new CharacterSelectorUI(null, e.getGui(), res.getScaleFactor());
        fakeCharacterSelector.setWorldAndResolution(Minecraft.getMinecraft(), e.getGui().width, e.getGui().height);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void closeCharacterMenu(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        if (!e.getGui().getLowerInv().getName().contains("Select a Class") && !e.getGui().getLowerInv().getName().contains("Reorder Classes")) return;

        CharacterReorderManager.stopReordering();
        selectedSlot = -1;
        fakeCharacterSelector = null;
        characterStacks.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceCharacterMenuDraw(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {  
        if (!e.getGui().getLowerInv().getName().contains("Select a Class") && !e.getGui().getLowerInv().getName().contains("Reorder Classes")) return;
        if (fakeCharacterSelector != null) {
            if (CharacterReorderManager.isReordering()) {
                fakeCharacterSelector = null;
            } else { 
                fakeCharacterSelector.drawScreen(e.getMouseX(), e.getMouseY(), e.getPartialTicks());
                e.setCanceled(true);
                return;
            }
        }
        
        // reorder icon
        ItemStack reorderStack;
        if (CharacterReorderManager.isReordering()) {
            reorderStack = new ItemStack(Blocks.BARRIER);
            reorderStack.setStackDisplayName(TextFormatting.GRAY + "" + TextFormatting.BOLD + "Finish reordering");
        } else {
            reorderStack = new ItemStack(Blocks.DIRT, 1, 1); // coarse dirt
            reorderStack.setStackDisplayName(TextFormatting.GRAY + "" + TextFormatting.BOLD + "Reorder Classes");
        }
        e.getGui().inventorySlots.getSlot(17).putStack(reorderStack);
        
        //set title
        if (CharacterReorderManager.isReordering()) {
            ((InventoryBasic) e.getGui().getLowerInv()).setCustomName(TextFormatting.BOLD + "Reorder Classes");
        } else {
            ((InventoryBasic) e.getGui().getLowerInv()).setCustomName(TextFormatting.BOLD + "Select a Class");
        }
        
        if (!receivedItems && e.getGui().inventorySlots.getSlot(8).getHasStack()) { // only populate stack list once items are present
            for (Slot s : e.getGui().inventorySlots.inventorySlots) {
                if (!s.getHasStack()) continue;
                if (!s.getStack().getDisplayName().contains("Select This Character") && !s.getStack().getDisplayName().contains("Deleting")) { // not a character stack
                    if (VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.containsKey(s.slotNumber)) { // character was swapped, is now deleted
                        CharacterReorderManager.removeSwappedSlot(s.slotNumber);
                        characterStacks.clear(); // cancel list population, must be redone
                        return;
                    }
                    continue; // don't add to list
                }
                int slot = s.slotNumber;
                if (VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.containsKey(s.slotNumber)) {
                    slot = VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.get(s.slotNumber);
                }
                characterStacks.put(slot, s.getStack());
            }
            receivedItems = true;
        }
        
        // fill character stacks from swapped list
        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            if (characterStacks.containsKey(s.slotNumber)) {
                s.putStack(characterStacks.get(s.slotNumber));
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void handleItemClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {  
        if (!e.getGui().getLowerInv().getName().contains("Select a Class") && !e.getGui().getLowerInv().getName().contains("Reorder Classes")) return;
        if (e.getSlotIn() == null || e.getSlotIn().getStack() == null) return;
        ItemStack is = e.getSlotIn().getStack();
        int slot = e.getSlotId();
        
        if (CharacterReorderManager.isReordering()) e.setCanceled(true); // don't let user click out while reordering
        
        // reorder toggle button
        if (slot == 17) {
            e.setCanceled(true);
            CharacterReorderManager.toggleReordering();
            if (!CharacterReorderManager.isReordering()) { // stopped reordering
                if (selectedSlot != -1) { // reset selected slot
                    characterStacks.get(selectedSlot).getEnchantmentTagList().removeTag(0);
                    selectedSlot = -1;
                }
                
                if (VisualConfig.CharacterSelector.INSTANCE.enabled) { // re-enable custom ui
                    WindowedResolution res = new WindowedResolution(480, 254);
                    fakeCharacterSelector = new CharacterSelectorUI(null, e.getGui(), res.getScaleFactor());
                    fakeCharacterSelector.setWorldAndResolution(Minecraft.getMinecraft(), e.getGui().width, e.getGui().height);
                }
            }
            return;
        }

        // character icons
        if (!is.getDisplayName().contains("Select This Character") && !is.getDisplayName().contains("Deleting")) return; // not a character icon
        if (CharacterReorderManager.isReordering()) {
            e.setCanceled(true);
            
            if (selectedSlot == -1) { // initial character
                characterStacks.get(slot).addEnchantment(Enchantment.getEnchantmentByID(34), 10);
                selectedSlot = slot;
            } else if (selectedSlot == slot) { // reset selection
                selectedSlot = -1;
                characterStacks.get(slot).getEnchantmentTagList().removeTag(0);
            } else { // swap characters
                CharacterReorderManager.reorder(slot, selectedSlot);
                // swap itemstacks
                ItemStack selected = e.getGui().getLowerInv().getStackInSlot(selectedSlot);
                characterStacks.get(selectedSlot).getEnchantmentTagList().removeTag(0);
                characterStacks.put(selectedSlot, e.getSlotIn().getStack());
                characterStacks.put(slot, selected);
                selectedSlot = -1;
            }
        } else {
            // redirect clicks
            if (VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.containsValue(slot)) {
                e.setCanceled(true);
                
                int swapSlot = CharacterReorderManager.getSwappedSlot(slot);
                if (swapSlot == -1) return;
                
                CPacketClickWindow packet = new CPacketClickWindow(e.getGui().inventorySlots.windowId, swapSlot, e.getMouseButton(), ClickType.PICKUP,
                        e.getGui().inventorySlots.getSlot(swapSlot).getStack(), e.getGui().inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
                ModCore.mc().getConnection().sendPacket(packet);
            }
        }
        
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceCharacterMenuClick(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        if (fakeCharacterSelector == null) return;

        fakeCharacterSelector.mouseClicked(e.getMouseX(), e.getMouseY(), e.getMouseButton());
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceMouseClickMove(GuiOverlapEvent.ChestOverlap.MouseClickMove e) {
        if (fakeCharacterSelector == null) return;

        fakeCharacterSelector.mouseClickMove(e.getMouseX(), e.getMouseY(), e.getClickedMouseButton(), e.getTimeSinceLastClick());
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void replaceMouseInput(GuiOverlapEvent.ChestOverlap.HandleMouseInput e) {
        if (fakeCharacterSelector == null) return;

        fakeCharacterSelector.handleMouseInput();
    }

    @SubscribeEvent
    public void replaceKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (fakeCharacterSelector == null) return;

        fakeCharacterSelector.keyTyped(e.getTypedChar(), e.getKeyCode());
        e.setCanceled(true);
    }

}
