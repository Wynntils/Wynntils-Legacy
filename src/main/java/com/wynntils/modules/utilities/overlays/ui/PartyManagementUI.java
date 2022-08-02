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
import net.minecraft.util.math.AxisAlignedBB;
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
    private final List<String> nearbyPlayers = new ArrayList<>();
    private final List<String> suggestedPlayers = new ArrayList<>();
    private Pair<HashSet<String>, String> unsortedPartyMembers = new Pair<>(new HashSet<>(), "");
    private int pageHeight;

    private final int nearbyRadius = 30;
    private final int verticalReference = 160;
    private boolean partyLeaveSent = false;

    private enum PartyManagementColors {
        LEADER(0xFFFF00), // Yellow
        MEMBER(0xFFFFFF), // White
        FRIEND(0x00FF00), // Green
        NEARBY(0x00FFFF); // Cyan

        private final int colour;

        PartyManagementColors(int colour) {
            this.colour = colour;
        }

        public int getColor() {
            return colour;
        }
    }

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

        // Top row buttons
        this.buttonList.add(inviteBtn = new GuiButton(1, this.width / 2 + 160, verticalReference - 50, 40, 20, "Invite"));
        if (inviteField != null) {
            inviteField.drawTextBox();
            inviteFieldLabel.drawLabel(McIf.mc(), mouseX, mouseY);
        }

        // Bottom row buttons
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
        drawRect(this.width/2 - 400, verticalReference + 9, this.width/2 - 359, verticalReference + 10, 0xFFFFFFFF); // Underline
        fontRenderer.drawString(TextFormatting.BOLD + "Self", this.width/2 - 400, verticalReference + 15, PartyManagementColors.MEMBER.getColor());
        fontRenderer.drawString("Leader", this.width / 2 - 400, verticalReference + 30, PartyManagementColors.LEADER.getColor());
        fontRenderer.drawString(TextFormatting.STRIKETHROUGH + "Offline", this.width/2 - 400, verticalReference + 45, PartyManagementColors.MEMBER.getColor());
        fontRenderer.drawString("Friend", this.width/2 - 400, verticalReference + 60, PartyManagementColors.FRIEND.getColor());
        fontRenderer.drawString("Nearby", this.width/2 - 400, verticalReference + 75, PartyManagementColors.NEARBY.getColor());

        // Draw title for suggestions
        fontRenderer.drawString(TextFormatting.BOLD + "Head", this.width/2 + 280, verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Suggestions", this.width/2 + 320, verticalReference, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Invite", this.width/2 + 470, verticalReference, 0xFFFFFF);
        drawRect(this.width/2 + 280, verticalReference + 9, this.width/2 + 504, verticalReference + 10, 0xFFFFFFFF); // Underline

        updateNearbyPlayers();
        updateSuggestionsList();
        NetHandlerPlayClient netHandlerPlayClient = McIf.mc().getConnection();
        // Draw details for people in suggestions
        for (String playerName : suggestedPlayers) {
            int colour = PartyManagementColors.MEMBER.getColor(); // White - shouldn't happen, but just in case neither criteria is met
            if (PlayerInfo.get(SocialData.class).getFriendList().contains(playerName)) {
                colour = PartyManagementColors.FRIEND.getColor();
            } else if (nearbyPlayers.contains(playerName)) {
                colour = PartyManagementColors.NEARBY.getColor();
            }

            fontRenderer.drawString(playerName, this.width/2 + 320, (verticalReference + 17) + 25 * suggestedPlayers.indexOf(playerName), colour);

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

            drawScaledCustomSizeModalRect(this.width/2 + 288, (verticalReference + 14) + 25 * suggestedPlayers.indexOf(playerName), 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            EntityPlayer entityPlayer = McIf.mc().world.getPlayerEntityByName(playerName);
            if (entityPlayer != null && entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                drawScaledCustomSizeModalRect(this.width/2 + 288, (verticalReference + 14) + 25 * suggestedPlayers.indexOf(playerName), 40.0F, 8, 8, 8, 12, 12, 64.0F, 64.0F);
            }
        }

        updatePartyMemberList();
        if (partyMembers.size() < 1) { // Refresh once and return as party could have previously been populated
            refreshAndSetButtons();
            partyLeaveSent = false;
            return;
        }

        ScreenRenderer.beginGL(0, 0);
        // Draw details for people in party
        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
            String playerName = partyMembers.get(i);
            if (playerName == null) continue;

            String rawPlayerName = playerName;
            int colour;

            if (playerName.equals(McIf.player().getName())) { // Self
                playerName = TextFormatting.BOLD + playerName;
            }
            if (offlineMembers.contains(playerName)) { // Offline
                playerName = TextFormatting.STRIKETHROUGH + playerName;
            }


            // Priority list is leader, friend, nearby, member
            // Colors override from the top down
            if (playerName.equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) { // is owner
                colour = PartyManagementColors.LEADER.getColor();
            } else if (PlayerInfo.get(SocialData.class).getFriendList().contains(playerName)) {
                colour = PartyManagementColors.FRIEND.getColor();
            } else if (nearbyPlayers.contains(playerName)) {
                colour = PartyManagementColors.NEARBY.getColor();
            } else {
                colour = PartyManagementColors.MEMBER.getColor();
            }

            fontRenderer.drawString(playerName, this.width/2 - 160, (verticalReference + 17) + 25 * i, colour);

            // Reset colour to white
            // This is so player heads don't have the previous self/owner colour overlaid onto them
            GlStateManager.color(1, 1, 1, 1);

            // Player heads
            NetworkPlayerInfo networkPlayerInfo = netHandlerPlayClient.getPlayerInfo(rawPlayerName);
            if (networkPlayerInfo != null) {
                McIf.mc().getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            } else {
                // If networkPlayerInfo is null, either the player is offline or invisible due to /switch bug
                // Use the default player head texture, it will automatically update when the player is visible again
                McIf.mc().getTextureManager().bindTexture(new ResourceLocation("textures/entity/steve.png"));
            }

            drawScaledCustomSizeModalRect(this.width/2 - 192, (verticalReference + 14) + 25 * i, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            EntityPlayer entityPlayer = McIf.mc().world.getPlayerEntityByName(rawPlayerName);
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

        // Suggestion invite buttons
        for (int i = 0; suggestedPlayers.size() > i; i++) {
            buttons.add(new GuiButton(i * 10 + 6, this.width/2 + 469, (verticalReference + 14) + 24 * i, 36, 20, "Invite"));
        }

        updatePartyMemberList();

        // Leave/Kick and promote buttons
        if (!partyMembers.isEmpty() && McIf.player().getName().equals(PlayerInfo.get(SocialData.class).getPlayerParty().getOwner())) {
            for (int i = 0, lim = Math.min(pageHeight, partyMembers.size()); i < lim; i++) {
                boolean isSelf = partyMembers.get(i).equals(McIf.player().getName());
                if (!isSelf) {
                    buttons.add(new GuiButton(7 + 10 * i, this.width / 2 + 100, (verticalReference + 11) + 25 * i, 50, 20, "Promote"));
                } // No promote button for self

                String kickLeaveText = (isSelf) ? "Leave" : "Kick";
                buttons.add(new GuiButton(9 + 10 * i, this.width / 2 + 158, (verticalReference + 11) + 25 * i, 38, 20, kickLeaveText));
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

    private void updateNearbyPlayers() {
        nearbyPlayers.clear();
        for (EntityPlayer entity : McIf.mc().world.getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB(McIf.mc().player.posX - nearbyRadius,
                        McIf.mc().player.posY - nearbyRadius,
                        McIf.mc().player.posZ - nearbyRadius,
                        McIf.mc().player.posX + nearbyRadius,
                        McIf.mc().player.posY + nearbyRadius,
                        McIf.mc().player.posZ + nearbyRadius))) {
            if (entity != null // Null check
                    && !entity.getName().startsWith("§") // Ignore fake players
                    && !entity.getName().equals(McIf.player().getName()) // Ignore self
                    && McIf.mc().world.getScoreboard().getTeamNames().contains(entity.getName())) { // Only players in this world
                nearbyPlayers.add(entity.getName());
            }
        }
    }

    private void updateSuggestionsList() {
        suggestedPlayers.clear();
        suggestedPlayers.addAll(nearbyPlayers);
        // Add friends that are online in the player's world
        for (String player : PlayerInfo.get(SocialData.class).getFriendList()) {
            if (McIf.mc().world.getScoreboard().getTeamNames().contains(player) && !nearbyPlayers.contains(player)) {
                // !nearbyPlayers.contains(player) to prevent duplicates
                suggestedPlayers.add(player);
            }
        }
        suggestedPlayers.removeAll(partyMembers); // Remove those already in party from suggestions
        suggestedPlayers.sort(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }
}
