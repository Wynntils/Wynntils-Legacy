/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChestGui2(GuiOverlapEvent.ChestOverlap.HoveredToolTip.Pre e) {
        if (!Reference.onWorld) return;
        if (!UtilitiesConfig.Items.INSTANCE.transportationSpecification && !UtilitiesConfig.Items.INSTANCE.keySpecification) return;

        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName()) continue; // display name also checks for tag compound

            List<String> lore = ItemUtils.getLore(stack);
            if (lore.isEmpty()) continue;
            String name = StringUtils.normalizeBadString(stack.getDisplayName());

            String destinationName = null;
            CustomColor color = null;

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
                ScreenRenderer.beginGL(e.getGui().getGuiLeft(), e.getGui().getGuiTop());
                GlStateManager.translate(0, 0, 251);
                ScreenRenderer r = new ScreenRenderer();
                RenderHelper.disableStandardItemLighting();
                r.drawString(destinationName.substring(0, 1), s.xPos + 2, s.yPos, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                ScreenRenderer.endGL();
            }
        }
    }
}
