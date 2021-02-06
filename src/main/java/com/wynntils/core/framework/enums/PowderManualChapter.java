/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums;

import net.minecraft.util.StringUtils;

public enum PowderManualChapter {
    
    ONE("                     §6Chapter 1 - The Basics\n\n" +
                "    §7§oThere exist five varieties of magic powder, each corresponding to one of five different elements; §c✹ Fire§7§o, §b❉ Water§7§o, §e✦ Thunder§7§o, §2✤ Earth§7§o, and §f❋ Air§7§o.\n" +
                "    §7§oIt is possible to augment items with these powders, if one is versed in the art of enchantment. Many offer this service for a price, as Powder Masters.\n" +
                "    §7§oCertain items have a higher magical capacity, and thusly are able to hold more powders within them. Some have many, some have few, others still have none.\n" +
                "    §7§oShould a §lweapon§r§7§o be enchanted with a powder, the powder will imbue the item with a slight amount of elemental damage, but also transfer basic neutral damage into its element.\n" +
                "    §7§oShould a piece of §larmour§r§7§o be enchanted with a powder, the garment will gain a resistance towards that powder's element, at the cost of an elemental weakness."),
    TWO("                  §6Chapter 2 - Elemental Abilities\n\n" +
                "    §7§oIf a powder is more concentrated, they can unlock powerful elemental magicks within an item. Powders appraised to be Tier IV or higher are capable of forming these special abilities.\n" +
                "    §7§oTwo or more like-elemented powders are necessary for this. While items can be augmented with powders even after unlocking an ability, only one amplified magic power can be kept within an item at one time.\n" +
                "    §7§oWeapons and armour channel this magical energy in radically different ways, just as powders change their more mundane abilities. The method of use for each piece can be referred to in Chapter 3.\n" +
                "    §7§oUsing different tiers of powder to try to form an ability on an item is somewhat unpredictable in the end potency of the magic, but using higher tiers of powder will generally lead to a stronger effect.\n" +
                "    §7§oTo unleash the unique ability imbued in your weapon, one must first charge the power by attacking enemies. Then, heavily focus on the latent energies within the item, and release them with a swing. §r(Shift+Left-Click)"),
    THREE("                  §6Chapter 3 - Elemental Abilities\n\n" +
                "    §2§l✤ Earth:  §7§oEarth-imbued weapons will unleash a powerful quake, rupturing the nearby ground and disorienting enemies. Earth-augmented armors produce a rage-state in the user as they near death, turning Earth-based attacks more potent, and more violent.\n" +
                "    §e§l✦ Thunder:  §7§oThunder-imbued weapons can release a streak of lightning that seeks enemies, bouncing from one foe to the next nearby. Thunder-augmented armors will steal the life force of the slain, and in kind increase the power of Thunder-based attacks temporarily.\n" +
                "    §b§l❉ Water:  §7§oWater-imbued weapons will curse one's nearby enemies. This curse weakens armor, and turns them vulnerable to further attack. Water-augmented armors recycle used mana, transferring it into a short boost to Water-affiliated techniques, depending on how much mana was consumed.\n" +
                "    §c§l✹ Fire:  §7§oFire-imbued weapons will generate a fan of flames that burn one's enemies and empower one's allies. Fire-augmented armors react to being struck, turning the impact of the blow, no matter how weak, into a temporary power boost to Fire-based attacks.\n" +
                "    §f§l❋ Air:  §7§oAir-imbued weapons trap nearby enemies in a vortex of wind, and blow the victims away when struck. Air-augmented armors will, so long as they are kept close, leach energy from nearby foes to improve the strength of Air-based attacks.");
    
    String chapterText;
    
    PowderManualChapter(String text) {
        chapterText = text;
    }
    
    public String getText() {
        return chapterText;
    }
    
    public static boolean isPowderManualLine(String line) {
        PowderManualChapter[] chapters = PowderManualChapter.values();
        for (PowderManualChapter chapter : chapters) {
            if (StringUtils.stripControlCodes(chapter.getText()).contains(line)) return true;
        }
        
        return false;
    }

}
