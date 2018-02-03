package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.core.utils.Utils;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
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

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class InventoryOverlay extends GuiInventory
{
    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    EntityPlayer player;

    public InventoryOverlay(EntityPlayer player) {
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
        GL11.glTranslatef(0, 10, 0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if(getSlotUnderMouse() != null) {
            if(getSlotUnderMouse().slotNumber == 45 || getSlotUnderMouse().slotNumber == 8)
                GL11.glEnable(GL11.GL_BLEND);
        }

        int amount = -1;
        int extra = 0;
        int floor = 0;
        int armorfloor = 0;
        boolean armorcheck = false;

        boolean accessories = /*ConfigValues.inventoryConfig.playerInv.highlightAccessories*/ true;
        boolean hotbar = /*ConfigValues.inventoryConfig.playerInv.highlightHotbar*/ true;
        boolean armor = /*ConfigValues.inventoryConfig.playerInv.highlightArmor*/ true;
        boolean main = /*ConfigValues.inventoryConfig.playerInv.highlightMain*/ true;


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

            if (lore.contains("Reward") || StringUtils.containsIgnoreCase(lore, "rewards")) {
                continue;
            } else if (lore.contains("§bLegendary") && /*ConfigValues.inventoryConfig.playerInv.highlightLegendary*/ true) {
                r = 0; g = 1; b = 1; alpha = .4f;
            } else if (lore.contains("§5Mythic") && /*ConfigValues.inventoryConfig.playerInv.highlightMythic*/ true) {
                r = 0.3; g = 0; b = 0.3; alpha = .6f;
            } else if (lore.contains("§dRare") && /*ConfigValues.inventoryConfig.playerInv.highlightRare*/ true) {
                r = 1; g = 0; b = 1; alpha = .4f;
            } else if (lore.contains("§eUnique") && /*ConfigValues.inventoryConfig.playerInv.highlightUnique*/ true) {
                r = 1; g = 1; b = 0; alpha = .4f;
            } else if (lore.contains("§aSet") && /*ConfigValues.inventoryConfig.playerInv.highlightSet*/ true) {
                r = 0; g = 1; b = 0; alpha = .4f;
            } else if (floor >= 4) {
                r = 0; g = 0; b = 0; alpha = 0f;
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

        if(getSlotUnderMouse() != null) {
            if(getSlotUnderMouse().slotNumber == 45 || getSlotUnderMouse().slotNumber == 8)
                GL11.glDisable(GL11.GL_BLEND);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();


        /*if (!ConfigValues.inventoryConfig.playerInv.allowEmeraldCount) {
            return;
        }*/

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
            }
        }

        int money = (liquid * 4096) + (blocks * 64) + emeralds;

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            String value = "²" + decimalFormat.format(money);
            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 72, 4210752);
        }else{
            String value;
            if(money == 0) {
                value = "²0";
            } else {
                int leAmount = (int) Math.floor(money / 4096);
                money -= leAmount * 4096;

                int blockAmount = (int) Math.floor(money / 64);
                money -= blockAmount * 64;

                value = (leAmount > 0 ? "¼²" + leAmount : "") + (blockAmount > 0 ? " ²½" + blockAmount  : "") + (money > 0 ? " ²" + money : "");
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

        String displayWC = Utils.stripColor(stack.getDisplayName());
        String itemType = displayWC.split(" ")[1];
        String level = null;

        List<String> lore = Utils.getLore(stack);

        for (String aLore : lore) {
            if (aLore.contains("Lv. Range")) {
                level = Utils.stripColor(aLore).replace("- Lv. Range: ", "");
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
        if(!WebManager.getItems().containsKey(Utils.stripColor(stack.getDisplayName()))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(Utils.stripColor(stack.getDisplayName()));

        if(wItem.isIdentified()) {
            return;
        }

        List<String> actualLore = Utils.getLore(stack);
        for(int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = Utils.stripColor(lore);

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

            String pField = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ").replace("*", "");

            if(pField == null) {
                actualLore.set(i, lore);
                continue;
            }

            boolean raw = !lore.contains("%");

            try{
                int amount = Integer.valueOf(values[0].replace("*", "").replace("%", "").replace("/3s", "").replace("/4s", "").replace("tier ", ""));

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
                }else {
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
        StringBuilder toReturn = new StringBuilder();

        for (String x : Utils.getLore(is)) {
            toReturn.append(x);
        }

        return toReturn.toString();
    }

}
