package com.wynntils.modules.core.commands;

import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

public class CommandResetDryStreak extends CommandBase implements IClientCommand {
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "resetdry";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        UtilitiesConfig.INSTANCE.dryStreakBoxes = 0;
        UtilitiesConfig.INSTANCE.dryStreakCount = 0;
        UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
