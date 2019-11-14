/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.DailyReminderManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.modules.utilities.managers.MountHorseManager;
import com.wynntils.modules.utilities.managers.NametagManager;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.util.HashSet;
import java.util.List;

public class ClientEvents implements Listener {

    boolean isAfk = false;
    int lastPosition = 0;
    long lastMovement = 0;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if(!Reference.onWorld) return;

        DailyReminderManager.checkDailyReminder(ModCore.mc().player);

        if(!UtilitiesConfig.INSTANCE.blockAfkPushs) return;

        if(isAfk) Utils.createFakeScoreboard("Afk", Team.CollisionRule.NEVER);

        //Afk detection
        if(!Display.isActive()) { //by focus
            isAfk = true;
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;
        if(player == null) return;

        //by position
        int currentPosition = player.getPosition().getX() + player.getPosition().getY() + player.getPosition().getZ();
        if(lastPosition == currentPosition) {
            if(!isAfk && (System.currentTimeMillis() - lastMovement) >= 10000) isAfk = true;
        }else{
            lastMovement = System.currentTimeMillis();
            isAfk = false;
            Utils.removeFakeScoreboard("Afk");
        }

        lastPosition = currentPosition;
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent e) {
        if(!UtilitiesConfig.INSTANCE.disableFovChanges) return;

        e.setNewfov(1f + (e.getEntity().isSprinting() ? 0.15f : 0));
    }

