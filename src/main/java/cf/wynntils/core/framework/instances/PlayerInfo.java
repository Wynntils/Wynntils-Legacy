package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;

import java.util.UUID;

/**
 * Created by HeyZeer0 on 24/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayerInfo {

    public static PlayerInfo instance;


    Minecraft mc;
    String name;
    UUID uuid;

    ClassType currentClass;

    //Actionbar Things
    String lastActionBar;
    String[] lastHealth = null;

    public PlayerInfo(Minecraft mc) {
        this.mc = mc; this.name = mc.player.getName(); this.uuid = mc.player.getUniqueID();

        instance = this;
    }

    public void updateActionBar(String lastActionBar) {
        this.lastActionBar = lastActionBar;
        this.lastHealth = Utils.stripColor(lastActionBar).split("/");
    }

    public void updatePlayerClass(ClassType currentClass) {
        this.currentClass = currentClass;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

    public int getMaxHealth() {
        return lastHealth != null && currentClass != ClassType.NONE ? Integer.valueOf(lastHealth[1].split(" ")[0]) : -1;
    }

    public int getCurrentHealth() {
        return lastHealth != null && currentClass != ClassType.NONE ? Integer.valueOf(lastHealth[0].split(" ")[1]) : -1;
    }

    public int getCurrentMana() {
        return mc.player.getFoodStats().getFoodLevel();
    }

    public static PlayerInfo getPlayerInfo() {
        if(instance == null)
            instance = new PlayerInfo(Minecraft.getMinecraft());

        return instance;
    }

}
