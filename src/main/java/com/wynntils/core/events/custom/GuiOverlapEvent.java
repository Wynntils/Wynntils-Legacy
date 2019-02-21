/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.core.overlays.inventories.HorseReplacer;
import com.wynntils.modules.core.overlays.inventories.IngameMenuReplacer;
import com.wynntils.modules.core.overlays.inventories.InventoryReplacer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class GuiOverlapEvent extends Event {

    public static class InventoryOverlap extends GuiOverlapEvent {

        InventoryReplacer guiInventory;

        public InventoryOverlap(InventoryReplacer guiInventory) {
            this.guiInventory = guiInventory;
        }

        public InventoryReplacer getGuiInventory() {
            return guiInventory;
        }

        public static class DrawScreen extends InventoryOverlap {

            int mouseX, mouseY; float partialTicks;

            public DrawScreen(InventoryReplacer guiInventory, int mouseX, int mouseY, float partialTicks) {
                super(guiInventory);

                this.mouseX = mouseX; this.mouseY = mouseY; this.partialTicks = partialTicks;
            }

            public float getPartialTicks() {
                return partialTicks;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseY() {
                return mouseY;
            }

        }

        public static class HandleMouseClick extends InventoryOverlap {

            Slot slotIn; int slotId, mouseButton; ClickType type;

            public HandleMouseClick(InventoryReplacer guiInventory, Slot slotIn, int slotId, int mouseButton, ClickType type)  {
                super(guiInventory);

                this.slotId = slotId; this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable()
            {
                return true;
            }

            public ClickType getType() {
                return type;
            }

            public int getMouseButton() {
                return mouseButton;
            }

            public int getSlotId() {
                return slotId;
            }

            public Slot getSlotIn() {
                return slotIn;
            }
        }

        public static class DrawGuiContainerForegroundLayer extends InventoryOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerForegroundLayer(InventoryReplacer guiInventory, int mouseX, int mouseY) {
                super(guiInventory);

                this.mouseX = mouseX; this.mouseY = mouseY;
            }

            public int getMouseY() {
                return mouseY;
            }

            public int getMouseX() {
                return mouseX;
            }
        }

        public static class KeyTyped extends InventoryOverlap {

            char typedChar; int keyCode;

            public KeyTyped(InventoryReplacer guiInventory, char typedChar, int keyCode) {
                super(guiInventory);

                this.typedChar = typedChar;
                this.keyCode = keyCode;
            }

            public char getTypedChar() {
                return typedChar;
            }

            public int getKeyCode() {
                return keyCode;
            }

            public boolean isCancelable() {
                return true;
            }

        }

    }

    public static class ChestOverlap extends GuiOverlapEvent {

        ChestReplacer guiChest;

        public ChestOverlap(ChestReplacer guiInventory) {
            this.guiChest = guiInventory;
        }

        public ChestReplacer getGuiInventory() {
            return guiChest;
        }

        public static class DrawScreen extends ChestOverlap {

            int mouseX, mouseY; float partialTicks;

            public DrawScreen(ChestReplacer guiChest, int mouseX, int mouseY, float partialTicks) {
                super(guiChest);

                this.mouseX = mouseX; this.mouseY = mouseY; this.partialTicks = partialTicks;
            }

            public float getPartialTicks() {
                return partialTicks;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseY() {
                return mouseY;
            }

        }

        public static class HandleMouseClick extends ChestOverlap {

            Slot slotIn; int slotId, mouseButton; ClickType type;

            public HandleMouseClick(ChestReplacer guiChest, Slot slotIn, int slotId, int mouseButton, ClickType type)  {
                super(guiChest);

                this.slotId = slotId; this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable()
            {
                return true;
            }

            public ClickType getType() {
                return type;
            }

            public int getMouseButton() {
                return mouseButton;
            }

            public int getSlotId() {
                return slotId;
            }

            public Slot getSlotIn() {
                return slotIn;
            }
        }

        public static class DrawGuiContainerForegroundLayer extends ChestOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerForegroundLayer(ChestReplacer guiChest, int mouseX, int mouseY) {
                super(guiChest);

                this.mouseX = mouseX; this.mouseY = mouseY;
            }

            public int getMouseY() {
                return mouseY;
            }

            public int getMouseX() {
                return mouseX;
            }
        }

        public static class KeyTyped extends ChestOverlap {

            char typedChar; int keyCode;

            public KeyTyped(ChestReplacer guiChest, char typedChar, int keyCode) {
                super(guiChest);

                this.typedChar = typedChar;
                this.keyCode = keyCode;
            }

            public char getTypedChar() {
                return typedChar;
            }

            public int getKeyCode() {
                return keyCode;
            }

            public boolean isCancelable() {
                return true;
            }

        }

        public static class MouseClicked extends ChestOverlap {

            int mouseX, mouseY, mouseButton;

            public MouseClicked(ChestReplacer guiChest, int mouseX, int mouseY, int mouseButton) {
                super(guiChest);

                this.mouseX = mouseX; this.mouseY = mouseY; this.mouseButton = mouseButton;
            }

            public int getMouseY() {
                return mouseY;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseButton() {
                return mouseButton;
            }

        }

        public static class InitGui extends ChestOverlap {

            List<GuiButton> buttonList;

            public InitGui(ChestReplacer guiChest, List<GuiButton> buttonList) {
                super(guiChest);
                this.buttonList = buttonList;
            }

        }

    }

    public static class HorseOverlap extends GuiOverlapEvent {

        HorseReplacer guiHorse;

        public HorseOverlap(HorseReplacer guiHorse) {
            this.guiHorse = guiHorse;
        }

        public HorseReplacer getGuiInventory() {
            return guiHorse;
        }

        public static class DrawScreen extends HorseOverlap {

            int mouseX, mouseY; float partialTicks;

            public DrawScreen(HorseReplacer guiHorse, int mouseX, int mouseY, float partialTicks) {
                super(guiHorse);

                this.mouseX = mouseX; this.mouseY = mouseY; this.partialTicks = partialTicks;
            }

            public float getPartialTicks() {
                return partialTicks;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseY() {
                return mouseY;
            }

        }

        public static class HandleMouseClick extends HorseOverlap {

            Slot slotIn; int slotId, mouseButton; ClickType type;

            public HandleMouseClick(HorseReplacer guiHorse, Slot slotIn, int slotId, int mouseButton, ClickType type)  {
                super(guiHorse);

                this.slotId = slotId; this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable()
            {
                return true;
            }

            public ClickType getType() {
                return type;
            }

            public int getMouseButton() {
                return mouseButton;
            }

            public int getSlotId() {
                return slotId;
            }

            public Slot getSlotIn() {
                return slotIn;
            }
        }

        public static class DrawGuiContainerForegroundLayer extends HorseOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerForegroundLayer(HorseReplacer guiHorse, int mouseX, int mouseY) {
                super(guiHorse);

                this.mouseX = mouseX; this.mouseY = mouseY;
            }

            public int getMouseY() {
                return mouseY;
            }

            public int getMouseX() {
                return mouseX;
            }
        }

        public static class KeyTyped extends HorseOverlap {

            char typedChar; int keyCode;

            public KeyTyped(HorseReplacer guiHorse, char typedChar, int keyCode) {
                super(guiHorse);

                this.typedChar = typedChar;
                this.keyCode = keyCode;
            }

            public char getTypedChar() {
                return typedChar;
            }

            public int getKeyCode() {
                return keyCode;
            }

            public boolean isCancelable() {
                return true;
            }
        }

    }

    public static class IngameMenuOverlap extends GuiOverlapEvent {

        IngameMenuReplacer ingameMenuReplacer;

        public IngameMenuOverlap(IngameMenuReplacer ingameMenuReplacer) {
            this.ingameMenuReplacer = ingameMenuReplacer;
        }

        public IngameMenuReplacer getGui() {
            return ingameMenuReplacer;
        }

        public static class DrawScreen extends IngameMenuOverlap {

            int mouseX, mouseY; float partialTicks;

            public DrawScreen(IngameMenuReplacer ingameMenuReplacer, int mouseX, int mouseY, float partialTicks) {
                super(ingameMenuReplacer);

                this.mouseX = mouseX; this.mouseY = mouseY; this.partialTicks = partialTicks;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseY() {
                return mouseY;
            }

            public float getPartialTicks() {
                return partialTicks;
            }
        }

        public static class MouseClicked extends IngameMenuOverlap {

            int mouseX, mouseY, mouseButton;

            public MouseClicked(IngameMenuReplacer ingameMenuReplacer, int mouseX, int mouseY, int mouseButton) {
                super(ingameMenuReplacer);

                this.mouseX = mouseX; this.mouseY = mouseY; this.mouseButton = mouseButton;
            }

            public int getMouseY() {
                return mouseY;
            }

            public int getMouseX() {
                return mouseX;
            }

            public int getMouseButton() {
                return mouseButton;
            }

        }

        public static class InitGui extends IngameMenuOverlap {

            List<GuiButton> buttonList;

            public InitGui(IngameMenuReplacer ingameMenuReplacer, List<GuiButton> buttonList) {
                super(ingameMenuReplacer);

                this.buttonList = buttonList;
            }

            public List<GuiButton> getButtonList() {
                return buttonList;
            }

        }

        public static class ActionPerformed extends IngameMenuOverlap {

            GuiButton button;

            public ActionPerformed(IngameMenuReplacer ingameMenuReplacer, GuiButton button) {
                super(ingameMenuReplacer);

                this.button = button;
            }

            public boolean isCancelable() {
                return true;
            }

            public GuiButton getButton() {
                return button;
            }

        }

    }

}
