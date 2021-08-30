/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.core.overlays.inventories.HorseReplacer;
import com.wynntils.modules.core.overlays.inventories.InventoryReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.SoundEffectsConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.*;
import com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import com.wynntils.modules.utilities.overlays.ui.FakeGuiContainer;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientEvents implements Listener {

    private static GuiScreen scheduledGuiScreen = null;
    private static boolean firstNullOccurred = false;

    private static boolean afkProtectionEnabled = false;
    private static boolean afkProtectionActivated = false;
    private static boolean afkProtectionRequested = false;
    private static boolean afkProtectionBlocked = false;

    private static float lastHealth = 0;
    private static long lastUserInput = Long.MAX_VALUE;
    private static long lastAfkRequested = Long.MAX_VALUE;
    private int tickCounter;

    public static boolean isAwaitingHorseMount = false;
    private static int lastHorseId = -1;

    private static boolean priceInput = false;

    private static Pattern CRAFTED_USES = Pattern.compile(".* \\[(\\d)/\\d\\]");

    @SubscribeEvent
    public void onMoveEvent(InputEvent.MouseInputEvent e) {
        lastUserInput = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onKeyboardEvent(InputEvent.KeyInputEvent e) {
        long currentTime = System.currentTimeMillis();
        // Events triggered just after the user pressed the Toggle AFK Protection key
        // should be ignored
        if (currentTime <= lastAfkRequested + 500) return;

        lastUserInput = currentTime;
    }

    @SubscribeEvent
    public void onReady(ClientEvent.Ready e) {
        if (!UtilitiesConfig.INSTANCE.autoResourceOnLoad) return;

        ServerResourcePackManager.loadServerResourcePack();
    }

    @SubscribeEvent
    public void classDialog(GuiOverlapEvent.ChestOverlap.DrawGuiContainerBackgroundLayer e) {
        if (!e.getGui().getLowerInv().getName().contains("Select a Class")) return;
        if (!afkProtectionActivated) return;

        InventoryBasic inv = (InventoryBasic) e.getGui().getLowerInv();
        if (inv.getName().contains("AFK Protection activated")) return;

        inv.setCustomName("" + TextFormatting.DARK_RED + TextFormatting.BOLD + "AFK Protection activated");
    }

    @SubscribeEvent
    public void classChange(WynnClassChangeEvent e) {
        afkProtectionEnabled = false;
        afkProtectionActivated = false;

        lastHealth = McIf.player().getHealth();
        lastUserInput = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        // Only trigger four times a second
        if (e.phase == TickEvent.Phase.END || (tickCounter++ % 5) != 0) return;
        if (Reference.onServer) WindowIconManager.update();
        if (!Reference.onWorld) return;

        DailyReminderManager.checkDailyReminder(McIf.player());

        if (!UtilitiesConfig.AfkProtection.INSTANCE.afkProtection) return;

        if (afkProtectionRequested) {
            afkProtectionRequested = false;
            // Immediate AFK requested, fake that last activity was long ago
            lastUserInput = 0;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceActivity = currentTime - this.lastUserInput;

        if (UtilitiesConfig.AfkProtection.INSTANCE.afkProtection) {
            if (afkProtectionActivated) {
                lastUserInput = currentTime;
                afkProtectionEnabled = false;
                afkProtectionActivated = false;
                return;
            }
            long longAfkThresholdMillis = (long)(UtilitiesConfig.AfkProtection.INSTANCE.afkProtectionThreshold * 60 * 1000);
            if (!afkProtectionEnabled) {
                if (!afkProtectionBlocked && timeSinceActivity >= longAfkThresholdMillis) {
                    // Enable AFK protection (but not if we're in a chest/inventory GUI)
                    afkProtectionRequested = false;
                    lastHealth = McIf.player().getHealth();
                    if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectAfk) {
                        GameUpdateOverlay.queueMessage("AFK Protection enabled");
                    } else {
                        McIf.mc().addScheduledTask(() ->
                                ChatOverlay.getChat().printChatMessage(new TextComponentString(TextFormatting.GRAY + "AFK Protection enabled due to lack of movement")));
                    }
                    afkProtectionEnabled = true;
                }
            } else {
                float currentHealth = McIf.player().getHealth();
                if (currentHealth < (lastHealth  * UtilitiesConfig.AfkProtection.INSTANCE.healthPercentage / 100.0f)) {
                    // We're taking damage; activate AFK protection and go to class screen
                    afkProtectionActivated = true;
                    McIf.mc().addScheduledTask(() ->
                            ChatOverlay.getChat().printChatMessage(new TextComponentString(TextFormatting.GRAY + "AFK Protection activated due to player taking damage")));
                    McIf.player().sendChatMessage("/class");
                }
                if (timeSinceActivity < longAfkThresholdMillis) {
                    if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectAfk) {
                        GameUpdateOverlay.queueMessage("AFK Protection disabled");
                    } else {
                        McIf.mc().addScheduledTask(() ->
                                ChatOverlay.getChat().printChatMessage(new TextComponentString(TextFormatting.GRAY + "AFK Protection disabled")));
                    }
                    afkProtectionEnabled = false;
                }
            }
        }
    }

    public static boolean isAfkProtectionEnabled() {
        return afkProtectionEnabled;
    }

    public static void toggleAfkProtection() {
        if (!afkProtectionEnabled) {
            afkProtectionRequested = true;
            lastAfkRequested = System.currentTimeMillis();
        }
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent e) {
        switch (UtilitiesConfig.INSTANCE.fovScalingFunction) {
            case Arctangent:
                e.setNewfov(1f + (float) (Math.atan(2d * Math.PI * (e.getNewfov() - 1d)) / (2d * Math.PI)));
                break;
            case Sprint_Only:
                e.setNewfov(1f + (e.getEntity().isSprinting() ? 0.15f : 0));
                break;
            case None:
                e.setNewfov(1f);
                break;
        }
    }

    @SubscribeEvent
    public void onDamage(GameEvent.DamageEntity e) {
        e.getDamageTypes().forEach((k, v) -> AreaDPSManager.registerDamage(v));
    }

    @SubscribeEvent
    public void onGUIClose(GuiOpenEvent e) {
        if (e.getGui() == null) {
            afkProtectionBlocked = false;
            lastUserInput = System.currentTimeMillis();
        } else if (e.getGui() instanceof InventoryReplacer || e.getGui() instanceof ChestReplacer ||
                e.getGui() instanceof HorseReplacer) {
            afkProtectionBlocked = true;
        }
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
        if (McIf.getUnformattedText(e.getMessage()).matches("Type the price in emeralds or type 'cancel' to cancel:")) {
            priceInput = true;
            if (UtilitiesConfig.Market.INSTANCE.openChatMarket)
                scheduledGuiScreen = new ChatGUI();
        }

        if (UtilitiesConfig.Market.INSTANCE.openChatMarket) {
            if (McIf.getUnformattedText(e.getMessage()).matches("Type the (item name|amount you wish to (buy|sell)) or type 'cancel' to cancel:")) {
                scheduledGuiScreen = new ChatGUI();
            }
        }

        if (UtilitiesConfig.Bank.INSTANCE.openChatBankSearch) {
            if (McIf.getUnformattedText(e.getMessage()).matches("Please type an item name in chat!")) {
                scheduledGuiScreen = new ChatGUI();
            }
        }
    }

    @SubscribeEvent
    public void onSendMessage(ClientChatEvent e) {
        if (!priceInput) return;

        priceInput = false;
        long price = StringUtils.convertEmeraldPrice(e.getMessage());
        if (price != 0) // price of 0 means either garbage input or actual 0, can be ignored either way
            e.setMessage("" + price);
    }

    @SubscribeEvent
    public void onSlotSet(PacketEvent<SPacketSetSlot> e) {
        if (McIf.mc().currentScreen == null) return;
        if (!(McIf.mc().currentScreen instanceof FakeGuiContainer)) return;

        e.setCanceled(true); // stops wynncraft from adding pouch to gui
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatHandler(ClientChatReceivedEvent e) {
        if (e.isCanceled() || e.getType() == ChatType.GAME_INFO) return;

        String msg = McIf.getUnformattedText(e.getMessage());
        if (msg.startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
    }

    @SubscribeEvent
    public void onTitle(PacketEvent<SPacketTitle> e) {
        if (!OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectPouch) return;

        SPacketTitle packet = e.getPacket();
        if (packet.getType() != SPacketTitle.Type.SUBTITLE) return;
        if (!McIf.getUnformattedText(packet.getMessage()).matches("^§a\\+\\d+ §7.+§a to pouch$")) return;

        e.setCanceled(true);
        GameUpdateOverlay.queueMessage(McIf.getFormattedText(packet.getMessage()));
    }

    @SubscribeEvent
    public void onHorseSpawn(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onServer || !Reference.onWorld) return;

        int thisId = e.getPacket().getEntityId();
        if (thisId == lastHorseId || McIf.world() == null) return;
        Entity entity = McIf.world().getEntityByID(thisId);

        if (!(entity instanceof AbstractHorse) || e.getPacket().getDataManagerEntries().isEmpty()) {
            return;
        }

        if (entity == McIf.player().getRidingEntity()) {
            lastHorseId = thisId;
            return;
        }

        EntityPlayerSP player = McIf.player();
        String entityName = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (entityName == null ||  entityName.isEmpty() ||
                !MountHorseManager.isPlayersHorse(entityName, player.getName())) return;

        lastHorseId = thisId;

        if (SoundEffectsConfig.INSTANCE.horseWhistle) WynntilsSound.HORSE_WHISTLE.play();

        if (isAwaitingHorseMount) {
            MountHorseManager.retryMountHorseAndShowMessage();
            isAwaitingHorseMount = false;
            return;
        }

        if(!UtilitiesConfig.INSTANCE.autoMount) return;
        MountHorseManager.mountHorseAndLogMessage();
    }

    @SubscribeEvent
    public void changeNametagColors(RenderLivingEvent.Specials.Pre e) {
        if (NametagManager.checkForNametags(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void inventoryOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        DailyReminderManager.openedDailyInventory(e);
    }

    private int getSecondsUntilDailyReward() {
        return (int) ((UtilitiesConfig.Data.INSTANCE.lastOpenedDailyReward + 86400000 - System.currentTimeMillis()) / 1000);
    }

    private int getSecondsUntilDailyObjectiveReset() {
        LocalDateTime tomorrowMidnight = LocalDateTime.of(LocalDate.now(ZoneOffset.UTC), LocalTime.MIDNIGHT).plusDays(1).plusSeconds(1);
        return (int) Duration.between(LocalDateTime.now(ZoneOffset.UTC), tomorrowMidnight).getSeconds();
    }

    private String getFormattedTimeString(int secondsLeft) {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

        int hoursLeft = secondsLeft / 3600;
        int minutesLeft = secondsLeft % 3600 / 60;
        return String.format(TextFormatting.AQUA + date.plusSeconds(secondsLeft).format(formatter) + TextFormatting.GRAY
                + " (in " + TextFormatting.WHITE + "%02d:%02d" + TextFormatting.GRAY + ")", hoursLeft, minutesLeft);
    }

    @SubscribeEvent
    public void onDrawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        Slot slot = e.getGui().inventorySlots.getSlot(20);

        ItemStack stack = slot.getStack();
        if (stack.getItem() == Item.getItemFromBlock(Blocks.SNOW_LAYER) || stack.getItem() == Items.CLOCK) {
            // There's no chest, create a clock with timer as lore
            NBTTagCompound nbt = new NBTTagCompound();
            ItemStack newStack = new ItemStack(Items.CLOCK);
            NBTTagCompound display = nbt.getCompoundTag("display");
            display.setTag("Name", new NBTTagString("" + TextFormatting.GREEN + "Daily Reward Countdown"));
            nbt.setTag("display", display);
            newStack.setTagCompound(nbt);

            List<String> lore = new LinkedList<>();
            lore.add("");
            lore.add(TextFormatting.GOLD + "Daily Reward");
            if (getSecondsUntilDailyReward() < 0) {
                // We've missed last time the user opened the chest; reset timer
                UtilitiesConfig.Data.INSTANCE.lastOpenedDailyReward = 0;
            }
            if (UtilitiesConfig.Data.INSTANCE.lastOpenedDailyReward == 0) {
                lore.add(""+ TextFormatting.GRAY + TextFormatting.ITALIC + "Unknown renewal time");
            } else {
                lore.add(TextFormatting.GRAY + "Will renew " + getFormattedTimeString(getSecondsUntilDailyReward()));
            }

            ItemUtils.replaceLore(newStack, lore);
            slot.putStack(newStack);
            stack = newStack; // use this for next check
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST)  || stack.getItem() == Items.CLOCK) {
            // We need to strip the old time from the lore, if existent
            List<String> lore = ItemUtils.getLore(stack);
            List<String> newLore = new LinkedList<>();
            for (String line : lore) {
                if (line.contains("Daily Objective")) break;
                newLore.add(line);
            }
            int lastLine = newLore.size()-1;
            if (lastLine >= 0 && newLore.get(lastLine).isEmpty()) {
                newLore.remove(lastLine);
            }

            PlayerStatsProfile profile = WebManager.getPlayerProfile();
            PlayerStatsProfile.PlayerTag playerRank = profile != null ? profile.getTag() : PlayerStatsProfile.PlayerTag.NONE;

            newLore.add("");
            newLore.add(TextFormatting.GOLD + "Daily Objective");
            if (playerRank.isVip()) {
                newLore.add(TextFormatting.GOLD + "Daily Mob Totems");
            }
            if (playerRank.isVipPlus()) {
                newLore.add(TextFormatting.GOLD + "Daily Crate");
            }
            newLore.add(TextFormatting.GRAY + "Will renew " + getFormattedTimeString(getSecondsUntilDailyObjectiveReset()));

            if (!playerRank.isVip()) {
                newLore.add("");
                newLore.add(TextFormatting.GOLD + "Daily Mob Totems");
                newLore.add(""+ TextFormatting.GRAY + TextFormatting.ITALIC + "Purchase a rank at wynncraft.com");
                newLore.add(""+ TextFormatting.GRAY + TextFormatting.ITALIC + "for daily mob totems");
            }

            if (!playerRank.isVipPlus()) {
                newLore.add("");
                newLore.add(TextFormatting.GOLD + "Daily Crate");
                newLore.add(""+ TextFormatting.GRAY + TextFormatting.ITALIC + "Get VIP+ or Hero rank");
                newLore.add(""+ TextFormatting.GRAY + TextFormatting.ITALIC + "for daily crates");
            }

            ItemUtils.replaceLore(stack, newLore);
            slot.putStack(stack);
        }
    }

    // HeyZeer0: Handles the inventory lock, 7 methods below, first 6 on inventory, last one by dropping the item (without inventory)
    @SubscribeEvent
    public void keyPressOnInventory(GuiOverlapEvent.InventoryOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getKeyCode() == KeyManager.getItemScreenshotKey().getKeyBinding().getKeyCode()) {
            ItemScreenshotManager.takeScreenshot();
            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnChest(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (UtilitiesConfig.INSTANCE.preventMythicChestClose || UtilitiesConfig.INSTANCE.preventFavoritedChestClose) {
            if (e.getKeyCode() == 1 || e.getKeyCode() == McIf.mc().gameSettings.keyBindInventory.getKeyCode()) {
                IInventory inv = e.getGui().getLowerInv();
                if (McIf.getUnformattedText(inv.getDisplayName()).contains("Loot Chest") ||
                        McIf.getUnformattedText(inv.getDisplayName()).contains("Daily Rewards") ||
                        McIf.getUnformattedText(inv.getDisplayName()).contains("Objective Rewards")) {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        TextComponentString text;
                        if (UtilitiesConfig.INSTANCE.preventMythicChestClose && stack.hasDisplayName() &&
                                stack.getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString()) &&
                                ItemUtils.getStringLore(stack).toLowerCase().contains("mythic")) {
                            text = new TextComponentString("You cannot close this loot chest while there is a mythic in it!");
                        } else if (UtilitiesConfig.INSTANCE.preventFavoritedChestClose && stack.hasTagCompound() &&
                                stack.getTagCompound().getBoolean("wynntilsFavorite")) {
                            text = new TextComponentString("You cannot close this loot chest while there is a favorited item in it!");
                        } else {
                            continue;
                        }

                        text.getStyle().setColor(TextFormatting.RED);
                        McIf.player().sendMessage(text);
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));
                        e.setCanceled(true);
                        break;
                    }
                }
                return;
            }
        }

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getKeyCode() == KeyManager.getItemScreenshotKey().getKeyBinding().getKeyCode()) {
            ItemScreenshotManager.takeScreenshot();
            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    @SubscribeEvent
    public void keyPressOnHorse(GuiOverlapEvent.HorseOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
                checkLockState(e.getGui().getSlotUnderMouse().getSlotIndex());
            }

            return;
        }

        if (e.getKeyCode() == KeyManager.getItemScreenshotKey().getKeyBinding().getKeyCode()) {
            ItemScreenshotManager.takeScreenshot();
            return;
        }

        if (e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId())) return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), e.getKeyCode()));
        }
    }

    private static int accessoryDestinationSlot = -1;

    @SubscribeEvent
    public void clickOnInventory(GuiOverlapEvent.InventoryOverlap.HandleMouseClick e) {
        if(!Reference.onWorld) return;

        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().inventory == McIf.player().inventory) {
            if (checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), McIf.mc().gameSettings.keyBindDrop.getKeyCode())) {
                e.setCanceled(true);
                return;
            }
        }

        if (UtilitiesConfig.INSTANCE.shiftClickAccessories && e.getGui().isShiftKeyDown() && e.getGui().getSlotUnderMouse() != null && McIf.player().inventory.getItemStack().isEmpty() && e.getGui().getSlotUnderMouse().inventory == McIf.player().inventory) {
            if (e.getSlotId() >= 9 && e.getSlotId() <= 12) { // taking off accessory
                // check if hotbar has open slot; if so, no action required
                for (int i = 36; i < 45; i++) {
                    if (!e.getGui().inventorySlots.getSlot(i).getHasStack()) return;
                }

                // move accessory into inventory
                // find first open slot
                int openSlot = 0;
                for (int i = 14; i < 36; i++) {
                    if (!e.getGui().inventorySlots.getSlot(i).getHasStack()) {
                        openSlot = i;
                        break;
                    }
                }
                if (openSlot == 0) return; // no open slots, cannot move accessory anywhere
                accessoryDestinationSlot = openSlot;

                e.setCanceled(true);

            } else { // putting on accessory
                // verify it's an accessory
                ItemType item = ItemUtils.getItemType(e.getSlotIn().getStack());
                if (item != ItemType.RING && item != ItemType.BRACELET && item != ItemType.NECKLACE) return;

                // check if the appropriate slot is open (snow layer = empty)
                int openSlot = 0;
                switch (item) {
                    case RING:
                        if (e.getGui().inventorySlots.getSlot(9).getHasStack() && e.getGui().inventorySlots.getSlot(9).getStack().getItem().equals(Item.getItemFromBlock(Blocks.SNOW_LAYER)))
                            openSlot = 9; // first ring slot
                        else if (e.getGui().inventorySlots.getSlot(10).getHasStack() && e.getGui().inventorySlots.getSlot(10).getStack().getItem().equals(Item.getItemFromBlock(Blocks.SNOW_LAYER)))
                            openSlot = 10; // second ring slot
                        break;
                    case BRACELET:
                        if (e.getGui().inventorySlots.getSlot(11).getHasStack() && e.getGui().inventorySlots.getSlot(11).getStack().getItem().equals(Item.getItemFromBlock(Blocks.SNOW_LAYER)))
                            openSlot = 11; // bracelet slot
                        break;
                    case NECKLACE:
                        if (e.getGui().inventorySlots.getSlot(12).getHasStack() && e.getGui().inventorySlots.getSlot(12).getStack().getItem().equals(Item.getItemFromBlock(Blocks.SNOW_LAYER)))
                            openSlot = 12; // necklace slot
                        break;
                    default:
                        return;
                }
                if (openSlot == 0) return;
                accessoryDestinationSlot = openSlot;

                e.setCanceled(true); // only cancel after finding open slot
            }

            // pick up accessory
            CPacketClickWindow packet = new CPacketClickWindow(e.getGui().inventorySlots.windowId, e.getSlotId(), 0, ClickType.PICKUP, e.getSlotIn().getStack(), e.getGui().inventorySlots.getNextTransactionID(McIf.player().inventory));
            McIf.mc().getConnection().sendPacket(packet);
        }
    }

    @SubscribeEvent
    public void handleAccessoryMovement(TickEvent.ClientTickEvent e) {
        if (e.phase != Phase.END) return;
        if (!Reference.onWorld || accessoryDestinationSlot == -1) return;

        // inventory was closed
        if (!(McIf.mc().currentScreen instanceof InventoryReplacer)) {
            accessoryDestinationSlot = -1;
            return;
        }
        InventoryReplacer gui = (InventoryReplacer) McIf.mc().currentScreen;

        // no item picked up
        if (McIf.player().inventory.getItemStack().isEmpty()) return;

        // destination slot was filled in the meantime
        if (gui.inventorySlots.getSlot(accessoryDestinationSlot).getHasStack() &&
                !gui.inventorySlots.getSlot(accessoryDestinationSlot).getStack().getItem().equals(Item.getItemFromBlock(Blocks.SNOW_LAYER))) {
            accessoryDestinationSlot = -1;
            return;
        }

        // move accessory
        gui.handleMouseClick(gui.inventorySlots.getSlot(accessoryDestinationSlot), accessoryDestinationSlot, 0, ClickType.PICKUP);
        accessoryDestinationSlot = -1;
    }

    private boolean bankPageConfirmed = false;

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (e.getSlotIn() != null && e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() == 4) { // prevent accidental pouch clicking
            e.setCanceled(true);
            return;
        }

        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getSlotIn() != null) {
            if (e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() >= 0 && e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() < 27) {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() + 9, McIf.mc().gameSettings.keyBindDrop.getKeyCode()));
            } else {
                e.setCanceled(checkDropState(e.getSlotId() - e.getGui().getLowerInv().getSizeInventory() - 27, McIf.mc().gameSettings.keyBindDrop.getKeyCode()));
            }
        }

        if (UtilitiesConfig.Bank.INSTANCE.addBankConfirmation && e.getSlotIn() != null) {
            IInventory inventory = e.getSlotIn().inventory;
            if (McIf.getUnformattedText(inventory.getDisplayName()).contains("Bank") && e.getSlotIn().getHasStack()) {
                ItemStack item = e.getSlotIn().getStack();
                if (item.getDisplayName().contains(">" + TextFormatting.DARK_RED + ">" + TextFormatting.RED + ">" + TextFormatting.DARK_RED + ">" + TextFormatting.RED + ">")) {
                    String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(item));
                    String price = lore.substring(lore.indexOf(" Price: ") + 8, lore.length());
                    String priceDisplay;
                    if (price.matches("\\d+" + EmeraldSymbols.EMERALDS)) {
                        int actualPrice = Integer.parseInt(price.replace(EmeraldSymbols.EMERALDS, ""));
                        int le = (int) Math.floor(actualPrice) / 4096;
                        int eb = (int) Math.floor(((double) (actualPrice % 4096)) / 64);
                        int emeralds = actualPrice % 64;
                        StringBuilder priceBuilder = new StringBuilder();
                        if (le != 0) {
                            priceBuilder.append(le + EmeraldSymbols.LE + " ");
                        }
                        if (eb != 0) {
                            priceBuilder.append(eb + EmeraldSymbols.BLOCKS + " ");
                        }
                        if (emeralds != 0) {
                            priceBuilder.append(emeralds + EmeraldSymbols.EMERALDS + " ");
                        }
                        priceBuilder.deleteCharAt(priceBuilder.length() - 1);
                        priceDisplay = priceBuilder.toString();
                    } else {
                        priceDisplay = price;
                    }
                    String itemName = item.getDisplayName();
                    String pageNumber = itemName.substring(9, itemName.indexOf(TextFormatting.RED + " >"));
                    ChestReplacer gui = e.getGui();
                    CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, e.getSlotId(), e.getMouseButton(), e.getType(), item, e.getGui().inventorySlots.getNextTransactionID(McIf.player().inventory));
                    McIf.mc().displayGuiScreen(new GuiYesNo((result, parentButtonID) -> {
                        McIf.mc().displayGuiScreen(gui);
                        if (result) {
                            McIf.mc().getConnection().sendPacket(packet);
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
            CPacketClickWindow packet = new CPacketClickWindow(McIf.player().openContainer.windowId, 8, 0, ClickType.PICKUP, event.getPacket().getStack(), McIf.player().openContainer.getNextTransactionID(McIf.player().inventory));
            McIf.mc().getConnection().sendPacket(packet);
        }
    }

    @SubscribeEvent
    public void clickOnHorse(GuiOverlapEvent.HorseOverlap.HandleMouseClick e) {
        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null) {
            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex(), McIf.mc().gameSettings.keyBindDrop.getKeyCode()));
        }
    }

    private boolean lastWasDrop = false;

    @SubscribeEvent
    public void keyPress(PacketEvent<CPacketPlayerDigging> e) {
        if ((e.getPacket().getAction() != Action.DROP_ITEM && e.getPacket().getAction() != Action.DROP_ALL_ITEMS)
                || !UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId()))
            return;

        lastWasDrop = true;
        if (UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.get(CharacterData.class).getClassId()).contains(McIf.player().inventory.currentItem))
            e.setCanceled(true);
    }

    @SubscribeEvent
    public void onConsumable(PacketEvent<SPacketSetSlot> e) {
        if (!Reference.onWorld || e.getPacket().getWindowId() != 0) return;

        // the reason of the +36, is because in the client the hotbar is handled between 0-8
        // the hotbar in the packet starts in 36, counting from up to down
        if (e.getPacket().getSlot() != McIf.player().inventory.currentItem + 36) return;

        InventoryPlayer inventory = McIf.player().inventory;
        ItemStack oldStack = inventory.getStackInSlot(e.getPacket().getSlot() - 36);
        ItemStack newStack = e.getPacket().getStack();

        if (lastWasDrop) {
            lastWasDrop = false;
            return;
        }

        if (oldStack.isEmpty() || !newStack.isEmpty() && !oldStack.isItemEqual(newStack)) return; // invalid move
        if (!oldStack.hasDisplayName()) return; // old item is not a valid item

        String oldName = TextFormatting.getTextWithoutFormattingCodes(oldStack.getDisplayName());
        Matcher oldMatcher = CRAFTED_USES.matcher(oldName);
        if (!oldMatcher.matches()) return;
        int oldUses = Integer.parseInt(oldMatcher.group(1));

        int newUses = 0;
        if (!newStack.isEmpty()) {
            String newName = TextFormatting.getTextWithoutFormattingCodes(StringUtils.normalizeBadString(newStack.getDisplayName()));
            Matcher newMatcher = CRAFTED_USES.matcher(newName);
            if (newMatcher.matches()) {
                newUses = Integer.parseInt(newMatcher.group(1));
            } else {
                return;
            }
        }

        if (oldUses - 1 != newUses) {
            return;
        }
        McIf.mc().addScheduledTask(() -> ConsumableTimerOverlay.addConsumable(oldStack));
    }

    @SubscribeEvent
    public void removePotionGui(RenderGameOverlayEvent.Pre e) {
        if (UtilitiesConfig.INSTANCE.hidePotionGui && e.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            e.setCanceled(true);
        }
    }

    private static boolean checkDropState(int slot, int key) {
        if (!Reference.onWorld) return false;

        if (key == McIf.mc().gameSettings.keyBindDrop.getKeyCode()) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId())) return false;

            return UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.get(CharacterData.class).getClassId()).contains(slot);
        }
        return false;
    }

    private static void checkLockState(int slot) {
        if (!Reference.onWorld) return;

        if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.get(CharacterData.class).getClassId())) {
            UtilitiesConfig.INSTANCE.locked_slots.put(PlayerInfo.get(CharacterData.class).getClassId(), new HashSet<>());
        }

        if (UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.get(CharacterData.class).getClassId()).contains(slot)) {
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.get(CharacterData.class).getClassId()).remove(slot);
        } else {
            UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.get(CharacterData.class).getClassId()).add(slot);
        }

        UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    // blocking healing pots below
    @SubscribeEvent
    public void onUseItem(PacketEvent<CPacketPlayerTryUseItem> e) {
        ItemStack item = McIf.player().getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing") || !UtilitiesConfig.INSTANCE.blockHealingPots) return;

        EntityPlayerSP player = McIf.player();
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        McIf.mc().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        ItemStack item = McIf.player().getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing") || !UtilitiesConfig.INSTANCE.blockHealingPots) return;

        EntityPlayerSP player = McIf.player();
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        McIf.mc().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void onUseItemOnEntity(PacketEvent<CPacketUseEntity> e) {
        ItemStack item = McIf.player().getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing") || !UtilitiesConfig.INSTANCE.blockHealingPots) return;

        EntityPlayerSP player = McIf.player();
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        McIf.mc().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (!e.getItemStack().hasDisplayName() || !e.getItemStack().getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;
        if (e.getEntityPlayer().getHealth() != e.getEntityPlayer().getMaxHealth()) return;

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onShiftClickPlayer(PacketEvent<CPacketUseEntity> e) {
        if (!UtilitiesConfig.INSTANCE.preventTradesDuels) return;

        EntityPlayerSP player = McIf.player();
        if (!player.isSneaking()) return;

        Entity clicked = e.getPacket().getEntityFromWorld(player.world);
        if (!(clicked instanceof EntityPlayer)) return;

        EntityPlayer ep = (EntityPlayer) clicked;
        if (ep.getTeam() == null) return; // player model npc

        ItemType item = ItemUtils.getItemType(player.getHeldItemMainhand());
        if (item == null) return; // not any type of gear
        if (item != ItemType.WAND && item != ItemType.DAGGER && item != ItemType.BOW && item != ItemType.SPEAR && item != ItemType.RELIK) return; // not a weapon
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        ConsumableTimerOverlay.clearConsumables(false); // clear consumable list
    }

    @SubscribeEvent
    public void onWorldLeave(WynnWorldEvent.Leave e) {
        ConsumableTimerOverlay.clearConsumables(true); // clear consumable list
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

    static {
        try {
            UtilitiesModule.getModule().registerEvents(new FailsToLoadIfNoColorEvent());
        } catch (NoClassDefFoundError e) { /* ignore */ }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void clearEmptyTooltip(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (e.getGui().getSlotUnderMouse() == null || e.getGui().getSlotUnderMouse().getStack().isEmpty()) return;

        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        if (stack.hasDisplayName() && stack.getDisplayName().equals(" ")) {
            e.setCanceled(true);
        }
    }

}
