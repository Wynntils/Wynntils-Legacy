package com.wynndevs.wynnmarket.market;

import net.minecraft.item.ItemStack;

/**
 * Created by HeyZeer0 on 17/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class AnnounceProfile {

    String id;

    int material;
    String base64;
    String owner;

    String server;
    ItemStack item;

    public AnnounceProfile(String id, int material, String base64, String owner) {
        this.id = id;
        this.material = material;
        this.base64 = base64;
        this.owner = owner;

        try{
            if(!base64.contains("::")) {
                item = null;
            }else{
                String[] b64 = base64.split("::");
                item = WrappedStack.getItemStack(b64[0], material);
                item.setItemDamage(Integer.valueOf(b64[1]));
            }
        }catch (Exception ex) { item = null; }
    }

    public AnnounceProfile(String id, int material, String base64, String owner, String server) {
        this.id = id;
        this.material = material;
        this.base64 = base64;
        this.owner = owner;
        this.server = server;

        try{
            if(!base64.contains("::")) {
                item = null;
            }else{
                String[] b64 = base64.split("::");
                item = WrappedStack.getItemStack(b64[0], material);
                item.setItemDamage(Integer.valueOf(b64[1]));
            }
        }catch (Exception ex) { item = null; }
    }

    public String getId() {
        return id;
    }

    public int getMaterial() {
        return material;
    }

    public String getBase64() {
        return base64;
    }

    public String getOwner() {
        return owner;
    }

    public String getServer() {
        return server;
    }

    public ItemStack getItem() {
        return item;
    }

}
