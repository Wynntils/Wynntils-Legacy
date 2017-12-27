package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.core.config.GuiConfig;
import com.wynndevs.modules.market.enums.ResetAccount;
import com.jagrosh.discordipc.entities.DiscordBuild;
import net.minecraftforge.common.config.Config;

import java.util.UUID;

@Config.LangKey("config.richpresence.title")
@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public class ConfigValues {

    @GuiConfig(title = "RichPresence", isInstance = true)
    @Config.LangKey("config.richpresence")
    public static WynncraftRichPresence wynnRichPresence = new WynncraftRichPresence();

    @Config.LangKey("config.market")
    public static ConfigValues.Account marketAccount = new Account();

    @GuiConfig(title = "Enhanced", isInstance = true)
    @Config.LangKey("config.enhanced")
    public static Enhanced wynnEnhanced = new Enhanced();

    @GuiConfig(title = "Receive chat mention notifications")
    @Config.Comment("Do you want to mention notifications ?")
    public static boolean mentionNotification = true;

    public static class WynncraftRichPresence {

        @GuiConfig(title = "Entering Notifier")
        @Config.Comment("Do you want to receive entering notifications?")
        public boolean enteringNotifier = true;

        @Config.LangKey("config.richpresence.discord")
        public Discord discordConfig = new Discord();

    }

    public static class Enhanced {

        //to EHTYCSCYTHE
        //place your configs here, and if you want to create sub-interfaces feel free
        //@GuiConfig(title = "the string that will show at the config") -- to boolean values
        //@GuiConfig(title = "the string that wills how at the config", interface = true) -- to instance values like the wynnRichPresence method

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
        public String accountPass = UUID.randomUUID().toString();

        @Config.LangKey("config.market.resetaccount")
        public ResetAccount resetAccount = ResetAccount.NO;

    }

}
