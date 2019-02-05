/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.webapi.account.WynntilsAccount;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

public class CommandToken extends CommandBase implements IClientCommand {


    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "token";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Returns your Wynntils auth token";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        ITextComponent token = new TextComponentString(WynntilsAccount.getToken());
        token.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://capes.wynntils.com/register.php?token=" + WynntilsAccount.getToken()));
        token.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click me to register account")));
        token.getStyle().setColor(TextFormatting.DARK_AQUA);
        token.getStyle().setUnderlined(true);
        sender.sendMessage(new TextComponentString("§bWynntils Token: §3").appendSibling(token));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
