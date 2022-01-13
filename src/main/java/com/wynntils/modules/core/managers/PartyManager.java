/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.core.managers;

import java.util.regex.Pattern;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.utils.helpers.CommandResponse;

import net.minecraft.util.text.ITextComponent;

public class PartyManager {

    private static final Pattern listPattern = Pattern.compile("(§eParty members:|§eYou must be in a party to list\\.)");
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

    }, listPattern);

    public static void handlePartyList() {
        listExecutor.executeCommand();
    }

    public static void handleMessages(ITextComponent component) {
        if (McIf.getUnformattedText(component).startsWith("You have successfully joined the party.")) {
            handlePartyList();
            return;
        }
        if (McIf.getUnformattedText(component).startsWith("You have been removed from the party.")
                || McIf.getUnformattedText(component).startsWith("Your party has been disbanded since you were the only member remaining.")
                || McIf.getUnformattedText(component).startsWith("Your party has been disbanded.")) {
            PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();
            partyContainer.removeMember(McIf.player().getName());
            return;
        }
        if (McIf.getUnformattedText(component).startsWith("You have successfully created a party.")) {
            PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();
            partyContainer.setOwner(McIf.player().getName());
            partyContainer.addMember(McIf.player().getName());
            return;
        }
        if (McIf.getFormattedText(component).startsWith("§e") && McIf.getUnformattedText(component).contains("has joined the party.")) {
            PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();

            String member = McIf.getUnformattedText(component).split(" has joined the party.")[0];
            partyContainer.addMember(member);
            return;
        }
        if (McIf.getFormattedText(component).startsWith("§e") && McIf.getUnformattedText(component).contains("has left the party.")) {
            handlePartyList();
            PartyContainer partyContainer = PlayerInfo.get(SocialData.class).getPlayerParty();

            String member = McIf.getUnformattedText(component).split(" has left the party.")[0];
            partyContainer.removeMember(member);
        }
    }

}
