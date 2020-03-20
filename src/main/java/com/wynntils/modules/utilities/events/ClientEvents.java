/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.enums.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.SoundEffectsConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.*;
import com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.util.HashSet;
import java.util.List;

public class ClientEvents implements Listener {
    private static GuiScreen scheduledGuiScreen = null;
    private static boolean firstNullOccurred = false;

    boolean isAfk = false;
    int lastPosition = 0;
    long lastMovement = 0;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if (Reference.onServer) WindowIconManager.update();
        if (!Reference.onWorld) return;

        DailyReminderManager.checkDailyReminder(ModCore.mc().player);

        if (!UtilitiesConfig.INSTANCE.blockAfkPushs) return;

        if (isAfk) Utils.createFakeScoreboard("Afk", Team.CollisionRule.NEVER);

        // Afk detection
        if (!Display.isActive()) {  // by focus
            isAfk = true;
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        // by position
        int currentPosition = player.getPosition().getX() + player.getPosition().getY() + player.getPosition().getZ();
        if (lastPosition == currentPosition) {
            if (!isAfk && (System.currentTimeMillis() - lastMovement) >= 10000) isAfk = true;
        } else {
            lastMovement = System.currentTimeMillis();
            isAfk = false;
            Utils.removeFakeScoreboard("Afk");
        }

        lastPosition = currentPosition;
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent e) {
        if (!UtilitiesConfig.INSTANCE.disableFovChanges) return;

        e.setNewfov(1f + (e.getEntity().isSprinting() ? 0.15f : 0));
    }

    @SubscribeEvent
    public void onGUIClose(GuiOpenEvent e) {
        if (scheduledGuiScreen != null && e.getGui() == null && firstNullOccurred) {
            firstNullOccurred = false;
            e.setGui(scheduledGuiScreen);
            scheduledGuiScreen = null;
            return;
        }

        firstNullOccurred = scheduledGuiScreen != null && e.getGui() == null && !firstNullOccurred;
    }

    @SubscribeEvent
    public void onPostChatEvent(ChatEvent.Post e) {
        if (UtilitiesConfig.Market.INSTANCE.openChatMarket) {
            if (e.getMessage().getUnformattedText().matches("Type the (item name|amount you wish to (buy|sell)|price in emeralds) or type 'cancel' to cancel:")) {
                scheduledGuiScreen = new ChatGUI();
            }
        }
        if (UtilitiesConfig.INSTANCE.openChatBankSearch) {
            if (e.getMessage().getUnformattedText().matches("Please type an item name in chat!")) {
                scheduledGuiScreen = new ChatGUI();
            }
        }
    }

