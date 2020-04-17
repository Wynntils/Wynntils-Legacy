/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.commands;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.events.custom.QuestBookUpdateEvent;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CommandExportDiscoveries extends CommandBase implements IClientCommand {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    @Override
    public String getName() {
        return "exportdiscoveries";
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "exportdiscoveries";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        ITextComponent command = new TextComponentString("/exportdiscoveries");
        command.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/exportdiscoveries"));
        if (!Reference.onWorld)
            throw new CommandException("You need to be in a Wynncraft world to run %s", command);
        if (PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.NONE)
            throw new CommandException("You need to select a class to run %s", command);

        FrameworkManager.getEventBus().register(this);
        QuestManager.readQuestBook();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @SubscribeEvent
    public void onQuestBookUpdate(QuestBookUpdateEvent.Partial e) {
        if (e.getAnalysed() == AnalysePosition.SECRET_DISCOVERIES) onQuestBookUpdate();
    }

    @SubscribeEvent
    public void onQuestBookUpdate(QuestBookUpdateEvent.Full e) {
        onQuestBookUpdate();
    }

    private void onQuestBookUpdate() {
        FrameworkManager.getEventBus().unregister(this);

        File exportFolder = new File(Reference.MOD_STORAGE_ROOT, "export");
        exportFolder.mkdirs();
        String date = dateFormat.format(new Date());
        File exportFile = new File(exportFolder, date + "-discoveries.csv");
        try {
            exportFile.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        List<DiscoveryInfo> discoveries = new ArrayList<>(QuestManager.getCurrentDiscoveries());
        discoveries.sort((d1, d2) -> {
            if (d1.getMinLevel() != d2.getMinLevel()) {
                return d1.getMinLevel() - d2.getMinLevel();
            } else if (d1.getType() != d2.getType()) {
                return d1.getType().getOrder() - d2.getType().getOrder();
            } else {
                return d1.getName().compareTo(d2.getName());
            }
        });
        OutputStreamWriter output = null;
        try {
            output = new OutputStreamWriter(new FileOutputStream(exportFile));
            output.write("level,type,name\n");
            Iterator<DiscoveryInfo> discoveriesIterator = discoveries.iterator();
            while (discoveriesIterator.hasNext()) {
                DiscoveryInfo discovery = discoveriesIterator.next();
                output.write(discovery.getMinLevel() + ",");
                output.write(StringUtils.firstCharToUpper(new String[] { discovery.getType().toString().toLowerCase() }) + ",");
                output.write(TextFormatting.getTextWithoutFormattingCodes(discovery.getName()));
                if (discoveriesIterator.hasNext()) {
                    output.write("\n");
                }
            }
            ITextComponent text = new TextComponentString("Exported discoveries to ");
            ITextComponent fileText = new TextComponentString(exportFile.getName());
            fileText.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, exportFile.getCanonicalPath()));
            fileText.getStyle().setUnderlined(true);
            text.appendSibling(fileText);
            ModCore.mc().addScheduledTask(() -> { ModCore.mc().player.sendMessage(text); });
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
