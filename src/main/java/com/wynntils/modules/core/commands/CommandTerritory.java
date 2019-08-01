/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CommandTerritory extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "territory";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Redirects your compass to a territory";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length <= 0) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            throw new WrongUsageException("/territory [name] | Ex: /territory Detlas");
        }
        String territoryName = StringUtils.join(args, " ");
        Collection<TerritoryProfile> territories = WebManager.getTerritories().values();

        Optional<TerritoryProfile> selectedTerritory = territories.stream().filter(c -> c.getFriendlyName().equalsIgnoreCase(territoryName)).findFirst();
        if(!selectedTerritory.isPresent()) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            throw new CommandException("Invalid territory! Use: /territory [name] | Ex: /territory Detlas");
        }
        Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 10.0f);

        TerritoryProfile tp = selectedTerritory.get();

        int xMiddle = tp.getStartX() + ((tp.getEndX() - tp.getStartX())/2);
        int zMiddle = tp.getStartZ() + ((tp.getEndZ() - tp.getStartZ())/2);

        CompassManager.setCompassLocation(new Location(xMiddle, 0, zMiddle)); //update compass location

        TextComponentString success = new TextComponentString("The compass is now pointing towards " + territoryName + " (" + xMiddle + ", " + zMiddle + ")");
        success.getStyle().setColor(TextFormatting.GREEN);

        TextComponentString warn = new TextComponentString("\nPlease be sure you know that this command redirects your compass to the middle of the territory.");
        warn.getStyle().setColor(TextFormatting.AQUA);

        success.appendSibling(warn);

        TextComponentString separator = new TextComponentString("-----------------------------------------------------");
        separator.getStyle().setColor(TextFormatting.DARK_GRAY).setStrikethrough(true);

        sender.sendMessage(separator);
        sender.sendMessage(success);
        sender.sendMessage(separator);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length >= 1) {
            String temp = String.join(" ", args).toLowerCase();
            List<String> result = getListOfStringsMatchingLastWord(args, WebManager.getTerritories().values().stream().map(territoryProfile -> {
                if (args.length <= territoryProfile.getFriendlyName().split(" ").length && territoryProfile.getFriendlyName().toLowerCase().startsWith(temp)) {
                    return territoryProfile.getFriendlyName().split(" ")[args.length - 1];
                }
                return "";
            }).collect(Collectors.toList()));
            result.removeAll(Arrays.asList("", null));
            return result.stream().distinct().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
