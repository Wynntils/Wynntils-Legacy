/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import com.wynntils.modules.utilities.overlays.hud.TerritoryFeedOverlay;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;

public class OverlayEvents implements Listener {

    private static boolean wynnExpTimestampNotified = false;

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

    private static long tickcounter = 0;

    /* XP Gain Messages */
    private static int oldxp = 0;
    private static String oldxppercent = "0.0";

    /* Update overlay consts */
    private static final char PROF_COOKING = 'Ⓐ';
    private static final char PROF_MINING = 'Ⓑ';
    private static final char PROF_WOODCUTTING = 'Ⓒ';
    private static final char PROF_JEWELING = 'Ⓓ';
    private static final char PROF_SCRIBING = 'Ⓔ';
    private static final char PROF_TAILORING = 'Ⓕ';
    private static final char PROF_WEAPONSMITHING = 'Ⓖ';
    private static final char PROF_ARMOURING = 'Ⓗ';
    private static final char PROF_WOODWORKING = 'Ⓘ';
    private static final char PROF_FARMING = 'Ⓙ';
    private static final char PROF_FISHING = 'Ⓚ';
    private static final char PROF_ALCHEMISM = 'Ⓛ';

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (Reference.onWorld && e.phase == TickEvent.Phase.END) {
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
            /*Inventory full message*/
            if (OverlayConfig.GameUpdate.GameUpdateInventoryMessages.INSTANCE.enabled) {
                if (tickcounter % (int) (OverlayConfig.GameUpdate.GameUpdateInventoryMessages.INSTANCE.inventoryUpdateRate * 20f) == 0) {
                    IInventory inv = Minecraft.getMinecraft().player.inventory;
                    int itemCounter = 0;
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        if (!inv.getStackInSlot(i).isEmpty()) {
                            itemCounter++;
                        }
                    }

                    if (itemCounter == inv.getSizeInventory() - 1) {
                        GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.GameUpdateInventoryMessages.INSTANCE.inventoryMessageFormat);
                    }

                }
            }
            tickcounter++;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatToRedirect(ChatEvent.Pre e) {
        // This doesn't seem like the best way to do it - but I couldn't come up with much better -Bedo
        for (Overlay overlay : FrameworkManager.registeredOverlays.get(Priority.LOW)) {
            if (overlay instanceof GameUpdateOverlay) {
                if (!overlay.active) {
                    GameUpdateOverlay.resetMessages();
                    return;
                }
                break;
            }
        }
        if (!Reference.onWorld) return;
        if (Utils.stripColor(e.getMessage().getFormattedText()).split(" ")[0].matches("\\[\\d+:\\d+\\]")) {
            if (!wynnExpTimestampNotified) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§4[" + Reference.NAME + "] WynnExpansion's chat timestamps detected, please use " + Reference.NAME + "' chat timestamps for full compatibility."));
                wynnExpTimestampNotified = true;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectHorse) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).equals("There is no room for a horse.")) {
                GameUpdateOverlay.queueMessage("§4There is no room for a horse.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("Since you interacted with your inventory, your horse has despawned.")) {
                GameUpdateOverlay.queueMessage("§dHorse despawned.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("Your horse is scared to come out right now, too many mobs are nearby.")) {
                GameUpdateOverlay.queueMessage("§4Too many mobs nearby to spawn your horse");
                e.setCanceled(true);
                return;
            }
        }

        if (OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            if (OverlayConfig.ToastsSettings.INSTANCE.enableQuestCompleted && e.getMessage().getFormattedText().matches("^§[ae] {5,}§r§[ae]§l\\w.*§r$")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.QUEST_COMPLETED, "Quest Completed!", Utils.stripColor(e.getMessage().getFormattedText()).trim().replace("Mini-Quest - ", "")));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableAreaDiscovered && e.getMessage().getFormattedText().matches("^(§e)? {5,}(§r§e)?((?![0-9]).)*§r$")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.AREA_DISCOVERED, "Area Discovered!", Utils.stripColor(e.getMessage().getFormattedText()).trim()));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableDiscovery && e.getMessage().getFormattedText().matches("^ {5,}§r§b\\w.*§r$")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "Discovery Found!", Utils.stripColor(e.getMessage().getFormattedText()).trim()));
            }
        }

        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCombat) {
            // GENERAL
            if (Utils.stripColor(e.getMessage().getFormattedText()).equals("You don't have enough mana to do that spell!")) {
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
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("You already have that potion active...")) {
                GameUpdateOverlay.queueMessage("§4" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            // MAGE
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("Sorry, you can't teleport... Try moving away from blocks.")) {
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
            // WARRIOR
            else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ has given you 10% resistance\\.")) {
                GameUpdateOverlay.queueMessage("§b+10% resistance §7(" + e.getMessage().getFormattedText().split(" ")[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            }
            else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ has given you 15% resistance\\.")) {
                GameUpdateOverlay.queueMessage("§b+15% resistance §7(" + e.getMessage().getFormattedText().split(" ")[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            }
            else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ has given you 20% resistance and 10% strength\\.")) {
                GameUpdateOverlay.queueMessage("§b+20% resistance §7& §b+10% strength §7(" + e.getMessage().getFormattedText().split(" ")[0].replace("§r", "") + "§7)");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectOther) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).matches("You still have \\d+ unused skill points! Click with your compass to use them!")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[3] + " §6skill points available.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ is now level \\d+")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[0] + " §6is now level §e" + res[4]);
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches(".+ is now level \\d+ in [" + PROF_COOKING + "-" + PROF_ALCHEMISM +  "] (Fishing|Woodcutting|Mining|Farming|Scribing|Jeweling|Alchemism|Cooking|Weaponsmithing|Tailoring|Woodworking|Armouring)")) {
                String[] res = e.getMessage().getUnformattedText().split(" ");
                GameUpdateOverlay.queueMessage("§e" + res[0] + " §6is now §e" + res[6] + " " + res[7] + "§6 level §e" + res[4]);
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("You must identify this item before using it.")) {
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
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("This potion is for Lv\\. \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage("§4You are not a high enough level to use this potion.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("[Please empty some space in your inventory first]")) {
                GameUpdateOverlay.queueMessage("§7Not enough inventory space.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("You have never been to that area!")) {
                GameUpdateOverlay.queueMessage("§4" + Utils.stripColor(e.getMessage().getFormattedText()));
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectSoulPoint) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).equals("As the sun rises, you feel a little bit safer...")) {
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("\\[\\+\\d+ Soul Points?\\]")) {
                e.setCanceled(true);
                GameUpdateOverlay.queueMessage("§d" + e.getMessage().getUnformattedText().substring(1, 14));
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectServer) {
            if (Utils.stripColor(e.getMessage().getFormattedText()).matches("The server is restarting in \\d+ (seconds?|minutes?)\\.")) {
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
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("Item Identifier: It is done\\. Your items? (has|have) been identified\\. The magic (it|they) contains? will now blossom\\.")) {
                GameUpdateOverlay.queueMessage("§dItem(s) Identified!");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).startsWith("Blacksmith: You ")) {
                boolean sold = e.getMessage().getFormattedText().split(" ")[2].equals("sold");
                String[] res = e.getMessage().getFormattedText().split("§");
                int countCommon = 0;
                int countUnique = 0;
                int countRare = 0;
                int countSet = 0;
                int countLegendary = 0;
                int countMythic = 0;
                int countCrafted = 0;
                int total = 0;
                for (String s : res) {
                    if (s.startsWith("f")) {
                        countCommon++;
                        total++;
                    } else if (s.startsWith("b")) {
                        countLegendary++;
                        total++;
                    } else if (s.startsWith("5") && !s.equals("5Blacksmith: ")) {
                        countMythic++;
                        total++;
                    } else if (s.startsWith("d") && !s.equals("dYou sold me: ") && !s.equals("dYou scrapped: ") && !s.equals("d, ") && !s.equals("d and ") && !s.equals("d for a total of ")) {
                        countRare++;
                        total++;
                    } else if (s.startsWith("a")) {
                        countSet++;
                        total++;
                    } else if (s.startsWith("3")) {
                        countCrafted++;
                        total++;
                    } else if (s.startsWith("e")) {
                        if (s.matches("e\\d+")) {
                            String message;
                            if (sold) {
                                message = "§dSold " + total + " (§f" + countCommon + "§d/§e" + countUnique + "§d/" + countRare + "/§a" + countSet + "§d/§b" + countLegendary + "§d/§5" + countMythic + "§d/§3" + countCrafted + "§d) item(s) for §a" + s.replace("e", "") + (char) 0xB2 + "§d.";
                            } else {
                                message = "§dScrapped " + total + " (§f" + countCommon + "§d/§e" + countUnique + "§d/" + countRare + "/§a" + countSet + "§d/§b" + countLegendary + "§d/§5" + countMythic + "§d/§3" + countCrafted + "§d) item(s) for §e" + s.replace("e", "") + " scrap§d.";
                            }
                            GameUpdateOverlay.queueMessage(message);
                            e.setCanceled(true);
                        } else {
                            countUnique++;
                            total++;
                        }
                    }
                }
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("Blacksmith: I can't buy that item! I only accept weapons, accessories, and armour.")) {
                GameUpdateOverlay.queueMessage("§dYou can only sell weapons, accessories, and armour here.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).equals("You can't scrap this item!")) {
                GameUpdateOverlay.queueMessage("§4This item cannot be scrapped.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("^.+ Merchant: Thank you for your business. Come again!")) {
                GameUpdateOverlay.queueMessage("§dPurchase complete.");
                e.setCanceled(true);
                return;
            } else if (Utils.stripColor(e.getMessage().getFormattedText()).matches("^.+ Merchant: I'm afraid you cannot afford that item.")) {
                GameUpdateOverlay.queueMessage("§dYou cannot afford that item.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginLocal) {
            String colorStrippedMessage = Utils.stripColor(e.getMessage().getFormattedText());
            if (colorStrippedMessage.matches("^\\[.+\\] .+ has just logged in!")) {
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
            // Not sure on the nether server format -Bedo
            if (colorStrippedMessage.matches(".+ has logged into server (WC|HB|WAR|N)\\d+ as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter)") && e.getMessage().getFormattedText().startsWith("§a")) {
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
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginGuild) {
            String colorStrippedMessage = Utils.stripColor(e.getMessage().getFormattedText());
            if (colorStrippedMessage.matches(".+ has logged into server (WC|HB|WAR)\\d+ as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter)") && e.getMessage().getFormattedText().startsWith("§b")) {
                String[] res = colorStrippedMessage.split(" ");
                if (res.length == 9) {
                    GameUpdateOverlay.queueMessage("§a→ §3" + res[0] + " [§b" + res[5] + "§3/§b" + res[8] + "§3]");
                } else if (res.length == 10) {
                    GameUpdateOverlay.queueMessage("§a→ §3" + res[0] + " [§b" + res[5] + "§3/§b" + res[8] + " " + res[9] + "§3]");
                }
                e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onTerritoryWar(WynnGuildWarEvent e) {
        if (!Reference.onServer)
            return;
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.ONLY_OWN_GUILD && !e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName()) && !e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName()))
            return;
        String message = "§b";
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.DISTINGUISH_OWN_GUILD) {
            if (e.getType() == WynnGuildWarEvent.WarUpdateType.ATTACKED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§c";
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§a";
                }
            } else if (e.getType() == WynnGuildWarEvent.WarUpdateType.DEFENDED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§2";
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§4";
                }
            } else {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§4";
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    message = "§2";
                }
            }
        }
        String attackerName = OverlayConfig.TerritoryFeed.INSTANCE.useTag ? e.getAttackerTag() : e.getAttackerName();
        String defenderName = OverlayConfig.TerritoryFeed.INSTANCE.useTag ? e.getDefenderTag() : e.getDefenderName();
        if (OverlayConfig.TerritoryFeed.INSTANCE.shortMessages) {
            switch (e.getType()) {
                case ATTACKED:
                    message += e.getTerritoryName() + " | " + attackerName + " ⚔ " + defenderName;
                    break;
                case DEFENDED:
                    message += e.getTerritoryName() + " | " + defenderName + " \uD83D\uDEE1 " + attackerName;
                    break;
                case CAPTURED:
                    message += e.getTerritoryName() + " | " + attackerName + " ⚑ " + defenderName;
                    break;
            }
        } else {
            switch (e.getType()) {
                case ATTACKED:
                    message += "[" + defenderName + "]'s territory " + e.getTerritoryName() + " is being attacked by [" + attackerName + "]";
                    break;
                case DEFENDED:
                    message += "[" + attackerName + "]'s attack on [" + defenderName + "]'s territory " + e.getTerritoryName() + " was defended!";
                    break;
                case CAPTURED:
                    message += "[" + attackerName + "] has captured " + e.getTerritoryName() + " from [" + defenderName + "]";
                    break;
            }
        }
        TerritoryFeedOverlay.queueMessage(message);
    }

    @SubscribeEvent
    public void onServerLeave(WynncraftServerEvent.Leave e) {
        ModCore.mc().gameSettings.heldItemTooltips = true;
    }

    @SubscribeEvent
    public void onWynnTerritoyChange(WynnTerritoryChangeEvent e) {
        if (OverlayConfig.ToastsSettings.INSTANCE.enableTerritoryEnter && OverlayConfig.ToastsSettings.INSTANCE.enableToast && !e.getNewTerritory().equals("Waiting")) {
            ToastOverlay.addToast(new Toast(Toast.ToastType.TERRITORY, "Now entering", e.getNewTerritory()));
        }
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
