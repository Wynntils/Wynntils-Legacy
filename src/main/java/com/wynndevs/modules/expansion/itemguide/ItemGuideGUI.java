package com.wynndevs.modules.expansion.itemguide;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.Delay;
import com.wynndevs.modules.expansion.misc.GuiScreenMod;
import com.wynndevs.modules.expansion.webapi.ItemDB;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemGuideGUI extends GuiScreenMod {
	
	public static boolean SearchBoxActiveDefault = false;
	public static boolean ItemGuideShowLore = false;
	public static boolean ItemGuideBoxRarity = false;
	public static boolean ItemGuidePurple = false;
	
	private static int Width = 0;
	private static int Height = 0;
	private static int xPos = 0;
	private static int yPos = 0;
	
	private static int Colums = 0;
	private static int Rows = 0;
	private static int ItemNameWidth = 0;
	private static int SidebarWidth = 0;
	private static int Page = 0;
	private static int MaxPage = 0;
	
	private static boolean SearchBoxActive = false;
	private static boolean ItemComparing = false;
	
	private static int Background = 0x66C4AB79; //BFA269 //CCA55D
	private static int BorderPrimary = 0xffD88B20; //AF7500 //D69E37 //CC8A28
	private static int BorderSecondary = 0xff966915; //724C00
	private static int BoxBackground = 0x66D8BC86; //E5BA69
	private static int LineGray = 0xffA08349;
	
	private static List<ItemButton> ItemButtonList = new ArrayList<ItemButton>();
	private static SearchBox SearchBar = new SearchBox();
	private static List<ItemFilterButton> ItemFilterButtonList = new ArrayList<ItemFilterButton>();
	private static List<ClassFilterButton> ClassFilterButtonList = new ArrayList<ClassFilterButton>();
	
	@Override
	public void initGui() {
		
		//Width = (this.width/5) * 3;
		//Height = (this.height/5) * 3;
		
		if (ItemGuidePurple) {
			Background = 0x665000A0; //0x66450087; //0x99b591e1; //0x997800df;
			BorderPrimary = 0xff7637BF; //0xff6200C4; //0xff450087; //0xffb753fd; //0xff8900ff;
			BorderSecondary = 0xff3B0077; //0xff6f37ab; //0xff5b00a9;
			BoxBackground = 0x669968d0; //0x999231e5;
			LineGray = 0xff8900ff;
		}else{
			Background = 0x99B09665; //BFA269 //CCA55D
			BorderPrimary = 0xffD88B20; //AF7500 //D69E37 //CC8A28
			BorderSecondary = 0xff966915; //724C00
			BoxBackground = 0x99D8BC86; //E5BA69
			LineGray = 0xffA08349;
		}
		
		SearchBoxActive = SearchBoxActiveDefault;
		
		ItemNameWidth = 130; // 100
		SidebarWidth = 104;
		ItemComparing = false;
		
		Colums = 4; // 4
		Rows = 8; // 7
		
		Colums = (int) Math.floor((this.width - (SidebarWidth + 15 + (ItemComparing ? 85 : 0))) / (ItemNameWidth+10));
		if (Colums > 4) Colums = 4;
		Width = SidebarWidth + 15 + (Colums * (ItemNameWidth+10)) + (ItemComparing ? 85 : 0);
		
		Rows = (int) Math.floor((this.height - 66) / (27));
		if (Rows > 8) Rows = 8;
		Height = 66 + (Rows * 27);
		
		
		
		xPos = (this.width/2) - (Width/2);
		yPos = (this.height/2) - (Height/2);
		
		
		//System.out.println("Width: " + Width + " Height:" + Height);
		//ModCore.mc().gameSettings.guiScale;
		
		ItemButtonList.clear();
		for (int i=0;i<Rows;i++) {
			for (int j=0;j<Colums;j++) {
				ItemButtonList.add(new ItemButton(((i*Colums)+j), 0, 0));
			}
		}
		
		ItemFilterButtonList.clear();
		for (int i=0;i<11;i++) {
			ItemFilterButtonList.add(new ItemFilterButton(1250 + i, 0, 0, i));
		}
		
		ClassFilterButtonList.clear();
		for (int i=0;i<4;i++) {
			ClassFilterButtonList.add(new ClassFilterButton(1300 + i, 0, 0, i));
		}
		
		this.addButton(SearchBar = new SearchBox(1000, xPos + SidebarWidth + 8, yPos +8, xPos +(Width/5)*4, yPos +32));
		this.addButton(new PageButton(1001, (this.width/2) + 30, yPos + Height - 26, true));
		this.addButton(new PageButton(1002, (this.width/2) - 70, yPos + Height - 26, false));
		this.addButton(new ExitButton(1003, xPos + Width - 28, yPos + 6));
		
		for (ItemButton ItemButton : ItemButtonList) {
			this.addButton(ItemButton);
		}
		
		for (ItemFilterButton ItemFilterButton : ItemFilterButtonList) {
			this.addButton(ItemFilterButton);
		}
		
		for (ClassFilterButton Button : ClassFilterButtonList) {
			this.addButton(Button);
		}
		
		if (ItemSE.ItemSE.isEmpty()) ItemSE.CreateDB();
		MaxPage = (int) Math.ceil((float) (ItemSE.ItemSE.size()) / (float) (Rows * Colums));
		if (MaxPage == 0) MaxPage = 1;
		Page = 1;
		
		RedrawButtons();
	}
	
	@Override
	public void updateScreen() {
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		DrawBackground(xPos, yPos, xPos + Width, yPos + Height);
		
		drawCenteredString(this.fontRenderer, new DecimalFormat("000").format(Page) + "/" + new DecimalFormat("000").format(MaxPage), this.width/2, yPos + Height - 20, 1.25f, 0xffffce00); //0xffffce00
		
		/*for (int i=0;i<Rows;i++) {
			for (int j=0;j<Colums;j++) {
				DrawItemBox(xPos +85 + (j*(ItemNameWidth +10)), yPos +40 + (i*27));
			}
		}*/
		
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = -20.0F;
		this.itemRender.zLevel = -20.0F;
		for (ItemButton Button : ItemButtonList){
			if (Button.visible) {
				this.itemRender.renderItemIntoGUI(Button.ItemIcon, Button.x + 3, Button.y + 3);
			}
		}
		
		for (ItemFilterButton Button : ItemFilterButtonList){
			if (Button.visible) {
				switch (Button.ItemFilterID) {
					case 0: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.BOW), Button.x+2, Button.y+2); break;
					case 1: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.IRON_SHOVEL), Button.x+2, Button.y+2); break;
					case 2: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.STICK), Button.x+2, Button.y+2); break;
					case 3: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.SHEARS), Button.x+2, Button.y+2); break;
					case 4: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.LEATHER_HELMET), Button.x+2, Button.y+2); break;
					case 5: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.LEATHER_CHESTPLATE), Button.x+2, Button.y+2); break;
					case 6: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.LEATHER_LEGGINGS), Button.x+2, Button.y+2); break;
					case 7: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.LEATHER_BOOTS), Button.x+2, Button.y+2); break;
					case 8: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.STAINED_GLASS), Button.x+2, Button.y+2); break;
					case 9: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.SPRUCE_FENCE), Button.x+2, Button.y+2); break;
					case 10: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.GLASS_PANE), Button.x+2, Button.y+2); break;
					case 11: this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), Button.x+2, Button.y+2); break;
				}
			}
		}
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GlStateManager.disableLighting();
		
		this.zLevel = 200.0F;
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		
	}
	
	public void RedrawButtons() {
		
		for (int i=0;i<Rows;i++) {
			for (int j=0;j<Colums;j++) {
				if (((i*Colums)+j) + (Rows * Colums * (Page-1)) < ItemSE.ItemSE.size()) {
					(ItemButtonList.get((i*Colums)+j)).ItemName = ItemDB.ItemDB.get(ItemSE.ItemSE.get(((i*Colums)+j) + (Rows * Colums * (Page-1)))).GetColouredName();
					(ItemButtonList.get((i*Colums)+j)).Tooltip = ItemDB.ItemDB.get(ItemSE.ItemSE.get(((i*Colums)+j) + (Rows * Colums * (Page-1)))).GetTooltip(ItemGuideShowLore, true);
					(ItemButtonList.get((i*Colums)+j)).ItemIcon = ItemDB.ItemDB.get(ItemSE.ItemSE.get(((i*Colums)+j) + (Rows * Colums * (Page-1)))).GetStaticMaterial();
					if (ItemGuideBoxRarity) (ItemButtonList.get((i*Colums)+j)).ItemRarity = ItemDB.ItemDB.get(ItemSE.ItemSE.get(((i*Colums)+j) + (Rows * Colums * (Page-1)))).GetRarity();
					(ItemButtonList.get((i*Colums)+j)).x = xPos + SidebarWidth + 10 + (j*(ItemNameWidth +10));
					(ItemButtonList.get((i*Colums)+j)).y = yPos + 40 + (i*27);
					(ItemButtonList.get((i*Colums)+j)).Recalculate = true;
					(ItemButtonList.get((i*Colums)+j)).visible = true;
				}else{
					(ItemButtonList.get((i*Colums)+j)).visible = false;
				}
			}
		}
		
		
		for (int i=0;i<=(int) Math.ceil(ItemFilterButtonList.size() / 4);i++) {
			for (int j=0;j<4 && ItemFilterButtonList.size()>(i*4)+j;j++) {
				ItemFilterButtonList.get((i*4) + j).x = xPos + 7 + (j*24);
				ItemFilterButtonList.get((i*4) + j).y = yPos + 27 + (i*24);
			}
		}
		for (int i=0;i<=(int) Math.ceil(ClassFilterButtonList.size() / 2);i++) {
			for (int j=0;j<2 && ClassFilterButtonList.size()>(i*2)+j;j++) {
				ClassFilterButtonList.get((i*2) + j).x = xPos + 7 + (j*48);
				ClassFilterButtonList.get((i*2) + j).y = ItemFilterButtonList.get(ItemFilterButtonList.size()-1).y + ItemFilterButtonList.get(ItemFilterButtonList.size()-1).height + 8 + (i*24);
			}
		}
	}
	
	private static void RecalcPage() {
		MaxPage = (int) Math.ceil((float) (ItemSE.ItemSE.size()) / (float) (Rows * Colums));
		if (MaxPage == 0) MaxPage = 1;
		if (Page > MaxPage) Page = 1;
	}
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// Esc > Close
		if (keyCode == 1){
			if (SearchBoxActive) {
				SearchBoxActive = false;
				SearchBar.Recalculate = true;
			}else{
				this.mc.displayGuiScreen(null);
				
				if (this.mc.currentScreen == null){
					this.mc.setIngameFocus();
				}
			}
		}else{
			if (SearchBoxActive) {
				if ((keyCode == 14) && !SearchBar.SearchField.equals("")) {
					SearchBar.SearchField = SearchBar.SearchField.substring(0, SearchBar.SearchField.length()-1);
					SearchBar.Recalculate = true;
				}else if (keyCode == 211) {
					SearchBar.SearchField = "";
					SearchBar.Recalculate = true;
				}else if (keyCode == 28) {
					StartSearch(SearchBar.SearchField);
				}else if (String.valueOf(typedChar).matches("[0-9a-zA-Z '-]")){
					SearchBar.SearchField = SearchBar.SearchField + String.valueOf(typedChar);
					SearchBar.Recalculate = true;
				}else{
					//System.out.println("KeyCode " + keyCode);
				}
			}
		}
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 1000) {SearchBoxActive = !SearchBoxActive; SearchBar.Recalculate = true;} else {SearchBoxActive = false; SearchBar.Recalculate = true;}
		switch (button.id) {
		case 1001: Page++; if (Page > MaxPage) Page = 1; RedrawButtons(); break;
		case 1002: Page--; if (Page < 1) Page = MaxPage; RedrawButtons(); break;
		case 1003: this.mc.displayGuiScreen(null); break;
		
		case 1250: ItemSE.ItemTypeFilter[0] = !ItemSE.ItemTypeFilter[0]; StartSearch(SearchBar.SearchField); break;
		case 1251: ItemSE.ItemTypeFilter[1] = !ItemSE.ItemTypeFilter[1]; StartSearch(SearchBar.SearchField); break;
		case 1252: ItemSE.ItemTypeFilter[2] = !ItemSE.ItemTypeFilter[2]; StartSearch(SearchBar.SearchField); break;
		case 1253: ItemSE.ItemTypeFilter[3] = !ItemSE.ItemTypeFilter[3]; StartSearch(SearchBar.SearchField); break;
		case 1254: ItemSE.ItemTypeFilter[4] = !ItemSE.ItemTypeFilter[4]; StartSearch(SearchBar.SearchField); break;
		case 1255: ItemSE.ItemTypeFilter[5] = !ItemSE.ItemTypeFilter[5]; StartSearch(SearchBar.SearchField); break;
		case 1256: ItemSE.ItemTypeFilter[6] = !ItemSE.ItemTypeFilter[6]; StartSearch(SearchBar.SearchField); break;
		case 1257: ItemSE.ItemTypeFilter[7] = !ItemSE.ItemTypeFilter[7]; StartSearch(SearchBar.SearchField); break;
		case 1258: ItemSE.ItemTypeFilter[8] = !ItemSE.ItemTypeFilter[8]; StartSearch(SearchBar.SearchField); break;
		case 1259: ItemSE.ItemTypeFilter[9] = !ItemSE.ItemTypeFilter[9]; StartSearch(SearchBar.SearchField); break;
		case 1260: ItemSE.ItemTypeFilter[10] = !ItemSE.ItemTypeFilter[10]; StartSearch(SearchBar.SearchField); break;
		case 1261: ItemSE.ItemTypeFilter[11] = !ItemSE.ItemTypeFilter[11]; StartSearch(SearchBar.SearchField); break;
		
		case 1300: ItemSE.ItemClassFilter[0] = !ItemSE.ItemClassFilter[0]; StartSearch(SearchBar.SearchField); break;
		case 1301: ItemSE.ItemClassFilter[1] = !ItemSE.ItemClassFilter[1]; StartSearch(SearchBar.SearchField); break;
		case 1302: ItemSE.ItemClassFilter[2] = !ItemSE.ItemClassFilter[2]; StartSearch(SearchBar.SearchField); break;
		case 1303: ItemSE.ItemClassFilter[3] = !ItemSE.ItemClassFilter[3]; StartSearch(SearchBar.SearchField); break;
		}
	}
	
	@Override
	protected String GetButtonTooltip(int buttonId) {
		if (buttonId >= 0 && buttonId < ItemButtonList.size()) return ItemButtonList.get(buttonId).Tooltip;
		//if (buttonId == 1000) return "Click to select or deselect box for input/nPress Enter to search/nPress Backspace to remove last typed charicter/nPress Delete to clear search box";
		//if (buttonId == 1001) return "Next Page";
		//if (buttonId == 1002) return "Previous Page";
		switch (buttonId) {
		case 1000: return "Click to select or deselect box for input/nPress Enter to search/nPress Backspace to remove last typed charicter/nPress Delete to clear search box";
		case 1001: return "Next Page";
		case 1002: return "Previous Page";
		
		case 1250: return "Bow";
		case 1251: return "Spear";
		case 1252: return "Wand";
		case 1253: return "Dagger";
		case 1254: return "Helmet";
		case 1255: return "Chestplate";
		case 1256: return "Leggings";
		case 1257: return "Boots";
		case 1258: return "Ring";
		case 1259: return "Bracelet";
		case 1260: return "Necklace";
		case 1261: return "Unknown";
		
		case 1300: return "Archer/Hunter";
		case 1301: return "Warrior/Knight";
		case 1302: return "Mage/Dark Wizard";
		case 1303: return "Assassin/Ninja";
		}
		return null;
	}
	
	private void StartSearch(String Search) {
		ItemSE.Search(Search);
		RecalcPage();
		RedrawButtons();
		SearchBoxActive = false;
		SearchBar.Recalculate = true;
	}
	
	private static void DrawBackground(int x, int y, int X, int Y) {
		DrawHorzLine(y, x, X-2, -1);
		DrawVertLine(x, y+2, Y-2, -1);
		DrawVertLine(X-4, y+2, Y, -1);
		DrawHorzLine(Y-4, x+2, X-2, -1);
		
		drawRect(x +4, y +4, X -4, Y -4, Background);
		
		DrawVertLine(x+SidebarWidth, y+2, Y-4, -1);
		DrawHorzLine(y+20, x+2, x+SidebarWidth, -1);
		
		//DrawBox(x+83, y+8, x + (Width/5)*3, y+32);
		//DrawGrayLine(y+24, x+85, x + (Width/5)*3);
		
		if (ItemComparing) {
			drawRect(x + ((ItemNameWidth+10)*Colums) + 89, y +44, X -4, Y -4, BoxBackground);
			DrawHorzLine(y+40, x + ((ItemNameWidth+10)*Colums) + 85, X-4, -1);
			DrawVertLine(x + ((ItemNameWidth+10)*Colums) + 85, y+42, Y-4, -1);
			DrawHorzLine(y+((Height-40)/2)+38, x + ((ItemNameWidth+10)*Colums) + 87, X-4, -1);
		}
	}
	
	private static void DrawItemBox(int x, int y, int Width, int Height, int Colour, boolean Selected) {
		int X1 = x + Height;
		int Y = y + Height;
		int X2 = x + Width;
		
		int[] Colours = new int[]{BorderPrimary, BorderSecondary};
		switch (Colour) {
			case -1: Colours[0] = BorderPrimary; Colours[1] = BorderSecondary; break;
			case 0: Colours[0] = 0xffBFBFBF; Colours[1] = 0xff2F2F2F; break;
			case 1: Colours[0] = 0xffBDBD51; Colours[1] = 0xff2F2F14; break;
			case 2: Colours[0] = 0xffB447B4; Colours[1] = 0xff2C112C; break;
			case 3: Colours[0] = 0xff4DB9B9; Colours[1] = 0xff132E2E; break;
			case 4: Colours[0] = 0xff740874; Colours[1] = 0xff1C021C; break;
			case 5: Colours[0] = 0xff4BB84B; Colours[1] = 0xff132D13; break;
		}
		drawRect(X1-2, y+2, X1, y+3, Colours[1]);
		drawRect(X1-2, Y-5, X1, Y, Colours[1]);
		drawRect(x+2, Y-2, X1, Y, Colours[1]);
		
		drawRect(x, y, X1-2, y+2, Colours[0]);
		drawRect(x, y+2, x+2, Y-2, Colours[0]);
		drawRect(x, Y-4, X1-2, Y-2, Colours[0]);
		drawRect(X1-4, y+2, X1-2, Y-2, Colours[0]);
		
		drawRect(x+2, y+2, X1-4, Y-4, (Selected ? BoxBackground : Background));
		
		
		drawRect(X2-2, y+5, X2, Y-5, Colours[1]);
		drawRect(X1, Y-5, X2, Y-3, Colours[1]);
		
		drawRect(X1-2, y+3, X2-2, y+5, Colours[0]);
		drawRect(X1-2, Y-7, X2-2, Y-5, Colours[0]);
		drawRect(X2-4, y+5, X2-2, Y-5, Colours[0]);
		
		drawRect(X1-2, y+5, X2-4, Y-7, (Selected ? BoxBackground : Background));
	}
	
	private static void DrawBox(int x, int y, int Width, int Height, int Colour, boolean Selected) {
		int X = x + Width;
		int Y = y + Height;
		
		int[] Colours = new int[]{BorderPrimary, BorderSecondary};
		switch (Colour) {
			case -1: Colours[0] = BorderPrimary; Colours[1] = BorderSecondary; break;
			case 0: Colours[0] = 0xffBFBFBF; Colours[1] = 0xff2F2F2F; break;
			case 1: Colours[0] = 0xffBDBD51; Colours[1] = 0xff2F2F14; break;
			case 2: Colours[0] = 0xffB447B4; Colours[1] = 0xff2C112C; break;
			case 3: Colours[0] = 0xff4DB9B9; Colours[1] = 0xff132E2E; break;
			case 4: Colours[0] = 0xff740874; Colours[1] = 0xff1C021C; break;
			case 5: Colours[0] = 0xff4BB84B; Colours[1] = 0xff132D13; break;
		}
		drawRect(X-2, y+2, X, Y, Colours[1]);
		drawRect(x+2, Y-2, X, Y, Colours[1]);
		
		drawRect(x, y, X-2, y+2, Colours[0]);
		drawRect(x, y+2, x+2, Y-2, Colours[0]);
		drawRect(x, Y-4, X-2, Y-2, Colours[0]);
		drawRect(X-4, y+2, X-2, Y-2, Colours[0]);
		
		drawRect(x+2, y+2, X-4, Y-4, (Selected ? BoxBackground : Background));
	}
	
	private static void DrawVertLine(int x, int y, int Y, int Colour) {
		switch (Colour) {
		case -1:
			drawRect(x, y, x +2, Y, BorderPrimary);
			drawRect(x +2, y, x +4, Y, BorderSecondary);
			break;
		case 0:
			drawRect(x, y, x +2, Y, 0xffBFBFBF);
			drawRect(x +2, y, x +4, Y, 0xff2F2F2F);
			break;
		case 1:
			drawRect(x, y, x +2, Y, 0xffBDBD51);
			drawRect(x +2, y, x +4, Y, 0xff2F2F14);
			break;
		case 2:
			drawRect(x, y, x +2, Y, 0xffB447B4);
			drawRect(x +2, y, x +4, Y, 0xff2C112C);
			break;
		case 3:
			drawRect(x, y, x +2, Y, 0xff4DB9B9);
			drawRect(x +2, y, x +4, Y, 0xff132E2E);
			break;
		case 4:
			drawRect(x, y, x +2, Y, 0xff740874);
			drawRect(x +2, y, x +4, Y, 0xff1C021C);
			break;
		case 5:
			drawRect(x, y, x +2, Y, 0xff4BB84B);
			drawRect(x +2, y, x +4, Y, 0xff132D13);
			break;
		}
	}
	
	private static void DrawHorzLine(int y, int x, int X, int Colour) {
		switch (Colour) {
		case -1:
			drawRect(x, y, X, y +2, BorderPrimary);
			drawRect(x, y +2, X, y +4, BorderSecondary);
			break;
		case 0:
			drawRect(x, y, X, y +2, 0xffBFBFBF);
			drawRect(x, y +2, X, y +4, 0xff2F2F2F);
			break;
		case 1:
			drawRect(x, y, X, y +2, 0xffBBBB5C);
			drawRect(x, y +2, X, y +4, 0xff2E2E16);
			break;
		case 2:
			drawRect(x, y, X, y +2, 0xffAD4DAD);
			drawRect(x, y +2, X, y +4, 0xff2B132B);
			break;
		case 3:
			drawRect(x, y, X, y +2, 0xff56B5B5);
			drawRect(x, y +2, X, y +4, 0xff152D2D);
			break;
		case 4:
			drawRect(x, y, X, y +2, 0xff6D0D6D);
			drawRect(x, y +2, X, y +4, 0xff1B031B);
			break;
		case 5:
			drawRect(x, y, X, y +2, 0xff53B353);
			drawRect(x, y +2, X, y +4, 0xff142C14);
			break;
		}
	}
	
	private class ItemButton extends GuiButton {
		
		public ItemButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 1, 24, "");
		}

		public ItemButton() {
			super(-1, -1, -1, "");
		}
		
		public String ItemName = "";
		public String Tooltip = "";
		public int ItemRarity = -1;
		public ItemStack ItemIcon = null;
		public boolean Recalculate = false;
		
		private String DisplayName = "";
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				if (Recalculate) {
					this.width = ItemNameWidth+4;
					DisplayName = ItemName;
					int Length = ExpReference.getMsgLength(DisplayName, 1.0f);
					while (Length > ItemNameWidth-26){
						DisplayName = DisplayName.substring(0, DisplayName.length()-1);
						Length = ExpReference.getMsgLength(DisplayName, 1.0f);
					}
					Recalculate = false;
				}
				
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				DrawItemBox(this.x, this.y, ItemNameWidth, 24, this.ItemRarity, hover);
				drawString(ModCore.mc().fontRenderer, DisplayName, this.x+25, this.y+8, 0x99ffffff);
			}
		}
	}
	
	private static class SearchBox extends GuiButton {
		
		public SearchBox(int buttonId, int x, int y, int X, int Y) {
			super(buttonId, x, y, 0, 0, "");
			this.width = X-x;
			this.height = Y-y;
		}
		
		public SearchBox() {
			super(-1, -1, -1, "");
		}
		
		public String SearchField = "";
		public boolean Recalculate = false;
		
		private String SearchDisplay = "";
		private Delay Blinking = new Delay(0.5f, true);
		private boolean Blink = false;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				if (Recalculate) {
					if (SearchBoxActive) {
						SearchDisplay = SearchField;
						int Length = ExpReference.getMsgLength(SearchDisplay, 1.25f);
						while (Length > this.width- ExpReference.getMsgLength("_", 1.25f)-12){
							SearchDisplay = SearchDisplay.substring(1, SearchDisplay.length());
							Length = ExpReference.getMsgLength(SearchDisplay, 1.25f);
						}
						Blink = true;
						Blinking.Reset();
					}else{
						SearchDisplay = SearchField;
						int Length = ExpReference.getMsgLength(SearchDisplay, 1.25f);
						while (Length > this.width-12){
							SearchDisplay = SearchDisplay.substring(0, SearchDisplay.length()-1);
							Length = ExpReference.getMsgLength(SearchDisplay, 1.25f);
						}
					}
					Recalculate = false;
				}
				
				if (Blinking.Passed()) {
					Blink = !Blink;
				}
				
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				DrawHorzLine(this.y, this.x, this.x+this.width-2, -1);
				DrawVertLine(this.x, this.y+2, this.y+this.height-2, -1);
				DrawVertLine(this.x+this.width-4, this.y+2, this.y+this.height, -1);
				DrawHorzLine(this.y+this.height-4, this.x+2, this.x+this.width-2, -1);
				
				drawRect(this.x +4, this.y +4, this.x+this.width-4, this.y+this.height-4, (hover ? BoxBackground : Background));
				
				
				//DrawBox(this.x, this.y, this.width, this.height, -1, hover);
				
				drawString(mc.fontRenderer, (SearchBoxActive || !SearchField.equals("") ? SearchDisplay + (SearchBoxActive && Blink ? "_" : "") : "Search"), this.x+6, this.y + 7, 1.25f, (SearchBoxActive || hover ? 0xffffffff : 0xffdddddd));
			}
		}
		
		public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
			GL11.glScalef(size,size,size);
			float mSize = (float)Math.pow(size,-1);
			this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
			GL11.glScalef(mSize,mSize,mSize);
		}
	}
	
	private static class ItemFilterButton extends GuiButton {
		public ItemFilterButton(int buttonId, int x, int y, int ItemFilter) {
			super(buttonId, x, y, 22, 22, "");
			ItemFilterID = ItemFilter;
		}
		
		public ItemFilterButton() {
			super(-1, -1, -1, "");
		}
		
		int ItemFilterID = 0;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				DrawBox(this.x, this.y, this.width, this.height, -1, ItemSE.ItemTypeFilter[ItemFilterID] ^ hover);
			}
		}
	}
	
	private static class ClassFilterButton extends GuiButton {
		public ClassFilterButton(int buttonId, int x, int y, int ItemFilter) {
			super(buttonId, x, y, 46, 22, "");
			ClassFilterID = ItemFilter;
		}
		
		public ClassFilterButton() {
			super(-1, -1, -1, "");
		}
		
		int ClassFilterID = 0;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				DrawHorzLine(this.y, this.x, this.x+this.width-2, -1);
				DrawVertLine(this.x, this.y+2, this.y+this.height-2, -1);
				DrawVertLine(this.x+this.width-4, this.y+2, this.y+this.height, -1);
				DrawHorzLine(this.y+this.height-4, this.x+2, this.x+this.width-2, -1);
				
				drawRect(this.x +4, this.y +4, this.x+this.width-4, this.y+this.height-4, (ItemSE.ItemClassFilter[ClassFilterID] ^ hover ? BoxBackground : Background));
				
				switch (ClassFilterID) {
				case 0: drawCenteredString(mc.fontRenderer, "Ar/Hu", this.x + (this.width/2), this.y + 7, 1.2f, (hover ? 0xffce9900 : 0xffffce00)); break;
				case 1: drawCenteredString(mc.fontRenderer, "Wa/Kn", this.x + (this.width/2), this.y + 7, 1.2f, (hover ? 0xffce9900 : 0xffffce00)); break;
				case 2: drawCenteredString(mc.fontRenderer, "Ma/Da", this.x + (this.width/2), this.y + 7, 1.2f, (hover ? 0xffce9900 : 0xffffce00)); break;
				case 3: drawCenteredString(mc.fontRenderer, "As/Ni", this.x + (this.width/2), this.y + 7, 1.2f, (hover ? 0xffce9900 : 0xffffce00)); break;
				}
			}
		}
		
		public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
			GL11.glScalef(size,size,size);
			float mSize = (float)Math.pow(size,-1);
			this.drawCenteredString(fontRendererIn, text, (int) Math.floor(x / size), (int) Math.floor(y / size), color);
			GL11.glScalef(mSize,mSize,mSize);
		}
	}
	
	private static class PageButton extends GuiButton {
		public PageButton(int buttonId, int x, int y, boolean Next) {
			super(buttonId, x, y, 40, 20, "");
			this.Next = Next;
		}
		
		public PageButton() {
			super(-1, -1, -1, "");
		}
		
		public boolean Next = false;
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				DrawHorzLine(this.y, this.x, this.x+this.width-2, -1);
				DrawVertLine(this.x, this.y+2, this.y+this.height-2, -1);
				DrawVertLine(this.x+this.width-4, this.y+2, this.y+this.height, -1);
				DrawHorzLine(this.y+this.height-4, this.x+2, this.x+this.width-2, -1);
				
				drawRect(this.x +4, this.y +4, this.x+this.width-4, this.y+this.height-4, BoxBackground);
				
				drawCenteredString(mc.fontRenderer, (Next ? "Next" : "Prev"), this.x + (this.width/2), this.y + 5, 1.25f, (hover ? 0xffce9900 : 0xffffce00));
			}
		}
		
		public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
			GL11.glScalef(size,size,size);
			float mSize = (float)Math.pow(size,-1);
			this.drawCenteredString(fontRendererIn, text, (int) Math.floor(x / size), (int) Math.floor(y / size), color);
			GL11.glScalef(mSize,mSize,mSize);
		}
	}
	
	private static class ExitButton extends GuiButton {
		public ExitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 22, 22, "");
		}

		public ExitButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				DrawHorzLine(this.y, this.x, this.x+this.width-2, -1);
				DrawVertLine(this.x, this.y+2, this.y+this.height-2, -1);
				DrawVertLine(this.x+this.width-4, this.y+2, this.y+this.height, -1);
				DrawHorzLine(this.y+this.height-4, this.x+2, this.x+this.width-2, -1);
				
				drawRect(this.x +4, this.y +4, this.x+this.width-4, this.y+this.height-4, BoxBackground);
				
				drawCenteredString(mc.fontRenderer, "x", this.x + (this.width/2), this.y + 2, 2.0f, (hover ? 0xffce9900 : 0xffffce00));
			}
		}
		
		public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
			GL11.glScalef(size,size,size);
			float mSize = (float)Math.pow(size,-1);
			this.drawCenteredString(fontRendererIn, text, (int) Math.floor(x / size), (int) Math.floor(y / size), color);
			GL11.glScalef(mSize,mSize,mSize);
		}
	}
}
