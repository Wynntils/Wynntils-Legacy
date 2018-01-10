package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Utils;
import com.wynndevs.modules.market.utils.MarketUtils;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.item.ItemGuessProfile;
import com.wynndevs.webapi.profiles.item.ItemProfile;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class CInventoryGUI extends GuiInventory {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    EntityPlayer player;

    public CInventoryGUI(EntityPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot slot = getSlotUnderMouse();
        if (mc.player.inventory.getItemStack().isEmpty() && slot != null && slot.getHasStack()) {
            drawHoverItem(slot.getStack());
            drawHoverGuess(slot.getStack());
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);


        GL11.glPushMatrix();
        if (true) {
            GL11.glTranslatef(0, 10, 0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            int amount = -1;
            int extra = 0;
            int floor = 0;
            int armorfloor = 0;
            boolean armorcheck = false;

            boolean accessories = ConfigValues.inventoryConfig.playerInv.highlightAccessories;
            boolean hotbar = ConfigValues.inventoryConfig.playerInv.highlightHotbar;
            boolean armor = ConfigValues.inventoryConfig.playerInv.highlightArmor;
            boolean main = ConfigValues.inventoryConfig.playerInv.highlightMain;


            for (int i = 0; i <= player.inventory.getSizeInventory(); i++) {
                ItemStack is = player.inventory.getStackInSlot(i);

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


                double r, g, b;
                float alpha;

                String lore = getStringLore(is);

                if (lore.contains("Reward")) {
                    continue;
                } else if (lore.contains("§bLegendary") && ConfigValues.inventoryConfig.playerInv.highlightLegendary) {
                    r = 0;
                    g = 1;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§5Mythic") && ConfigValues.inventoryConfig.playerInv.highlightMythic) {
                    r = 0.3;
                    g = 0;
                    b = 0.3;
                    alpha = .6f;
                } else if (lore.contains("§dRare") && ConfigValues.inventoryConfig.playerInv.highlightRare) {
                    r = 1;
                    g = 0;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§eUnique") && ConfigValues.inventoryConfig.playerInv.highlightUnique) {
                    r = 1;
                    g = 1;
                    b = 0;
                    alpha = .4f;
                } else if (lore.contains("§aSet") && ConfigValues.inventoryConfig.playerInv.highlightSet) {
                    r = 0;
                    g = 1;
                    b = 0;
                    alpha = .4f;
                } else if (floor >= 4) {
                    r = 0;
                    g = 0;
                    b = 0;
                    alpha = 0f;
                } else {
                    continue;
                }
                int offset = floor == 0 ? 124 : 48;

                if (floor >= 4 && amount < 4 && armor) {
                    armorcheck = true;
                    offset = 62;
                    floor++;
                    armorfloor++;
                } else if (floor >= 4 && amount == 4) {
                    armorcheck = true;
                    extra = 69;
                    offset = 116;
                    floor++;
                } else if (floor >= 4 && amount > 4) {
                    continue;
                }
                if (!armorcheck) {
                    GL11.glBegin(GL11.GL_QUADS);
                    {
                        GL11.glColor4d(r, g, b, alpha);
                        GL11.glVertex2f(24 + (18 * amount), offset + 8 + (18 * floor));
                        GL11.glVertex2f(8 + (18 * amount), offset + 8 + (18 * floor));
                        GL11.glVertex2f(8 + (18 * amount), offset + 24 + (18 * floor));
                        GL11.glVertex2f(24 + (18 * amount), offset + 24 + (18 * floor));
                    }
                    GL11.glEnd();
                } else {
                    GL11.glBegin(GL11.GL_QUADS);
                    {
                        GL11.glColor4d(r, g, b, alpha);
                        GL11.glVertex2f(24 + extra, offset + 8 - (18 * armorfloor));
                        GL11.glVertex2f(8 + extra, offset + 8 - (18 * armorfloor));
                        GL11.glVertex2f(8 + extra, offset + 24 - (18 * armorfloor));
                        GL11.glVertex2f(24 + extra, offset + 24 - (18 * armorfloor));
                    }
                    GL11.glEnd();
                }
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();


        if (!ConfigValues.inventoryConfig.playerInv.allowEmeraldCount) {
            return;
        }

        int blocks = 0;
        int liquid = 0;
        int emeralds = 0;

        for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack it = mc.player.inventory.getStackInSlot(i);
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
            String value = "$" + decimalFormat.format(money);
            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 72, 4210752);
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

            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 72, 4210752);
        }

        GlStateManager.enableLighting();
    }

    public void drawHoverGuess(ItemStack stack) {
        if(stack == null || !stack.hasDisplayName() || stack.isEmpty()) {
            return;
        }

        if(!stack.getDisplayName().contains("Unidentified")) {
            return;
        }

        String displayWC = RichUtils.stripColor(stack.getDisplayName());
        String itemType = displayWC.split(" ")[1];
        String level = null;

        List<String> lore = MarketUtils.getLore(stack);

        for(int i = 0; i< lore.size(); i++) {
            if(lore.get(i).contains("Lv. Range")) {
                level = RichUtils.stripColor(lore.get(i)).replace("- Lv. Range: ", "");
                break;
            }
        }

        if(itemType == null || level == null) {
            return;
        }

        if(!WebManager.getItemGuesses().containsKey(level)) {
            return;
        }

        ItemGuessProfile igp = WebManager.getItemGuesses().get(level);
        if(!igp.getItems().containsKey(itemType)) {
            return;
        }

        String items = null;
        String color = "§";

        if(stack.getDisplayName().startsWith("§b") && igp.getItems().get(itemType).containsKey("Legendary")) {
            items = igp.getItems().get(itemType).get("Legendary"); color+="b";
        }else if(stack.getDisplayName().startsWith("§d") && igp.getItems().get(itemType).containsKey("Rare")) {
            items = igp.getItems().get(itemType).get("Rare"); color+="d";
        }else if(stack.getDisplayName().startsWith("§e") && igp.getItems().get(itemType).containsKey("Unique")) {
            items = igp.getItems().get(itemType).get("Unique"); color+="e";
        }else if(stack.getDisplayName().startsWith("§5") && igp.getItems().get(itemType).containsKey("Mythic")) {
            items = igp.getItems().get(itemType).get("Mythic"); color+="5";
        }else if(stack.getDisplayName().startsWith("§a") && igp.getItems().get(itemType).containsKey("Set")) {
            items = igp.getItems().get(itemType).get("Set"); color+="a";
        }

        if(items != null) {
            if(lore.get(lore.size() - 1).contains("7Possibilities")) {
                return;
            }
            lore.add("§a- §7Possibilities: " + color + items);

            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagCompound display = nbt.getCompoundTag("display");
            NBTTagList tag = new NBTTagList();

            lore.forEach(s -> tag.appendTag(new NBTTagString(s)));

            display.setTag("Lore", tag);
            nbt.setTag("display", display);
            stack.setTagCompound(nbt);
        }
    }

    public void drawHoverItem(ItemStack stack) {
        if(!WebManager.getItems().containsKey(RichUtils.stripColor(stack.getDisplayName()))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(RichUtils.stripColor(stack.getDisplayName()));

        if(wItem.isIdentified()) {
            return;
        }

        List<String> actualLore = MarketUtils.getLore(stack);
        for(int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = RichUtils.stripColor(lore);

            if(lore.contains("Set") && lore.contains("Bonus")) {
                break;
            }

            if(!wColor.startsWith("+") && !wColor.startsWith("-")) {
                actualLore.set(i, lore);
                continue;
            }

            String[] values = wColor.split(" ");

            if(values.length < 2) {
                actualLore.set(i, lore);
                continue;
            }

            String pField = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");

            if(pField == null) {
                actualLore.set(i, lore);
                continue;
            }

            boolean raw = !lore.contains("%");

            try{
                int amount = Integer.valueOf(values[0].replace("*", "").replace("%", "").replace("/3s", "").replace("/4s", ""));

                String fieldName;
                if(raw) {
                    fieldName = Utils.getFieldName("raw" + pField);
                    if(fieldName == null) {
                        fieldName = Utils.getFieldName(pField);
                    }
                }else{
                    fieldName = Utils.getFieldName(pField);
                }

                if(fieldName == null) {
                    actualLore.set(i, lore);
                    continue;
                }

                Field f = wItem.getClass().getField(fieldName);
                if(f == null) {
                    actualLore.set(i, lore);
                    continue;
                }

                int itemVal = Integer.valueOf(String.valueOf(f.get(wItem)));
                int min;
                int max;
                if (amount < 0) {
                    max = (int) Math.min(Math.round(itemVal * 1.3d), -1);
                    min = (int) Math.min(Math.round(itemVal * 0.7d), -1);
                } else {
                    max = (int) Math.max(Math.round(itemVal * 1.3d), 1);
                    min = (int) Math.max(Math.round(itemVal * 0.3d), 1);
                }

                if (max == min) {
                    actualLore.set(i, lore);
                    continue;
                }

                double intVal = (double) (max - min);
                double pVal = (double) (amount - min);
                int percent = (int) ((pVal / intVal) * 100);

                String color = "§";

                if(amount < 0) percent = 100 - percent;

                if(percent >= 97) {
                    color += "b";
                }else if(percent >= 80) {
                    color += "a";
                }else if(percent >= 30) {
                    color += "e";
                }else if(percent < 30) {
                    color += "c";
                }

                actualLore.set(i,lore + color + " [" + percent + "%]");

            }catch (Exception ex) { actualLore.set(i, lore); }
        }

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();

        actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        display.setTag("Lore", tag);
        nbt.setTag("display", display);
        stack.setTagCompound(nbt);

    }

    public String getStringLore(ItemStack is){
        String toReturn = "";

        for (String x : MarketUtils.getLore(is)) {
            toReturn += x;
        }

        return toReturn;
    }

}
