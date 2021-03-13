/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.ModCore;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunNote;
import com.wynntils.modules.map.managers.LootRunManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

import java.io.IOException;
import java.net.URI;
import java.util.*;

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
    public List<String> getAliases() {
        return Arrays.asList("loot", "lr");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "lootrun <load/save/delete/rename/record/undo/note/list/folder/clear/help>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("/" + getUsage(sender));
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "l":
            case "load": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun load [name]");
                }
                String name = args[1];
                boolean result = LootRunManager.loadFromFile(name);

                String message;
                if (result) message = GREEN + "Loaded loot run " + name + " successfully! " + GRAY + "(" + LootRunManager.getActivePath().getChests().size() + " chests)";
                else {
                    throw new CommandException("The specified loot run doesn't exist!");
                }

                sender.sendMessage(new TextComponentString(message));

                if (!LootRunManager.getActivePath().getPoints().isEmpty()) {
                    Location start = LootRunManager.getActivePath().getPoints().get(0);
                    ITextComponent startingPointMsg = new TextComponentString("Loot run starts at [" +
                            (int) start.getX() + ", " + (int) start.getZ() + "]");
                    startingPointMsg.getStyle().setColor(GRAY);
                    sender.sendMessage(startingPointMsg);
                }

                return;
            }
            case "s":
            case "save": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun save [name]");
                }
                String name = args[1];

                if (LootRunManager.isRecording()) {
                    sender.sendMessage(new TextComponentString(RED + "You're currently recording a lootrun, to save it first stop recording with /lootrun record!"));
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
            case "d":
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
            case "rename": {
                if (args.length < 3) {
                    throw new WrongUsageException("/lootrun rename [oldname] [newname]");
                }
                String oldName = args[1];
                String newName = args[2];

                String message;
                if (LootRunManager.rename(oldName, newName)) {
                    message = GREEN + "Successfully renamed " + oldName + " to " + newName + "!";
                } else if (LootRunManager.hasLootrun(oldName)) {
                    message = RED + "An error occurred whilst renaming!";
                } else {
                    message = RED + "Could not rename " + oldName + " as it doesn't exist!";
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "r":
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
            case "u":
            case "undo": {
                if (!LootRunManager.isRecording()) {
                    sender.sendMessage(new TextComponentString(RED + "You are not currently recording a loot run!"));
                    return;
                }

                Entity lowest = ModCore.mc().player.getLowestRidingEntity();
                String message;
                if (LootRunManager.undoMovement(lowest.posX, lowest.posY, lowest.posZ)) {
                    message = GREEN + "Undid your most recent movements!";
                } else {
                    message = RED + "Failed to undo your movements!\n" + RED + "Make sure you are standing on the part of the path you want to rewind to";
                }

                sender.sendMessage(new TextComponentString(message));
                return;
            }
            case "n":
            case "note": {
                if (args.length < 2) {
                    throw new WrongUsageException("/lootrun note list or /lootrun note <note>");
                }

                if (args.length == 2 && args[1].equalsIgnoreCase("list")) {
                    ITextComponent message = new TextComponentString(YELLOW + "Loot run notes:");
                    Set<LootRunNote> notes = null;
                    if (LootRunManager.isRecording()) {
                        notes = LootRunManager.getRecordingPath().getNotes();
                    } else if (LootRunManager.getActivePath() != null) {
                        notes = LootRunManager.getActivePath().getNotes();
                    }

                    if (notes == null) {
                        message.appendText("\n");
                        message.appendText(RED + "You have no active or recording loot runs!");
                    } else if (notes.isEmpty()) {
                        message.appendText("\n");
                        message.appendText(GRAY + "No notes to display!");
                    } else {
                        for (LootRunNote n : notes) {
                            ITextComponent deleteButton = new TextComponentString(RED + "[X] ");
                            deleteButton.getStyle()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(
                                    "Click here to delete this note!"
                                )))
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lootrun deletenote " + n.getShortLocationString()));

                            ITextComponent noteMessage = new TextComponentString(n.getLocationString() + ": " + AQUA + n.getNote());
                            noteMessage.getStyle().setColor(GRAY);

                            message.appendText("\n");
                            message.appendSibling(deleteButton);
                            message.appendSibling(noteMessage);
                        }
                    }

                    sender.sendMessage(message);
                    return;
                }

                String text = String.join(" ", args);
                text = text.substring(text.indexOf(" ")).trim();
                EntityPlayerSP player = ModCore.mc().player;
                LootRunNote note = new LootRunNote(new Location(player.posX, player.posY, player.posZ), text);

                ITextComponent message;
                if (LootRunManager.addNote(note)) {
                    message = new TextComponentString("Saved note at " + note.getLocationString() + "!");
                    message.getStyle().setColor(GREEN);
                } else {
                    message = new TextComponentString(RED + "You have no active or recording loot runs!");
                }

                sender.sendMessage(message);
                return;
            }
            case "deletenote": {
                if (args.length < 2) return;

                String location = args[1];
                ITextComponent message;
                if (LootRunManager.removeNote(location)) {
                    message = new TextComponentString("Removed note at (" + location.replace(",", ", ") + ")!");
                    message.getStyle().setColor(GREEN);
                } else {
                    message = new TextComponentString(RED + "You have no active or recording loot runs!");
                }

                sender.sendMessage(message);
                return;
            }
            case "addchest":
            case "removechest": {
                String command = args[0].toLowerCase(Locale.ROOT);
                BlockPos pos;
                if (args.length < 4) {
                    pos = new BlockPos((int) ModCore.mc().player.posX, (int) ModCore.mc().player.posY, (int) ModCore.mc().player.posZ - 1);
                } else {
                    int x = 0, y = 0, z = 0;
                    try {
                        x = Integer.parseInt(args[1]);
                        y = Integer.parseInt(args[2]);
                        z = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(new TextComponentString(RED + "Invalid coordinates!"));
                        return;
                    }
                    pos = new BlockPos(x, y, z - 1); // offset z by one
                }

                ITextComponent message;
                if (command.equals("addchest")) {
                    if (LootRunManager.addChest(pos)) {
                        message = new TextComponentString("Added chest at (" + pos.getX() + ", " + pos.getY() + ", " + (pos.getZ() + 1) + ")!");
                        message.getStyle().setColor(GREEN);
                    } else {
                        message = new TextComponentString(RED + "You have no active or recording loot runs!");
                    }
                } else {
                    if (LootRunManager.removeChest(pos)) {
                        message = new TextComponentString("Removed chest at (" + pos.getX() + ", " + pos.getY() + ", " + (pos.getZ() + 1) + ")!");
                        message.getStyle().setColor(GREEN);
                    } else {
                        message = new TextComponentString(RED + "You have no active or recording loot runs!");
                    }
                }

                sender.sendMessage(message);
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
                try {
                    messageText.getStyle()
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(
                            "Loot runs are saved in\n" + LootRunManager.STORAGE_FOLDER.getCanonicalPath() + "\nClick here to open!"
                        )))
                        .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, LootRunManager.STORAGE_FOLDER.getCanonicalPath()));
                } catch (IOException e) {
                    // Shouldn't throw
                }
                sender.sendMessage(messageText);
                return;
            }
            case "folder": {
                URI uri = LootRunManager.STORAGE_FOLDER.toURI();
                Utils.openUrl(uri.toString());
                return;
            }
            case "c":
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
                    DARK_GRAY + "/lootrun " + RED + "rename <oldname> <newname> " + GRAY + "Rename a lootrun (Will overwrite any lootrun called <newname>)\n" +
                    DARK_GRAY + "/lootrun " + RED + "record " + GRAY + "Start/Stop recording a new loot run\n" +
                    DARK_GRAY + "/lootrun " + RED + "undo " + GRAY + "Undo the recording loot run to your current position\n" +
                    DARK_GRAY + "/lootrun " + RED + "note " + GRAY + "Add or list notes to the active or recording loot run\n" +
                    DARK_GRAY + "/lootrun " + RED + "addchest/removechest <x> <y> <z> " + GRAY + "Manually mark a chest in the active or recording loot run\n" +
                    DARK_GRAY + "/lootrun " + RED + "list " + GRAY + "List all saved lootruns\n" +
                    DARK_GRAY + "/lootrun " + RED + "folder " + GRAY + "Open the folder where lootruns are stored, for import\n" +
                    DARK_GRAY + "/lootrun " + RED + "clear " + GRAY + "Clears/Hide the currently loaded loot run and the loot run being recorded\n" +
                    DARK_GRAY + "/lootrun " + RED + "help " + GRAY + "View this help message"
                ));
                return;
            }
            default:
                throw new WrongUsageException("/" + getUsage(sender));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "load":
            case "save":
            case "delete":
            case "rename":
                if (args.length > 2) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, LootRunManager.getStoredLootruns());
            case "list":
            case "folder":
            case "help":
            case "record":
            case "undo":
            default:
                if (args.length > 1) return Collections.emptyList();
                return getListOfStringsMatchingLastWord(args, "load", "save", "delete", "rename", "record", "list", "folder", "clear", "help", "undo");
        }
    }

}
