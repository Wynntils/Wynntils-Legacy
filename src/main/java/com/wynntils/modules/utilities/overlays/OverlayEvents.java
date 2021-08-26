/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.managers.MountHorseManager;
import com.wynntils.modules.utilities.overlays.hud.*;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import net.minecraft.client.Minecraft;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import static net.minecraft.util.text.TextFormatting.*;

public class OverlayEvents implements Listener {

    private static final Pattern CHEST_COOLDOWN_PATTERN = Pattern.compile("Please wait an additional ([0-9]+) minutes? before opening this chest.");
    private static final Pattern GATHERING_COOLDOWN_PATTERN = Pattern.compile("^You need to wait ([0-9]+) seconds after logging in to gather from this resource!");
    private static final Pattern SERVER_RESTART_PATTERN = Pattern.compile("The server is restarting in ([0-9]+) (minutes?|seconds?)");

    private static boolean wynnExpTimestampNotified = false;
    private long loginTime;

    private static String totemName;

    private boolean isVanished = false; // used in onEffectApplied

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
    private static long msgcounter = 0;

    /* XP Gain Messages */
    private static int oldxp = 0;
    private static String oldxppercent = "0.0";

    /* Toasts */
    private static final String filterList = "Upper|Lower|Mid|East|West|North|South|Entrance|Exit|Edge|Close|Far |-";
    private static final String[] blackList = new String[]{"Transition", "to "};

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (Reference.onWorld && e.phase == TickEvent.Phase.END) {
            /* XP Gain Messages */
            CharacterData data = PlayerInfo.get(CharacterData.class);

            if (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.enabled) {
                if (tickcounter % (int) (OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expUpdateRate * 20f) == 0) {
                    if (oldxp != data.getCurrentXP()) {
                        if (!data.getCurrentXPAsPercentage().equals("")) {
                            if (oldxp < data.getCurrentXP()) {
                                DecimalFormat df = new DecimalFormat("0.0");
                                float xpchange = Float.parseFloat(data.getCurrentXPAsPercentage()) - Float.parseFloat(oldxppercent);
                                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.GameUpdateEXPMessages.INSTANCE.expMessageFormat
                                        .replace("%xo%", Integer.toString(oldxp))
                                        .replace("%xn%", Integer.toString(data.getCurrentXP()))
                                        .replace("%xc%", Integer.toString(data.getCurrentXP() - oldxp))
                                        .replace("%po%", oldxppercent)
                                        .replace("%pn%", data.getCurrentXPAsPercentage())
                                        .replace("%pc%", df.format(xpchange)));
                            }
                            oldxp = data.getCurrentXP();
                            oldxppercent = data.getCurrentXPAsPercentage();
                        }
                    }
                }
            }
            /*Inventory full message*/
            if (OverlayConfig.GameUpdate.GameUpdateInventoryMessages.INSTANCE.enabled) {
                if (tickcounter % (int) (OverlayConfig.GameUpdate.GameUpdateInventoryMessages.INSTANCE.inventoryUpdateRate * 20f) == 0) {
                    IInventory inv = McIf.player().inventory;
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

    @SubscribeEvent
    public void onLevelUp(GameEvent.LevelUp e) {
        if (!OverlayConfig.ToastsSettings.INSTANCE.enableToast ||
                !OverlayConfig.ToastsSettings.INSTANCE.enableLevelUp) return;

        if (e instanceof GameEvent.LevelUp.Profession) {
            if ((e.getNewLevel() % 5) == 0 && e.getNewLevel() <= 110) {
                // For professions, only display Toast when you get new perks
                String profession = ((GameEvent.LevelUp.Profession) e).getProfession().getName();
                ToastOverlay.addToast(new Toast(Toast.ToastType.LEVEL_UP, "Level Up!", "You are now level " + e.getNewLevel() + " in " + profession));
            }
        } else {
            ToastOverlay.addToast(new Toast(Toast.ToastType.LEVEL_UP, "Level Up!", "You are now level " + e.getNewLevel()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onScrollUsed(ChatEvent.Post e) {
        String messageText = McIf.getUnformattedText(e.getMessage());
        if (messageText.matches(".*? for [0-9]* seconds\\]")) { //consumable message
            //10 tick delay, since chat event occurs before default consumable event
            new Delay(() -> ConsumableTimerOverlay.addExternalScroll(messageText), 10);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatToRedirect(ChatEvent.Pre e) {
        if (!UtilitiesModule.getModule().getGameUpdateOverlay().active) {
            GameUpdateOverlay.resetMessages();
            return;
        }

        if (!Reference.onWorld || McIf.getUnformattedText(e.getMessage()).equals(" ")) return;
        String messageText = McIf.getUnformattedText(e.getMessage());
        String formattedText = McIf.getFormattedText(e.getMessage());
        if (messageText.split(" ")[0].matches("\\[\\d+:\\d+\\]")) {
            if (!wynnExpTimestampNotified) {
                TextComponentString text = new TextComponentString("[" + Reference.NAME + "] WynnExpansion's chat timestamps detected, please use " + Reference.NAME + "' chat timestamps for full compatibility.");
                text.getStyle().setColor(DARK_RED);
                McIf.player().sendMessage(text);
                wynnExpTimestampNotified = true;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectHorse) {
            switch (messageText) {
                case "There is no room for a horse.":
                    GameUpdateOverlay.queueMessage(DARK_RED + "There is no room for a horse.");
                    e.setCanceled(true);
                    MountHorseManager.preventNextMount();
                    return;
                case "Since you interacted with your inventory, your horse has despawned.":
                    GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "Horse despawned.");
                    e.setCanceled(true);
                    return;
                case "Your horse is scared to come out right now, too many mobs are nearby.":
                    GameUpdateOverlay.queueMessage(DARK_RED + "Too many mobs nearby to spawn your horse");
                    e.setCanceled(true);
                    MountHorseManager.preventNextMount();
                    return;
            }
        }

        if (OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            if (OverlayConfig.ToastsSettings.INSTANCE.enableQuestCompleted && formattedText.matches("^(" + GREEN + "|" + YELLOW + ") {5,}" + RESET + "(" + GREEN + "|" + YELLOW + ")" + BOLD + "\\w.*" + RESET + "$") && !messageText.contains("Powder Manual")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.QUEST_COMPLETED, "Quest Completed!", messageText.trim().replace("Mini-Quest - ", "")));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableAreaDiscovered && (formattedText.matches("^(" + YELLOW + ")? {5,}(" + RESET + YELLOW + ")?(?!§)((?![0-9§]).)*" + RESET + "$") || formattedText.matches("^ {5,}" + RESET + GOLD + "Area Discovered: " + RESET + YELLOW + "\\w.*" + RESET + LIGHT_PURPLE + " \\(\\+\\d+ XP\\)" + RESET + "$")) && !messageText.contains("Battle Summary") && !messageText.contains("Powder Manual") && !messageText.contains("hunted mode")) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.AREA_DISCOVERED, "Area Discovered!", messageText.replace("Area Discovered: ", "").trim()));
            } else if (OverlayConfig.ToastsSettings.INSTANCE.enableDiscovery && (formattedText.matches("^ {5,}" + RESET + AQUA + "\\w.*" + RESET + "$") || formattedText.matches("^ {5,}" + RESET + DARK_AQUA + "Secret Discovery: " + RESET + AQUA + "\\w.*" + RESET + LIGHT_PURPLE + " \\(\\+\\d+ XP\\)" + RESET + "$"))) {
                ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "Discovery Found!", messageText.replace("Secret Discovery: ", "").trim()));
            }
        }

        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCombat) {
            // GENERAL
            if (messageText.equals("You don't have enough mana to do that spell!")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "Not enough mana.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You have not unlocked this spell!")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "Spell not unlocked.");
                e.setCanceled(true);
                return;
            // POTIONS
            } else if (messageText.matches("\\[\\+\\d+ ❤\\]")) {
                GameUpdateOverlay.queueMessage(DARK_RED + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✺ for \\d+ seconds\\]")) {
                GameUpdateOverlay.queueMessage(AQUA + messageText.replace("for", "over"));
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✤ Strength for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(DARK_GREEN + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❋ Agility for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(WHITE + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✦ Dexterity for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(YELLOW + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❉ Intelligence for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(AQUA + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ✹ Defence for \\d+ seconds]")) {
                GameUpdateOverlay.queueMessage(DARK_RED + messageText);
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You already have that potion active...")) {
                GameUpdateOverlay.queueMessage(DARK_RED + messageText);
                e.setCanceled(true);
                return;
            // MAGE
            } else if (messageText.equals("Sorry, you can't teleport... Try moving away from blocks.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "Can't teleport - move away from blocks.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} gave you \\[\\+\\d+ ❤\\]")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(DARK_RED + res[3].substring(2) + " ❤] " + GRAY + "(" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + formattedText.split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage(AQUA + "Cleared " + GRAY + "all potion effects");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} gave you \\[\\+\\d+ ❤\\] Cleared all potion effects\\.")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(DARK_RED + res[3].substring(2) + " ❤] " + GRAY + "(" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                GameUpdateOverlay.queueMessage(AQUA + "Cleared " + GRAY + "all potion effects (" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + formattedText.split(" ")[0].substring(2) + " ❤]");
                GameUpdateOverlay.queueMessage(AQUA + "Cleared " + GRAY + "all potion effects");
                GameUpdateOverlay.queueMessage(AQUA + "Removed " + GRAY + "all fire");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} gave you \\[\\+\\d+ ❤\\] Cleared all potion effects Removed all fire\\.")) {
                String[] res = formattedText.split(" ");
                GameUpdateOverlay.queueMessage(DARK_RED + res[3].substring(2) + " ❤] " + GRAY + "(" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                GameUpdateOverlay.queueMessage(AQUA + "Cleared " + GRAY + "all potion effects (" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                GameUpdateOverlay.queueMessage(AQUA + "Removed " + GRAY + "all fire (" + AQUA + res[0].replace(RESET.toString(), "") + GRAY + ")");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("\\[\\+\\d+ ❤\\]\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + messageText.substring(0, messageText.length() - 1));
                e.setCanceled(true);
                return;
            // ARCHER
            } else if (messageText.equals("+3 minutes speed boost.")) {
                GameUpdateOverlay.queueMessage(AQUA + "+3 minutes " + GRAY + "speed boost");
                if (OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) {
                    ConsumableTimerOverlay.addBasicTimer("Speed boost", 3 * 60);
                }
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} gave you \\+3 minutes speed boost\\.")) {
                GameUpdateOverlay.queueMessage(AQUA + "+3 minutes " + GRAY + "speed boost (" + formattedText.split(" ")[0].replace(RESET.toString(), "") + GRAY + ")");
                if (OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) {
                    ConsumableTimerOverlay.addBasicTimer("Speed boost", 3 * 60);
                }
                e.setCanceled(true);
                return;
            }
            // WARRIOR
            else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} has given you 10% resistance\\.")) {
                GameUpdateOverlay.queueMessage(AQUA + "+10% resistance " + GRAY + "(" + formattedText.split(" ")[0].replace(RESET.toString(), "") + GRAY + ")");
                if (OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) {
                    ConsumableTimerOverlay.addBasicTimer("War Scream I", 2 * 60);
                }
                e.setCanceled(true);
                return;
            }
            else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} has given you 15% resistance\\.")) {
                GameUpdateOverlay.queueMessage(AQUA + "+15% resistance " + GRAY + "(" + formattedText.split(" ")[0].replace(RESET.toString(), "") + GRAY + ")");
                if (OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) {
                    ConsumableTimerOverlay.addBasicTimer("War Scream II", 3 * 60);
                }
                e.setCanceled(true);
                return;
            }
            else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} has given you 20% resistance and 10% strength\\.")) {
                GameUpdateOverlay.queueMessage(AQUA + "+20% resistance " + GRAY + "& " + AQUA + "+10% strength " + GRAY + "(" + formattedText.split(" ")[0].replace(RESET.toString(), "") + GRAY + ")");
                if (OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) {
                    ConsumableTimerOverlay.addBasicTimer("War Scream III", 4 * 60);
                }
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectOther) {
            if (messageText.matches("You have \\d+ unused Skill Points! Right-Click while holding your compass to use them!")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(YELLOW + res[2] + GOLD + " skill points available.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} is now level \\d+")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(YELLOW + res[0] + GOLD + " is now level " + YELLOW + res[4]);
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} is now level \\d+ in [" + ProfessionType.ALCHEMISM.getIcon() + "-" + ProfessionType.ALCHEMISM.getIcon() + "] (Fishing|Woodcutting|Mining|Farming|Scribing|Jeweling|Alchemism|Cooking|Weaponsmithing|Tailoring|Woodworking|Armouring)")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(YELLOW + res[0] + GOLD + " is now " + YELLOW + res[6] + " " + res[7] + GOLD + " level " + YELLOW + res[4]);
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You must identify this item before using it.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "Item not identified.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ is not a .+ weapon\\. You must use a .+\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "This weapon is not from your class.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ is for combat level \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "You are not a high enough level to use this item.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ requires your .+ skill to be at least \\d+\\.")) {
                String[] res = messageText.split(" ");
                GameUpdateOverlay.queueMessage(DARK_RED + "You don't have enough " + res[res.length - 7] + " to use this item.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("This potion is for Combat Lv\\. \\d+\\+ only\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "You are not a high enough level to use this potion.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("[Please empty some space in your inventory first]")) {
                GameUpdateOverlay.queueMessage(GRAY + "Not enough inventory space.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("You have never been to that area!")) {
                GameUpdateOverlay.queueMessage(DARK_RED + messageText);
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
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + messageText.substring(1, 14));
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectResourcePack) {
            if (messageText.equals("Thank you for using the WynnPack. Enjoy the game!")) {
                e.setCanceled(true);
                return;
            }
            if (messageText.equals("Loading Resource Pack...")) {
                GameUpdateOverlay.queueMessage(GRAY + messageText);
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectClass) {
            if (messageText.equals("Select a class! Each class is saved individually across all servers, you can come back at any time with /class and select another class!")) {
                GameUpdateOverlay.queueMessage(GOLD + "Select a character!");
                e.setCanceled(true);
                return;
            }
            if (messageText.equals("Your class has been automatically been selected. Use /class to change your class, or /toggle autojoin to turn this feature off.")) {
                GameUpdateOverlay.queueMessage(GOLD + "Automatically selected your last character!");
                GameUpdateOverlay.queueMessage(GRAY + "Use /class to change your class,");
                GameUpdateOverlay.queueMessage(GRAY + "or /toggle autojoin to turn this off.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectQuest) {
            if (messageText.startsWith("[Quest Book Updated]")) {
                GameUpdateOverlay.queueMessage(GRAY + "Quest book updated.");
                e.setCanceled(true);
                return;
            } else if (messageText.startsWith("[New Quest Started:")) {
                GameUpdateOverlay.queueMessage(formattedText.replace("[", "").replace("]", "").replace(RESET.toString(), ""));
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectMerchants) {
            if (messageText.equals("Item Identifier: Okay, I'll identify them now!")) {
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "Identifying Item(s)...");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("Item Identifier: It is done\\. Your items? (has|have) been identified\\. The magic (it|they) contains? will now blossom\\.")) {
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "Item(s) Identified!");
                e.setCanceled(true);
                return;
            } else if (messageText.startsWith("Blacksmith: You ")) {

                EnumMap<ItemTier, Integer> itemCounts = new EnumMap(ItemTier.class); // counter for sold/scrapped items

                String[] res = formattedText.split("§");

                for (String s : res) {
                    if (s.equals("dYou sold me: ") //non-item founds
                            || s.equals("dYou scrapped: ")
                            || s.equals("d, ")
                            || s.equals("d and ")
                            || s.equals("d for a total of ")
                            || s.equals("5Blacksmith: ")) {
                        continue;
                    }
                    if (s.matches("e\\d+")) { // the final part of the message
                        int total = 0;
                        for (ItemTier tier : ItemTier.values()) {
                            total += itemCounts.getOrDefault(tier, 0);
                        }

                        // creates the counting part of the message
                        StringBuilder messageCounts = new StringBuilder();
                        for (ItemTier tier : ItemTier.values()) {
                            messageCounts.append('/' + tier.getTextColor() + itemCounts.getOrDefault(tier, 0));
                            messageCounts.append(LIGHT_PURPLE);
                        }

                        messageCounts.append(") item(s) for ");
                        messageCounts.setCharAt(0, '(');

                        // creates the full message
                        StringBuilder message = new StringBuilder();
                        if (formattedText.split(" ")[2].equals("sold")) { // normal selling
                            message.append(LIGHT_PURPLE + "Sold " + total + " ");
                            message.append(messageCounts);
                            message.append(GREEN + s.replace("e", "") + EmeraldSymbols.EMERALDS + LIGHT_PURPLE + ".");

                        } else { // scrapping
                            message.append(LIGHT_PURPLE + "Scrapped " + total + " ");
                            message.append(messageCounts);
                            message.append(YELLOW + s.replace("e", "") + " scrap" + LIGHT_PURPLE + ".");
                        }

                        GameUpdateOverlay.queueMessage(message.toString()); // send the redirect messsage
                        e.setCanceled(true); // remove the chat message
                        continue;
                    }
                    // item counter
                    ItemTier tierToIncrease = ItemTier.fromColorCodeString(s);
                    if (tierToIncrease != null) {
                        itemCounts.put(tierToIncrease, itemCounts.getOrDefault(tierToIncrease, 0) + 1);
                    }
                }
                return;
            } else if (messageText.equals("Blacksmith: I can't buy that item! I only accept weapons, accessories, potions, armour, ingredients, resources, and crafted items.")) {
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "You can only sell weapons, accessories, potions, armour, ingredients, resources, and crafted items here.");
                e.setCanceled(true);
                return;
            } else if (messageText.equals("Blacksmith: I can't buy that item! I only accept weapons, accessories, potions, armour, and crafted items.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "You can only scrap weapons, accessories, potions, armour, and crafted items here.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ Merchant: Thank you for your business. Come again!")) {
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "Purchase complete.");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("^(?!.*\\].*).+ Merchant: I'm afraid you cannot afford that item.")) {
                GameUpdateOverlay.queueMessage(LIGHT_PURPLE + "You cannot afford that item.");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginLocal) {
            if (messageText.matches("^\\[.+\\] .+ has just logged in!")) {
                if (messageText.startsWith("[CHAMPION]")) {
                    GameUpdateOverlay.queueMessage(GREEN + "→ " + YELLOW + "[" + GOLD + "CHAMPION" + YELLOW + "] " + GOLD + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (messageText.startsWith("[HERO]")) {
                    GameUpdateOverlay.queueMessage(GREEN + "→ " + DARK_PURPLE + "[" + LIGHT_PURPLE + "HERO" + DARK_PURPLE + "] " + LIGHT_PURPLE + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (messageText.startsWith("[VIP+]")) {
                    GameUpdateOverlay.queueMessage(GREEN + "→ " + DARK_AQUA + "[" + AQUA + "VIP+" + DARK_AQUA + "] " + AQUA + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                } else if (messageText.startsWith("[VIP]")) {
                    GameUpdateOverlay.queueMessage(GREEN + "→ " + DARK_GREEN + "[" + GREEN + "VIP" + DARK_GREEN + "] " + GREEN + messageText.split(" ")[1]);
                    e.setCanceled(true);
                    return;
                }
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginFriend) {
            Pattern login = Pattern.compile("([a-zA-Z0-9_ ]{1,19}) has logged into server ((?:WC|HB|WAR|N)\\d+) as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter|Shaman|Skyseer)À?");
            Matcher loginMatcher = login.matcher(messageText);
            if (loginMatcher.matches() && formattedText.startsWith(GREEN.toString())) {
                String userName = loginMatcher.group(1);
                String userWorld = loginMatcher.group(2);
                String userClass = loginMatcher.group(3);
                if (userClass.equals("ArcherÀ")) userClass = "Shaman";
                GameUpdateOverlay.queueMessage(GREEN + "→ " + DARK_GREEN + userName + " [" + GREEN + userWorld + DARK_GREEN + "/" + GREEN + userClass + DARK_GREEN + "]");
                e.setCanceled(true);
                return;
            } else if (messageText.matches("[a-zA-Z0-9_ ]{1,19} left the game\\.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "← " + DARK_GREEN + StringUtils.substringBefore(messageText, " left the game"));
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectLoginGuild) {
            Pattern login = Pattern.compile("([a-zA-Z0-9_ ]{1,19}) has logged into server ((?:WC|HB|WAR|N)\\d+) as an? (Warrior|Knight|Mage|Dark Wizard|Assassin|Ninja|Archer|Hunter|Shaman|Skyseer)À?");
            Matcher loginMatcher = login.matcher(messageText);
            if (loginMatcher.matches() && formattedText.startsWith(AQUA.toString())) {
                String userName = loginMatcher.group(1);
                String userWorld = loginMatcher.group(2);
                String userClass = loginMatcher.group(3);
                if (userClass.equals("ArcherÀ")) userClass = "Shaman";
                GameUpdateOverlay.queueMessage(GREEN + "→ " + DARK_AQUA + userName + " [" + AQUA + userWorld + DARK_AQUA + "/" + AQUA + userClass + DARK_AQUA + "]");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectGatheringDura) {
            if (messageText.equals("Your tool has 0 durability left! You will not receive any new resources until you repair it at a Blacksmith.")) {
                if (msgcounter++ % 5 == 0)
                    GameUpdateOverlay.queueMessage(DARK_RED + "Your tool has 0 durability");
                e.setCanceled(true);
                return;
            }
        }
        if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCraftedDura) {
            if (messageText.equals("Your items are damaged and have become less effective. Bring them to a Blacksmith to repair them.")) {
                GameUpdateOverlay.queueMessage(DARK_RED + "Your items are damaged");
                e.setCanceled(true);
                return;
            }
        }

        Matcher gatheringMatcher = GATHERING_COOLDOWN_PATTERN.matcher(messageText);
        if (gatheringMatcher.find()) {
            int seconds = Integer.parseInt(gatheringMatcher.group(1));
            if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCooldown) {
                GameUpdateOverlay.queueMessage("Wait " + seconds + " seconds to gather");
                e.setCanceled(true);
            }

            if (OverlayConfig.ConsumableTimer.INSTANCE.showCooldown) {
                long timeNow = McIf.getSystemTime();
                int timeLeft = seconds - (int)(timeNow - loginTime)/1000;
                if (timeLeft > 0) {
                    ConsumableTimerOverlay.addBasicTimer("Gather cooldown", timeLeft, false);
                }
            }
            return;
        }

        Matcher chestMatcher = CHEST_COOLDOWN_PATTERN.matcher(messageText);
        if (chestMatcher.find()) {
            int minutes = Integer.parseInt(chestMatcher.group(1));
            if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectCooldown) {
                GameUpdateOverlay.queueMessage("Wait " + minutes + " minutes for loot chest");
                e.setCanceled(true);
            }

            if (OverlayConfig.ConsumableTimer.INSTANCE.showCooldown) {
                ConsumableTimerOverlay.addBasicTimer("Loot cooldown", minutes*60, true);
            }
            return;
        }

        // Server restart message handling
        Matcher restartMatcher = SERVER_RESTART_PATTERN.matcher(messageText);
        if (restartMatcher.find()) {
            if (OverlayConfig.ConsumableTimer.INSTANCE.showServerRestart) { // if you want the timer
                int seconds = Integer.parseInt(restartMatcher.group(1));

                if (restartMatcher.group(2).equals("minutes") || restartMatcher.group(2).equals("minute")) { // if it is in minutes
                    seconds *= 60;
                }
                ConsumableTimerOverlay.addBasicTimer("Server restart", seconds);
            }
            if (OverlayConfig.GameUpdate.RedirectSystemMessages.INSTANCE.redirectServer) { // if you want to redirect it
                GameUpdateOverlay.queueMessage(DARK_RED + "The server is restarting in " + restartMatcher.group(1) + " " + restartMatcher.group(2));
                e.setCanceled(true); // do not show the message in chat
            }
        }
    }

    @SubscribeEvent
    public void onMusicStart(MusicPlayerEvent.Playback.Start e) {
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChange)
            GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.musicChangeFormat
                    .replace("%np%", e.getSongName()));
    }

    @SubscribeEvent
    public void onTerritoryWar(WynnGuildWarEvent e) {
        if (!Reference.onServer)
            return;
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.ONLY_OWN_GUILD && WebManager.getPlayerProfile() != null && !e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName()) && !e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName()))
            return;
        TextFormatting color = AQUA;
        if (OverlayConfig.TerritoryFeed.INSTANCE.displayMode == OverlayConfig.TerritoryFeed.TerritoryFeedDisplayMode.DISTINGUISH_OWN_GUILD && WebManager.getPlayerProfile() != null) {
            if (e.getType() == WynnGuildWarEvent.WarUpdateType.ATTACKED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = RED;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = GREEN;
                }
            } else if (e.getType() == WynnGuildWarEvent.WarUpdateType.DEFENDED) {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = DARK_GREEN;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = DARK_RED;
                }
            } else {
                if (e.getDefenderName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = DARK_RED;
                } else if (e.getAttackerName().equals(WebManager.getPlayerProfile().getGuildName())) {
                    color = DARK_GREEN;
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
        ScoreboardOverlay.enableCustomScoreboard(false);
        ObjectivesOverlay.resetObjectives();
    }

    @SubscribeEvent
    public void onServerJoin(WynncraftServerEvent.Login e) {
        ScoreboardOverlay.enableCustomScoreboard(OverlayConfig.Scoreboard.INSTANCE.enableScoreboard);
    }

    @SubscribeEvent
    public void onInventoryDraw(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        // Refresh overlay if hidden and inventory is open
        ObjectivesOverlay.refreshVisibility();
    }

    @SubscribeEvent
    public void onChestDraw(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
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
    public void onWynnTerritoryChange(WynnTerritoryChangeEvent e) {
        if (OverlayConfig.ToastsSettings.INSTANCE.enableTerritoryEnter && OverlayConfig.ToastsSettings.INSTANCE.enableToast && !e.getNewTerritory().equals("Waiting")) {
            if (Arrays.stream(blackList).parallel().anyMatch(e.getNewTerritory()::contains)) return;

            String newTerritoryArea = e.getNewTerritory().replaceAll(filterList, "").replaceAll(" {2,}", " ").trim();
            String oldTerritoryArea = e.getOldTerritory().replaceAll(filterList, "").replaceAll(" {2,}", " ").trim();
            if (newTerritoryArea.equalsIgnoreCase(oldTerritoryArea)) return;

            ToastOverlay.addToast(new Toast(Toast.ToastType.TERRITORY, "Now entering", newTerritoryArea));
        }
        if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enabled) {
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.leave && !e.getOldTerritory().equals("")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryLeaveFormat
                        .replace("%t%", e.getOldTerritory()));
            }
            if (OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.enter && !e.getNewTerritory().equals("")) {
                GameUpdateOverlay.queueMessage(OverlayConfig.GameUpdate.TerritoryChangeMessages.INSTANCE.territoryEnterFormat
                        .replace("%t%", e.getNewTerritory()));
            }
        }
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        McIf.mc().addScheduledTask(GameUpdateOverlay::resetMessages);
        // WynnCraft seem to be off with its timer with around 10 seconds
        loginTime = McIf.getSystemTime() + 10000;
        msgcounter = 0;
    }

    @SubscribeEvent
    public void onPlayerDeath(GameEvent.PlayerDeath e) {
        ConsumableTimerOverlay.clearConsumables(false);
    }

    @SubscribeEvent
    public void onEffectApplied(PacketEvent<SPacketEntityEffect> e) {
        if (!Reference.onWorld || !OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) return;

        SPacketEntityEffect effect = e.getPacket();
        if (effect.getEntityId() != McIf.player().getEntityId()) return;

        Potion potion = Potion.getPotionById(effect.getEffectId());

        String timerName;

        // if the effect is speed timer is "Speed boost"
        if (potion == MobEffects.SPEED && effect.getAmplifier() == 2) {
            timerName = "Speed boost";
        }
        // if the effect is invisibility timer is "Vanish"
        else if (potion == MobEffects.INVISIBILITY && effect.getDuration() < 200) {
            timerName = "Vanish";
            isVanished = true;
        }
        // if the player isn't invisible (didn't use vanish)
        else if (potion == MobEffects.RESISTANCE) { // War Scream effect
            if (isVanished) { // remove the vanish indicator
                isVanished = false;
                return;
            }

            if (effect.getAmplifier() == 0) {
                timerName = "War Scream I";
            }
            else if (effect.getAmplifier() == 1) {
                timerName = "War Scream II";
            }
            else if (effect.getAmplifier() == 2) {
                timerName = "War Scream III";
            } else {
                return;
            }
        } else {
            return;
        }

        // create timer with name and duration (duration in ticks)/20 -> seconds
        McIf.mc().addScheduledTask(() ->
                ConsumableTimerOverlay.addBasicTimer(timerName, effect.getDuration() / 20));
    }

    @SubscribeEvent
    public void onEffectRemoved(PacketEvent<SPacketRemoveEntityEffect> e) {
        if (!Reference.onWorld || !OverlayConfig.ConsumableTimer.INSTANCE.showSpellEffects) return;

        SPacketRemoveEntityEffect effect = e.getPacket();
        if (effect.getEntity(McIf.world()) != McIf.player()) return;

        McIf.mc().addScheduledTask(() -> {
            Potion potion = effect.getPotion();

            // When removing speed boost from (archer)
            if (potion == MobEffects.SPEED) {
                ConsumableTimerOverlay.removeBasicTimer("Speed boost");
            }
            // When removing invisibility from assassin
            else if (potion == MobEffects.INVISIBILITY) {
                isVanished = false; // So it won't skip
                ConsumableTimerOverlay.removeBasicTimer("Vanish");
            }
        });
    }

    @SubscribeEvent
    public void onTotemEvent(SpellEvent.TotemSummoned e) {
        if (!OverlayConfig.ConsumableTimer.INSTANCE.trackTotem) return;

        ConsumableTimerOverlay.addBasicTimer("Totem Summoned", 59);
    }

    @SubscribeEvent
    public void onTotemEvent(SpellEvent.TotemActivated e) {
        if (!OverlayConfig.ConsumableTimer.INSTANCE.trackTotem) return;

        ConsumableTimerOverlay.removeBasicTimer("Totem Summoned");
        ConsumableTimerOverlay.removeBasicTimer(totemName);
        totemName = "Totem " + e.getLocation();
        ConsumableTimerOverlay.addBasicTimer(totemName, e.getTime());
    }

    @SubscribeEvent
    public void onTotemEvent(SpellEvent.TotemRemoved e) {
        if (!OverlayConfig.ConsumableTimer.INSTANCE.trackTotem) return;

        ConsumableTimerOverlay.removeBasicTimer("Totem Summoned");
        ConsumableTimerOverlay.removeBasicTimer(totemName);
    }

    @SubscribeEvent
    public void onMobTotemEvent(SpellEvent.MobTotemActivated e) {
        ConsumableTimerOverlay.addBasicTimer(e.getMobTotem().toString(), e.getTime());
    }

    @SubscribeEvent
    public void onMobTotemEvent(SpellEvent.MobTotemRemoved e) {
        ConsumableTimerOverlay.removeBasicTimer(e.getMobTotem().toString());
    }

}
