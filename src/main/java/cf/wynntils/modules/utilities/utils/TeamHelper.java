/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;


public class TeamHelper {

    String name;
    String color;

    Team selectedTeam;
    Minecraft mc = Minecraft.getMinecraft();

    public TeamHelper(String name, String color) {
        this.name = name; this.color = color;
    }

    public Team getTeam() {
        if(!mc.player.getWorldScoreboard().getTeamNames().contains(name)) mc.player.getWorldScoreboard().createTeam(name).setPrefix("§" + color);
        if(selectedTeam == null) selectedTeam = mc.player.getWorldScoreboard().getTeam(name);

        return selectedTeam;
    }

    public void addEntity(Entity entity) {
        if(!mc.player.getWorldScoreboard().getTeamNames().contains(name)) mc.player.getWorldScoreboard().createTeam(name).setPrefix("§" + color);

        mc.player.getWorldScoreboard().addPlayerToTeam(entity.getCachedUniqueIdString(), name);
    }

}
