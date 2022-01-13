/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.instances.inventory;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.enums.InventoryResult;
import com.wynntils.modules.core.interfaces.IInventoryOpenAction;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Used for fake opening inventories that are opened by inventory
 * Just create the instance (title, clickItemPosition)
 * you can receive the inventory by setting up onReceiveItems
 * call #.isOpen when you are ready to isOpen the inventory and
 * call #.close whenever you finish using the inventory, otherwise
 * everything will bug and catch fire.
 */
public class FakeInventory {

    Pattern expectedWindowTitle;
    IInventoryOpenAction openAction;

    private Consumer<FakeInventory> onReceiveItems = null;
    private BiConsumer<FakeInventory, InventoryResult> onClose = null;

    private int windowId = -1;
    private short transaction = 0;
    private String windowTitle = "";
    private NonNullList<ItemStack> inventory = null;

    private boolean isOpen = false;
    private long lastAction = 0;
    private boolean expectingResponse = false;
    private long limitTime = 10000;

    public FakeInventory(Pattern expectedWindowTitle, IInventoryOpenAction openAction) {
        this.expectedWindowTitle = expectedWindowTitle;
        this.openAction = openAction;
    }

    public FakeInventory onReceiveItems(Consumer<FakeInventory> onReceiveItems) {
        this.onReceiveItems = onReceiveItems;

        return this;
    }

    public FakeInventory onClose(BiConsumer<FakeInventory, InventoryResult> onClose) {
        this.onClose = onClose;

        return this;
    }

    public FakeInventory setLimitTime(long limitTime) {
        this.limitTime = limitTime;

        return this;
    }

    public void open() {
        if (isOpen) return;

        lastAction = McIf.getSystemTime();
        expectingResponse = true;

        FrameworkManager.getEventBus().register(this);

        openAction.onOpen(this, () -> close(InventoryResult.CLOSED_PREMATURELY));
    }

    public void close() {
        close(InventoryResult.CLOSED_SUCCESSFULLY);
    }

    public void closeUnsuccessfully() {
        close(InventoryResult.CLOSED_UNSUCCESSFULLY);
    }

    private void close(InventoryResult result) {
        if (!isOpen) return;

        FrameworkManager.getEventBus().unregister(this);
        isOpen = false;

        if (windowId != -1) McIf.mc().getConnection().sendPacket(new CPacketCloseWindow(windowId));
        windowId = -1;

        if(onClose != null) McIf.mc().addScheduledTask(() -> onClose.accept(this, result));
    }

    public void clickItem(int slot, int mouseButton, ClickType type) {
        if (!isOpen) return;

        lastAction = McIf.getSystemTime();
        expectingResponse = true;

        transaction++;
        McIf.mc().getConnection().sendPacket(new CPacketClickWindow(windowId, slot, mouseButton, type, inventory.get(slot), transaction));
    }

    public Pair<Integer, ItemStack> findItem(String name, BiPredicate<String, String> filterType) {
        if (!isOpen) return null;

        for(int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.get(slot);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue;

            String displayName = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            if (filterType.test(displayName, name)) {
                return new Pair<>(slot, stack);
            }
        }

        return null;
    }

    public List<Pair<Integer, ItemStack>> findItems(List<String> names, BiPredicate<String, String> filterType) {
        return findItems(names, Collections.nCopies(names.size(), filterType));
    }

    public List<Pair<Integer, ItemStack>> findItems(List<String> names, List<? extends BiPredicate<String, String>> filterTypes) {
        if (!isOpen) return null;

        int found = 0;
        List<Pair<Integer, ItemStack>> result = new ArrayList<>(Collections.nCopies(names.size(), null));

        for (int slot = 0, len = inventory.size(); slot < len && found != names.size(); ++slot) {
            ItemStack stack = inventory.get(slot);
            if (stack.isEmpty() || !stack.hasDisplayName()) continue;

            String displayName = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            for (int i = 0; i < names.size(); ++i) {
                if (result.get(i) != null) continue;

                if (filterTypes.get(i).test(displayName, names.get(i))) {
                    result.set(i, new Pair<>(slot, stack));
                }
            }
        }

        return result;
    }

    public List<ItemStack> getInventory() {
        return inventory;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    @Override
    public FakeInventory clone() {
        return new FakeInventory(expectedWindowTitle, openAction);
    }

    // detects if the server is not responding
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!isOpen || e.phase != TickEvent.Phase.END) return;
        if (!expectingResponse) return;
        if (McIf.getSystemTime() - lastAction < limitTime) return;

        close(InventoryResult.CLOSED_PREMATURELY);
    }

    // detects the GUI open, and gatters information
    @SubscribeEvent
    public void onInventoryReceive(PacketEvent<SPacketOpenWindow> e) {
        if (!e.getPacket().getGuiId().equalsIgnoreCase("minecraft:container") || !e.getPacket().hasSlots()) {
            close(InventoryResult.CLOSED_OVERLAP);
            return;
        }

        if (!expectedWindowTitle.matcher(TextFormatting.getTextWithoutFormattingCodes(McIf.getUnformattedText(e.getPacket().getWindowTitle()))).matches()) {
            close(InventoryResult.CLOSED_OVERLAP);
            return;
        }

        isOpen = true;
        expectingResponse = false;
        lastAction = McIf.getSystemTime();

        windowId = e.getPacket().getWindowId();
        windowTitle = McIf.getUnformattedText(e.getPacket().getWindowTitle());
        inventory = NonNullList.create();

        e.setCanceled(true);
    }

    // detects item receiving
    @SubscribeEvent
    public void onItemsReceive(PacketEvent<SPacketWindowItems> e) {
        if (windowId != e.getPacket().getWindowId()) {
            close(InventoryResult.CLOSED_OVERLAP);
            return;
        }

        inventory.clear();
        inventory.addAll(e.getPacket().getItemStacks());

        expectingResponse = false;
        lastAction = McIf.getSystemTime();

        if (onReceiveItems != null) McIf.mc().addScheduledTask(() -> onReceiveItems.accept(this));

        e.setCanceled(true);
    }

    // confirm all server transactions
    @SubscribeEvent
    public void confirmAllTransactions(PacketEvent.Incoming<SPacketConfirmTransaction> e) {
        if (windowId != e.getPacket().getWindowId()) {
            close(InventoryResult.CLOSED_OVERLAP);
            return;
        }

        McIf.mc().getConnection().sendPacket(new CPacketConfirmTransaction(windowId, e.getPacket().getActionNumber(), true));
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void close(PacketEvent.Incoming<SPacketCloseWindow> e) {
        close(InventoryResult.CLOSED_PREMATURELY);
    }

    // interrupt if execute command
    @SubscribeEvent
    public void cancelCommands(ClientChatEvent e) {
        if (!e.getMessage().startsWith("/class") || !e.getMessage().startsWith("/classes")) return;

        close(InventoryResult.CLOSED_ACTION);
    }

    // interrupt if world is loaded
    @SubscribeEvent
    public void closeOnWorldLoad(WorldEvent.Load e) {
        if (!isOpen) return;

        close(InventoryResult.CLOSED_ACTION);
    }

}
