package com.wynndevs.modules.expansion.experience;

import com.wynndevs.modules.expansion.misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;


public class LegacyExperienceUI extends ModGui {

    public LegacyExperienceUI(Minecraft mc) {
        ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();
        FontRenderer font = mc.fontRenderer;

        if (LegacyExperience.getCurrentWynncraftMaxXp() != -1) {
            this.drawString(font,"0",(width/2 - 93),(height - 29),0.65f,Integer.parseInt("FFA700",16));
            this.drawString(font,Integer.toString(LegacyExperience.getCurrentWynncraftMaxXp()),(width/2 + 91),(height - 29),0.65f,Integer.parseInt("FFA700",16));
            this.drawCenteredString(font,Integer.toString(Math.round(LegacyExperience.getCurrentWynncraftXp())),(width/2),(height - 27),0.65f,Integer.parseInt("FFA700",16));

            this.drawCenteredString(font,String.valueOf('\u00a7') + 'l' + LegacyExperience.getPercentage() + '%',(width/2),(height - 40),0.64f,Integer.parseInt("24ff21",16));
        }

        String[] added = LegacyExperience.getAddedAmount();
        if (!added[0].equals("0") && !added[0].startsWith("-")) {
            this.drawCenteredString(font,String.valueOf('\u00a7') + "l+" + added[1] + "%",(width/2),10 - LegacyExperience.AddedHUDProgress,1.8f,Integer.parseInt("24ff21",16));
            this.drawCenteredString(font,"+" + added[0],(width/2),26 - LegacyExperience.AddedHUDProgress,0.9f,Integer.parseInt("FFA700",16));
        }
    }
}
