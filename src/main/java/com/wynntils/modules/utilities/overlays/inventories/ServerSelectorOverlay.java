/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ServerSelectorOverlay implements Listener {

    @SubscribeEvent
    public void onDrawChest(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
        if (!Utils.isServerSelector(e.getGui())) return;
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("wynntilsServerIgnore")) return;
        String itemName = StringUtils.normalizeBadString(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName()));

        if (itemName.startsWith("World") && Reference.onBeta) {
            nbt.setBoolean("wynntilsServerIgnore", true);
            if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE && WebManager.blockHeroBetaStable()) {
                nbt.setBoolean("wynntilsBlock", true);
                List<String> lore = ItemUtils.getLore(stack);
                lore.add("" + TextFormatting.RED + TextFormatting.BOLD + "Your version of Wynntils is currently blocked from joining the Hero Beta due to instability. Try switching to Cutting Edge, or removing Wynntils while on the Hero Beta until support is added.");
                NBTTagCompound compound = nbt.getCompoundTag("display");
                NBTTagList list = new NBTTagList();
                lore.forEach(c -> list.appendTag(new NBTTagString(c)));
                compound.setTag("Lore", list);
                nbt.setTag("display", compound);
            } else if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE && WebManager.blockHeroBetaCuttingEdge()) {
                nbt.setBoolean("wynntilsBlock", true);
                List<String> lore = ItemUtils.getLore(stack);
                lore.add("" + TextFormatting.RED + TextFormatting.BOLD + "Your version of Wynntils is currently blocked from joining the Hero Beta due to instability. Try removing Wynntils until support is added.");
                NBTTagCompound compound = nbt.getCompoundTag("display");
                NBTTagList list = new NBTTagList();
                lore.forEach(c -> list.appendTag(new NBTTagString(c)));
                compound.setTag("Lore", list);
                nbt.setTag("display", compound);
            } else if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE && WebManager.warnHeroBetaStable()) {
                addWarningToStack(stack, nbt);
            } else if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE && WebManager.warnHeroBetaCuttingEdge()) {
                addWarningToStack(stack, nbt);
            }
        }
    }

    private void addWarningToStack(ItemStack stack, NBTTagCompound nbt) {
        nbt.setBoolean("wynntilsWarn", true);
        List<String> lore = ItemUtils.getLore(stack);
        lore.add("" + TextFormatting.RED + TextFormatting.BOLD + "Your version of Wynntils is currently unstable on the Hero Beta. Expect frequent crashes and bugs!");
        lore.add("" + TextFormatting.GREEN + "Please report any issues you do experience on the Wynntils discord (" + WebManager.getApiUrl("DiscordInvite") + ")");
        NBTTagCompound compound = nbt.getCompoundTag("display");
        NBTTagList list = new NBTTagList();
        lore.forEach(c -> list.appendTag(new NBTTagString(c)));
        compound.setTag("Lore", list);
        nbt.setTag("display", compound);
    }

    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Utils.isServerSelector(e.getGui())) return;
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;
        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("wynntilsBlock")) {
            TextComponentString text = new TextComponentString("Your version of Wynntils is currently blocked from joining the Hero Beta due to instability. Trying changing update stream to cutting edge, or removing Wynntils while on the Hero Beta until support is added.");
            text.getStyle().setColor(TextFormatting.RED);
            McIf.player().sendMessage(text);
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));

            e.setCanceled(true);
        } else if (nbt.hasKey("wynntilsWarn")) {
            TextComponentString text = new TextComponentString("Your version of Wynntils is currently unstable on the Hero Beta. Expect frequent crashes and bugs!");
            text.getStyle().setColor(TextFormatting.RED);
            text.getStyle().setBold(true);
            McIf.player().sendMessage(text);

            text = new TextComponentString("Please report any issues you do experience on the Wynntils discord ");
            text.getStyle().setColor(TextFormatting.GREEN);
            String discordInvite = WebManager.getApiUrl("DiscordInvite");
            if (discordInvite != null) {
                TextComponentString linkText = new TextComponentString("(" + discordInvite + ")");
                linkText.getStyle().setColor(TextFormatting.GREEN);
                linkText.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discordInvite));
                text.appendSibling(linkText);
            }
            McIf.player().sendMessage(text);

            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_BASS, 1f));
        }
    }
}
