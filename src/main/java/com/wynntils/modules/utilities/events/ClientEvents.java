/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionFields;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
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
    public void onHorseSpawn(PacketEvent.EntityMetadata e) {
        if (!UtilitiesConfig.INSTANCE.autoMount || !Reference.onServer || !Reference.onWorld) return;

        int thisId = e.getPacket().getEntityId();
        if (thisId == lastHorseId) return;
        Entity entity = ModCore.mc().world.getEntityByID(thisId);
        if (!(entity instanceof AbstractHorse) || e.getPacket().getDataManagerEntries().isEmpty()) {
            return;
        }
        if (entity == ModCore.mc().player.getRidingEntity()) {
            lastHorseId = thisId;
            return;
        }
        String horseName = MountHorseManager.getHorseNameForPlayer();
        assert nameKey != null;
        for (EntityDataManager.DataEntry<?> entry : e.getPacket().getDataManagerEntries()) {
            if (nameKey.equals(entry.getKey())) {
                if (horseName.equals(entry.getValue())) {
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

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            if (e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() >= 0 && e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() < 27) {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() + 9, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
            } else {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGuiInventory().getLowerInv().getSizeInventory() - 27, Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
            }
        }
    }

    @SubscribeEvent
    public void clickOnHorse(GuiOverlapEvent.HorseOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGuiInventory().getSlotUnderMouse() != null) {
            e.setCanceled(checkDropState(e.getGuiInventory().getSlotUnderMouse().getSlotIndex(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPress(PacketEvent.PlayerDropItemEvent e) {
        if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

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
    public void onUseItem(PacketEvent.PlayerUseItemEvent e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
    }

    @SubscribeEvent
    public void onUseItem(PacketEvent.PlayerUseItemOnBlockEvent e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
    }

    @SubscribeEvent
    public void onUseItem(PacketEvent.UseEntityEvent e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);
        if(!item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if(player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!");
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        if(!e.getItemStack().hasDisplayName() || !e.getItemStack().getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;
        if(e.getEntityPlayer().getHealth() != e.getEntityPlayer().getMaxHealth()) return;

        e.setCanceled(true);
    }

}
