/*
 *  * Copyright © Wynntils - 2019.
 */
package com.wynntils.modules.core.commands;

import com.wynntils.ModCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
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
        return "compass [<x> <z> | <direction>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("§4Missing arguments: /compass [<x> <z> | <direction>]"));
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
                    sender.sendMessage(new TextComponentString("§4That wasn't supposed to happen!"));
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
                        sender.sendMessage(new TextComponentString("§4That wasn't supposed to happen!"));
                    }
                    break;
            }
            ModCore.mc().world.setSpawnPoint(new BlockPos(Integer.valueOf(newPos[0]), 0, Integer.valueOf(newPos[1])));
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
            sender.sendMessage(new TextComponentString("§aCompass is now pointing towards §2" + dir + "§a." ));
        } else if (args.length == 2 && args[0].matches("[0-9-]+") && args[1].matches("[0-9-]+")) {
            ModCore.mc().world.setSpawnPoint(new BlockPos(Integer.valueOf(args[0]), 0, Integer.valueOf(args[1])));
            sender.sendMessage(new TextComponentString("§aCompass is now pointing towards (§2" + args[0] + "§a, §2" + args[1] + "§a)." ));
        } else {
            sender.sendMessage(new TextComponentString("§4Invalid arguments: /compass [<x> <z> | <direction>]"));
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
                    "west");
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
