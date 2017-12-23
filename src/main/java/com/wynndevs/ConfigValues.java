package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.market.enums.ResetAccount;
import com.wynndevs.richpresence.utils.RichUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.minecraftforge.common.config.Config;

import java.util.UUID;

@Config.LangKey("config.richpresence.title")
@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public class ConfigValues {

    @Config.LangKey("config.richpresence")
    public static WynncraftRichPresence wynnRichPresence = new WynncraftRichPresence();

    @Config.LangKey("config.market")
    public static ConfigValues.Account marketAccount = new Account();

    public static class WynncraftRichPresence {

        @Config.Comment("Do you want to receive entering notifications?")
        public boolean enteringNotifier = true;

        @Config.LangKey("config.richpresence.discord")
        public Discord discordConfig = new Discord();

    }

    public static class Discord {

        @Config.Comment("Your nicknname and class will be showed at rich presence")
        public boolean showNicknameAndClass = true;

        @Config.RequiresMcRestart
        @Config.LangKey("config.richpresence.discordversion")
        @Config.Comment("Your discord version | Availabe: Any, Stable, Canary, PTB")
        public DiscordBuild discordBuild = DiscordBuild.ANY;
    }

    public static class Account {

        @Config.RequiresMcRestart
        public String accountName = UUID.randomUUID().toString();

        @Config.RequiresMcRestart
        public String accountPass = RichUtils.generatePassword(15);

        @Config.LangKey("config.market.resetaccount")
        public ResetAccount resetAccount = ResetAccount.NO;

    }

}
