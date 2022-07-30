/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.managers.PartyManager;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.*;

public class PartyManagementUI extends GuiScreen {

    private GuiButton exitBtn;
    private final List<GuiButton> buttons = new ArrayList<>();
    // Top row
    private GuiLabel inviteFieldLabel;
    private GuiTextField inviteField;
    private GuiButton inviteBtn;
    // Bottom row
    private GuiButton refreshPartyBtn;
    private GuiButton kickOfflineBtn;
    private GuiButton createBtn;
    private GuiButton disbandLeaveBtn;

    private final List<String> partyMembers = new ArrayList<>();
    private final List<String> offlineMembers = new ArrayList<>();
    private Pair<HashSet<String>, String> unsortedPartyMembers = new Pair<>(new HashSet<>(), "");
    private int pageHeight;

    private final int verticalReference = 160;
    private boolean partyLeaveSent = false;

    @Override
    public void initGui() {
        super.initGui();
        inviteFieldLabel = new GuiLabel(McIf.mc().fontRenderer, 0, this.width/2 - 200, verticalReference - 61, 40, 10, 0xFFFFFF);
        inviteFieldLabel.addLine("Invite players: " + TextFormatting.GRAY + "(Separate with commas)");
        inviteField = new GuiTextField(0, McIf.mc().fontRenderer, this.width/2 - 200, verticalReference - 50, 355, 20);
        inviteField.setMaxStringLength(128);
        pageHeight = (this.height - 100) / 25;
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Top row
        this.buttonList.add(inviteBtn = new GuiButton(1, this.width / 2 + 160, verticalReference - 50, 40, 20, "Invite"));
        if (inviteField != null) {
            inviteField.drawTextBox();
            inviteFieldLabel.drawLabel(McIf.mc(), mouseX, mouseY);
        }

        // Bottom row
        this.buttonList.add(refreshPartyBtn = new GuiButton(4, this.width / 2 - 200, verticalReference - 25, 96, 20, TextFormatting.GREEN + "Refresh Party"));
        this.buttonList.add(kickOfflineBtn = new GuiButton(5, this.width / 2 - 100, verticalReference - 25, 96, 20, TextFormatting.RED + "Kick Offline"));
        this.buttonList.add(createBtn = new GuiButton(2, this.width / 2 + 4, verticalReference - 25, 96, 20, "Create Party"));
        String disbandLeaveText = (PlayerInfo.get(SocialData.class).getPlayerParty().getOwner().equals(McIf.player().getName())) ? "Disband Party" : "Leave Party";
        this.buttonList.add(disbandLeaveBtn = new GuiButton(3, this.width / 2 + 104, verticalReference - 25, 96, 20, TextFormatting.RED + disbandLeaveText));

        setManagementButtonStatuses();

        // Titles for the actual member list
        fontRenderer.drawString(TextFormatting.BOLD + "Head", this.width/2 - 200, verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 160, verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Promote", this.width/2 + 125, verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Kick", this.width/2 + 177, verticalReference, 0xFFFFFF);
        drawRect(this.width/2 - 200, verticalReference + 9, this.width/2 + 200, verticalReference + 10, 0xFFFFFFFF); // Underline

        // Draw legend
        fontRenderer.drawString(TextFormatting.BOLD + "Legend", this.width/2 - 400, verticalReference, 0xFFFFFF);
        drawRect(this.width/2 - 400, verticalReference + 9, this.width/2 - 360, verticalReference + 10, 0xFFFFFFFF); // Underline
        fontRenderer.drawString("Self", this.width/2 - 400, verticalReference + 15, 0x00FFFF);
        fontRenderer.drawString("Leader", this.width / 2 - 400, verticalReference + 30, 0xFFFF00);
        fontRenderer.drawString("Member", this.width / 2 - 400, verticalReference + 45, 0xFFFFFF);
        fontRenderer.drawString("Offline", this.width/2 - 400, verticalReference + 60, 0xFF0000);
        fontRenderer.drawString("Leader Offline", this.width/2 - 400, verticalReference + 75, 0xFF8800);


        updatePartyMemberList();
        if (partyMembers.size() < 1) { // Refresh once and return as party could have previously been populated
            refreshAndSetButtons();
            partyLeaveSent = false;
            return;
        }

        ScreenRenderer.beginGL(0, 0);
        NetHandlerPlayClient netHandlerPlayClient = McIf.mc().getConnection();
        // Draw names
        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
            String playerName = partyMembers.get(i);
            if (playerName == null) continue;

            int colour;
            if (playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) { // is owner
                // orange if leader + offline, yellow if leader + online
                colour = offlineMembers.contains(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner()) ? 0xFF8800 : 0xFFFF00;
            } else if (playerName.equals(McIf.player().getName())) { // is yourself
                colour = 0x00FFFF;
            } else if (offlineMembers.contains(playerName)) { // is offline
                colour = 0xFF0000;
            } else { // is online
                colour = 0xFFFFFF;
            }

            fontRenderer.drawString(playerName, this.width/2 - 160, (verticalReference + 17) + 25 * i, colour);
            // Reset colour to white
            // This is so player heads don't have the previous self/owner colour overlaid onto them
            GlStateManager.color(1, 1, 1, 1);

            // Player heads
            NetworkPlayerInfo networkPlayerInfo = netHandlerPlayClient.getPlayerInfo(playerName);
            if (networkPlayerInfo != null) {
                McIf.mc().getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            } else {
                // If networkPlayerInfo is null, either the player is offline or invisible due to /switch bug
                // Use the default player head texture, it will automatically update when the player is visible again
                McIf.mc().getTextureManager().bindTexture(new ResourceLocation("textures/entity/steve.png"));
            }

            drawScaledCustomSizeModalRect(this.width/2 - 192, (verticalReference + 14) + 25 * i, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            EntityPlayer entityPlayer = McIf.mc().world.getPlayerEntityByName(playerName);
            if (entityPlayer != null && entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                drawScaledCustomSizeModalRect(this.width/2 - 192, (verticalReference + 14) + 25 * i, 40.0F, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            }

        }
        refreshAndSetButtons();
        ScreenRenderer.endGL();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        inviteField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN && inviteField.isFocused()) {
            invitePlayersFromTextField();
        }
        super.keyTyped(typedChar, keyCode);
        inviteField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b == exitBtn) {
            Utils.displayGuiScreen(null);
        } else if (b == inviteBtn) {
            invitePlayersFromTextField();
        } else if (b == refreshPartyBtn) {
            PartyManager.handlePartyList();
        } else if (b == kickOfflineBtn) {
            // Refresh before kicking offline players
            PartyManager.handlePartyList();
            updatePartyMemberList();
            for (String playerName : offlineMembers) {
                McIf.player().sendChatMessage("/party kick " + playerName);
            }
            // And refresh again after to update the visible list
            PartyManager.handlePartyList();
            updatePartyMemberList();
        } else if (b == createBtn) {
            McIf.player().sendChatMessage("/party create");
        } else if (b == disbandLeaveBtn && b.displayString.contains("Disband")) {
            McIf.player().sendChatMessage("/party disband");
        } else if (b.id % 10 == 7) { // Promote
            McIf.player().sendChatMessage("/party promote " + partyMembers.get(b.id / 10));
        } else if (b.id % 10 == 9 && b.displayString.contains("Kick")) { // Kick
            McIf.player().sendChatMessage("/party kick " + partyMembers.get(b.id / 10));
        } else if (b.displayString.contains("Leave") && !partyLeaveSent) { // For both dynamic leave/kick and disband/leave btns
            McIf.player().sendChatMessage("/party leave");
            partyLeaveSent = true;
        }
        refreshAndSetButtons();
    }

    private void setManagementButtonStatuses() {
        String ownerName = PlayerInfo.get(SocialData.class).getPlayerParty().getOwner();
        String playerName = McIf.player().getName();
        if (ownerName == null) return;
        // text field >= 2chars && (must be owner || party doesn't exist)
        inviteBtn.enabled = inviteField.getText().length() >= 2 && (ownerName.equals(playerName) || ownerName.equals(""));
        // not already in party
        createBtn.enabled = ownerName.equals("");
        // in party
        disbandLeaveBtn.enabled = !ownerName.equals("");
        // owner of party
        kickOfflineBtn.enabled = ownerName.equals(playerName) && offlineMembers.size() > 0;
    }

    private void invitePlayersFromTextField() {
        // Remove anything that isn't possible in a minecraft name, except commas and spaces
        String fieldText = inviteField.getText().replaceAll("[^\\w, ]+", "");
        fieldText = fieldText.replaceAll("[, ]+", ","); // commas and spaces to just comma
        if (fieldText.equals("")) return;

        // Create party if we aren't in one
        if (!PlayerInfo.get(SocialData.class).getPlayerParty().isPartying()) {
            McIf.player().sendChatMessage("/party create");
        }

        // HashSet to remove duplicates
        HashSet<String> players = new HashSet<>(Arrays.asList(fieldText.split(",")));
        for (String player : players) {
            // ignore those already in party
            if (PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().contains(player)) continue;
            McIf.player().sendChatMessage("/party invite " + player);
        }
    }

    private void refreshAndSetButtons() {
        this.buttonList.removeAll(buttons);
        buttons.clear();
        updatePartyMemberList();
        if (partyMembers.isEmpty()) return; // No party
        String playerName = McIf.player().getName();
        // No buttons if we are not owner, members can't kick/promote
        if (!playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) return;

        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
            boolean isSelf = partyMembers.get(i).equals(playerName);
            if (!isSelf) {
                buttons.add(new GuiButton(7 + 10 * i, this.width/2 + 100, (verticalReference + 11) + 25 * i, 50, 20, "Promote"));
            } // No promote button for self

            String kickLeaveText = (isSelf) ? "Leave" : "Kick";
            buttons.add(new GuiButton(9 + 10 * i, this.width/2 + 158, (verticalReference + 11) + 25 * i, 38, 20, kickLeaveText));
        }
        this.buttonList.addAll(buttons);
    }

    private void updatePartyMemberList() {
        offlineMembers.clear();
        String ownerName = PlayerInfo.get(SocialData.class).getPlayerParty().getOwner();
        List<String> temporaryMembers = new ArrayList<>(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers());

        // Remove offline members, we can deal with those later
        for (String member : temporaryMembers) {
            if (!McIf.mc().world.getScoreboard().getTeamNames().contains(member)) {
                offlineMembers.add(member);
            }
        }

        Pair<HashSet<String>, String> unsorted = new Pair<>(new HashSet<>(temporaryMembers), ownerName);
        if (unsorted.equals(unsortedPartyMembers)) return; // No changes, do not continue
        unsortedPartyMembers = unsorted;

        temporaryMembers.removeAll(offlineMembers);
        // temporaryMembers is now unsorted list of online members

        String playerName = McIf.player().getName();
        Comparator<String> sortPartyMembers = (String s1, String s2) -> {
            if (s1.equals(playerName)) return -1;
            if (s2.equals(playerName)) return 1;

            if (s1.equals(ownerName)) return -1;
            if (s2.equals(ownerName)) return 1;

            return s1.compareTo(s2);
        };
        temporaryMembers.sort(sortPartyMembers);
        partyMembers.clear();
        partyMembers.addAll(temporaryMembers);
        partyMembers.addAll(offlineMembers);
        // partyMembers list is now sorted in [self, owner, online party alphabetically, offline members]
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }
}
