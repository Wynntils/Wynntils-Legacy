/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.map.commands;

import com.wynntils.modules.map.instances.LabelBake;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.LocationProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import java.util.*;

public class CommandDetection extends CommandBase implements IClientCommand {
    Map<String, List<LocationProfile>> mapFeatures = new HashMap<>();

    public CommandDetection() {
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
        return location.getTranslatedName().replace(" ", "_");
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "detection";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "detection <output filename>";
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

        String filename = args[0];
        LabelBake.dumpDetectedLocations(filename);
    }

}
