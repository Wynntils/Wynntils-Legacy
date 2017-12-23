package com.wynndevs.market.profiles;

import com.wynndevs.market.guis.WMGuiScreen;
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

    public ItemDataProfile(int x, int y, ItemStack item) {
        this.x = x; this.y = y; this.item = item;
    }

    public void addDefaultLore(String x) {
        defaultLore.add(x);
    }

    public void addDefaultLore(List<String> x) {
        defaultLore.addAll(x);
    }

    public void addShiftLore(String x) {
        shiftLore.add(x);
    }

    public void addShiftLore(List<String> x) {
        shiftLore.addAll(x);
    }

    public void addCtrlLore(String x) {
        ctrlLore.add(x);
    }

    public void addCtrlLore(List<String> x) {
        ctrlLore.addAll(x);
    }

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
