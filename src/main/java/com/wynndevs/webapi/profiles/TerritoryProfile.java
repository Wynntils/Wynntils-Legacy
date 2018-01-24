package com.wynndevs.webapi.profiles;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TerritoryProfile {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String name;
    int startX;
    int startZ;
    int endX;
    int endZ;

    String guild;
    String attacker;
    Date acquired;

    public TerritoryProfile(String name, int startX, int startZ, int endX, int endZ, String guild, String attacker, String date) {
        this.name = name;

        this.guild = guild;
        this.attacker = attacker;

        try{
            this.acquired = dateFormat.parse(date);
        }catch (Exception ignored) {}


        if(endX < startX) {
            this.startX = endX;
            this.endX = startX;
        }else{
            this.startX = startX;
            this.endX = endX;
        }

        if(endZ < startZ) {
            this.startZ = endZ;
            this.endZ = startZ;
        }else{
            this.startZ = startZ;
            this.endZ = endZ;
        }
    }

    public String getName() {
        return name;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndZ() {
        return endZ;
    }

    public String getGuild() {
        return guild;
    }

    public String getAttacker() {
        return attacker;
    }

    public Date getAcquired() {
        return acquired;
    }

    public boolean insideArea(int playerX, int playerZ){
        return startX <= playerX && endX >= playerX && startZ <= playerZ && endZ >= playerZ;
    }

}
