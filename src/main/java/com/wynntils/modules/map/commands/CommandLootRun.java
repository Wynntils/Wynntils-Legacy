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

import java.util.ArrayList;
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
            throw new WrongUsageException("/lootrun <load/save/hide/record/stop/list/clear/help>");
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

                if(!LootRunManager.isRecording()) {
                    sender.sendMessage(new TextComponentString(RED + "You're not currently recording a path!"));
                    return;
                }
                if(LootRunManager.getRecordingPath().getChests().isEmpty()) {
                    sender.sendMessage(new TextComponentString(RED + "You have to open at least one chest to save a loot run path!"));
                    return;
                }

                boolean result = LootRunManager.saveToFile(name);

                String message;
                if (result) {
                    message = GREEN + "Saved loot run " + name + " successfully!";
                    LootRunManager.stopRecording();
                } else {
                    message = RED + "An error occurred while trying to save your loot run path!";
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "hide":
                LootRunManager.hide();

                sender.sendMessage(new TextComponentString(GREEN + "Your active loot run has been hidden!"));
            case "record": {
                String message;
                if (LootRunManager.isRecording()) {
                    message = RED + "Already recording... Please clear using /lootrun save or /lootrun stop.";
                } else {
                    message = GREEN + "Started to record your current movements! " + RED + "Red means recording.";
                    LootRunManager.startRecording();
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "stop": {
                String message;
                if (LootRunManager.isRecording()) {
                    message = GREEN + "Stopped to record your movements!";
                    LootRunManager.stopRecording();
                } else {
                    message = RED + "Not recording a loot run!";
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
                        "Loot runs are saved in\n" + LootRunManager.STORAGE_FOLDER.getAbsolutePath() + "\nClick to open!"
                    )))
                    .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, LootRunManager.STORAGE_FOLDER.getAbsolutePath()));
                sender.sendMessage(messageText);
                return;
            }
            case "clear":
                LootRunManager.clear();

                sender.sendMessage(new TextComponentString(GREEN + "Cleared current loot run points!"));
                return;
            case "help": {
                // TODO
            }
            default:
                throw new WrongUsageException("/lootrun <load/save/hide/record/stop/list/clear/help>");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "load":
            case "save":
                if (args.length > 2) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, LootRunManager.getStoredLootruns());
            case "list":
            case "help":
            case "record":
            case "stop":
            case "clear":
            case "hide":
                if (args.length > 1) return Collections.emptyList();
                // getTabCompletions() result *must* be settable or crash
                return new ArrayList<>(Collections.singletonList(args[0].toLowerCase(Locale.ROOT)));
            default:
                if (args.length > 1) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, "load", "save", "record", "list", "stop", "clear", "hide", "help");
        }
    }

}
