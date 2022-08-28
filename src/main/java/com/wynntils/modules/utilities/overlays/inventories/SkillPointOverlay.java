/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.ui.elements.GuiTextFieldWynn;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.SkillPointAllocation;
import com.wynntils.modules.utilities.overlays.ui.SkillPointLoadoutUI;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillPointOverlay implements Listener {

    private static final Pattern REMAINING_SKILLPOINT_PATTERN = Pattern.compile("§7You have §a(\\d+)§7 skill points§7to be distributed");
    private static final Pattern PLAYER_INFO_PAGE_PATTERN = Pattern.compile("§fPage (\\d+)§7");
    private static final Pattern SKILLPOINT_PATTERN = Pattern.compile(".*?§7(\\d+) points");
    private static final Pattern[] MODIFIER_PATTERNS = {
            Pattern.compile("- Strength: ([-+0-9]+)"),
            Pattern.compile("- Dexterity: ([-+0-9]+)"),
            Pattern.compile("- Intelligence: ([-+0-9]+)"),
            Pattern.compile("- Defence: ([-+0-9]+)"),
            Pattern.compile("- Agility: ([-+0-9]+)"),
    };

    private static final int SAVE_SLOT = 3;
    private static final int LOAD_SLOT = 5;
    private static final int PLAYER_INFO_SLOT = 7;

    private final ScreenRenderer renderer = new ScreenRenderer();

    private GuiTextFieldWynn nameField;

    private int skillPointsRemaining;
    private SkillPointAllocation loadedBuild = null;
    private boolean startBuild = false;
    private boolean itemsLoaded = false;
    private float buildPercentage = 0.0f;

    private int[] currentSp = new int[5];
    private boolean gotInitialSp = false;

    private boolean skipResettingSkillPointData = false;
    private boolean waitingForSkillPointData = false;
    private Container playerInfoInventorySlots = null;
    private int[] gearSkillPoints = new int[5];

    @SubscribeEvent
    public void onChestClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        nameField = null;
        itemsLoaded = false;
        skipResettingSkillPointData = false;

        Keyboard.enableRepeatEvents(false);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        if (!e.getGui().getLowerInv().getName().equals("Character Info") || !e.getGui().getLowerInv().getStackInSlot(4).getDisplayName().equals("§2§lSkill Crystal")) return;
        Matcher spMatcher = REMAINING_SKILLPOINT_PATTERN.matcher(ItemUtils.getStringLore(e.getGui().getLowerInv().getStackInSlot(4)));
        if (!spMatcher.find()) return;
        skillPointsRemaining = Integer.parseInt(spMatcher.group(1));

        if (!skipResettingSkillPointData && !waitingForSkillPointData) {
            gearSkillPoints = new int[5];
            skipResettingSkillPointData = true;
            // Here, we will need to start going through the player information pages as we don't want to continuously do this
            // while builds are loading - that creates noise and requires many delays as we wait for the information
            // Additionally, gear should not be able to change while we are loading a skill point build
            // We need to check the first page here manually, since SPacketSetSlot will only detect updates
            ItemStack itemStack = e.getGui().getLowerInv().getStackInSlot(PLAYER_INFO_SLOT);

            // Add the skill point gear info into gearSkillPoints
            addGearSPModifiers(itemStack);
            int pages = (int) ItemUtils.getStringLore(itemStack).chars().filter(num -> num == '■').count();
            if (pages > 0) { // 0 because no squares present when only 1 page
                // More than one page, set variables and send click
                waitingForSkillPointData = true;
                playerInfoInventorySlots = e.getGui().inventorySlots;
                CPacketClickWindow packet = new CPacketClickWindow(e.getGui().inventorySlots.windowId, PLAYER_INFO_SLOT, 0,
                        ClickType.PICKUP, itemStack, playerInfoInventorySlots.getNextTransactionID(McIf.player().inventory));
                McIf.mc().getConnection().sendPacket(packet);
            }
        }

        // load/save loadout items
        ItemStack save = new ItemStack(Items.WRITABLE_BOOK);
        save.setStackDisplayName(TextFormatting.GOLD + "[>] Save current loadout");
        ItemUtils.replaceLore(save, (waitingForSkillPointData) ?
                Arrays.asList(TextFormatting.GRAY + "Allows you to save this loadout with a name.",
                        TextFormatting.RED + "Waiting for skill point data...",
                        TextFormatting.GRAY + "Click on your player head if this is not loading.") :
                Collections.singletonList(TextFormatting.GRAY + "Allows you to save this loadout with a name."));
        e.getGui().inventorySlots.getSlot(SAVE_SLOT).putStack(save);

        ItemStack load = new ItemStack(Items.ENCHANTED_BOOK);
        load.setStackDisplayName(TextFormatting.GOLD + "[>] Load loadout");
        ItemUtils.replaceLore(load, (waitingForSkillPointData) ?
                Arrays.asList(TextFormatting.GRAY + "Allows you to load one of your saved loadouts.",
                        TextFormatting.RED + "Waiting for skill point data...",
                        TextFormatting.GRAY + "Click on your player head if this is not loading.") :
                Collections.singletonList(TextFormatting.GRAY + "Allows you to load one of your saved loadouts."));
        e.getGui().inventorySlots.getSlot(LOAD_SLOT).putStack(load);

        // skill point allocating
        if (!itemsLoaded || startBuild) {
            startBuild = false;
            allocateSkillPoints(e.getGui());
        }
        for (int i = 0; i < e.getGui().getLowerInv().getSizeInventory(); i++) {
            ItemStack stack = e.getGui().getLowerInv().getStackInSlot(i);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue; // display name also checks for tag compound

            String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
            String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            int value;
            if (name.contains("Profession [")) { // Profession Icons
                int start = lore.indexOf("Level: ") + 7;
                int end = lore.indexOf("XP: ");
                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ") + 11;
                int end = lore.indexOf("Class: ");
                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("Damage Info")) { //Average Damage
                Pattern pattern = Pattern.compile("Total Damage \\(\\+Bonus\\): ([0-9]+)-([0-9]+)");
                Matcher m2  = pattern.matcher(lore);
                if (!m2.find()) continue;
                int min = Integer.parseInt(m2.group(1));
                int max = Integer.parseInt(m2.group(2));
                value = Math.round((max + min) / 2.0f);
            } else if (name.contains("Daily Rewards")) { //Daily Reward Multiplier
                int start = lore.indexOf("Streak Multiplier: ") + 19;
                int end = lore.indexOf("Log in everyday to");
                value = Integer.parseInt(lore.substring(start, end));
            } else continue;
            stack.setCount(value <= 0 ? 1 : value);
        }
    }

    @SubscribeEvent
    public void onChestForeground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!Reference.onWorld) return;
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        // draw name field
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 500);
        if (nameField != null) nameField.drawTextBox();
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        if (e.getSlotId() == SAVE_SLOT) {
            e.setCanceled(true);
            if (waitingForSkillPointData) return;
            nameField = new GuiTextFieldWynn(200, McIf.mc().fontRenderer, 8, 5, 130, 10);
            nameField.setFocused(true);
            nameField.setText("Enter build name");
            Keyboard.enableRepeatEvents(true);
            skipResettingSkillPointData = true;
        } else if (e.getSlotId() == LOAD_SLOT) {
            e.setCanceled(true);
            if (waitingForSkillPointData) return;
            McIf.mc().displayGuiScreen(
                    new SkillPointLoadoutUI(this, McIf.mc().currentScreen,
                            new InventoryBasic("Skill Points Loadouts", false, 54)));
            skipResettingSkillPointData = true;
        }
    }

    @SubscribeEvent
    public void onPlayerInfoUpdated(PacketEvent<SPacketSetSlot> e) {
        ItemStack newStack = e.getPacket().getStack();
        if (!waitingForSkillPointData || !newStack.hasDisplayName() || !newStack.getDisplayName().endsWith("'s Info")) return;

        // Send click, either to progress pages or to put pages back to 1
        // Done immediately so Wynn has time to respond
        CPacketClickWindow packet = new CPacketClickWindow(e.getPacket().getWindowId(), PLAYER_INFO_SLOT, 0,
                ClickType.PICKUP, newStack, playerInfoInventorySlots.getNextTransactionID(McIf.player().inventory));
        McIf.mc().getConnection().sendPacket(packet);

        // Get current page, should be 2 or higher since we triggered first page in onSlotClicked
        Matcher pageMatcher = PLAYER_INFO_PAGE_PATTERN.matcher(ItemUtils.getStringLore(newStack));
        int currentPage = 0;
        if (pageMatcher.find()) {
            currentPage = Integer.parseInt(pageMatcher.group(1));
        }
        if (currentPage < 2) return;

        // Get total pages
        int pages = (int) ItemUtils.getStringLore(newStack).chars().filter(num -> num == '■').count();
        // Add the skill point gear info into gearSkillPoints
        addGearSPModifiers(newStack);
        // Determine if we need to go the next page:
        if (currentPage == pages) { // page == max pages now
            waitingForSkillPointData = false;
        }
    }

    private void addGearSPModifiers(ItemStack itemStack) {
        for (String line : ItemUtils.getLore(itemStack)) {
            String unformattedLine = TextFormatting.getTextWithoutFormattingCodes(line);
            for (int i = 0; i < 5; i++) {
                Matcher m = MODIFIER_PATTERNS[i].matcher(unformattedLine);
                if (!m.find()) continue;

                int modifier = Integer.parseInt(m.group(1));
                gearSkillPoints[i] += modifier;
            }
        }
    }

    @SubscribeEvent
    public void onMouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        int offsetMouseX = e.getMouseX() - e.getGui().getGuiLeft();
        int offsetMouseY = e.getMouseY() - e.getGui().getGuiTop();

        // handle mouse input on name editor
        if (nameField == null) return;

        nameField.mouseClicked(offsetMouseX, offsetMouseY, e.getMouseButton());
        if (e.getMouseButton() == MouseButton.LEFT.ordinal()) { // left click
            if (nameField.isFocused()) {
                nameField.setCursorPositionEnd();
                nameField.setSelectionPos(0);
                return;
            }

            nameField.setSelectionPos(nameField.getCursorPosition());
        }
    }

    @SubscribeEvent
    public void onKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        // handle typing in text boxes
        if (nameField == null || !nameField.isFocused()) return;

        if (e.getKeyCode() == Keyboard.KEY_RETURN) {
            String name = nameField.getText();
            nameField = null;

            name = name.replaceAll("&([a-f0-9k-or])", "§$1");
            UtilitiesConfig.INSTANCE.skillPointLoadouts.put(name, getSkillPoints(e.getGui()));
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1f));
        } else if (e.getKeyCode() == Keyboard.KEY_ESCAPE) {
            nameField = null;
            loadedBuild = null;
            buildPercentage = 0.0f;
        } else {
            nameField.textboxKeyTyped(e.getTypedChar(), e.getKeyCode());
        }

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void removeLoadPling(PacketEvent<SPacketCustomSound> e) {
        if (!Reference.onWorld || loadedBuild == null) return;
        if (!e.getPacket().getSoundName().equalsIgnoreCase("entity.experience_orb.pickup")) return;

        e.setCanceled(true);
    }


    private SkillPointAllocation getSkillPoints(ChestReplacer gui) {
        int[] sp = new int[5];

        for (int i = 0; i < 5; i++) {
            ItemStack stack = gui.getLowerInv().getStackInSlot(11 + i); // sp indicators start at 11
            Matcher m = SKILLPOINT_PATTERN.matcher(ItemUtils.getStringLore(stack));
            if (!m.find()) continue;

            sp[i] = Integer.parseInt(m.group(1));
            sp[i] -= gearSkillPoints[i];
        }

        return new SkillPointAllocation(sp[0], sp[1], sp[2], sp[3], sp[4]);
    }

    public void loadBuild(SkillPointAllocation build) {
        if (build.getTotalSkillPoints() > skillPointsRemaining) {
            TextComponentString text = new TextComponentString("Not enough free skill points!");
            text.getStyle().setColor(TextFormatting.RED);

            McIf.player().sendMessage(text);
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
            return;
        }

        loadedBuild = build;
        startBuild = true;
    }

    private void allocateSkillPoints(ChestReplacer gui) {
        if (loadedBuild == null) return;
        if (gui.inventorySlots.getSlot(11).getStack().isEmpty()) return;
        itemsLoaded = true;


        if (!gotInitialSp) {
            currentSp = getSkillPoints(gui).getAsArray();
            gotInitialSp = true;
        }

        int[] buildSp = loadedBuild.getAsArray();

        float perSkill = 1 / (float) loadedBuild.getTotalSkillPoints();

        for (int i = 0; i < 5; i++) {
            startBuild = true;
            if (currentSp[i] >= buildSp[i]) continue;
            ClickType clickType = (buildSp[i] - currentSp[i] >= 5) ? ClickType.QUICK_MOVE : ClickType.PICKUP; // shift click if difference >= 5 for efficiency
            int spAdded = (clickType == ClickType.QUICK_MOVE) ? 5 : 1;
            buildPercentage += perSkill * (spAdded);

            CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, 11 + i, 0,
                    clickType, gui.inventorySlots.getSlot(11 + i).getStack(),
                    gui.inventorySlots.getNextTransactionID(McIf.player().inventory));

            McIf.mc().getSoundHandler().playSound(
                    PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ITEM_PICKUP, 0.3f + (1.2f * buildPercentage)));

            McIf.mc().getConnection().sendPacket(packet);
            currentSp[i] += spAdded;
            return; // can only click once at a time
        }
        startBuild = false;
        gotInitialSp = false;

        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f));
        loadedBuild = null; // we've fully loaded the build if we reach this point
        buildPercentage = 0.0f;
    }

    @SubscribeEvent
    public void onRenderItemOverlay(RenderEvent.DrawItemOverlay e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(McIf.mc().currentScreen)) return;

        ItemStack stack = e.getStack();
        if (stack.isEmpty() || !stack.hasDisplayName()) return; // display name also checks for tag compound

        String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
        String value = e.getOverlayText();

        if (!name.contains("Upgrade")) return; // Skill Points

        Matcher spm = SKILLPOINT_PATTERN.matcher(ItemUtils.getStringLore(stack));
        if (!spm.find()) return;

        SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
        if (skillPoint != null) {
            value = spm.group(1);

            if (UtilitiesConfig.Items.INSTANCE.colorSkillPointNumberOverlay) e.setOverlayTextColor(MinecraftChatColors.fromTextFormatting(skillPoint.getColor()));

            ScreenRenderer.beginGL(e.getX(), e.getY());
            {
                GlStateManager.translate(0, 0, 500);
                RenderHelper.disableStandardItemLighting();
                renderer.drawString(skillPoint.getColoredSymbol(),  2, 0, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                RenderHelper.enableGUIStandardItemLighting();
            }
            ScreenRenderer.endGL();
        }

        e.setOverlayText(value);
    }
}
