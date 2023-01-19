/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.McIf;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.LocationProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

import java.util.*;

import static net.minecraft.util.text.TextFormatting.GRAY;

public class CommandLocate extends CommandBase implements IClientCommand {
    Map<String, List<LocationProfile>> mapFeatures = new HashMap<>();

    public CommandLocate() {
        for (LocationProfile location : WebManager.getNonIgnoredApiMarkers()) {
            mapFeatures.put(getFeatureKey(location), getProfileList(location));
        }
        for (LocationProfile location : WebManager.getNpcLocations()) {
            mapFeatures.put(getFeatureKey(location), Collections.singletonList(location));
        }
        for (LocationProfile location : WebManager.getMapLabels()) {
            mapFeatures.put(getFeatureKey(location), Collections.singletonList(location));
        }
    }

    private List<LocationProfile> getProfileList(LocationProfile location) {
        List<LocationProfile> knownProfiles = mapFeatures.get(getFeatureKey(location));
        if (knownProfiles == null) {
            knownProfiles = new LinkedList<>();
        }
        knownProfiles.add(location);
        return knownProfiles;
    }

    private String getFeatureKey(LocationProfile location) {
        return (location == null || location.getTranslatedName() == null) ? "feature_unavailable" : location.getTranslatedName().replace(" ", "_");
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "locate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "locate <map feature>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException("/" + getUsage(sender));
        }

        List<LocationProfile> knownProfiles = mapFeatures.get(args[0]);
        if (knownProfiles == null) {
            // Try case insensitive search
            Optional<String> match = mapFeatures.keySet().stream().filter(key -> key.toLowerCase().equals(args[0].toLowerCase())).findFirst();
            if (!match.isPresent()) {
                throw new WrongUsageException("Unknown map feature: " + args[0]);
            } else {
                knownProfiles = mapFeatures.get(match.get());
            }
        }

        Map<Double, LocationProfile> distanceToLocations = new TreeMap<>();
        Location currentLocation = new Location(McIf.player());

        for (LocationProfile locationProfile : knownProfiles) {
            Location location = new Location(locationProfile.getX(), currentLocation.getY(), locationProfile.getZ());
            double distance = location.distance(currentLocation);
            distanceToLocations.put(distance, locationProfile);
        }

        int numPrinted = 0;
        for (Map.Entry<Double, LocationProfile> entry : distanceToLocations.entrySet()) {
            double distance = entry.getKey();
            LocationProfile mmp = entry.getValue();

            ITextComponent startingPointMsg = new TextComponentString(mmp.getTranslatedName() + " is located at [" +
                    mmp.getX() + ", " + mmp.getZ() + "] (" + (int) distance + " blocks)");
            startingPointMsg.getStyle().setColor(GRAY);
            sender.sendMessage(startingPointMsg);
            numPrinted++;
            if (numPrinted >= 3) break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return getListOfStringsMatchingLastWord(args, mapFeatures.keySet());
    }
}
