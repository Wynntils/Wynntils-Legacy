/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemSpecificationOverlay implements Listener {

    private final ScreenRenderer renderer = new ScreenRenderer();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDrawItem(RenderEvent.RenderItem e) {
        if (!Reference.onWorld) return;

        ItemStack stack = e.getStack();
        if (stack.isEmpty() || !stack.hasDisplayName()) return; // display name also checks for tag compound

        List<String> lore = ItemUtils.getLore(stack);
        String name = stack.getDisplayName();

        String destinationName = null;
        CustomColor color = null;
        int xOffset = 2;
        //TODO test
        float scale = 1f;

        if (ItemsConfig.ItemHighlights.INSTANCE.unidentifiedSpecification) {
            Pattern unidentifiedItem = Pattern.compile("^§.Unidentified (.*)");
            Matcher m = unidentifiedItem.matcher(name);
            if (m.find()) {
                ItemType type = ItemType.from(m.group(1));
                if (type != null) {
                    // Draw an icon representing the type on top
                    ScreenRenderer.beginGL(0, 0);

                    RenderHelper.disableStandardItemLighting();
                    float scaleFactor = 0.75f;
                    ScreenRenderer.scale(scaleFactor);
                    renderer.drawRect(Textures.UIs.hud_overlays, 3, 3, type.getTextureX(), type.getTextureY(), 16, 16);
                    ScreenRenderer.endGL();
                } else {
                    // This is an un-id:ed but named item
                    destinationName = "";
                    color = MinecraftChatColors.GRAY;
                }
            }
        }

        if (ItemsConfig.ItemHighlights.INSTANCE.potionSpecification) {
            if (name.startsWith("§aPotion of §")) {
                SkillPoint skillPoint = SkillPoint.findSkillPoint(name);
                destinationName = skillPoint.getSymbol();
                color = MinecraftChatColors.fromTextFormatting(skillPoint.getColor());
            }
        }

        if (ItemsConfig.ItemHighlights.INSTANCE.keySpecification) {
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

        if (ItemsConfig.ItemHighlights.INSTANCE.transportationSpecification) {
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
                        Matcher m3 = dungeonTeleport.matcher(loreLine);
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

        if (ItemsConfig.ItemHighlights.INSTANCE.amplifierSpecification) {
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
            ScreenRenderer.scale(scale);
            RenderHelper.disableStandardItemLighting();
            // Make a modifiable copy
            color = new CustomColor(color);
            color.setA(0.8f);
            renderer.drawString(destinationName, xOffset, 1, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
            ScreenRenderer.resetScale();
        }
    }
}
