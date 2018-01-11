package com.wynndevs.modules.expansion.options;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.GuiScreenMod;
import com.wynndevs.modules.expansion.options.GuiSHCMWynnOptions.ExitButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class EggPage extends GuiScreenMod {
	
	private static final ResourceLocation TEXTURE_OPTIONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/options.png");
	
	private static ExitButton btnExit = new ExitButton();
	
	private static String Time = "";
	private static boolean EggDay = false;
	private static int SECONDS_IN_A_DAY = 24 * 60 * 60;
	
	private static void RefreshTimer() {
		Calendar today = Calendar.getInstance();
		Calendar thatDay = Calendar.getInstance();
		if (today.get(Calendar.MONTH) == GetEggDay(today.get(Calendar.YEAR))[0] && today.get(Calendar.DAY_OF_MONTH) == GetEggDay(today.get(Calendar.YEAR))[1]){
			EggDay = true;
		}else if (today.get(Calendar.MONTH) > GetEggDay(today.get(Calendar.YEAR))[0] || (today.get(Calendar.MONTH) == GetEggDay(today.get(Calendar.YEAR))[0] && today.get(Calendar.DAY_OF_MONTH) > GetEggDay(today.get(Calendar.YEAR))[1])){
			thatDay.setTime(new Date(0)); /* reset */
			thatDay.set(Calendar.DAY_OF_MONTH,GetEggDay(today.get(Calendar.YEAR) +1)[1]);
			thatDay.set(Calendar.MONTH,GetEggDay(today.get(Calendar.YEAR) +1)[0]); // 0-11 so 1 less
			thatDay.set(Calendar.YEAR, today.get(Calendar.YEAR) +1);
			EggDay = false;
		}else{
			thatDay.setTime(new Date(0)); /* reset */
			thatDay.set(Calendar.DAY_OF_MONTH,GetEggDay(today.get(Calendar.YEAR))[1]);
			thatDay.set(Calendar.MONTH,GetEggDay(today.get(Calendar.YEAR))[0]); // 0-11 so 1 less
			thatDay.set(Calendar.YEAR, today.get(Calendar.YEAR));
			EggDay = false;
		}
		
		
		long diff =  thatDay.getTimeInMillis() - today.getTimeInMillis(); 
		long diffSec = diff / 1000;
		
		long days = diffSec / SECONDS_IN_A_DAY;
		long secondsDay = diffSec % SECONDS_IN_A_DAY;
		long seconds = secondsDay % 60;
		long minutes = (secondsDay / 60) % 60;
		long hours = (secondsDay / 3600);
		
		Time = new DecimalFormat("000").format(days) + ":" + new DecimalFormat("00").format(hours) + ":" + new DecimalFormat("00").format(minutes) + ":" + new DecimalFormat("00").format(seconds);
	}
	
	private static int[] GetEggDay(int y){
		int a = y % 19;
		int b = y / 100;
		int c = y % 100;
		int d = b / 4;
		int e = b % 4;
		int g = (8 * b + 13) / 25;
		int h = (19 * a + b - d - g + 15) % 30;
		int j = c / 4;
		int k = c % 4;
		int m = (a + 11 * h) / 319;
		int r = (2 * e + 2 * j - k - h + m + 32) % 7;
		int n = (h - m + r + 90) / 25;
		int p = (h - m + r + n + 19) % 32;
		return new int[] {n-1, p};
	}
	
	@Override
	protected String GetButtonTooltip(int buttonId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initGui() {
		btnExit = new ExitButton(-1, (this.width / 2) + 100, 15);
		this.addButton(btnExit);
	}
	
	@Override
	public void updateScreen() {
		RefreshTimer();
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			}
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		this.mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
		
		this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);
		
		if (EggDay) {
			this.drawTexturedModalRect((this.width / 2) -17, 95, 139, 197, 34, 59);
			this.drawCenteredStringPlain(mc.fontRenderer, "Happy Easter!", (this.width/2), 165, 2.0f, Integer.parseInt("FFA700", 16));
		}else{
			this.drawTexturedModalRect((this.width / 2) -17, 95, 100, 197, 34, 59);
			this.drawCenteredStringPlain(mc.fontRenderer, Time, (this.width/2), 165, 2.0f, Integer.parseInt("FFA700", 16));
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
