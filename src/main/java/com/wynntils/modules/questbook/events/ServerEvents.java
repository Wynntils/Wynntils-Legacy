/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.modules.questbook.enums.QuestSize;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.text.TextFormatting;
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
    private boolean quests = true;
    private boolean discoveries = false;
    private boolean secretDiscoveries = false;
    private boolean skip = false;

    private ArrayList<String> readedQuests = new ArrayList<>();
    private ArrayList<String> readedDiscoveries = new ArrayList<>();
    private ArrayList<String> readedSecretDiscoveries = new ArrayList<>();

    private ArrayList<QuestInfo> tempQuestList = new ArrayList<>();
    private ArrayList<DiscoveryInfo> tempDiscoveryList = new ArrayList<>();

    @SubscribeEvent
    public void onInventoryReceive(PacketEvent.InventoryReceived e) {
        if(!Reference.onWorld) return;

        if(QuestManager.isReadingQuestBook()) {
            if ("minecraft:container".equals(e.getPacket().getGuiId())) {
                InventoryBasic base = new InventoryBasic(e.getPacket().getWindowTitle(), e.getPacket().getSlotCount());

                if(e.getPacket().getSlotCount() >= 54 && base.hasCustomName() && (base.getDisplayName().getFormattedText().contains("Quests") || base.getDisplayName().getFormattedText().contains("Discoveries")) && base.getDisplayName().getFormattedText().contains("[Pg.")) {
                    if(!acceptItems) {
                        readedQuests.clear();
                        tempQuestList.clear();
                        readedDiscoveries.clear();
                        readedSecretDiscoveries.clear();
                        tempDiscoveryList.clear();
                        transactionId = 0;
                        acceptItems = true;
                        currentInventory = base;
                        quests = true;
                        discoveries = false;
                        secretDiscoveries = false;
                    }

                    e.setCanceled(true);
                }else{
                    acceptItems = false;
                    QuestManager.setReadingQuestBook(false);
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
            if (skip) {
                skip = false;
                return;
            }
            e.setCanceled(true);

            int inventory = -1;
            ItemStack next = ItemStack.EMPTY;
            ItemStack discoveriesItem = ItemStack.EMPTY;
            ItemStack secretDiscoveriesItem = ItemStack.EMPTY;
            int nextId = 0;

            for(ItemStack i : e.getPacket().getItemStacks()) {
                inventory++;

                if(inventory == 35) {
                    QuestManager.updateDiscoveryLore(i.getDisplayName(), Utils.getLore(i));
                    discoveriesItem = i;
                }
                if(inventory == 44) {
                    QuestManager.updateSecretDiscoveryLore(i.getDisplayName(), Utils.getLore(i));
                    secretDiscoveriesItem = i;
                }
                if(inventory == 54) break;

                if(inventory == 8) {
                    if(i.hasDisplayName() && i.getDisplayName().contains(">" + TextFormatting.DARK_GREEN + ">" + TextFormatting.GREEN + ">" + TextFormatting.DARK_GREEN + ">" + TextFormatting.GREEN + ">")) {
                        next = i;
                        nextId = inventory;
                    }
                    continue;
                }
                
                if(i.isEmpty() || i.hasDisplayName() && i.getDisplayName().equalsIgnoreCase(" ") || (inventory+1)%9 == 0 || (inventory+1)%9 == 8) continue;

                if (quests) {
                    if(!readedQuests.contains(i.getDisplayName())) {
                        readedQuests.add(i.getDisplayName());
                        if (!parseQuest(i)) {
                            acceptItems = false;
                            e.getPlayClient().sendPacket(new CPacketCloseWindow(e.getPacket().getWindowId()));
                            QuestManager.setReadingQuestBook(false);
                            return;
                        }
                    }
                } else if (discoveries) {
                    if (!readedDiscoveries.contains(i.getDisplayName())) {
                        readedDiscoveries.add(i.getDisplayName());
                        if (!parseDiscovery(i)) {
                            acceptItems = false;
                            e.getPlayClient().sendPacket(new CPacketCloseWindow(e.getPacket().getWindowId()));
                            QuestManager.setReadingQuestBook(false);
                            return;
                        }
                    }
                } else if (secretDiscoveries) {
                    if (!readedSecretDiscoveries.contains(i.getDisplayName())) {
                        readedSecretDiscoveries.add(i.getDisplayName());
                        if (!parseDiscovery(i)) {
                            acceptItems = false;
                            e.getPlayClient().sendPacket(new CPacketCloseWindow(e.getPacket().getWindowId()));
                            QuestManager.setReadingQuestBook(false);
                            return;
                        }
                    }
                }
            }
            if(!next.isEmpty()) {
                windowClick(next, nextId, e.getPacket().getWindowId(), 0, ClickType.PICKUP, e.getPlayClient());
                QuestManager.updateRequestTime();
            }else{
                if (quests) {
                    quests = false;
                    discoveries = true;
                    windowClick(discoveriesItem, 35, e.getPacket().getWindowId(), 0, ClickType.PICKUP, e.getPlayClient());
                    skip = true;
                    QuestManager.updateRequestTime();
                } else if (discoveries) {
                    discoveries = false;
                    secretDiscoveries = true;
                    windowClick(secretDiscoveriesItem, 44, e.getPacket().getWindowId(), 0, ClickType.PICKUP, e.getPlayClient());
                    skip = true;
                    QuestManager.updateRequestTime();
                } else if (secretDiscoveries) {
                    secretDiscoveries = false;
                    quests = true;
                    acceptItems = false;
                    e.getPlayClient().sendPacket(new CPacketCloseWindow(e.getPacket().getWindowId()));
                    QuestManager.setReadingQuestBook(false);
                }
            }
            QuestManager.setCurrentQuestsData(tempQuestList);
            QuestManager.setCurrentDiscoveryData(tempDiscoveryList);
        }
    }

    public ItemStack windowClick(ItemStack stack, int slotId, int windowId, int mouseButton, ClickType type, NetHandlerPlayClient client) {
        short short1 = transactionId++;
        client.sendPacket(new CPacketClickWindow(windowId, slotId, mouseButton, type, stack, short1));
        return stack;
    }

    public boolean parseQuest(ItemStack item) {
        if(!item.hasDisplayName()) return true;

        String displayName = item.getDisplayName();
        displayName = displayName.substring(0, displayName.length() - 1);
        displayName = Utils.stripColor(displayName).replace("À", "").replace("\u058E", "");

        QuestStatus status = null;

        List<String> lore = Utils.getLore(item);
        if(lore.size() <= 0) return true;

        if(lore.get(0).contains("Completed!")) {
            status = QuestStatus.COMPLETED;
        }else if(lore.get(0).contains("Started")) {
            status = QuestStatus.STARTED;
        }else if(lore.get(0).contains("Can start")) {
            status = QuestStatus.CAN_START;
        }else if(lore.get(0).contains("Cannot start")) {
            status = QuestStatus.CANNOT_START;
        }

        if(status == null)
            return false;

        int minLevel = Integer.valueOf(Utils.stripColor(lore.get(2)).replace("✔ Combat Lv. Min: ", "").replace("✖ Combat Lv. Min: ", ""));
        QuestSize size = QuestSize.valueOf(Utils.stripColor(lore.get(3)).replace("- Length: ", "").toUpperCase());

        String description = "";
        for(int i = 5; i < lore.size(); i ++) {
            if(lore.get(i).equalsIgnoreCase(TextFormatting.GRAY + "Right click to track")) {
                break;
            }
            description = description + Utils.stripColor(lore.get(i));
        }

        tempQuestList.add(new QuestInfo(displayName, status, minLevel, size, description, lore));
        return true;
    }
    
    public boolean parseDiscovery(ItemStack item) {
        if(!item.hasDisplayName()) return true;

        String displayName = item.getDisplayName();
        displayName = displayName.substring(0, displayName.length() - 1);
        
        DiscoveryType discoveryType = null;
        if (displayName.charAt(1) == 'e') {
            discoveryType = DiscoveryType.WORLD;
        } else if (displayName.charAt(1) == 'f') {
            discoveryType = DiscoveryType.TERRITORY;
        } else if (displayName.charAt(1) == 'b') {
            discoveryType = DiscoveryType.SECRET;
        }
        
        List<String> lore = Utils.getLore(item);

        int minLevel;
        try {
            minLevel = Integer.valueOf(Utils.stripColor(lore.get(0)).replace("✔ Combat Lv. Min: ", ""));
        } catch (NumberFormatException ex) {
            return false;
        }

        String description = "";
        for(int i = 2; i < lore.size(); i ++) {
            description = description + Utils.stripColor(lore.get(i));
        }

        tempDiscoveryList.add(new DiscoveryInfo(displayName, minLevel, description, lore, discoveryType));
        return true;
    }

}
