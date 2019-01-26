/*
 *  * Copyright © Wynntils - 2019.
 */

package cf.wynntils.modules.core.commands;

import cf.wynntils.webapi.WebManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class CommandWynntils extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "wynntils";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/wynntils <command>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "help":
                    sender.sendMessage(new TextComponentString(
                            "§6Wynntils' command list (/wynntils <command>): " +
                            "\n§8-§chelp§7 Shows a list of all Wynntils' commands." +
                            "\n§8-§ctoken§7 Provides you with a clickable token to create a Wynntils account for capes." +
                            "\n§8-§cforceupdate§7 Downloads & installs the latest successful build." +
                            "\n§8-§ccompass§7 Makes your compass point towards an x & z or a direction (e.g. north, se)."
                    ));
            }
        } else {
            sender.sendMessage(new TextComponentString("§4Missing argument, use /wynntils help for more info"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}