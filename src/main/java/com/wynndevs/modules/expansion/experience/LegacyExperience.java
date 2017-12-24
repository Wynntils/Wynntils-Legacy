package com.wynndevs.modules.expansion.experience;


import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.Delay;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class LegacyExperience {
    private static int[] WynncraftXPLevels = new int[100];

    public static void PreInit() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ModCore.mc().getResourceManager().getResource(new ResourceLocation(ExpReference.MOD_ID, "misc/wynncraftxplevels")).getInputStream()));
            String strLine;
            for(int j = 0; j < 100; j++){
                strLine = br.readLine();
                WynncraftXPLevels[j] = Integer.parseInt(strLine);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getXpNeededForWynncraftLevel(int level) {
        return ((level > 0 && level < 101) ? (WynncraftXPLevels[level - 1]) : (-1));
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
    private static boolean ExperienceChanged() {
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

    private static int AddedAmount = 0;
    private static float AddedPercent = 0;
    private static Delay AddedDelay = new Delay(1.95f);
    private static Delay AddedHUDDelay = new Delay(0.06f,true);
    public static int AddedHUDProgress = 0;

    private static int oldXPe = -1;
    private static float oldXPeP = -1;
    public static String[] getAddedAmount()
    {
        if (ExperienceChanged()) {
            AddedAmount += getCurrentWynncraftXp() - oldXPe;
            AddedPercent += ModCore.mc().player.experience - oldXPeP;

            AddedHUDProgress = 0;

            AddedDelay.Reset();
        }
        oldXPe = Math.round(getCurrentWynncraftXp());
        oldXPeP = ModCore.mc().player.experience;

        if (AddedDelay.Passed()) {
            AddedAmount = 0;
            AddedPercent = 0;
        }

        if (AddedHUDDelay.Passed()){
            AddedHUDProgress++;
        }


        String[] OUTS = new String[2];

        OUTS[0] = Integer.toString(AddedAmount);
        OUTS[1] = new DecimalFormat("##.##").format(AddedPercent * 100);

        return OUTS;
    }
}

