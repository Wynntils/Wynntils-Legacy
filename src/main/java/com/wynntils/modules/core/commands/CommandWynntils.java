/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.commands;

import com.wynntils.Reference;
import com.wynntils.core.utils.Delay;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.core.overlays.ui.ChangelogUI;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;

import java.util.Collections;
import java.util.List;

public class CommandWynntils extends CommandBase implements IClientCommand {

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "wynntils";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "wynntils.commands.wynntils.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1) {
            switch (String.join("", args).toLowerCase()) {
                case "donate":
                    TextComponentTranslation c = new TextComponentTranslation("wynntils.commands.wynntils.donate.donate_text");
                    c.appendText(" ");
                    c.getStyle().setColor(TextFormatting.AQUA);
                    TextComponentString url = new TextComponentString("https://www.patreon.com/Wynntils");
                    url.getStyle()
                            .setColor(TextFormatting.LIGHT_PURPLE)
                            .setUnderlined(true)
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.patreon.com/Wynntils"))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("wynntils.commands.wynntils.donate.donate_link_hover")));

                    sender.sendMessage(c.appendSibling(url));
                    break;
                case "help":
                    TextComponentTranslation text = new TextComponentTranslation("wynntils.commands.wynntils.help.basic_text");
                    text.getStyle().setColor(TextFormatting.GOLD);
                    text.appendText(" ");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " help", "wynntils.commands.wynntils.help.help");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " discord", "wynntils.commands.wynntils.help.discord");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " version", "wynntils.commands.wynntils.help.version");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " changelog [major]", "wynntils.commands.wynntils.help.changelog");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " reloadapi", "wynntils.commands.wynntils.help.reloadapi");
                    text.appendText("\n");
                    addCommandDescription(text, "-wynntils", " donate", "wynntils.commands.wynntils.help.donate");
                    text.appendText("\n");
                    addCommandDescription(text, "-", "token", "wynntils.commands.wynntils.help.token");
                    text.appendText("\n");
                    addCommandDescription(text, "-", "forceupdate", "wynntils.commands.wynntils.help.forceupdate");
                    text.appendText("\n");
                    addCommandDescription(text, "-", "compass", "wynntils.commands.wynntils.help.compass");
                    text.appendText("\n");
                    addCommandDescription(text, "-", "territory", "wynntils.commands.wynntils.help.territory");
                    sender.sendMessage(text);
                    break;
                    /*Since we combine all arguments, to get the second page of help the case could be "help2" for "/wynntils help 2".*/
                case "discord":
                    TextComponentTranslation msg = new TextComponentTranslation("wynntils.commands.wynntils.discord.basic_text");
                    msg.appendText("\n");
                    msg.getStyle().setColor(TextFormatting.GOLD);
                    TextComponentString link = new TextComponentString(WebManager.getApiUrls().get("DiscordInvite"));
                    link.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WebManager.getApiUrls().get("DiscordInvite")))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("wynntils.commands.wynntils.discord.discord_link_hover")))
                            .setColor(TextFormatting.DARK_AQUA);
                    sender.sendMessage(msg.appendSibling(link));
                    break;
                case "version":
                    handleModVersion(sender);
                    break;
                case "reloadapi":
                    WebManager.reset();
                    WebManager.setupWebApi();
                    break;
                case "changelog":
                    new Delay(() -> {
                        boolean major = CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE;
                        Minecraft.getMinecraft().displayGuiScreen(new ChangelogUI(WebManager.getChangelog(major), major));
                    }, 1);
                    break;
                case "changelogmajor":
                    new Delay(() -> {
                        Minecraft.getMinecraft().displayGuiScreen(new ChangelogUI(WebManager.getChangelog(true), true));
                    }, 1);
                    break;
                case "debug":
                    QuestManager.requestQuestBookReading();
                    break;
                default:
                    throw new CommandException("wynntils.commands.wynntils.error.invalid_argument");
            }
        } else {
            throw new CommandException("wynntils.commands.wynntils.error.missing_argument");
        }
    }
    
    private void addCommandDescription(ITextComponent text, String prefix, String name, String description) {
        TextComponentString prefixText = new TextComponentString(prefix);
        prefixText.getStyle().setColor(TextFormatting.DARK_GRAY);
        text.appendSibling(prefixText);
        
        TextComponentString nameText = new TextComponentString(name);
        nameText.getStyle().setColor(TextFormatting.RED);
        text.appendSibling(nameText);
        
        text.appendText(" ");
        
        TextComponentTranslation descriptionText = new TextComponentTranslation(description);
        descriptionText.getStyle().setColor(TextFormatting.GRAY);
        text.appendSibling(descriptionText);
    }

    private void handleModVersion(ICommandSender sender) {
        if (Reference.developmentEnvironment) {
            TextComponentTranslation text = new TextComponentTranslation("wynntils.commands.wynntils.version.development_environment");
            text.getStyle().setColor(TextFormatting.GOLD);
            sender.sendMessage(text);
            return;
        }
        
        TextComponentTranslation releaseStreamText;
        TextComponentTranslation buildText;
        if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE) {
            releaseStreamText = new TextComponentTranslation("wynntils.commands.wynntils.version.using_stable");
            buildText = new TextComponentTranslation("wynntils.commands.wynntils.version.version", Reference.VERSION);
        } else {
            releaseStreamText = new TextComponentTranslation("wynntils.commands.wynntils.version.using_cutting_edge");
            if (Reference.BUILD_NUMBER == -1) {
                buildText = new TextComponentTranslation("wynntils.commands.wynntils.version.build_unknown");
            } else {
                buildText = new TextComponentTranslation("wynntils.commands.wynntils.version.build", Reference.BUILD_NUMBER);
            }
        }
        releaseStreamText.appendText(" ");
        releaseStreamText.getStyle().setColor(TextFormatting.GOLD);
        buildText.getStyle().setColor(TextFormatting.YELLOW);
        TextComponentString versionText = new TextComponentString("");
        versionText.appendSibling(releaseStreamText);
        versionText.appendSibling(buildText);
        
        TextComponentTranslation updateCheckText;
        TextFormatting color;
        if (WebManager.getUpdate().updateCheckFailed()) {
            updateCheckText = new TextComponentTranslation("wynntils.commands.wynntils.version.update_check_failed", KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName());
            color = TextFormatting.DARK_RED;
        } else if (WebManager.getUpdate().hasUpdate()) {
            updateCheckText = new TextComponentTranslation("wynntils.commands.wynntils.version.update_check_outdated", KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName());
            color = TextFormatting.DARK_RED;
        } else {
            updateCheckText = new TextComponentTranslation("wynntils.commands.wynntils.version.update_check_up_to_date", KeyManager.getCheckForUpdatesKey().getKeyBinding().getDisplayName());
            color = TextFormatting.DARK_GREEN;
        }
        updateCheckText.getStyle().setColor(color);
        sender.sendMessage(updateCheckText);
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "help", "discord", "version", "reloadapi", "donate");
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
