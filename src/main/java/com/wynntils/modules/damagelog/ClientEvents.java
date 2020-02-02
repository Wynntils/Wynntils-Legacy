package com.wynntils.modules.damagelog;

import java.util.List;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {
    
    private static final WeakHashMap<EntityArmorStand, String> armorStands = new WeakHashMap<>();
    private static final Pattern mobNamePattern = Pattern.compile(".+\\[Lv. \\d+\\]");
    private static float targetSearchRadius = 1.5f;
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    	if (!DamageLogConfig.INSTANCE.logDamageToGameUpdateOverlay)
    		return;

    	EntityPlayerSP player = Minecraft.getMinecraft().player;
    	if (player == null || !(event.getEntity() instanceof EntityArmorStand))
    		return;
    	
    	EntityArmorStand armorStand = (EntityArmorStand)event.getEntity();
    	AxisAlignedBB nearbyAABB = new AxisAlignedBB(
    			armorStand.getPositionVector().subtract(targetSearchRadius, targetSearchRadius, targetSearchRadius),
    			armorStand.getPositionVector().add(targetSearchRadius, targetSearchRadius, targetSearchRadius));

    	List<Entity> nearbyEntities = Minecraft.getMinecraft().world.getEntitiesWithinAABBExcludingEntity(armorStand, nearbyAABB);
    	
    	if (nearbyEntities.isEmpty())
    		return;
    	
    	double nearestDist = Double.MAX_VALUE;
    	String entityName = null;
    	for (Entity e : nearbyEntities) {
    		Matcher m = mobNamePattern.matcher(e.getCustomNameTag());
    		if (!m.matches()) {
    			continue;
    		}
    		double dist = e.getPositionVector().squareDistanceTo(armorStand.getPositionVector());
    		if (dist < nearestDist) {
    			entityName = e.getCustomNameTag();
    			nearestDist = dist;
    		}
    	}
    	if (entityName != null) {
    		armorStands.put(armorStand, entityName);
    	}
    }

    private static String damageTypesString() {
    	String s = "";
    	for (DamageType v : DamageType.values()) {
    		s = s+v.symbol;
    	}
    	return s;
    }
    
    private static final Pattern damagePattern = Pattern.compile("-(\\d+) ([" + damageTypesString() + "])");
    
    @SubscribeEvent
    public void onEntityEvent(EntityEvent event) {
    	if (!(event.getEntity() instanceof EntityArmorStand))
    		return;
    	EntityArmorStand armorStand = (EntityArmorStand)event.getEntity();
    	
    	if (armorStands.containsKey(armorStand)) {
    		if (!armorStand.getCustomNameTag().equals("")) {

    			String targetName = armorStands.remove(armorStand);
    			
    			Matcher m = damagePattern.matcher(armorStand.getCustomNameTag());
    			StringBuilder sb = new StringBuilder();
    			float total = 0;
    			while (m.find()) {
    				float amount = Float.parseFloat(m.group(1));
    				total += amount;
    				DamageType type = DamageType.fromSymbol(m.group(2));
    				sb.append(type.textFormat.toString())
    				  .append(" ").append(type.symbol).append("=").append(amount);
    			}
    			if (total > 0) {
    				String msg = TextFormatting.WHITE+"HIT "+targetName+TextFormatting.WHITE+" for "+total
    						+" ("+sb.toString()+TextFormatting.WHITE+")";
    				GameUpdateOverlay.queueMessage(msg);
    			}
    		}
    	}
    }

}
