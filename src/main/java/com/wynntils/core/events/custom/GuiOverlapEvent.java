/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.core.overlays.inventories.HorseReplacer;
import com.wynntils.modules.core.overlays.inventories.IngameMenuReplacer;
import com.wynntils.modules.core.overlays.inventories.InventoryReplacer;
import com.wynntils.modules.core.overlays.ui.PlayerInfoReplacer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class GuiOverlapEvent<T extends Gui> extends Event {

    protected T gui;

    protected GuiOverlapEvent(T gui) {
        this.gui = gui;
    }

    public T getGui() {
        return gui;
    }

    public static class GuiScreenOverlapEvent<T extends GuiScreen> extends GuiOverlapEvent<T> {

        protected GuiScreenOverlapEvent(T guiScreen) {
            super(guiScreen);
        }

        public List<GuiButton> getButtonList() {
            return (List<GuiButton>) ReflectionFields.GuiScreen_buttonList.getValue(getGui());
        }

    }

    public static class InventoryOverlap extends GuiScreenOverlapEvent<InventoryReplacer> {

        public InventoryOverlap(InventoryReplacer guiInventory) {
            super(guiInventory);
        }

        @Override
        public List<GuiButton> getButtonList() {
            return getGui().getButtonList();
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

                this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable() {
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

        public static class DrawGuiContainerBackgroundLayer extends InventoryOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerBackgroundLayer(InventoryReplacer guiInventory, int mouseX, int mouseY) {
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

        public static class HoveredToolTip extends InventoryOverlap {

            int x, y;

            public HoveredToolTip(InventoryReplacer guiInventory, int x, int y) {
                super(guiInventory);

                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            public static class Pre extends HoveredToolTip {

                public Pre(InventoryReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }
            }

            public static class Post extends HoveredToolTip {

                public Post(InventoryReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }
            }

        }
    }

    public static class ChestOverlap extends GuiScreenOverlapEvent<ChestReplacer> {

        public ChestOverlap(ChestReplacer guiInventory) {
            super(guiInventory);
        }

        @Override
        public List<GuiButton> getButtonList() {
            return getGui().getButtonList();
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

                this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable() {
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

        public static class DrawGuiContainerBackgroundLayer extends ChestOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerBackgroundLayer(ChestReplacer guiChest, int mouseX, int mouseY) {
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

            @Override
            public List<GuiButton> getButtonList() {
                return buttonList;
            }

        }

        public static class HoveredToolTip extends ChestOverlap {

            int x, y;

            public HoveredToolTip(ChestReplacer guiInventory, int x, int y) {
                super(guiInventory);

                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            public static class Pre extends HoveredToolTip {

                public Pre(ChestReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }
            }

            public static class Post extends HoveredToolTip {

                public Post(ChestReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }
            }

        }

    }

    public static class HorseOverlap extends GuiScreenOverlapEvent<HorseReplacer> {

        public HorseOverlap(HorseReplacer guiHorse) {
            super(guiHorse);
        }

        @Override
        public List<GuiButton> getButtonList() {
            return getGui().getButtonList();
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

                this.slotIn = slotIn; this.slotId = slotId; this.mouseButton = mouseButton; this.type = type;
            }

            public boolean isCancelable() {
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

        public static class DrawGuiContainerBackgroundLayer extends HorseOverlap {

            int mouseX, mouseY;

            public DrawGuiContainerBackgroundLayer(HorseReplacer guiHorse, int mouseX, int mouseY) {
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

        public static class HoveredToolTip extends HorseOverlap {

            int x, y;

            public HoveredToolTip(HorseReplacer guiInventory, int x, int y) {
                super(guiInventory);

                this.x = x;
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            public static class Pre extends HoveredToolTip {

                public Pre(HorseReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }

                @Override
                public boolean isCancelable() {
                    return true;
                }

            }

            public static class Post extends HoveredToolTip {

                public Post(HorseReplacer guiInventory, int x, int y) {
                    super(guiInventory, x, y);
                }
            }

        }

    }

    public static class IngameMenuOverlap extends GuiScreenOverlapEvent<IngameMenuReplacer> {

        public IngameMenuOverlap(IngameMenuReplacer ingameMenuReplacer) {
            super(ingameMenuReplacer);
        }

        @Override
        public List<GuiButton> getButtonList() {
            return getGui().getButtonList();
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

            @Override
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

    public static class PlayerInfoOverlap extends GuiOverlapEvent<PlayerInfoReplacer> {

        public PlayerInfoOverlap(PlayerInfoReplacer replacer) {
            super(replacer);
        }

        public static class RenderList extends PlayerInfoOverlap {

            public RenderList(PlayerInfoReplacer replacer) {
                super(replacer);
            }

            public boolean isCancelable() {
                return true;
            }

        }

    }
}
