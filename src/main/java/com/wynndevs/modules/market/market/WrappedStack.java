package com.wynndevs.modules.market.market;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;

/**
 * Created by HeyZeer0 on 17/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WrappedStack {

    /**
     * Convert an item {@link net.minecraft.nbt.NBTTagCompound} to Base64
     *
     * @param item
     *
     * @return the item {@link net.minecraft.nbt.NBTTagCompound} as Base64
     */
    public static String getBase64(ItemStack item) throws Exception {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        CompressedStreamTools.write(item.getTagCompound(), dataOutput);

        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }

    /**
     * Convert an base64 to the original item
     *
     * @param base64
     *        Origem Base64
     * @param material
     *        Material ID
     *
     * @return the converted item
     */
    public static ItemStack getItemStack(String base64, int material) throws Exception {
        long current = System.currentTimeMillis();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(base64, 32).toByteArray());

        ItemStack x = new ItemStack(Item.getItemById(material));
        x.setTagCompound(CompressedStreamTools.read(new DataInputStream(inputStream)));

        return x;
    }

}
