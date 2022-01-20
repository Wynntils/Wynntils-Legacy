/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.richpresence.events;

import com.sun.jna.Pointer;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.utils.ServerUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.enums.InventoryResult;
import com.wynntils.modules.core.instances.inventory.FakeInventory;
import com.wynntils.modules.core.instances.inventory.InventoryOpenByItem;
import com.wynntils.modules.richpresence.RichPresenceModule;
import com.wynntils.modules.richpresence.discordgamesdk.IDiscordActivityEvents;
import com.wynntils.modules.richpresence.profiles.SecretContainer;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile.PlayerTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RPCJoinHandler implements IDiscordActivityEvents.on_activity_join_callback {

    private static final Pattern dmRegex = Pattern.compile("§7(\\[(.*) ➤ (.*)\\])(.*)");

    boolean waitingLobby = false;
    boolean waitingInvite = false;

    boolean sentInvite = false;

    SecretContainer lastSecret = null;

    long delayTime = 0;

    public RPCJoinHandler() {
        FrameworkManager.getEventBus().register(this);
    }

    public void apply(Pointer eventData, String joinSecret) {
        lastSecret = new SecretContainer(joinSecret);
        if (lastSecret.getOwner().isEmpty() || lastSecret.getRandomHash().isEmpty() || lastSecret.getWorldType().equals("HB") && WebManager.getPlayerProfile() != null && WebManager.getPlayerProfile().getTag() != PlayerTag.HERO)
            return;

        RichPresenceModule.getModule().getRichPresence().setJoinSecret(lastSecret);

        if (!Reference.onServer) {
            ServerData serverData = ServerUtils.getWynncraftServerData(true);
            ServerUtils.connect(serverData);
            waitingLobby = true;
            return;
        }
        if (Reference.onWorld) {
            if (Reference.getUserWorld().replace("WC", "").replace("HB", "").equals(Integer.toString(lastSecret.getWorld())) && Reference.getUserWorld().replaceAll("\\d+", "").equals(lastSecret.getWorldType())) {
                sentInvite = true;
                McIf.player().sendChatMessage("/msg " + lastSecret.getOwner() + " " + lastSecret.getRandomHash());
                return;
            }

            McIf.player().sendChatMessage("/hub");
            waitingLobby = true;
            return;
        }

        joinWorld(lastSecret.getWorldType(), lastSecret.getWorld());
        waitingInvite = true;
    }

    @SubscribeEvent
    public void onLobby(RenderPlayerEvent.Post e) {
        if (Reference.onWorld || !waitingLobby || delayTime > Minecraft.getSystemTime()) return;

        waitingLobby = false;
        waitingInvite = true;
        joinWorld(lastSecret.getWorldType(), lastSecret.getWorld());
    }

    @SubscribeEvent
    public void onWorldJoin(WynnWorldEvent.Join e) {
        if (!waitingInvite) return;
        sentInvite = true;
        waitingInvite = false;
        McIf.player().sendChatMessage("/msg " + lastSecret.getOwner() + " " + lastSecret.getRandomHash());
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent e) {
        if (e.getType() != ChatType.CHAT && e.getType() != ChatType.SYSTEM) return;

        // handles the invitation
        if (lastSecret != null && McIf.getUnformattedText(e.getMessage()).startsWith("You have been invited to join " + lastSecret.getOwner())) {
            McIf.player().sendChatMessage("/party join " + lastSecret.getOwner());

            lastSecret = null;
            return;
        }

        // handles the user join
        if (sentInvite && McIf.getUnformattedText(e.getMessage()).startsWith("[" + McIf.player().getName())) {
            sentInvite = false;
            e.setCanceled(true);
            return;
        }

        // handles the party owner
        if (PlayerInfo.get(SocialData.class).getPlayerParty().isPartying()) {
            String text = McIf.getFormattedText(e.getMessage());
            Matcher m = dmRegex.matcher(text);

            if (!m.matches()) return;

            String content = TextFormatting.getTextWithoutFormattingCodes(m.group(4).substring(1));
            String user = TextFormatting.getTextWithoutFormattingCodes(m.group(2));

            if (!RichPresenceModule.getModule().getRichPresence().validSecret(content.substring(0, content.length() - 1)))
                return;

            e.setCanceled(true);
            McIf.player().sendChatMessage("/party invite " + user);
        }
    }

    @SubscribeEvent
    public void onTitle(ClientChatReceivedEvent e) {
        String text = McIf.getUnformattedText(e.getMessage());
        if ((text.equals("You are already connected to this server!") || text.equals("You're rejoining too quickly! Give us a moment to save your data.")) && waitingInvite) {
            waitingLobby = true;
            waitingInvite = false;
            delayTime = McIf.getSystemTime() + 2000;
        }
    }

    private static Pattern WYNNCRAFT_SERVERS_WINDOW_TITLE_PATTERN = Pattern.compile("Wynncraft Servers: Page \\d+");

    /**
     * Search for a Wynncraft World.
     * only works if the user is on lobby!
     *
     * @param worldType   the world type to join
     * @param worldNumber The world to join
     */
    private static void joinWorld(String worldType, int worldNumber) {
        if (!Reference.onServer || Reference.onWorld) return;

        FakeInventory serverSelector = new FakeInventory(WYNNCRAFT_SERVERS_WINDOW_TITLE_PATTERN, new InventoryOpenByItem(0));
        serverSelector.onReceiveItems(inventory -> {
            String prefix = "";
            if (worldType.equals("WC")) {
                // US Servers
                prefix = "";
            } else if (worldType.equals("HB")) {
                prefix = "Beta ";
            }

            boolean onCorrectCategory = inventory.findItem(prefix + "World ", FilterType.STARTS_WITH) != null;
            if (!onCorrectCategory) {
                String worldCategory = "";
                if (worldType.equals("WC")) {
                    worldCategory = "US Servers";
                } else if (worldType.equals("HB")) {
                    worldCategory = "Hero Beta";
                }

                Pair<Integer, ItemStack> categoryItem = inventory.findItem(worldCategory, FilterType.EQUALS_IGNORE_CASE);
                if (categoryItem != null) {
                    inventory.clickItem(categoryItem.a, 1, ClickType.PICKUP);
                    return;
                }

                inventory.close();
                return;
            }

            Pair<Integer, ItemStack> world = inventory.findItem(prefix + "World " + worldNumber, FilterType.EQUALS_IGNORE_CASE);
            if (world != null) {
                inventory.clickItem(world.a, 1, ClickType.PICKUP);
                inventory.close();
                return;
            }

            Pair<Integer, ItemStack> nextPage = inventory.findItem("Next Page", FilterType.CONTAINS);
            if (nextPage != null) {
                serverSelector.clickItem(nextPage.a, 1, ClickType.PICKUP);
                return;
            }

            inventory.close();
        }).onClose((inv, result) -> {
            if (result != InventoryResult.CLOSED_SUCCESSFULLY) return;
            joinWorld(worldType, worldNumber);
        });

        serverSelector.open();
    }

}
