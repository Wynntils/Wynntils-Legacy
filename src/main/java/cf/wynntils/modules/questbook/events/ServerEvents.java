/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.events;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.questbook.enums.QuestSize;
import cf.wynntils.modules.questbook.enums.QuestStatus;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerEvents implements Listener {

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        if(e.getCurrentClass() != ClassType.NONE) {
            QuestManager.setTrackedQuest(null);
        }
    }

    @SubscribeEvent
    public void onUserUseItem(PacketEvent.PlayerUseItemEvent e) {
        if(acceptItems) e.setCanceled(true);
    }

    @SubscribeEvent
    public void onUserUseItemOnBlock(PacketEvent.PlayerUseItemOnBlockEvent e) {
        if(acceptItems) e.setCanceled(true);
    }

    private boolean acceptItems = false;
    private short transactionId = 0;
    private InventoryBasic currentInventory = null;

    private ArrayList<String> readedQuests = new ArrayList<>();

    @SubscribeEvent
    public void onInventoryReceive(PacketEvent.InventoryReceived e) {
        if(!Reference.onWorld) return;

        if(QuestManager.isReadingQuestBook()) {
            if ("minecraft:container".equals(e.getPacket().getGuiId())) {
                InventoryBasic base = new InventoryBasic(e.getPacket().getWindowTitle(), e.getPacket().getSlotCount());

                if(e.getPacket().getSlotCount() >= 54 && base.hasCustomName() && base.getDisplayName().getFormattedText().contains("Quests") && base.getDisplayName().getFormattedText().contains("[Pg.")) {
                    if(!acceptItems) {
                        readedQuests.clear();
                        transactionId = 0;
                        acceptItems = true;
                        currentInventory = base;
                    }

                    e.setCanceled(true);
                }else{
                    acceptItems = false; QuestManager.setReadingQuestBook(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onInventoryReceiveItems(PacketEvent.InventoryItemsReceived e) {
        if(currentInventory == null || !currentInventory.hasCustomName() || !currentInventory.getDisplayName().getFormattedText().contains("Quests") || e.getPacket().getWindowId() == 0) {
            acceptItems = false;
            QuestManager.setReadingQuestBook(false);
            return;
        }
        if(acceptItems) {
            e.setCanceled(true);

            int inventory = -1;
            ItemStack next = null;
            int nextId = 0;

            for(ItemStack i : e.getPacket().getItemStacks()) {
                inventory++;

                if(inventory == 35) QuestManager.updateDiscoveryLore(i.getDisplayName(), Utils.getLore(i));
                if(inventory == 44) QuestManager.updateSecretDiscoveryLore(i.getDisplayName(), Utils.getLore(i));
                if(inventory == 54) break;

                if(inventory == 8) {
                    if(i.hasDisplayName() && i.getDisplayName().contains(">§2>§a>§2>§a>")) {
                        next = i;
                        nextId = inventory;
                    }
                    continue;
                }

                if(!(i.hasDisplayName() && i.getDisplayName().equalsIgnoreCase(" ")) && !readedQuests.contains(i.getDisplayName())) {
                    if((inventory+1)%9 == 0 || (inventory+1)%9 == 8) {
                        continue;
                    }
                    readedQuests.add(i.getDisplayName());
                    parseQuest(i);
                }
            }

            if(next != null) {
                windowClick(next, nextId, e.getPacket().getWindowId(), 0, ClickType.PICKUP, e.getPlayClient());
            }else{
                acceptItems = false;
                e.getPlayClient().sendPacket(new CPacketCloseWindow(e.getPacket().getWindowId()));
                QuestManager.updateTrackedQuest();
                QuestManager.setReadingQuestBook(false);
            }
        }
    }

    public ItemStack windowClick(ItemStack stack, int slotId, int windowId, int mouseButton, ClickType type, NetHandlerPlayClient client) {
        short short1 = transactionId++;
        client.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, stack, short1));
        return stack;
    }

    public void parseQuest(ItemStack item) {
        if(!item.hasDisplayName()) return;

        String displayName = item.getDisplayName();
        displayName = displayName.substring(0, displayName.length() - 1);
        displayName = Utils.stripColor(displayName).replace("À", "").replace("\u058E", "");

        QuestStatus status = null;

        List<String> lore = Utils.getLore(item);
        if(lore.size() <= 0) return;

        if(lore.get(0).contains("Completed!")) {
            status = QuestStatus.COMPLETED;
        }else if(lore.get(0).contains("Started")) {
            status = QuestStatus.STARTED;
        }else if(lore.get(0).contains("Can start")) {
            status = QuestStatus.CAN_START;
        }else if(lore.get(0).contains("Cannot start")) {
            status = QuestStatus.CANNOT_START;
        }

        if(status == null) {
            acceptItems = false;
            QuestManager.setReadingQuestBook(false);
            QuestManager.requestQuestBookReading();
            return;
        }

        int minLevel = Integer.valueOf(Utils.stripColor(lore.get(2)).replace("✔ Combat Lv. Min: ", "").replace("✖ Combat Lv. Min: ", ""));
        QuestSize size = QuestSize.valueOf(Utils.stripColor(lore.get(3)).replace("- Length: ", "").toUpperCase());

        String description = "";
        for(int i = 5; i < lore.size(); i ++) {
            if(lore.get(i).equalsIgnoreCase("§7Right click to track")) {
                break;
            }
            description = description + Utils.stripColor(lore.get(i));
        }

        QuestManager.addQuestInfo(new QuestInfo(displayName, status, minLevel, size, description, lore));
    }

}
