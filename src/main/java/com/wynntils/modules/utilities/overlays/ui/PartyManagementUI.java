/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyManagementUI extends GuiScreen {

    private GuiButton exitBtn;
    private List<GuiButton> buttons = new ArrayList<>();

    private List<WaypointProfile> waypoints;
    private List<String> partyMembers = new ArrayList<>();
    private int page;
    private int pageHeight;

    @Override
    public void initGui() {
        super.initGui();
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        pageHeight = (this.height - 100) / 25;
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));

        onMemberListChange();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width/2 - 205, 43, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 165, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Promote", this.width/2 + 120, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Kick", this.width/2 + 172, 43, 0xFFFFFF);
        drawRect(this.width/2 - 205, 52, this.width/2 + 190, 53, 0xFFFFFFFF); // Underline

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
            int colour = (playerName.equals(McIf.player().getName())) ? 0x00FFFF : 0xFFFFFF;

//            MapWaypointIcon wpIcon = new MapWaypointIcon(wp);
//            float centreZ = 64 + 25 * i;
//            float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
//            wpIcon.renderAt(renderer, this.width / 2f - 151, centreZ, multiplier, 1);

            fontRenderer.drawString(playerName, this.width/2 - 165, 60 + 25 * i, colour);
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
        // TODO: remove debugs
        System.out.println(b.id / 10 + page * pageHeight);
        if (b.id % 10 == 3) { // Promote
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
            buttons.add(new GuiButton(3 + 10 * i, this.width/2 + 95, 54 + 25 * i, 50, 20, "Promote"));
            buttons.add(new GuiButton(5 + 10 * i, this.width/2 + 155, 54 + 25 * i, 35, 20, "Kick"));

        }
        this.buttonList.addAll(buttons);
    }

    private void updatePartyMemberList() {
        partyMembers = new ArrayList<>(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers());
        Collections.sort(partyMembers);

        // We put ourselves at the top, owner as #2
        String playerName = McIf.player().getName();
        String ownerName = PlayerInfo.get(SocialData.class).getPlayerParty().getOwner();

        for (String member : partyMembers) {
            if (member.equals(ownerName) && partyMembers.size() > 1 && !partyMembers.get(1).equals(ownerName)) {
                partyMembers.remove(ownerName);
                partyMembers.add(1, ownerName);
            }
            if (member.equals(playerName) && !partyMembers.get(0).equals(playerName)) {
                // Set ourselves after so it's higher priority, we will always be first even if owner
                partyMembers.remove(playerName);
                partyMembers.add(0, playerName);
            }
        }
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
