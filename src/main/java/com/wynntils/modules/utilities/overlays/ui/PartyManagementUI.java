/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.managers.PartyManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
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
    private GuiButton leaveBtn;

    private final List<String> partyMembers = new ArrayList<>();
    private final List<String> offlineMembers = new ArrayList<>();
    private final List<String> suggestedPlayers = new ArrayList<>();
    private Pair<HashSet<String>, String> unsortedPartyMembers = new Pair<>(new HashSet<>(), "");
    private int pageHeight;

    private final NetHandlerPlayClient netHandlerPlayClient = McIf.mc().getConnection();

    private boolean partyLeaveSent = false;

    private enum PartyManagementColors {
        LEADER(0xFFFF00), // Yellow
        MEMBER(0xFFFFFF), // White
        FRIEND(0x00FF00); // Green

        private final int colour;

        PartyManagementColors(int colour) {
            this.colour = colour;
        }

        public int getColor() {
            return colour;
        }
    }

    private static class DispRef {
        public static final int btmRowButtonWidth = 75; // Width of each button on the bottom row
        public static final int buttonGap = 4; // Gap between buttons

        /*
        mainBodyWidth refers to the width of the main section of the GUI, which is where the party members are displayed.
        This includes the four buttons on the bottom row, as well as the invite field, invite label, and invite button.
        (!) This is calculated based on the four buttons on the bottom row (line 101)
        First (refresh): Starts at -166, ends at -86, gap of 4, so -82
        Second (kick): Starts at -82, ends at -2, gap of 4, so +2
        Third (create): Starts at +2, ends at +82, gap of 4, so +86
        Fourth (leave): Starts at +86, ends at +166, no gap because last button

        Also, mainBodyLeftX and mainBodyRightX are relative to (this.width/2), and should be referenced with
        (this.width/2) + mainBodyLeftX and (this.width/2) + mainBodyRightX.
         */
        public static final int mainBodyWidth = btmRowButtonWidth*4 + buttonGap*3;
        public static final int mainBodyLeftX = -mainBodyWidth / 2;
        public static final int mainBodyRightX = mainBodyWidth / 2;
        public static final int verticalReference = 160;

        public static final int inviteButtonWidth = 40; // Width of the invite button
        public static final int buttonHeight = 20; // All buttons use this

        public static final int headOffset = 8; // Used to center the player head under headers
        public static final int memberOffset = 40; // Used to align the member header and names

        public static final int promoteButtonWidth = 50;
        public static final int disbandKickButtonWidth = 47;

        /*
        legendBodyWidth is relative to (this.width/4), and should be referenced with
        (this.width/4) + legendBodyWidth
         */
        public static final int legendBodyWidth = 41;

        /*
        suggestionBodyWidth is relative to (this.width/4 + this.width/2), and should be referenced with
        (this.width/4 + this.width/2) + suggestionBodyWidth
         */
        public static final int suggestionBodyWidth = 180;
    }

    @Override
    public void initGui() {
        super.initGui();
        pageHeight = (this.height - 100) / 25;

        // Invite field row and label
        inviteFieldLabel = new GuiLabel(McIf.mc().fontRenderer, 0, this.width/2 + DispRef.mainBodyLeftX, DispRef.verticalReference - 61, 20, 10, 0xFFFFFF);
        inviteFieldLabel.addLine("Invite players: " + TextFormatting.GRAY + "(Separate with commas)");
        inviteField = new GuiTextField(0, McIf.mc().fontRenderer, this.width/2 + DispRef.mainBodyLeftX, DispRef.verticalReference - 50,
                DispRef.mainBodyWidth - DispRef.inviteButtonWidth - DispRef.buttonGap, DispRef.buttonHeight);
        inviteField.setMaxStringLength(128);
        // Invite button +126 comes from +166 (next row ending) - 40 (button width)
        this.buttonList.add(inviteBtn = new GuiButton(1, this.width/2 + DispRef.mainBodyRightX - DispRef.inviteButtonWidth,
                DispRef.verticalReference - 50, DispRef.inviteButtonWidth, DispRef.buttonHeight, "Invite"));

        // Bottom row buttons
        this.buttonList.add(refreshPartyBtn = new GuiButton(4,
                this.width/2 + DispRef.mainBodyLeftX + 0*(DispRef.buttonGap+DispRef.btmRowButtonWidth), DispRef.verticalReference - 25,
                DispRef.btmRowButtonWidth, DispRef.buttonHeight,
                TextFormatting.GREEN + "Refresh"));
        this.buttonList.add(kickOfflineBtn = new GuiButton(5,
                this.width/2 + DispRef.mainBodyLeftX + 1*(DispRef.buttonGap+DispRef.btmRowButtonWidth), DispRef.verticalReference - 25,
                DispRef.btmRowButtonWidth, DispRef.buttonHeight,
                TextFormatting.RED + "Kick Offline"));
        this.buttonList.add(createBtn = new GuiButton(2,
                this.width/2 + DispRef.mainBodyLeftX + 2*(DispRef.buttonGap+DispRef.btmRowButtonWidth), DispRef.verticalReference - 25,
                DispRef.btmRowButtonWidth, DispRef.buttonHeight,
                TextFormatting.WHITE + "Create Party"));
        this.buttonList.add(leaveBtn = new GuiButton(3,
                this.width/2 + DispRef.mainBodyLeftX + 3*(DispRef.buttonGap+DispRef.btmRowButtonWidth), DispRef.verticalReference - 25,
                DispRef.btmRowButtonWidth, DispRef.buttonHeight,
                TextFormatting.RED + "Leave Party"));

        // Exit button; does not use any of the above references because it is a special case
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        int halfWidth = this.width / 2;
        int quarterWidth = this.width / 4;
        int threeQWidth = quarterWidth * 3;

        // Invite field
        if (inviteField != null) {
            inviteField.drawTextBox();
            inviteFieldLabel.drawLabel(McIf.mc(), mouseX, mouseY);
        }

        setManagementButtonStatuses();

        // Titles for the actual member list
        fontRenderer.drawString(TextFormatting.BOLD + "Head", halfWidth + DispRef.mainBodyLeftX, DispRef.verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Member", halfWidth + DispRef.mainBodyLeftX + DispRef.memberOffset, DispRef.verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Promote", halfWidth + DispRef.mainBodyRightX - 76, DispRef.verticalReference, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Kick", halfWidth + DispRef.mainBodyRightX - 23, DispRef.verticalReference, 0xFFFFFF);
        drawRect(halfWidth + DispRef.mainBodyLeftX, DispRef.verticalReference + 9, halfWidth + DispRef.mainBodyRightX, DispRef.verticalReference + 10, 0xFFFFFFFF); // Underline

        // Draw legend
        fontRenderer.drawString(TextFormatting.BOLD + "Legend", quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference, 0xFFFFFF);
        drawRect(quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference + 9, quarterWidth, DispRef.verticalReference + 10, 0xFFFFFFFF); // Underline
        fontRenderer.drawString(TextFormatting.BOLD + "Self", quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference + 15, PartyManagementColors.MEMBER.getColor());
        fontRenderer.drawString("Leader", quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference + 30, PartyManagementColors.LEADER.getColor());
        fontRenderer.drawString(TextFormatting.STRIKETHROUGH + "Offline", quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference + 45, PartyManagementColors.MEMBER.getColor());
        fontRenderer.drawString("Friend", quarterWidth - DispRef.legendBodyWidth, DispRef.verticalReference + 60, PartyManagementColors.FRIEND.getColor());

        // Draw title for suggestions
        fontRenderer.drawString(TextFormatting.BOLD + "Head", threeQWidth, DispRef.verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Suggestions", threeQWidth + DispRef.memberOffset, DispRef.verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Invite", threeQWidth + DispRef.suggestionBodyWidth - DispRef.inviteButtonWidth, DispRef.verticalReference, 0xFFFFFF);
        drawRect(threeQWidth, DispRef.verticalReference + 9, threeQWidth + DispRef.suggestionBodyWidth, DispRef.verticalReference + 10, 0xFFFFFFFF); // Underline

        updateSuggestionsList();
        // Draw details for people in suggestions
        for (String playerName : suggestedPlayers) {
            fontRenderer.drawString(playerName, threeQWidth + DispRef.memberOffset, (DispRef.verticalReference + 17) + 25 * suggestedPlayers.indexOf(playerName), PartyManagementColors.FRIEND.getColor());

            // Reset colour to white
            // This is so player heads don't have the previous self/owner colour overlaid onto them
            GlStateManager.color(1, 1, 1, 1);

            bindPlayerHeadTexture(playerName);
            drawScaledCustomSizeModalRect(threeQWidth + DispRef.headOffset, (DispRef.verticalReference + 14) + 25 * suggestedPlayers.indexOf(playerName), 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            EntityPlayer entityPlayer = McIf.mc().world.getPlayerEntityByName(playerName);
            if (entityPlayer != null && entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                drawScaledCustomSizeModalRect(threeQWidth + DispRef.headOffset, (DispRef.verticalReference + 14) + 25 * suggestedPlayers.indexOf(playerName), 40.0F, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            }
        }

        updatePartyMemberList();
        if (partyMembers.size() < 1) { // Refresh once and return as party could have previously been populated
            refreshAndSetButtons();
            partyLeaveSent = false;
            return;
        }
        // Draw details for people in party
        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
            String playerName = partyMembers.get(i);
            if (playerName == null) continue;

            String formattedPlayerName = playerName;
            int colour;

            if (formattedPlayerName.equals(McIf.player().getName())) { formattedPlayerName = TextFormatting.BOLD + formattedPlayerName; } // Bold self
            if (offlineMembers.contains(formattedPlayerName)) { formattedPlayerName = TextFormatting.STRIKETHROUGH + formattedPlayerName; }// Strikethrough offline members

            // Priority list is leader, friend, member
            // Colors override from the top down
            if (playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) { // is owner
                colour = PartyManagementColors.LEADER.getColor();
            } else if (PlayerInfo.get(SocialData.class).getFriendList().contains(formattedPlayerName)) {
                colour = PartyManagementColors.FRIEND.getColor();
            } else {
                colour = PartyManagementColors.MEMBER.getColor();
            }

            fontRenderer.drawString(formattedPlayerName, this.width/2 + DispRef.mainBodyLeftX + DispRef.memberOffset, (DispRef.verticalReference + 17) + 25 * i, colour);

            // Reset colour to white
            // This is so player heads don't have the previous friend/owner colour overlaid onto them
            GlStateManager.color(1, 1, 1, 1);

            bindPlayerHeadTexture(playerName);
            // +8 to center head
            drawScaledCustomSizeModalRect(this.width/2 + DispRef.mainBodyLeftX + DispRef.headOffset, (DispRef.verticalReference + 14) + 25 * i, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            EntityPlayer entityPlayer = McIf.mc().world.getPlayerEntityByName(playerName);
            if (entityPlayer != null && entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                drawScaledCustomSizeModalRect(this.width/2 + DispRef.mainBodyLeftX + DispRef.headOffset, (DispRef.verticalReference + 14) + 25 * i, 40.0F, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            }

        }
        refreshAndSetButtons();
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
        } else if (b == leaveBtn && !partyLeaveSent) {
            McIf.player().sendChatMessage("/party leave");
            partyLeaveSent = true;
        } else if (b.id % 10 == 6) { // Suggestion invites
            // Create party if we aren't in one
            if (!PlayerInfo.get(SocialData.class).getPlayerParty().isPartying()) {
                McIf.player().sendChatMessage("/party create");
            }
            McIf.player().sendChatMessage("/party invite " + suggestedPlayers.get(b.id / 10));
        } else if (b.id % 10 == 7) { // Promote
            McIf.player().sendChatMessage("/party promote " + partyMembers.get(b.id / 10));
        } else if (b.id % 10 == 9 && b.displayString.contains("Kick")) { // Kick
            McIf.player().sendChatMessage("/party kick " + partyMembers.get(b.id / 10));
        } else if (b.displayString.contains("Disband"))  {
            McIf.player().sendChatMessage("/party disband");
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
        leaveBtn.enabled = !ownerName.equals("");
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

        // Suggestion invite buttons
        for (int i = 0; suggestedPlayers.size() > i; i++) {
            buttons.add(new GuiButton(i * 10 + 6, this.width/4*3 + DispRef.suggestionBodyWidth - DispRef.inviteButtonWidth, (DispRef.verticalReference + 11) + 24 * i, 36, 20, "Invite"));
        }

        updatePartyMemberList();

        // Leave/Kick and promote buttons
        if (!partyMembers.isEmpty() && McIf.player().getName().equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) {
            for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
                boolean isSelf = partyMembers.get(i).equals(McIf.player().getName());
                if (!isSelf) {
                    buttons.add(new GuiButton(7 + 10 * i, this.width/2 + DispRef.mainBodyRightX - DispRef.buttonGap - DispRef.disbandKickButtonWidth - DispRef.promoteButtonWidth,
                            (DispRef.verticalReference + 11) + 25 * i, DispRef.promoteButtonWidth, DispRef.buttonHeight, "Promote"));
                } // No promote button for self

                String disbandKickText = (isSelf) ? "Disband" : "Kick";
                buttons.add(new GuiButton(9 + 10 * i, this.width/2 + DispRef.mainBodyRightX - DispRef.disbandKickButtonWidth, (DispRef.verticalReference + 11) + 25 * i,
                        DispRef.disbandKickButtonWidth, DispRef.buttonHeight, disbandKickText));
            }
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
        offlineMembers.sort(sortPartyMembers);
        partyMembers.clear();
        partyMembers.addAll(temporaryMembers);
        partyMembers.addAll(offlineMembers);
        // partyMembers list is now sorted in [self, owner, online party alphabetically, offline members alphabetically]
        // Unless owner is offline, in which case it will be [self, online party alphabetically, owner (offline), offline members alphabetically]
    }

    private void updateSuggestionsList() {
        suggestedPlayers.clear();
        // Add friends that are online in the player's world
        for (String player : PlayerInfo.get(SocialData.class).getFriendList()) {
            if (McIf.mc().world.getScoreboard().getTeamNames().contains(player)) {
                // !nearbyPlayers.contains(player) to prevent duplicates
                suggestedPlayers.add(player);
            }
        }
        suggestedPlayers.removeAll(partyMembers); // Remove those already in party from suggestions
        suggestedPlayers.sort(String.CASE_INSENSITIVE_ORDER);
    }

    private void bindPlayerHeadTexture(String rawPlayerName) {
        NetworkPlayerInfo networkPlayerInfo = netHandlerPlayClient.getPlayerInfo(rawPlayerName);
        if (networkPlayerInfo != null) {
            McIf.mc().getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
        } else {
            // If networkPlayerInfo is null, either the player is offline or invisible due to /switch bug
            // Use the default player head texture, it will automatically update when the player is visible again
            McIf.mc().getTextureManager().bindTexture(new ResourceLocation("textures/entity/steve.png"));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }
}
