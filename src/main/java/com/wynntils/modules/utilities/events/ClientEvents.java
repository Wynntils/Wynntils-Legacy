/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionFields;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.network.play.server.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ClientEvents implements Listener {
    private static GuiScreen scheduledGuiScreen = null;
    private static boolean firstNullOccurred = false;

    private static boolean pushBlockingEnabled = false;
    private static boolean afkProtectionEnabled = false;
    private static boolean afkProtectionActivated = false;
    private static boolean afkProtectionRequested = false;
    private static boolean afkProtectionBlocked = false;

    private static float lastHealth = 0;
    private static long lastUserInput = Long.MAX_VALUE;
    private static long lastAfkRequested = Long.MAX_VALUE;
    private int tickCounter;

    public static boolean isAwaitingHorseMount = false;

    @SubscribeEvent
    public void onMoveEvent(InputEvent.MouseInputEvent e) {
        lastUserInput = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void onKeyboardEven(InputEvent.KeyInputEvent e) {
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
        pushBlockingEnabled = false;
        afkProtectionEnabled = false;
        afkProtectionActivated = false;

        lastHealth = Minecraft.getMinecraft().player.getHealth();
        lastUserInput = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        // Only trigger four times a second
        if (e.phase == TickEvent.Phase.END || (tickCounter++ % 5) != 0) return;
        if (Reference.onServer) WindowIconManager.update();
        if (!Reference.onWorld) return;

        DailyReminderManager.checkDailyReminder(ModCore.mc().player);

        if (!UtilitiesConfig.AfkProtection.INSTANCE.blockAfkPushs && !UtilitiesConfig.AfkProtection.INSTANCE.afkProtection) return;

        if (afkProtectionRequested) {
            afkProtectionRequested = false;
            // Immediate AFK requested, fake that last activity was long ago
            lastUserInput = 0;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceActivity = currentTime - this.lastUserInput;

        if (UtilitiesConfig.AfkProtection.INSTANCE.blockAfkPushs) {
            if (!pushBlockingEnabled) {
                if (timeSinceActivity >= 10000 || !Display.isActive()) {
                    // If not enabled, but we lose focus or no activity for 10 seconds, turn on
                    Utils.createFakeScoreboard("Afk", Team.CollisionRule.NEVER);
                    pushBlockingEnabled = true;
                }
            } else  {
                if (timeSinceActivity < 10000 && Display.isActive()) {
                    // If turned on, but we gain focus or have activity, turn off
                    pushBlockingEnabled = false;
                    Utils.removeFakeScoreboard("Afk");
                }
            }
        }

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
                    lastHealth = Minecraft.getMinecraft().player.getHealth();
                    if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectAfk) {
                        GameUpdateOverlay.queueMessage("AFK Protection enabled");
                    } else {
                        Minecraft.getMinecraft().addScheduledTask(() ->
                                ChatOverlay.getChat().printChatMessage(new TextComponentString(TextFormatting.GRAY + "AFK Protection enabled due to lack of movement")));
                    }
                    afkProtectionEnabled = true;
                }
            } else {
                float currentHealth = Minecraft.getMinecraft().player.getHealth();
                if (currentHealth < (lastHealth  * UtilitiesConfig.AfkProtection.INSTANCE.healthPercentage / 100.0f)) {
                    // We're taking damage; activate AFK protection and go to class screen
                    afkProtectionActivated = true;
                    Minecraft.getMinecraft().addScheduledTask(() ->
                            ChatOverlay.getChat().printChatMessage(new TextComponentString(TextFormatting.GRAY + "AFK Protection activated due to player taking damage")));
                    Minecraft.getMinecraft().player.sendChatMessage("/class");
                }
                if (timeSinceActivity < longAfkThresholdMillis) {
                    if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectAfk) {
                        GameUpdateOverlay.queueMessage("AFK Protection disabled");
                    } else {
                        Minecraft.getMinecraft().addScheduledTask(() ->
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
        if (!UtilitiesConfig.INSTANCE.disableFovChanges) return;

        e.setNewfov(1f + (e.getEntity().isSprinting() ? 0.15f : 0));
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
        String msg = e.getMessage().getUnformattedText();
        if (msg.startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
    }

    private static int lastHorseId = -1;
    @SuppressWarnings("unchecked")
    private static final DataParameter<String> nameKey = (DataParameter<String>) ReflectionFields.Entity_CUSTOM_NAME.getValue(Entity.class);

    public static String getNameFromMetadata(SPacketEntityMetadata packet) {
        assert nameKey != null;
        for (EntityDataManager.DataEntry<?> entry :packet.getDataManagerEntries()) {
            if (nameKey.equals(entry.getKey())) {
                return (String) entry.getValue();
            }
        }
        return null;
    }

    TotemTracker totemTracker = new TotemTracker();

    private static class TotemTracker {
        public enum TotemState { NONE, SUMMONED, LANDING, PREPARING, ACTIVATING, ACTIVE}
        private TotemState totemState = TotemState.NONE;
        private int trackedTotemId = -1;
        private double trackedX, trackedY, trackedZ;
        private double potentialX, potentialY, potentialZ;
        private int potentialTrackedId = -1;
        private int trackedTime;
        private long spellCastTimestamp = 0;
        private long totemCreatedTimestamp = Long.MAX_VALUE;

        private int bufferedId = -1;
        private double bufferedX = -1;
        private double bufferedY = -1;
        private double bufferedZ = -1;

        private void updateTotemPosition(double x, double y, double z) {
            trackedX = x;
            trackedY = y;
            trackedZ = z;
        }

        private void resetTotemTracking() {
            trackedTotemId = -1;
            trackedTime = -1;
            trackedX = 0;
            trackedY = 0;
            trackedZ = 0;
        }

        private static boolean isClose(double a, double b)
        {
            double diff = Math.abs(a - b);
            return  (diff < 3);
        }

        private Entity getBufferedEntity(int entityId) {
            Entity entity = ModCore.mc().world.getEntityByID(entityId);
            if (entity != null) return entity;

            if (entityId == bufferedId) {
                return new EntityArmorStand(ModCore.mc().world, bufferedX, bufferedY, bufferedZ);
            }

            return null;
        }

        private void postEvent(Event event) {
            ModCore.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
        }

        private void checkTotemSummoned() {
            // Check if we have both creation and spell cast at roughly the same time
            if (Math.abs(totemCreatedTimestamp - spellCastTimestamp) < 500) {
                if (totemState != TotemState.NONE) {
                    // We have an active totem already, first remove that one
                    totemState = TotemState.NONE;
                    resetTotemTracking();
                    postEvent(new SpellEvent.TotemRemoved(true));
                }
                trackedTotemId = potentialTrackedId;
                trackedTime  = -1;

                updateTotemPosition(potentialX, potentialY, potentialZ);
                totemState = TotemState.SUMMONED;
                postEvent(new SpellEvent.TotemSummoned());
            }
        }

        public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
            if (e.getPacket().getType() == 78) {
                bufferedId = e.getPacket().getEntityID();
                bufferedX = e.getPacket().getX();
                bufferedY = e.getPacket().getY();
                bufferedZ = e.getPacket().getZ();

                if (e.getPacket().getEntityID() == trackedTotemId) {
                    // Totems respawn with the same entityID when landing.
                    // Update with more precise coordinates
                    updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
                    totemState = TotemState.LANDING;
                    return;
                }

                // Is it created close to us? Then it's a potential new totem
                if (isClose(e.getPacket().getX(), Minecraft.getMinecraft().player.posX) &&
                        isClose(e.getPacket().getY(), Minecraft.getMinecraft().player.posY + 1.0) &&
                        isClose(e.getPacket().getZ(), Minecraft.getMinecraft().player.posZ)) {
                    potentialTrackedId = e.getPacket().getEntityID();
                    potentialX = e.getPacket().getX();
                    potentialY = e.getPacket().getY();
                    potentialZ = e.getPacket().getZ();
                    totemCreatedTimestamp = System.currentTimeMillis();
                    checkTotemSummoned();
                }
            }
        }

        public void onTotemSpellCast(SpellEvent.Cast e) {
            if (e.getSpell().equals("Totem")) {
                spellCastTimestamp = System.currentTimeMillis();
                checkTotemSummoned();
            }
        }

        public void onTotemTeleport(PacketEvent<SPacketEntityTeleport> e) {
            int thisId = e.getPacket().getEntityId();

            if (thisId == trackedTotemId && (totemState == TotemState.SUMMONED || totemState == TotemState.LANDING)) {
                // Now the totem has gotten it's final coordinates
                updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
                totemState = TotemState.PREPARING;
            }
        }

        public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
            if (!Reference.onServer || !Reference.onWorld) return;

            String name = getNameFromMetadata(e.getPacket());
            if (name == null || name.isEmpty()) return;

            Entity entity = getBufferedEntity(e.getPacket().getEntityId());
            if (!(entity instanceof EntityArmorStand)) return;

            Pattern shamanTotemTimer = Pattern.compile("^§c([0-9][0-9]?)s$");
            Matcher m = shamanTotemTimer.matcher(name);
            if (m.find()) {
                // We got a armor stand with a timer nametag
                double distanceXZ = Math.abs(entity.posX  - trackedX) +  Math.abs(entity.posZ  - trackedZ);
                if (distanceXZ < 3.0 && entity.posY <= (trackedY + 3.0) && entity.posY >= ((trackedY + 2.0))) {
                    // ... and it's close to our totem; regard this as our timer
                    int time = Integer.parseInt(m.group(1));

                    if (trackedTime == -1) {
                        trackedTime = time;
                        totemState = TotemState.ACTIVE;
                        postEvent(new SpellEvent.TotemActivated(trackedTime));
                    } else if (time != trackedTime) {
                        trackedTime = time;
                    }
                }
            }
        }

        public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
            IntStream entityIDs = Arrays.stream(e.getPacket().getEntityIDs());
            if (entityIDs.filter(id -> id == trackedTotemId).findFirst().isPresent()) {
                if (totemState == TotemState.ACTIVE && trackedTime == 0) {
                    totemState = TotemState.NONE;
                    resetTotemTracking();
                    postEvent(new SpellEvent.TotemRemoved(false));
                }
            }
        }

        public void onTotemClassChange(WynnClassChangeEvent e) {
            resetTotemTracking();
        }
    }

    @SubscribeEvent
    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        totemTracker.onTotemSpawn(e);
    }

    @SubscribeEvent
    public void onTotemSpellCast(SpellEvent.Cast e) {
        totemTracker.onTotemSpellCast(e);
    }

    @SubscribeEvent
    public void onTotemTeleport(PacketEvent<SPacketEntityTeleport> e) {
        totemTracker.onTotemTeleport(e);
    }

    @SubscribeEvent
    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        totemTracker.onTotemRename(e);
    }

    @SubscribeEvent
    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        totemTracker.onTotemDestroy(e);
    }

    @SubscribeEvent
    public void onTotemClassChange(WynnClassChangeEvent e) {
        totemTracker.onTotemClassChange(e);
    }

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
        String entityName = getNameFromMetadata(e.getPacket());
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
                        ItemStack stack = inv.getStackInSlot(i);
                        if (!stack.hasDisplayName() ||
                            !stack.getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString()) ||
                            !ItemUtils.getStringLore(stack).toLowerCase().contains("mythic")) continue;

                        TextComponentString text = new TextComponentString("You cannot close this loot chest while there is a mythic in it!");
                        text.getStyle().setColor(TextFormatting.RED);

                        Minecraft.getMinecraft().player.sendMessage(text);
                        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));
                        e.setCanceled(true);
                        break;
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
                    int le = (int) Math.floor(actualPrice) / 4096;
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
        Minecraft.getMinecraft().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void onUseItemOnEntity(PacketEvent<CPacketUseEntity> e) {
        ItemStack item = Minecraft.getMinecraft().player.getHeldItem(EnumHand.MAIN_HAND);

        if (item.isEmpty() || !item.hasDisplayName() || !item.getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.getHealth() != player.getMaxHealth()) return;

        e.setCanceled(true);
        Minecraft.getMinecraft().addScheduledTask(() -> GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are already at full health!"));
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (!e.getItemStack().hasDisplayName() || !e.getItemStack().getDisplayName().contains(TextFormatting.RED + "Potion of Healing")) return;
        if (e.getEntityPlayer().getHealth() != e.getEntityPlayer().getMaxHealth()) return;

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

    @SubscribeEvent
    public void clearEmptyTooltip(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (e.getGui().getSlotUnderMouse() == null || e.getGui().getSlotUnderMouse().getStack().isEmpty()) return;

        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        if (stack.hasDisplayName() && stack.getDisplayName().equals(" ")) {
            e.setCanceled(true);
        }
    }

}
