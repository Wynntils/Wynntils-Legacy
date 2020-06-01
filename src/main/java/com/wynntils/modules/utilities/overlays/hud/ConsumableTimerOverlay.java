/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.ConsumableContainer;
import com.wynntils.modules.utilities.instances.IdentificationHolder;
import com.wynntils.modules.utilities.overlays.inventories.ItemIdentificationOverlay;
import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.text.TextFormatting.*;

public class ConsumableTimerOverlay extends Overlay {

    transient private static final Pattern DURATION_PATTERN = Pattern.compile("^- Duration: ([0-9]*) (.*?)");
    transient private static final Pattern EFFECT_PATTERN = Pattern.compile("^- Effect: (.*)");

    transient private static ArrayList<ConsumableContainer> activeConsumables = new ArrayList<>();
    transient private static HashMap<String, IdentificationHolder> activeEffects = new HashMap<>();

    public ConsumableTimerOverlay() {
        super("Consumable Timer", 100, 60, true, 1, 0.2f, 0, 0, OverlayGrowFrom.TOP_RIGHT, RenderGameOverlayEvent.ElementType.ALL);
    }

    public static void clearConsumables() {
        // assigns a new object to avoid CME
        activeConsumables = new ArrayList<>();
        activeEffects = new HashMap<>();
    }

    public static void addConsumable(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasDisplayName()) return; // display name also checks for the nbt data
        if (stack.getItem() != Items.DIAMOND_AXE && stack.getItem() != Items.POTIONITEM) return; // foods and scrolls have DIAMOND_AXE as their items

        // vanilla potions needs a special verification, they DON'T start with dark aqua
        if (!stack.getDisplayName().startsWith(DARK_AQUA.toString())) {
            String displayName = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            SkillPoint sp = SkillPoint.findSkillPoint(displayName);
            if (sp == null) return;

            ConsumableContainer consumable = new ConsumableContainer(sp.getAsName());

            List<String> itemLore = ItemUtils.getLore(stack);
            for (String line : itemLore) {
                line = TextFormatting.getTextWithoutFormattingCodes(line);

                // duration | - Duration: <group1> Seconds
                Matcher m = DURATION_PATTERN.matcher(line);
                if (m.matches() && m.group(1) != null) {
                    consumable.setExpirationTime(
                            Minecraft.getSystemTime() + (Integer.parseInt(m.group(1)) * 1000)
                    ); // currentMillis + (seconds * 1000)
                    continue;
                }

                // effects | - Effect: <id>
                m = EFFECT_PATTERN.matcher(line);
                if (!m.matches()) continue; // continues if not a valid effect

                String id = m.group(1);
                if (id == null || id.isEmpty()) continue; // continues if id is null or empty

                // removing skill point symbols
                for (SkillPoint skillPoint : SkillPoint.values()) {
                    id = id.replace(skillPoint.getSymbol() + " ", "");
                }

                m = ItemIdentificationOverlay.ID_PATTERN.matcher(id);
                if (!m.matches()) continue; // continues if the effect is not a valid id

                verifyIdentification(m, consumable);
            }

            activeConsumables.add(consumable);
            updateActiveEffects();
            return;
        }

        // crafted items
        String name;
        if (stack.getItem() == Items.POTIONITEM)
            name = LIGHT_PURPLE + "Ⓛ Potion";
        else if (stack.getItemDamage() >= 70 && stack.getItemDamage() <= 75) // food, 70 <= damage <= 75
            name = GOLD + "Ⓐ Food";
        else if (stack.getItemDamage() >= 42 && stack.getItemDamage() <= 44) // scrolls, 42 <= damage <= 44
            name = YELLOW + "Ⓔ Scroll";
        else return; // breaks if not valid

        ConsumableContainer consumable = new ConsumableContainer(name);

