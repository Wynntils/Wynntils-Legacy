package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.WynnExpansion;
import com.wynndevs.modules.expansion.misc.GuiScreenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlayerListMenu extends GuiScreenMod {
	private static final ResourceLocation TEXTURE_FRIENDS = new ResourceLocation(Reference.MOD_ID, "textures/gui/menu.png");
	
	private static List<GuiButton> PlayerButtonList = new ArrayList<GuiButton>();
	
	
	private static PlayerHomeMenu.KickButton KickButton = new PlayerHomeMenu.KickButton();
	private static PlayerHomeMenu.MsgButton MsgButton = new PlayerHomeMenu.MsgButton();
	private static PlayerHomeMenu.PartyButton PartyButton = new PlayerHomeMenu.PartyButton();
	private static PlayerHomeMenu.JoinButton JoinButton = new PlayerHomeMenu.JoinButton();
	private static PlayerHomeMenu.FavouriteButton FavouriteButton = new PlayerHomeMenu.FavouriteButton();
	private static PlayerHomeMenu.FriendButton FriendButton = new PlayerHomeMenu.FriendButton();
	private static PlayerHomeMenu.PlayerInfoButton PlayerInfoButton = new PlayerHomeMenu.PlayerInfoButton();
	
	private static PlayerHomeMenu.RankRecruitButton RankRecruitButton = new PlayerHomeMenu.RankRecruitButton();
	private static PlayerHomeMenu.RankRecruiterButton RankRecruiterButton = new PlayerHomeMenu.RankRecruiterButton();
	private static PlayerHomeMenu.RankCaptainButton RankCaptainButton = new PlayerHomeMenu.RankCaptainButton();
	private static PlayerHomeMenu.RankChiefButton RankChiefButton = new PlayerHomeMenu.RankChiefButton();
	private static PlayerHomeMenu.RankChiefButton RankLeadingChiefButton = new PlayerHomeMenu.RankChiefButton();
	
	private static PlayerHomeMenu.ConfirmButton ConfirmButton = new PlayerHomeMenu.ConfirmButton();
	private static PlayerHomeMenu.CancelButton CancelButton = new PlayerHomeMenu.CancelButton();
	
	
	private static boolean Loaded = true;
	static int Page = 1;
	private static int MaxPages = 0;
	private static int MaxRows = 7;
	private static int Selected = 0;
	private static int Confirm = 0;
	private static boolean GuildRankChange = false;
	private static boolean ConfirmScreen = false;
	
	static List<String[]> PlayerList = new ArrayList<String[]>();
	static int ListType = 0;
	
	
	@Override
	protected String GetButtonTooltip(int buttonId) {
		switch (buttonId) {
		case 0:
			return "Back to selection";
		case 1:
			return "Next Page";
		case 2:
			return "Previous Page";
			
		case 99: return String.valueOf('\u00a7') + "cRemove " + PlayerList.get(((Page-1)*14) + (Selected-1))[1];
		
		case 98: return "Message " + PlayerList.get(((Page-1)*14) + (Selected-1))[1];
		
		case 97: return String.valueOf('\u00a7') + "eInvite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Party";
		
		case 96: return String.valueOf('\u00a7') + "aJoin " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " on " + PlayerList.get(((Page-1)*14) + (Selected-1))[0];
		
		case 95: return (FavouriteButton.Favourite ? String.valueOf('\u00a7') + "eRemove " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " from Favourites" : String.valueOf('\u00a7') + "6Add " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Favourites");
		
		case 94: return String.valueOf('\u00a7') + "aAdd " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to your Friends list";
		
		case 93: return String.valueOf('\u00a7') + "fView info on " + PlayerList.get(((Page-1)*14) + (Selected-1))[1];
		
		case 92: return String.valueOf('\u00a7') + "bInvite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to your Guild";
		
		case 70: return String.valueOf('\u00a7') + "bRecruit, Change rank?";
		case 71: return String.valueOf('\u00a7') + "bRecruiter, Change rank?";
		case 72: return String.valueOf('\u00a7') + "bCaptain, Change rank?";
		case 73: return String.valueOf('\u00a7') + "bChief, Change rank?";
		
		case 75: return String.valueOf('\u00a7') + "bSet to Recruit";
		case 76: return String.valueOf('\u00a7') + "bSet to Recruiter";
		case 77: return String.valueOf('\u00a7') + "bSet to Captain";
		case 78: return String.valueOf('\u00a7') + "bSet to Chief";
		case 79: return String.valueOf('\u00a7') + "bCancel";
		
		case 80: return String.valueOf('\u00a7') + "bRecruit";
		case 81: return String.valueOf('\u00a7') + "bRecruiter";
		case 82: return String.valueOf('\u00a7') + "bCaptain";
		case 83: return String.valueOf('\u00a7') + "bChief";
		case 84: return String.valueOf('\u00a7') + "bGuild Master";
		
		}
		return null;
	}
	
	@Override
	public void initGui() {
		Selected = 0;
		Confirm = 0;
		ConfirmScreen = false;
		Loaded = true;
		if (PlayerHomeMenu.PlayersLoaded){
			PlayerList.clear();
			switch (ListType) {
				case 1: PlayerList.addAll(PlayerHomeMenu.PartyList); break;
				case 2: PlayerList.addAll(PlayerHomeMenu.FriendsList); break;
				case 3: PlayerList.addAll(PlayerHomeMenu.GuildList); break;
				case 4: PlayerList.addAll(PlayerHomeMenu.WorldList); break;
			}
		}
		MaxPages = Math.round((PlayerList.size()-1)/(MaxRows*2)) +1;
		if (MaxPages == 0) MaxPages = 1;
		
		this.addButton(new PlayerHomeMenu.ChangePageButton(1, (this.width / 2) + 25, 50, true));
		this.addButton(new PlayerHomeMenu.ChangePageButton(2, (this.width / 2) - 52, 50, false));
		
		this.addButton(new PlayerHomeMenu.BackButton(0, (this.width / 2) + 55, 50));
		this.addButton(new PlayerHomeMenu.ExitButton(-1, (this.width / 2) + 100, 15));
		
		PlayerButtonList.clear();
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(101, (this.width / 2) - 120, 80, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(102, (this.width / 2) - 120, 95, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(103, (this.width / 2) - 120, 110, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(104, (this.width / 2) - 120, 125, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(105, (this.width / 2) - 120, 140, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(106, (this.width / 2) - 120, 155, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(107, (this.width / 2) - 120, 170, ""));
		
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(108, (this.width / 2) - 2, 80, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(109, (this.width / 2) - 2, 95, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(110, (this.width / 2) - 2, 110, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(111, (this.width / 2) - 2, 125, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(112, (this.width / 2) - 2, 140, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(113, (this.width / 2) - 2, 155, ""));
		PlayerButtonList.add(new PlayerHomeMenu.PlayerButton(114, (this.width / 2) - 2, 170, ""));
		
		
		this.addButton(KickButton = new PlayerHomeMenu.KickButton(99, (this.width / 2), 80));
		this.addButton(MsgButton = new PlayerHomeMenu.MsgButton(98, (this.width / 2), 80));
		this.addButton(PartyButton = new PlayerHomeMenu.PartyButton(97, (this.width / 2), 80));
		this.addButton(JoinButton = new PlayerHomeMenu.JoinButton(96, (this.width / 2), 80));
		this.addButton(FavouriteButton = new PlayerHomeMenu.FavouriteButton(95, (this.width / 2), 80));
		this.addButton(FriendButton = new PlayerHomeMenu.FriendButton(94, (this.width / 2), 80));
		this.addButton(PlayerInfoButton = new PlayerHomeMenu.PlayerInfoButton(93, (this.width / 2), 80));
		
		this.addButton(RankRecruitButton = new PlayerHomeMenu.RankRecruitButton(80, (this.width / 2), 80));
		this.addButton(RankRecruiterButton = new PlayerHomeMenu.RankRecruiterButton(81, (this.width / 2), 80));
		this.addButton(RankCaptainButton = new PlayerHomeMenu.RankCaptainButton(82, (this.width / 2), 80));
		this.addButton(RankChiefButton = new PlayerHomeMenu.RankChiefButton(83, (this.width / 2), 80));
		this.addButton(RankLeadingChiefButton = new PlayerHomeMenu.RankChiefButton(84, (this.width / 2), 80));
		
		this.addButton(ConfirmButton = new PlayerHomeMenu.ConfirmButton(50, (this.width / 2) + 50, 160));
		this.addButton(CancelButton = new PlayerHomeMenu.CancelButton(49, (this.width / 2) - 69, 160));
		
		
		for (GuiButton btn : PlayerButtonList) {
			this.addButton(btn);
		}
		
		RedrawButtons();
	}
	
	@Override
	public void updateScreen() {
		/*if (PlayerHomeMenu.PlayersLoaded && PlayerHomeMenu.RefreshTimer.Passed()){
			PlayerHomeMenu.RefreshTimer.Reset();
			RedrawButtons();
		}*/
		if (Confirm == -1) {
			for (int i=0;i<14;i++){
				PlayerButtonList.get(i).enabled = true;
			}
			Confirm = 0;
		}else if (ConfirmScreen){
			ConfirmButton.enabled = true;
			CancelButton.enabled = true;
			ConfirmScreen = false;
		}else if (GuildRankChange && !KickButton.enabled){
			KickButton.enabled = true;
			RankRecruitButton.enabled = true;
			RankRecruiterButton.enabled = true;
			RankCaptainButton.enabled = true;
			RankChiefButton.enabled = true;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(TEXTURE_FRIENDS);
		
		this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
		
		this.drawCenteredStringPlain(mc.fontRenderer, Page + "/" + MaxPages, this.width / 2, 53, Integer.parseInt("858585", 16));
		
		if (!PlayerHomeMenu.PlayersLoaded){
			switch (ListType){
			case 1: this.drawCenteredStringPlain(mc.fontRenderer,"Going to Party...", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 2: this.drawCenteredStringPlain(mc.fontRenderer,"Finding Friends...", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 3: this.drawCenteredStringPlain(mc.fontRenderer,"Discovering Guild...", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 4: this.drawCenteredStringPlain(mc.fontRenderer,"Searching World...", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			}
			Loaded = false;
		}else if (!Loaded){
			Loaded = true;
			mc.displayGuiScreen(new PlayerListMenu());
		}else if (Loaded && PlayerList.isEmpty()){
			switch (ListType){
			case 1: this.drawCenteredStringPlain(mc.fontRenderer,"You found no-where to party", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 2: this.drawCenteredStringPlain(mc.fontRenderer,"Time to start making friends!", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 3: this.drawCenteredStringPlain(mc.fontRenderer,"Guilds beware " + ModCore.mc().player.getName() + " is here!", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			case 4: this.drawCenteredStringPlain(mc.fontRenderer,"The world is full of... no-one", this.width / 2, 125, Integer.parseInt("858585", 16)); break;
			}
		}
		
		if (Confirm > 0){
			this.drawCenteredStringPlain(mc.fontRenderer, "You are about to:", this.width / 2, 80, Integer.parseInt("858585", 16));
			switch (Confirm){
				case 1: 
					switch (ListType){
					case 1: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cRemove " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " from the Party", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
					case 2: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cRemove " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " from Friends", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
					case 3: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cKick " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " from the Guild", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
					}
					break;
				
				case 2: ConfirmSelection(); RedrawButtons(); break;
					
				case 3: this.drawCenteredStringPlain(mc.fontRenderer, "Invite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to your party", this.width / 2, 105, Integer.parseInt("d9e512", 16)); break;
				
				case 4: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "aJoin " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " on world " + PlayerList.get(((Page-1)*14) + (Selected-1))[0], this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				
				case 5: ConfirmSelection(); RedrawButtons(); break;
				
				case 6: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "aAdd " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to your Friends list", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 7: ConfirmSelection(); RedrawButtons(); break;
				case 8: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Invite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to your guild", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				
				
				case 25: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Recruit", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 26: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Recruiter", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 27: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Captain", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 28: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " to Chief", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
			}
			
			this.drawCenteredStringPlain(mc.fontRenderer,"Are you sure?", this.width / 2, 130, Integer.parseInt("858585", 16));
			this.drawCenteredStringPlain(mc.fontRenderer,"Continue", (this.width / 2) +59, 145, Integer.parseInt("55FF55", 16));
			this.drawCenteredStringPlain(mc.fontRenderer,"Cancel", (this.width / 2) -60, 145, Integer.parseInt("FF5555", 16));
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void RedrawButtons() {
		if (Confirm > 0) {
			for (int i=0;i<14;i++){
				PlayerButtonList.get(i).visible = false;
				((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = "";
				PlayerButtonList.get(i).enabled = false;
			}
			KickButton.visible = false;
			MsgButton.visible = false;
			PartyButton.visible = false;
			JoinButton.visible = false;
			FavouriteButton.visible = false;
			FriendButton.visible = false;
			PlayerInfoButton.visible = false;
			
			RankRecruitButton.visible = false;
			RankRecruiterButton.visible = false;
			RankCaptainButton.visible = false;
			RankChiefButton.visible = false;
			RankLeadingChiefButton.visible = false;
			
			ConfirmButton.visible = true;
			CancelButton.visible = true;
			
			ConfirmButton.enabled = false;
			CancelButton.enabled = false;
			ConfirmScreen = true;
		}else{
			
			boolean VisibleKickButton = false;
			boolean VisibleMsgButton = false;
			boolean VisiblePartyButton = false;
			boolean VisibleJoinButton = false;
			boolean VisibleFavouriteButton = false;
			boolean VisibleFriendButton = false;
			boolean VisiblePlayerInfoButton = false;
			
			boolean VisibleRankRecruitButton = false;
			boolean VisibleRankRecruiterButton = false;
			boolean VisibleRankCaptainButton = false;
			boolean VisibleRankChiefButton = false;
			boolean VisibleRankLeadingChiefButton = false;
			
			ConfirmButton.visible = false;
			CancelButton.visible = false;
			
			for (int i=0;i<14;i++) {
				if (((Page-1)*14) +i >= PlayerList.size() || !PlayerHomeMenu.PlayersLoaded){
					PlayerButtonList.get(i).visible = false;
					((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = "";
					if (i < MaxRows){
						PlayerButtonList.get(i).x = (this.width / 2) -120;
						PlayerButtonList.get(i).y = (i * 15) +80;
					}else{
						PlayerButtonList.get(i).x = (this.width / 2) -2;
						PlayerButtonList.get(i).y = ((i - MaxRows) * 15) +80;
					}
				}else{
					if (Selected == i+1){
						if (GuildRankChange){
							VisibleKickButton = true; KickButton.id = 79; KickButton.enabled = false; // Show Cancel Button
							if (PlayerHomeMenu.GuildRank >= 3 && Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]) < PlayerHomeMenu.GuildRank && !PlayerList.get(((Page-1)*14) +i)[2].equals("1")) {VisibleRankRecruitButton = true; RankRecruitButton.id = 75;  RankRecruitButton.enabled = false;}
							if (PlayerHomeMenu.GuildRank >= 3 && Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]) < PlayerHomeMenu.GuildRank && !PlayerList.get(((Page-1)*14) +i)[2].equals("2")) {VisibleRankRecruiterButton = true; RankRecruiterButton.id = 76; RankRecruiterButton.enabled = false;}
							if (PlayerHomeMenu.GuildRank >= 4 && Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]) < PlayerHomeMenu.GuildRank && !PlayerList.get(((Page-1)*14) +i)[2].equals("3")) {VisibleRankCaptainButton = true; RankCaptainButton.id = 77; RankCaptainButton.enabled = false;}
							if (PlayerHomeMenu.GuildRank >= 5 && Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]) < PlayerHomeMenu.GuildRank && !PlayerList.get(((Page-1)*14) +i)[2].equals("4")) {VisibleRankChiefButton = true; RankChiefButton.id = 78; RankChiefButton.enabled = false;}
						}else{
							PlayerButtonList.get(i).visible = false;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = "";
							if (!PlayerList.get(((Page-1)*14) +i)[1].equals(ModCore.mc().player.getName())){
								if (ListType == 2 || ((ListType == 3 && PlayerHomeMenu.GuildRank == 4 && Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]) < 4) || (ListType == 3 && PlayerHomeMenu.GuildRank == 5)) || (ListType == 1 && PlayerHomeMenu.PartyLeader)) {VisibleKickButton = true; KickButton.id = 99;}
								if (!PlayerList.get(((Page-1)*14) +i)[0].equals("0")){
									VisibleMsgButton = true;
									if (PlayerHomeMenu.CurrentWorld.equals(PlayerList.get(((Page-1)*14) +i)[0])){
										if (PlayerHomeMenu.PartyList.isEmpty() || (PlayerHomeMenu.PartyLeader && !PlayerGlow.PartyList.contains(PlayerList.get(((Page-1)*14) +i)[1]))) VisiblePartyButton = true;
									}else{
	// TO BE IMPLIMENTED WHEN THERE IS A GOOD WAY TO CHANGE FOM SERVER "A" TO SERVER "B"									VisibleJoinButton = true;
									}
								}
								if (ListType == 2){
									if (PlayerList.get(((Page-1)*14) +i)[2].equals("true")){
										VisibleFavouriteButton = true;
										FavouriteButton.Favourite = true;
									}else{
										VisibleFavouriteButton = true;
										FavouriteButton.Favourite = false;
									}
								}else{
									VisibleFriendButton = true;
									for (String[] Friend : PlayerHomeMenu.FriendsList){
										if (Friend[1].equals((PlayerList.get(((Page-1)*14) +i)[1]))){
											VisibleFriendButton = false;
											break;
										}
									}
								}
								if (ListType != 3 && PlayerHomeMenu.GuildRank >= 2 && !PlayerGlow.GuildList.contains((PlayerList.get(((Page-1)*14) +i)[1]))){
									VisibleRankRecruiterButton = true;
									RankRecruiterButton.id = 92;
								}
							}
							VisiblePlayerInfoButton = true;
							if (ListType == 3){
								switch (Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2])){
									case 1: VisibleRankRecruitButton = true; if (PlayerHomeMenu.GuildRank >= 3) {RankRecruitButton.id = 70;}else{RankRecruitButton.id = 80;} break;
									case 2: VisibleRankRecruiterButton = true; if (PlayerHomeMenu.GuildRank >= 3) {RankRecruiterButton.id = 71;}else{RankRecruiterButton.id = 81;} break;
									case 3: VisibleRankCaptainButton = true; if (PlayerHomeMenu.GuildRank >= 4) {RankCaptainButton.id = 72;}else{RankCaptainButton.id = 82;} break;
									case 4: VisibleRankChiefButton = true; if (PlayerHomeMenu.GuildRank >= 5) {RankChiefButton.id = 73;}else{RankChiefButton.id = 83;} break;
									case 5: VisibleRankLeadingChiefButton = true;
								}
							}
						}
					}else{
						PlayerButtonList.get(i).visible = true;
						if (PlayerList.get(((Page-1)*14) +i)[0].equals("0")){
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = PlayerList.get(((Page-1)*14) +i)[1];
						}else if(PlayerHomeMenu.CurrentWorld.equals(PlayerList.get(((Page-1)*14) +i)[0])){
							switch (ListType){
							case 1: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + (PlayerList.get(((Page-1)*14) +i)[2].equals("true") ? "c " : "e ") + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 2: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + '2' + PlayerList.get(((Page-1)*14) +i)[0] + String.valueOf('\u00a7') + "a " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 3: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + '3' + PlayerList.get(((Page-1)*14) +i)[0] + String.valueOf('\u00a7') + "b " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 4: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + "f " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							}
						}else{
							switch (ListType){
							case 1: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + 'e' + PlayerList.get(((Page-1)*14) +i)[0] + String.valueOf('\u00a7') + (PlayerList.get(((Page-1)*14) +i)[2].equals("true") ? "c " : "e ") + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 2: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + 'e' + PlayerList.get(((Page-1)*14) +i)[0] + String.valueOf('\u00a7') + "a " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 3: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + 'e' + PlayerList.get(((Page-1)*14) +i)[0] + String.valueOf('\u00a7') + "b " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							case 4: ((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).text = String.valueOf('\u00a7') + "f " + PlayerList.get(((Page-1)*14) +i)[1]; break;
							}
						}
						if (ListType == 3) {
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Rank = Integer.parseInt(PlayerList.get(((Page-1)*14) +i)[2]);
						}else{
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Rank = 0;
						}
						if (ModCore.mc().player.getName().equals(PlayerList.get(((Page-1)*14) +i)[1])) {
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Self = true;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Party = false;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Favourite = false;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Friend = false;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Guild = false;
						}else{
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Self = false;
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Party = ListType != 1 && PlayerGlow.PartyList.contains(PlayerList.get(((Page - 1) * 14) + i)[1]);
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Favourite = ListType == 2 && PlayerList.get(((Page - 1) * 14) + i)[2].equals("true");
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Friend = ListType != 2 && PlayerGlow.FriendsList.contains(PlayerList.get(((Page - 1) * 14) + i)[1]);
							((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).Guild = ListType != 3 && PlayerGlow.GuildList.contains(PlayerList.get(((Page - 1) * 14) + i)[1]);
						}
						((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(i)).NameParseLength = true;
					}
				}
			}
			
			int AllignLeft = 0;
			if (VisibleMsgButton) {MsgButton.visible = true; MsgButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft; AllignLeft += MsgButton.width; MsgButton.y = PlayerButtonList.get(Selected-1).y;}else{MsgButton.visible = false;}
			if (VisiblePartyButton) {PartyButton.visible = true; PartyButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += PartyButton.width; PartyButton.y = PlayerButtonList.get(Selected-1).y;}else{PartyButton.visible = false;}
			if (VisibleJoinButton) {JoinButton.visible = true; JoinButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += JoinButton.width; JoinButton.y = PlayerButtonList.get(Selected-1).y;}else{JoinButton.visible = false;}
			if (VisibleFavouriteButton) {FavouriteButton.visible = true; FavouriteButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += FavouriteButton.width; FavouriteButton.y = PlayerButtonList.get(Selected-1).y;}else{FavouriteButton.visible = false;}
			if (VisibleFriendButton) {FriendButton.visible = true; FriendButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += FriendButton.width; FriendButton.y = PlayerButtonList.get(Selected-1).y;}else{FriendButton.visible = false;}
			
			if (VisibleRankRecruitButton) {RankRecruitButton.visible = true; RankRecruitButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += RankRecruitButton.width; RankRecruitButton.y = PlayerButtonList.get(Selected-1).y;}else{RankRecruitButton.visible = false;}
			if (VisibleRankRecruiterButton) {RankRecruiterButton.visible = true; RankRecruiterButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += RankRecruiterButton.width; RankRecruiterButton.y = PlayerButtonList.get(Selected-1).y;}else{RankRecruiterButton.visible = false;}
			if (VisibleRankCaptainButton) {RankCaptainButton.visible = true; RankCaptainButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += RankCaptainButton.width; RankCaptainButton.y = PlayerButtonList.get(Selected-1).y;}else{RankCaptainButton.visible = false;}
			if (VisibleRankChiefButton) {RankChiefButton.visible = true; RankChiefButton.x = PlayerButtonList.get(Selected-1).x + AllignLeft;  AllignLeft += RankChiefButton.width; RankChiefButton.y = PlayerButtonList.get(Selected-1).y;}else{RankChiefButton.visible = false;}
            if (VisibleRankLeadingChiefButton) {
                RankLeadingChiefButton.visible = true;
                RankLeadingChiefButton.x = PlayerButtonList.get(Selected - 1).x + AllignLeft;
                RankLeadingChiefButton.y = PlayerButtonList.get(Selected - 1).y;
            } else {
                RankLeadingChiefButton.visible = false;
            }
			
			
			int AllignRight = PlayerButtonList.get(0).width;
			if (VisibleKickButton) {KickButton.visible = true; AllignRight -= KickButton.width; KickButton.x = PlayerButtonList.get(Selected-1).x + AllignRight; KickButton.y = PlayerButtonList.get(Selected-1).y;}else{KickButton.visible = false;}
			if (VisiblePlayerInfoButton) {PlayerInfoButton.visible = true; AllignRight -= PlayerInfoButton.width; PlayerInfoButton.x = PlayerButtonList.get(Selected-1).x + AllignRight; PlayerInfoButton.y = PlayerButtonList.get(Selected-1).y;}else{PlayerInfoButton.visible = false;}
		}
	}

    protected void actionPerformed(GuiButton button){
		if (button.visible && button.enabled) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			case 0:
				ListType = 0;
				mc.displayGuiScreen(new PlayerHomeMenu());
				break;
			case 1:
				if (Page + 1 > MaxPages) {
					Page = 1;
				} else {
					Page++;
				}
				Selected = 0;
				Confirm = 0;
				GuildRankChange = false; 
				RedrawButtons();
				break;
			case 2:
				if (Page - 1 < 1) {
					Page = MaxPages;
				} else {
					Page--;
				}
				Selected = 0;
				Confirm = 0;
				GuildRankChange = false; 
				RedrawButtons();
				break;
			case 101: Selected = 1; GuildRankChange = false; RedrawButtons(); break;
			case 102: Selected = 2; GuildRankChange = false; RedrawButtons(); break;
			case 103: Selected = 3; GuildRankChange = false; RedrawButtons(); break;
			case 104: Selected = 4; GuildRankChange = false; RedrawButtons(); break;
			case 105: Selected = 5; GuildRankChange = false; RedrawButtons(); break;
			case 106: Selected = 6; GuildRankChange = false; RedrawButtons(); break;
			case 107: Selected = 7; GuildRankChange = false; RedrawButtons(); break;
			
			case 108: Selected = 8; GuildRankChange = false; RedrawButtons(); break;
			case 109: Selected = 9; GuildRankChange = false; RedrawButtons(); break;
			case 110: Selected = 10; GuildRankChange = false; RedrawButtons(); break;
			case 111: Selected = 11; GuildRankChange = false; RedrawButtons(); break;
			case 112: Selected = 12; GuildRankChange = false; RedrawButtons(); break;
			case 113: Selected = 13; GuildRankChange = false; RedrawButtons(); break;
			case 114: Selected = 14; GuildRankChange = false; RedrawButtons(); break;
			
			case 99: Confirm = 1; RedrawButtons(); break;
			case 98: Confirm = 2; RedrawButtons(); break;
			case 97: Confirm = 3; RedrawButtons(); break;
			case 96: Confirm = 4; RedrawButtons(); break;
			case 95: Confirm = 5; RedrawButtons(); break;
			case 94: Confirm = 6; RedrawButtons(); break;
			case 93: Confirm = 7; RedrawButtons(); break;
			case 92: Confirm = 8; RedrawButtons(); break;
			
			case 70: GuildRankChange = true; RedrawButtons(); break;
			case 71: GuildRankChange = true; RedrawButtons(); break;
			case 72: GuildRankChange = true; RedrawButtons(); break;
			case 73: GuildRankChange = true; RedrawButtons(); break;
			
			case 75: Confirm = 25; RedrawButtons(); break;
			case 76: Confirm = 26; RedrawButtons(); break;
			case 77: Confirm = 27; RedrawButtons(); break;
			case 78: Confirm = 28; RedrawButtons(); break;
			case 79: Confirm = 0; ConfirmSelection(); RedrawButtons(); break;
			
			case 50: ConfirmSelection(); RedrawButtons(); break;
			case 49: CancelSelection(); RedrawButtons(); break;
			}
		}
	}
	
	private static void ConfirmSelection(){
		switch (Confirm){
		case 0: break; // Do nothing just skip
		case 1:
			switch (ListType){
			case 1: WynnExpansion.ChatQue.add("/party kick " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]); PlayerList.remove(((Page-1)*14) + (Selected-1)); PlayerHomeMenu.PartyList.remove(((Page-1)*14) + (Selected-1)); break;
			case 2: WynnExpansion.ChatQue.add("/friend remove " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]); PlayerList.remove(((Page-1)*14) + (Selected-1)); PlayerHomeMenu.FriendsList.remove(((Page-1)*14) + (Selected-1)); break;
			case 3: WynnExpansion.ChatQue.add("/guild kick " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]); PlayerList.remove(((Page-1)*14) + (Selected-1)); PlayerHomeMenu.GuildList.remove(((Page-1)*14) + (Selected-1)); break;
			}
			break;
		case 2:
			Minecraft.getMinecraft().displayGuiScreen(new GuiChat("/msg " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " "));
			break;
		case 3:
			if (PlayerHomeMenu.PartyList.isEmpty()){
				WynnExpansion.ChatQue.add("/party create");
				PlayerHomeMenu.PartyList.add(new String[] {PlayerHomeMenu.CurrentWorld, ModCore.mc().player.getName(), "true"});
				PlayerHomeMenu.PartyLeader = true;
			}
			WynnExpansion.ChatQue.add("/party invite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]);
			break;
		case 4: break; // Join world button
		case 5:
			PlayerList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], (PlayerList.get(((Page-1)*14) + (Selected-1))[2].equals("true") ? "false" : "true")});
			PlayerHomeMenu.FriendsList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], PlayerList.get(((Page-1)*14) + (Selected-1))[2]});
			PlayerHomeMenu.SaveFriendsList();
			break;
		case 6:
			WynnExpansion.ChatQue.add("/friend add " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]);
			PlayerHomeMenu.FriendsList.add(new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "false"});
			((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(Selected-1)).Friend = true;
			break;
		case 7:
			PlayerInfoMenu.LastPageHome = false;
			PlayerInfoMenu.PlayerLoaded = false;
			PlayerInfoMenu.CurrentPlayer = PlayerList.get(((Page-1)*14) + (Selected-1))[1];
			ModCore.mc().displayGuiScreen(new PlayerInfoMenu());
			break;
		case 8:
			WynnExpansion.ChatQue.add("/guild invite " + PlayerList.get(((Page-1)*14) + (Selected-1))[1]);
			((PlayerHomeMenu.PlayerButton) PlayerButtonList.get(Selected-1)).Guild = true;
			break;
		
		case 25:
			GuildRankChange = false;
			WynnExpansion.ChatQue.add("/guild rank " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " recruit");
			PlayerList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "1"});
			PlayerHomeMenu.GuildList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "1"});
			break;
		case 26:
			GuildRankChange = false;
			WynnExpansion.ChatQue.add("/guild rank " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " recruiter");
			PlayerList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "2"});
			PlayerHomeMenu.GuildList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "2"});
			break;
		case 27:
			GuildRankChange = false;
			WynnExpansion.ChatQue.add("/guild rank " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " captain");
			PlayerList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "3"});
			PlayerHomeMenu.GuildList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "3"});
			break;
		case 28:
			GuildRankChange = false;
			WynnExpansion.ChatQue.add("/guild rank " + PlayerList.get(((Page-1)*14) + (Selected-1))[1] + " chief");
			PlayerList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "4"});
			PlayerHomeMenu.GuildList.set(((Page-1)*14) + (Selected-1), new String[] {PlayerList.get(((Page-1)*14) + (Selected-1))[0], PlayerList.get(((Page-1)*14) + (Selected-1))[1], "4"});
			break;
		default: System.out.println("You dun goofed, there is no action with ID:" + Confirm); break;
		}
		Selected = 0;
		Confirm = -1;
	}
	
	private static void CancelSelection(){
		Selected = 0;
		Confirm = -1;
	}
}
