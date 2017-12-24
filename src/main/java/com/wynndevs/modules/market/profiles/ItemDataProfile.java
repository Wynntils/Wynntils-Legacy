package com.wynndevs.modules.market.profiles;

import com.wynndevs.modules.market.guis.WMGuiScreen;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HeyZeer0 on 17/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class ItemDataProfile {

    int x;
    int y;
    ItemStack item;

    ArrayList<String> defaultLore = new ArrayList<>();
    ArrayList<String> shiftLore = new ArrayList<>();
    ArrayList<String> ctrlLore = new ArrayList<>();
    ItemExecutor runnable;

    /**
     * A simple wrapper for rendering items
     *
     * @param x
     * @param y
     * @param item
     */
    public ItemDataProfile(int x, int y, ItemStack item) {
        this.x = x; this.y = y; this.item = item;
    }

    /**
     * Adds a string to mouse hover lore
     *
     * @param x
     */
    public void addDefaultLore(String x) {
        defaultLore.add(x);
    }

    /**
     * Adds the entire {@link List} to the mouse hove lore
     *
     * @param x
     */
    public void addDefaultLore(List<String> x) {
        defaultLore.addAll(x);
    }

    /**
     * Adds a string to mouse hover + shift lore
     *
     * @param x
     */
    public void addShiftLore(String x) {
        shiftLore.add(x);
    }

    /**
     * Adds the entire {@link List} to mouse hover + shift lore
     *
     * @param x
     */
    public void addShiftLore(List<String> x) {
        shiftLore.addAll(x);
    }

    /**
     * Adds the entire {@link List} to mouse hover + ctrl lore
     *
     * @param x
     */
    public void addCtrlLore(String x) {
        ctrlLore.add(x);
    }

    /**
     * Adds the entire {@link List} to mouse hover + ctrl lore
     *
     * @param x
     */
    public void addCtrlLore(List<String> x) {
        ctrlLore.addAll(x);
    }

    /**
     * Adds an action when the users click on the item
     *
     * @param r
     *        ItemExecutor runnable
     */
    public void addRunnable(ItemExecutor r) {
        runnable = r;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ItemExecutor getRunnable() {
        return runnable;
    }

    public ArrayList<String> getDefaultLore() {
        return defaultLore;
    }

    public ArrayList<String> getShiftLore() {
        return shiftLore;
    }

    public ArrayList<String> getCtrlLore() {
        return ctrlLore;
    }

    public ItemStack getItem() {
        return item;
    }

    public interface ItemExecutor {

        void userClicked(WMGuiScreen s, ItemDataProfile item);

    }

}
