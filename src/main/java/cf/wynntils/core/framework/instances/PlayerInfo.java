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

    public PlayerInfo(Minecraft mc) {
        this.mc = mc; this.name = mc.player.getName(); this.uuid = mc.player.getUniqueID();

        instance = this;
    }

    public void updateActionBar(String lastActionBar) {
        this.lastActionBar = lastActionBar;
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
        return  currentClass != ClassType.NONE ? (int)mc.player.getMaxHealth() : -1;
    }

    public int getCurrentHealth() {
        return currentClass != ClassType.NONE ? (int)mc.player.getHealth() : -1;
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
