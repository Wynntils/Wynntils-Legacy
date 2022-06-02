/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.core.managers;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.utils.helpers.CommandResponse;
import net.minecraft.util.text.ITextComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartyManager {

    private static final Pattern LIST_PATTERN = Pattern.compile("(§eParty members:|§eYou must be in a party to list\\.)");
    private static final Pattern PROMOTE_PATTERN = Pattern.compile("Successfully promoted (.+) to party leader!");

    private static final CommandResponse listExecutor = new CommandResponse("/party list", (matcher, text) -> {
        String entire = matcher.group(0);
        if (entire.contains("You must be in")) {  // clears the party
            PlayerInfo.get(SocialData.class).getPlayerParty().removeMember(McIf.player().getName());
            return;
        }

        PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();
        for (ITextComponent components : text.getSiblings()) {
            if (McIf.getFormattedText(components).startsWith("§e")) continue;

            boolean owner = McIf.getFormattedText(components).startsWith("§b");
            String member = McIf.getUnformattedText(components).contains(",") ? McIf.getUnformattedText(components).split(",")[0] : McIf.getUnformattedText(components);

            if (owner) partyContainer.setOwner(member);
            partyContainer.addMember(member);
        }

    }, LIST_PATTERN);

    public static void handlePartyList() {
        listExecutor.executeCommand();
    }

    public static void handleMessages(ITextComponent component) {
        String unformattedText = McIf.getUnformattedText(component);
        if (unformattedText.startsWith("You have successfully joined the party.")) {
            handlePartyList();
            return;
        }
        PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();
        if (unformattedText.startsWith("You have been removed from the party.")
                || McIf.getUnformattedText(component).startsWith("Your party has been disbanded since you were the only member remaining.")
                || McIf.getUnformattedText(component).startsWith("Your party has been disbanded.")) {
            PlayerInfo.get(SocialData.class).resetPlayerParty();
            return;
        }
        if (unformattedText.startsWith("You have successfully created a party.")) {
            partyContainer.setOwner(McIf.player().getName());
            partyContainer.addMember(McIf.player().getName());
            return;
        }
        if (McIf.getFormattedText(component).startsWith("§e") && McIf.getUnformattedText(component).contains("has joined the party.")) {
            String member = McIf.getUnformattedText(component).split(" has joined the party.")[0];
            partyContainer.addMember(member);
            return;
        }
        if (McIf.getFormattedText(component).startsWith("§e") && McIf.getUnformattedText(component).contains("has left the party.")) {
            handlePartyList();

            String member = McIf.getUnformattedText(component).split(" has left the party.")[0];
            partyContainer.removeMember(member);
            return;
        }
        if (unformattedText.startsWith("You are now the leader of this party! Type /party for a list of commands.")) {
            partyContainer.setOwner(McIf.player().getName());
            return;
        }
        Matcher promoteMatcher = PROMOTE_PATTERN.matcher(unformattedText);
        if (promoteMatcher.find()) {
            partyContainer.setOwner(promoteMatcher.group(1));
        }
    }

}
