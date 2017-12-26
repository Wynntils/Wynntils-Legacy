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

    @GuiConfig(title = "Rich Presence", isInstance = true)
    @Config.LangKey("config.richpresence")
    public static WynncraftRichPresence wynnRichPresence = new WynncraftRichPresence();

    @Config.LangKey("config.market")
    public static ConfigValues.Account marketAccount = new Account();

    @GuiConfig(title = "Receive chat mention notifications")
    @Config.Comment("Do you want to mention notifications ?")
    public static boolean mentionNotification = true;

    @GuiConfig(title = "Test1")
    public static boolean iAmATest1 = true;
    @GuiConfig(title = "Test2")
    public static boolean iAmATest2 = true;
    @GuiConfig(title = "Test3")
    public static boolean iAmATest3 = true;
    @GuiConfig(title = "Test4")
    public static boolean iAmATest4 = true;
    @GuiConfig(title = "Test5")
    public static boolean iAmATest5 = true;
    @GuiConfig(title = "Test6")
    public static boolean iAmATest6 = true;

    public static class WynncraftRichPresence {

        @GuiConfig(title = "Entering Notifier")
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
        public String accountPass = UUID.randomUUID().toString();

        @Config.LangKey("config.market.resetaccount")
        public ResetAccount resetAccount = ResetAccount.NO;

    }

}
