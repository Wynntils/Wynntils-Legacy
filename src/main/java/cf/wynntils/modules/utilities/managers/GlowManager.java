/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.utils.TeamHelper;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


public class GlowManager {

    //Items
    private static TeamHelper mythic = new TeamHelper("mythic", "5");
    private static TeamHelper legendary = new TeamHelper("legendary", "b");
    private static TeamHelper rare = new TeamHelper("rare", "d");
    private static TeamHelper unique = new TeamHelper("unique", "e");
    private static TeamHelper set = new TeamHelper("set", "a");

    //Players
    private static TeamHelper helpers = new TeamHelper("helpers", "5", true);

    public static void verifyEntity(Entity i) {
        if(!Reference.onWorld || i == null) return;

        if(i instanceof EntityItem) {
            ItemStack item = ((EntityItem)i).getItem();
            if(!item.hasDisplayName() || i.getTags().contains("verified")) {
                return;
            }

            if(!checkTicks(i)) {
                return;
            }

            String displayName = Utils.stripColor(item.getDisplayName());
            String lore = item.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString();

            if(lore.contains("Junk Item")) {
                i.setCustomNameTag("§7" + (item.getCount() > 1 ? item.getCount() + "x " : "") + displayName);
                i.setAlwaysRenderNameTag(true);
                return;
            }
            if(lore.contains("Misc. Item")) {
                i.setCustomNameTag("§7" + (item.getCount() > 1 ? item.getCount() + "x " : "") + displayName);
                i.setAlwaysRenderNameTag(true);
                return;
            }

            boolean identified = !lore.equalsIgnoreCase("[]");

            ItemProfile wynnItem = WebManager.getItems().getOrDefault(displayName, null);

            if(wynnItem != null) {
                String text = (identified ? wynnItem.getName() : "Unidentified " + wynnItem.getType()) + " §6[Lv.  " + wynnItem.getLevel() + "]";
                if(wynnItem.tier.equals("Mythic")) {
                    i.setCustomNameTag("§5" + text);
                    i.setAlwaysRenderNameTag(true);
                    i.setGlowing(true);
                    mythic.addEntity(i);
                }else if(wynnItem.tier.equals("Legendary")) {
                    i.setCustomNameTag("§b" + text);
                    i.setAlwaysRenderNameTag(true);
                    i.setGlowing(true);
                    legendary.addEntity(i);
                }else if(wynnItem.tier.equals("Rare")) {
                    i.setCustomNameTag("§d" + text);
                    i.setAlwaysRenderNameTag(true);
                    i.setGlowing(true);
                    rare.addEntity(i);
                }else if(wynnItem.tier.equals("Unique")) {
                    i.setCustomNameTag("§e" + text);
                    i.setAlwaysRenderNameTag(true);
                    i.setGlowing(true);
                    unique.addEntity(i);
                }else if(wynnItem.tier.equals("Set")) {
                    i.setCustomNameTag("§a" + text);
                    i.setAlwaysRenderNameTag(true);
                    i.setGlowing(true);
                    set.addEntity(i);
                }
            }

            return;
        }
        if(i instanceof EntityPlayer && !i.getUniqueID().toString().equalsIgnoreCase(ModCore.mc().player.getUniqueID().toString())) {
            EntityPlayer player = (EntityPlayer) i;
            if(WebManager.isHelper(player.getUniqueID().toString())) {
                helpers.addPlayer(player);
                if(!player.isGlowing()) i.setGlowing(true);
            }
        }
    }

    private static boolean checkTicks(Entity i) {
        if(i.getTags().contains("Ver5")) {
            i.getTags().add("verified");
            i.removeTag("Ver5");
            return false;
        }else if(i.getTags().contains("Ver4")) {
            i.getTags().add("Ver5");
            i.removeTag("Ver4");
            return true;
        }else if(i.getTags().contains("Ver3")) {
            i.getTags().add("Ver4");
            i.removeTag("Ver3");
            return false;
        }else if(i.getTags().contains("Ver2")) {
            i.getTags().add("Var3");
            i.removeTag("Ver2");
            return true;
        }
        else if(i.getTags().contains("Ver1")) {
            i.getTags().add("Ver2");
            i.removeTag("Ver1");
            return false;
        }
        i.getTags().add("Ver1");
        return false;
    }

}
