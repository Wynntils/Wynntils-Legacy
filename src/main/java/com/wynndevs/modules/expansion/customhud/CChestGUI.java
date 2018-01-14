package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Utils;
import com.wynndevs.modules.market.utils.MarketUtils;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.item.ItemGuessProfile;
import com.wynndevs.webapi.profiles.item.ItemProfile;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
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
import java.util.Arrays;
import java.util.List;

public class CChestGUI extends GuiChest {

    IInventory lowerInv;
    IInventory upperInv;

    public CChestGUI(IInventory upperInv, IInventory lowerInv){
        super(upperInv, lowerInv);

        this.lowerInv = lowerInv;
        this.upperInv = upperInv;


    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot slot = getSlotUnderMouse();
        if (mc.player.inventory.getItemStack().isEmpty() && slot != null && slot.getHasStack()) {
            drawHoverItem(slot.getStack());
            drawHoverGuess(slot.getStack());
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(0, 10, 0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            int amount = -1;
            int floor = 0;
            for (int i = 0; i <= lowerInv.getSizeInventory(); i++) {
                ItemStack is = lowerInv.getStackInSlot(i);

                amount++;
                if (amount > 8) {
                    amount = 0;
                    floor++;
                }

                if (is == null || is.isEmpty() || !is.hasDisplayName()) {
                    continue;
                }

                double r, g, b;
                float alpha;

                String lore = getStringLore(is);

                if (this.lowerInv.getName().contains("skill points remaining") && lore.contains("points")) {
                    lore = lore.replace("§", "");
                    String[] tokens = lore.split("[0-9]{1,3} points");
                    for (int j = 0; j <= tokens.length - 1; j++) {
                        lore = lore.replace(tokens[j], "");
                    }
                    String[] numbers = lore.split(" ");
                    int count = Integer.parseInt(numbers[0]);
                    is.setCount(count == 0 ? 1 : count);
                }
                 if (lore.contains("§bLegendary") && ConfigValues.inventoryConfig.chestInv.highlightLegendary) {
                    r = 0; g = 1; b = 1; alpha = .4f;
                } else if (lore.contains("§5Mythic") && ConfigValues.inventoryConfig.chestInv.highlightMythic) {
                    r = 0.3; g = 0; b = 0.3; alpha = .6f;
                } else if (lore.contains("§dRare") && ConfigValues.inventoryConfig.chestInv.highlightRare) {
                    r = 1; g = 0; b = 1; alpha = .4f;
                } else if (lore.contains("§eUnique") && ConfigValues.inventoryConfig.chestInv.highlightUnique) {
                    r = 1; g = 1; b = 0; alpha = .4f;
                } else if (lore.contains("§aSet") && ConfigValues.inventoryConfig.chestInv.highlightSet) {
                    r = 0; g = 1; b = 0; alpha = .4f;
                } else if (lore.contains("§6Epic") && lore.contains("Reward")) { //TODO add to settings
                    r = 1; g = 0.666; b = 0; alpha = .4f;
                } else if (lore.contains("§cGodly") && lore.contains("Reward")) {//TODO add to settings
                    r = 1; g = 0; b = 0; alpha = .6f;
                } else if (lore.contains("§dRare") && lore.contains("Reward")) {//TODO add to settings
                    r = 1; g = 0; b = 1; alpha = .4f;
                } else if (lore.contains("§fCommon") && lore.contains("Reward")) {//TODO add to settings
                    r = 1; g = 1; b = 1; alpha = .4f;
                } else {
                    continue;
                }



                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glColor4d(r, g, b, alpha);
                    GL11.glVertex2f(24 + (18 * amount), 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), 24 + (18 * floor));
                    GL11.glVertex2f(24 + (18 * amount), 24 + (18 * floor));
                }
                GL11.glEnd();
            }
            amount = -1;
            int invfloor = 0;
            boolean accessories = ConfigValues.inventoryConfig.chestInv.highlightAccessories;
            boolean hotbar = ConfigValues.inventoryConfig.chestInv.highlightHotbar;
            boolean main = ConfigValues.inventoryConfig.chestInv.highlightMain;
            for (int i = 0; i <= upperInv.getSizeInventory(); i++) {
                ItemStack is = upperInv.getStackInSlot(i);
//                System.out.println(upperInv.getDisplayName().getUnformattedText());

                amount++;
                if (amount > 8) {
                    amount = 0;
                    floor++;
                    invfloor++;
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
                    //Render armor rarity background in chest GUI
                    continue;
                }
                if (invfloor == 0) {
                    offset = 71;
                }

                if (is == null || is.isEmpty() || !is.hasDisplayName()) {
                    continue;
                }

                double r, g, b;
                float alpha;

                String lore = getStringLore(is);

                if (lore.contains("Reward")) {
                    continue;
                } else if (lore.contains("§bLegendary") && ConfigValues.inventoryConfig.chestInv.highlightLegendary) {
                    r = 0;
                    g = 1;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§5Mythic") && ConfigValues.inventoryConfig.chestInv.highlightMythic) {
                    r = 0.3;
                    g = 0;
                    b = 0.3;
                    alpha = .6f;
                } else if (lore.contains("§dRare") && ConfigValues.inventoryConfig.chestInv.highlightRare) {
                    r = 1;
                    g = 0;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§eUnique") && ConfigValues.inventoryConfig.chestInv.highlightUnique) {
                    r = 1;
                    g = 1;
                    b = 0;
                    alpha = .4f;
                } else if (lore.contains("§aSet") && ConfigValues.inventoryConfig.chestInv.highlightSet) {
                    r = 0;
                    g = 1;
                    b = 0;
                    alpha = .4f;
                } else {
                    continue;
                }

                GL11.glBegin(GL11.GL_QUADS);
                {
                    GL11.glColor4d(r, g, b, alpha);
                    GL11.glVertex2f(24 + (18 * amount), offset + 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), offset + 8 + (18 * floor));
                    GL11.glVertex2f(8 + (18 * amount), offset + 24 + (18 * floor));
                    GL11.glVertex2f(24 + (18 * amount), offset + 24 + (18 * floor));
                }
                GL11.glEnd();
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        GL11.glPopMatrix();

        if (!ConfigValues.inventoryConfig.chestInv.allowEmeraldCount) {
            return;
        }

        int blocks = 0;
        int liquid = 0;
        int emeralds = 0;

        for (int i = 0; i < lowerInv.getSizeInventory(); i++) {
            ItemStack it = lowerInv.getStackInSlot(i);
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

        int money = (liquid * 4096) + (blocks * 64) + emeralds;

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

        if(!lowerInv.getName().contains("Quests") && !lowerInv.getName().contains("points")) {
            if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                String value = "^ ²" + CInventoryGUI.decimalFormat.format(money);
                mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 20 + lowerInv.getSizeInventory() * 2, 4210752);
            } else {
                String value;
                if (money == 0) {
                    value = "^ ²0";
                } else {
                    int leAmount = (int) Math.floor(money / 4096);
                    money -= leAmount * 4096;

                    int blockAmount = (int) Math.floor(money / 64);
                    money -= blockAmount * 64;

                    value = "^ " + (leAmount > 0 ? "¼²" + leAmount : "") + (blockAmount > 0 ? " ²½" + blockAmount : "") + (money > 0 ? " ²" + money : "");
                }
                if (value.substring(value.length() - 1).equalsIgnoreCase(" ")) {
                    value = value.substring(0, value.length() - 1);
                }

                mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 20 + lowerInv.getSizeInventory() * 2, 4210752);
            }
        }

        GlStateManager.enableLighting();
    }

    public String getStringLore(ItemStack is){
        StringBuilder toReturn = new StringBuilder();

        for (String x : MarketUtils.getLore(is)) {
            toReturn.append(x);
        }

        return toReturn.toString();
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color){
        GL11.glScalef(size, size, size);
        float mSize = (float) Math.pow(size, -1);
        this.drawString(fontRendererIn, text, Math.round(x / size), Math.round(y / size), color);
        GL11.glScalef(mSize, mSize, mSize);
    }

    public void drawHoverGuess(ItemStack stack){
        if (stack == null || !stack.hasDisplayName() || stack.isEmpty()) {
            return;
        }

        if (!stack.getDisplayName().contains("Unidentified")) {
            return;
        }

        String displayWC = RichUtils.stripColor(stack.getDisplayName());
        String itemType = displayWC.split(" ")[1];
        String level = null;

        List <String> lore = MarketUtils.getLore(stack);

        for (String aLore : lore) {
            if (aLore.contains("Lv. Range")) {
                level = RichUtils.stripColor(aLore).replace("- Lv. Range: ", "");
                break;
            }
        }

        if (itemType == null || level == null) {
            return;
        }

        if (!WebManager.getItemGuesses().containsKey(level)) {
            return;
        }

        ItemGuessProfile igp = WebManager.getItemGuesses().get(level);
        if (!igp.getItems().containsKey(itemType)) {
            return;
        }

        String items = null;
        String color = "§";

        if (stack.getDisplayName().startsWith("§b") && igp.getItems().get(itemType).containsKey("Legendary")) {
            items = igp.getItems().get(itemType).get("Legendary");
            color += "b";
        } else if (stack.getDisplayName().startsWith("§d") && igp.getItems().get(itemType).containsKey("Rare")) {
            items = igp.getItems().get(itemType).get("Rare");
            color += "d";
        } else if (stack.getDisplayName().startsWith("§e") && igp.getItems().get(itemType).containsKey("Unique")) {
            items = igp.getItems().get(itemType).get("Unique");
            color += "e";
        } else if (stack.getDisplayName().startsWith("§5") && igp.getItems().get(itemType).containsKey("Mythic")) {
            items = igp.getItems().get(itemType).get("Mythic");
            color += "5";
        } else if (stack.getDisplayName().startsWith("§a") && igp.getItems().get(itemType).containsKey("Set")) {
            items = igp.getItems().get(itemType).get("Set");
            color += "a";
        }

        if (items != null) {
            if (lore.get(lore.size() - 1).contains("7Possibilities")) {
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

    public void drawHoverItem(ItemStack stack){
        if (!WebManager.getItems().containsKey(RichUtils.stripColor(stack.getDisplayName()))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(RichUtils.stripColor(stack.getDisplayName()));

        if (wItem.isIdentified()) {
            return;
        }

        List <String> actualLore = MarketUtils.getLore(stack);
        for (int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = RichUtils.stripColor(lore);

            if (lore.contains("Set") && lore.contains("Bonus")) {
                break;
            }

            if (!wColor.startsWith("+") && !wColor.startsWith("-")) {
                actualLore.set(i, lore);
                continue;
            }

            String[] values = wColor.split(" ");

            if (values.length < 2) {
                actualLore.set(i, lore);
                continue;
            }

            String pField = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");

            if (pField == null) {
                actualLore.set(i, lore);
                continue;
            }

            boolean raw = !lore.contains("%");

            try {
                int amount = Integer.valueOf(values[0].replace("*", "").replace("%", "").replace("/3s", "").replace("/4s", "").replace("tier ", ""));

                String fieldName;
                if (raw) {
                    fieldName = Utils.getFieldName("raw" + pField);
                    if (fieldName == null) {
                        fieldName = Utils.getFieldName(pField);
                    }
                } else {
                    fieldName = Utils.getFieldName(pField);
                }

                if (fieldName == null) {
                    actualLore.set(i, lore);
                    continue;
                }

                Field f = wItem.getClass().getField(fieldName);
                if (f == null) {
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

                if (amount < 0) percent = 100 - percent;

                if (percent >= 97) {
                    color += "b";
                } else if (percent >= 80) {
                    color += "a";
                } else if (percent >= 30) {
                    color += "e";
                } else {
                    color += "c";
                }

                actualLore.set(i, lore + color + " [" + percent + "%]");

            } catch (Exception ex) {
                actualLore.set(i, lore);
            }
        }

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();

        actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        display.setTag("Lore", tag);
        nbt.setTag("display", display);
        stack.setTagCompound(nbt);

    }

}
