/**
 * Copyright © Wynntils - 2019
 */
package cf.wynntils.modules.core.commands;

import cf.wynntils.ModCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import java.util.Arrays;

public class CommandCompass extends CommandBase implements IClientCommand {

    private String[] directions = {"north", "south", "east", "west", "n", "s", "e", "w"};

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
            switch (args[0].toLowerCase()) {
                case "north":
                case "n":
                    ModCore.mc().world.setSpawnPoint(new BlockPos(0, 0, -9999999));
                    break;
                case "south":
                case "s":
                    ModCore.mc().world.setSpawnPoint(new BlockPos(0, 0, 9999999));
                    break;
                case "east":
                case "e":
                    ModCore.mc().world.setSpawnPoint(new BlockPos(9999999, 0, 0));
                    break;
                case "west":
                case "w":
                    ModCore.mc().world.setSpawnPoint(new BlockPos(-9999999, 0, 0));
                    break;
                default:
                    sender.sendMessage(new TextComponentString("§4That wasn't supposed to happen!"));
            }
        } else if (args.length == 2 && args[0].matches("[0-9-]+") && args[1].matches("[0-9-]+")) {
            ModCore.mc().world.setSpawnPoint(new BlockPos(Integer.valueOf(args[0]), 0, Integer.valueOf(args[1])));
        } else {
            sender.sendMessage(new TextComponentString("§4Invalid arguments: /compass [<x> <z> | <direction>]"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
