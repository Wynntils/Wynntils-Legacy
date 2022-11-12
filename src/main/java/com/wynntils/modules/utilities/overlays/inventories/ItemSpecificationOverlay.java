/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.CorkianAmplifierManager;
import com.wynntils.modules.utilities.managers.DungeonKeyManager;
import com.wynntils.modules.utilities.managers.EmeraldPouchManager;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemSpecificationOverlay implements Listener {

    private final ScreenRenderer renderer = new ScreenRenderer();
    private final Pattern ARCHETYPE_UNLOCKED_PATTERN = Pattern.compile("§7Unlocked Abilities: §f(\\d+)§7/\\d+");
    private final Pattern SKILL_CRYSTAL_PATTERN = Pattern.compile("§7You have §a(\\d+)§7 skill points§7to be distributed§eShift-Click to reset");
    private final Pattern ABILITY_POINT_PATTERN = Pattern.compile("✦ (?:Available|Unused) Points: §f(\\d+)§");

    private void renderOverlay(GuiContainer gui) {
        if (!Reference.onWorld) return;

        for (Slot s : gui.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName()) continue; // display name also checks for tag compound

            List<String> lore = ItemUtils.getLore(stack);
            String name = StringUtils.normalizeBadString(stack.getDisplayName());

            // name and lore fixing
            stack.setStackDisplayName(name);
            List<String> fixedLore = new ArrayList<>();
            for (String line : lore) {
                fixedLore.add(StringUtils.normalizeBadString(line));
            }
            ItemUtils.replaceLore(stack, fixedLore);

            String specificationChars = null;
            CustomColor color = null;
            int xOffset = 2;
            int yOffset = 1;
            float scale = 1f;

            if (UtilitiesConfig.Items.INSTANCE.unidentifiedSpecification) {
                Pattern unidentifiedItem = Pattern.compile("^§.Unidentified (.*)");
                Matcher m = unidentifiedItem.matcher(name);
                if (m.find()) {
                    ItemType type = ItemType.fromString(m.group(1));
                    if (type != null) {
                        // Draw an icon representing the type on top
                        ScreenRenderer.beginGL(0, 0);
                        GlStateManager.translate(0, 0, 270);

                        RenderHelper.disableStandardItemLighting();
                        float scaleFactor = 0.75f;
                        ScreenRenderer.scale(scaleFactor);
                        renderer.drawRect(Textures.UIs.hud_overlays, (int) ((gui.getGuiLeft() + s.xPos) / scaleFactor) + 3, (int) ((gui.getGuiTop() + s.yPos) / scaleFactor) + 3, type.getTextureX(), type.getTextureY(), 16, 16);
                        ScreenRenderer.endGL();
                    } else {
                        // This is an un-id:ed but named item
                        specificationChars = "";
                        color = MinecraftChatColors.GRAY;
                    }
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.potionSpecification) {
                if (name.startsWith("§aPotion of §")) {
                    SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
                    specificationChars = skillPoint.getSymbol();
                    color = MinecraftChatColors.fromTextFormatting(skillPoint.getColor());
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.keySpecification && DungeonKeyManager.isDungeonKey(stack)) {
                DungeonKeyManager.DungeonKey dungeonKey = DungeonKeyManager.getDungeonKey(stack);
                if (dungeonKey != null) {
                    specificationChars = dungeonKey.getAcronym();
                    if (DungeonKeyManager.isCorrupted(stack)) {
                        color = MinecraftChatColors.DARK_RED;
                    } else {
                        color = MinecraftChatColors.GOLD;
                    }
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.transportationSpecification) {
                Pattern boatPass = Pattern.compile("§b(.*) (?:Boat )?Pass");
                Matcher m = boatPass.matcher(name);
                if (m.find() && lore.get(0).equals("§7Use this at the §fV.S.S. Seaskipper")) {
                    specificationChars = m.group(1).substring(0, 1);
                    color = MinecraftChatColors.BLUE;
                }

                Pattern cityTeleport = Pattern.compile("^§b(.*) Teleport Scroll$");
                Matcher m2 = cityTeleport.matcher(name);
                if (m2.find()) {
                    specificationChars = m2.group(1);
                    if (specificationChars.equals("Dungeon")) {
                        color = MinecraftChatColors.GOLD;
                        for (String loreLine : lore) {
                            Pattern dungeonTeleport = Pattern.compile("§3- §7Teleports to: §f(.*)");
                            Matcher m3 = dungeonTeleport.matcher(StringUtils.normalizeBadString(loreLine));
                            if (m3.find()) {
                                // Make sure we print "F" for "the Forgery"
                                specificationChars = m3.group(1).replace("the ", "");
                                break;
                            }
                        }
                    } else {
                        color = MinecraftChatColors.AQUA;
                    }
                    specificationChars = specificationChars.substring(0, 1);
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.powderSpecification) {
                Pattern powder = Powder.POWDER_NAME_PATTERN;
                Matcher m = powder.matcher(StringUtils.normalizeBadString(name));
                if (m.matches()) {
                    if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                        specificationChars = m.group(2);
                    } else {
                        specificationChars = ItemLevelOverlay.romanToArabic(m.group(2));
                    }
                    color = Powder.determineChatColor(m.group(1));
                    xOffset = -1;
                    scale = UtilitiesConfig.Items.INSTANCE.specificationTierSize;
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.amplifierSpecification) {
                if (CorkianAmplifierManager.isAmplifier(stack)) {
                    if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                        specificationChars = CorkianAmplifierManager.getAmplifierTier(stack);
                    } else {
                        specificationChars = ItemLevelOverlay.romanToArabic(CorkianAmplifierManager.getAmplifierTier(stack));
                    }
                    color = MinecraftChatColors.AQUA;
                    xOffset = -1;
                    scale = UtilitiesConfig.Items.INSTANCE.specificationTierSize;
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.emeraldPouchSpecification) {
                if (EmeraldPouchManager.isEmeraldPouch(stack)) {
                    if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                        specificationChars = EmeraldPouchManager.getPouchTier(stack);
                    } else {
                        specificationChars = ItemLevelOverlay.romanToArabic(EmeraldPouchManager.getPouchTier(stack));
                    }
                    color = MinecraftChatColors.GREEN;
                    xOffset = -1;
                    scale = UtilitiesConfig.Items.INSTANCE.specificationTierSize;
                }
            }

            // Specification numbers for skill points remaining
            if (Utils.isCharacterInfoPage(gui) && stack.getDisplayName().equals("§2§lSkill Crystal")) {
                Matcher skillPointMatcher = SKILL_CRYSTAL_PATTERN.matcher(ItemUtils.getStringLore(stack));
                if (skillPointMatcher.find()) {
                    specificationChars = skillPointMatcher.group(1);
                    color = MinecraftChatColors.GREEN;
                    xOffset = 1;
                }
            }

            // Specification numbers for ability points remaining
            // Works on both character info page and ability tree page
            if ((Utils.isAbilityTreePage(gui) || Utils.isCharacterInfoPage(gui)) && stack.getDisplayName().contains("§lAbility ")) {
                Matcher abilityPointMatcher = ABILITY_POINT_PATTERN.matcher(ItemUtils.getStringLore(stack));
                if (abilityPointMatcher.find()) {
                    specificationChars = abilityPointMatcher.group(1);
                    color = MinecraftChatColors.AQUA;
                    xOffset = 1;
                }
            }

            // Specification numbers for archetypes; only draws when >0 on that archetype
            if (stack.getDisplayName().contains("Archetype") && stack.getDisplayName().contains("§l") && stack.getItem() == Items.STONE_AXE) {
                Matcher archetypeMatcher = ARCHETYPE_UNLOCKED_PATTERN.matcher(ItemUtils.getStringLore(stack));
                if (archetypeMatcher.find()) {
                    int archetypeAmount = Integer.parseInt(archetypeMatcher.group(1));
                    if (archetypeAmount > 0) {
                        specificationChars = archetypeMatcher.group(1);
                        color = MinecraftChatColors.fromColorCode(stack.getDisplayName().charAt(1));
                        xOffset = specificationChars.length() > 1 ? 2 : 5; // Alignment for 2-digit numbers
                        yOffset = -10;
                    }
                }
            }

            if (specificationChars != null) {
                ScreenRenderer.beginGL((int) (gui.getGuiLeft() / scale), (int) (gui.getGuiTop() / scale));
                GlStateManager.translate(0, 0, 260);
                GlStateManager.scale(scale, scale, 1);
                RenderHelper.disableStandardItemLighting();
                // Make a modifiable copy
                color = new CustomColor(color);
                color.setA(0.8f);
                renderer.drawString(specificationChars, (s.xPos + xOffset) / scale, (s.yPos + yOffset) / scale, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
                ScreenRenderer.endGL();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        renderOverlay(e.getGui());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInventoryGui(GuiOverlapEvent.InventoryOverlap.HoveredToolTip.Pre e) {
        renderOverlay(e.getGui());
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onHorseGui(GuiOverlapEvent.HorseOverlap.HoveredToolTip.Pre e) {
        renderOverlay(e.getGui());
    }
}
