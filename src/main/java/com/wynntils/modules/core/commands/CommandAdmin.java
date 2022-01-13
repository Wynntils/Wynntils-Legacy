/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.McIf;
import com.wynntils.modules.core.enums.AccountType;
import com.wynntils.modules.core.managers.UserManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;

import java.util.StringJoiner;

public class CommandAdmin extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "wadmin";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!UserManager.isAccountType(McIf.player().getUniqueID(), AccountType.MODERATOR)) return;

        TextComponentString output;

        if (args.length >= 1 && args[0].equalsIgnoreCase("broadcast")) {
            if (args.length < 3) {
                output = new TextComponentString("Use: /wadmin broadcast <TITLE/MESSAGE> <message>");
                output.getStyle().setColor(TextFormatting.RED);

                sender.sendMessage(output);
                return;
            }

            String type = args[1].toUpperCase();

            StringJoiner message = new StringJoiner(" ");
            for (int i = 2; i < args.length; i++) {
                message.add(args[i]);
            }

            //SocketManager.emitEvent("sendBroadcast", type, message);
            return;
        }

        output = new TextComponentString("Use: /wadmin broadcast");
        output.getStyle().setColor(TextFormatting.RED);

        sender.sendMessage(output);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
