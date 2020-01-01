/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.modules.map.managers.LootRunManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.minecraft.util.text.TextFormatting.*;

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
        return "lootrun <load/save/hide/record/stop/list/clear/help>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("/lootrun <load/save/delete/record/list/clear/help>");
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "load": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun load [name]");
                }
                String name = args[1];
                boolean result = LootRunManager.loadFromFile(name);

                String message;
                if (result) message = GREEN + "Loaded loot run " + name + " successfully!";
                else message = RED + "The specified loot run doesn't exist!";

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "save": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun save [name]");
                }
                String name = args[1];

                if (LootRunManager.isRecording()) {
                    sender.sendMessage(new TextComponentString(RED + "You're currently recording a lootrun, to save it first stop recording with /lootrun record!"));
                    return;
                }
                if (LootRunManager.getActivePath().getChests().isEmpty()) {
                    sender.sendMessage(new TextComponentString(RED + "You have to open at least one chest to save a loot run path!"));
                    return;
                }

                boolean result = LootRunManager.saveToFile(name);

                String message;
                if (result) {
                    message = GREEN + "Saved loot run " + name + " successfully!";
                } else {
                    message = RED + "An error occurred while trying to save your loot run path!";
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "delete": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun delete [name]");
                }
                String name = args[1];

                boolean result = LootRunManager.delete(name);

                String message;
                if (result) {
                    message = GREEN + "Deleted run " + name + " successfully!";
                } else {
                    message = RED + "The provided lootrun doesn't exists!";
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "record": {
                String message;
                if (LootRunManager.isRecording()) {
                    message = GREEN + "Stopped to record your movements!\n" + AQUA + "Save your lootrun with /lootrun save <name> or delete with /lootrun clear";
                    LootRunManager.stopRecording();
                } else {
                    message = GREEN + "Started to record your current movements!\n" + RED + "Use the command again to stop.";
                    LootRunManager.startRecording();
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "list": {
                StringBuilder message = new StringBuilder(YELLOW.toString()).append("Stored loot runs:");
                List<String> lootruns = LootRunManager.getStoredLootruns();
                if (lootruns.isEmpty()) {
                    message.append('\n').append(GRAY).append("You currently have no saved loot runs!");
                } else {
                    for (String lootrun : lootruns) {
                        message.append('\n').append(WHITE).append(lootrun);
                    }
                }
                ITextComponent messageText = new TextComponentString(message.toString());
                if (!LootRunManager.STORAGE_FOLDER.exists()) {
                    LootRunManager.STORAGE_FOLDER.mkdirs();
                }
                messageText.getStyle()
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(
                        "Loot runs are saved in\n" + LootRunManager.STORAGE_FOLDER.getAbsolutePath() + "\nClick here to open!"
                    )))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, LootRunManager.STORAGE_FOLDER.getAbsolutePath()));
                sender.sendMessage(messageText);
                return;
            }
            case "clear":
                if (!LootRunManager.isRecording() && LootRunManager.getActivePath() == null) {
                    sender.sendMessage(new TextComponentString(RED + "You have no loot runs to clear!"));
                    return;
                }
                LootRunManager.clear();

                sender.sendMessage(new TextComponentString(GREEN + "Cleared current loot runs!"));
                return;
            case "help": {
                sender.sendMessage(new TextComponentString(
                    GOLD + "Loot run recording help\n" +
                    DARK_GRAY + "/lootrun " + RED + "load <name> " + GRAY + "Loads a saved loot run\n" +
                    DARK_GRAY + "/lootrun " + RED + "save <name> " + GRAY + "Save the currently recording loot run as the given name\n" +
                    DARK_GRAY + "/lootrun " + RED + "delete <name> " + GRAY + "Delete a previously saved lootrun\n" +
                    DARK_GRAY + "/lootrun " + RED + "record " + GRAY + "Start/Stop recording a new loot run\n" +
                    DARK_GRAY + "/lootrun " + RED + "list " + GRAY + "List all saved lootruns\n" +
                    DARK_GRAY + "/lootrun " + RED + "clear " + GRAY + "Clears/Hide the currently loaded loot run and the loot run being recorded\n" +
                    DARK_GRAY + "/lootrun " + RED + "help " + GRAY + "View this help message"
                ));
                return;
            }
            default:
                throw new WrongUsageException("/lootrun <load/save/delete/record/list/help/clear>");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "load":
            case "save":
            case "delete":
                if (args.length > 2) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, LootRunManager.getStoredLootruns());
            case "list":
            case "help":
            case "record":
            default:
                if (args.length > 1) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, "load", "save", "delete", "record", "list", "clear", "help");
        }
    }

}
