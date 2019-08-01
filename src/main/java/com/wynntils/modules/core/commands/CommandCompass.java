/*
 *  * Copyright © Wynntils - 2019.
 */
package com.wynntils.modules.core.commands;

import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.managers.CompassManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCompass extends CommandBase implements IClientCommand {

    private String[] directions = {
        "north",
        "northeast",
        "northwest",
        "south",
        "southeast",
        "southwest",
        "east",
        "west",
        "n",
        "ne",
        "nw",
        "s",
        "se",
        "sw",
        "e",
        "w"
    };

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "compass";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "compass [<x> <z> | <direction> | clear]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if (args.length == 0) {
            throw new WrongUsageException("/compass [<x> <z> | <direction> | clear]");
        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            if (CompassManager.getCompassLocation() != null) {
                CompassManager.reset();
                TextComponentString text = new TextComponentString("The beacon and icon of your desired coordinates have been cleared.");
                text.getStyle().setColor(TextFormatting.GREEN);
                sender.sendMessage(text);
            } else {
                throw new CommandException("There is nothing to be cleared as you have not set any coordinates to be displayed as a beacon and icon.");
            }
        } else if (args.length == 1 && Arrays.stream(directions).anyMatch(args[0]::equalsIgnoreCase)) {
            int[] newPos = { 0, 0 };
            //check for north/south
            switch (args[0].toLowerCase()) {
                case "north":
                case "northeast":
                case "northwest":
                case "n":
                case "ne":
                case "nw":
                    newPos[1] = -9999999;
                    break;
                case "south":
                case "southeast":
                case "southwest":
                case "s":
                case "se":
                case "sw":
                    newPos[1] = 9999999;
                    break;
                default:
                    break;
            }
            //check for east/west
            switch (args[0].toLowerCase()) {
                case "east":
                case "northeast":
                case "southeast":
                case "e":
                case "ne":
                case "se":
                    newPos[0] = 9999999;
                    break;
                case "west":
                case "northwest":
                case "southwest":
                case "w":
                case "nw":
                case "sw":
                    newPos[0] = -9999999;
                    break;
                default:
                    if (newPos[1] == 0) {
                        TextComponentString text = new TextComponentString("That wasn't supposed to happen!");
                        text.getStyle().setColor(TextFormatting.DARK_RED);
                        sender.sendMessage(text);
                    }
                    break;
            }

            CompassManager.setCompassLocation(new Location(newPos[0], 0, newPos[1]));

            String dir = args[0];
            if (dir.length() <= 2) {
                //dir = dir.toUpperCase();
                switch (dir.toLowerCase()) {
                    case "n":
                        dir = "North";
                        break;
                    case "ne":
                        dir = "Northeast";
                        break;
                    case "nw":
                        dir = "Northwest";
                        break;
                    case "s":
                        dir = "South";
                        break;
                    case "se":
                        dir = "Southeast";
                        break;
                    case "sw":
                        dir = "Southwest";
                        break;
                    case "e":
                        dir = "East";
                        break;
                    case "w":
                        dir = "West";
                        break;
                    default:
                        dir = "Somewhere";
                        break;
                }
            }
            dir = dir.substring(0,1).toUpperCase() + dir.substring(1);
            TextComponentString text = new TextComponentString("");
            text.getStyle().setColor(TextFormatting.GREEN);
            text.appendText("Compass is now pointing towards ");
            
            TextComponentString directionText = new TextComponentString(dir);
            directionText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(directionText);
            
            text.appendText(".");
            sender.sendMessage(text);
        } else if (args.length == 2 && args[0].matches("(-?(?!0)\\d+)|0") && args[1].matches("(-?(?!0)\\d+)|0")) {
            CompassManager.setCompassLocation(new Location(Integer.valueOf(args[0]), 0, Integer.valueOf(args[1])));

            TextComponentString text = new TextComponentString("");
            text.getStyle().setColor(TextFormatting.GREEN);
            text.appendText("Compass is now pointing towards (");
            
            TextComponentString xCoordinateText = new TextComponentString(args[0]);
            xCoordinateText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(xCoordinateText);
            
            text.appendText(", ");
            
            TextComponentString zCoordinateText = new TextComponentString(args[1]);
            zCoordinateText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(zCoordinateText);
            
            text.appendText(").");
            sender.sendMessage(text);
        } else {
            throw new CommandException("Invalid arguments: /compass [<x> <z> | <direction> | clear]");
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args,
                    "north",
                    "northeast",
                    "northwest",
                    "south",
                    "southeast",
                    "southwest",
                    "east",
                    "west",
                    "clear");
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
