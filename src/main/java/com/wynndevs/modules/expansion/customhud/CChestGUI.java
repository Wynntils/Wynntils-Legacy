package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ConfigValues;
import com.wynndevs.modules.market.utils.MarketUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class CChestGUI extends GuiChest {

    IInventory lowerInv;

    public CChestGUI(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);

        this.lowerInv = lowerInv;
    }


    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0, 10, 0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);

            int amount = -1;
            int floor = 0;
            for(int i = 0; i <= lowerInv.getSizeInventory(); i++) {
                ItemStack is = lowerInv.getStackInSlot(i);

                amount++;
                if(amount >= 8) {
                    amount = -1;
                    floor++;
                }

                if(is == null || is.isEmpty() || !is.hasDisplayName()) {
                    continue;
                }

                double r; double g; double b; float alpha;

                String lore = getStringLore(is);

                if(lore.contains("§bLegendary") && ConfigValues.inventoryConfig.highlightLegendary) {
                    r = 0; g = 1; b = 1; alpha = .4f;
                }else if(lore.contains("§5Mythic") && ConfigValues.inventoryConfig.highlightMythic) {
                    r = 0.3; g = 0; b = 0.3; alpha = .6f;
                }else if(lore.contains("§dRare") && ConfigValues.inventoryConfig.highlightRare) {
                    r = 1; g = 0; b = 1; alpha = .4f;
                }else if(lore.contains("§eUnique") && ConfigValues.inventoryConfig.highlightUnique) {
                    r = 1; g = 1; b = 0; alpha = .4f;
                }else if(lore.contains("§aSet") && ConfigValues.inventoryConfig.highlightSet) {
                    r = 0; g = 1; b = 0; alpha = .4f;
                }else{
                    continue;
                }

                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glColor4d(r, g, b, alpha);
                    GL11.glVertex2f(24 + (18 * amount) , 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), 24 + (18 * floor));
                    GL11.glVertex2f(24 + (18 * amount), 24 + (18 * floor));
                }
                GL11.glEnd();
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();

        if(!ConfigValues.inventoryConfig.allowEmeraldCount) {
            return;
        }

        int blocks = 0;
        int liquid = 0;
        int emeralds = 0;

        for(int i = 0; i < lowerInv.getSizeInventory(); i++) {
            ItemStack it = lowerInv.getStackInSlot(i);
            if(it == null || it.isEmpty()) {
                continue;
            }

            if(it.getItem() == Items.EMERALD) {
                emeralds+= it.getCount();
                continue;
            }
            if(it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                blocks+= it.getCount();
                continue;
            }
            if(it.getItem() == Items.EXPERIENCE_BOTTLE) {
                liquid+= it.getCount();
                continue;
            }
        }

        int money = (liquid * 4096) + (blocks * 64) + emeralds;

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            String value = "$" + CInventoryGUI.decimalFormat.format(money);
            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 20 + lowerInv.getSizeInventory() * 2, 4210752);
        }else{

            int leAmount = (int)Math.floor(money / 4096);
            money-= leAmount * 4096;

            int blockAmount = (int)Math.floor(money / 64);
            money-= blockAmount * 64;

            String value = "$" + (leAmount > 0 ? leAmount + "LE " : "") + (blockAmount > 0 ? blockAmount + "EB " : "") + (money > 0 ? money + "E" : "");
            if(value.equalsIgnoreCase("$")) {
                value = "$0";
            }

            if(value.substring(value.length() - 1).equalsIgnoreCase(" ")) {
                value = value.substring(0, value.length() - 1);
            }

            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 20 + lowerInv.getSizeInventory() * 2, 4210752);
        }

        GlStateManager.enableLighting();
    }

    public String getStringLore(ItemStack is) {
        String toReturn = "";

        for(String x : MarketUtils.getLore(is)) {
            toReturn+=x;
        }

        return toReturn;
    }

}
