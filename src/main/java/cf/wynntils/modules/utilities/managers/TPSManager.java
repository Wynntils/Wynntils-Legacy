package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.core.utils.LimitedList;
import cf.wynntils.modules.utilities.UtilitiesModule;
import net.minecraft.util.text.TextComponentString;

import java.text.DecimalFormat;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class TPSManager {

    private static LimitedList<double[]> tpsInfo = new LimitedList<>(2400);
    private static DecimalFormat tpsFormat = new DecimalFormat("00.0");

    public static void updateTPS() {
        if(!UtilitiesModule.getMainConfig().showTPSCount) return;

        tpsInfo.add(new double[] {System.currentTimeMillis(), ModCore.mc().world.getWorldTime()});

        //5s
        double tpsWorld5 = 0;
        double tpsSystem5 = 0;
        int tickAmount5 = 1;

        //20s
        double tpsWorld20 = 0;
        double tpsSystem20 = 0;
        int tickAmount20 = 1;

        //60s
        double tpsWorld60 = 0;
        double tpsSystem60 = 0;
        int tickAmount60 = 1;

        for(int i = 0; i < tpsInfo.size(); i++) {
            if (i >= 5 && i <= 105) {
                tpsSystem5 = tpsSystem5 + (tpsInfo.get(i-1)[0] - tpsInfo.get(i)[0]);
                tpsWorld5 = tpsWorld5 + (tpsInfo.get(i-1)[1] - tpsInfo.get(i)[1]);
                tickAmount5++;
            }
            if (i >= 5 && i <= 405) {
                tpsSystem20 = tpsSystem20 + (tpsInfo.get(i-1)[0] - tpsInfo.get(i)[0]);
                tpsWorld20 = tpsWorld20 + (tpsInfo.get(i-1)[1] - tpsInfo.get(i)[1]);
                tickAmount20++;
            }
            if (i >= 5) {
                tpsSystem60 = tpsSystem60 + (tpsInfo.get(i-1)[0] - tpsInfo.get(i)[0]);
                tpsWorld60 = tpsWorld60 + (tpsInfo.get(i-1)[1] - tpsInfo.get(i)[1]);
                tickAmount60++;
            }
        }

        double tps5 = (tpsWorld5 / (tickAmount5 <= 1 ? tickAmount5 : tickAmount5-1)) / ((tpsSystem5 / 1000) / (tickAmount5 <= 1 ? tickAmount5 : tickAmount5-1));
        double tps20 = (tpsWorld20 / (tickAmount20 <= 1 ? tickAmount20 : tickAmount20-1)) / ((tpsSystem20 / 1000) / (tickAmount20 <= 1 ? tickAmount20 : tickAmount20-1));
        double tps60 = (tpsWorld60 / (tickAmount60 <= 1 ? tickAmount60 : tickAmount60-1)) / ((tpsSystem60 / 1000) / (tickAmount60 <= 1 ? tickAmount60 : tickAmount60-1));

        if(tps5 > 20) tps5 = 20;
        if(tps20 > 20) tps20 = 20;
        if(tps60 > 20) tps60 = 20;

        String footer = "§aEstimated TPS       ";

        //5s
        if(tps5 >= 18) {
            footer = footer + "§a";
        }else if(tps5 >= 15) {
            footer = footer + "§e";
        }else if(tps5 < 15) {
            footer = footer + "§c";
        }
        footer = footer + tpsFormat.format(tps5) + "/5s       ";

        //20s
        if(tps20 >= 18) {
            footer = footer + "§a";
        }else if(tps20 >= 15) {
            footer = footer + "§e";
        }else if(tps20 < 15) {
            footer = footer + "§c";
        }
        footer = footer + tpsFormat.format(tps20) + "/20s       ";

        //60s
        if(tps60 >= 18) {
            footer = footer + "§a";
        }else if(tps60 >= 15) {
            footer = footer + "§e";
        }else if(tps60 < 15) {
            footer = footer + "§c";
        }
        footer = footer + tpsFormat.format(tps60) + "/60s";

        ModCore.mc().ingameGUI.getTabList().setFooter(new TextComponentString(footer));
    }

    public static void clearTpsInfo() {
        tpsInfo.clear();
    }
}
