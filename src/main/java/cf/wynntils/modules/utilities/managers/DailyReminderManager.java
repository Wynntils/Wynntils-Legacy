package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.modules.utilities.configs.UtilitiesDataConfig;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class DailyReminderManager {

    public static void checkDailyReminder(EntityPlayer p) {
        if(!UtilitiesModule.getMainConfig().dailyReminder || Reference.onWorld) return;

        UtilitiesDataConfig config = UtilitiesModule.getData();

        if(System.currentTimeMillis() > config.dailyReminder) {
            p.sendMessage(new TextComponentString("§aDaily Rewards are available!"));
            ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));

            config.dailyReminder = System.currentTimeMillis() + 1800000;
            config.saveSettings(UtilitiesModule.getModule());
        }
    }

    public static void openedDaily() {
        if(!UtilitiesModule.getMainConfig().dailyReminder || Reference.onWorld) return;

        UtilitiesDataConfig config = UtilitiesModule.getData();

        config.dailyReminder = System.currentTimeMillis() + 86400000;
        config.saveSettings(UtilitiesModule.getModule());
    }

}
