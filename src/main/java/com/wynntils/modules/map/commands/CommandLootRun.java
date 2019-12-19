/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.modules.map.managers.LootRunManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

public class CommandLootRun extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "lootrun";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/lootrun <load/save/record>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(RED + "Use: /lootrun <load/save/record/clear>"));
            return;
        }

        if (args[0].equalsIgnoreCase("load")) {
            if(args.length < 2) {
                sender.sendMessage(new TextComponentString(RED + "Use: /lootrun load [name]"));
                return;
            }
            String name = args[1];
            boolean result = LootRunManager.loadFromFile(name);

            String message;
            if(result) message = GREEN + "Loaded LootRun " + name + " successfully!";
            else message = RED + "The specified LootRun doesn't exists!";

            sender.sendMessage(new TextComponentString(message));
            return;
        }
        if (args[0].equalsIgnoreCase("save")) {
            if(args.length < 2) {
                sender.sendMessage(new TextComponentString(RED + "Use: /lootrun save [name]"));
                return;
            }
            String name = args[1];

            boolean result = LootRunManager.saveToFile(name);

            String message;
            if(result) message = GREEN + "Saved LootRun " + name + " successfully!";
            else message = RED + "An error occured while trying to save your LootRun route!";

            sender.sendMessage(new TextComponentString(message));
            return;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            LootRunManager.clear();

            sender.sendMessage(new TextComponentString(GREEN + "Cleaned current LootRun points!"));
            return;
        }
        if (args[0].equalsIgnoreCase("record")) {
            boolean result = LootRunManager.isRecording();

            String message;
            if(result) {
                message = GREEN + "Stoped to record your movements!";
                LootRunManager.stopRecording();
            } else {
                message = GREEN + "Started to record your current movements! " + RED + "Red means recording.";
                LootRunManager.startRecording();
            }

            sender.sendMessage(new TextComponentString(message));
            return;
        }

        sender.sendMessage(new TextComponentString(RED + "Use: /lootrun <load/save/record/clear>"));
    }

}