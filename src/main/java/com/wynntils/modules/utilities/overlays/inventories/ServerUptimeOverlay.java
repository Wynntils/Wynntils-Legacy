/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.utilities.managers.ServerListManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ServerUptimeOverlay implements Listener {

    @SubscribeEvent
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
        if (!Reference.onLobby) return;
        if (e.getGui().getSlotUnderMouse() == null || e.getGui().getSlotUnderMouse().getStack().isEmpty()) return;

        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        if (!ItemUtils.getStringLore(stack).contains("Click to join") || stack.getItem() == Items.CLOCK) return;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("wynntils")) return;

        String world = "WC" + stack.getCount();

        List<String> newLore = ItemUtils.getLore(stack);
        newLore.add(TextFormatting.DARK_GREEN + "Uptime: " + TextFormatting.GREEN + ServerListManager.getUptime(world));

        NBTTagCompound compound = nbt.getCompoundTag("display");
        NBTTagList list = new NBTTagList();

        newLore.forEach(c -> list.appendTag(new NBTTagString(c)));

        compound.setTag("Lore", list);
        nbt.setBoolean("wynntils", true);
    }

}
