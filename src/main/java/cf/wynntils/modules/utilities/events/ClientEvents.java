package cf.wynntils.modules.utilities.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.managers.DailyReminderManager;
import cf.wynntils.modules.utilities.managers.KeyManager;
import cf.wynntils.modules.utilities.managers.NametagManager;
import cf.wynntils.modules.utilities.managers.TPSManager;
import cf.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.util.HashSet;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ClientEvents implements Listener {

    /* XP Gain Messages */
    public static int oldxp = 0;
    public static String oldxppercent = "0.0";
    public static int oldlevel = 0;
    public static int xpticks = 0;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if(!Reference.onWorld)
            return;
        TPSManager.updateTPS();
        DailyReminderManager.checkDailyReminder(ModCore.mc().player);

        if (OverlayConfig.GameUpdate.INSTANCE.enabled) {
            /* XP Gain Messages */
            if (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.enabled) {
                if (xpticks == 0) {
                    if (oldxp != PlayerInfo.getPlayerInfo().getCurrentXP()) {
                        if (!PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage().equals("")) {
                            if (oldxp < PlayerInfo.getPlayerInfo().getCurrentXP()) {
                                DecimalFormat df = new DecimalFormat("0.0");
                                float xpchange = Float.valueOf(PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage()) - Float.valueOf(oldxppercent);
                                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expMessageFormat
                                        .replace("%xo%", Integer.toString(oldxp))
                                        .replace("%xn%", Integer.toString(PlayerInfo.getPlayerInfo().getCurrentXP()))
                                        .replace("%xc%", Integer.toString(PlayerInfo.getPlayerInfo().getCurrentXP() - oldxp))
                                        .replace("%po%", oldxppercent)
                                        .replace("%pn%", PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage())
                                        .replace("%pc%", df.format(xpchange)));
                            }
                            oldxp = PlayerInfo.getPlayerInfo().getCurrentXP();
                            oldxppercent = PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage();
                        }
                    }
                    xpticks = (int) (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expUpdateRate * 20f);
                } else {
                    xpticks--;
                }
            }

            /* Levelup Messages */
            if (OverlayConfig.GameUpdate.LevelupMessages.INSTANCE.enabled) {
                if (oldlevel < PlayerInfo.getPlayerInfo().getLevel()) {
                    GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.LevelupMessages.INSTANCE.lvlupMessageFormat
                            .replace("%ol%", Integer.toString(oldlevel))
                            .replace("%nl%", Integer.toString(PlayerInfo.getPlayerInfo().getLevel())));
                    oldlevel = PlayerInfo.getPlayerInfo().getLevel();
                }
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

    @SubscribeEvent
    public void changeNametagColors(RenderLivingEvent.Specials.Pre e) {
        if(NametagManager.checkForNametag(e)) e.setCanceled(true);
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
                        if(inv.getStackInSlot(i).hasDisplayName() && inv.getStackInSlot(i).getDisplayName().startsWith("§5")) {
                            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cYou cannot close this loot chest while there is mythic in it!"));
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
    public void clickOnIventory(GuiOverlapEvent.InventoryOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            e.setCanceled(checkDropState(e.getSlotId(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            e.setCanceled(checkDropState(e.getSlotId(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void clickOnHorse(GuiOverlapEvent.HorseOverlap.HandleMouseClick e) {
        if(UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            e.setCanceled(checkDropState(e.getSlotId(), Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPress(PacketEvent.PlayerDropItemEvent e) {
        if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

        if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(Minecraft.getMinecraft().player.inventory.currentItem))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        GameUpdateOverlay.resetMessages();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatToRedirect(ClientChatReceivedEvent e) {
        if (!Reference.onWorld || !OverlayConfig.GameUpdate.INSTANCE.enabled || e.getType() == ChatType.GAME_INFO)
            return;
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectHorse) {
            if (e.getMessage().getUnformattedText().contains("There is no room for a horse.")) {
                GameUpdateOverlay.queueMessage("§4There is no room for a horse.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("Since you interacted with your inventory, your horse has despawned.")) {
                GameUpdateOverlay.queueMessage("§dHorse despawned.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCombat) {
            if (e.getMessage().getUnformattedText().contains("You don't have enough mana to do that spell!")) {
                GameUpdateOverlay.queueMessage("§4Not enough mana.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("You have not unlocked this spell!")) {
                GameUpdateOverlay.queueMessage("§4Spell not unlocked.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("Sorry, you can't teleport... Try moving away from blocks.")) {
                GameUpdateOverlay.queueMessage("§4Can't teleport - move away from blocks.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectOther) {
            if (e.getMessage().getUnformattedText().contains(" unused skill points! Click with your compass to use them!")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[3] + " §6skill points available.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains(" is now level ")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[0] + " §6is now level §e" + res[4]);
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("You must identify this item before using it.")) {
                GameUpdateOverlay.queueMessage("§4Item not identified.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectServer) {
            if (e.getMessage().getUnformattedText().contains("The server is restarting in ")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§4" + res[5] + " " + res[6].replace(".", "") + " until server restart");
                e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onWynnTerritoyChange(WynnTerritoryChangeEvent e) {
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enabled) {
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.leave && !e.getOldTerritory().equals("Waiting")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryLeaveFormat
                        .replace("%t%", e.getOldTerritory()));
            }
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enter && !e.getNewTerritory().equals("Waiting")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryEnterFormat
                        .replace("%t%", e.getNewTerritory()));
            }
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

}
