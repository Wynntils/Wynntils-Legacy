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
import com.wynntils.core.utils.StringUtils;
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
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.client.gui.GuiScreen.isShiftKeyDown;

public class SkillPointOverlay implements Listener {

    private static final Pattern REMAINING_SKILLPOINT_PATTERN = Pattern.compile("§7You have §a(\\d+)§7 skill points§7to be distributed");
    private static final Pattern SKILLPOINT_PATTERN = Pattern.compile(".*?§7(\\d+) points");
    private static final Pattern[] MODIFIER_PATTERNS = {
            Pattern.compile("§[ac]([+-])(\\d+) §7Strength"),
            Pattern.compile("§[ac]([+-])(\\d+) §7Dexterity"),
            Pattern.compile("§[ac]([+-])(\\d+) §7Intelligence"),
            Pattern.compile("§[ac]([+-])(\\d+) §7Defence"),
            Pattern.compile("§[ac]([+-])(\\d+) §7Agility"),
    };

    private static final int SAVE_SLOT = 3;
    private static final int LOAD_SLOT = 5;
    private static final int SKILL_CRYSTAL_SLOT = 4;

    private final ScreenRenderer renderer = new ScreenRenderer();

    private GuiTextFieldWynn nameField;

    private int skillPointsRemaining;
    private SkillPointAllocation loadedBuild = null;
    private boolean startBuild = false;
    private boolean itemsLoaded = false;
    private float buildPercentage = 0.0f;

    private boolean shouldUpdateSkillPoints = true;
    private int[] currentSPShown = new int[5]; // This should be the skill points that the user sees
    private int[] currentSPWithoutGearTome = new int[5]; // This should be the skill points that the user assigns
    private int[] gearSP = new int[5]; // This should be the skill points that come from armour and accessories
    private int[] tomeAndSetBonusSP = new int[5]; // This should be the skill points that come from tomes and set bonuses

    // 36-39 inclusive is armour, 9-12 inclusive is accessories
    private static final int[] gearSlotsToCheck = {36, 37, 38, 39, 9, 10, 11, 12};
    // 4 is guild tome slot, 11 and 19 are weapon tome slots
    private static final int[] tomeSlotsToCheck = {4, 11, 19};

    @SubscribeEvent
    public void onChestClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        nameField = null;
        itemsLoaded = false;

