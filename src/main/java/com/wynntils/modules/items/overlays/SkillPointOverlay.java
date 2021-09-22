/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
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
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillPointOverlay implements Listener {

    public static final Pattern SKILLPOINT_PATTERN = Pattern.compile(".*?([-0-9]+)(?=\\spoints).*");
    private static final Pattern[] MODIFIER_PATTERNS = {
            Pattern.compile("- Strength: ([-+0-9]+)"),
            Pattern.compile("- Dexterity: ([-+0-9]+)"),
            Pattern.compile("- Intelligence: ([-+0-9]+)"),
            Pattern.compile("- Defence: ([-+0-9]+)"),
            Pattern.compile("- Agility: ([-+0-9]+)"),
    };

    private static final int SAVE_SLOT = 1;
    private static final int LOAD_SLOT = 3;

    private final ScreenRenderer renderer = new ScreenRenderer();

    private GuiTextFieldWynn nameField;

    private int skillPointsRemaining;
    private SkillPointAllocation loadedBuild = null;
    private boolean startBuild = false;
    private boolean itemsLoaded = false;
    private float buildPercentage = 0.0f;

    @SubscribeEvent
    public void onChestClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        nameField = null;
        itemsLoaded = false;

        Keyboard.enableRepeatEvents(false);
    }

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        Matcher m = Utils.CHAR_INFO_PAGE_TITLE.matcher(e.getGui().getLowerInv().getName());
        if (!m.find()) return;

        skillPointsRemaining = Integer.parseInt(m.group(1));

        // load/save loadout items
        ItemStack save = new ItemStack(Items.WRITABLE_BOOK);
        save.setStackDisplayName(TextFormatting.GOLD + "[>] Save current loadout");
        ItemUtils.replaceLore(save, Arrays.asList(TextFormatting.GRAY + "Allows you to save this loadout with a name."));
        e.getGui().inventorySlots.getSlot(SAVE_SLOT).putStack(save);

        ItemStack load = new ItemStack(Items.ENCHANTED_BOOK);
        load.setStackDisplayName(TextFormatting.GOLD + "[>] Load loadout");
        ItemUtils.replaceLore(load, Arrays.asList(TextFormatting.GRAY + "Allows you to load one of your saved loadouts."));
        e.getGui().inventorySlots.getSlot(LOAD_SLOT).putStack(load);

        // skill point allocating
        if (!itemsLoaded || startBuild) {
            startBuild = false;
            allocateSkillPoints(e.getGui());
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            String name = TextFormatting.getTextWithoutFormattingCodes(s.getStack().getDisplayName());
            SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
            if (skillPoint == null) continue;

            ScreenRenderer.beginGL(e.getGui().getGuiLeft() , e.getGui().getGuiTop());
            {
                GlStateManager.translate(0, 0, 251);
                RenderHelper.disableStandardItemLighting();
                renderer.drawString(skillPoint.getColoredSymbol(), s.xPos + 2, s.yPos, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }
    }

    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        if (e.getSlotId() == SAVE_SLOT) {
            nameField = new GuiTextFieldWynn(200, McIf.mc().fontRenderer, 8, 5, 130, 10);
            nameField.setFocused(true);
            nameField.setText("Enter build name");
            Keyboard.enableRepeatEvents(true);

            e.setCanceled(true);
        } else if (e.getSlotId() == LOAD_SLOT) {
            McIf.mc().displayGuiScreen(
                    new SkillPointLoadoutUI(this, McIf.mc().currentScreen,
                            new InventoryBasic("Skill Points Loadouts", false, 54))
            );

            e.setCanceled(true);
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
            ItemStack stack = gui.getLowerInv().getStackInSlot(i + 9); // sp indicators start at 9
            Matcher m = SKILLPOINT_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack)));
            if (!m.find()) continue;

            sp[i] = Integer.parseInt(m.group(1));
        }

        // following code subtracts gear sp from total sp to find player-allocated sp
        ItemStack info = gui.getLowerInv().getStackInSlot(6); // player info slot
        for (String line : ItemUtils.getLore(info)) {
            String unformattedLine = TextFormatting.getTextWithoutFormattingCodes(line);
            for (int i = 0; i < 5; i++) {
                Matcher m = MODIFIER_PATTERNS[i].matcher(unformattedLine);
                if (!m.find()) continue;

                int modifier = Integer.parseInt(m.group(1));
                sp[i] -= modifier;
            }
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
        if (gui.inventorySlots.getSlot(9).getStack().isEmpty()) return;
        itemsLoaded = true;

        int[] currentSp = getSkillPoints(gui).getAsArray();
        int[] buildSp = loadedBuild.getAsArray();

        float perSkill = 1 / (float) loadedBuild.getTotalSkillPoints();

        for (int i = 0; i < 5; i++) {
            if (currentSp[i] >= buildSp[i]) continue;
            int button = (buildSp[i] - currentSp[i] >= 5) ? 1 : 0; // right click if difference >= 5 for efficiency

            buildPercentage += perSkill * (button == 1 ? 5 : 0);

            CPacketClickWindow packet = new CPacketClickWindow(gui.inventorySlots.windowId, 9 + i, button,
                    ClickType.PICKUP, gui.inventorySlots.getSlot(9 + i).getStack(),
                    gui.inventorySlots.getNextTransactionID(McIf.player().inventory));

            McIf.mc().getSoundHandler().playSound(
                    PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_ITEM_PICKUP, 0.3f + (1.2f * buildPercentage)));

            McIf.mc().getConnection().sendPacket(packet);
            return; // can only click once at a time
        }

        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f));
        loadedBuild = null; // we've fully loaded the build if we reach this point
        buildPercentage = 0.0f;
    }

}
