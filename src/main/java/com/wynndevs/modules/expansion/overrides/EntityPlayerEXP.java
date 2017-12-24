package com.wynndevs.modules.expansion.overrides;

import com.wynndevs.modules.expansion.misc.EPowderSymbol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.lang.reflect.Field;


public class EntityPlayerEXP extends EntityPlayerSP {
    public static String rawText = "";
    private static String healthText = "";
    private static String sprintText = null;
    /**
     * Variable holding the original class that is getting overwritten
     */
    private EntityPlayerSP original;

    public EntityPlayerEXP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatisticsManager statFile) {
        super(mcIn, worldIn, netHandler, statFile);

        Class <?> cClass0 = EntityPlayerSP.class;
        Class <?> cClass1 = cClass0.getSuperclass();
        Class <?> cClass2 = cClass1.getSuperclass();
        Class <?> cClass3 = cClass2.getSuperclass();
        Class <?> cClass4 = cClass3.getSuperclass();

        copyClass(cClass0, original, this);
        copyClass(cClass1, original, this);
        copyClass(cClass2, original, this);
        copyClass(cClass3, original, this);
        copyClass(cClass4, original, this);

        this.original = original;
    }

    public static String getHealthText() {
        return healthText;
    }

    public void setHealthText(String healthText) {
        EntityPlayerEXP.healthText = healthText;
    }

    public static String getSprintText() {
        return sprintText;
    }

    public void setSprintText(String sprintText) {
        EntityPlayerEXP.sprintText = sprintText;
    }

    public void sendStatusMessage(ITextComponent chatComponent) {
        String text = chatComponent.getFormattedText().trim();
        rawText = TextFormatting.getTextWithoutFormattingCodes(text);

        if (text.contains("/20\u00A7r")) {
            final String lookForPowder = String.format("%s%s%s%s%s", EPowderSymbol.AIR.getSymbol(), EPowderSymbol.FIRE.getSymbol(), EPowderSymbol.EARTH.getSymbol(), EPowderSymbol.WATER.getSymbol(), EPowderSymbol.THUNDER.getSymbol());
            final String lookForFull = "[" + lookForPowder + "] 100%";
            final String lookForFill = "[" + lookForPowder + "] [0-9]{1,2}%";
            if (rawText.split(lookForFull).length > 1) {
                final String splt = rawText.split(lookForFull)[0];
                final String powder = rawText.substring(splt.length(), splt.length() + 1);
                int index = TextFormatting.getTextWithoutFormattingCodes(lookForPowder).indexOf(powder);
                //					if (index > 0) index = 3;
                //					System.out.println("LFP: {" + lookForPowder + "} P: {" + powder + "} I: {" + index + "}");
                String uncolor = TextFormatting.getTextWithoutFormattingCodes(text);
                int indexH = uncolor.indexOf("/");
                indexH = uncolor.indexOf(" ", indexH + 1);
                setHealthText(uncolor.substring(0, indexH));
                setHealthText(String.format("%s%s", "\u00A7r\u00A7c", getHealthText()));
                if (index < 0 || index >= 5) {
                    //						System.out.println("Failed to fetch powder");
                }
            } else {

                String uncolor = TextFormatting.getTextWithoutFormattingCodes(text);
                boolean hasSprint = uncolor.contains("Sprint");
                int index = uncolor.indexOf("/");
                index = uncolor.indexOf(" ", index + 1);
                setHealthText(uncolor.substring(0, index));
                setHealthText(String.format("%s%s", "\u00A7r\u00A7c", getHealthText()));
                if (hasSprint && mc.player.isSprinting()) {
                    index = text.indexOf('[');
                    while (text.charAt(index) != '\u00A7')
                        index--;
                    int index1 = text.indexOf(']', index + 1) + 1;
                    setSprintText(text.substring(index, index1));
                }
            }
        }
    }

    private void copyClass(Class <?> c, Object src, Object dest) {
        Field[] fields = c.getDeclaredFields();
        Field field;
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            field.setAccessible(false);
            field.setAccessible(true);
            try {
                field.set(dest, field.get(src));
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }
}
