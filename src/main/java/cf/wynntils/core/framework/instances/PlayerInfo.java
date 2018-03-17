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


    private Minecraft mc;
    private String name;
    private UUID uuid;
    private ClassType currentClass = ClassType.NONE;
    private int health = -1;
    private int maxHealth = -1;
    private int level = -1;
    private float experiencePercentage = -1;
    //TODO math for Wynn exp values

    private String lastActionBar;

    public PlayerInfo(Minecraft mc) {
        this.mc = mc; this.name = mc.player.getName(); this.uuid = mc.player.getUniqueID();

        instance = this;
    }

    public void updateActionBar(String lastActionBar) {
        this.lastActionBar = lastActionBar;
        if(currentClass == ClassType.NONE) {
            this.health = -1;
            this.maxHealth = -1;
            this.level = -1;
            this.experiencePercentage = -1;
        } else {
            if (lastActionBar.contains("❤")) {
                StringBuilder read = new StringBuilder();
                for (char c : lastActionBar.substring(4).toCharArray()) {
                    if (c == '/') {
                        this.health = Integer.parseInt(read.toString());
                        read = new StringBuilder();
                    } else if (c == '§') {
                        this.maxHealth = Integer.parseInt(read.toString());
                        break;
                    } else {
                        read.append(c);
                    }
                }
            }
            this.level = mc.player.experienceLevel;
            this.experiencePercentage = mc.player.experience;
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
        return currentClass == ClassType.NONE ? -1 : health;
    }

    public int getCurrentMana() { return currentClass == ClassType.NONE ? -1 : mc.player.getFoodStats().getFoodLevel(); }

    public int getMaxHealth() {
        return currentClass == ClassType.NONE ? -1 : maxHealth;
    }

    public float getExperiencePercentage() { return experiencePercentage; }

    public int getLevel() { return level; }

    public int getMaxMana() {return 20;}

    public static PlayerInfo getPlayerInfo() {
        if(instance == null)
            return new PlayerInfo(Minecraft.getMinecraft());
        else
            return instance;
    }

}
