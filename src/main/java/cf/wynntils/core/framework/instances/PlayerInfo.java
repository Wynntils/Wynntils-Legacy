package cf.wynntils.core.framework.instances;

import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by HeyZeer0 on 24/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class PlayerInfo {

    public static PlayerInfo instance;
    public static int[] xpNeeded = new int[] {100,170,250,340,440,560,690,840,990,1160,1370,1590,1840,2140,2500,2910,3370,3870,4370,4900,5500,6250,7100,8000,9000,10200,11500,13000,14700,15900,17300,19900,22200,24800,27400,30400,33800,37300,41900,46100,51000,56400,62600,68800,77200,84500,92000,101000,112000,124600,136000,150000,165000,184000,204000,224000,248000,273000,300000,329000,363000,400000,440000,484000,528000,570000,625000,690000,760000,844000,920000,1010000,1110000,1220000,1343000,1477000,1610000,1770000,1930000,2131000,2344000,2578000,2835000,3110000,3371000,3708000,4070000,4470000,4910000,5321000,5853000,6428000,7070000,7770000,8383218,9210000,10140000,11140000,12250000,138665968,277054604};
    public static DecimalFormat perFormat = new DecimalFormat("##.#");

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

    public int getXpNeededToLevelUp() {
        return xpNeeded[mc.player.experienceLevel - 1];
    }

    public String getCurrentXPAsPercentage() {
        return perFormat.format(mc.player.experience * 100);
    }

    public int getCurrentXP() {
        return (int)((getXpNeededToLevelUp()) * mc.player.experience);
    }

    public int getLevel() { return level; }

    public int getMaxMana() {return 20;}

    public static PlayerInfo getPlayerInfo() {
        if(instance == null)
            return new PlayerInfo(Minecraft.getMinecraft());
        else
            return instance;
    }

}
