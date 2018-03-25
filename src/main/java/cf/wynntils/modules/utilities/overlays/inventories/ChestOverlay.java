package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
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

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ChestOverlay extends GuiChest {

    IInventory lowerInv;
    IInventory upperInv;

    public ChestOverlay(IInventory upperInv, IInventory lowerInv){
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
            if(getSlotUnderMouse() != null) {
                if(getSlotUnderMouse().slotNumber == 89)
                    GL11.glEnable(GL11.GL_BLEND);
            }

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

                if (lore.contains("§bLegendary") && UtilitiesModule.getMainConfig().legendaryHighlightChest) {
                    r = 0; g = 1; b = 1; alpha = .4f;
                } else if (lore.contains("§5Mythic") && UtilitiesModule.getMainConfig().mythicHighlightChest) {
                    r = 0.3; g = 0; b = 0.3; alpha = .6f;
                } else if (lore.contains("§dRare") && UtilitiesModule.getMainConfig().rareHighlightChest) {
                    r = 1; g = 0; b = 1; alpha = .4f;
                } else if (lore.contains("§eUnique") && UtilitiesModule.getMainConfig().uniqueHighlightChest) {
                    r = 1; g = 1; b = 0; alpha = .4f;
                } else if (lore.contains("§aSet") && UtilitiesModule.getMainConfig().setHighlightChest) {
                    r = 0; g = 1; b = 0; alpha = .4f;
                } else if (lore.contains("§6Epic") && lore.contains("Reward") && UtilitiesModule.getMainConfig().epicEffectsHighlightChest) {
                    r = 1; g = 0.666; b = 0; alpha = .4f;
                } else if (lore.contains("§cGodly") && lore.contains("Reward") && UtilitiesModule.getMainConfig().godlyEffectsHighlightChest) {
                    r = 1; g = 0; b = 0; alpha = .6f;
                } else if (lore.contains("§dRare") && lore.contains("Reward") && UtilitiesModule.getMainConfig().rareEffectsHighlightChest) {
                    r = 1; g = 0; b = 1; alpha = .4f;
                } else if (lore.contains("§fCommon") && lore.contains("Reward") && UtilitiesModule.getMainConfig().commonEffectsHighlightChest) {
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
            boolean accessories = UtilitiesModule.getMainConfig().accesoryHighlightChest;
            boolean hotbar = UtilitiesModule.getMainConfig().hotbarHighlightChest;
            boolean main = UtilitiesModule.getMainConfig().mainHighlightChest;
            for (int i = 0; i <= upperInv.getSizeInventory(); i++) {
                ItemStack is = upperInv.getStackInSlot(i);

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
                } else if (lore.contains("§bLegendary") && UtilitiesModule.getMainConfig().legendaryHighlightChest) {
                    r = 0;
                    g = 1;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§5Mythic") && UtilitiesModule.getMainConfig().mythicHighlightChest) {
                    r = 0.3;
                    g = 0;
                    b = 0.3;
                    alpha = .6f;
                } else if (lore.contains("§dRare") && UtilitiesModule.getMainConfig().rareHighlightChest) {
                    r = 1;
                    g = 0;
                    b = 1;
                    alpha = .4f;
                } else if (lore.contains("§eUnique") && UtilitiesModule.getMainConfig().uniqueHighlightChest) {
                    r = 1;
                    g = 1;
                    b = 0;
                    alpha = .4f;
                } else if (lore.contains("§aSet") && UtilitiesModule.getMainConfig().setHighlightChest) {
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
            if(getSlotUnderMouse() != null) {
                if(getSlotUnderMouse().slotNumber == 89)
                    GL11.glDisable(GL11.GL_BLEND);
            }
        }
        GL11.glPopMatrix();

        if (!UtilitiesModule.getMainConfig().allowEmeraldCountChest) {
            return;
        }

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
        ScreenRenderer screen = new ScreenRenderer();
        if (!lowerInv.getName().contains("Quests") && !lowerInv.getName().contains("points") && !lowerInv.getName().contains("Servers")) {
            lowerInv.setInventorySlotContents(2, new ItemStack(Blocks.BARRIER));
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                ScreenRenderer.beginGL(0, 0);
                {
                    ScreenRenderer.scale(0.9f);
                    ItemStack LWRis = new ItemStack(Items.EMERALD);
                    LWRis.setCount(LWRmoney);
                    screen.drawItemStack(LWRis, 170, 0);

                    ItemStack UPRis = new ItemStack(Items.EMERALD);
                    UPRis.setCount(UPRmoney);
                    screen.drawItemStack(UPRis, 170, 135);
                }
                ScreenRenderer.endGL();
            } else {
                if (LWRmoney != 0) {
                    LWRleAmount = (int) Math.floor(LWRmoney / 4096);
                    LWRmoney -= LWRleAmount * 4096;

                    LWRblockAmount = (int) Math.floor(LWRmoney / 64);
                    LWRmoney -= LWRblockAmount * 64;
                }
                if (UPRmoney != 0) {
                    UPRleAmount = (int) Math.floor(UPRmoney / 4096);
                    UPRmoney -= UPRleAmount * 4096;

                    UPRblockAmount = (int) Math.floor(UPRmoney / 64);
                    UPRmoney -= UPRblockAmount * 64;
                }

                ScreenRenderer.beginGL(0, 0);
                {
                    ScreenRenderer.scale(0.9f);
                    ItemStack LWRliquids = new ItemStack(Items.EXPERIENCE_BOTTLE);
                    LWRliquids.setCount(LWRleAmount);
                    ItemStack LWRblock = new ItemStack(Blocks.EMERALD_BLOCK);
                    LWRblock.setCount(LWRblockAmount);
                    ItemStack LWRemerald = new ItemStack(Items.EMERALD);
                    LWRemerald.setCount(LWRmoney);
                    screen.drawItemStack(LWRliquids, 130, 0, true);
                    screen.drawItemStack(LWRblock, 150, 0, true);
                    screen.drawItemStack(LWRemerald, 170, 0, true);

                    ItemStack UPRliquids = new ItemStack(Items.EXPERIENCE_BOTTLE);
                    UPRliquids.setCount(UPRleAmount);
                    ItemStack UPRblock = new ItemStack(Blocks.EMERALD_BLOCK);
                    UPRblock.setCount(UPRblockAmount);
                    ItemStack UPRemerald = new ItemStack(Items.EMERALD);
                    UPRemerald.setCount(UPRmoney);
                    screen.drawItemStack(UPRliquids, 130, 135, true);
                    screen.drawItemStack(UPRblock, 150, 135, true);
                    screen.drawItemStack(UPRemerald, 170, 135, true);
                }
                ScreenRenderer.endGL();
            }
        }
        GlStateManager.enableLighting();
    }

    public String getStringLore(ItemStack is){
        StringBuilder toReturn = new StringBuilder();

        for (String x : Utils.getLore(is)) {
            toReturn.append(x);
        }

        return toReturn.toString();
    }

    public void drawHoverGuess(ItemStack stack){
        if (stack == null || !stack.hasDisplayName() || stack.isEmpty()) {
            return;
        }

        if (!stack.getDisplayName().contains("Unidentified")) {
            return;
        }

        String displayWC = Utils.stripColor(stack.getDisplayName());
        String itemType = displayWC.split(" ")[1];
        String level = null;

        List <String> lore = Utils.getLore(stack);

        for (String aLore : lore) {
            if (aLore.contains("Lv. Range")) {
                level = Utils.stripColor(aLore).replace("- Lv. Range: ", "");
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
        if (!WebManager.getItems().containsKey(Utils.stripColor(stack.getDisplayName()))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(Utils.stripColor(stack.getDisplayName()));

        if (wItem.isIdentified()) {
            return;
        }

        List <String> actualLore = Utils.getLore(stack);
        for (int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = Utils.stripColor(lore);

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

            String pField = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ").replace("*", "");

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
