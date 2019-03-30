/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.ModCore;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length <= 0) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            TextComponentString c = new TextComponentString("Use: /territory [name] | Ex: /territory Detlas");
            c.getStyle().setColor(TextFormatting.RED);

            sender.sendMessage(c);
            return;
        }
        String territoryName = StringUtils.join(args, " ");
        Collection<TerritoryProfile> territories = WebManager.getTerritories().values();

        Optional<TerritoryProfile> selectedTerritory = territories.stream().filter(c -> c.getName().equalsIgnoreCase(territoryName)).findFirst();
        if(!selectedTerritory.isPresent()) {
            Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);

            TextComponentString c = new TextComponentString("Invalid territory! Use: /territory [name] | Ex: /territory Detlas");
            c.getStyle().setColor(TextFormatting.RED);

            sender.sendMessage(c);
            return;
        }
        Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 10.0f);

        TerritoryProfile tp = selectedTerritory.get();

        int xMiddle = tp.getStartX() + ((tp.getEndX() - tp.getStartX())/2);
        int zMiddle = tp.getStartZ() + ((tp.getEndZ() - tp.getStartZ())/2);

        ModCore.mc().world.setSpawnPoint(new BlockPos(xMiddle, 0, zMiddle));

        TextComponentString success = new TextComponentString("Compass is now pointing towards " + territoryName + " (" + xMiddle + ", " + zMiddle + ")");
        success.getStyle().setColor(TextFormatting.GREEN);

        TextComponentString warn = new TextComponentString("\nPlease be sure that you know that this command redirects your compass to the middle of the territory");
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
            return getListOfStringsMatchingLastWord(args, WebManager.getTerritories().values().stream().map(TerritoryProfile::getName).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

}
