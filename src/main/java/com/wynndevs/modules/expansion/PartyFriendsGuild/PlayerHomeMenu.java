package com.wynndevs.modules.expansion.PartyFriendsGuild;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.Misc.Delay;
import com.wynndevs.modules.expansion.Misc.GuiScreenMod;
import com.wynndevs.modules.expansion.WebAPI.PlayerCollection;
import com.wynndevs.modules.expansion.WebAPI.WebAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerHomeMenu extends GuiScreenMod {
	private static final ResourceLocation TEXTURE_MENU = new ResourceLocation(ExpReference.MOD_ID, "textures/gui/menu.png");
	
	
	static List<String[]> PartyList = new CopyOnWriteArrayList<String[]>();
	static List<String[]> FriendsList = new CopyOnWriteArrayList<String[]>();
	static List<String[]> GuildList = new CopyOnWriteArrayList<String[]>();
	static List<String[]> WorldList = new CopyOnWriteArrayList<String[]>();
	
	static String CurrentWorld = "0";
	public static String GuildName = "";
	public static int GuildRank = 0;
	static boolean PartyLeader = false;
	public static boolean PlayersLoaded = false;
	public static Delay RefreshTimer = new Delay(7.5f,false);
	
	
	@Override
	protected String GetButtonTooltip(int buttonId) {
		/*switch (buttonId) {
		case 1:
			return "Next Page";
		case 2:
			return "Previous Page";
		
		}*/
		return null;
	}
	
	@Override
	public void initGui() {
		
		this.addButton(new ExitButton(-1, (this.width / 2) + 100, 15));
		
		this.addButton(new HomeMenuButton(0, (this.width / 2) - 58, 85, String.valueOf('\u00a7') + "f" + ModCore.mc().player.getName()));
		this.addButton(new HomeMenuButton(1, (this.width / 2) - 58, 105, String.valueOf('\u00a7') + "eParty"));
		this.addButton(new HomeMenuButton(2, (this.width / 2) - 58, 125, String.valueOf('\u00a7') + "aFriends"));
		this.addButton(new HomeMenuButton(3, (this.width / 2) - 58, 145, String.valueOf('\u00a7') + "bGuild"));
		this.addButton(new HomeMenuButton(4, (this.width / 2) - 58, 165, "World"));
		
		if (!PlayersLoaded || RefreshTimer.Passed()){
			RefreshTimer.Reset();
			PlayersLoaded = false;
			WebAPI.TaskSchedule.add("RefreshPlayerLists");
		}
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.visible) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			case 0:
				PlayerInfoMenu.LastPageHome = true;
				PlayerInfoMenu.PlayerLoaded = false;
				PlayerInfoMenu.CurrentPlayer = ModCore.mc().player.getName();
				mc.displayGuiScreen(new PlayerInfoMenu());
				break;
			case 1:
				PlayerListMenu.Page = 1;
				PlayerListMenu.ListType = 1;
				mc.displayGuiScreen(new PlayerListMenu());
				break;
			case 2:
				PlayerListMenu.Page = 1;
				PlayerListMenu.ListType = 2;
				mc.displayGuiScreen(new PlayerListMenu());
				break;
			case 3:
				PlayerListMenu.Page = 1;
				PlayerListMenu.ListType = 3;
				mc.displayGuiScreen(new PlayerListMenu());
				break;
			case 4:
				PlayerListMenu.Page = 1;
				PlayerListMenu.ListType = 4;
				mc.displayGuiScreen(new PlayerListMenu());
				break;
			}
			
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(TEXTURE_MENU);
		
		this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	
	
	static class HomeMenuButton extends GuiButton {
		
		public HomeMenuButton(int buttonId, int x, int y, String text) {
			super(buttonId, x, y, 116, 14, "");
			this.text = text;
		}
		
		public HomeMenuButton() {
			super(-1, -1, -1, "");
		}
		
		public String text = "";
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible && !this.text.equals("")) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 0, 222 + (hover ? 14 : 0), 116, 14);
				
				if (this.text.equals(String.valueOf('\u00a7') + "f" + ModCore.mc().player.getName())) {this.drawTexturedModalRect(this.x + this.width - 11, this.y +3, 251, 248, 5, 8); this.drawTexturedModalRect(this.x + 7, this.y +3, 251, 248, 5, 8);}
				if (this.text.equals(String.valueOf('\u00a7') + "eParty")) {this.drawTexturedModalRect(this.x + this.width - 11, this.y +3, 231, 248, 5, 8); this.drawTexturedModalRect(this.x + 7, this.y +3, 231, 248, 5, 8);}
				if (this.text.equals(String.valueOf('\u00a7') + "aFriends")) {this.drawTexturedModalRect(this.x + this.width - 11, this.y +3, 241, 248, 5, 8); this.drawTexturedModalRect(this.x + 7, this.y +3, 241, 248, 5, 8);}
				if (this.text.equals(String.valueOf('\u00a7') + "bGuild")) {this.drawTexturedModalRect(this.x + this.width - 11, this.y +3, 246, 248, 5, 8); this.drawTexturedModalRect(this.x + 7, this.y +3, 246, 248, 5, 8);}
				if (this.text.equals("World")) {this.drawTexturedModalRect(this.x + this.width - 13, this.y +3, 236, 240, 8, 8); this.drawTexturedModalRect(this.x + 7, this.y +3, 236, 240, 8, 8);}
				
				this.drawCenteredString(mc.fontRenderer, text, this.x + (this.width/2), this.y + 3, Integer.parseInt("CACACA", 16));
				
			}
		}
	}
	
	static class PlayerButton extends GuiButton {
		
		public PlayerButton(int buttonId, int x, int y, String text) {
			super(buttonId, x, y, 116, 14, "");
			this.text = text;
		}
		
		public PlayerButton() {
			super(-1, -1, -1, "");
		}
		
		public String text = "";
		
		public boolean Self = false;
		public boolean Party = false;
		public boolean Favourite = false;
		public boolean Friend = false;
		public boolean Guild = false;
		public boolean World = false;
		
		public int Rank = 0;
		
		public boolean NameParseLength = false;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible && !this.text.equals("")) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 0, 222 + (hover ? 14 : 0), 116, 14);
				
				int IconOffset = 11;
				if (this.Rank > 0) {
					switch (this.Rank){
					case 1: this.drawTexturedModalRect(this.x + this.width - IconOffset - 4, this.y +6, 228, 243, 8, 2); IconOffset += 11; break;
					case 2: this.drawTexturedModalRect(this.x + this.width - IconOffset - 4, this.y +3, 228, 240, 8, 8); IconOffset += 11; break;
					case 3: this.drawTexturedModalRect(this.x + this.width - IconOffset - 5, this.y +3, 228, 225, 10, 8); IconOffset += 11; break;
					case 4: this.drawTexturedModalRect(this.x + this.width - IconOffset - 5, this.y +3, 228, 233, 10, 7); IconOffset += 11; break;
					case 5: this.drawTexturedModalRect(this.x + this.width - IconOffset - 5, this.y +3, 228, 233, 10, 7); IconOffset += 11; break;
					}
				}
				
				if (this.Guild) {this.drawTexturedModalRect(this.x + this.width - IconOffset, this.y +3, 246, 248, 5, 8); IconOffset += 6;}
				if (this.Favourite) {this.drawTexturedModalRect(this.x + this.width - IconOffset, this.y +3, 236, 248, 5, 8); IconOffset += 6;}else
				if (this.Friend) {this.drawTexturedModalRect(this.x + this.width - IconOffset, this.y +3, 241, 248, 5, 8); IconOffset += 6;}
				if (this.Party) {this.drawTexturedModalRect(this.x + this.width - IconOffset, this.y +3, 231, 248, 5, 8); IconOffset += 6;}
				if (this.Self) {this.drawTexturedModalRect(this.x + this.width - IconOffset, this.y +3, 251, 248, 5, 8); IconOffset += 6;}
				
				if (NameParseLength){
					int Length = ExpReference.GetMsgLength(text, 1.0f);
					while (Length > (111 - IconOffset)){
						text = text.substring(0, text.length()-1);
						Length = ExpReference.GetMsgLength(text, 1.0f);
					}
					NameParseLength = false;
				}
				
				this.drawString(mc.fontRenderer, text, this.x + 7, this.y + 3, Integer.parseInt("CACACA", 16));
				
			}
		}
	}
	
	static class KickButton extends GuiButton {
		
		public KickButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 14, 14, "");
		}
		
		public KickButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 84, 194 + (hover ? 14 : 0), 14, 14);
			}
		}
	}
	
	static class MsgButton extends GuiButton {
		
		public MsgButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public MsgButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +3, this.y +2, 144, 236 + (hover ? 10 : 0), 12, 10);
			}
		}
	}
	
	static class PartyButton extends GuiButton {
		
		public PartyButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public PartyButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +2, this.y +2, 156, 236 + (hover ? 10 : 0), 14, 10);
			}
		}
	}

	static class JoinButton extends GuiButton {
		
		public JoinButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public JoinButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +4, this.y +2, 170, 236 + (hover ? 10 : 0), 10, 10);
			}
		}
	}
	
	static class FavouriteButton extends GuiButton {
		
		public FavouriteButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public FavouriteButton() {
			super(-1, -1, -1, "");
		}
		
		public boolean Favourite = false;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +3, this.y +2, 180 + (Favourite ? 12 : 0), 236 + (hover ? 10 : 0), 12, 10);
			}
		}
	}
	
	static class FriendButton extends GuiButton {
		
		public FriendButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public FriendButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +5, this.y +2, 204, 236 + (hover ? 10 : 0), 8, 10);
			}
		}
	}
	
	static class PlayerInfoButton extends GuiButton {
		
		public PlayerInfoButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public PlayerInfoButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +5, this.y +2, 212, 236 + (hover ? 10 : 0), 8, 10);
			}
		}
	}
	
	static class GuildInfoButton extends GuiButton {
		
		public GuildInfoButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public GuildInfoButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +5, this.y +2, 220, 236 + (hover ? 10 : 0), 8, 10);
			}
		}
	}
	
	static class RankRecruitButton extends GuiButton {
		
		public RankRecruitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public RankRecruitButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x + 5, this.y +6, 228, 243, 8, 2);
			}
		}
	}

	static class RankRecruiterButton extends GuiButton {
		
		public RankRecruiterButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public RankRecruiterButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +5, this.y +3, 228, 240, 8, 8);
			}
		}
	}
	
	static class RankCaptainButton extends GuiButton {
		
		public RankCaptainButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public RankCaptainButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +4, this.y +3, 228, 225, 10, 8);
			}
		}
	}
	
	static class RankChiefButton extends GuiButton {
		
		public RankChiefButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 14, "");
		}
		
		public RankChiefButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 98, 194 + (hover ? 14 : 0), 18, 14);
				
				this.drawTexturedModalRect(this.x +4, this.y +3, 228, 233, 10, 7);
			}
		}
	}
	
	static class ConfirmButton extends GuiButton {
		
		public ConfirmButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 20, 14, "");
		}
		
		public ConfirmButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 116, 194 + (hover ? 14 : 0), 20, 14);
			}
		}
	}

	static class CancelButton extends GuiButton {
	
		public CancelButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 20, 14, "");
		}
		
		public CancelButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 136, 194 + (hover ? 14 : 0), 20, 14);
				
			}
		}
	}
	
	static class ChangePageButton extends GuiButton {
		private boolean right = true;
		
		public ChangePageButton(int buttonId, int x, int y, boolean right) {
			super(buttonId, x, y, 27, 14, "");
			this.right = right;
		}
		
		public ChangePageButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				int textureX = (this.right ? 28 : 0);
				int textureY = (hover ? 194 : 208);
				
				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 28, 14);
			}
		}
	}
	
	static class BackButton extends GuiButton {
		
		
		public BackButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 27, 14, "");
		}
		
		public BackButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				int textureX = 56;
				int textureY = (hover ? 194 : 208);
				
				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 28, 14);
			}
		}
	}
	
	static class ExitButton extends GuiButton {
		
		public ExitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 12, 12, "");
		}
		
		public ExitButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_MENU);
				
				this.drawTexturedModalRect(this.x, this.y, 244, 222 + (hover ? 12 : 0), 12, 12);
			}
		}
	}
	
	
	
	
	public static void ClearLists(){
		if (PlayersLoaded){
			PartyList.clear();
			FriendsList.clear();
			GuildList.clear();
			PlayerListMenu.PlayerList.clear();
			PlayersLoaded = false;
		}
	}
	
	
	// Off Main Thread \\
	public static void GatherPlayers(){
		CurrentWorld = "0";
		try {
			Collection<NetworkPlayerInfo> Tablist = Minecraft.getMinecraft().getConnection().getPlayerInfoMap();
			for (NetworkPlayerInfo TabSlot : Tablist) {
				String TabName = ModCore.mc().ingameGUI.getTabList().getPlayerName(TabSlot);
				if (TabName.contains("Global [")) {
					CurrentWorld = TabName.substring(TabName.indexOf("[") +1, TabName.indexOf("]"));
					if (Character.isDigit(CurrentWorld.charAt(0))){
						CurrentWorld = "WC" + CurrentWorld;
					}
					break;
				}
			}
		} catch (Exception ignored){}
		
		if (ExpReference.inGame()){
			PartyList.clear();
			try {
				Collection<NetworkPlayerInfo> Tablist = Minecraft.getMinecraft().getConnection().getPlayerInfoMap();
				for (NetworkPlayerInfo TabSlot : Tablist) {
					String TabName = ModCore.mc().ingameGUI.getTabList().getPlayerName(TabSlot);
					if (TabName.contains(String.valueOf('\u00a7') + 'e') && !TabName.contains(String.valueOf('\u00a7') + 'l') && !TabName.contains("[")) {
						TabName = TabName.substring(2).replace(String.valueOf('\u00a7') + "r", "");
						if (TabName.equals(ModCore.mc().player.getName())) PartyLeader = false;
						String[] Tmp = {"0", TabName, "false"};
						PartyList.add(Tmp);
					}else if (TabName.contains(String.valueOf('\u00a7') + 'c')){
						TabName = TabName.substring(2).replace(String.valueOf('\u00a7') + "r", "");
						if (TabName.equals(ModCore.mc().player.getName())) PartyLeader = true;
						String[] Tmp = {"0", TabName, "true"};
						PartyList.add(Tmp);
					}
				}
			} catch (Exception ignored){}
			
			ReadFriendsList();
			boolean NewFriends = false;
			for (String Friend : PlayerGlow.FriendsList){
				boolean Exists = false;
				for (String[] Friends : FriendsList){
					if (Friends[1].equals(Friend)) Exists = true;
					break;
				}
				if (!Exists){
					String[] Tmp = {"0", Friend, "false"};	// Favourite
					FriendsList.add(Tmp);
					NewFriends = true;
				}
			}
			if (NewFriends) {
				SaveFriendsList();
			}else{
				PlayerGlow.FriendsList.clear();
				for (String[] Player : FriendsList){
					PlayerGlow.FriendsList.add(Player[1]);
				}
			}
			
			GuildList.clear();
			GuildList.addAll(PlayerCollection.GetGuildList());
			PlayerGlow.GuildList.clear();
			for (String[] Player : GuildList){
				PlayerGlow.GuildList.add(Player[1]);
			}
			
		}else{
			ReadFriendsList();
			PlayerGlow.FriendsList.clear();
			for (String[] Player : FriendsList){
				PlayerGlow.FriendsList.add(Player[1]);
			}
			GuildList.clear();
			GuildList.addAll(PlayerCollection.GetGuildList());
			PlayerGlow.GuildList.clear();
			for (String[] Player : GuildList){
				PlayerGlow.GuildList.add(Player[1]);
			}
		}
		
		// Get online players
		PlayerCollection.CollectPlayers();
		
		// Sorting Markers
		int PartySameWorld = 0;
		int PartyOnline = 0;
		
		int FriendsFavourite = 0;
		int FriendsOnline = 0;
		
		int GuildOnline = 0;
		
		// Sort Online players
		for (List<String> World : PlayerCollection.Worlds){
			if (CurrentWorld.equals("0")){ 
				if (World.contains(ModCore.mc().player.getName())){
					CurrentWorld = World.get(0);
					WorldList.clear();
					for (int i=1;i<World.size();i++){
						String[] Tmp = {World.get(0), World.get(i), ""} ;
						WorldList.add(Tmp);
					}
				}
			}else if (CurrentWorld.equals(World.get(0))){
				WorldList.clear();
				for (int i=1;i<World.size();i++){
					String[] Tmp = {World.get(0), World.get(i), ""} ;
					WorldList.add(Tmp);
				}
			}
			// Party
			for (int i=0;i<PartyList.size();i++){
				if (PartyList.get(i)[1].length() == 14){
					for (int j=0;j<World.size();j++){
						if (World.get(j).length() >= 14){
							if (World.get(j).substring(0, 14).equals(PartyList.get(i)[1])){
								String[] Tmp = {World.get(0), World.get(j), PartyList.get(i)[2]};
								if (World.get(0).equals(CurrentWorld)){
									PartyList.add(PartySameWorld, Tmp);
									PartySameWorld++;
									PartyOnline++;
									i++;
								}else{
									PartyList.add(PartyOnline, Tmp);
									PartyOnline++;
									i++;
								}
							}
						}
					}
				}else{
					if (World.contains(PartyList.get(i)[1])){
						String[] Tmp = {World.get(0), PartyList.get(i)[1], PartyList.get(i)[2]};
						if (World.get(0).equals(CurrentWorld)){
							PartyList.add(PartySameWorld, Tmp);
							PartySameWorld++;
							PartyOnline++;
							i++;
						}else{
							PartyList.add(PartyOnline, Tmp);
							PartyOnline++;
							i++;
						}
					}
				}
			}
			// Friends
			for (int i=0;i<FriendsList.size();i++){
				if (World.contains(FriendsList.get(i)[1])){
					String[] Tmp = {World.get(0), FriendsList.get(i)[1], FriendsList.get(i)[2]};
					if (FriendsList.get(i)[2].equals("true")){
						FriendsList.add(FriendsFavourite, Tmp);
						FriendsFavourite++;
						FriendsOnline++;
						i++;
					}else{
						FriendsList.add(FriendsOnline, Tmp);
						FriendsOnline++;
						i++;
					}
				}
			}
			// Guild
			for (int i=0;i<GuildList.size();i++){
				if (World.contains(GuildList.get(i)[1])){
					String[] Tmp = {World.get(0), GuildList.get(i)[1], GuildList.get(i)[2], GuildList.get(i)[3], GuildList.get(i)[4]};
					for (int j=0;j<GuildOnline+1;j++){
						if (Integer.parseInt(GuildList.get(i)[2]) > Integer.parseInt(GuildList.get(j)[2]) || j == GuildOnline){
							GuildList.add(j, Tmp);
							GuildOnline++;
							i++;
							break;
						}
					}
				}
			}
		}
		
		// Sort Offline players
		for (int i=FriendsOnline;i<FriendsList.size();i++){
			if (FriendsList.get(i)[2].equals("true")){
				FriendsList.add(FriendsOnline, FriendsList.get(i));
				FriendsOnline++;
				i++;
			}
		}
		int GuildOffline = GuildOnline;
		for (int i=GuildOnline;i<GuildList.size();i++){
			for (int j=GuildOnline;j<GuildOffline+1;j++){
				if (Integer.parseInt(GuildList.get(i)[2]) > Integer.parseInt(GuildList.get(j)[2]) || j == GuildOffline){
					GuildList.add(j, GuildList.get(i));
					GuildOffline++;
					i++;
					break;
				}
			}
		}
		
		// Clear Unneeded player list
		PlayerCollection.Worlds.clear();
		
		// Duplicate removal
		for (int i=0;i<PartyList.size();i++){
			for (int j=i+1;j<PartyList.size();j++){
				if (PartyList.get(j)[1].equals(PartyList.get(i)[1])){
					PartyList.remove(j);
					j--;
				}
			}
		}
		for (int i=0;i<FriendsList.size();i++){
			for (int j=i+1;j<FriendsList.size();j++){
				if (FriendsList.get(j)[1].equals(FriendsList.get(i)[1])){
					FriendsList.remove(j);
					j--;
				}
			}
		}
		for (int i=0;i<GuildList.size();i++){
			for (int j=i+1;j<GuildList.size();j++){
				if (GuildList.get(j)[1].equals(GuildList.get(i)[1])){
					GuildList.remove(j);
					j--;
				}
			}
		}
		
		PlayersLoaded = true;
		PlayerHomeMenu.RefreshTimer.Reset();
		
		/*try{
			PrintWriter writer = new PrintWriter("config/Wynn Expansion/Player Dump.txt", "UTF-8");
			writer.println("Party Members:");
			for (String[] Player : PartyList){
				writer.println(Player[0] + " " + Player[1] + " " + Player[2]);
			}
			writer.println("");
			writer.println("Friends List:");
			for (String[] Player : FriendsList){
				writer.println(Player[0] + " " + Player[1] + " " + Player[2]);
			}
			writer.println("");
			writer.println("Guild List:");
			for (String[] Player : GuildList){
				writer.println(Player[0] + " " + Player[1] + " " + Player[2] + " " + Player[3] + " " + Player[4]);
			}
			writer.close();
		} catch (Exception ignore) {}*/
	}
	
	private static void ReadFriendsList(){
		FriendsList.clear();
		try{
			if (new File("config/Wynn Expansion/Friends.cfg").exists()){
				BufferedReader FileFriends = new BufferedReader(new FileReader("config/Wynn Expansion/Friends.cfg"));
				try {
					String Friend = FileFriends.readLine();
					
					while (Friend != null) {
						String[] Tmp = {"0", Friend.substring(0, Friend.indexOf(" - ")), Friend.substring(Friend.indexOf(" - ") +3)};
						if (Tmp[1].matches("[0-9a-zA-Z_]+")) {
							FriendsList.add(Tmp);
						}
						Friend = FileFriends.readLine();
					}
				} finally {
					FileFriends.close();
				}
			}
		}catch(Exception ignore){}
	}
	
	static void SaveFriendsList(){
		try{
			PrintWriter writer = new PrintWriter("config/Wynn Expansion/Friends.cfg", "UTF-8");
			for (String Friend : PlayerGlow.FriendsList){
				for (String[] Friends : FriendsList){
					if (Friend.equals(Friends[1])){
						writer.println(Friends[1] + " - " + Friends[2]);
						break;
					}
				}
			}
			writer.close();
		}catch(Exception ignore){}
	}
}
