/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;
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
    Map<String, List<MapMarkerProfile>> mapFeatures = new HashMap<>();

    public CommandLocate() {
        for (MapMarkerProfile mmp : WebManager.getNonIgnoredApiMarkers()) {
            registerFeature(mmp);
        }
    }

    private String getFeatureKey(MapMarkerProfile mmp) {
        return mmp.getTranslatedName().replace(" ", "_");
    }

    private void registerFeature(MapMarkerProfile mmp) {
        String featureKey = getFeatureKey(mmp);
        List<MapMarkerProfile> knownProfiles = mapFeatures.get(featureKey);
        if (knownProfiles == null) {
            knownProfiles = new LinkedList<>();
        }
        knownProfiles.add(mmp);
        mapFeatures.put(featureKey, knownProfiles);
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

        List<MapMarkerProfile> knownProfiles = mapFeatures.get(args[0]);
        if (knownProfiles == null) {
            throw new WrongUsageException("Unknown map feature: " + args[0]);
        }

        TreeMap<Double, MapMarkerProfile> distanceToLocations = new TreeMap<>();
        Location currentLocation = new Location(Minecraft.getMinecraft().player);

        for (MapMarkerProfile mmp : knownProfiles) {
            Location location = new Location(mmp.getX(), currentLocation.getY(), mmp.getZ());
            double distance = location.distance(currentLocation);
            distanceToLocations.put(distance, mmp);
        }

        int numPrinted = 0;
        for (Map.Entry<Double, MapMarkerProfile> entry : distanceToLocations.entrySet()) {
            double distance = entry.getKey();
            MapMarkerProfile mmp = entry.getValue();

            ITextComponent startingPointMsg = new TextComponentString(mmp.getTranslatedName() + " at [" +
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
