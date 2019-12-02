/*
 *  * Copyright © Wynntils - 2019.
 */
package com.wynntils.modules.core.commands;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.managers.CompassManager;
import net.minecraft.client.Minecraft;
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
import java.util.Locale;

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
        if (args.length == 0) throw new WrongUsageException("/compass [<x> <z> | <direction> | clear]");

        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            if (CompassManager.getCompassLocation() != null) {
                CompassManager.reset();

                TextComponentString text = new TextComponentString("The beacon and icon of your desired coordinates have been cleared.");
                text.getStyle().setColor(TextFormatting.GREEN);
                sender.sendMessage(text);
                return;
            }

            throw new CommandException("There is nothing to be cleared as you have not set any coordinates to be displayed as a beacon and icon.");
        }

        if (args.length == 1 && Arrays.stream(directions).anyMatch(args[0]::equalsIgnoreCase)) {
            int[] newPos = {0, 0};
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
                switch (dir.toLowerCase(Locale.ROOT)) {
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

            dir = dir.substring(0, 1).toUpperCase() + dir.substring(1);
            TextComponentString text = new TextComponentString("");
            text.getStyle().setColor(TextFormatting.GREEN);
            text.appendText("Compass is now pointing towards ");

            TextComponentString directionText = new TextComponentString(dir);
            directionText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(directionText);

            text.appendText(".");
            sender.sendMessage(text);
            return;
        }

        if (args.length == 2 && args[0].matches("~|~?(?:-?[1-9][0-9]*|0)") && args[1].matches("~|~?(?:-?[1-9][0-9]*|0)")) {
            int x = 0; int z = 0;

            boolean invalid = false;
            if (args[0].charAt(0) == '~') {
                x = (int) Minecraft.getMinecraft().player.posX;

                if (args[0].length() != 1) {
                    String offset = args[0].substring(1);
                    if (!Utils.isValidInteger(offset)) {
                        invalid = true;
                    } else {
                        x += Integer.parseInt(offset);
                    }
                }
            } else if (!Utils.isValidInteger(args[0])) {
                invalid = true;
            } else {
                x = Integer.parseInt(args[0]);
            }

            if (!invalid) {
                if (args[1].charAt(0) == '~') {
                    z = ((int) Minecraft.getMinecraft().player.posZ);
                    if (args[1].length() != 1) {
                        String offset = args[1].substring(1);

                        if (!Utils.isValidInteger(offset)) {
                            invalid = true;
                        } else {
                            z += Integer.parseInt(offset);
                        }
                    }
                } else if (!Utils.isValidInteger(args[1])) {
                    invalid = true;
                } else {
                    z = Integer.parseInt(args[1]);
                }
            }

            if (invalid) throw new CommandException("The coordinate passed was too big");

            CompassManager.setCompassLocation(new Location(x, 0, z));

            TextComponentString text = new TextComponentString("");
            text.getStyle().setColor(TextFormatting.GREEN);
            text.appendText("Compass is now pointing towards (");

            TextComponentString xCoordinateText = new TextComponentString(Integer.toString(x));
            xCoordinateText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(xCoordinateText);

            text.appendText(", ");

            TextComponentString zCoordinateText = new TextComponentString(Integer.toString(z));
            zCoordinateText.getStyle().setColor(TextFormatting.DARK_GREEN);
            text.appendSibling(zCoordinateText);

            text.appendText(").");
            sender.sendMessage(text);

            return;
        }

        throw new CommandException("Invalid arguments: /compass [<x> <z> | <direction> | clear]");
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
