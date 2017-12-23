package com.wynndevs.market.guis;

import com.wynndevs.market.profiles.ItemDataProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;

public class WMGuiScreen extends GuiScreen {

    public Minecraft mc;

    HashMap<Integer, ItemDataProfile> renderedItems = new HashMap<>();
    public ItemDataProfile compar;

    public boolean requestClean = false;

    public WMGuiScreen(Minecraft mc) {
        this.mc = mc;
    }

    public void drawString(String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(mc.fontRenderer, text, x, y, color);
        GL11.glScalef(mSize,mSize,mSize);
    }

    public void drawString(String text, int x, int y, int color) {
        this.drawString(mc.fontRenderer, text, x, y, color);
    }

    public void drawItemStack(ItemDataProfile data, int id) {
        if(requestClean) {
            renderedItems.clear();
            requestClean = false;
        }

        if(!renderedItems.containsKey(id)) {
            renderedItems.put(id, data);
        }

        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(data.getItem(), data.getX(), data.getY());
    }

    public void drawItemStack(ItemStack stack, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawString(isShiftKeyDown() ? "SHIFT" : "" + (isCtrlKeyDown() ? "CTRL" : ""), mouseX, mouseY + 10, -1);

        for(int y : renderedItems.keySet()) {
            ItemDataProfile x = renderedItems.get(y);
            if(mouseX > x.getX() - 2 && mouseX < x.getX() + 16 && mouseY > x.getY() - 2 && mouseY < x.getY() + 16) {
                if(isCtrlKeyDown() && x.getCtrlLore().size() >= 1) {
                    drawHoveringText(x.getCtrlLore(), mouseX, mouseY);
                    if(compar != null) {
                        drawHoveringText(compar.getCtrlLore(), mouseX - 180, mouseY);
                    }
                }else if(isShiftKeyDown() && x.getShiftLore().size() >= 1) {
                    drawHoveringText(x.getShiftLore(), mouseX, mouseY);
                    if(compar != null) {
                        drawHoveringText(compar.getShiftLore(), mouseX - 180, mouseY);
                    }
                }else{
                    drawHoveringText(x.getDefaultLore(), mouseX, mouseY);
                    if(compar != null) {
                        drawHoveringText(compar.getDefaultLore(), mouseX - 180, mouseY);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(int y : renderedItems.keySet()) {
            ItemDataProfile x = renderedItems.get(y);

            if(mouseX > x.getX() - 2 && mouseX < x.getX() + 16 && mouseY > x.getY() - 2 && mouseY < x.getY() + 16) {
                if(isCtrlKeyDown()) {
                    if(compar != null && compar.getX() == x.getX() && compar.getY() == x.getY()) {
                        compar = null;
                        break;
                    }
                    compar = x;
                    break;
                }
                if(x.getRunnable() != null) {
                    x.getRunnable().userClicked(this, x);
                }
                break;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
