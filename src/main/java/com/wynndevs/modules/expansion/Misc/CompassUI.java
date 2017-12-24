package com.wynndevs.modules.expansion.Misc;

import com.wynndevs.modules.expansion.Experience.ExperienceUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class CompassUI extends ModGui{
	
	public static boolean Compass = false;
	public static boolean CompassType = false;
	
	public CompassUI(Minecraft mc){
		ScaledResolution scaled = new ScaledResolution(mc);
        int width = scaled.getScaledWidth();
        int height = scaled.getScaledHeight();
        FontRenderer font = mc.fontRenderer;
        
        
        if (Compass){
        	if (CompassType){
				try {
		            ItemStack Compass = mc.player.inventory.getCurrentItem();
		            if (Compass.hasDisplayName() && Compass.getDisplayName().contains("Character Info")) {
		            	this.drawCenteredString(font, "x: " + mc.player.getPosition().getX() + ", y: " + mc.player.getPosition().getY() + ", z: " + mc.player.getPosition().getZ(), width/2, (height/2) + 25, 1.0f, Integer.parseInt("55FFFF",16));
		            }
		        }
		        catch (Exception ignored) {}
	        }else{
	        	if (ExperienceUI.ExpAboveHealth){
	        		this.drawCenteredString(font, "x: " + mc.player.getPosition().getX() + ", y: " + mc.player.getPosition().getY() + ", z: " + mc.player.getPosition().getZ(), width/2, height - 48, 1.0f, Integer.parseInt("55FFFF",16));
	        	}else{
	        		this.drawCenteredString(font, "x: " + mc.player.getPosition().getX() + ", y: " + mc.player.getPosition().getY() + ", z: " + mc.player.getPosition().getZ(), width/2, height - 85, 1.0f, Integer.parseInt("55FFFF",16));
	        	}
	        }
        }
	}
}
