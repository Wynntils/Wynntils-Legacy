/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.questbook.commands;

import com.wynntils.McIf;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.IClientCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandExportFavorites extends CommandBase implements IClientCommand {


    @Override
    public String getName() {
        return "exportfavorites";
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "exportfavorites";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ITextComponent command = new TextComponentString("/exportfavorites");
        command.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exportfavorites"));

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteItems);
        combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteIngredients);
        combinedList.addAll(UtilitiesConfig.INSTANCE.favoritePowders);
        combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteEmeraldPouches);

        Utils.copyToClipboard("wynntilsFavorites," + String.join(",", combinedList));
        McIf.player().sendMessage(new TextComponentString(TextFormatting.GREEN + "Copied favorites to clipboard! Open the guide list and click the + button on Artemis to import them."));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
