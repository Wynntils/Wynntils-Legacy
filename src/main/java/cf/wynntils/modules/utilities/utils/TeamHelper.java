/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;


public class TeamHelper {

    String name;
    String color;
    boolean useArray = false;

    Team selectedTeam;
    Minecraft mc = Minecraft.getMinecraft();

    ArrayList<String> added = new ArrayList<>();

    public TeamHelper(String name, String color) {
        this.name = name; this.color = color;
    }

    public TeamHelper(String name, String color, boolean useArray) {
        this.name = name; this.color = color; this.useArray = useArray;
    }


    public Team getTeam() {
        if(!mc.player.getWorldScoreboard().getTeamNames().contains(name)) {
            mc.player.getWorldScoreboard().createTeam(name).setPrefix("§" + color);
            added.clear();
        }
        if(selectedTeam == null) selectedTeam = mc.player.getWorldScoreboard().getTeam(name);

        return selectedTeam;
    }

    public void addEntity(Entity entity) {
        if(useArray && added.contains(entity.getUniqueID().toString())) return;
        if(!mc.player.getWorldScoreboard().getTeamNames().contains(name)) {
            mc.player.getWorldScoreboard().createTeam(name).setPrefix("§" + color);
            added.clear();
        }

        mc.player.getWorldScoreboard().addPlayerToTeam(entity.getCachedUniqueIdString(), name);
        if(useArray) added.add(entity.getUniqueID().toString());
    }

    public void addPlayer(EntityPlayer entity) {
        if(useArray && added.contains(entity.getUniqueID().toString())) return;
        if(!mc.player.getWorldScoreboard().getTeamNames().contains(name)) {
            mc.player.getWorldScoreboard().createTeam(name).setPrefix("§" + color);
            added.clear();
        }

        mc.player.getWorldScoreboard().addPlayerToTeam(entity.getName(), name);
        if(useArray) added.add(entity.getUniqueID().toString());
    }

}
