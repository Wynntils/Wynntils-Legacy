/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemIdentificationOverlay implements Listener {

    private final static Pattern BRACKETS = Pattern.compile("\\[.*?\\]");
    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");
    public final static String E = new String(new char[]{(char) 0xB2}), B = new String(new char[]{(char) 0xBD}), L = new String(new char[]{(char) 0xBC});

    @SubscribeEvent
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty() && e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack());
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack());
        }
    }

    @SubscribeEvent
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        if (Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty() && e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack());
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack());
        }
    }

    @SubscribeEvent
    public void onHorseInventory(GuiOverlapEvent.HorseOverlap.DrawScreen e) {
        if (Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty() && e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack());
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack());
        }
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

        List<String> lore = Utils.getLore(stack);

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
        if (igp == null || !igp.getItems().containsKey(itemType)) {
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

    public static void drawHoverItem(ItemStack stack) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("verifiedWynntils")) return;

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

            if(wColor.matches(".*(Mythic|Legendary|Rare|Unique|Set) Item.*")) {
                int rerollValue = 0;

                //thanks nbcss for this Math
                if(wColor.contains("Mythics")) {
                    rerollValue = (int)Math.ceil(90.0D + wItem.getLevel() * 18);
                }else if(wColor.contains("Legendary")) {
                    rerollValue = (int)Math.ceil(30.0D + wItem.getLevel() * 6);
                }else if(wColor.contains("Rare")) {
                    rerollValue = (int)Math.ceil(10.0D + wItem.getLevel() * 1.4d);
                }else if(wColor.contains("Set")) {
                    rerollValue = (int)Math.ceil(10.0D + wItem.getLevel() * 1.6d);
                }else if(wColor.contains("Unique")) {
                    rerollValue = (int)Math.ceil(3.0D + wItem.getLevel() * 0.5d);
                }

                int alreadyRolled = 1;
                Matcher m = BRACKETS.matcher(wColor);
                if(m.find()) {
                    alreadyRolled = Integer.valueOf(m.group().replace("[", "").replace("]", ""));
                }

                for(int bb = 1; bb <= alreadyRolled; bb++) rerollValue *= 5;

                actualLore.set(i, lore + " §a[" + decimalFormat.format(rerollValue) + E + "]");
                break;
            }

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
        nbt.setBoolean("verifiedWynntils", true);
        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();

        actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        display.setTag("Lore", tag);
        nbt.setTag("display", display);
        stack.setTagCompound(nbt);
    }

}
