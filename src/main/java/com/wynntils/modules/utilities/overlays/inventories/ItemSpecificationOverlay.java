/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.profiles.item.enums.ItemType;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemSpecificationOverlay implements Listener {

    private final ScreenRenderer renderer = new ScreenRenderer();

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

            String destinationName = null;
            CustomColor color = null;
            int xOffset = 2;
            float scale = 1f;

            if (UtilitiesConfig.Items.INSTANCE.unidentifiedSpecification) {
                Pattern unidentifiedItem = Pattern.compile("^§.Unidentified (.*)");
                Matcher m = unidentifiedItem.matcher(name);
                if (m.find()) {
                    ItemType type = ItemType.from(m.group(1));
                    if (type != null) {
                        // Draw an icon representing the type on top
                        ScreenRenderer.beginGL(0, 0);
                        GlStateManager.translate(0, 0, 270);

                        RenderHelper.disableStandardItemLighting();
                        float scaleFactor = 0.75f;
                        ScreenRenderer.scale(scaleFactor);
                        renderer.drawRect(Textures.UIs.hud_overlays, (int)((gui.getGuiLeft() + s.xPos) / scaleFactor) + 3, (int)((gui.getGuiTop() + s.yPos) / scaleFactor) + 3 , type.getTextureX(), type.getTextureY(), 16, 16);
                        ScreenRenderer.endGL();
                    } else {
                        // This is an un-id:ed but named item
                        destinationName = "";
                        color = MinecraftChatColors.GRAY;
                    }
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.potionSpecification) {
                if (name.startsWith("§aPotion of §")) {
                    SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
                    destinationName = skillPoint.getSymbol();
                    color = MinecraftChatColors.fromTextFormatting(skillPoint.getColor());
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.keySpecification) {
                Pattern dungeonKey = Pattern.compile("§6(.*) Key");
                Matcher m = dungeonKey.matcher(name);
                if (m.find() && lore.get(0).equals("§7Grants access to the")) {
                    destinationName = m.group(1).substring(0, 1);
                    color = MinecraftChatColors.GOLD;
                }

                Pattern brokenDungeonKey = Pattern.compile("Broken (.*) Key");
                Matcher m2 = brokenDungeonKey.matcher(name);
                if (m2.find()) {
                    destinationName = m2.group(1).substring(0, 1);
                    color = MinecraftChatColors.DARK_RED;
                }

                // I'm not sure if this happens anymore, but keep it for now
                Pattern corruptedDungeonKey = Pattern.compile("§4(?:Broken )?(?:Corrupted )?(.*) Key");
                Matcher m3 = corruptedDungeonKey.matcher(name);
                if (m3.find()) {
                    destinationName = m3.group(1).substring(0, 1);
                    color = MinecraftChatColors.DARK_RED;
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.transportationSpecification) {
                Pattern boatPass = Pattern.compile("§b(.*) (?:Boat )?Pass");
                Matcher m = boatPass.matcher(name);
                if (m.find() && lore.get(0).equals("§7Use this at the §fV.S.S. Seaskipper")) {
                    destinationName = m.group(1).substring(0, 1);
                    color = MinecraftChatColors.BLUE;
                }

                Pattern cityTeleport = Pattern.compile("^§b(.*) Teleport Scroll$");
                Matcher m2 = cityTeleport.matcher(name);
                if (m2.find()) {
                    destinationName = m2.group(1);
                    if (destinationName.equals("Dungeon")) {
                        color = MinecraftChatColors.GOLD;
                        for (String loreLine : lore) {
                            Pattern dungeonTeleport = Pattern.compile("§3- §7Teleports to: §f(.*)");
                            Matcher m3 = dungeonTeleport.matcher(StringUtils.normalizeBadString(loreLine));
                            if (m3.find()) {
                                // Make sure we print "F" for "the Forgery"
                                destinationName = m3.group(1).replace("the ", "");
                                break;
                            }
                        }
                    } else {
                        color = MinecraftChatColors.AQUA;
                    }
                    destinationName = destinationName.substring(0, 1);
                }
            }

            if (UtilitiesConfig.Items.INSTANCE.amplifierSpecification) {
                Pattern amp = Pattern.compile("^§bCorkian Amplifier (I{1,3})$");
                Matcher m = amp.matcher(name);
                if (m.matches()) {
                    destinationName = m.group(1);
                    color = MinecraftChatColors.AQUA;
                    xOffset = -1;
                    scale = 0.8f;
                }
            }

            if (destinationName != null) {
                ScreenRenderer.beginGL((int) (gui.getGuiLeft()/scale), (int) (gui.getGuiTop()/scale));
                GlStateManager.translate(0, 0, 260);
                GlStateManager.scale(scale, scale, 1);
                RenderHelper.disableStandardItemLighting();
                // Make a modifiable copy
                color = new CustomColor(color);
                color.setA(0.8f);
                renderer.drawString(destinationName, (s.xPos + xOffset)/scale, (s.yPos + 1)/scale, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
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
