/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils.reflections;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public enum ReflectionFields {

    GuiChest_lowerChestInventory(GuiChest.class, "lowerChestInventory", "field_147015_w"),
    Entity_CUSTOM_NAME(Entity.class, "CUSTOM_NAME", "field_184242_az"),
    Event_phase(Event.class, "phase"),
    GuiScreenHorseInventory_horseEntity(GuiScreenHorseInventory.class, "horseEntity", "field_147034_x"),
    GuiScreenHorseInventory_horseInventory(GuiScreenHorseInventory.class, "horseInventory", "field_147029_w"),
    GuiIngame_persistantChatGUI(GuiIngame.class, "persistantChatGUI", "field_73840_e"),
    GuiIngame_remainingHighlightTicks(GuiIngame.class, "remainingHighlightTicks", "field_92017_k"),
    GuiIngame_highlightingItemStack(GuiIngame.class, "highlightingItemStack", "field_92016_l"),
    GuiIngame_displayedSubTitle(GuiIngame.class, "displayedSubTitle", "field_175200_y"),
    GuiChat_defaultInputFieldText(GuiChat.class, "defaultInputFieldText", "field_146409_v");

    final Field field;

    ReflectionFields(Class<?> holdingClass, String... values) {
        this.field = ReflectionHelper.findField(holdingClass, values);
    }

    public Object getValue(Object parent) {
        try{
            return field.get(parent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValue(Object parent, Object value) {
        try{
            field.set(parent,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
