/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import com.wynntils.modules.utilities.overlays.hud.ObjectivesOverlay;
import com.wynntils.modules.utilities.overlays.hud.TerritoryFeedOverlay;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.util.Arrays;

public class OverlayEvents implements Listener {

    private static boolean wynnExpTimestampNotified = false;

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent e) {
        WarTimerOverlay.warMessage(e);
        ObjectivesOverlay.checkObjectiveReached(e);
    }

    @SubscribeEvent
    public void onWorldJoin(WynnWorldEvent.Join e) {
        WarTimerOverlay.onWorldJoin(e);
    }

    @SubscribeEvent
    public void onTitle(PacketEvent<SPacketTitle> e) {
        WarTimerOverlay.onTitle(e);
    }

    @SubscribeEvent
    public void onPlayerInfoRender(GuiOverlapEvent.PlayerInfoOverlap.RenderList e) {
        if (!Reference.onWorld || !OverlayConfig.PlayerInfo.INSTANCE.replaceVanilla) return;

        e.setCanceled(true);
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

    /* Toasts */
    private static final String filterList = "Upper|Lower|Mid|East|West|North|South|Entrance|Exit|Edge|Close|Far |-";
    private static final String[] blackList = new String[]{"Transition", "to "};

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
                                float xpchange = Float.parseFloat(PlayerInfo.getPlayerInfo().getCurrentXPAsPercentage()) - Float.parseFloat(oldxppercent);
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
        if (!UtilitiesModule.getModule().getGameUpdateOverlay().active) {
            GameUpdateOverlay.resetMessages();
            return;
        }

        if (!Reference.onWorld || e.getMessage().getUnformattedText().equals(" ")) return;
        String messageText = e.getMessage().getUnformattedText();
        String formattedText = e.getMessage().getFormattedText();
        if (messageText.split(" ")[0].matches("\\[\\d+:\\d+\\]")) {
            if (!wynnExpTimestampNotified) {
                TextComponentString text = new TextComponentString("[" + Reference.NAME + "] WynnExpansion's chat timestamps detected, please use " + Reference.NAME + "' chat timestamps for full compatibility.");
                text.getStyle().setColor(TextFormatting.DARK_RED);
                Minecraft.getMinecraft().player.sendMessage(text);
                wynnExpTimestampNotified = true;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectHorse) {
            switch (messageText) {
                case "There is no room for a horse.":
                    GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "There is no room for a horse.");
                    e.setCanceled(true);
                    return;
                case "Since you interacted with your inventory, your horse has despawned.":
                    GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "Horse despawned.");
                    e.setCanceled(true);
                    return;
                case "Your horse is scared to come out right now, too many mobs are nearby.":
                    GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "Too many mobs nearby to spawn your horse");
                    e.setCanceled(true);
                    return;
            }
        }

        if (OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            if (OverlayConfig.ToastsSettings.INSTANCE.enableQuestCompleted && formattedText.matches("^(" + TextFormatting.GREEN + "|" + TextFormatting.YELLOW + ") {5,}" + TextFormatting.RESET + "(" + TextFormatting.GREEN + "|" + TextFormatting.YELLOW + ")" + TextFormatting.BOLD + "\\w.*" + TextFormatting.RESET + "$") && !messageText.contains("Powder Manual")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.QUEST_COMPLETED, "Quest Completed!", messageText.trim().replace("Mini-Quest - ", "")));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableAreaDiscovered && formattedText.matches("^(" + TextFormatting.YELLOW + ")? {5,}(" + TextFormatting.RESET + TextFormatting.YELLOW + ")?(?!§)((?![0-9]).)*" + TextFormatting.RESET + "$") && !messageText.contains("Battle Summary") && !messageText.contains("Powder Manual") && !messageText.contains("hunted mode")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.AREA_DISCOVERED, "Area Discovered!", messageText.trim()));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableDiscovery && formattedText.matches("^ {5,}" + TextFormatting.RESET + TextFormatting.AQUA + "\\w.*" + TextFormatting.RESET + "$")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "Discovery Found!", messageText.trim()));
            }
        }

        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCombat) {
            // GENERAL
            if (messageText.equals("You don't have enough mana to do that spell!")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "Not enough mana.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You have not unlocked this spell!")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "Spell not unlocked.");
                e.setCanceled(true);
                return;
            // POTIONS
            } else if (messageText.matches("\\[\\+\\d+ ❤\\]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✺ for \\d+ seconds\\]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + messageText.replace("for", "over"));
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✤ Strength for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_GREEN + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❋ Agility for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.WHITE + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✦ Dexterity for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.YELLOW + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❉ Intelligence for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✹ Defence for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You already have that potion active...")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + messageText);
                e.setCanceled(true);
                return;
            // MAGE
            } else if (messageText.equals("Sorry, you can't teleport... Try moving away from blocks.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "Can't teleport - move away from blocks.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} gave you \\[\\+\\d+ ❤\\]")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + res[3].substring(2) + " ❤] " + TextFormatting.GRAY + "(" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + formattedText.split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Cleared " + TextFormatting.GRAY + "all potion effects");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} gave you \\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + res[3].substring(2) + " ❤] " + TextFormatting.GRAY + "(" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Cleared " + TextFormatting.GRAY + "all potion effects (" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + formattedText.split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Cleared " + TextFormatting.GRAY + "all potion effects");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Removed " + TextFormatting.GRAY + "all fire");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} gave you \\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + res[3].substring(2) + " ❤] " + TextFormatting.GRAY + "(" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Cleared " + TextFormatting.GRAY + "all potion effects (" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "Removed " + TextFormatting.GRAY + "all fire (" + TextFormatting.AQUA + res[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\]\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + messageText.substring(0, messageText.length() - 1));
                e.setCanceled(true);
                return;
            // ARCHER
            } else if (messageText.equals("+3 minutes speed boost.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "+3 minutes " + TextFormatting.GRAY + "speed boost");
                if (OverlayConfig.ConsumableTimer.INSTANCE.captureChat) {
                    ConsumableTimerOverlay.addBasicTimer("Speed boost", 3 * 60 - 1);
                }
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} gave you \\+3 minutes speed boost\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "+3 minutes " + TextFormatting.GRAY + "speed boost (" + formattedText.split(" ")[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                if (OverlayConfig.ConsumableTimer.INSTANCE.captureChat) {
                    ConsumableTimerOverlay.addBasicTimer("Speed boost", 3 * 60 - 1);
                }
                e.setCanceled(true);
                return;
            }
            // WARRIOR
            else if (messageText.matches("[a-zA-Z0-9_]{1,16} has given you 10% resistance\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "+10% resistance " + TextFormatting.GRAY + "(" + formattedText.split(" ")[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            }
            else if (messageText.matches("[a-zA-Z0-9_]{1,16} has given you 15% resistance\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "+15% resistance " + TextFormatting.GRAY + "(" + formattedText.split(" ")[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            }
            else if (messageText.matches("[a-zA-Z0-9_]{1,16} has given you 20% resistance and 10% strength\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.AQUA + "+20% resistance " + TextFormatting.GRAY + "& " + TextFormatting.AQUA + "+10% strength " + TextFormatting.GRAY + "(" + formattedText.split(" ")[0].replace(TextFormatting.RESET.toString(), "") + TextFormatting.GRAY + ")");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectOther) {
            if (messageText.matches("You still have \\d+ unused skill points! Click with your compass to use them!")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.YELLOW + res[3] + TextFormatting.GOLD + " skill points available.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} is now level \\d+")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.YELLOW + res[0] + TextFormatting.GOLD + " is now level " + TextFormatting.YELLOW + res[4]);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} is now level \\d+ in [" + PROF_COOKING + "-" + PROF_ALCHEMISM + "] (Fishing|Woodcutting|Mining|Farming|Scribing|Jeweling|Alchemism|Cooking|Weaponsmithing|Tailoring|Woodworking|Armouring)")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.YELLOW + res[0] + TextFormatting.GOLD + " is now " + TextFormatting.YELLOW + res[6] + " " + res[7] + TextFormatting.GOLD + " level " + TextFormatting.YELLOW + res[4]);
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You must identify this item before using it.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "Item not identified.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ is not a .+ weapon\\. You must use a .+\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "This weapon is not from your class.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ is for combat level \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are not a high enough level to use this item.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ requires your .+ skill to be at least \\d+\\.")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You don't have enough " + res[res.length - 7] + " to use this item.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("This potion is for Combat Lv\\. \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You are not a high enough level to use this potion.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("[Please empty some space in your inventory first]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.GRAY + "Not enough inventory space.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You have never been to that area!")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + messageText);
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectSoulPoint) {
            if (messageText.equals("As the sun rises, you feel a little bit safer...")) {
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ Soul Points?\\]")) {
                e.setCanceled(true);
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + messageText.substring(1, 14));
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectServer) {
            if (messageText.matches("The server is restarting in \\d+ (seconds?|minutes?)\\.")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + res[5] + " " + res[6].replace(".", "") + " until server restart");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectQuest) {
            if (messageText.startsWith("[Quest Book Updated]")) {
                GameUpdateOverlay.queueMessage(TextFormatting.GRAY + "Quest book updated.");
                e.setCanceled(true);
                return;
            } else if (messageText.startsWith("[New Quest Started:")) {
                GameUpdateOverlay.queueMessage(formattedText.replace("[", "").replace("]", "").replace(TextFormatting.RESET.toString(), ""));
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectMerchants) {
            if (messageText.equals("Item Identifier: Okay, I'll identify them now!")) {
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "Identifying Item(s)...");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("Item Identifier: It is done\\. Your items? (has|have) been identified\\. The magic (it|they) contains? will now blossom\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "Item(s) Identified!");
                e.setCanceled(true);
                return;
            } else if (messageText.startsWith("Blacksmith: You ")) {
                boolean sold = formattedText.split(" ")[2].equals("sold");
                String[] res = formattedText.split("§");
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
                                message = TextFormatting.LIGHT_PURPLE + "Sold " + total + " (" + TextFormatting.WHITE + countCommon + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.YELLOW + countUnique + TextFormatting.LIGHT_PURPLE + "/" + countRare + "/" + TextFormatting.GREEN + countSet + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.AQUA + countLegendary + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.DARK_PURPLE + countMythic + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.DARK_AQUA + countCrafted + TextFormatting.LIGHT_PURPLE + ") item(s) for " + TextFormatting.GREEN + s.replace("e", "") + (char) 0xB2 + TextFormatting.LIGHT_PURPLE + ".";
                            } else {
                                message = TextFormatting.LIGHT_PURPLE + "Scrapped " + total + " (" + TextFormatting.WHITE + countCommon + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.YELLOW + countUnique + TextFormatting.LIGHT_PURPLE + "/" + countRare + "/" + TextFormatting.GREEN + countSet + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.AQUA + countLegendary + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.DARK_PURPLE + countMythic + TextFormatting.LIGHT_PURPLE + "/" + TextFormatting.DARK_AQUA + countCrafted + TextFormatting.LIGHT_PURPLE + ") item(s) for " + TextFormatting.YELLOW + s.replace("e", "") + " scrap" + TextFormatting.LIGHT_PURPLE + ".";
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
            } else if (messageText.equals("Blacksmith: I can't buy that item! I only accept weapons, accessories, potions, armour, ingredients, resources, and crafted items.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "You can only sell weapons, accessories, potions, armour, ingredients, resources, and crafted items here.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("Blacksmith: I can't buy that item! I only accept weapons, accessories, potions, armour, and crafted items.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You can only scrap weapons, accessories, potions, armour, and crafted items here.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ Merchant: Thank you for your business. Come again!")) {
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "Purchase complete.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ Merchant: I'm afraid you cannot afford that item.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.LIGHT_PURPLE + "You cannot afford that item.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginLocal) {
            if (messageText.matches("^\\[.+\\] .+ has just logged in!")) {
                if (messageText.startsWith("[HERO]")) {
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_PURPLE + "[" + TextFormatting.LIGHT_PURPLE + "HERO" + TextFormatting.DARK_PURPLE + "] " + TextFormatting.LIGHT_PURPLE + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (messageText.startsWith("[VIP+]")) {
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_AQUA + "[" + TextFormatting.AQUA + "VIP+" + TextFormatting.DARK_AQUA + "] " + TextFormatting.AQUA + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (messageText.startsWith("[VIP]")) {
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_GREEN + "[" + TextFormatting.GREEN + "VIP" + TextFormatting.DARK_GREEN + "] " + TextFormatting.GREEN + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                }
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginFriend) {
            // Not sure on the nether server format -Bedo
            if (messageText.matches("[a-zA-Z0-9_]{1,16} has logged into server (WC|HB|WAR|N)\\d+ as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter|Shaman|Skyseer)À?") && formattedText.startsWith(TextFormatting.GREEN.toString())) {
                String[] res = messageText.split(" ");
                if (res.length == 9) {
                    if (res[8].equals("ArcherÀ")) res[8] = "Shaman";
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_GREEN + res[0] + " [" + TextFormatting.GREEN + res[5] + TextFormatting.DARK_GREEN + "/" + TextFormatting.GREEN + res[8] + TextFormatting.DARK_GREEN + "]");
                } else if (res.length == 10) {
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_GREEN + res[0] + " [" + TextFormatting.GREEN + res[5] + TextFormatting.DARK_GREEN + "/" + TextFormatting.GREEN + res[8] + " " + res[9] + TextFormatting.DARK_GREEN + "]");
                }
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_]{1,16} left the game\\.")) {
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "← " + TextFormatting.DARK_GREEN + messageText.split(" ")[0]);
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginGuild) {
            if (messageText.matches("[a-zA-Z0-9_]{1,16} has logged into server (WC|HB|WAR)\\d+ as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter|Shaman|Skyseer)À?") && formattedText.startsWith(TextFormatting.AQUA.toString())) { // À temp for Shaman
                String[] res = messageText.split(" ");
                if (res.length == 9) {
                    if (res[8].equals("ArcherÀ")) res[8] = "Shaman";  // Temp replace for Shaman (Same changes as above)
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_AQUA + res[0] + " [" + TextFormatting.AQUA + res[5] + TextFormatting.DARK_AQUA + "/" + TextFormatting.AQUA + res[8] + TextFormatting.DARK_AQUA + "]");
                } else if (res.length == 10) {
                    GameUpdateOverlay.queueMessage(TextFormatting.GREEN + "→ " + TextFormatting.DARK_AQUA + res[0] + " [" + TextFormatting.AQUA + res[5] + TextFormatting.DARK_AQUA + "/" + TextFormatting.AQUA + res[8] + " " + res[9] + TextFormatting.DARK_AQUA + "]");
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
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.ONLY_OWN_GUILD && WebManager.getPlayerProfile() != null && !e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName()) && !e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName()))
            return;
        TextFormatting color = TextFormatting.AQUA;
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.DISTINGUISH_OWN_GUILD && WebManager.getPlayerProfile() != null) {
            if (e.getType() == WynnGuildWarEvent.WarUpdateType.ATTACKED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.RED;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.GREEN;
                }
            } else if (e.getType() == WynnGuildWarEvent.WarUpdateType.DEFENDED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.DARK_GREEN;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.DARK_RED;
                }
            } else {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.DARK_RED;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = TextFormatting.DARK_GREEN;
                }
            }
        }
        String attackerName = OverlayConfig.TerritoryFeed.INSTANCE.useTag ? e.getAttackerTag() : e.getAttackerName();
        String defenderName = OverlayConfig.TerritoryFeed.INSTANCE.useTag ? e.getDefenderTag() : e.getDefenderName();
        String rawMessage = "";
        if (OverlayConfig.TerritoryFeed.INSTANCE.shortMessages) {
            switch (e.getType()) {
                case ATTACKED:
                    rawMessage = e.getTerritoryName() + " | " + attackerName + " ⚔ " + defenderName;
                    break;
                case DEFENDED:
                    rawMessage = e.getTerritoryName() + " | " + defenderName + " \uD83D\uDEE1 " + attackerName;
                    break;
                case CAPTURED:
                    rawMessage = e.getTerritoryName() + " | " + attackerName + " ⚑ " + defenderName;
                    break;
            }
        } else {
            switch (e.getType()) {
                case ATTACKED:
                    rawMessage = "[" + defenderName + "]'s territory " + e.getTerritoryName() + " is being attacked by [" + attackerName + "]";
                    break;
                case DEFENDED:
                    rawMessage = "[" + attackerName + "]'s attack on [" + defenderName + "]'s territory " + e.getTerritoryName() + " was defended!";
                    break;
                case CAPTURED:
                    rawMessage = "[" + attackerName + "] has captured " + e.getTerritoryName() + " from [" + defenderName + "]";
                    break;
            }
        }
        TerritoryFeedOverlay.queueMessage(color + rawMessage);
    }

    @SubscribeEvent
    public void onServerLeave(WynncraftServerEvent.Leave e) {
        ModCore.mc().gameSettings.heldItemTooltips = true;
        ObjectivesOverlay.restoreVanillaScoreboard();
    }

    @SubscribeEvent
    public void onServerJoin(WynncraftServerEvent.Login e) {
        ObjectivesOverlay.updateOverlayActivation();
    }

    @SubscribeEvent
    public void onInventoryDraw(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        // Refresh overlay if hidden and inventory is open
        ObjectivesOverlay.refreshVisibility();
    }

    @SubscribeEvent
    public void onChestDraw(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        // Refresh overlay if hidden and chest is open
        ObjectivesOverlay.refreshVisibility();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChestOpen(GuiOverlapEvent.ChestOverlap.InitGui e) {
        ObjectivesOverlay.checkRewardsClaimed(e);
    }

    @SubscribeEvent
    public void onDisplayObjective(PacketEvent<SPacketDisplayObjective> e) {
        ObjectivesOverlay.checkForSidebar(e.getPacket());
    }

    @SubscribeEvent
    public void onScoreboardObjective(PacketEvent<SPacketScoreboardObjective> e) {
        ObjectivesOverlay.checkSidebarRemoved(e.getPacket());
    }

    @SubscribeEvent
    public void onUpdateScore(PacketEvent<SPacketUpdateScore> e) {
        ObjectivesOverlay.checkObjectiveUpdate(e.getPacket());
    }

    @SubscribeEvent
    public void onWynnTerritoyChange(WynnTerritoryChangeEvent e) {
        if (OverlayConfig.ToastsSettings.INSTANCE.enableTerritoryEnter && OverlayConfig.ToastsSettings.INSTANCE.enableToast && !e.getNewTerritory().equals("Waiting")) {
            if (Arrays.stream(blackList).parallel().anyMatch(e.getNewTerritory()::contains)) return;

            String newTerritoryArea = e.getNewTerritory().replaceAll(filterList, "").replaceAll(" {2,}", " ").trim();
            String oldTerritoryArea = e.getOldTerritory().replaceAll(filterList, "").replaceAll(" {2,}", " ").trim();
            if (newTerritoryArea.equalsIgnoreCase(oldTerritoryArea)) return;

            ToastOverlay.addToast(new Toast(Toast.ToastType.TERRITORY, "Now entering", newTerritoryArea));
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
        ModCore.mc().addScheduledTask(GameUpdateOverlay::resetMessages);
    }

    @SubscribeEvent
    public void onPlayerDeath(GameEvent.PlayerDeath e) {
        ConsumableTimerOverlay.clearConsumables();
    }

    @SubscribeEvent
    public void onEffectApplied(PacketEvent<SPacketEntityEffect> e) {
        if (OverlayConfig.ConsumableTimer.INSTANCE.captureChat) {
            SPacketEntityEffect effect = e.getPacket();
            Potion potion = Potion.getPotionById(effect.getEffectId());
            if (potion.getName().equals("effect.moveSpeed")) {
                int newTime = effect.getDuration() / 20;
                ConsumableTimerOverlay.addBasicTimer("Speed boost", newTime-1);
            }
        }
    }
}
