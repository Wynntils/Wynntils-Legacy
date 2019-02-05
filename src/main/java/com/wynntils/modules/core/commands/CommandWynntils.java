/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.Reference;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.webapi.WebManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

import java.util.Collections;
import java.util.List;

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
        if (args.length >= 1) {
            switch (String.join("", args).toLowerCase()) {
                case "help":
                    sender.sendMessage(new TextComponentString(
                            "§6Wynntils' command list: " +
                            "\n§8-wynntils§c help§7 Shows a list of all Wynntils' commands." +
                            "\n§8-wynntils§c discord§7 Provides you with an invite to our Discord." +
                            "\n§8-wynntils§c version§7 Show Wynntils' version." +
                            "\n§8-§ctoken§7 Provides you with a clickable token to create a Wynntils account for capes." +
                            "\n§8-§cforceupdate§7 Downloads & installs the latest successful build." +
                            "\n§8-§ccompass§7 Makes your compass point towards an x & z or a direction (e.g. north, se)."
                    ));
                    break;
                    /*Since we combine all arguments, to get the second page of help the case could be "help2" for "/wynntils help 2".*/
                case "discord":
                    TextComponentString msg = new TextComponentString("§6You're welcome to join our Discord at:\n");
                    TextComponentString link = new TextComponentString("§3" + WebManager.getApiUrls().get("DiscordInvite"));
                    link.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WebManager.getApiUrls().get("DiscordInvite")))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click here to join our Discord guild.")));
                    sender.sendMessage(msg.appendSibling(link));
                    break;
                case "version":
                    handleModVersion(sender);
                    break;
                default:
                    sender.sendMessage(new TextComponentString("§4Invalid argument, use /wynntils help for more info"));
                    break;
            }
        } else {
            sender.sendMessage(new TextComponentString("§4Missing argument, use /wynntils help for more info"));
        }
    }

    private void handleModVersion(ICommandSender sender) {
        if (Reference.developmentEnvironment) {
            sender.sendMessage(new TextComponentString("§6Wynntils' is running in a development environment"));
            return;
        }
        if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE) {
            sender.sendMessage(new TextComponentString("§6Using stable release stream: §eVersion " + Reference.VERSION));
        } else {
            if (Reference.BUILD_NUMBER == -1) {
                sender.sendMessage(new TextComponentString("§6Using cutting edge release stream: §eUnknown Build"));
            } else {
                sender.sendMessage(new TextComponentString("§6Using cutting edge release stream: §eBuild " + Reference.BUILD_NUMBER));
            }
        }
        if (WebManager.getUpdate().updateCheckFailed()) {
            sender.sendMessage(new TextComponentString("§4Wynntils failed the update check - press " + KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName() + " to try again."));
        } else if (WebManager.getUpdate().hasUpdate()) {
            sender.sendMessage(new TextComponentString("§4Wynntils is not up to date - press " + KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName() + " to update now."));
        } else {
            sender.sendMessage(new TextComponentString("§2Wynntils was up to date when last checked - press " + KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName() + " to check for updates now."));
        }
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "help", "discord", "version");
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
