package com.wynndevs.modules.expansion.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;

public class WarOptimiser {
	
	public static boolean OptimiseWar(Entity entity){
		if (entity instanceof EntityItem || entity instanceof EntityArmorStand){
			return true;
		}
		return false;
	}
}
