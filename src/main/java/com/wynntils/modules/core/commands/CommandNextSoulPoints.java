package com.wynntils.modules.core.commands;

import com.wynntils.modules.utilities.managers.ServerListManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandNextSoulPoints extends CommandBase implements IClientCommand {


    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "nextsoulpoints";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Returns a list of servers/time pairs that regenerate soul points within the next 10 minutes";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Map<String, Integer> nextServers = new HashMap<>();

        // Get all servers that will generate a soul point within the next 10 minutes
        for (String availServer : ServerListManager.availableServers.keySet()) {
            int uptimeMinutes = ServerListManager.getUptimeTotalMinutes(availServer);
            if ((uptimeMinutes % 20) >= 10) { // >= to 10min because we're looking for time UNTIL 20min, not after
                nextServers.put(availServer, 20 - (uptimeMinutes % 20));
            }
        }

        // Sort servers by time until soul point
        Map<String, Integer> sortedServers = nextServers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        TextComponentString toSend = new TextComponentString("§bNext soul points:");

        for (String wynnServer : sortedServers.keySet()) {
            int uptimeMinutes = sortedServers.get(wynnServer);
            TextFormatting minuteColor;
            if (uptimeMinutes <= 2) {
                minuteColor = TextFormatting.GREEN;
            } else if (uptimeMinutes <= 5) {
                minuteColor = TextFormatting.YELLOW;
            } else {
                minuteColor = TextFormatting.RED;
            }

            toSend.appendText("\n§b- §6" + wynnServer + " §b in " + minuteColor + uptimeMinutes + " minutes");
        }

        sender.sendMessage(toSend);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
