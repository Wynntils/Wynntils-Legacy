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
    int health;
    int maxHealth;
    int sprint;


    ClassType currentClass = ClassType.NONE;

    //Actionbar Things
    String lastActionBar;

    public PlayerInfo(Minecraft mc) {
        this.mc = mc; this.name = mc.player.getName(); this.uuid = mc.player.getUniqueID();
        this.health = -1;
        this.maxHealth = -1;
        this.sprint = -1;

        instance = this;
    }

    public void updateActionBar(String lastActionBar) {
        this.lastActionBar = lastActionBar;
        if(currentClass == ClassType.NONE) {
            this.health = -1;
            this.maxHealth = -1;
            this.sprint = -1;
        }
        else if(lastActionBar.contains("❤")) {
            StringBuilder read = new StringBuilder();
            for (char c : lastActionBar.substring(4).toCharArray()) {
                if(c == '/') {
                    this.health = Integer.parseInt(read.toString());
                    read = new StringBuilder();
                } else if(c == '§') {
                    this.maxHealth = Integer.parseInt(read.toString());
                    break;
                } else {
                    read.append(c);
                }
            }/*  TODO, redo something to track sprint
            if(lastActionBar.contains("[§8")) this.sprint = 0;
            else if(lastActionBar.contains("[§a")){
                this.sprint = -1;
                for(char c : lastActionBar.toCharArray()) {
                    if((sprint == -1 && c == 'a') || (sprint != -1 && c != '§')) {
                        sprint++;
                    }
                    else if(sprint != -1) {
                        break;
                    }
                }
            }*/
        }
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

    public int getCurrentHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentSprint() {
        return sprint;
    }

    public int getMaxSprint() {
        return 12;
    }

    public int getMaxMana() {return 20;}

    public int getCurrentMana() {
        return currentClass == ClassType.NONE ? -1 : mc.player.getFoodStats().getFoodLevel();
    }

    public static PlayerInfo getPlayerInfo() {
        if(instance == null)
            return new PlayerInfo(Minecraft.getMinecraft());
        else
            return instance;
    }

}