        Keyboard.enableRepeatEvents(false);
    }

    @SubscribeEvent
    public void onTomePageOpened(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        if (!Utils.isTomePage(e.getGui())) return;

        for (int i : tomeSlotsToCheck) {
            ItemStack itemStack = e.getGui().getLowerInv().getStackInSlot(i);
            if (itemStack.isEmpty()) continue;

            // todo: remove
            System.out.println(itemStack.getDisplayName() + ": " + Arrays.toString(getItemSPModifier(itemStack)));
        }
    }

    @SubscribeEvent
    public void onCharacterInfoOpened(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        if (!Utils.isCharacterInfoPage(e.getGui())) return;

        // Set remaining skill points
        Matcher spMatcher = REMAINING_SKILLPOINT_PATTERN.matcher(ItemUtils.getStringLore(e.getGui().getLowerInv().getStackInSlot(SKILL_CRYSTAL_SLOT)));
        if (!spMatcher.find()) return;
        skillPointsRemaining = Integer.parseInt(spMatcher.group(1));

        if (shouldUpdateSkillPoints) {
            // Reset all skill point data
            currentSPShown = getSkillPoints(e.getGui()).getAsArray();
            gearSP = new int[5];
            tomeAndSetBonusSP = new int[5];
            // currentSPActual now has the skill points shown to the user
            // gearSP and tomeAndSetBonusSP are now empty

            // Set gearSkillPoints to the number of skill points that our armour and accessories give us
            // We will do this by going through all our armour and accessory slots and adding the skill point modifiers to gearSkillPoints
            for (int i : gearSlotsToCheck) {
                ItemStack item = McIf.player().inventory.getStackInSlot(i);
                if (item.isEmpty()) continue;

                int[] itemSPModifier = getItemSPModifier(item);
                for (int j = 0; j < 5; j++) {
                    gearSP[j] += itemSPModifier[j];
                }
            }

            // Now that we have gearSP, we can go check for tomeSkillPoints
            // Get the current number of skill points we have as shown in the current compass menu
            // Then subtract gearSP to get the number of skill points we have from our tomes
            for (int i = 0; i < 5; i++) {
                tomeAndSetBonusSP[i] = currentSPShown[i] - gearSP[i];
            }

            currentSPWithoutGearTome = Arrays.copyOf(currentSPShown, currentSPShown.length);
            for (int i = 0; i < 5; i++) {
                currentSPWithoutGearTome[i] -= gearSP[i];
                currentSPWithoutGearTome[i] -= tomeAndSetBonusSP[i];
            }

            System.out.println("currentSPActual: " + Arrays.toString(currentSPShown));
            System.out.println("gearSP: " + Arrays.toString(gearSP));
            System.out.println("tomeAndSetBonusSP: " + Arrays.toString(tomeAndSetBonusSP));
            System.out.println("currentSPWithoutGearTome: " + Arrays.toString(currentSPWithoutGearTome));

            shouldUpdateSkillPoints = false;
        }

        // load/save loadout items
        ItemStack save = new ItemStack(Items.WRITABLE_BOOK);
        save.setStackDisplayName(TextFormatting.GOLD + "[>] Save current loadout");
        ItemUtils.replaceLore(save, Collections.singletonList(TextFormatting.GRAY + "Allows you to save this loadout with a name."));
        e.getGui().inventorySlots.getSlot(SAVE_SLOT).putStack(save);

        ItemStack load = new ItemStack(Items.ENCHANTED_BOOK);
        load.setStackDisplayName(TextFormatting.GOLD + "[>] Load loadout");
        ItemUtils.replaceLore(load, Collections.singletonList(TextFormatting.GRAY + "Allows you to load one of your saved loadouts."));
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
                if (end == -1) continue;
                value = StringUtils.parseIntOr(lore.substring(start, end), -1);
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ") + 11;
                int end = lore.indexOf("Class: ");
                if (end == -1) continue;
                value = StringUtils.parseIntOr(lore.substring(start, end), -1);
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
                if (end == -1) continue;
                value = StringUtils.parseIntOr(lore.substring(start, end), -1);
            } else continue;
            if (value == -1) continue;
            stack.setCount(value <= 0 ? 1 : value);
        }
    }

    private int[] getItemSPModifier(ItemStack itemStack) {
        int[] sp = new int[5];
        for (int i = 0; i < MODIFIER_PATTERNS.length; i++) {
            Matcher matcher = MODIFIER_PATTERNS[i].matcher(ItemUtils.getStringLore(itemStack).split("Set Bonus")[0]); // Make sure set bonus is not counted
            if (!matcher.find()) continue;

            if (matcher.group(1).equals("+")) {
                sp[i] += Integer.parseInt(matcher.group(2));
            } else {
                sp[i] -= Integer.parseInt(matcher.group(2));
            }
        }
        System.out.println(itemStack.getDisplayName() + ": " + Arrays.toString(sp));
        return sp;
    }

    @SubscribeEvent
    public void onCharacterInfoForeground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
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

        if (e.getSlotId() == SKILL_CRYSTAL_SLOT && isShiftKeyDown()) {
            shouldUpdateSkillPoints = true;
        }

        if (e.getSlotId() == SAVE_SLOT) {
            e.setCanceled(true);
            nameField = new GuiTextFieldWynn(200, McIf.mc().fontRenderer, 8, 5, 130, 10);
            nameField.setFocused(true);
            nameField.setText("Enter build name");
            Keyboard.enableRepeatEvents(true);
        } else if (e.getSlotId() == LOAD_SLOT) {
            e.setCanceled(true);
            McIf.mc().displayGuiScreen(
                    new SkillPointLoadoutUI(this, McIf.mc().currentScreen,
                            new InventoryBasic("Skill Points Loadouts", false, 54)));
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


    /**
    @param gui Must pass the character info GUI.
    @return The skill points in the character info GUI, as displayed to the user.
     */
    private SkillPointAllocation getSkillPoints(ChestReplacer gui) {
        int[] sp = new int[5];

        for (int i = 0; i < 5; i++) {
            ItemStack stack = gui.getLowerInv().getStackInSlot(11 + i); // sp indicators start at 11
            Matcher m = SKILLPOINT_PATTERN.matcher(ItemUtils.getStringLore(stack));
            if (!m.find()) continue;

            sp[i] = Integer.parseInt(m.group(1));
        }

        return new SkillPointAllocation(sp[0], sp[1], sp[2], sp[3], sp[4]);
    }

    public void loadBuild(SkillPointAllocation build) {
        String errorMsg = "";
        if (build.getStrength() < currentSPWithoutGearTome[0] ||
                build.getDexterity() < currentSPWithoutGearTome[1] ||
                build.getIntelligence() < currentSPWithoutGearTome[2] ||
                build.getDefence() < currentSPWithoutGearTome[3] ||
                build.getAgility() < currentSPWithoutGearTome[4]) {
            errorMsg = "You must reset your skill points before loading a build.";
        } else if (build.getStrength() > skillPointsRemaining + currentSPWithoutGearTome[0] ||
                build.getDexterity() > skillPointsRemaining + currentSPWithoutGearTome[1] ||
                build.getIntelligence() > skillPointsRemaining + currentSPWithoutGearTome[2] ||
                build.getDefence() > skillPointsRemaining + currentSPWithoutGearTome[3] ||
                build.getAgility() > skillPointsRemaining + currentSPWithoutGearTome[4]) {
            errorMsg = "You don't have enough skill points to load this build.";
        }

        if (!errorMsg.isEmpty()) {
            TextComponentString text = new TextComponentString(errorMsg);
            text.getStyle().setColor(TextFormatting.RED);

            McIf.player().sendMessage(text);
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
            shouldUpdateSkillPoints = true;
            return;
        }
        loadedBuild = build;
        startBuild = true;
    }

    private void allocateSkillPoints(ChestReplacer gui) {
        if (loadedBuild == null) return;
        if (gui.inventorySlots.getSlot(11).getStack().isEmpty()) return;
        itemsLoaded = true;

        int[] buildSp = loadedBuild.getAsArray();
        float perSkill = 1 / (float) loadedBuild.getTotalSkillPoints();
        shouldUpdateSkillPoints = false;

        for (int i = 0; i < 5; i++) {
            startBuild = true;
            if (currentSPWithoutGearTome[i] >= buildSp[i]) continue;
            ClickType clickType = (buildSp[i] - currentSPWithoutGearTome[i] >= 5) ? ClickType.QUICK_MOVE : ClickType.PICKUP; // shift click if difference >= 5 for efficiency
            int spAdded = (clickType == ClickType.QUICK_MOVE) ? 5 : 1;
            buildPercentage += perSkill * (spAdded);

            CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, 11 + i, 0,
                    clickType, gui.inventorySlots.getSlot(11 + i).getStack(),
                    gui.inventorySlots.getNextTransactionID(McIf.player().inventory));

            McIf.mc().getSoundHandler().playSound(
                    PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ITEM_PICKUP, 0.3f + (1.2f * buildPercentage)));

            McIf.mc().getConnection().sendPacket(packet);
            currentSPWithoutGearTome[i] += spAdded;
            return; // can only click once at a time
        }
        shouldUpdateSkillPoints = true;
        startBuild = false;

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
