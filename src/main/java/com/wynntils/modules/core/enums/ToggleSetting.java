/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.core.enums;

import com.wynntils.McIf;
import com.wynntils.core.utils.helpers.CommandResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.regex.Pattern;

public enum ToggleSetting {

    MUSIC ("music", "You will now hear music again!", "Music is now fading out..."),
    QUEST_TRACKER("autotracking", "Your quests will now auto-track.", "Your quests will not auto-track anymore.");

    private static final Pattern TOGGLE_MESSAGE_PATTERN = Pattern.compile("(^§2)");

    String name;
    String enabledMatcher;
    String disabledMatcher;

    ToggleSetting(String name, String enabledMatcher, String disabledMatcher) {
        this.name = name;
        this.enabledMatcher = enabledMatcher;
        this.disabledMatcher = disabledMatcher;
    }

    /**
     * Updates the provided toggle setting to the defined value
     * If the setting was toggled also sends a message warning the player.
     *
     * @param value the expected value
     */
    public void set(boolean value) {
        set(value, true);
    }

    private void set(boolean value, boolean showMessage) {
        String matcher = value ? enabledMatcher : disabledMatcher;
        String inverseMatcher = value ? disabledMatcher : enabledMatcher;

        CommandResponse response = new CommandResponse("/toggle " + name, (m, t) -> {
            String message = McIf.getUnformattedText(t);
            // Try again if the current value was already the expected result
            if (!message.contains(matcher)) {
                // Make sure it's the right toggle in case we're running multiple
                if (!message.contains(inverseMatcher)) return;

                set(value, false);
                return;
            }

            // show message is false when the option was already on the value status
            if (!showMessage) return;

            McIf.player().sendMessage(getToggleText(value));
        }, TOGGLE_MESSAGE_PATTERN);

        response.setCancel(true);

        response.executeCommand();
    }

    private TextComponentBase getToggleText(boolean value) {
        String function = value ? "enabled" : "disabled";
        String callback = value ? "disable" : "enable";

        TextComponentString base = new TextComponentString(
                "Wynntils automatically "
        );
        base.getStyle().setColor(TextFormatting.GRAY);

        TextComponentString status = new TextComponentString(function);
        status.getStyle().setColor(TextFormatting.WHITE);
        base.appendSibling(status);

        TextComponentString continuation = new TextComponentString(" Wynncraft toggle option ");
        continuation.getStyle().setColor(TextFormatting.GRAY);
        base.appendSibling(continuation);

        TextComponentString toggle = new TextComponentString(name);
        toggle.getStyle().setColor(TextFormatting.WHITE);
        base.appendSibling(toggle);

        TextComponentString back = new TextComponentString(" (mostly likely for conflict issues).\nTo " + callback + " it again type ");
        back.getStyle().setColor(TextFormatting.GRAY);
        base.appendSibling(back);

        TextComponentString backCommand = new TextComponentString("/toggle " + name);
        backCommand.getStyle().setColor(TextFormatting.WHITE);
        backCommand.getStyle().setUnderlined(true);
        backCommand.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/toggle " + name));
        backCommand.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new TextComponentString("Click to run /toggle " + name))
        );
        base.appendSibling(backCommand);

        TextComponentString end = new TextComponentString(".\n");
        end.getStyle().setColor(TextFormatting.GRAY);
        base.appendSibling(end);

        return base;
    }

}