        List<String> itemLore = ItemUtils.getLore(stack);
        for (String line : itemLore) {
            line = TextFormatting.getTextWithoutFormattingCodes(line); // remove colors

            // duration | - Duration: <group1> Seconds
            Matcher m = DURATION_PATTERN.matcher(line);
            if (m.matches() && m.group(1) != null) {
                consumable.setExpirationTime(
                        Minecraft.getSystemTime() + (Integer.parseInt(m.group(1)) * 1000)
                ); // currentMillis + (seconds * 1000)
                continue;
            }

            // effects | <Value><Suffix> <ID>
            m = ItemIdentificationOverlay.ID_PATTERN.matcher(line);
            if (!m.matches()) continue; // continues if not a valid effect

            verifyIdentification(m, consumable);
        }

        if (!consumable.isValid()) return;

        activeConsumables.add(consumable);
        updateActiveEffects();
    }

    private static void verifyIdentification(Matcher m, ConsumableContainer consumable) {
        String idName = m.group("ID");
        String suffix = m.group("Suffix");
        boolean isRaw = suffix == null;

        SpellType spell = SpellType.getSpell(idName);
        if (spell != null) idName = spell.replaceWithShortName(idName);

        String shortIdName = ItemIdentificationOverlay.toShortIdName(idName, isRaw);

        IdentificationModifier modifier = IdentificationModifier.INTEGER;
        if (!isRaw) {
            // loop through all modifier options to find a valid one
            for (IdentificationModifier mod : IdentificationModifier.values()) {
                if (mod.getInGame().isEmpty() || !suffix.contains(mod.getInGame())) continue;

                modifier = mod;
                break;
            }
        }

        // finish by adding the effect to the container
        consumable.addEffect(shortIdName, Integer.parseInt(m.group("Value")), modifier);
    }

    private static void updateActiveEffects() {
        HashMap<String, IdentificationHolder> effects = new HashMap<>();
        for (ConsumableContainer consumable : activeConsumables) {
            for (String cEf : consumable.getEffects().keySet()) {
                IdentificationHolder holder = consumable.getEffects().get(cEf);

                if (effects.containsKey(cEf)) {
                    effects.get(cEf).sumAmount(holder.getCurrentAmount());
                    continue;
                }

                effects.put(cEf, new IdentificationHolder(holder.getCurrentAmount(), holder.getModifier()));

            }
        }

        activeEffects = effects;
    }

    private static void removeActiveEffect(ConsumableContainer consumable) {
        Iterator<Map.Entry<String, IdentificationHolder>> it = activeEffects.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, IdentificationHolder> entry = it.next();
            if (!consumable.hasEffect(entry.getKey())) continue;

            entry.getValue().sumAmount(-consumable.getEffect(entry.getKey()).getCurrentAmount());
            if (entry.getValue().getCurrentAmount() != 0) continue;

            it.remove();
        }
    }

    public static void addBasicTimer(String name, int timeInSeconds) {
        String formattedName = GRAY + name;
        ConsumableContainer consumable = new ConsumableContainer(formattedName);
        consumable.setExpirationTime(System.currentTimeMillis() + timeInSeconds*1000);
        // Clear old consumable with the same name
        activeConsumables.removeIf(c -> c.getName().equals(formattedName));
        activeConsumables.add(consumable);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        event.setCanceled(false);

        if (activeConsumables.isEmpty()) return;

        Iterator<ConsumableContainer> it = activeConsumables.iterator();

        // id names
        int extraY = 0;
        while (it.hasNext()) {
            ConsumableContainer consumable = it.next();
            if (consumable.hasExpired()) { // remove if expired
                removeActiveEffect(consumable); // update active effects
                it.remove();
                continue;
            }

            drawString(consumable.getName() + " (" + StringUtils.timeLeft(consumable.getExpirationTime() - Minecraft.getSystemTime()) + ")"
                    , 0, extraY, CommonColors.WHITE, getAlignment(), SmartFontRenderer.TextShadow.OUTLINE);

            extraY+=10;
        }

        // effects
        if (!OverlayConfig.ConsumableTimer.INSTANCE.showEffects || activeEffects.isEmpty()) return;
        extraY+=10;

        for (Map.Entry<String, IdentificationHolder> entry : activeEffects.entrySet()) {
            drawString(entry.getValue().getAsLore(entry.getKey()), 0, extraY, CommonColors.WHITE, getAlignment(), SmartFontRenderer.TextShadow.OUTLINE);

            extraY += 10;
        }
    }

}
