/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.webapi.WebManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;

public class CommandForceUpdate extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "forceupdate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Force Wynntils to update";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        WebManager.getUpdate().forceUpdate();
        TextComponentString text = new TextComponentString("Forcing Wynntils to update...");
        text.getStyle().setColor(TextFormatting.AQUA);
        sender.sendMessage(text);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
