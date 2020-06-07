/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */
package com.wynntils.modules.core.commands;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.utils.objects.Location;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandCompass extends CommandBase implements IClientCommand {

    private static final String USAGE = "compass [<x> [<y>] <z> | <direction> | clear | share [location] [party|user]";

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
        return USAGE;
    }

    /**
     * Parse a single minecraft coordinate number. A ~ prefix means a relative position
     * @throws NumberFormatException
     */
    private int getSingleCoordinate(String str, int relativePosition) throws NumberFormatException {
        if (str.startsWith("~")) {
            str = str.substring(1);
            if (str.isEmpty()) {
                return relativePosition;
            }
        } else {
            relativePosition = 0;
        }
        return relativePosition + Integer.parseInt(str);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if (args.length == 0) throw new WrongUsageException("/" + USAGE);

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
            // check for north/south
            switch (args[0].toLowerCase(Locale.ROOT)) {
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

            // check for east/west
            switch (args[0].toLowerCase(Locale.ROOT)) {
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

        if (args.length >= 1 && args[0].equalsIgnoreCase("share")) {
            String recipientUser = null;
            String type;
            int recipientIndex = 1;
            double x;
            double z;

            if (args.length >= 2 && args[1].equalsIgnoreCase("location")) {
                // Use current location instead of compass
                x = Minecraft.getMinecraft().player.posX;
                z = Minecraft.getMinecraft().player.posZ;
                type = "location";
                recipientIndex = 2;
            } else {
                Location location = CompassManager.getCompassLocation();
                if (location == null) {
                    throw new CommandException("No compass location set (did you mean /compass share location?)");
                }
                x = location.getX();
                z = location.getZ();
                type = "compass";
            }
            if (args.length >= recipientIndex+1 && !args[recipientIndex].equalsIgnoreCase("party")) {
                recipientUser = args[recipientIndex];
            }

            shareCoordinates(recipientUser, type, (int) x, (int) z);

            return;
        }

        if (args.length >= 2) {
            String argument = String.join(" ", args);

            // Accept fuzzy coordinates like "[x] [<punctuation>] <coord> [<punctuation>] [z] <coord>"
            // where <punctuation> is any of ":.,", <coord> is ~, ~<int> or <int> and whitespace is liberally accepted
            Pattern patternXY = Pattern.compile("^[:., ]*[xX]?[:., ]*(~|~?-?[0-9]+)[:., ]+[zZ]?[:., ]*(~|~?-?[0-9]+)[:., ]*$");
            Matcher matcherXY = patternXY.matcher(argument);
            // And an alternative with a [y] <y> in between x and z, which is ignored but allowed
            Pattern patternXYZ = Pattern.compile("^[:., ]*[xX]?[:., ]*(~|~?-?[0-9]+)[:., ]+[yY]?[:., ]*(?:~|~?-?[0-9]+)[:., ]+[zZ]?[:., ]*(~|~?-?[0-9]+)[:., ]*$");
            Matcher matcherXYZ = patternXYZ.matcher(argument);

            String xStr;
            String zStr;

            if (matcherXY.find()) {
                xStr = matcherXY.group(1);
                zStr = matcherXY.group(2);
            }  else if (matcherXYZ.find()) {
                xStr = matcherXYZ.group(1);
                zStr = matcherXYZ.group(2);
            }  else {
                throw new CommandException("Invalid arguments: /" + USAGE);
            }

            try {
                int x = getSingleCoordinate(xStr, (int) Minecraft.getMinecraft().player.posX);
                int z = getSingleCoordinate(zStr, (int) Minecraft.getMinecraft().player.posZ);

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
            } catch (NumberFormatException e) {
                throw new CommandException("Invalid coordinates passed to /compass");
            }
        }

        throw new CommandException("Invalid arguments: /" + USAGE);
    }

    public static void shareCoordinates(String recipientUser, String type, int x, int z) {
        String location = "[" + x + ", " + z + "]";
        if (recipientUser == null) {
            Minecraft.getMinecraft().player.sendChatMessage("/p " + " My " + type + " is at " + location);
        } else {
            Minecraft.getMinecraft().player.sendChatMessage("/msg " + recipientUser + " My " + type + " is at " + location);
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
                    "clear",
                    "share");
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("share")) {
            // Allow easy completion of friends' names
            HashSet<String> completions = new HashSet<>(PlayerInfo.getPlayerInfo().getFriendList());
            completions.add("party");
            if (args.length == 3 && args[1].equalsIgnoreCase("location")) {
                return getListOfStringsMatchingLastWord(args, completions);
            }
            completions.add("location");
            if (args.length == 2) {
                return getListOfStringsMatchingLastWord(args, completions);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
