package com.wynndevs.modules.market.guis.screen;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.market.WynnMarket;
import com.wynndevs.modules.market.guis.WMGuiScreen;
import com.wynndevs.modules.market.market.AnnounceProfile;
import com.wynndevs.modules.market.market.WrappedStack;
import com.wynndevs.modules.market.profiles.ItemDataProfile;
import com.wynndevs.modules.market.utils.MarketUtils;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 17/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MarketGUI extends WMGuiScreen {

    private static final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":textures/market-gui.png");
    private static final ResourceLocation gui = new ResourceLocation(Reference.MOD_ID + ":textures/marketplace.png");

    public static ArrayList<String> i = new ArrayList<>();

    int inventory_page = 1;
    int actual_page = 1;

    boolean atStock = false;

    boolean onChangeAnimation = false;
    int value = 35;

    ArrayList<AnnounceProfile> ann = new ArrayList<>();

    boolean requestAnnounces = true;

    String search_box = "";
    long text_flicker = System.currentTimeMillis();
    boolean keepForTime = false;

    //SearchBox handler
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_BACK) {
            if(search_box.length() <= 0) {
                return;
            }
            search_box = search_box.substring(0, search_box.length() - 1);
            requestClean = true;
            text_flicker = System.currentTimeMillis();
            keepForTime = false;
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            search_box = search_box + typedChar;
            text_flicker = System.currentTimeMillis();
            requestClean = true;
            keepForTime = true;
        }
    }

    public MarketGUI() {
        super(Minecraft.getMinecraft());
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int x = width / 2 - 225;
        int y = height / 2 - 15;

        //Next Inventory page
        if(mouseX > x + 118 && mouseX < x + 124 && mouseY > y + 127 && mouseY < y + 137) {
            if(inventory_page < 2) {
                inventory_page++;
                requestClean = true;

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }
        //Previous Inventory page
        if(mouseX > x + 25 && mouseX < x + 32 && mouseY > y + 127 && mouseY < y + 137) {
            if(inventory_page > 1) {
                inventory_page--;
                requestClean = true;

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }
        //To stock button handler
        if(mouseX > x + 34 && mouseX < x + 131 && mouseY > y - 98 && mouseY < y - 71) {
            if(atStock) {
                onChangeAnimation = true;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }
        //To global button handler
        if(mouseX > x + 34 && mouseX < x + 131 && mouseY > y - 63 && mouseY < y - 38) {
            if(!atStock) {
                onChangeAnimation = true;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }
        //Global next page
        if(mouseX > x + 330 && mouseX < x + 337 && mouseY > y + 129 && mouseY < y + 141) {
            int pages = ann.size() <= 100 ? 1 : (int)Math.ceil(ann.size() / 100d);
            if(actual_page < pages) {
                actual_page++;
                requestClean = true;

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }
        //Global previous page
        if(mouseX > x + 247 && mouseX < x + 255 && mouseY > y + 129 && mouseY < y + 141) {
            if(actual_page > 1) {
                actual_page--;
                requestClean = true;

                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        //Changing this value will make a request to update all announces
        if(requestAnnounces) {
            WynnMarket.getMarket().getGlobalAnnounces(array -> {
                if(array != null) {
                    ann.clear();
                    ann.addAll(array);

                    requestClean = true;
                    requestAnnounces = false;
                }
            });
        }

        //dimensions
        int box = 450;

        int x = width / 2 - (box /2);
        int y = height / 2 - 15;

        int pages = ann.size() <= 100 ? 1 : (int)Math.ceil(ann.size() / 100d);

        //rendering gui texture
        GL11.glPushMatrix();
        {
            mc.getTextureManager().bindTexture(gui);

            drawModalRectWithCustomSizedTexture(x - 15, y - 165, 508, 370, 508, 370, 508, 370);
        }
        GL11.glPopMatrix();

        //redering all item slots
        GL11.glPushMatrix();
        {
            GL11.glTranslatef(x, y, 0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);

            int amount = 0;
            int floor = 0;

            //inventory slots
            for(int i = 0; i< 25; i++) {
                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glColor4f(1, 1, 1, 0.3f);
                    GL11.glVertex2f(35 + (25 * amount) , 5 + (25 * floor));
                    GL11.glVertex2f(15 + (25 * amount), 5 + (25 * floor));
                    GL11.glVertex2f(15 + (25 * amount), 25 + (25 * floor));
                    GL11.glVertex2f(35 + (25 * amount), 25 + (25 * floor));
                }
                GL11.glEnd();
                amount++;

                if(amount == 5) {
                    floor++;
                    amount = 0;
                }
            }

            int amount2 = 0;
            int floor2 = 0;

            //market slots
            for(int i = 0; i < (atStock ? 10 : 100); i++) {
                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glColor4f(1, 1, 1, 0.5f);
                    GL11.glVertex2f(193 + (25 * amount2), -120 + (25 * floor2));
                    GL11.glVertex2f(173 + (25 * amount2), -120 + (25 * floor2));
                    GL11.glVertex2f(173 + (25 * amount2), -100 + (25 * floor2));
                    GL11.glVertex2f(193 + (25 * amount2), -100 + (25 * floor2));
                }
                GL11.glEnd();
                amount2++;

                if(amount2 == 10) {
                    floor2++;
                    amount2 = 0;
                }
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();

        //drawing textures
        GlStateManager.pushAttrib();
        {
            GlStateManager.disableLighting();
            mc.getTextureManager().bindTexture(texture);

            if(onChangeAnimation) {
                if(atStock) {
                    if(value < 35) {
                        value++;
                    }else{
                        onChangeAnimation = false;
                        atStock = false;
                        requestClean = true;
                    }
                }else{
                    if(value > 0) {
                        value--;
                    }else{
                        onChangeAnimation = false;
                        atStock = true;
                        requestClean = true;
                    }
                }
            }

            drawTexturedModalRect(x + 15, y - (62 + value), 0, 0, 20, 30);

            drawTexturedModalRect(x + 18, y - 134, 0, 105, 15, 16);

            //global next page button
            if(actual_page < pages) {
                //hover
                if(mouseX > x + 330 && mouseX < x + 337 && mouseY > y + 129 && mouseY < y + 141) {
                    drawTexturedModalRect(x + 330, y + 127, 0, 74, 7, 14);
                }else{
                    drawTexturedModalRect(x + 330, y + 127, 0, 46, 7, 14);
                }
            }else{
                drawTexturedModalRect(x + 330, y + 127, 0, 46, 7, 14);
            }

            //global previus page button
            if(actual_page > 1) {
                //hover
                if(mouseX > x + 247 && mouseX < x + 255 && mouseY > y + 129 && mouseY < y + 141) {
                    drawTexturedModalRect(x + 248, y + 127, 0, 60, 7, 14);
                }else{
                    drawTexturedModalRect(x + 248, y + 127, 0, 32, 7, 14);
                }
            }else{
                drawTexturedModalRect(x + 248, y + 127, 0, 32, 7, 14);
            }

            //inventory next page button
            if(inventory_page < 2) {
                //hover
                if(mouseX > x + 118 && mouseX < x + 124 && mouseY > y + 127 && mouseY < y + 137) {
                    drawTexturedModalRect(x + (26 + 92), y + 125, 0, 74, 7, 14);
                }else {
                    drawTexturedModalRect(x + (26 + 92), y + 125, 0, 46, 7, 14);
                }
            }else{
                drawTexturedModalRect(x + (26 + 92), y + 125, 0, 46, 7, 14);
            }

            //inventory previus page button
            if(inventory_page > 1) {
                //hover
                if(mouseX > x + 25 && mouseX < x + 32 && mouseY > y + 127 && mouseY < y + 137) {
                    drawTexturedModalRect(x + 25, y + 125, 0, 60, 7, 14);
                }else {
                    drawTexturedModalRect(x + 25, y + 125, 0, 32, 7, 14);
                }
            }else{
                drawTexturedModalRect(x + 25, y + 125, 0, 32, 7, 14);
            }

            drawTexturedModalRect(x + 40, y - 95, 0, 121, 21, 32);
            drawTexturedModalRect(x + 40, y - 67, 0, 150, 21, 32);
        }
        GlStateManager.popAttrib();

        //rendering inventoryItems
        int inv_floor = 0;
        int inv_amount = 0;
        int id = 0;

        //there is a problem over there
        //this gets the item by the i(int) value which is pretty bad
        //because some empty slots will appear or useless pages
        for(int i = ((inventory_page - 1) * 25); i < 25 * inventory_page; i++) {
            ItemStack is = mc.player.inventory.getStackInSlot(i);

            if(is == null || is.getItem() == Item.getItemById(0)) {
                continue;
            }

            List<String> iLore = MarketUtils.getLore(is);

            boolean stop = true;
            for(String lr : iLore) {
                if(lr.toLowerCase().contains("lv. min")) {
                    stop = false;
                }
                if(lr.toLowerCase().contains("quest item")) {
                    stop = true;
                    break;
                }
                if(lr.toLowerCase().contains("untradable")) {
                    stop = true;
                    break;
                }
            }

            if(stop) {
                continue;
            }

            ItemDataProfile itempf = new ItemDataProfile(x + 17 + (25 * inv_amount), y + 7 + (25 * inv_floor), is);
            itempf.addDefaultLore(is.getDisplayName());
            itempf.addDefaultLore(iLore);
            itempf.addDefaultLore("");
            itempf.addDefaultLore("§a[Hold shift and click to announce]");

            //adding item runnable
            itempf.addRunnable((gui, item) -> {
                try{
                    if(!isShiftKeyDown()) {
                        return;
                    }

                    String base64 = WrappedStack.getBase64(item.getItem());
                    boolean valid = true;
                    for(AnnounceProfile ann : WynnMarket.getMarket().getAnnounces().values()) {
                        if(ann.getBase64().equals(base64)) {
                            valid = false;
                        }
                    }

                    if(valid) {
                        WynnMarket.getMarket().createAnnounce(Item.getIdFromItem(item.getItem().getItem()), base64, mc.player.getName(), item.getItem().getItemDamage(), (b) -> {
                            if(b) {
                                requestAnnounces = true;
                                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                            }else{
                                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1.0F));
                            }
                        });
                    }else{
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1.0F));
                    }
                }catch (Exception ignored) { }
            });

            drawItemStack(itempf, id++);

            inv_amount++;
            if(inv_amount == 5) {
                inv_amount = 0;
                inv_floor++;
            }
        }

        //rendering market
        if(!atStock) {
            int market_floor = 0;
            int market_amount = 0;
            for(int i = ((actual_page - 1) * 100); i < 100 * actual_page; i++) {
                if(ann.size() <= i) {
                    break;
                }

                AnnounceProfile announce = ann.get(i);
                if(announce == null || announce.getItem() == null) {
                    continue;
                }

                //handling searchbox
                if(search_box.length() > 0 && !RichUtils.stripColor(announce.getItem().getDisplayName().toLowerCase()).startsWith(search_box.toLowerCase())) {
                    continue;
                }

                ItemDataProfile item = new ItemDataProfile(x + 175 + (25 * market_amount), y - 118 + (25 * market_floor), announce.getItem());

                item.addDefaultLore(announce.getItem().getDisplayName());
                item.addDefaultLore(MarketUtils.getLore(announce.getItem()));
                item.addDefaultLore("");
                item.addDefaultLore("§e[Hold shift to see author info]");
                item.addDefaultLore("§a[Hold ctrl and click to compare]");

                item.addShiftLore(announce.getItem().getDisplayName());
                item.addShiftLore("");
                item.addShiftLore("§aOwner: §7" + announce.getOwner());
                item.addShiftLore("§aOwner Server: §7" + announce.getServer());

                drawItemStack(item, id++);

                market_amount++;
                if(market_amount == 10) {
                    market_amount = 0;
                    market_floor++;
                }
            }
        }else{
            int market_amount = 0;
            for(AnnounceProfile n : WynnMarket.getMarket().getAnnounces().values()) {
                if(WynnMarket.getMarket().getAnnounces().size() <= market_amount) {
                    break;
                }

                if(n == null || n.getItem() == null) {
                    continue;
                }

                //handling searchbox
                if(search_box.length() > 0 && !RichUtils.stripColor(n.getItem().getDisplayName().toLowerCase()).startsWith(search_box.toLowerCase())) {
                    continue;
                }

                ItemDataProfile item = new ItemDataProfile(x + 175 + (25 * market_amount), y - 118, n.getItem());
                item.addDefaultLore(n.getItem().getDisplayName());
                item.addDefaultLore(MarketUtils.getLore(n.getItem()));
                item.addDefaultLore("");
                item.addDefaultLore("§a[Hold ctrl and click to compare]");
                item.addDefaultLore("§c[Hold shift and click to delete]");

                //adding item runnable
                item.addRunnable((gui, im) -> {
                    if(isShiftKeyDown()) {
                        WynnMarket.getMarket().deleteAnnounce(n.getId(), (b) -> {
                            if(b) {
                                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
                                requestAnnounces = true;
                            }else{
                                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1.0F));
                            }
                        });
                    }
                });

                drawItemStack(item, id++);

                market_amount++;
            }
        }

        drawString("§lGlobal", x + 75, y - 85, -1);
        drawString("§lPersonal", x + 75, y - 52, -1);

        //searchbox drawing and animation
        if(search_box.length() <= 0) {
            drawString("§7Type to search ", x + 40, y - 128, -1);
        }else{
            if(search_box.length() >= 16) {
                drawString(search_box.substring(search_box.length() - 16), x + 40, y - 128, -1);
            }else{
                if(System.currentTimeMillis() - text_flicker >= 500) {
                    keepForTime = !keepForTime;
                    text_flicker = System.currentTimeMillis();
                }

                if(keepForTime) {
                    drawString(search_box + "_", x + 40, y - 128, -1);
                }else{
                    drawString(search_box, x + 40, y - 128, -1);
                }
            }
        }

        drawString("Page " + inventory_page + "/2", x + 50, y + 129, -1);

        if(atStock) {
            drawString("Page " + "1/1", x + 270, y + 132, -1);
            drawString("Your Announces", x + 253, y - 137, -1);
        }else{
            drawString("Page " + actual_page + "/" + pages, x + 270, y + 132, -1);
            drawString("Global Announces", x + 251, y - 137, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
