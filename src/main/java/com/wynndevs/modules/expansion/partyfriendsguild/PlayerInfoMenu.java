package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.GuiScreenMod;
import com.wynndevs.modules.expansion.webapi.WebAPI;
import com.wynndevs.modules.expansion.WynnExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerInfoMenu extends GuiScreenMod {
	private static final ResourceLocation TEXTURE_PLAYER = new ResourceLocation(Reference.MOD_ID, "textures/gui/menu.png");
	
	private static PlayerHomeMenu.KickButton KickPartyButton = new PlayerHomeMenu.KickButton();
	private static PlayerHomeMenu.KickButton KickFriendButton = new PlayerHomeMenu.KickButton();
	private static PlayerHomeMenu.KickButton KickGuildButton = new PlayerHomeMenu.KickButton();
	private static PlayerHomeMenu.MsgButton MsgButton = new PlayerHomeMenu.MsgButton();
	private static PlayerHomeMenu.PartyButton PartyButton = new PlayerHomeMenu.PartyButton();
	private static PlayerHomeMenu.FavouriteButton FavouriteButton = new PlayerHomeMenu.FavouriteButton();
	private static PlayerHomeMenu.FriendButton FriendButton = new PlayerHomeMenu.FriendButton();
	
	private static PlayerHomeMenu.RankRecruiterButton GuildInviteButton = new PlayerHomeMenu.RankRecruiterButton();
	private static PlayerHomeMenu.RankRecruitButton RankRecruitButton = new PlayerHomeMenu.RankRecruitButton();
	private static PlayerHomeMenu.RankRecruiterButton RankRecruiterButton = new PlayerHomeMenu.RankRecruiterButton();
	private static PlayerHomeMenu.RankCaptainButton RankCaptainButton = new PlayerHomeMenu.RankCaptainButton();
	private static PlayerHomeMenu.RankChiefButton RankChiefButton = new PlayerHomeMenu.RankChiefButton();
	
	private static PlayerHomeMenu.ConfirmButton ConfirmButton = new PlayerHomeMenu.ConfirmButton();
	private static PlayerHomeMenu.CancelButton CancelButton = new PlayerHomeMenu.CancelButton();
	
	
	public static boolean LastPageHome = false;
	public static String CurrentPlayer = "";
	static int GuildID = -1;
	private static boolean Loaded = false;
	public static boolean PlayerLoaded = false;
	static List<String[]> PlayerInfomation = new ArrayList<String[]>();
	private static int Confirm = 0;
	
	private static int PartyIndex = -1;
	private static int FriendsIndex = -1;
	private static int GuildIndex = -1;
	
	private static int Page = 0;
	private static int MaxPages = 0;
	
	@Override
	protected String GetButtonTooltip(int buttonId) {
		switch (buttonId) {
		case 0: return "Back";
		
		case 100: return (FavouriteButton.Favourite ? String.valueOf('\u00a7') + "eRemove " + CurrentPlayer + " from Favourites" : String.valueOf('\u00a7') + "6Add " + CurrentPlayer + " to Favourites");
		case 101: return String.valueOf('\u00a7') + "aAdd " + CurrentPlayer + " to your Friends list";
		case 102: return String.valueOf('\u00a7') + "cRemove " + CurrentPlayer + " from your Friends list";
		
		case 103: return "Message " + CurrentPlayer;
		case 104: return String.valueOf('\u00a7') + "eInvite " + CurrentPlayer + " to Party";
		case 105: return String.valueOf('\u00a7') + "cKick " + CurrentPlayer + " from Party";
		
		case 106: return String.valueOf('\u00a7') + "bSet to Recruit";
		case 107: return String.valueOf('\u00a7') + "bSet to Recruiter";
		case 108: return String.valueOf('\u00a7') + "bSet to Captain";
		case 109: return String.valueOf('\u00a7') + "bSet to Chief";
		case 110: return String.valueOf('\u00a7') + "bInvite " + CurrentPlayer + " to your Guild";
		case 111: return String.valueOf('\u00a7') + "bKick " + CurrentPlayer + " from your Guild";
		}
		return null;
	}
	
	@Override
	public void initGui() {
		if (!PlayerLoaded){
			PlayerInfomation.clear();
			GuildID = -1;
			WebAPI.TaskSchedule.add("GetPlayerInfo");
		}else{
			PartyIndex = -1;
			for (int i=0;i<PlayerHomeMenu.PartyList.size();i++){
				if (PlayerHomeMenu.PartyList.get(i)[1].equals(CurrentPlayer)){
					PartyIndex = i;
					break;
				}
			}
			FriendsIndex = -1;
			for (int i=0;i<PlayerHomeMenu.FriendsList.size();i++){
				if (PlayerHomeMenu.FriendsList.get(i)[1].equals(CurrentPlayer)){
					FriendsIndex = i;
					break;
				}
			}
			GuildIndex = -1;
			for (int i=0;i<PlayerHomeMenu.GuildList.size();i++){
				if (PlayerHomeMenu.GuildList.get(i)[1].equals(CurrentPlayer)){
					GuildIndex = i;
					break;
				}
			}
		}
		Confirm = 0;
		Page = 1;
		MaxPages = PlayerInfomation.size();
		if (MaxPages == 0) MaxPages = 1;
		
		this.addButton(new PlayerHomeMenu.ChangePageButton(1, (this.width / 2) + 25, 50, true));
		this.addButton(new PlayerHomeMenu.ChangePageButton(2, (this.width / 2) - 52, 50, false));
		
		this.addButton(new PlayerHomeMenu.BackButton(0, (this.width / 2) + 55, 50));
		this.addButton(new PlayerHomeMenu.ExitButton(-1, (this.width / 2) + 100, 15));
		
		
		this.addButton(FavouriteButton = new PlayerHomeMenu.FavouriteButton(100, (this.width / 2), 170));
		this.addButton(FriendButton = new PlayerHomeMenu.FriendButton(101, (this.width / 2), 170));
		this.addButton(KickFriendButton = new PlayerHomeMenu.KickButton(102, (this.width / 2), 170));
		
		this.addButton(MsgButton = new PlayerHomeMenu.MsgButton(103, (this.width / 2), 170));
		this.addButton(PartyButton = new PlayerHomeMenu.PartyButton(104, (this.width / 2), 170));
		this.addButton(KickPartyButton = new PlayerHomeMenu.KickButton(105, (this.width / 2), 170));
		
		this.addButton(RankRecruitButton = new PlayerHomeMenu.RankRecruitButton(106, (this.width / 2), 170));
		this.addButton(RankRecruiterButton = new PlayerHomeMenu.RankRecruiterButton(107, (this.width / 2), 170));
		this.addButton(RankCaptainButton = new PlayerHomeMenu.RankCaptainButton(108, (this.width / 2), 170));
		this.addButton(RankChiefButton = new PlayerHomeMenu.RankChiefButton(109, (this.width / 2), 170));
		this.addButton(GuildInviteButton = new PlayerHomeMenu.RankRecruiterButton(110, (this.width / 2), 170));
		this.addButton(KickGuildButton = new PlayerHomeMenu.KickButton(111, (this.width / 2), 170));
		
		
		this.addButton(ConfirmButton = new PlayerHomeMenu.ConfirmButton(50, (this.width / 2) + 50, 160));
		this.addButton(CancelButton = new PlayerHomeMenu.CancelButton(49, (this.width / 2) - 69, 160));
		
		
		RedrawButtons();
	}
	
	@Override
	public void updateScreen() {
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(TEXTURE_PLAYER);
		
		this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
		
		this.drawCenteredStringPlain(mc.fontRenderer, Page + "/" + MaxPages, this.width / 2, 53, Integer.parseInt("959595", 16));
		
		if (!PlayerLoaded){
			this.drawCenteredStringPlain(mc.fontRenderer,"Collecting Data...", this.width / 2, 125, Integer.parseInt("959595", 16));
			Loaded = false;
		}else if (!Loaded){
			Loaded = true;
			mc.displayGuiScreen(new PlayerInfoMenu());
		}else if (Loaded && PlayerInfomation.isEmpty()){
			this.drawCenteredStringPlain(mc.fontRenderer, CurrentPlayer, this.width / 2, 110, Integer.parseInt("858585", 16));
			this.drawCenteredStringPlain(mc.fontRenderer, "Is not a valid Minecraft Username", this.width / 2, 125, Integer.parseInt("858585", 16));
		}else if (MaxPages == 1){
			this.drawCenteredStringPlain(mc.fontRenderer, CurrentPlayer, this.width / 2, 110, Integer.parseInt("858585", 16));
			this.drawCenteredStringPlain(mc.fontRenderer, "Has never played Wynncraft", this.width / 2, 125, Integer.parseInt("858585", 16));
		}else if (Confirm > 0){ 
			this.drawCenteredStringPlain(mc.fontRenderer, "You are about to:", this.width / 2, 80, Integer.parseInt("858585", 16));
			switch (Confirm){
				case 1: ConfirmSelection(); RedrawButtons(); break;
				case 2: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "aAdd " + CurrentPlayer + " to your Friends list", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 3: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cRemove " + CurrentPlayer + " from Friends", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				
				case 4: ConfirmSelection(); RedrawButtons(); break;
				case 5: this.drawCenteredStringPlain(mc.fontRenderer, "Invite " + CurrentPlayer + " to your party", this.width / 2, 105, Integer.parseInt("d9e512", 16)); break;
				case 6: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cRemove " + CurrentPlayer + " from the Party", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				
				case 7: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + CurrentPlayer + " to Recruit", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 8: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + CurrentPlayer + " to Recruiter", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 9: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + CurrentPlayer + " to Captain", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 10: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Change Rank of " + CurrentPlayer + " to Chief", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 11: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Invite " + CurrentPlayer + " to your guild", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
				case 12: this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "cKick " + CurrentPlayer + " from your Guild", this.width / 2, 105, Integer.parseInt("858585", 16)); break;
			}
			
			this.drawCenteredStringPlain(mc.fontRenderer,"Are you sure?", this.width / 2, 130, Integer.parseInt("858585", 16));
			this.drawCenteredStringPlain(mc.fontRenderer,"Continue", (this.width / 2) +59, 145, Integer.parseInt("55FF55", 16));
			this.drawCenteredStringPlain(mc.fontRenderer,"Cancel", (this.width / 2) -60, 145, Integer.parseInt("FF5555", 16));
		}else if (Page == 1){
			this.drawCenteredStringPlain(mc.fontRenderer, (PlayerInfomation.get(0)[1].equals("Player") ? (PlayerInfomation.get(0)[2].equals("") ? "" : PlayerInfomation.get(0)[2] + " ") : PlayerInfomation.get(0)[1] + " ") + CurrentPlayer + String.valueOf('\u00a7') + "6 [Lvl. " + PlayerInfomation.get(0)[9] + "]", this.width / 2, 75, Integer.parseInt("858585", 16));
			if (PlayerHomeMenu.CurrentWorld.equals(PlayerInfomation.get(0)[6])){
				this.drawCenteredStringPlain(mc.fontRenderer, "[" + PlayerInfomation.get(0)[6] + "]", this.width / 2, 85, Integer.parseInt("24e512", 16));
			}else if (!PlayerInfomation.get(0)[6].equals("null")){
				this.drawCenteredStringPlain(mc.fontRenderer,"[" + PlayerInfomation.get(0)[6] + "]", this.width / 2, 85, Integer.parseInt("d9e512", 16));
			}else{
				this.drawCenteredStringPlain(mc.fontRenderer, "[Offline]", this.width / 2, 85, Integer.parseInt("858585", 16));
			}
			
			this.drawStringPlain(mc.fontRenderer, "Total Playtime: " + Math.round(Integer.parseInt(PlayerInfomation.get(0)[3]) / 60) + " hours and " + new DecimalFormat("00").format(Integer.parseInt(PlayerInfomation.get(0)[3]) % 60) + " minutes", (this.width / 2) -105, 110, Integer.parseInt("858585", 16));
			this.drawStringPlain(mc.fontRenderer, "Joined: " + PlayerInfomation.get(0)[4], (this.width / 2) -120, 120, Integer.parseInt("858585", 16));
			this.drawStringPlain(mc.fontRenderer, "Last On: " + PlayerInfomation.get(0)[5], (this.width / 2) , 120, Integer.parseInt("858585", 16));
			
			this.drawStringPlain(mc.fontRenderer, "Mob Kills: " + new DecimalFormat("###,###,###,###,###,##0").format(Long.parseLong(PlayerInfomation.get(0)[7])), (this.width / 2) -105, 130, Integer.parseInt("959595", 16));
			this.drawStringPlain(mc.fontRenderer, "Chests Found: " + new DecimalFormat("###,###,###,###,###,##0").format(Long.parseLong(PlayerInfomation.get(0)[8])), (this.width / 2), 130, Integer.parseInt("959595", 16));
			
			this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Guild: " + PlayerInfomation.get(0)[10] + (PlayerInfomation.get(0)[10].equals("None") ? "" : " [" + (PlayerInfomation.get(0)[11].equals("Owner") ? "Guild Master" : PlayerInfomation.get(0)[11]) + "]"), (this.width / 2), 140, Integer.parseInt("858585", 16));
			if (PlayerInfomation.get(0)[10].equals(PlayerHomeMenu.GuildName)){
				if (GuildID == -1) {
					for (int i=0;i<PlayerHomeMenu.GuildList.size();i++){
						if (PlayerHomeMenu.GuildList.get(i)[1].equals(CurrentPlayer)){
							GuildID = i;
							RedrawButtons();
							break;
						}
					}
					if (GuildID == -1) {GuildID = -2;}
				}else if (GuildID >= 0){
					this.drawCenteredStringPlain(mc.fontRenderer, String.valueOf('\u00a7') + "3Contributed: " + new DecimalFormat("###,###,###,###,###,##0").format(Long.parseLong(PlayerHomeMenu.GuildList.get(GuildID)[3])) + " XP", (this.width / 2), 150, Integer.parseInt("858585", 16));
				}
			}
		}else if (Page > 1){
			this.drawCenteredStringPlain(mc.fontRenderer, CurrentPlayer, this.width / 2, 75, Integer.parseInt("858585", 16));
			this.drawCenteredStringPlain(mc.fontRenderer,  PlayerInfomation.get(Page-1)[0] + String.valueOf('\u00a7') + "6 [Lvl. " + PlayerInfomation.get(Page-1)[1] + "]", this.width / 2, 85, Integer.parseInt("858585", 16));
			
			this.drawStringPlain(mc.fontRenderer, "Playtime: " + Math.round(Integer.parseInt(PlayerInfomation.get(Page-1)[6]) / 60) + " hours and " + new DecimalFormat("00").format(Integer.parseInt(PlayerInfomation.get(Page-1)[6]) % 60) + " minutes", (this.width / 2) -105, 110, Integer.parseInt("858585", 16));
			this.drawStringPlain(mc.fontRenderer, "XP: " + PlayerInfomation.get(Page-1)[2] + "%", (this.width / 2) -105, 120, Integer.parseInt("858585", 16));
			this.drawStringPlain(mc.fontRenderer, "Quests: " + PlayerInfomation.get(Page-1)[3], this.width / 2, 120, Integer.parseInt("858585", 16));
			
			this.drawStringPlain(mc.fontRenderer, "Mob Kills: " + new DecimalFormat("###,###,###,###,###,##0").format(Long.parseLong(PlayerInfomation.get(Page-1)[4])), (this.width / 2) -105, 130, Integer.parseInt("858585", 16));
			this.drawStringPlain(mc.fontRenderer, "Chests Found: " + new DecimalFormat("###,###,###,###,###,##0").format(Long.parseLong(PlayerInfomation.get(Page-1)[5])), this.width / 2, 130, Integer.parseInt("858585", 16));
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private void RedrawButtons() {
		if (PlayerInfomation.isEmpty() || MaxPages == 1){
			KickPartyButton.visible = false;
			KickFriendButton.visible = false;
			KickGuildButton.visible = false;
			
			MsgButton.visible = false;
			PartyButton.visible = false;
			FavouriteButton.visible = false;
			FriendButton.visible = false;
			
			RankRecruitButton.visible = false;
			RankRecruiterButton.visible = false;
			RankCaptainButton.visible = false;
			RankChiefButton.visible = false;
			GuildInviteButton.visible = false;
			
			ConfirmButton.visible = false;
			CancelButton.visible = false;
		}else{
			if (Confirm == 0){
				boolean VisibleFavouriteButton = false;
				boolean VisibleFriendButton = false;
				boolean VisibleKickFriendButton = false;
				
				boolean VisibleMsgButton = false;
				boolean VisiblePartyButton = false;
				boolean VisibleKickPartyButton = false;
				
				boolean VisibleRankRecruitButton = false;
				boolean VisibleRankRecruiterButton = false;
				boolean VisibleRankCaptainButton = false;
				boolean VisibleRankChiefButton = false;
				boolean VisibleGuildInviteButton = false;
				boolean VisibleKickGuildButton = false;
				
				ConfirmButton.visible = false;
				CancelButton.visible = false;
				
				if (!CurrentPlayer.equals(ModCore.mc().player.getName())){
					if (!PlayerInfomation.get(0)[6].equals("null")){
						VisibleMsgButton = true;
					}
					if (PlayerHomeMenu.PartyLeader && PlayerGlow.PartyList.contains(CurrentPlayer)){
						VisibleKickPartyButton = true;
					}
					if (((PlayerHomeMenu.PartyLeader && !PlayerGlow.PartyList.contains(CurrentPlayer) && PlayerHomeMenu.CurrentWorld.equals(PlayerInfomation.get(0)[6])) || (PlayerGlow.PartyList.isEmpty()) && PlayerHomeMenu.CurrentWorld.equals(PlayerInfomation.get(0)[6]))){
						VisiblePartyButton = true;
					}
					if (PlayerGlow.FriendsList.contains(CurrentPlayer)){
						VisibleKickFriendButton = true;
						for (int i=0;i<PlayerHomeMenu.FriendsList.size();i++){
							if (PlayerHomeMenu.FriendsList.get(i)[1].equals(CurrentPlayer)){
								if (PlayerHomeMenu.FriendsList.get(i)[2].equals("true")){
									VisibleFavouriteButton = true;
									FavouriteButton.Favourite = true;
								}else{
									VisibleFavouriteButton = true;
									FavouriteButton.Favourite = false;
								}
								break;
							}
						}
					}else{
						VisibleFriendButton = true;
					}
					if (PlayerGlow.GuildList.contains(CurrentPlayer)){
						if (GuildID >= 0 ){
							if (PlayerHomeMenu.GuildRank >= 4 && Integer.parseInt(PlayerHomeMenu.GuildList.get(GuildID)[2]) < PlayerHomeMenu.GuildRank) {VisibleKickGuildButton = true;}
							if (PlayerHomeMenu.GuildRank >= 3 && Integer.parseInt(PlayerHomeMenu.GuildList.get(GuildID)[2]) < PlayerHomeMenu.GuildRank && !PlayerHomeMenu.GuildList.get(GuildID)[2].equals("1")) {VisibleRankRecruitButton = true;}
							if (PlayerHomeMenu.GuildRank >= 3 && Integer.parseInt(PlayerHomeMenu.GuildList.get(GuildID)[2]) < PlayerHomeMenu.GuildRank && !PlayerHomeMenu.GuildList.get(GuildID)[2].equals("2")) {VisibleRankRecruiterButton = true;}
							if (PlayerHomeMenu.GuildRank >= 4 && Integer.parseInt(PlayerHomeMenu.GuildList.get(GuildID)[2]) < PlayerHomeMenu.GuildRank && !PlayerHomeMenu.GuildList.get(GuildID)[2].equals("3")) {VisibleRankCaptainButton = true;}
							if (PlayerHomeMenu.GuildRank >= 5 && Integer.parseInt(PlayerHomeMenu.GuildList.get(GuildID)[2]) < PlayerHomeMenu.GuildRank && !PlayerHomeMenu.GuildList.get(GuildID)[2].equals("4")) {VisibleRankChiefButton = true;}
						}
					}else if (PlayerHomeMenu.GuildRank >= 2 && PlayerInfomation.get(0)[10].equals("None")){
						VisibleGuildInviteButton = true;
					}
				}
				
				int FriendAllign = -30;
				if (VisibleFavouriteButton) {FavouriteButton.visible = true; FriendAllign -= FavouriteButton.width; FavouriteButton.x = (this.width/2) + FriendAllign;}else{FavouriteButton.visible = false;}
				if (VisibleFriendButton) {FriendButton.visible = true; FriendAllign -= FriendButton.width; FriendButton.x = (this.width/2) + FriendAllign;}else{FriendButton.visible = false;}
				if (VisibleKickFriendButton) {KickFriendButton.visible = true; FriendAllign -= KickFriendButton.width; KickFriendButton.x = (this.width/2) + FriendAllign;}else{KickFriendButton.visible = false;}
				
				if (VisibleMsgButton) {MsgButton.visible = true; MsgButton.x = (this.width/2) - (MsgButton.width/(VisiblePartyButton || VisibleKickPartyButton ? 1 : 2));}else{MsgButton.visible = false;}
				if (VisiblePartyButton) {PartyButton.visible = true; PartyButton.x = (this.width/2);}else{PartyButton.visible = false;}
				if (VisibleKickPartyButton) {KickPartyButton.visible = true; KickPartyButton.x = (this.width/2);}else{KickPartyButton.visible = false;}
				
				int GuildAllign = 30;
				if (VisibleRankRecruitButton) {RankRecruitButton.visible = true; RankRecruitButton.x = (this.width/2) + GuildAllign; GuildAllign += RankRecruitButton.width;}else{RankRecruitButton.visible = false;}
				if (VisibleRankRecruiterButton) {RankRecruiterButton.visible = true; RankRecruiterButton.x = (this.width/2) + GuildAllign; GuildAllign += RankRecruiterButton.width;}else{RankRecruiterButton.visible = false;}
				if (VisibleRankCaptainButton) {RankCaptainButton.visible = true; RankCaptainButton.x = (this.width/2) + GuildAllign; GuildAllign += RankCaptainButton.width;}else{RankCaptainButton.visible = false;}
				if (VisibleRankChiefButton) {RankChiefButton.visible = true; RankChiefButton.x = (this.width/2) + GuildAllign; GuildAllign += RankChiefButton.width;}else{RankChiefButton.visible = false;}
				if (VisibleGuildInviteButton) {GuildInviteButton.visible = true; GuildInviteButton.x = (this.width/2) + GuildAllign; GuildAllign += GuildInviteButton.width;}else{GuildInviteButton.visible = false;}
				if (VisibleKickGuildButton) {KickGuildButton.visible = true; KickGuildButton.x = (this.width/2) + GuildAllign; GuildAllign += KickGuildButton.width;}else{KickGuildButton.visible = false;}
			}else{
				KickPartyButton.visible = false;
				KickFriendButton.visible = false;
				KickGuildButton.visible = false;
				
				MsgButton.visible = false;
				PartyButton.visible = false;
				FavouriteButton.visible = false;
				FriendButton.visible = false;
				
				RankRecruitButton.visible = false;
				RankRecruiterButton.visible = false;
				RankCaptainButton.visible = false;
				RankChiefButton.visible = false;
				GuildInviteButton.visible = false;
				
				ConfirmButton.visible = true;
				CancelButton.visible = true;
			}
		}
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.visible) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			case 0:
				if (LastPageHome) {mc.displayGuiScreen(new PlayerHomeMenu());}else{mc.displayGuiScreen(new PlayerListMenu());} break;
			case 1:
				if (Page + 1 > MaxPages) {
					Page = 1;
				} else {
					Page++;
				}
				RedrawButtons();
				break;
			case 2:
				if (Page - 1 < 1) {
					Page = MaxPages;
				} else {
					Page--;
				}
				RedrawButtons();
				break;
			case 100: Confirm = 1; RedrawButtons(); break;
			case 101: Confirm = 2; RedrawButtons(); break;
			case 102: Confirm = 3; RedrawButtons(); break;
			
			case 103: Confirm = 4; RedrawButtons(); break;
			case 104: Confirm = 5; RedrawButtons(); break;
			case 105: Confirm = 6; RedrawButtons(); break;
			
			case 106: Confirm = 7; RedrawButtons(); break;
			case 107: Confirm = 8; RedrawButtons(); break;
			case 108: Confirm = 9; RedrawButtons(); break;
			case 109: Confirm = 10; RedrawButtons(); break;
			case 110: Confirm = 11; RedrawButtons(); break;
			case 111: Confirm = 12; RedrawButtons(); break;
			
			case 50: ConfirmSelection(); RedrawButtons(); break;
			case 49: CancelSelection(); RedrawButtons(); break;
			}
		}
	}
	
	private static void ConfirmSelection(){
		switch (Confirm){
			case 1: PlayerHomeMenu.FriendsList.set(FriendsIndex, new String[] {PlayerHomeMenu.FriendsList.get(FriendsIndex)[0], CurrentPlayer, (PlayerHomeMenu.FriendsList.get(FriendsIndex)[2].equals("true") ? "false" : "true")}); PlayerHomeMenu.SaveFriendsList(); break;
			case 2: WynnExpansion.ChatQue.add("/friend add " + CurrentPlayer); PlayerHomeMenu.FriendsList.add(new String[] {PlayerInfomation.get(0)[6], CurrentPlayer, "false"}); PlayerHomeMenu.SaveFriendsList(); FriendsIndex = PlayerHomeMenu.FriendsList.size(); break;
			case 3: WynnExpansion.ChatQue.add("/friend remove " + CurrentPlayer); PlayerHomeMenu.FriendsList.remove(FriendsIndex); PlayerHomeMenu.SaveFriendsList(); FriendsIndex = -1; break;
				
			case 4: Minecraft.getMinecraft().displayGuiScreen(new GuiChat("/msg " + CurrentPlayer)); break;
			case 5:
				if (PlayerHomeMenu.PartyList.isEmpty()){
					WynnExpansion.ChatQue.add("/party create");
					PlayerHomeMenu.PartyList.add(new String[] {PlayerHomeMenu.CurrentWorld, ModCore.mc().player.getName(), "true"});
				}
				WynnExpansion.ChatQue.add("/party invite " + CurrentPlayer);
				break;
			case 6: WynnExpansion.ChatQue.add("/party kick " + CurrentPlayer); PlayerHomeMenu.PartyList.remove(PartyIndex); PartyIndex = -1; break;
				
			case 7: WynnExpansion.ChatQue.add("/guild rank " + CurrentPlayer + " Recruit"); PlayerHomeMenu.GuildList.set(GuildIndex, new String[] {PlayerInfomation.get(0)[6], CurrentPlayer, "1", PlayerHomeMenu.GuildList.get(GuildIndex)[3]}); PlayerInfomation.get(0)[11] = "Recruit"; break;
			case 8: WynnExpansion.ChatQue.add("/guild rank " + CurrentPlayer + " Recruiter"); PlayerHomeMenu.GuildList.set(GuildIndex, new String[] {PlayerInfomation.get(0)[6], CurrentPlayer, "2", PlayerHomeMenu.GuildList.get(GuildIndex)[3]}); PlayerInfomation.get(0)[11] = "Recruiter"; break;
			case 9: WynnExpansion.ChatQue.add("/guild rank " + CurrentPlayer + " Captain"); PlayerHomeMenu.GuildList.set(GuildIndex, new String[] {PlayerInfomation.get(0)[6], CurrentPlayer, "3", PlayerHomeMenu.GuildList.get(GuildIndex)[3]}); PlayerInfomation.get(0)[11] = "Captain"; break;
			case 10: WynnExpansion.ChatQue.add("/guild rank " + CurrentPlayer + " Chief"); PlayerHomeMenu.GuildList.set(GuildIndex, new String[] {PlayerInfomation.get(0)[6], CurrentPlayer, "4", PlayerHomeMenu.GuildList.get(GuildIndex)[3]}); PlayerInfomation.get(0)[11] = "Chief"; break;
			case 11: WynnExpansion.ChatQue.add("/guild invite " + CurrentPlayer); break;
			case 12: WynnExpansion.ChatQue.add("/guild kick " + CurrentPlayer); PlayerHomeMenu.GuildList.remove(GuildIndex); GuildIndex = -1; PlayerInfomation.get(0)[10] = "None"; break;
		}
		Confirm = 0;
	}
	
	private static void CancelSelection(){
		Confirm = 0;
	}
	
	
	private static String[] Overview = {"username", "rank", "tag", "playtime", "first_join_friendly", "last_join_friendly", "current_server", "mobs_killed", "chests_found", "total_level", "guild\":{\"name", "rank"};
	private static String[] ClassPage = {"level", "xp", "questsAmount", "mobs_killed", "chests_found", "playtime"};
	
	// Off Main Thread \\
	public static void GetPlayerInfo(){
		try {
			BufferedReader PlayerNameCorrectionRawURL = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + CurrentPlayer + "?at=" + (System.currentTimeMillis() / 1000L)).openConnection().getInputStream()));
			String PlayerNameCorrectionRaw = PlayerNameCorrectionRawURL.readLine();
			PlayerNameCorrectionRawURL.close(); 
			
			if (PlayerNameCorrectionRaw == null || PlayerNameCorrectionRaw.equals("")) {PlayerLoaded = true; return;}
			CurrentPlayer = PlayerNameCorrectionRaw.substring(PlayerNameCorrectionRaw.indexOf("\"name\":\"") +8, PlayerNameCorrectionRaw.indexOf("\"", PlayerNameCorrectionRaw.indexOf("\"name\":\"") +8));
			
			BufferedReader PlayerInfomationRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.PlayerInfoAPIURL.replace("CMDARG", CurrentPlayer)).openConnection().getInputStream()));
			String PlayerInfomationRaw = PlayerInfomationRawURL.readLine();
			PlayerInfomationRawURL.close(); 
			
			int LastLocation = 0;
			String[] OverviewTmp = new String[Overview.length];
			for (int i=0;i<Overview.length;i++){
				if (PlayerInfomationRaw.contains("\"" + Overview[i].toString() + "\":")) {
					LastLocation = PlayerInfomationRaw.indexOf("\"" + Overview[i].toString() + "\":", LastLocation);
					if (LastLocation > 0){
						LastLocation += Overview[i].length() + 3;
						OverviewTmp[i] = PlayerInfomationRaw.substring(LastLocation, (PlayerInfomationRaw.indexOf(",", LastLocation) < PlayerInfomationRaw.indexOf("}", LastLocation) ? PlayerInfomationRaw.indexOf(",", LastLocation) : PlayerInfomationRaw.indexOf("}", LastLocation))).replace("\"", "");
					}else{
						OverviewTmp[i] = "?";
					}
				}else{
					OverviewTmp[i] = "?";
				}
			}
			switch (OverviewTmp[1]) {
			case "Administrator": OverviewTmp[1] = String.valueOf('\u00a7') + "4[" + String.valueOf('\u00a7') + "c" + OverviewTmp[1] + String.valueOf('\u00a7') + "4]"; break;
			case "Moderator": OverviewTmp[1] = String.valueOf('\u00a7') + "6[" + String.valueOf('\u00a7') + "6" + OverviewTmp[1] + String.valueOf('\u00a7') + "6]"; break;
			case "Builder": OverviewTmp[1] = String.valueOf('\u00a7') + "3[" + String.valueOf('\u00a7') + "b" + OverviewTmp[1] + String.valueOf('\u00a7') + "3]"; break;
			case "Game Master": OverviewTmp[1] = String.valueOf('\u00a7') + "3[" + String.valueOf('\u00a7') + "b" + OverviewTmp[1] + String.valueOf('\u00a7') + "3]"; break;
			case "Hybrid": OverviewTmp[1] = String.valueOf('\u00a7') + "3[" + String.valueOf('\u00a7') + "b" + OverviewTmp[1] + String.valueOf('\u00a7') + "3]"; break;
			case "CMD": OverviewTmp[1] = String.valueOf('\u00a7') + "3[" + String.valueOf('\u00a7') + "b" + OverviewTmp[1] + String.valueOf('\u00a7') + "3]"; break;
			case "Music": OverviewTmp[1] = String.valueOf('\u00a7') + "3[" + String.valueOf('\u00a7') + "b" + OverviewTmp[1] + String.valueOf('\u00a7') + "3]"; break;
			case "Media": OverviewTmp[1] = String.valueOf('\u00a7') + "d[" + String.valueOf('\u00a7') + "5" + OverviewTmp[1] + String.valueOf('\u00a7') + "d]"; break;
			default: break;
			}
			switch (OverviewTmp[2]) {
			case "VIP": OverviewTmp[2] = String.valueOf('\u00a7') + "2[" + String.valueOf('\u00a7') + "a" + (PlayerInfomationRaw.contains("\"displayTag\":true,\"veteran\":true") ? "Vet" : OverviewTmp[2]) + String.valueOf('\u00a7') + "2]"; break;
			case "VIP+": OverviewTmp[2] = String.valueOf('\u00a7') + "b[" + String.valueOf('\u00a7') + "3" + (PlayerInfomationRaw.contains("\"displayTag\":true,\"veteran\":true") ? "Vet" : OverviewTmp[2]) + String.valueOf('\u00a7') + "b]"; break;
			case "HERO": OverviewTmp[2] = String.valueOf('\u00a7') + "5[" + String.valueOf('\u00a7') + "d" + (PlayerInfomationRaw.contains("\"displayTag\":true,\"veteran\":true") ? "Vet" : OverviewTmp[2]) + String.valueOf('\u00a7') + "5]"; break;
			default: break;
			}
			PlayerInfomation.add(OverviewTmp);
			
			if (!PlayerInfomationRaw.contains("\"error\":\"Player not found\"")){
				LastLocation = PlayerInfomationRaw.indexOf("\"classes\":{") + 11;
				boolean CollectingClasses = true;
				while (CollectingClasses){
					String[] ClassPageTmp = new String[ClassPage.length+1];
					
					String ClassTmp = PlayerInfomationRaw.substring(LastLocation, PlayerInfomationRaw.indexOf(":{", LastLocation)).replace("\"", "");
					if (ClassTmp.startsWith("archer")){
						ClassPageTmp[0] = "Archer";
					}else if (ClassTmp.startsWith("warrior")){
						ClassPageTmp[0] = "Warrior";
					}else if (ClassTmp.startsWith("mage")){
						ClassPageTmp[0] = "Mage";
					}else if (ClassTmp.startsWith("assassin")){
						ClassPageTmp[0] = "Assassin";
					}else if (ClassTmp.startsWith("hunter")){
						ClassPageTmp[0] = "Hunter";
					}else if (ClassTmp.startsWith("knight")){
						ClassPageTmp[0] = "Knight";
					}else if (ClassTmp.startsWith("darkwizard")){
						ClassPageTmp[0] = "Dark Wizard";
					}else if (ClassTmp.startsWith("ninja")){
						ClassPageTmp[0] = "Ninja";
					}else{
						ClassPageTmp[0] = "?";
					}
					
					for (int i=0;i<ClassPage.length;i++){
						if (PlayerInfomationRaw.contains("\"" + ClassPage[i].toString() + "\":")) {
							LastLocation = PlayerInfomationRaw.indexOf("\"" + ClassPage[i].toString() + "\":", LastLocation);
							if (LastLocation > 0){
								LastLocation += ClassPage[i].length() + 3;
								ClassPageTmp[i+1] = PlayerInfomationRaw.substring(LastLocation, (PlayerInfomationRaw.indexOf(",", LastLocation) < PlayerInfomationRaw.indexOf("}", LastLocation) ? PlayerInfomationRaw.indexOf(",", LastLocation) : PlayerInfomationRaw.indexOf("}", LastLocation))).replace("\"", "");
							}
						}
					}
					
					PlayerInfomation.add(ClassPageTmp);
					
					//System.out.println(LastLocation + " : " + PlayerInfomationRaw.indexOf("}", LastLocation) + PlayerInfomationRaw.charAt(PlayerInfomationRaw.indexOf("}", LastLocation) +1));
					
					if (PlayerInfomationRaw.charAt(PlayerInfomationRaw.indexOf("}", LastLocation) +1) == '}') {
						CollectingClasses = false;
					}else{
						LastLocation = PlayerInfomationRaw.indexOf("}", LastLocation) + 2;
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		PlayerLoaded = true;
	}
}
