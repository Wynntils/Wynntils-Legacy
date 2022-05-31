/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyManagementUI extends GuiScreen {

    private GuiButton exitBtn;
    private List<GuiButton> buttons = new ArrayList<>();
    private GuiTextField inviteField;

    private List<String> partyMembers = new ArrayList<>();
    private int page;
    private int pageHeight;

    private final int verticalReference = 160;

    @Override
    public void initGui() {
        super.initGui();

        pageHeight = (this.height - 100) / 25;
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));

        onMemberListChange();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        inviteField = new GuiTextField(0, McIf.mc().fontRenderer, this.width/2 - 205, this.height/2 - 70, 160, verticalReference - 23);

        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width/2 - 205, verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 165, verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Promote", this.width/2 + 120, verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Kick", this.width/2 + 172, verticalReference, 0xFFFFFF);
        drawRect(this.width/2 - 205, verticalReference + 9, this.width/2 + 190, verticalReference + 10, 0xFFFFFFFF); // Underline

        updatePartyMemberList();
        if (partyMembers.size() < 1) { // Refresh once and return as party could have previously been populated
            refreshAndSetButtons();
            return;
        }

        ScreenRenderer.beginGL(0, 0);
        // Draw names
        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size() - pageHeight * page); i < lim; i++) {
            String playerName = partyMembers.get(page * pageHeight + i);
            if (playerName == null) continue;

            int colour;
            if (playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) {
                colour = 0xFFFF00;
            } else if (playerName.equals(McIf.player().getName())) {
                colour = 0x00FFFF;
            } else {
                colour = 0xFFFFFF;
            }

//            MapWaypointIcon wpIcon = new MapWaypointIcon(wp);
//            float centreZ = 64 + 25 * i;
//            float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
//            wpIcon.renderAt(renderer, this.width / 2f - 151, centreZ, multiplier, 1);

            fontRenderer.drawString(playerName, this.width/2 - 165, (verticalReference + 17) + 25 * i, colour);
//            drawCenteredString(fontRenderer, Integer.toString((int) wp.getX()), this.width/2 + 120, 60 + 25 * i, colour);
        }
        refreshAndSetButtons();
        ScreenRenderer.endGL();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b == exitBtn) {
            Utils.displayGuiScreen(null);
        } else if (b.id % 10 == 3) { // Promote
            McIf.player().sendChatMessage("/party promote " + partyMembers.get(b.id / 10 + page * pageHeight));
        } else if (b.id % 10 == 5) { // Kick
            McIf.player().sendChatMessage("/party kick " + partyMembers.get(b.id / 10 + page * pageHeight));
        }
        refreshAndSetButtons();
        onMemberListChange();
    }

    private void refreshAndSetButtons() {
        this.buttonList.removeAll(buttons);
        buttons.clear();
        updatePartyMemberList();
        if (partyMembers == null || partyMembers.isEmpty()) return; // No party
        String playerName = McIf.player().getName();
        // No buttons if we are not owner, members can't kick/promote
        if (!playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) return;

        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size() - pageHeight * page); i < lim; i++) {
            // TODO: uncomment
            //if (partyMembers.get(i).equals(playerName)) continue; // No buttons for self
            buttons.add(new GuiButton(3 + 10 * i, this.width/2 + 95, (verticalReference + 11) + 25 * i, 50, 20, "Promote"));
            buttons.add(new GuiButton(5 + 10 * i, this.width/2 + 155, (verticalReference + 11) + 25 * i, 35, 20, "Kick"));

        }
        this.buttonList.addAll(buttons);
    }

    private void updatePartyMemberList() {
        List<String> temporaryMembers = new ArrayList<>(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers());
        Collections.sort(temporaryMembers);
        partyMembers.clear();

        // We put ourselves at the top, owner as #2
        String playerName = McIf.player().getName();
        String ownerName = PlayerInfo.get(SocialData.class).getPlayerParty().getOwner();
        if (ownerName.equals("")) return; // We are not in a party

        temporaryMembers.remove(playerName);
        temporaryMembers.remove(ownerName);

        partyMembers.add(ownerName);
        if (!ownerName.equals(playerName)) {
            partyMembers.add(playerName);
        }
        partyMembers.addAll(temporaryMembers);
        // List is now sorted in [self, owner, party alphabetically]
    }

    private void onMemberListChange() {
        if (partyMembers.size() > 0 && page * pageHeight > partyMembers.size() - 1) {
            page = (partyMembers.size() - 1) / pageHeight;
            System.out.println("PAGE IS " + page);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }
}
