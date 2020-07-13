package com.wynntils.modules.core.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class CommandTest extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public int getRequiredPermissionLevel() {return 0;}

    @Override
    public String getName() {return "test";}

    @Override
    public String getUsage(ICommandSender sender) {return "/test test is just a test";}

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        TextComponentString text = new TextComponentString("ahoj");
        sender.sendMessage(text);

    }
}
