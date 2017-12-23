package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.wynnmarket.enums.ResetAccount;
import com.wynndevs.wynnrp.utils.Utils;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.minecraftforge.common.config.Config;

import java.util.UUID;

@Config.LangKey("config.wynnrp.title")
@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public class ConfigValues {

    @Config.LangKey("config.wynnrp.discord")
    public static Discord discordConfig = new Discord();

    @Config.LangKey("config.wynnrp.marketaccount")
    public static ConfigValues.Account marketAccount = new Account();

    @Config.Comment("Do you want to receive entering notifications?")
    public static boolean enteringNotifier = true;

    public static class Discord {

        @Config.Comment("Your nicknname and class will be showed at rich presence")
        public boolean showNicknameAndClass = true;

        @Config.RequiresMcRestart
        @Config.LangKey("config.wynnrp.discordversion")
        @Config.Comment("Your discord version | Availabe: Any, Stable, Canary, PTB")
        public DiscordBuild discordBuild = DiscordBuild.ANY;
    }

    public static class Account {

        @Config.RequiresMcRestart
        public String accountName = UUID.randomUUID().toString();

        @Config.RequiresMcRestart
        public String accountPass = Utils.generatePassword(15);

        @Config.LangKey("config.wynnrp.resetaccount")
        public ResetAccount resetAccount = ResetAccount.NO;

    }

}
