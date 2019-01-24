/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RarityColorOverlay implements Listener {

    private static final ResourceLocation RESOURCE = new ResourceLocation(Reference.MOD_ID, "textures/overlays/rarity.png");

    int lowerInvFloor;

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        drawChest(e.getGuiInventory().getLowerInv(), e.getGuiInventory().getUpperInv(), e.getGuiInventory().getSlotUnderMouse(), 1, true, true);
    }

    @SubscribeEvent
    public void onHorseInventory(GuiOverlapEvent.HorseOverlap.DrawGuiContainerForegroundLayer e) {
        drawChest(e.getGuiInventory().getUpperInv(), e.getGuiInventory().getLowerInv(), e.getGuiInventory().getSlotUnderMouse(), 53, true, false);
    }

    @SubscribeEvent
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawGuiContainerForegroundLayer e) {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0, 10, 0F);
            GL11.glEnable(GL11.GL_BLEND);

            int amount = -1;
            int extra = 0;
            int floor = 0;
            int armorfloor = 0;
            boolean armorcheck = false;

            boolean accessories = UtilitiesConfig.Items.INSTANCE.accesoryHighlight;
            boolean hotbar = UtilitiesConfig.Items.INSTANCE.hotbarHighlight;
            boolean armor = UtilitiesConfig.Items.INSTANCE.armorHighlight;
            boolean main = UtilitiesConfig.Items.INSTANCE.mainHighlightInventory;


            for (int i = 0; i <= e.getGuiInventory().getPlayer().inventory.getSizeInventory(); i++) {
                ItemStack is = e.getGuiInventory().getPlayer().inventory.getStackInSlot(i);

                amount++;
                if (amount > 8) {
                    amount = 0;
                    floor++;
                }


                if (!accessories && floor == 1 && amount < 4) {
                    continue;
                }
                if (!hotbar && floor == 0) {
                    continue;
                }
                if (!main) {
                    if (floor == 1 && amount > 3) {
                        continue;
                    } else if (floor != 1 && floor < 4 && floor != 0) {
                        continue;
                    }
                }


                float r, g, b;

                String lore = Utils.getStringLore(is);
                String name = is.getDisplayName();

                if (is.getCount() == 0) {
                    continue;
                } else if (lore.contains("Reward") || StringUtils.containsIgnoreCase(lore, "rewards")) {
                    continue;
                } else if (lore.contains("§bLegendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
                    r = 0; g = 1; b = 1;
                } else if (lore.contains("§5Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
                    r = 0.3f; g = 0; b = 0.3f;
                } else if (lore.contains("§dRare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
                    r = 1; g = 0; b = 1;
                } else if (lore.contains("§eUnique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
                    r = 1; g = 1; b = 0;
                } else if (lore.contains("§aSet") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
                    r = 0; g = 1; b = 0;
                } else if (lore.contains("§fNormal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
                    r = 1; g = 1; b = 1;
                } else if (name.endsWith("§6 [§e✫§8✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                    r = 1; g = 0.97f; b = 0.6f;
                } else if (name.endsWith("§6 [§e✫✫§8✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                    r = 1; g = 1; b = 0;
                } else if (name.endsWith("§6 [§e✫✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight && !(is.getCount() == 0)) {
                    r = 0.9f; g = 0.3f; b = 0;
                } else if (isPowder(is) && UtilitiesConfig.Items.INSTANCE.powderHighlight) {
                    if (getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier)
                        continue;
                    r = getPowderColor(is)[0];
                    g = getPowderColor(is)[1];
                    b = getPowderColor(is)[2];
                } else if (floor >= 4) {
                    armorfloor++;
                    continue;
                } else {
                    continue;
                }
                int offset = floor == 0 ? 124 : 48;

                if (floor >= 4 && amount < 4 && armor) {
                    armorcheck = true;
                    offset = 62;
                    floor++;
                    armorfloor++;
                } else if (floor >= 4 && amount == 4 && armor) {
                    armorcheck = true;
                    extra = 69;
                    offset = 116;
                    floor++;
                } else if ((floor >= 4 && amount == 4) || (floor >= 4 && amount < 4) || (floor >= 4 && amount > 4) && !armor) {
                    continue;
                }

                if (!armorcheck) {
                    int x = 8 + (18 * amount), y = offset + 8 + (18 * floor);
                    GL11.glPushMatrix();
                    {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
                        GlStateManager.color(r, g, b, 1.0f);
                        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                        float zoom = 2.0f;
                        float factor = (16.0f + zoom * 2) / 16.0f;
                        GL11.glTranslatef(x - zoom, y - zoom, 0.0f);
                        GL11.glScalef(factor, factor, 0);
                        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    GL11.glPopMatrix();
                    //XY: 8 + (18 * amount), offset + 8 + (18 * floor)
                } else {
                    int x = 8 + extra, y = offset + 8 - (18 * armorfloor);
                    GL11.glPushMatrix();
                    {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
                        GlStateManager.color(r, g, b, 1.0f);
                        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                        float zoom = 2.0f;
                        float factor = (16.0f + zoom * 2) / 16.0f;
                        GL11.glTranslatef(x - zoom, y - zoom, 0.0f);
                        GL11.glScalef(factor, factor, 0);
                        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    GL11.glPopMatrix();
                    //XY: 8 + extra, offset + 8 - (18 * armorfloor)
                }
            }
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();


        if (UtilitiesConfig.Items.INSTANCE.emeraldCountInventory) {
            final String E = new String(new char[]{(char) 0xB2}), B = new String(new char[]{(char) 0xBD}), L = new String(new char[]{(char) 0xBC});

            int blocks = 0, liquid = 0, emeralds = 0;

            for (int i = 0; i < Minecraft.getMinecraft().player.inventory.getSizeInventory(); i++) {
                ItemStack it = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
                if (it == null || it.isEmpty()) {
                    continue;
                }

                if (it.getItem() == Items.EMERALD) {
                    emeralds += it.getCount();
                    continue;
                }
                if (it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                    blocks += it.getCount();
                    continue;
                }
                if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                    liquid += it.getCount();
                }
            }

            int money = (liquid * 4096) + (blocks * 64) + emeralds, leAmount = 0, blockAmount = 0;

            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
            ScreenRenderer screen = new ScreenRenderer();
            int x = 190;
            int y = 80;
//            CustomColor emeraldColor = CustomColor.fromString("595959", 1);
            CustomColor emeraldColor = new CustomColor(77f / 255f, 77f / 255f, 77f / 255f, 1);
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                ScreenRenderer.beginGL(0, 0);
                {
                    ScreenRenderer.scale(0.9f);
                    String moneyText = ItemIdentificationOverlay.decimalFormat.format(money) + E;
                    screen.drawString(moneyText, x, y, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                }
                ScreenRenderer.endGL();

            } else {
                if (money != 0) {
                    leAmount = (int) Math.floor(money / 4096);
                    money -= leAmount * 4096;

                    blockAmount = (int) Math.floor(money / 64);
                    money -= blockAmount * 64;
                }
                ScreenRenderer.beginGL(0, 0);
                {
                    ScreenRenderer.scale(0.9f);
                    String moneyText = ItemIdentificationOverlay.decimalFormat.format(leAmount) + L + E + " " + ItemIdentificationOverlay.decimalFormat.format(blockAmount) + E + B + " " + ItemIdentificationOverlay.decimalFormat.format(money) + E;
                    screen.drawString(moneyText, x, y, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                }
                ScreenRenderer.endGL();
            }
        }
    }

    public void drawChest(IInventory lowerInv, IInventory upperInv, Slot slot, int scaleOfset, boolean emeraldsUpperInv, boolean emeraldsLowerInv) {
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0, 10, 0F);
            GL11.glEnable(GL11.GL_BLEND);

            int amount = -1;
            int floor = 0;
            for (int i = 0; i <= lowerInv.getSizeInventory(); i++) {
                ItemStack is = lowerInv.getStackInSlot(i);

                amount++;
                if (amount > 8) {
                    amount = 0;
                    floor++;
                }

                if (is.isEmpty() || !is.hasDisplayName()) {
                    continue;
                }

                String lore = Utils.getStringLore(is);
                String name = is.getDisplayName();

                float r, g, b;

                if (is.getCount() == 0) {
                    continue;
                } else if (lore.contains("§bLegendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
                    r = 0; g = 1; b = 1;
                } else if (lore.contains("§5Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
                    r = 0.3f; g = 0; b = 0.3f;
                } else if (lore.contains("§dRare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
                    r = 1; g = 0; b = 1;
                } else if (lore.contains("§eUnique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
                    r = 1; g = 1; b = 0;
                } else if (lore.contains("§aSet") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
                    r = 0; g = 1; b = 0;
                } else if (lore.contains("§fNormal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
                    r = 1; g = 1; b = 1;
                } else if (lore.contains("§6Epic") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.epicEffectsHighlight) {
                    r = 1; g = 0.666f; b = 0;
                } else if (lore.contains("§cGodly") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.godlyEffectsHighlight) {
                    r = 1; g = 0; b = 0;
                } else if (lore.contains("§dRare") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.rareEffectsHighlight) {
                    r = 1; g = 0; b = 1;
                } else if (lore.contains("§fCommon") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.commonEffectsHighlight) {
                    r = 1; g = 1; b = 1;
                } else if (lore.contains("§4 Black Market") && lore.contains("Reward") && UtilitiesConfig.Items.INSTANCE.blackMarketEffectsHighlight) {
                    r = 0; g = 0; b = 0;
                } else if (name.endsWith("§6 [§e✫§8✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 1; g = 0.97f; b = 0.6f;
                } else if (name.endsWith("§6 [§e✫✫§8✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 1; g = 1; b = 0;
                } else if (name.endsWith("§6 [§e✫✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 0.9f; g = 0.3f; b = 0;
                } else if (isPowder(is) && UtilitiesConfig.Items.INSTANCE.powderHighlight) {
                    if (getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier)
                        continue;
                    r = getPowderColor(is)[0];
                    g = getPowderColor(is)[1];
                    b = getPowderColor(is)[2];
                } else if (floor >= 4) {
                    continue;
                } else {
                    continue;
                }
                int x = 8 + (18 * amount), y = 8 + (18 * floor) + scaleOfset;
                GL11.glPushMatrix();
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
                    GlStateManager.color(r, g, b, 1.0f);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                    float zoom = 2.0f;
                    float factor = (16.0f + zoom * 2) / 16.0f;
                    GL11.glTranslatef(x - zoom, y - zoom, 0.0f);
                    GL11.glScalef(factor, factor, 0);
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                }
                GL11.glPopMatrix();
            }

            lowerInvFloor = floor;

            amount = -1;
            int invfloor = 0;
            boolean accessories = UtilitiesConfig.Items.INSTANCE.accesoryHighlight;
            boolean hotbar = UtilitiesConfig.Items.INSTANCE.hotbarHighlight;
            boolean main = UtilitiesConfig.Items.INSTANCE.mainHighlightChest;
            for (int i = 0; i <= upperInv.getSizeInventory(); i++) {
                ItemStack is = upperInv.getStackInSlot(i);

                amount++;
                if (amount > 8) {
                    amount = 0;
                    floor++;
                    invfloor++;
                }

                if (is == null || is.isEmpty() || !is.hasDisplayName()) {
                    continue;
                }

                int offset = -5;

                if (!accessories && invfloor == 1 && amount < 4) {
                    continue;
                }
                if (!hotbar && invfloor == 0) {
                    continue;
                }
                if (!main) {
                    if (invfloor == 1 && amount > 3) {
                        continue;
                    } else if (invfloor != 1 && invfloor < 4 && invfloor != 0) {
                        continue;
                    }
                }

                if (invfloor >= 4) {
                    continue;
                }
                if (invfloor == 0) {
                    offset = 71;
                }

                offset+= scaleOfset;

                float r, g, b;

                String lore = Utils.getStringLore(is);
                String name = is.getDisplayName();

                if (is.getCount() == 0) {
                    continue;
                } else if (lore.contains("Reward") || StringUtils.containsIgnoreCase(lore, "rewards")) {
                    continue;
                } else if (lore.contains("§bLegendary") && UtilitiesConfig.Items.INSTANCE.legendaryHighlight) {
                    r = 0; g = 1; b = 1;
                } else if (lore.contains("§5Mythic") && UtilitiesConfig.Items.INSTANCE.mythicHighlight) {
                    r = 0.3f; g = 0; b = 0.3f;
                } else if (lore.contains("§dRare") && UtilitiesConfig.Items.INSTANCE.rareHighlight) {
                    r = 1; g = 0; b = 1;
                } else if (lore.contains("§eUnique") && UtilitiesConfig.Items.INSTANCE.uniqueHighlight) {
                    r = 1; g = 1; b = 0;
                } else if (lore.contains("§aSet") && UtilitiesConfig.Items.INSTANCE.setHighlight) {
                    r = 0; g = 1; b = 0;
                } else if (lore.contains("§fNormal") && UtilitiesConfig.Items.INSTANCE.normalHighlight) {
                    r = 1; g = 1; b = 1;
                } else if (name.endsWith("§6 [§e✫§8✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 1; g = 0.97f; b = 0.6f;
                } else if (name.endsWith("§6 [§e✫✫§8✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 1; g = 1; b = 0;
                } else if (name.endsWith("§6 [§e✫✫✫§6]") && UtilitiesConfig.Items.INSTANCE.ingredientHighlight) {
                    r = 0.9f; g = 0.3f; b = 0;
                } else if (isPowder(is) && UtilitiesConfig.Items.INSTANCE.powderHighlight) {
                    if (getPowderTier(is) < UtilitiesConfig.Items.INSTANCE.minPowderTier)
                        continue;
                    r = getPowderColor(is)[0];
                    g = getPowderColor(is)[1];
                    b = getPowderColor(is)[2];
                } else if (floor >= 4) {
                    continue;
                } else {
                    continue;
                }

                int x = 8 + (18 * amount), y = offset + 8 + (18 * floor);

                GL11.glPushMatrix();
                {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(RESOURCE);
                    GlStateManager.color(r, g, b, 1.0f);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                    float zoom = 2.0f;
                    float factor = (16.0f + zoom * 2) / 16.0f;
                    GL11.glTranslatef(x - zoom, y - zoom, 0.0f);
                    GL11.glScalef(factor, factor, 0);
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 16, 16, 16, 16);
                    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                }
                GL11.glPopMatrix();
            }
            GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glPopMatrix();

        if (UtilitiesConfig.Items.INSTANCE.emeraldCountChest) {
            if (!lowerInv.getName().contains("Quests") && !lowerInv.getName().contains("points") && !lowerInv.getName().contains("Servers")) {
                int LWRblocks = 0, LWRliquid = 0, LWRemeralds = 0, LWRleAmount = 0, LWRblockAmount = 0;
                int UPRblocks = 0, UPRliquid = 0, UPRemeralds = 0, UPRleAmount = 0, UPRblockAmount = 0;

                for (int i = 0; i < lowerInv.getSizeInventory(); i++) {
                    ItemStack it = lowerInv.getStackInSlot(i);
                    if (it == null || it.isEmpty()) {
                        continue;
                    }

                    if (it.getItem() == Items.EMERALD) {
                        LWRemeralds += it.getCount();
                        continue;
                    }
                    if (it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                        LWRblocks += it.getCount();
                        continue;
                    }
                    if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                        LWRliquid += it.getCount();
                    }
                }
                for (int i = 0; i < upperInv.getSizeInventory(); i++) {
                    ItemStack it = upperInv.getStackInSlot(i);
                    if (it == null || it.isEmpty()) {
                        continue;
                    }

                    if (it.getItem() == Items.EMERALD) {
                        UPRemeralds += it.getCount();
                        continue;
                    }
                    if (it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                        UPRblocks += it.getCount();
                        continue;
                    }
                    if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                        UPRliquid += it.getCount();
                    }
                }

                int LWRmoney = (LWRliquid * 4096) + (LWRblocks * 64) + LWRemeralds;
                int UPRmoney = (UPRliquid * 4096) + (UPRblocks * 64) + UPRemeralds;


                GlStateManager.disableLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

                GlStateManager.disableLighting();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                ScreenRenderer screen = new ScreenRenderer();
                int x = 190;
                int y = (int) (lowerInvFloor * 19.7) + 25;
                CustomColor emeraldColor = new CustomColor(77f / 255f, 77f / 255f, 77f / 255f, 1);
                //LWR INV
                if (emeraldsLowerInv) {
                    if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        ScreenRenderer.beginGL(0, 0);
                        {
                            ScreenRenderer.scale(0.9f);
                            String moneyText = ItemIdentificationOverlay.decimalFormat.format(LWRmoney) + ItemIdentificationOverlay.E;
                            screen.drawString(moneyText, x, 6, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                        }
                        ScreenRenderer.endGL();

                    } else {
                        if (LWRmoney != 0) {
                            LWRleAmount = (int) Math.floor(LWRmoney / 4096);
                            LWRmoney -= LWRleAmount * 4096;

                            LWRblockAmount = (int) Math.floor(LWRmoney / 64);
                            LWRmoney -= LWRblockAmount * 64;
                        }
                        ScreenRenderer.beginGL(0, 0);
                        {
                            ScreenRenderer.scale(0.9f);
                            String moneyText = ItemIdentificationOverlay.decimalFormat.format(LWRleAmount) + ItemIdentificationOverlay.L + ItemIdentificationOverlay.E + " " + ItemIdentificationOverlay.decimalFormat.format(LWRblockAmount) + ItemIdentificationOverlay.E + ItemIdentificationOverlay.B + " " + ItemIdentificationOverlay.decimalFormat.format(LWRmoney) + ItemIdentificationOverlay.E;
                            screen.drawString(moneyText, x, 6, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                        }
                        ScreenRenderer.endGL();
                    }
                }

                //UPR INV
                if (emeraldsUpperInv) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        ScreenRenderer.beginGL(0, 0);
                        {
                            ScreenRenderer.scale(0.9f);
                            String moneyText = ItemIdentificationOverlay.decimalFormat.format(UPRmoney) + ItemIdentificationOverlay.E;
                            screen.drawString(moneyText, x, y, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                        }
                        ScreenRenderer.endGL();

                    } else {
                        if (UPRmoney != 0) {
                            UPRleAmount = (int) Math.floor(UPRmoney / 4096);
                            UPRmoney -= UPRleAmount * 4096;

                            UPRblockAmount = (int) Math.floor(UPRmoney / 64);
                            UPRmoney -= UPRblockAmount * 64;
                        }
                        ScreenRenderer.beginGL(0, 0);
                        {
                            ScreenRenderer.scale(0.9f);
                            String moneyText = ItemIdentificationOverlay.decimalFormat.format(UPRleAmount) + ItemIdentificationOverlay.L + ItemIdentificationOverlay.E + " " + ItemIdentificationOverlay.decimalFormat.format(UPRblockAmount) + ItemIdentificationOverlay.E + ItemIdentificationOverlay.B + " " + ItemIdentificationOverlay.decimalFormat.format(UPRmoney) + ItemIdentificationOverlay.E;
                            screen.drawString(moneyText, x, y, emeraldColor, SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NONE);
                        }
                        ScreenRenderer.endGL();
                    }

                    GlStateManager.enableLighting();
                }
            }
        }
    }

    private boolean isPowder(ItemStack is) {
        return (is.hasDisplayName() && is.getDisplayName().contains("Powder") && Utils.stripColor(Utils.getStringLore(is)).contains("Effect on Weapons"));
    }

    private int getPowderTier(ItemStack is) {
        if (is.getDisplayName().endsWith("III")) {
            return 3;
        } else if (is.getDisplayName().endsWith("IV")) {
            return 4;
        } else if (is.getDisplayName().endsWith("VI")) {
            return 6;
        } else if (is.getDisplayName().endsWith("V")) {
            return 5;
        } else if (is.getDisplayName().endsWith("II")) {
            return 2;
        } else {
            return 1;
        }
    }

    private float[] getPowderColor(ItemStack is) {
        float[] returnVal;
        if (is.getDisplayName().startsWith("§e")) {
            // Lightning
            returnVal = new float[]{1f, 1f, 0.333f};
        } else if (is.getDisplayName().startsWith("§b")) {
            // Water
            returnVal = new float[]{0.333f, 1f, 1f};
        } else if (is.getDisplayName().startsWith("§f")) {
            // Air
            returnVal = new float[]{1f, 1f, 1f};
        } else if (is.getDisplayName().startsWith("§2")) {
            // Earth
            returnVal = new float[]{0f, 0.666f, 0f};
        } else {
            // Fire
            returnVal = new float[]{1f, 0.333f, 0.333f};
        }
        return returnVal;
    }
}