    @SubscribeEvent
    public void onMythicFound(PacketEvent<SPacketWindowItems> e) {
        if (!SoundEffectsConfig.INSTANCE.mythicFound) return;
        if (Minecraft.getMinecraft().currentScreen == null) return;
        if (!(Minecraft.getMinecraft().currentScreen instanceof ChestReplacer)) return;

        ChestReplacer chest = (ChestReplacer) Minecraft.getMinecraft().currentScreen;
        if (!chest.getLowerInv().getName().contains("Loot Chest")) return;

        for (int i = 0; i < 27; i++) {
            ItemStack stack = e.getPacket().getItemStacks().get(i);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue;
            if (!stack.getDisplayName().contains(TextFormatting.DARK_PURPLE.toString())) continue;
            if (!stack.getDisplayName().contains("Unidentified")) continue;

            Minecraft.getMinecraft().addScheduledTask(() -> WynntilsSound.MYTHIC_FOUND.play(1f, 1f));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatHandler(ClientChatReceivedEvent e) {
        if (e.isCanceled() || e.getType() == ChatType.GAME_INFO) {
            return;
        }
        if (e.getMessage().getUnformattedText().startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
    }

    private static int lastHorseId = -1;
    @SuppressWarnings("unchecked")
    private static final DataParameter<String> nameKey = (DataParameter<String>) ReflectionFields.Entity_CUSTOM_NAME.getValue(Entity.class);

    @SubscribeEvent
    public void onHorseSpawn(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onServer || !Reference.onWorld) return;

        int thisId = e.getPacket().getEntityId();
        if (thisId == lastHorseId || ModCore.mc().world == null) return;
        Entity entity = ModCore.mc().world.getEntityByID(thisId);

        if (!(entity instanceof AbstractHorse) || e.getPacket().getDataManagerEntries().isEmpty()) {
            return;
        }

        if (entity == ModCore.mc().player.getRidingEntity()) {
            lastHorseId = thisId;
            return;
        }

        EntityPlayerSP player = ModCore.mc().player;
        assert nameKey != null;
        for (EntityDataManager.DataEntry<?> entry : e.getPacket().getDataManagerEntries()) {
            if (!nameKey.equals(entry.getKey())) continue;
            Object value = entry.getValue();
            if (value == null || !MountHorseManager.isPlayersHorse((String)value, player.getName())) continue;

            lastHorseId = thisId;

            if (SoundEffectsConfig.INSTANCE.horseWhistle) WynntilsSound.HORSE_WHISTLE.play();

            if(!UtilitiesConfig.INSTANCE.autoMount) return;
            MountHorseManager.mountHorseAndLogMessage();
            return;
        }
    }

    @SubscribeEvent
    public void changeNametagColors(RenderLivingEvent.Specials.Pre e) {
        if (NametagManager.checkForNametags(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void inventoryOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        DailyReminderManager.openedDailyInventory(e);
    }

    // HeyZeer0: Handles the inventory lock, 7 methods below, first 6 on inventory, last one by dropping the item (without inventory)
    @SubscribeEvent
    public void keyPressOnInventory(GuiOverlapEvent.InventoryOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnChest(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (UtilitiesConfig.INSTANCE.preventMythicChestClose) {
            if (e.getKeyCode() == 1 || e.getKeyCode() == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) {
                IInventory inv = e.getGui().getLowerInv();
                if (inv.getDisplayName().getUnformattedText().contains("Loot Chest")) {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        if (inv.getStackInSlot(i).hasDisplayName() && inv.getStackInSlot(i).getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString())) {
                            TextComponentString text = new TextComponentString("You cannot close this loot chest while there is a mythic in it!");
                            text.getStyle().setColor(TextFormatting.RED);

                            Minecraft.getMinecraft().player.sendMessage(text);
                            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));
                            e.setCanceled(true);
                            break;
                        }
                    }
                }
                return;
            }
        }

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnHorse(GuiOverlapEvent.HorseOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void clickOnInventory(GuiOverlapEvent.InventoryOverlap.HandleMouseClick e) {
        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().inventory == Minecraft.getMinecraft().player.inventory) {
            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    private boolean bankPageConfirmed = false;

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            if (e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() >= 0 && e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() < 27) {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() + 9, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
            } else {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() - 27, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
            }
        }

        if (UtilitiesConfig.INSTANCE.addBankConfirmation && e.getSlotIn() != null) {
            IInventory inventory = e.getSlotIn().inventory;
            if (inventory.getDisplayName().getUnformattedText().contains("Bank") && e.getSlotIn().getHasStack()) {
                ItemStack item = e.getSlotIn().getStack();
                if (item.getDisplayName().contains(">" + TextFormatting.DARK_RED + ">" + TextFormatting.RED + ">" + TextFormatting.DARK_RED + ">" + TextFormatting.RED + ">")) {
                    String E = "\u00B2";
                    String B = "\u00BD";
                    String L = "\u00BC";
                    List<String> lore = ItemUtils.getLore(item);
                    String price = lore.get(4);
                    int actualPrice = Integer.parseInt(price.substring(20, price.indexOf(TextFormatting.GRAY + E)));
                    int le = (int) Math.floor((double) actualPrice) / 4096;
                    int eb = (int) Math.floor(((double) (actualPrice % 4096)) / 64);
                    int emeralds = actualPrice % 64;
                    String priceDisplay = "";
                    if (le != 0) {
                        priceDisplay += le + L + E + " ";
                    }
                    if (eb != 0) {
                        priceDisplay += eb + E + B + " ";
                    }
                    if (emeralds != 0) {
                        priceDisplay += emeralds + E + " ";
                    }
                    priceDisplay = priceDisplay.substring(0, priceDisplay.length() - 1);
                    String itemName = item.getDisplayName();
                    String pageNumber = itemName.substring(9, itemName.indexOf(TextFormatting.RED + " >"));
                    ChestReplacer gui = e.getGui();
                    CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, e.getSlotId(), e.getMouseButton(), e.getType(), item, e.getGui().inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
                    ModCore.mc().displayGuiScreen(new GuiYesNo((result, parentButtonID) -> {
                        ModCore.mc().displayGuiScreen(gui);
                        if (result) {
                            ModCore.mc().getConnection().sendPacket(packet);
                            bankPageConfirmed = true;
                        }
                    }, "Are you sure you want to purchase another bank page?", "Page number: " + pageNumber + "\nCost: " + priceDisplay, 0));
                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onSetSlot(PacketEvent<SPacketSetSlot> event) {
        if (bankPageConfirmed && event.getPacket().getSlot() == 8) {
            bankPageConfirmed = false;
            CPacketClickWindow packet = new CPacketClickWindow(ModCore.mc().player.openContainer.windowId, 8, 0, ClickType.PICKUP, event.getPacket().getStack(), ModCore.mc().player.openContainer.getNextTransactionID(ModCore.mc().player.inventory));
            ModCore.mc().getConnection().sendPacket(packet);
        }
    }

    @SubscribeEvent
    public void clickOnHorse(GuiOverlapEvent.HorseOverlap.HandleMouseClick e) {
        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null) {
            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    private boolean lastWasDrop = false;

    @SubscribeEvent
    public void keyPress(PacketEvent<CPacketPlayerDigging> e) {
        if ((e.getPacket().getAction() != Action.DROP_ITEM && e.getPacket().getAction() != Action.DROP_ALL_ITEMS) || !UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

        lastWasDrop = true;
        if (UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(Minecraft.getMinecraft().player.inventory.currentItem))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public void onConsumable(PacketEvent<SPacketSetSlot> e) {
        if (!Reference.onWorld || e.getPacket().getWindowId() != 0) return;

        // the reason of the +36, is because in the client the hotbar is handled between 0-8
        // the hotbar in the packet starts in 36, counting from up to down
        if (e.getPacket().getSlot() != Minecraft.getMinecraft().player.inventory.currentItem + 36) return;

        InventoryPlayer inventory = Minecraft.getMinecraft().player.inventory;
        ItemStack oldStack = inventory.getStackInSlot(e.getPacket().getSlot() - 36);
        ItemStack newStack = e.getPacket().getStack();

        if (lastWasDrop) {
            lastWasDrop = false;
            return;
        }

        if (oldStack.isEmpty() || !newStack.isEmpty() && !oldStack.isItemEqual(newStack)) return; // invalid move
        if (!oldStack.hasDisplayName()) return; // old item is not a valid item
        if (!newStack.isEmpty() && oldStack.getDisplayName().equalsIgnoreCase(newStack.getDisplayName())) return; // not consumed

        Minecraft.getMinecraft().addScheduledTask(() -> ConsumableTimerOverlay.addConsumable(oldStack));
    }

    @SubscribeEvent
    public void removePotionGui(RenderGameOverlayEvent.Pre e) {
        if (UtilitiesConfig.INSTANCE.hidePotionGui && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            e.setCanceled(true);
        }
    }

    private static boolean checkDropState(int slot, int key) {
        if (!Reference.onWorld) return false;

        if (key == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return false;

            return UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(slot);
        }
        return false;
    }

    private static void checkLockState(int slot) {
        if (!Reference.onWorld) return;

        if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) {
            UtilitiesConfig.INSTANCE.locked_slots.put(PlayerInfo.getPlayerInfo().getClassId(), new HashSet<>());
        }

        if (UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(slot)) {
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).remove(slot);
        } else {
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).add(slot);
        }

        UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    // blocking healing pots below
    @SubscribeEvent
    public void onUseItem(PacketEvent<CPacketPlayerTryUseItem> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void onUseItemOnEntity(PacketEvent<CPacketUseEntity> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (!e.getItemStack().hasDisplayName() || !e.getItemStack().getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;
        if (e.getEntityPlayer().getHealth() != e.getEntityPlayer().getMaxHealth()) return;

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        ConsumableTimerOverlay.clearConsumables(); // clear consumable list
    }

    // tooltip scroller
    @SubscribeEvent
    public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre e) {
        TooltipScrollManager.onGuiMouseInput(e.getGui());
    }

    @SubscribeEvent
    public void onBeforeDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre e) {
        TooltipScrollManager.onBeforeDrawScreen(e.getGui());
    }

    @SubscribeEvent
    public void onAfterDrawScreen(GuiScreenEvent.DrawScreenEvent.Post e) {
        TooltipScrollManager.onAfterDrawScreen(e.getGui());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBeforeTooltip(RenderTooltipEvent.Pre e) {
        TooltipScrollManager.dispatchTooltipEvent(e);
    }

    @SubscribeEvent
    public void onAfterTooltipRender(RenderTooltipEvent.PostText e) {
        TooltipScrollManager.dispatchTooltipEvent(e);
    }

    private static class FailsToLoadIfNoColorEvent implements Listener {
        @SubscribeEvent
        public void onBeforeTooltipRender(RenderTooltipEvent.Color e) {
            TooltipScrollManager.dispatchTooltipEvent(e);
        }
    }

    {
        try {
            UtilitiesModule.getModule().registerEvents(new FailsToLoadIfNoColorEvent());
        } catch (NoClassDefFoundError e) { /* ignore */ }
    }

}