    @SubscribeEvent
    public void onPreChatEvent(ChatEvent.Pre e) {
        if (UtilitiesConfig.INSTANCE.clickableTradeMessage) {
            if (e.getMessage().getUnformattedText().matches("\\w+ would like to trade! Type /trade \\w+ to accept\\.")) {
                e.setCanceled(true);
                String[] res = e.getMessage().getUnformattedText().split(" ");

                ITextComponent newMessage = new TextComponentString(res[0] + " would like to trade! To accept type ").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE))
                        .appendSibling(new TextComponentString("/trade " + res[0]).setStyle(new Style().setColor(TextFormatting.GOLD)))
                        .appendSibling(new TextComponentString(" or click this message!").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                newMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + res[0]));
                newMessage.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("/trade " + res[0]).setStyle(new Style().setColor(TextFormatting.GOLD))));

                Minecraft.getMinecraft().player.sendMessage(newMessage);
            }
        }
        if (UtilitiesConfig.INSTANCE.clickableDuelMessage) {
            if (e.getMessage().getUnformattedText().matches("\\w+ \\[Lv\\. \\d+] would like to duel! Type /duel \\w+ to accept\\.")) {
                e.setCanceled(true);
                String[] res = e.getMessage().getUnformattedText().split(" ");

                ITextComponent newMessage = new TextComponentString(res[0] + " [Lv. " + res[2] + " would like to duel! To accept type ").setStyle(new Style().setColor(TextFormatting.BLUE))
                        .appendSibling(new TextComponentString("/duel " + res[0]).setStyle(new Style().setColor(TextFormatting.GOLD)))
                        .appendSibling(new TextComponentString(" or click this message!").setStyle(new Style().setColor(TextFormatting.BLUE)));
                newMessage.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel " + res[0]));
                newMessage.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("/duel " + res[0]).setStyle(new Style().setColor(TextFormatting.GOLD))));

                Minecraft.getMinecraft().player.sendMessage(newMessage);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatHandler(ClientChatReceivedEvent e) {
        if(e.isCanceled() || e.getType() == ChatType.GAME_INFO) {
            return;
        }
        if(e.getMessage().getUnformattedText().startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
    }


    private static int lastHorseId = -1;
    @SuppressWarnings("unchecked")
    private static final DataParameter<String> nameKey = (DataParameter<String>) ReflectionFields.Entity_CUSTOM_NAME.getValue(Entity.class);

    @SubscribeEvent
    public void onHorseSpawn(PacketEvent<SPacketEntityMetadata> e) {
        if (!UtilitiesConfig.INSTANCE.autoMount || !Reference.onServer || !Reference.onWorld) return;

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
        String playerName = ModCore.mc().player.getName();

        assert nameKey != null;
        for (EntityDataManager.DataEntry<?> entry : e.getPacket().getDataManagerEntries()) {
            if (nameKey.equals(entry.getKey())) {
                Object value = entry.getValue();
                if (value instanceof String && MountHorseManager.isPlayersHorse((String) value, playerName)) {
                    lastHorseId = thisId;
                    MountHorseManager.mountHorseAndLogMessage();
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public void changeNametagColors(RenderLivingEvent.Specials.Pre e) {
        if(NametagManager.checkForNametags(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void inventoryOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        DailyReminderManager.openedDailyInventory(e);
    }

    //HeyZeer0: Handles the inventory lock, 7 methods below, first 6 on inventory, last one by dropping the item (without inventory)
    @SubscribeEvent
    public void keyPressOnInventory(GuiOverlapEvent.InventoryOverlap.KeyTyped e) {
        if(!Reference.onWorld) return;

        if(e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
                checkLockState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
            if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnChest(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if(!Reference.onWorld) return;

        if (UtilitiesConfig.INSTANCE.preventMythicChestClose) {
            if (e.getKeyCode() == 1 || e.getKeyCode() == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) {
                IInventory inv = e.getGuiInventory().getLowerInv();
                if (inv.getDisplayName().getUnformattedText().contains("Loot Chest")) {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        if(inv.getStackInSlot(i).hasDisplayName() && inv.getStackInSlot(i).getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString())) {
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

        if(e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
                checkLockState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
            if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnHorse(GuiOverlapEvent.HorseOverlap.KeyTyped e) {
        if(!Reference.onWorld) return;

        if(e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
                checkLockState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
            if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void clickOnInventory(GuiOverlapEvent.InventoryOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().inventory == Minecraft.getMinecraft().player.inventory) {
            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    private boolean bankPageConfirmed = false;

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            if (e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() >= 0 && e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() < 27) {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() + 9, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
            } else {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() - 27, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
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
                    List<String> lore = Utils.getLore(item);
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
                    ChestReplacer gui = e.getGuiInventory();
                    CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, e.getSlotId(), e.getMouseButton(), e.getType(), item, e.getGuiInventory().inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
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
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGuiInventory().getSlotUnderMouse() != null) {
            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPress(PacketEvent<CPacketPlayerDigging> e) {
        if ((e.getPacket().getAction() != Action.DROP_ITEM && e.getPacket().getAction() != Action.DROP_ALL_ITEMS) || !UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

        if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(Minecraft.getMinecraft().player.inventory.currentItem))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public void removePotionGui(RenderGameOverlayEvent.Pre e) {
        if (UtilitiesConfig.INSTANCE.hidePotionGui && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            e.setCanceled(true);
        }
    }

    private boolean checkDropState(int slot, int key) {
        if(!Reference.onWorld) return false;

        if(key == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()) {
            if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return false;

            return UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(slot);
        }
        return false;
    }

    private void checkLockState(int slot) {
        if(!Reference.onWorld) return;

        if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) {
            UtilitiesConfig.INSTANCE.locked_slots.put(PlayerInfo.getPlayerInfo().getClassId(), new HashSet<>());
        }

        if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(slot)) {
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).remove(slot);
        }else{
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).add(slot);
        }

        UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    //blocking healing pots below
    @SubscribeEvent
    public void onUseItem(PacketEvent<CPacketPlayerTryUseItem> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void onUseItemOnEntity(PacketEvent<CPacketUseEntity> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> {
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
        });
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        if(!e.getItemStack().hasDisplayName() || !e.getItemStack().getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;
        if(e.getEntityPlayer().getHealth() != e.getEntityPlayer().getMaxHealth()) return;

        e.setCanceled(true);
    }

}
