/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.InventoryData;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.core.utils.reference.RequirementSymbols;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.core.overlays.inventories.HorseReplacer;
import com.wynntils.modules.core.overlays.inventories.InventoryReplacer;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.SoundTrackManager;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.SoundEffectsConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.*;
import com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import com.wynntils.modules.utilities.overlays.hud.ManaTimerOverlay;
import com.wynntils.modules.utilities.overlays.ui.FakeGuiContainer;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.core.framework.instances.PlayerInfo.get;
import static net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes;

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

    private Timestamp emeraldPouchLastPickup = new Timestamp(0);
    private GameUpdateOverlay.MessageContainer emeraldPouchMessage;
    private IInventory currentLootChest;

    private static final Pattern PRICE_REPLACER = Pattern.compile("§6 - §a. §f([1-9]\\d*)§7" + EmeraldSymbols.E_STRING);
    private static final Pattern INGREDIENT_SPLIT_PATTERN = Pattern.compile("§f(\\d+) x (.+)");
    private static final Pattern WAR_CHAT_MESSAGE_PATTERN = Pattern.compile("§3\\[WAR§3\\] The war for (.+) will start in \\d+ minutes.");

    public static boolean isAwaitingHorseMount = false;
    private static int lastHorseId = -1;

    private static boolean priceInput = false;

    private static final Pattern CRAFTED_USES = Pattern.compile(".* \\[(\\d)/\\d\\]");

    private static Vec3i lastPlayerLocation = null;
    private static int lastProcessedOpenedChest = -1;
    private int lastOpenedChestWindowId = -1;
    private int lastOpenedRewardWindowId = -1;
    private int timesClosed = 0;


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

        // Manually send a hotbar packet if the user presses the already selected hotbar slot - for dialogue
        if (Keyboard.getEventKeyState() && Keyboard.getEventKey() - 2 == McIf.player().inventory.currentItem) { // -2 because KEY_1 is 0x02, and hotbar is 0-8
            McIf.mc().getConnection().sendPacket(new CPacketHeldItemChange(McIf.player().inventory.currentItem));
        }
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

        EntityPlayerSP player = McIf.player();
        if (player != null) {
            lastHealth = player.getHealth();
        }

        lastUserInput = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        // Only trigger four times a second
        if (e.phase == TickEvent.Phase.END || (tickCounter++ % 5) != 0) return;
        if (Reference.onServer) WindowIconManager.update();
        if (!Reference.onWorld) return;

        DailyReminderManager.checkDailyReminder(McIf.player());

        EntityPlayerSP player = McIf.player();
        if (player != null) {
            Entity lowestEntity = player.getLowestRidingEntity();

            lastPlayerLocation = new Vec3i(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ);
        }

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
            long longAfkThresholdMillis = (long) (UtilitiesConfig.AfkProtection.INSTANCE.afkProtectionThreshold * 60 * 1000);
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
                if (currentHealth < (lastHealth * UtilitiesConfig.AfkProtection.INSTANCE.healthPercentage / 100.0f)) {
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
    public void onKill(GameEvent.KillEntity ignored) {
        KillsManager.addKill();
   }

    @SubscribeEvent
    public void onGUIOpen(GuiOpenEvent e) {
        // Store the original opened chest so we can check itemstacks later
        // Make sure this check is not spoofed by checking inventory size
        if (e.getGui() instanceof ChestReplacer && getTextWithoutFormattingCodes(((ChestReplacer) e.getGui()).getLowerInv().getName()).startsWith("Loot Chest")
                && ((ChestReplacer) e.getGui()).getLowerInv().getSizeInventory() == 27) {
            currentLootChest = ((ChestReplacer) e.getGui()).getLowerInv();
        }
    }

    @SubscribeEvent
    public void onGUIClose(GuiOpenEvent e) {
        if (e.getGui() == null) {
            afkProtectionBlocked = false;
            lastUserInput = System.currentTimeMillis();
        } else if (e.getGui() instanceof InventoryReplacer || e.getGui() instanceof ChestReplacer || e.getGui() instanceof HorseReplacer) {
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

        Matcher warMatcher = WAR_CHAT_MESSAGE_PATTERN.matcher(McIf.getUnformattedText(e.getMessage()));
        if (warMatcher.matches()) {
            String territory = warMatcher.group(1);
            ITextComponent m = new TextComponentString("Click here to set your waypoint to " + territory + ".");
            m.getStyle()
                .setColor(TextFormatting.BLUE)
                .setUnderlined(true)
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/territory " + territory))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Set waypoint to: " + territory)));
            McIf.player().sendMessage(m);
        }
    }

    @SubscribeEvent
    public void onSendMessage(ClientChatEvent e) {
        if (!priceInput) return;

        priceInput = false;
        String price = StringUtils.convertEmeraldPrice(e.getMessage());
        if (!price.isEmpty()) e.setMessage(price);
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

        if (msg.startsWith("[!] Darkness has been enabled.") || msg.startsWith("[!] Twilight has been enabled.")) {
            ManaTimerOverlay.isTimeFrozen = true;
        }
    }

    @SubscribeEvent
    public void onTitle(PacketEvent<SPacketTitle> e) {
        if (!OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectIngredientPouch
                && !OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectEmeraldPouch
                && !OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectPotionStack)
            return;

        SPacketTitle packet = e.getPacket();
        if (packet.getType() != SPacketTitle.Type.SUBTITLE) return;
        String message = McIf.getUnformattedText(packet.getMessage());

        Matcher ingredientMatcher = Pattern.compile("^§a\\+\\d+ §7.+§a to pouch$").matcher(message);
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectIngredientPouch && ingredientMatcher.matches()) {
            e.setCanceled(true);
            GameUpdateOverlay.queueMessage(message);
            return;
        }

        Matcher emeraldMatcher = Pattern.compile("§a\\+(\\d+)§7 Emeralds? §ato pouch").matcher(message);
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectEmeraldPouch && emeraldMatcher.matches()) {
            e.setCanceled(true);
            if (new Timestamp(System.currentTimeMillis() - 3000).before(emeraldPouchLastPickup)) {
                // If the last emerald pickup event was less than 3 seconds ago, assume Wynn has relayed us an "updated" emerald title
                // Edit the first message it gave us with the new amount
                // editMessage doesn't return the new MessageContainer, so we can just keep re-using the first one
                int currentEmeralds = Integer.parseInt(emeraldMatcher.group(1));
                GameUpdateOverlay.editMessage(emeraldPouchMessage, "§a+" + currentEmeralds + "§7 Emeralds §ato pouch");
                emeraldPouchLastPickup = new Timestamp(System.currentTimeMillis());
                return;
            }
            // First time we've picked up emeralds in 3 seconds, set new MessageContainer and start the timer
            emeraldPouchMessage = GameUpdateOverlay.queueMessage(message);
            emeraldPouchLastPickup = new Timestamp(System.currentTimeMillis());
        }

        Matcher potionMatcher = Pattern.compile("§a\\+(\\d+)§7 potion §acharges?").matcher(message);
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectPotionStack && potionMatcher.matches()) {
            e.setCanceled(true);
            GameUpdateOverlay.queueMessage(message);
        }
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
        if (entityName == null || entityName.isEmpty() ||
                !MountHorseManager.isPlayersHorse(entityName, player.getName())) return;

        lastHorseId = thisId;

        if (SoundEffectsConfig.INSTANCE.horseWhistle) WynntilsSound.HORSE_WHISTLE.play();

        if (isAwaitingHorseMount) {
            MountHorseManager.retryMountHorseAndShowMessage();
            isAwaitingHorseMount = false;
            return;
        }

        if (!UtilitiesConfig.INSTANCE.autoMount) return;
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
                lore.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "Unknown renewal time");
            } else {
                lore.add(TextFormatting.GRAY + "Will renew " + getFormattedTimeString(getSecondsUntilDailyReward()));
            }

            ItemUtils.replaceLore(newStack, lore);
            slot.putStack(newStack);
            stack = newStack; // use this for next check
        }

        if (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) || stack.getItem() == Items.CLOCK) {
            // We need to strip the old time from the lore, if existent
            List<String> lore = ItemUtils.getLore(stack);
            List<String> newLore = new LinkedList<>();
            for (String line : lore) {
                if (line.contains("Daily Objective")) break;
                newLore.add(line);
            }
            int lastLine = newLore.size() - 1;
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
                newLore.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "Purchase a rank at wynncraft.com");
                newLore.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "for daily mob totems");
            }

            if (!playerRank.isVipPlus()) {
                newLore.add("");
                newLore.add(TextFormatting.GOLD + "Daily Crate");
                newLore.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "Get VIP+ or Hero rank");
                newLore.add("" + TextFormatting.GRAY + TextFormatting.ITALIC + "for daily crates");
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


        if (e.getKeyCode() == McIf.mc().gameSettings.keyBindDrop.getKeyCode() && e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId()))
                return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex()));
        }
    }

    @SubscribeEvent
    public void keyPressOnChest(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld) return;

        if (UtilitiesConfig.INSTANCE.preventMythicChestClose || UtilitiesConfig.INSTANCE.preventFavoritedChestClose) {
            boolean preventedClose = false;
            if (e.getKeyCode() == 1 || e.getKeyCode() == McIf.mc().gameSettings.keyBindInventory.getKeyCode()) {
                if (UtilitiesConfig.INSTANCE.preventFavoritedChestClosingAmount != 0 && timesClosed + 1 >= UtilitiesConfig.INSTANCE.preventFavoritedChestClosingAmount) {
                    timesClosed = 0;
                    return;
                }

                IInventory inv = e.getGui().getLowerInv();
                String invName = getTextWithoutFormattingCodes(inv.getName());
                if ((invName.startsWith("Loot Chest") || invName.contains("Daily Rewards") ||
                        invName.contains("Objective Rewards")) && inv.getSizeInventory() <= 27) {
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        TextComponentString text;
                        if (UtilitiesConfig.INSTANCE.preventMythicChestClose && stack.hasDisplayName() &&
                                stack.getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString()) &&
                                ItemUtils.getStringLore(stack).toLowerCase().contains("mythic")) {
                            text = new TextComponentString("You cannot close this loot chest while there is a mythic in it!");
                        } else if (UtilitiesConfig.INSTANCE.preventFavoritedChestClose && stack.hasTagCompound() &&
                                stack.getTagCompound().getBoolean("wynntilsFavorite")) {
                            text = new TextComponentString("You cannot close this loot chest while there is a favorited item, ingredient, emerald pouch, emerald or powder in it!"
                            + (UtilitiesConfig.INSTANCE.preventFavoritedChestClosingAmount != 0 ? " Try closing this chest " +
                                    (UtilitiesConfig.INSTANCE.preventFavoritedChestClosingAmount - timesClosed - 1) + " more times to forcefully close!" : ""));
                        } else {
                            continue;
                        }

                        text.getStyle().setColor(TextFormatting.RED);
                        McIf.player().sendMessage(text);
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));
                        e.setCanceled(true);
                        timesClosed++;
                        return;
                    }
                }
                timesClosed = 0;
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


        if (e.getKeyCode() == McIf.mc().gameSettings.keyBindDrop.getKeyCode() && e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId()))
                return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex()));
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

        if (e.getKeyCode() == McIf.mc().gameSettings.keyBindDrop.getKeyCode() && e.getGui().getSlotUnderMouse() != null && McIf.player().inventory == e.getGui().getSlotUnderMouse().inventory) {
            if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId()))
                return;

            e.setCanceled(checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex()));
        }
    }

    private void handleDungeonKeyMClick(ItemStack is) {
        if (DungeonKeyManager.isDungeonKey(is)) {
            Pair<Integer, Integer> coords = DungeonKeyManager.getDungeonCoords(is);
            CompassManager.setCompassLocation(new Location(coords.a, 0, coords.b));
            boolean isCorrupted = DungeonKeyManager.isCorrupted(is);
            String location = (isCorrupted) ? "§4The Forgery" : "§6" + DungeonKeyManager.getDungeonKey(is).getFullName(false);
            GameUpdateOverlay.queueMessage("§aSet compass to " + location);
        }
    }

    @SubscribeEvent
    public void clickOnInventory(GuiOverlapEvent.InventoryOverlap.HandleMouseClick e) {
        if (!Reference.onWorld) return;

        // Dungeon key middle click functionality
        if (e.getMouseButton() == 2 && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().getHasStack()) {
            ItemStack is = e.getGui().getSlotUnderMouse().getStack();
            handleDungeonKeyMClick(is);
        }

        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().inventory instanceof InventoryPlayer) {
            if ((!EmeraldPouchManager.isEmeraldPouch(e.getGui().getSlotUnderMouse().getStack()) || e.getMouseButton() == 0) && checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex())) {
                e.setCanceled(true);
                return;
            }
        }
    }

    /**
     * This method ensures that clicking on a party finder queue updates party data, since you are automatically put
     * into a party without a chat notification when joining a queue by yourself
     */
    @SubscribeEvent
    public void partyFinderInventoryClick(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        // This .contains() check is quite loose because it needs to account for the raid-specific pfinder NPCs at the start of each raid
        if (!e.getGui().getLowerInv().getName().contains("Party Finder")) return;
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        ItemStack clickedItemStack = e.getGui().getSlotUnderMouse().getStack();
        if (!ItemUtils.getStringLore(clickedItemStack).contains("§aClick to join the queue")) return;
        if (PlayerInfo.get(SocialData.class).getPlayerParty().isPartying()) return; // Already in a party, no need to do anything

        PlayerInfo.get(SocialData.class).getPlayerParty().setOwner(McIf.player().getName());
        PlayerInfo.get(SocialData.class).getPlayerParty().addMember(McIf.player().getName());
    }

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (e.getSlotIn() == null) return;

        // Dungeon key middle click functionality
        if (e.getMouseButton() == 2 && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().getHasStack()) {
            ItemStack is = e.getGui().getSlotUnderMouse().getStack();
            handleDungeonKeyMClick(is);
        }

        IInventory chestInv = e.getGui().getLowerInv();

        // Queue messages into game update ticker when clicking on emeralds in loot chest
        if (getTextWithoutFormattingCodes(chestInv.getName()).startsWith("Loot Chest") && OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectEmeraldPouch) {
            // Check if item is actually an emerald, if we're left clicking, and make sure we're not shift clicking
            if (currentLootChest.getStackInSlot(e.getSlotId()).getDisplayName().equals("§aEmerald") && e.getMouseButton() == 0 && !GuiScreen.isShiftKeyDown()) {
                // Find all emerald pouches in inventory
                NonNullList<Integer> availableCapacities = NonNullList.create();
                for (int i = 0; i < e.getGui().getUpperInv().getSizeInventory(); i++) {
                    ItemStack is = e.getGui().getUpperInv().getStackInSlot(i);
                    if (EmeraldPouchManager.isEmeraldPouch(is)) {
                        // Append the available capacities of all emerald pouches to a list
                        availableCapacities.add(EmeraldPouchManager.getPouchCapacity(is) - EmeraldPouchManager.getPouchUsage(is));
                    }
                }
                int emeraldAmount = currentLootChest.getStackInSlot(e.getSlotId()).getCount();
                // Iterate through all the available capacities and determine if emeralds can actually fit into any pouch
                for (int capacity : availableCapacities) {
                    // If yes, proceed and send a message to ticker
                    if (!(emeraldAmount > capacity)) {
                        String emeraldString = "Emerald";
                        if (emeraldAmount > 1) {
                            emeraldString += 's';
                        } // Grammar check!
                        GameUpdateOverlay.queueMessage("§a+" + emeraldAmount + "§7 " + emeraldString + " §ato pouch");
                        break; // Make sure we don't send multiple messages, if multiple pouches in inventory
                    }
                }
            }
        }


        // Prevent accidental ingredient/emerald pouch clicks in loot chests
        if (getTextWithoutFormattingCodes(chestInv.getName()).startsWith("Loot Chest") && UtilitiesConfig.INSTANCE.preventOpeningPouchesChest) {
            // Ingredient pouch
            if (e.getSlotId() - chestInv.getSizeInventory() == 4) {
                e.setCanceled(true);
                return;
            }
            // Emerald pouch
            int mappedSlot = e.getSlotId();
            if (e.getSlotId() > 54) mappedSlot -= 54;
            if (e.getSlotId() > 31 && e.getSlotId() < 54) mappedSlot -= 18;
            if (EmeraldPouchManager.isEmeraldPouch(e.getGui().getUpperInv().getStackInSlot(mappedSlot)) && e.getSlotId() > 26) {
                e.setCanceled(true);
                return;
            }
        }


        // Bulk buy functionality
        // The title for the shops are in slot 4
        if (UtilitiesConfig.INSTANCE.shiftBulkBuy && isBulkShopConsumable(chestInv, chestInv.getStackInSlot(e.getSlotId())) && GuiScreen.isShiftKeyDown()) {
            CPacketClickWindow packet = new CPacketClickWindow(e.getGui().inventorySlots.windowId, e.getSlotId(), e.getMouseButton(), e.getType(), e.getSlotIn().getStack(), e.getGui().inventorySlots.getNextTransactionID(McIf.player().inventory));
            for (int i = 1; i < UtilitiesConfig.INSTANCE.bulkBuyAmount; i++) { // int i is 1 by default because the user's original click is not cancelled
                McIf.mc().getConnection().sendPacket(packet);
            }
        }

        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().inventory instanceof InventoryPlayer) {
            if ((!EmeraldPouchManager.isEmeraldPouch(e.getGui().getSlotUnderMouse().getStack()) || e.getMouseButton() == 0) && checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex())) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void clickOnHorse(GuiOverlapEvent.HorseOverlap.HandleMouseClick e) {
        if (UtilitiesConfig.INSTANCE.preventSlotClicking && e.getGui().getSlotUnderMouse() != null && e.getGui().getSlotUnderMouse().inventory instanceof InventoryPlayer) {
            if ((!EmeraldPouchManager.isEmeraldPouch(e.getGui().getSlotUnderMouse().getStack()) || e.getMouseButton() == 0) && checkDropState(e.getGui().getSlotUnderMouse().getSlotIndex())) {
                e.setCanceled(true);
            }
        }
    }

    private boolean lastWasDrop = false;

    @SubscribeEvent
    public void keyPress(PacketEvent<CPacketPlayerDigging> e) {
        if ((e.getPacket().getAction() != Action.DROP_ITEM && e.getPacket().getAction() != Action.DROP_ALL_ITEMS)
                || !UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId()))
            return;

        lastWasDrop = true;
        if (UtilitiesConfig.INSTANCE.locked_slots.get(get(CharacterData.class).getClassId()).contains(McIf.player().inventory.currentItem))
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

        String oldName = getTextWithoutFormattingCodes(oldStack.getDisplayName());
        Matcher oldMatcher = CRAFTED_USES.matcher(oldName);
        if (!oldMatcher.matches()) return;
        int oldUses = Integer.parseInt(oldMatcher.group(1));

        int newUses = 0;
        if (!newStack.isEmpty()) {
            String newName = getTextWithoutFormattingCodes(StringUtils.normalizeBadString(newStack.getDisplayName()));
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

    private static boolean checkDropState(int slot) {
        if (!Reference.onWorld) return false;

        if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId()))
            return false;

        return UtilitiesConfig.INSTANCE.locked_slots.get(get(CharacterData.class).getClassId()).contains(slot);
    }

    private static void checkLockState(int slot) {
        if (!Reference.onWorld) return;

        if (!UtilitiesConfig.INSTANCE.locked_slots.containsKey(get(CharacterData.class).getClassId())) {
            UtilitiesConfig.INSTANCE.locked_slots.put(get(CharacterData.class).getClassId(), new HashSet<>());
        }

        if (UtilitiesConfig.INSTANCE.locked_slots.get(get(CharacterData.class).getClassId()).contains(slot)) {
            UtilitiesConfig.INSTANCE.locked_slots.get(get(CharacterData.class).getClassId()).remove(slot);
        } else {
            UtilitiesConfig.INSTANCE.locked_slots.get(get(CharacterData.class).getClassId()).add(slot);
        }

        UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    // blocking healing pots below
    private boolean blockHealingPotions(ItemStack stack) {
        if (UtilitiesConfig.INSTANCE.potionBlockingType.equals(UtilitiesConfig.PotionBlockingType.Never) || !HealthPotionManager.isHealthPotion(stack))
            return false;

        int threshold = UtilitiesConfig.INSTANCE.blockHealingPotThreshold;
        int currentHealth = get(CharacterData.class).getCurrentHealth();
        int maxHealth = get(CharacterData.class).getMaxHealth();

        switch (UtilitiesConfig.INSTANCE.potionBlockingType) {

            case HealthPercent:
                if (currentHealth / maxHealth * 100 < threshold) return false;
                McIf.mc().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You already have more than " + threshold + "% health!"));
                return true;

            case EffectivePercent:
                float missingHealth = maxHealth - currentHealth;
                float minHeal = (float) threshold / 100 * HealthPotionManager.getNextHealAmount(stack);
                if (missingHealth > minHeal) return false;
                McIf.mc().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "This potion is currently not " + threshold + "%+ effective!"));
                return true;

            case Never:
            default:
                return false;
        }
    }

    @SubscribeEvent
    public void onUsePotionBlock(PacketEvent<CPacketPlayerTryUseItem> e) {
        boolean blockPotionUse = blockHealingPotions(McIf.player().getHeldItem(EnumHand.MAIN_HAND));
        e.setCanceled(blockPotionUse);
    }

    @SubscribeEvent
    public void onUseBlockPotionBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        boolean blockPotionUse = blockHealingPotions(McIf.player().getHeldItem(EnumHand.MAIN_HAND));
        e.setCanceled(blockPotionUse);
    }

    @SubscribeEvent
    public void onUseEntityPotionBlock(PacketEvent<CPacketUseEntity> e) {
        boolean blockPotionUse = blockHealingPotions(McIf.player().getHeldItem(EnumHand.MAIN_HAND));
        e.setCanceled(blockPotionUse);
    }

    @SubscribeEvent
    public void onRightClickPotionBlock(PlayerInteractEvent.RightClickItem e) {
        boolean blockPotionUse = blockHealingPotions(e.getItemStack());
        e.setCanceled(blockPotionUse);
    }
    // blocking healing pots above

    @SubscribeEvent
    public void onShiftClickPlayer(PacketEvent<CPacketUseEntity> e) {
        if (!UtilitiesConfig.INSTANCE.preventTradesDuels) return;

        EntityPlayerSP player = McIf.player();
        if (!player.isSneaking()) return;

        Entity clicked = e.getPacket().getEntityFromWorld(player.world);
        if (!(clicked instanceof EntityPlayer)) return;

        EntityPlayer ep = (EntityPlayer) clicked;
        if (ep.getTeam() == null) return; // player model npc

        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) return;

        if (!ItemUtils.isWeapon(heldItem) &&
            !ItemUtils.isConsumable(heldItem) && // Potions, scrolls, food, etc.
            !ItemUtils.isHorse(heldItem) &&
            !ItemUtils.isGatheringTool(heldItem) &&
            !heldItem.getDisplayName().equals("§bCharacter Info") &&
            !heldItem.getDisplayName().equals("§dQuest Book")) return;

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        ConsumableTimerOverlay.clearConsumables(false); // clear consumable list
    }

    @SubscribeEvent
    public void onWorldLeave(WynnWorldEvent.Leave e) {
        ConsumableTimerOverlay.clearConsumables(true); // clear consumable list
        ManaTimerOverlay.isTimeFrozen = false;
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

    private static boolean isBulkShopConsumable(IInventory inv, ItemStack is) {
        return inv.getStackInSlot(4).getDisplayName().contains("§a")
                && inv.getStackInSlot(4).getDisplayName().contains("Shop")
                && ItemUtils.getStringLore(is).contains("§6Price:")
                && !ItemUtils.getStringLore(is).contains(" x "); // Make sure we're not in trade market
        // Normal shops don't have a string with " x " whereas TM uses it for the amount of the item being sold
    }

    @SubscribeEvent
    public void onItemHovered(ItemTooltipEvent e) {
        // If the shift to bulk buy setting is on and is applicable to the hovered item, add bulk prices
        if (!UtilitiesConfig.INSTANCE.shiftBulkBuy
                || !(McIf.mc().currentScreen instanceof ChestReplacer)
                || !isBulkShopConsumable(((ChestReplacer) McIf.mc().currentScreen).getLowerInv(), e.getItemStack())) return;

        ItemStack is = e.getItemStack();
        List<String> newLore = new ArrayList<>();
        NBTTagCompound nbt = is.getTagCompound();

        for (String loreLine : ItemUtils.getLore(is)) {
            if (!loreLine.contains("§aPurchasing ") && !loreLine.contains("§aShift-click to purchase ")) { // Do not add the last bit of info text
                newLore.add(loreLine);
            }

            if (!nbt.hasKey("wynntilsBulkPrice")) {
                Matcher priceMatcher = PRICE_REPLACER.matcher(loreLine);
                if (priceMatcher.matches()) { // Determine if we have enough money to buy the bulk amount and add lore
                    int singularPrice = Integer.parseInt(priceMatcher.group(1));
                    int bulkPrice = singularPrice * UtilitiesConfig.INSTANCE.bulkBuyAmount;
                    int availMoney = get(InventoryData.class).getMoney(); // this value includes both raw emeralds and pouches

                    String moneySymbol = (bulkPrice > availMoney) ? TextFormatting.RED + RequirementSymbols.XMARK_STRING : TextFormatting.GREEN + RequirementSymbols.CHECKMARK_STRING;
                    String loreString = "§6 - " + moneySymbol + " §f" + bulkPrice + "§7" + EmeraldSymbols.E_STRING + " (" + UtilitiesConfig.INSTANCE.bulkBuyAmount + "x)";

                    newLore.add(loreString);
                    nbt.setBoolean("wynntilsBulkPrice", true);
                }
            }
        }

        if (!nbt.hasKey("wynntilsBulkShiftSpacer")) { // Only add the spacer once
            newLore.add(" ");
            nbt.setBoolean("wynntilsBulkShiftSpacer", true);
        }

        // If user is holding shift, just tell them they're already buying x amount
        String purchaseString = "§a" + (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? "Purchasing " : "Shift-click to purchase ") + UtilitiesConfig.INSTANCE.bulkBuyAmount;

        newLore.add(purchaseString);
        ItemUtils.replaceLore(is, newLore);
    }

    @SubscribeEvent
    public void onPlayerDeath(GameEvent.PlayerDeath e) {
        if (lastPlayerLocation == null || !UtilitiesConfig.INSTANCE.deathMessageWithCoords) return;

        ITextComponent textComponent = new TextComponentString("You have died at ");
        ITextComponent textComponentCoords = new TextComponentString(String.format("X: %d Y: %d Z: %d.", lastPlayerLocation.getX(), lastPlayerLocation.getY(), lastPlayerLocation.getZ()));

        textComponent.getStyle()
                .setColor(TextFormatting.DARK_RED);
        textComponentCoords.getStyle()
                .setColor(TextFormatting.DARK_RED)
                .setUnderlined(true)
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " + lastPlayerLocation.getX() + " " + lastPlayerLocation.getY() + " " + lastPlayerLocation.getZ()));

        McIf.player().sendMessage(textComponent.appendSibling(textComponentCoords));
    }

    @SubscribeEvent
    public void onWindowOpen(PacketEvent<SPacketOpenWindow> e){
        if (getTextWithoutFormattingCodes(e.getPacket().getWindowTitle().getUnformattedText()).startsWith("Loot Chest"))
            lastOpenedChestWindowId = e.getPacket().getWindowId();
        if (e.getPacket().getWindowTitle().toString().contains("Daily Rewards") || e.getPacket().getWindowTitle().toString().contains("Objective Rewards"))
            lastOpenedRewardWindowId = e.getPacket().getWindowId();
    }

    // Dry streak counter, mythic music event
    @SubscribeEvent
    public void onMythicFound(PacketEvent<SPacketWindowItems> e) {
        if (lastOpenedChestWindowId != e.getPacket().getWindowId() && lastOpenedRewardWindowId != e.getPacket().getWindowId()) return;

        // Get items in chest, return if there is not enough or too many items
        // 63 is the size of a chest (27) + player inventory
        if (e.getPacket().getItemStacks().size() != 63)
            return;

        // Only run at first time we get items, don't care about updating
        if (e.getPacket().getWindowId() == lastProcessedOpenedChest) return;

        lastProcessedOpenedChest = e.getPacket().getWindowId();

        // Dry streak counter and sound sfx
        if (UtilitiesConfig.INSTANCE.enableDryStreak && lastOpenedChestWindowId == e.getPacket().getWindowId()) {
            boolean foundMythic = false;
            // Size should be at least 27, checked for it earlier
            int size = 27;
            for (int i = 0; i < size; i++) {
                ItemStack stack = e.getPacket().getItemStacks().get(i);
                if (stack.isEmpty() || !stack.hasDisplayName()) continue;
                if (!stack.getDisplayName().contains("Unidentified")) continue;

                UtilitiesConfig.INSTANCE.dryStreakBoxes += 1;

                if (!stack.getDisplayName().contains(TextFormatting.DARK_PURPLE.toString())) continue;

                if (MusicConfig.SoundEffects.INSTANCE.mythicFound) {
                    try {
                        SoundTrackManager.findTrack(WebManager.getMusicLocations().getEntryTrack("mythicFound"),
                                true, false, false, false, true, false, true);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }


                if (UtilitiesConfig.INSTANCE.enableDryStreak && UtilitiesConfig.INSTANCE.dryStreakEndedMessage) {
                    ITextComponent textComponent = new TextComponentString(UtilitiesConfig.INSTANCE.dryStreakCount + " long dry streak broken! Mythic found! Found boxes since last mythic: " + UtilitiesConfig.INSTANCE.dryStreakBoxes);
                    textComponent.getStyle()
                            .setColor(TextFormatting.DARK_PURPLE)
                            .setBold(true);
                    McIf.player().sendMessage(textComponent);
                }

                foundMythic = true;
                UtilitiesConfig.INSTANCE.dryStreakCount = 0;
                UtilitiesConfig.INSTANCE.dryStreakBoxes = 0;

                break;
            }

            if (!foundMythic)
                UtilitiesConfig.INSTANCE.dryStreakCount += 1;

            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
            return;
        }

        // Mythic found sfx for daily rewards and objective rewards
        if (!MusicConfig.SoundEffects.INSTANCE.mythicFound) return;

        // Size should be at least 27, checked for it earlier
        int size = 27;
        for (int i = 0; i < size; i++) {
            ItemStack stack = e.getPacket().getItemStacks().get(i);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue;
            if (!stack.getDisplayName().contains(TextFormatting.DARK_PURPLE.toString())) continue;
            if (!stack.getDisplayName().contains("Unidentified")) continue;

            try {
                SoundTrackManager.findTrack(WebManager.getMusicLocations().getEntryTrack("mythicFound"),
                        true, false, false, false, true, false, true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            break;
        }
    }

    @SubscribeEvent
    public void onIngredientPouchHovered(ItemTooltipEvent e) {
        ItemStack itemStack = e.getItemStack();
        if (!itemStack.getDisplayName().equals("§6Ingredient Pouch")) return; // Is item Ingredient Pouch

        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt.hasKey("groupedItems"))
            return;

        List<String> lore = ItemUtils.getLore(itemStack);
        // Name, Pair<Qty, Rarity>
        HashMap<String, Pair<Integer, Integer>> items = new HashMap<>();

        boolean foundFirstItem = false;
        int[] originalSlots = new int[27];
        int slot = 0;
        for (String line : lore) {
            if (line == null)
                return;

            Matcher matcher = INGREDIENT_SPLIT_PATTERN.matcher(line);

            //Account for ironman
            if (!matcher.matches() && foundFirstItem)
                break;
            else if (!matcher.matches())
                continue;

            foundFirstItem = true;
            int rarity = 0;
            String firstHalf = line.split("§8")[0]; // Wynn uses §8 to make stars grey (deactivated)
            int stars = (int) firstHalf.chars().filter(star -> star == '✫').count();
            if (line.contains("§6") && stars == 1) {
                rarity = 1;
            } else if (line.contains("§5") && stars == 2) {
                rarity = 2;
            } else if (line.contains("§3") && stars == 3) {
                rarity = 3;
            }

            int itemCount = Integer.parseInt(matcher.group(1));
            String itemName = matcher.group(2);

            if (!items.containsKey(itemName)) {
                items.put(itemName, new Pair<>(itemCount, rarity));
            } else {
                int prevQty = items.get(itemName).a;
                items.replace(itemName, new Pair<>(prevQty + itemCount, rarity));
            }
            originalSlots[slot] = itemCount;
            slot += 1;
        }

        Map<String, Pair<Integer, Integer>> sortedIngredients;
        switch (UtilitiesConfig.INSTANCE.sortIngredientPouch) {
            case Alphabetical:
                if (UtilitiesConfig.INSTANCE.sortIngredientPouchReverse) {
                    // Use TreeMap with reverseOrder to sort reverse-alphabetically
                    sortedIngredients = new TreeMap<>(Collections.reverseOrder());
                    sortedIngredients.putAll(items);
                } else {
                    sortedIngredients = new TreeMap<>(items); // TreeMap will sort it for us
                }
                break;
            case Quantity:
                sortedIngredients = new LinkedHashMap<>();
                List<Map.Entry<String, Pair<Integer, Integer>>> sortedQtyList = new ArrayList<>(items.entrySet());

                // Define default and reverse comparators
                Comparator<Map.Entry<String, Pair<Integer, Integer>>> sortQtyComparator = UtilitiesConfig.INSTANCE.sortIngredientPouchReverse ?
                        Comparator.comparing(o -> (o.getValue().a)) : // Sort based on qty, sorted ascending
                        Comparator.comparing((Map.Entry<String, Pair<Integer, Integer>> o) -> (o.getValue().a)).reversed(); // Sort based on qty, sorted descending

                // Sort using comparator
                sortedQtyList.sort(sortQtyComparator);

                for (Map.Entry<String, Pair<Integer, Integer>> ingredient : sortedQtyList) {
                    sortedIngredients.put(ingredient.getKey(), ingredient.getValue());
                }
                break;
            case Rarity:
            default:
                sortedIngredients = new LinkedHashMap<>();
                List<Map.Entry<String, Pair<Integer, Integer>>> sortedRarityList = new ArrayList<>(items.entrySet());

                // Define default and reverse comparators
                Comparator<Map.Entry<String, Pair<Integer, Integer>>> sortRarityComparator = UtilitiesConfig.INSTANCE.sortIngredientPouchReverse ?
                        Comparator.comparing(o -> (o.getValue().b)) : // Sort based on rarity, sorted ascending (0* -> 3*)
                        Comparator.comparing((Map.Entry<String, Pair<Integer, Integer>> o) -> (o.getValue().b)).reversed(); // Sort based on rarity, sorted descending (3* -> 0*)

                // Sort using comparator
                sortedRarityList.sort(sortRarityComparator);

                for (Map.Entry<String, Pair<Integer, Integer>> ingredient : sortedRarityList) {
                    sortedIngredients.put(ingredient.getKey(), ingredient.getValue());
                }
                break;
        }

        List<String> groupedItemLore = new ArrayList<>();

        //Account for +2 lines when using ironman
        for (int i = 0; i < 6 && i < lore.size(); i++) {
            String line = lore.get(i);
            Matcher matcher = INGREDIENT_SPLIT_PATTERN.matcher(line);

            if (matcher.matches())
                break;

            groupedItemLore.add(line);
        }

        for (Map.Entry<String, Pair<Integer, Integer>> line : sortedIngredients.entrySet()) {
            groupedItemLore.add("§f" + line.getValue().a + " x " + line.getKey());
        }

        nbt.setBoolean("groupedItems", true);
        nbt.setIntArray("originalItems", originalSlots);
        ItemUtils.replaceLore(itemStack, groupedItemLore);
    }
}
