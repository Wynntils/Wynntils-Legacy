package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;

public class DrawedGui {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    @SubscribeEvent
    public void onGuiDrawed(GuiScreenEvent.DrawScreenEvent.Post e) {
        if(e.getGui() instanceof GuiInventory) {
            Minecraft mc = ModCore.mc();

            int x = e.getGui().width / 2;
            int y = e.getGui().height / 2;

            int blocks = 0;
            int liquid = 0;
            int emeralds = 0;

            for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
                ItemStack it = mc.player.inventory.getStackInSlot(i);
                if(it == null || it.isEmpty()) {
                    continue;
                }

                if(it.getItem() == Item.getItemById(388)) {
                    emeralds+= it.getCount();
                    continue;
                }
                if(it.getItem() == Item.getItemById(133)) {
                    blocks+= it.getCount();
                    continue;
                }
                if(it.getItem() == Item.getItemById(384)) {
                    liquid+= it.getCount();
                    continue;
                }
            }

            int money = (liquid * 1000) + (blocks * 64) + emeralds;

            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

            if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                String value = "$" + decimalFormat.format(money);
                mc.fontRenderer.drawString(value, x + (80 - mc.fontRenderer.getStringWidth(value)), y - 11, 1);
            }else{

                int leAmount = (int)Math.floor(money / 1000);
                money-= leAmount * 1000;

                int blockAmount = (int)Math.floor(money / 64);
                money-= blockAmount * 64;

                String value = "$" + (leAmount > 0 ? leAmount + "LE " : "") + (blockAmount > 0 ? blockAmount + "EB " : "") + (money > 0 ? money + "E" : "");

                mc.fontRenderer.drawString(value , x + (80 - mc.fontRenderer.getStringWidth(value)), y - 11, 1);
            }
            GlStateManager.enableLighting();
            return;
        }
    }

}
