/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemSpecificationOverlay implements Listener {

    private void renderOverlay(GuiContainer gui) {
        if (!Reference.onWorld) return;
        if (!UtilitiesConfig.Items.INSTANCE.transportationSpecification && !UtilitiesConfig.Items.INSTANCE.keySpecification) return;

        for (Slot s : gui.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName()) continue; // display name also checks for tag compound

            List<String> lore = ItemUtils.getLore(stack);
            if (lore.isEmpty()) continue;
            String name = StringUtils.normalizeBadString(stack.getDisplayName());

            String destinationName = null;
            CustomColor color = null;

            if (UtilitiesConfig.Items.INSTANCE.potionSpecification) {
                if (name.startsWith("§aPotion of §")) {
                    SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
                    destinationName = skillPoint.getSymbol();
                    color = MinecraftChatColors.fromTextFormatting(skillPoint.getColor());
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.keySpecification) {
                Pattern dungeonKey = Pattern.compile("§6(.*) Key");
                Matcher m3 = dungeonKey.matcher(name);
                if (m3.find() && lore.get(0).equals("§7Grants access to the")) {
                    destinationName = m3.group(1);
                    color = MinecraftChatColors.GOLD;
                }

                Pattern corruptedDungeonKey = Pattern.compile("§4(?:Broken )?Corrupted (.*) Key");
                Matcher m5 = corruptedDungeonKey.matcher(name);
                if (m5.find() && (lore.get(0).equals("§7Grants access to the") || lore.get(0).equals("§7Use this item at the"))) {
                    destinationName = m5.group(1);
                    color = MinecraftChatColors.DARK_RED;
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.transportationSpecification) {
                Pattern boatPass = Pattern.compile("§b(.*) (?:Boat )?Pass");
                Matcher m4 = boatPass.matcher(name);
                if (m4.find() && lore.get(0).equals("§7Use this at the §fV.S.S. Seaskipper")) {
                    destinationName = m4.group(1);
                    color = MinecraftChatColors.BLUE;
                }

                Pattern cityTeleport = Pattern.compile("^§b(.*) Teleport Scroll$");
                Matcher m = cityTeleport.matcher(name);
                if (m.find()) {
                    destinationName = m.group(1);
                    if (destinationName.equals("Dungeon")) {
                        color = MinecraftChatColors.GOLD;
                        for (String loreLine : lore) {
                            Pattern dungeonTeleport = Pattern.compile("§3- §7Teleports to: §f(.*)");
                            Matcher m2 = dungeonTeleport.matcher(StringUtils.normalizeBadString(loreLine));
                            if (m2.find()) {
                                // Make sure we print "F" for "the Forgery"
                                destinationName = m2.group(1).replace("the ", "");
                                break;
                            }
                        }
                    } else {
                        color = MinecraftChatColors.AQUA;
                    }
                }
            }

            if (destinationName != null) {
                ScreenRenderer.beginGL(gui.getGuiLeft(), gui.getGuiTop());
                GlStateManager.translate(0, 0, 251);
                ScreenRenderer r = new ScreenRenderer();
                RenderHelper.disableStandardItemLighting();
                // Make a modifiable copy
                color = new CustomColor(color);
                color.setA(0.8f);
                r.drawString(destinationName.substring(0, 1), s.xPos + 2, s.yPos + 1, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
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
