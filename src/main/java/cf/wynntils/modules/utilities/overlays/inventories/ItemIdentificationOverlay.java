/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.ModCore;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemGuessProfile;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemIdentificationOverlay implements Listener {

    private final static Pattern BRACKETS = Pattern.compile("\\[.*?\\]");
    private final static Pattern ID_PERCENTAGES = Pattern.compile("( \\[\\d{1,3}%\\]$)|( §[abc]§l[\\u21E9\\u21E7\\u21EA]§r§[abc]\\d+\\.\\d+%)");
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

    @SubscribeEvent
    public void InputEventKeyInputEvent(GuiScreenEvent.KeyboardInputEvent e){
        if (!(e.getGui() instanceof GuiContainer) || e.getGui().mc == null || e.getGui().mc.player == null){
            return;
        }
        if (Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty() && ((GuiContainer) e.getGui()).getSlotUnderMouse() != null && ((GuiContainer) e.getGui()).getSlotUnderMouse().getHasStack()) {
            Slot InvSlot = ((GuiContainer) e.getGui()).getSlotUnderMouse();
            if (InvSlot != null) {
                drawHoverItem(((GuiContainer) e.getGui()).getSlotUnderMouse().getStack());
            }
        }
    }

    public void drawHoverGuess(ItemStack stack){
        if (stack == null || !stack.hasDisplayName() || stack.isEmpty()) {
            return;
        }

        if (stack.getDisplayName().contains("Soul Point")) {
            List<String> lore = Utils.getLore(stack);
            if (lore.get(lore.size() - 1).contains("Time until next soul point: ")) {
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
            }
            long worldTimeTicks = ModCore.mc().world.getWorldTime();
            long currentTime = worldTimeTicks % 24000;
            long minutesUntilSoulPoint = Math.floorDiv(currentTime, 1200);
            currentTime -= (minutesUntilSoulPoint * 1200);
            long secondsUntilSoulPoint = Math.floorDiv(currentTime, 20);
            minutesUntilSoulPoint = 20 - minutesUntilSoulPoint - 1;
            secondsUntilSoulPoint = 60 - secondsUntilSoulPoint - 1;
            lore.add("");
            lore.add("§bTime until next soul point: §f" + minutesUntilSoulPoint + ":" + String.format("%02d", secondsUntilSoulPoint));
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagCompound display = nbt.getCompoundTag("display");
            NBTTagList tag = new NBTTagList();
            lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
            display.setTag("Lore", tag);
            nbt.setTag("display", display);
            stack.setTagCompound(nbt);
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
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("verifiedWynntils") && (stack.getTagCompound().getBoolean("extendedStats") == Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) return;
        boolean extended = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

        if (!WebManager.getItems().containsKey(Utils.stripColor(cleanse(stack.getDisplayName())))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(Utils.stripColor(cleanse(stack.getDisplayName())));

        if (wItem.isIdentified()) {
            return;
        }

        int total = 0;
        int identifications = 0;
        double chanceUp = 0;
        double chanceDown = 0;
        double chanceBest = 1;

        List <String> actualLore = Utils.getLore(stack);
        for (int i = 0; i < actualLore.size(); i++) {
            String lore = cleanse(actualLore.get(i));
            String wColor = Utils.stripColor(lore);

            if(wColor.matches(".*(Mythic|Legendary|Rare|Unique|Set) Item.*") && !lore.contains(E)) {
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

                if (extended) {
                    float downPercent;
                    float upPercent;
                    float bestPercent;
                    if (amount < 0){
                        downPercent = 0;
                        upPercent = 0;
                        bestPercent = 0;
                        for (double j = 70;j <= 130; j++){
                            if (Math.round(itemVal * (j/100)) < amount){
                                downPercent++;
                            } else if (Math.round(itemVal * (j/100)) > amount){
                                upPercent++;
                            }
                            if (Math.round(itemVal * (j/100)) == min){
                                bestPercent++;
                            }
                        }
                        downPercent = downPercent / 0.61f;
                        upPercent = upPercent / 0.61f;
                        bestPercent = bestPercent / 0.61f;

                        // Equations for calculating percent chances (not used currently because of a weird offset issue)
                        //downPercent = (amount == max ? 0 : 100 - (float) (((Math.ceil(((amount - 0.5d) / itemVal) * 100) - 70) / 61) * 100));
                        //upPercent = (amount == min ? 0 : (float) (((Math.ceil(((amount + 0.5d) / itemVal) * 100) - 69) / 61) * 100) );
                        //bestPercent = (float) (((Math.ceil(((min * 100d) - 50d) / itemVal) - 70) / 61) * 100);
                    } else {
                        downPercent = 0;
                        upPercent = 0;
                        bestPercent = 0;
                        for (double j = 30;j <= 130; j++){
                            if (Math.round(itemVal * (j/100)) < amount){
                                downPercent++;
                            } else if (Math.round(itemVal * (j/100)) > amount){
                                upPercent++;
                            }
                            if (Math.round(itemVal * (j/100)) == max){
                                bestPercent++;
                            }
                        }
                        downPercent = downPercent / 1.01f;
                        upPercent = upPercent / 1.01f;
                        bestPercent = bestPercent / 1.01f;

                        // Equations for calculating percent chances (not used currently because of a weird offset issue)
                        //downPercent = (amount == min ? 0 : (float) (((Math.ceil(((amount - 0.5d) / itemVal) * 100) - 30) / 101) * 100) );
                        //upPercent =  (amount == max ? 0 : 100 - (float) (((Math.ceil(((amount + 0.5d) / itemVal) * 100) - 30) / 101) * 100));
                        //bestPercent = 100 - (float) (((Math.ceil(((max - 0.5d) / itemVal) * 100) - 30) / 101) * 100);
                    }

                    actualLore.set(i, lore + " §c§l\u21E9§r§c" + String.format("%.1f", downPercent) + "% §a§l\u21E7§r§a" + String.format("%.1f", upPercent) + "% §b§l\u21EA§r§b" + String.format("%.1f", bestPercent) + "%");
                    identifications += 1;

                    chanceUp = chanceUp + ((1 - chanceUp) * (upPercent / 100));
                    chanceDown = chanceDown + ((1 - chanceDown) * (downPercent / 100));
                    chanceBest *= (bestPercent / 100);
                } else {
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
                    total += percent;
                    identifications += 1;
                }


            } catch (Exception ex) {
                actualLore.set(i, lore);
            }
        }

        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound nbt = stack.getTagCompound();
        nbt.setBoolean("verifiedWynntils", true);
        nbt.setBoolean("extendedStats", extended);

        if (identifications > 0) {
            if (extended) {

                NBTTagCompound display = nbt.getCompoundTag("display");
                NBTTagList tag = new NBTTagList();

                actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

                String name = cleanse(display.getString("Name"));

                display.setTag("Lore", tag);
                display.setString("Name", name + " §c§l\u21E9§r§c" + String.format("%.1f", (chanceDown / (chanceDown + chanceUp)) * 100) + "% §a§l\u21E7§r§a" + String.format("%.1f", (chanceUp / (chanceDown + chanceUp)) * 100) + "%");
                nbt.setTag("display", display);
            } else {
                int average = total / identifications;
                String color = "§";
                if (average >= 97) {
                    color += "b";
                } else if (average >= 80) {
                    color += "a";
                } else if (average >= 30) {
                    color += "e";
                } else {
                    color += "c";
                }

                NBTTagCompound display = nbt.getCompoundTag("display");
                NBTTagList tag = new NBTTagList();

                actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

                String name = cleanse(display.getString("Name"));

                display.setTag("Lore", tag);
                display.setString("Name", name + color + " [" + average + "%]");
                nbt.setTag("display", display);
            }
        }

        stack.setTagCompound(nbt);
    }

    private static String cleanse(String str){
        return ID_PERCENTAGES.matcher(str).replaceAll("");
    }

}
