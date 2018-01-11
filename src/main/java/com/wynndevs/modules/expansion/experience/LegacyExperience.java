package com.wynndevs.modules.expansion.experience;


import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.misc.Delay;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class LegacyExperience {
    private static int[] wynncraftXPLevels = new int[100];

    public static void preInit() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ModCore.mc().getResourceManager().getResource(new ResourceLocation(Reference.MOD_ID, "misc/wynncraftxplevels")).getInputStream()));
            String strLine;
            for(int j = 0; j < 100; j++){
                strLine = br.readLine();
                wynncraftXPLevels[j] = Integer.parseInt(strLine);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getXpNeededForWynncraftLevel(int level) {
        return ((level > 0 && level < 101) ? (wynncraftXPLevels[level - 1]) : (-1));
    }

    public static float getCurrentWynncraftXp(){
        return (getCurrentWynncraftMaxXp() * ModCore.mc().player.experience);
    }

    public static int getCurrentWynncraftMaxXp(){
        return getXpNeededForWynncraftLevel(ModCore.mc().player.experienceLevel);
    }

    public static String getPercentage(){
        return new DecimalFormat("##.##").format(ModCore.mc().player.experience * 100);
    }

    private static float oldXPb = -1;
    private static boolean experienceChanged() {
        if (oldXPb != -1)
        {
            if (oldXPb == getCurrentWynncraftXp())
            {
                return false;
            }
            else
            {
                oldXPb = getCurrentWynncraftXp();
                return true;
            }
        }
        else
        {
            oldXPb = getCurrentWynncraftXp();
            return false;
        }
    }

    private static int addedAmount = 0;
    private static float addedPercent = 0;
    private static Delay addedDelay = new Delay(1.95f);
    private static Delay addedHUDDelay = new Delay(0.06f,true);
    public static int addedHUDProgress = 0;

    private static int oldXPe = -1;
    private static float oldXPeP = -1;
    public static String[] getAddedAmount()
    {
        if (experienceChanged()) {
            addedAmount += getCurrentWynncraftXp() - oldXPe;
            addedPercent += ModCore.mc().player.experience - oldXPeP;

            addedHUDProgress = 0;

            addedDelay.Reset();
        }
        oldXPe = Math.round(getCurrentWynncraftXp());
        oldXPeP = ModCore.mc().player.experience;

        if (addedDelay.Passed()) {
            addedAmount = 0;
            addedPercent = 0;
        }

        if (addedHUDDelay.Passed()){
            addedHUDProgress++;
        }


        String[] OUTS = new String[2];

        OUTS[0] = Integer.toString(addedAmount);
        OUTS[1] = new DecimalFormat("##.##").format(addedPercent * 100);

        return OUTS;
    }
}

