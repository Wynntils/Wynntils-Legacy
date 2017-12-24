package com.wynndevs.expansion.QuestBook;

import com.wynndevs.ModCore;
import com.wynndevs.expansion.ExpReference;
import com.wynndevs.expansion.Misc.GuiScreenMod;
import com.wynndevs.expansion.Options.GuiSHCMWynnOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class GuiQuestBook extends GuiScreenMod {
	private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation(ExpReference.MOD_ID,"textures/gui/questbook.png");
	private static final ResourceLocation TEXTURE_BOOK_DISABLED = new ResourceLocation(ExpReference.MOD_ID,"textures/gui/questbook_disabled.png");
	private static final ResourceLocation TEXTURE_BOOK_DISABLED_2 = new ResourceLocation(ExpReference.MOD_ID,"textures/gui/questbook_disabled_2.png");
	
	private static ChangePageButton btnNextPage = new ChangePageButton();
	private static ChangePageButton btnPrevPage = new ChangePageButton();
	
	private static ChangePageButton btnNextTenPage = new ChangePageButton();
	private static ChangePageButton btnPrevTenPage = new ChangePageButton();
	
	private static MiscButton btnWiki = new MiscButton();
	private static MiscButton btnXyz = new MiscButton();
	
	private static MiscToggleTrackingButton btnTglTracking = new MiscToggleTrackingButton();
	
	private static OptionsButton btnOptions = new OptionsButton();
	private static ExitButton btnExit = new ExitButton();
	
	private Quest showedQuest;
	private int showedQuestIndex;
	
	public GuiQuestBook() {
		//QuestBook.ReloadBook();
		showedQuest = QuestBook.GetSelectedQuest();
		showedQuestIndex = QuestBook.GetSelectedQuestIndex();
	}
	
	@Override
	public void initGui() {
		btnNextPage = new ChangePageButton(0, (this.width / 2) + 25, 50, true, false);
		btnPrevPage = new ChangePageButton(1, (this.width / 2) - 52, 50, false, false);
		
		btnNextTenPage = new ChangePageButton(2, (this.width / 2) + 55, 50, true, true);
		btnPrevTenPage = new ChangePageButton(3, (this.width / 2) - 82, 50, false, true);
		
		//btnWiki = new MiscButton(4, (this.width / 2) - 112, 50, 0, 222);
		//btnXyz = new MiscButton(5, (this.width / 2) + 85, 50, 28, 222);
		
		btnWiki = new MiscButton(4, (this.width / 2) - 82, 50, 0, 222);
		btnXyz = new MiscButton(5, (this.width / 2) + 55, 50, 28, 222);
		
		btnTglTracking = new MiscToggleTrackingButton(6, (this.width / 2) - 115, 145);
		
		btnOptions = new OptionsButton(7, (this.width / 2) + 84, 15);
		btnExit = new ExitButton(-1, (this.width / 2) + 100, 15);
		
		//this.addButton(btnNextPage);
		//this.addButton(btnPrevPage);
		
		//this.addButton(btnNextTenPage);
		//this.addButton(btnPrevTenPage);

		this.addButton(btnWiki);
		this.addButton(btnXyz);

		this.addButton(btnTglTracking);

		this.addButton(btnOptions);
		this.addButton(btnExit);
		
		if (showedQuest != null) {
			btnWiki.visible = true;
			btnXyz.visible = true;
			btnTglTracking.visible = true;
		}else{
			btnWiki.visible = false;
			btnXyz.visible = false;
			btnTglTracking.visible = false;
		}

		super.initGui();
	}

	@Override
	public void updateScreen() {
		showedQuest = QuestBook.GetSelectedQuest();
		showedQuestIndex = QuestBook.GetSelectedQuestIndex();
		
		if (showedQuest != null) {
			btnXyz.enabled = QuestBook.XyzEnabled(showedQuest.getQuestDescription());
			
			btnWiki.visible = true;
			btnXyz.visible = true;
			btnTglTracking.visible = true;
		}else{
			btnWiki.visible = false;
			btnXyz.visible = false;
			btnTglTracking.visible = false;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (showedQuest == null){
			this.mc.getTextureManager().bindTexture(TEXTURE_BOOK_DISABLED);
			this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
			
			this.drawStringPlain(mc.fontRenderer, "Could not load quest.", (this.width / 2) - 80, 83, 1.14f, Integer.parseInt("CC5200", 16));
			
			this.drawSplitStringPlain(mc.fontRenderer, "Try reopening the quest from the default quest menu", (this.width / 2) - 117, 103, 240, 1.03f, 1.4f, Integer.parseInt("CC5200", 16));
		} else {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			
			this.mc.getTextureManager().bindTexture((showedQuest.getQuestStatus() != Quest.QuestStatus.FINISHED ? (showedQuest.getQuestLevel() <= ModCore.mc().player.experienceLevel ? TEXTURE_BOOK : TEXTURE_BOOK_DISABLED) : TEXTURE_BOOK_DISABLED_2));
			
			this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
			
			this.mc.getTextureManager().bindTexture(TEXTURE_BOOK);
			
			//this.drawCenteredStringPlain(mc.fontRenderer, String.format("%03d", (showedQuestIndex + 1)) + "/" + QuestBook.quests.size(), this.width / 2, 53, Integer.parseInt("858585", 16));
			
			this.drawStringPlain(mc.fontRenderer, showedQuest.getQuestName(), (this.width / 2) - 80, 83, 1.14f, Integer.parseInt("CC5200", 16));
			
			this.drawStringPlain(mc.fontRenderer, "Lv. " + Integer.toString(showedQuest.getQuestLevel()), (this.width / 2) - 117, 103, 1.03f, (ModCore.mc().player.experienceLevel >= showedQuest.getQuestLevel() ? (showedQuest.getQuestStatus().getName().equals("Finished") ? Integer.parseInt("239400", 16) : Integer.parseInt("64c700", 16)) : Integer.parseInt("FF1715", 16)));
			
			this.drawStringPlain(mc.fontRenderer, "Length: " + showedQuest.getQuestLength().getName(), (this.width / 2) - 112, 116, 1.03f, (showedQuest.getQuestStatus().getName().equals("Finished") && showedQuest.getQuestLength().getName().equals("Easy") ? Integer.parseInt("239400", 16) : showedQuest.getQuestLength().getColor()));
			
			//this.drawStringPlain(mc.fontRenderer, "Difficulty: " + showedQuest.getQuestDifficulty().getName(), (this.width / 2) - 112, 116, 1.03f, (showedQuest.getQuestStatus().getName().equals("Finished") && showedQuest.getQuestDifficulty().getName().equals("Easy") ? Integer.parseInt("239400", 16) : showedQuest.getQuestDifficulty().getColor()));
			
			this.drawStringPlain(mc.fontRenderer, "Status: " + showedQuest.getQuestStatus().getName(), (this.width / 2) - 107, 129, 1.03f, showedQuest.getQuestStatus().getColor());
			
			this.drawSplitStringPlain(mc.fontRenderer, showedQuest.getQuestDescription(), (this.width / 2) - 100, 148, 240, 0.9f, 1.4f, Integer.parseInt("858585", 16));
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			case 0:
				QuestBook.selectedQuestTracking = false;
				if (showedQuestIndex + 1 > QuestBook.quests.size() - 1) {
					QuestBook.selectedQuest = QuestBook.quests.get(0).getQuestName();
				} else {
					QuestBook.selectedQuest = QuestBook.quests.get(showedQuestIndex + 1).getQuestName();
				}
				break;
			case 1:
				QuestBook.selectedQuestTracking = false;
				if (showedQuestIndex - 1 < 0) {
					QuestBook.selectedQuest = QuestBook.quests.get(QuestBook.quests.size() - 1).getQuestName();
				} else {
					QuestBook.selectedQuest = QuestBook.quests.get(showedQuestIndex - 1).getQuestName();
				}
				break;
			case 2:
				QuestBook.selectedQuestTracking = false;
				if (showedQuestIndex + 10 > QuestBook.quests.size() - 1) {
					QuestBook.selectedQuest = QuestBook.quests.get(0).getQuestName();
				} else {
					QuestBook.selectedQuest = QuestBook.quests.get(showedQuestIndex + 10).getQuestName();
				}
				break;
			case 3:
				QuestBook.selectedQuestTracking = false;
				if (showedQuestIndex - 10 < 0) {
					QuestBook.selectedQuest = QuestBook.quests.get(QuestBook.quests.size() - 1).getQuestName();
				} else {
					QuestBook.selectedQuest = QuestBook.quests.get(showedQuestIndex - 10).getQuestName();
				}
				break;
			case 4:
				try {
					Desktop.getDesktop().browse(new URI("https://wiki.wynncraft.com/index.php?title="+ showedQuest.getQuestName().replace(' ', '_')));
				} catch (Exception ignored) {
				}
				break;
			case 5:
				String reader = "";
				String builder = "";
				int stage = -1;
				for (char chr : showedQuest.getQuestDescription().toCharArray()) {
					if (stage == -1) {
						reader += chr;
					} else {
						builder += chr;
					}

					if (reader.endsWith("[") && stage == -1) {
						builder += "[x:";
						stage = 0;
					}
					if (builder.endsWith(",") && stage != -1) {
						if (stage == 0) {
							builder += "y:";
						}
						if (stage == 1) {
							builder += "z:";
						}
						stage++;
					}
					if (builder.endsWith("]") && stage == 2) {
						mc.player.sendChatMessage(builder);
						builder = "";
						stage = -1;
					}
				}
				QuestBook.xyzDelay.Reset();
				break;
			case 6:
				QuestBook.selectedQuestDescription = QuestBook.GetSelectedQuest().getQuestDescription();
				QuestBook.selectedQuestTracking = !QuestBook.selectedQuestTracking;
				break;
			case 7:
				mc.displayGuiScreen(new GuiSHCMWynnOptions());
			}
		}

		this.updateScreen();
	}

	@Override
	protected String GetButtonTooltip(int buttonId) {
		switch (buttonId) {
		case 0:
			return "Next Quest";
		case 1:
			return "Previous Quest";
		case 2:
			return "Next 10th Quest";
		case 3:
			return "Previous 10th Quest";
		case 4:
			return "Open the WynnWiki page for \"" + showedQuest.getQuestName() + "\"";
		case 5:
			return QuestBook.xyzDelay.Passed() ? "Say aloud coordinates that are in the quest description" : "Cooldown: " + QuestBook.xyzDelay.getSecondsLeft() + "s";
		case 6:
			return QuestBook.selectedQuestTracking ? "Hide Quest Description on the top-left of the screen" : "Show Quest Description on the top-left of the screen";
		case 7:
			return "Mod Options";
		}
		return null;
	}

	static class ChangePageButton extends GuiButton {
		private boolean right = true;
		private boolean ten = false;

		public ChangePageButton(int buttonId, int x, int y, boolean right, boolean ten) {
			super(buttonId, x, y, 27, 14, "");
			this.right = right;
			this.ten = ten;
		}

		public ChangePageButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_BOOK);

				int textureX = (this.right ? 28 : 0) + (ten ? 56 : 0);
				int textureY = (hover ? 194 : 208);

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 28, 14);
			}
		}
	}

	static class MiscButton extends GuiButton {
		private int textureX;
		private int textureY;

		public MiscButton(int buttonId, int x, int y, int textureX, int textureY) {
			super(buttonId, x, y, 27, 14, "");
			this.textureX = textureX;
			this.textureY = textureY;
		}

		public MiscButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y
						&& mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_BOOK);

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY + (hover ? 0 : 14), 28, 14);
			}
		}
	}

	static class MiscToggleTrackingButton extends GuiButton {

		public MiscToggleTrackingButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 8, 14, "");
		}

		public MiscToggleTrackingButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_BOOK);

				this.drawTexturedModalRect(this.x, this.y, 240 + (QuestBook.selectedQuestTracking ? 8 : 0), 194 + (hover ? 14 : 0), 8, 14);
			}
		}
	}

	static class OptionsButton extends GuiButton {

		public OptionsButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 14, 14, "");
		}

		public OptionsButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_BOOK);

				this.drawTexturedModalRect(this.x, this.y, 226, 194 + (hover ? 14 : 0), 14, 14);
			}
		}
	}
	
	static class ExitButton extends GuiButton {
		
		public ExitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 14, 14, "");
		}
		
		public ExitButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_BOOK);
				
				this.drawTexturedModalRect(this.x, this.y, 242, 222 + (hover ? 14 : 0), 14, 14);
			}
		}
	}
}
