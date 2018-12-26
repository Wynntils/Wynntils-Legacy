package cf.wynntils.modules.utilities.overlays;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import cf.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import cf.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OverlayEvents implements Listener {
    
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent e) {
        WarTimerOverlay.warMessage(e);
    }
    
    @SubscribeEvent
    public void onWorldJoin(WynnWorldJoinEvent e) {
        WarTimerOverlay.onWorldJoin(e);
    }
    
    @SubscribeEvent
    public void onTitle(PacketEvent.TitleEvent e) {
        WarTimerOverlay.onTitle(e);
    }

    public static long tickcounter = 0;

    /* XP Gain Messages */
    public static int oldxp = 0;
    public static String oldxppercent = "0.0";

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (OverlayConfig.GameUpdate.INSTANCE.enabled && Reference.onWorld) {
            /* XP Gain Messages */
            if (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.enabled) {
                if (tickcounter % (int) (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expUpdateRate * 20f) == 0) {
                    if (oldxp != PlayerInfo.getPlayerInfo().getCurrentXP()) {
                        if (!PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage().equals("")) {
                            if (oldxp < PlayerInfo.getPlayerInfo().getCurrentXP()) {
                                DecimalFormat df = new DecimalFormat("0.0");
                                float xpchange = Float.valueOf(PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage()) - Float.valueOf(oldxppercent);
                                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expMessageFormat
                                        .replace("%xo%", Integer.toString(oldxp))
                                        .replace("%xn%", Integer.toString(PlayerInfo.getPlayerInfo().getCurrentXP()))
                                        .replace("%xc%", Integer.toString(PlayerInfo.getPlayerInfo().getCurrentXP() - oldxp))
                                        .replace("%po%", oldxppercent)
                                        .replace("%pn%", PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage())
                                        .replace("%pc%", df.format(xpchange)));
                            }
                            oldxp = PlayerInfo.getPlayerInfo().getCurrentXP();
                            oldxppercent = PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage();
                        }
                    }
                }
            }
        }
        tickcounter++;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatToRedirect(ClientChatReceivedEvent e) {
        if (!Reference.onWorld || !OverlayConfig.GameUpdate.INSTANCE.enabled || e.getType() == ChatType.GAME_INFO)
            return;
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectHorse) {
            if (e.getMessage().getUnformattedText().contains("There is no room for a horse.")) {
                GameUpdateOverlay.queueMessage("§4There is no room for a horse.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("Since you interacted with your inventory, your horse has despawned.")) {
                GameUpdateOverlay.queueMessage("§dHorse despawned.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCombat) {
            // GENERAL
            if (e.getMessage().getUnformattedText().contains("You don't have enough mana to do that spell!")) {
                GameUpdateOverlay.queueMessage("§4Not enough mana.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("You have not unlocked this spell!")) {
                GameUpdateOverlay.queueMessage("§4Spell not unlocked.");
                e.setCanceled(true);
                return;
            // POTIONS
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ❤\\]")) {
                GameUpdateOverlay.queueMessage("§4" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ✺ for \\d+ seconds\\]")) {
                GameUpdateOverlay.queueMessage("§b" + Utils.stripColor(e.getMessage().getFormattedText()).replace("for", "over"));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ✤ Strength for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage("§2" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ❋ Agility for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage("§f" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ✦ Dexterity for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage("§e" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ❉ Intelligence for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage("§b" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ✹ Defense for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage("§4" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            // MAGE
            } else if (e.getMessage().getUnformattedText().contains("Sorry, you can't teleport... Try moving away from blocks.")) {
                GameUpdateOverlay.queueMessage("§4Can't teleport - move away from blocks.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ gave you \\[\\+\\d+ ❤\\]")) {
                String[] res = e.getMessage().getFormattedText().split(" ");
                GameUpdateOverlay.queueMessage("§4" + res[3].substring(2) + " ❤] §7(§b" + res[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                GameUpdateOverlay.queueMessage("§4" + e.getMessage().getFormattedText().split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage("§bCleared §7all potion effects");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ gave you \\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                String[] res = e.getMessage().getFormattedText().split(" ");
                GameUpdateOverlay.queueMessage("§4" + res[3].substring(2) + " ❤] §7(§b" + res[0].replace("§r", "") + "§7)");
                GameUpdateOverlay.queueMessage("§bCleared §7all potion effects (§b" + res[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                GameUpdateOverlay.queueMessage("§4" + e.getMessage().getFormattedText().split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage("§bCleared §7all potion effects");
                GameUpdateOverlay.queueMessage("§bRemoved §7all fire");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ gave you \\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                String[] res = e.getMessage().getFormattedText().split(" ");
                GameUpdateOverlay.queueMessage("§4" + res[3].substring(2) + " ❤] §7(§b" + res[0].replace("§r", "") + "§7)");
                GameUpdateOverlay.queueMessage("§bCleared §7all potion effects (§b" + res[0].replace("§r", "") + "§7)");
                GameUpdateOverlay.queueMessage("§bRemoved §7all fire (§b" + res[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            // ARCHER
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("+3 minutes speed boost.")) {
                GameUpdateOverlay.queueMessage("§b+3 minutes §7speed boost");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ gave you \\+3 minutes speed boost\\.")) {
                GameUpdateOverlay.queueMessage("§b+3 minutes §7speed boost (" + e.getMessage().getFormattedText().split(" ")[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            }
            //TODO: Combat messages for Assassin & Warrior
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectOther) {
            if (e.getMessage().getUnformattedText().contains(" unused skill points! Click with your compass to use them!")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[3] + " §6skill points available.");
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains(" is now level ")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[0] + " §6is now level §e" + res[4]);
                e.setCanceled(true);
                return;
            } else if (e.getMessage().getUnformattedText().contains("You must identify this item before using it.")) {
                GameUpdateOverlay.queueMessage("§4Item not identified.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ is not a .+ weapon\\. You must use a .+\\.")) {
                GameUpdateOverlay.queueMessage("§4This weapon is not from your class.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ is for level \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage("§4You are not a high enough level to use this item.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ requires your .+ skill to be at least \\d+\\.")) {
                String[] res = Utils.stripColor(e.getMessage().getFormattedText()).split(" ");
                GameUpdateOverlay.queueMessage("§4You don't have enough " + res[res.length - 7] + " to use this item.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectServer) {
            if (e.getMessage().getUnformattedText().contains("The server is restarting in ")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§4" + res[5] + " " + res[6].replace(".", "") + " until server restart");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectQuest) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]")) {
                GameUpdateOverlay.queueMessage("§7Quest book updated.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:")) {
                GameUpdateOverlay.queueMessage(e.getMessage().getFormattedText().replace("[", "").replace("]", "").replace("§r", ""));
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectMerchants) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).equals("Item Identifier: Okay, I'll identify them now!")) {
                GameUpdateOverlay.queueMessage("§dIdentifying Item(s)...");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("Item Identifier: It is done\\. Your items? has been identified\\. The magic it contains will now blossom\\.")) {
                GameUpdateOverlay.queueMessage("§dItem(s) Identified!");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).startsWith("Item Buyer: You sold me: ")) {
                String[] res = e.getMessage().getFormattedText().split("§");
                int countCommon = 0;
                int countUnique = 0;
                int countRare = 0;
                int countSet = 0;
                int countLegendary = 0;
                int countMythic = 0;
                int total = 0;
                for (String s : res) {
                    if (s.startsWith("f")) {
                        countCommon++;
                        total++;
                    } else if (s.startsWith("b")) {
                        countLegendary++;
                        total++;
                    } else if (s.startsWith("5") && !s.equals("5Item Buyer: ")) {
                        countMythic++;
                        total++;
                    } else if (s.startsWith("d") && !s.equals("dYou sold me: ") && !s.equals("d, ") && !s.equals("d and ") && !s.equals("d for a total of ")) {
                        countRare++;
                        total++;
                    } else if (s.startsWith("a")) {
                        countSet++;
                        total++;
                    } else if (s.startsWith("e")) {
                        if (s.matches("e\\d+")) {
                            GameUpdateOverlay.queueMessage("§dSold " + total + " (§f" + countCommon + "§d/§e" + countUnique + "§d/" + countRare + "/§a" + countSet + "§d/§b" + countLegendary + "§d/§5" + countMythic + "§d) item(s) for §a" + s.replace("e", "") + (char) 0xB2 + "§d.");
                            e.setCanceled(true);
                        } else {
                            countUnique++;
                            total++;
                        }
                    }
                }
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).endsWith(" Merchant: Thank you for your business. Come again!")) {
                GameUpdateOverlay.queueMessage("§dPurchase complete.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).endsWith(" Merchant: I'm afraid you cannot afford that item.")) {
                GameUpdateOverlay.queueMessage("§dYou cannot afford that item.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginLocal) {
            String colorStrippedMessage = Utils.stripColor(e.getMessage().getFormattedText());
            if (colorStrippedMessage.endsWith(" has just logged in!")) {
                if (colorStrippedMessage.startsWith("[HERO]")) {
                    GameUpdateOverlay.queueMessage("§a→ §5[§dHERO§5] §d" + colorStrippedMessage.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (colorStrippedMessage.startsWith("[VIP+]")) {
                    GameUpdateOverlay.queueMessage("§a→ §3[§bVIP+§3] §b" + colorStrippedMessage.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (colorStrippedMessage.startsWith("[VIP]")) {
                    GameUpdateOverlay.queueMessage("§a→ §2[§aVIP§2] §a" + colorStrippedMessage.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                }
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginFriend) {
            String colorStrippedMessage = Utils.stripColor(e.getMessage().getFormattedText());
            if (colorStrippedMessage.contains(" has logged into server ") && colorStrippedMessage.contains(" as a")) {
                String[] res = colorStrippedMessage.split(" ");
                if (res.length == 9) {
                    GameUpdateOverlay.queueMessage("§a→ §2" + res[0] + " [§a" + res[5] + "§2/§a" + res[8] + "§2]");
                } else if (res.length == 10) {
                    GameUpdateOverlay.queueMessage("§a→ §2" + res[0] + " [§a" + res[5] + "§2/§a" + res[8] + " " + res[9] + "§2]");
                }
                e.setCanceled(true);
                return;
            } else if (colorStrippedMessage.matches(".+ left the game\\.")) {
                GameUpdateOverlay.queueMessage("§4← §2" + colorStrippedMessage.split(" ")[0]);
                e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onWynnTerritoyChange(WynnTerritoryChangeEvent e) {
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enabled) {
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.leave && !e.getOldTerritory().equals("Waiting")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryLeaveFormat
                        .replace("%t%", e.getOldTerritory()));
            }
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enter && !e.getNewTerritory().equals("Waiting")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryEnterFormat
                        .replace("%t%", e.getNewTerritory()));
            }
        }
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        GameUpdateOverlay.resetMessages();
    }
}
