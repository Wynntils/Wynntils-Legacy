/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.commands;

import com.google.common.collect.Lists;
import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.utilities.managers.ServerListManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CommandServer extends CommandBase implements IClientCommand {
    private List<String> serverTypes = Lists.newArrayList("WC", "lobby", "GM", "DEV", "WAR", "HB");

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "s";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/s <command> [options]\n\ncommands:\nl,ls,list | list available servers\ni,info | get info about a server\nsp,nextsoulpoint | list servers with soul point timers\n\nmore detailed help:\n/s <command> help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (Reference.onServer) {
            if (args.length >= 1) {
                // String option = args[0];
                switch (args[0].toLowerCase()) {
                    case "list":
                    case "ls":
                    case "l":
                        serverList(server, sender, Arrays.copyOfRange(args, 1, args.length));
                        break;
                    case "info":
                    case "i":
                        serverInfo(server, sender, Arrays.copyOfRange(args, 1, args.length));
                        break;
                    case "nextsoulpoint":
                    case "sp":
                        nextSoulPoint(server, sender, args);
                        break;
                    default:
                        throw new CommandException(getUsage(sender));
                }
            } else {
                throw new CommandException(getUsage(sender));
            }
        }
    }

    private void nextSoulPoint(MinecraftServer server, ICommandSender sender, String[] args){
        Map<String, Integer> nextServers = new HashMap<>();

        // No need for checks if timer underneath 10min as I shorten the keySet anyways.
        for (String availableServer : ServerListManager.getAvailableServers().keySet()) {
            nextServers.put(availableServer, 20 - (ServerListManager.getServer(availableServer).getUptimeMinutes() % 20));
        }

        // Sort servers by time until soul point
        Map<String, Integer> sortedServers = nextServers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        if (args[1].equalsIgnoreCase("help")) {
            TextComponentString text = new TextComponentString("Usage: /s sp \nDefault: Prints 10 worlds with increasing lowest soul point timers");
            sender.sendMessage(text);
        } else {
            TextComponentString soulPointInfo = new TextComponentString("Approximate soul point times:" + "\n");
            soulPointInfo.getStyle()
                    .setBold(true)
                    .setColor(TextFormatting.AQUA);
            sender.sendMessage(soulPointInfo);

            List<String> keys = sortedServers.keySet().stream().limit(10).collect(Collectors.toList());
            for (String wynnServer : keys) {
                int uptimeMinutes = sortedServers.get(wynnServer);

                TextFormatting minuteColor;
                if (uptimeMinutes <= 2) {
                    minuteColor = TextFormatting.GREEN;
                } else if (uptimeMinutes <= 5) {
                    minuteColor = TextFormatting.YELLOW;
                } else {
                    minuteColor = TextFormatting.RED;
                }

                TextComponentString world = new TextComponentString(TextFormatting.BOLD + "-" + TextFormatting.GOLD);
                if (WebManager.getPlayerProfile().canUseSwitch()) {
                    TextComponentString serverLine = new TextComponentString(TextFormatting.BLUE + wynnServer);
                    serverLine.getStyle()
                            .setColor(TextFormatting.BLUE)
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/switch " + wynnServer))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Switch To " + TextFormatting.BLUE + wynnServer)));
                    world.appendSibling(serverLine);
                } else {
                    TextComponentString serverLine = new TextComponentString(TextFormatting.BLUE + wynnServer);
                    serverLine.getStyle()
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.RED + "HERO or higher rank is required to use /switch")));
                    world.appendSibling(serverLine);
                }

                if (uptimeMinutes == 1) {
                    world.appendText(" §b in " + minuteColor + uptimeMinutes + " minute");
                } else {
                    world.appendText(" §b in " + minuteColor + uptimeMinutes + " minutes");
                }

                sender.sendMessage(world);
            }
        }
    }

    private void serverList(MinecraftServer server, ICommandSender sender, String[] args) {
        List<String> options = new ArrayList<>();
        String selectedType = null;

        for (String arg : args) {
            for (String type : serverTypes) {
                if (arg.equalsIgnoreCase(type)) {
                    selectedType = type;
                    break;
                }
            }
            switch (arg.toLowerCase()) {
                case "group":
                case "g":
                    options.add("group");
                    break;
                case "sort":
                case "s":
                    options.add("sort");
                    options.add("group");
                    break;
                case "count":
                case "c":
                    options.add("count");
                    break;
                case "help":
                case "h":
                    options.add("help");
                    break;
            }
        }

        TextComponentString text;
        if (options.contains("help")) {
            text = new TextComponentString(
                    "Usage: /s list [type] [options]\n order of types and options does not matter\nDefault: print all servers oldest to new\n\ntypes:\n");
            for (String type : serverTypes) {
                text.appendText(String.format("  %s\n", type));
            }
            text.appendText("options:\n");
            text.appendText("  g, group : group servers by type\n");
            text.appendText("  s, sort : sort servers alphabetically, sets group flag\n");
            text.appendText("  c, count : print amount of online servers\n");
            text.appendText("  h, help : this help\n");
            sender.sendMessage(text);
            return;
        }

        int messageId = Utils.getRandom().nextInt(Integer.MAX_VALUE);
        ChatOverlay.getChat().printUnloggedChatMessage(new TextComponentString(TextFormatting.GRAY + "Calculating Servers..."), messageId);

        String finalSelectedType = selectedType;
        Utils.runAsync(() -> {
            try {
                Map<String, List<String>> onlinePlayers = WebManager.getOnlinePlayers();

                if (options.contains("group") && finalSelectedType == null) {
                    TextComponentString toEdit = new TextComponentString("Available servers" +
                            (options.contains("count") ? String.format(" (%d)", onlinePlayers.size()): "") + ":\n");

                    for (String type : serverTypes.subList(0, serverTypes.size() - 1)) {
                        toEdit.appendSibling(getFilteredServerList(onlinePlayers, type, options));
                        toEdit.appendText("\n");
                    }
                    toEdit.appendSibling(getFilteredServerList(onlinePlayers, serverTypes.get(serverTypes.size() - 1), options));

                    ChatOverlay.getChat().printUnloggedChatMessage(toEdit, messageId);  // updates the message
                    return;
                }

                if (finalSelectedType == null) {
                    ChatOverlay.getChat().printUnloggedChatMessage(
                            getFilteredServerList(onlinePlayers, "", options), messageId
                    );  // updates the message
                    return;
                }

                ChatOverlay.getChat().printUnloggedChatMessage(
                        getFilteredServerList(onlinePlayers, finalSelectedType, options), messageId
                );  // updates the message
            } catch (Exception ex) {
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString(
                                TextFormatting.RED +
                                "An error occurred while trying to get the servers!"
                        ).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TextComponentString(TextFormatting.RED + ex.getMessage())
                                ))),
                        messageId
                );

                ex.printStackTrace();
            }
        });
    }

    private static void serverInfo(MinecraftServer server, ICommandSender sender, String[] args) {
        int messageId = Utils.getRandom().nextInt(Integer.MAX_VALUE);
        ChatOverlay.getChat().printUnloggedChatMessage(
                new TextComponentString(TextFormatting.GRAY + "Calculating Server Information..."
                ), messageId);

        Utils.runAsync(() -> {
            if (args.length == 0) {
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString("Usage: /s info <serverID>"), messageId);
                return;
            }
            if (args.length > 1) {
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString("Too many arguments\nUsage: /s info <serverID>"), messageId);
                return;
            }
            if (args[0].equalsIgnoreCase("help")) {
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString("Usage: /s info <serverID>"), messageId);
                return;
            }
            // args.length == 1 and no help
            try {
                Map<String, List<String>> onlinePlayers = WebManager.getOnlinePlayers();
                for (String serverName : onlinePlayers.keySet()) {
                    if (args[0].equalsIgnoreCase(serverName)) {
                        TextComponentString text = new TextComponentString(String.format("%s: ", serverName));
                        TextComponentString playerText = new TextComponentString("");

                        List<String> players = onlinePlayers.get(serverName);

                        if (players.size() > 0) {
                            for (String player : players.subList(0, players.size() - 1)) {
                                playerText.appendText(String.format("%s, ", player));
                            }
                            playerText.appendText(players.get(players.size() - 1));
                            playerText.getStyle().setColor(TextFormatting.GRAY);
                            text.appendSibling(playerText);
                        }

                        text.appendText("\nTotal online players: ");
                        TextComponentString playerCountText = new TextComponentString(Integer.toString(players.size()));
                        playerCountText.getStyle().setColor(TextFormatting.GRAY);
                        text.appendSibling(playerCountText);

                        ChatOverlay.getChat().printUnloggedChatMessage(text, messageId);
                        return;
                    }
                }
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString(String.format("Unknown server ID: %s", args[0])), messageId);

            } catch (Exception e) {
                ChatOverlay.getChat().printUnloggedChatMessage(
                        new TextComponentString(
                                TextFormatting.RED +
                                        "An error occurred while trying to get the servers!"
                        ).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TextComponentString(TextFormatting.RED + e.getMessage())
                        ))),
                        messageId
                );

                e.printStackTrace();
            }
        });
    }

    private static TextComponentString getFilteredServerList(Map<String, List<String>> onlinePlayers,
                                                       String filter,
                                                       List<String> options) {
        TextComponentString text = new TextComponentString("");
        TextComponentString serverListText = new TextComponentString("");

        int serverCount = 0;
        for (String serverName : options.contains("sort") ? new TreeSet<>(onlinePlayers.keySet()) : onlinePlayers.keySet()) {
            if (serverName.toLowerCase().contains(filter.toLowerCase())) {
                TextComponentString serverText = new TextComponentString(String.format("%s ", serverName));
                if (onlinePlayers.get(serverName).size() >= 48) {serverText.getStyle().setColor(TextFormatting.RED);}
                else {serverText.getStyle().setColor(TextFormatting.GREEN);}
                serverListText.appendSibling(serverText);
                serverCount++;
            }
        }

        if (filter.equals("")) {
            text.appendText("Available servers" +
                    (options.contains("count") ? String.format(" (%d)", onlinePlayers.size()): "") + ":\n");
        } else if (options.contains("count")) {
            text.appendText(String.format("%s (%d):\n", filter, serverCount));
        } else {
            text.appendText(String.format("%s:\n", filter));
        }

        if (serverCount == 0) {
            serverListText.appendText("none");
            serverListText.getStyle().setColor(TextFormatting.DARK_GRAY);
            text.getStyle().setColor(TextFormatting.GRAY);
        }

        text.appendSibling(serverListText);

        return text;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "list", "info");
        }
        switch (args[0].toLowerCase()) {
            case "list":
            case "ls":
            case "l":
                List<String> arguments = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
                if (arguments.size() > 1 && arguments.get(0).equals("help"))
                    return Collections.emptyList();

                boolean containsServerType = arguments.stream().anyMatch((arg) -> {
                    List<String> incompatibilities = new ArrayList<>(serverTypes);
                    incompatibilities.add("group");
                    return incompatibilities.contains(arg);
                });

                boolean containsGroup = arguments.stream().anyMatch((arg) -> {
                    List<String> incompatibilities = new ArrayList<>();
                    incompatibilities.add("sort");
                    incompatibilities.add("group");
                    return incompatibilities.contains(arg);
                });

                List<String> possibleArguments = new ArrayList<>();

                if (!containsServerType) {
                    possibleArguments.addAll(serverTypes);
                    if (!containsGroup) {
                        possibleArguments.add("group");
                    }
                }
                if (!containsGroup) {
                    possibleArguments.add("sort");
                }
                possibleArguments.add("count");
                if (arguments.size() == 1) {
                    possibleArguments.add("help");
                }
                possibleArguments.removeAll(arguments);

                return getListOfStringsMatchingLastWord(args, possibleArguments);
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
