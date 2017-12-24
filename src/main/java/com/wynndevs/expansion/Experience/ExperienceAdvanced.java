package com.wynndevs.expansion.Experience;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ExperienceAdvanced {
	
	public static List<String[]> ExperienceAdvanced = new ArrayList<String[]>();
	
	private static int EntityEntanglementID = 0;
	private static List<Entity> entityRejects = new ArrayList<Entity>();
	
	//private static List<EEID> EntanglementsList = new ArrayList<EEID>();
	//Current Bugs:
	
	// Items dont get names
	//
	
	public static void GatherExp(Minecraft mc){
		List<Entity> EntityList = mc.world.loadedEntityList;
		for (Entity entity : EntityList){
			if (EntityList == null)
				break;
			if (entity instanceof EntityArmorStand && !entity.getTags().contains("AdvExpUSED")) {
				if (entity.getCustomNameTag().contentEquals(String.valueOf('\u00a7') + "7[" + mc.player.getName() + "]") || (entity.getCustomNameTag().contentEquals(String.valueOf('\u00a7') + "7[Shared]") && Experience.GainedExp)){
					
					Entity ExpEntity = null;
					Entity NameEntity = null;
					Entity MobEntity = null;
					
					// Find how much Exp
					for (Entity ExpEntityTest : EntityList){
						if (ExpEntityTest instanceof EntityArmorStand) {
							if (!ExpEntityTest.getTags().contains("AdvExpUSED") && entity.posX == ExpEntityTest.posX && entity.posZ == ExpEntityTest.posZ && entity.posY +0.28125 == ExpEntityTest.posY && ExpEntityTest.getName().contains(String.valueOf('\u00a7') + "f XP")) {
								ExpEntity = ExpEntityTest;
								break;
							}else if (!ExpEntityTest.getTags().contains("AdvExpUSED") && entity.posX == ExpEntityTest.posX && entity.posZ == ExpEntityTest.posZ && entity.posY +0.59375 == ExpEntityTest.posY && ExpEntityTest.getName().contains(String.valueOf('\u00a7') + "f XP")) {
								ExpEntity = ExpEntityTest;
								break;
							}
						}
					}
					// Find what died
					double closest = 4;
					for (Entity TargetTest : EntityList) {
						if (!TargetTest.getTags().contains("AdvExpUSED") && !TargetTest.isEntityAlive()){
							if (entity.getDistanceToEntity(TargetTest)<closest && entity != TargetTest) {
								closest = entity.getDistanceToEntity(TargetTest);
								MobEntity = TargetTest;
							}
						}
					}
					// Locate Entity's Name
					if (MobEntity != null && MobEntity.getTags().toString().contains("EEID")) {
						String EEID = MobEntity.getTags().toString().replace("]", ",");
						EEID = EEID.substring(EEID.indexOf("EEID"), EEID.indexOf(",", EEID.indexOf("EEID") +4));
						
						for (Entity NameEntityTest : EntityList) {
							if (NameEntityTest != MobEntity && NameEntityTest.getTags().contains(EEID)){
								NameEntity = NameEntityTest;
								break;
							}
						}
					}
					
					// Check if mob should be rejected
					if (ExpEntity == null || NameEntity == null){
						entityRejects.add(entity);
					}else{
						entity.addTag("AdvExpUSED");
						ExpEntity.addTag("AdvExpUSED");
						if (MobEntity != null) {MobEntity.addTag("AdvExpUSED");}
						NameEntity.addTag("AdvExpUSED");
						
						String MobExp = ExpEntity.getName().substring(ExpEntity.getName().indexOf(String.valueOf('\u00a7') + "f+") +5, ExpEntity.getName().indexOf(" XP") -2);
						String MobExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(MobExp)/Experience.getCurrentWynncraftMaxXp())*100);
						MobExp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(MobExp));
						String MobName = null;
						String MobLevel = null;
						MobName = NameEntity.getName();
						
						// Send Data
						if (MobName != null){
							if (MobName.contains("6 [Lv. ")){
								MobLevel = String.valueOf('\u00a7') + "6" + MobName.substring(MobName.indexOf(String.valueOf('\u00a7') +"6 ") +3);
								MobName = MobName.substring(0, MobName.indexOf(String.valueOf('\u00a7') +"6 "));
							}
							
							ExperienceUI.ExpHUDHang.Reset();
							ExperienceUI.ExpHUD += 0;
							ExperienceUI.ExpHUDPer += 0;
							ExperienceUI.ExpHUDAnimation = 0;
							ExperienceUI.ExpHUDLength = 1;
							
							String[] AdvExp = {MobName, MobLevel, MobExp, MobExpPer};
							Experience.Exp.add(AdvExp);
						}
					}
				}
			}
		}
		
		// Double check all rejected entities
		for (Entity entity : entityRejects){
			
			Entity ExpEntity = null;
			Entity NameEntity = null;
			Entity MobEntity = null;
			
			float Closest = 4;
			entity.setPosition(entity.posX, entity.posY+0.43750, entity.posZ);
			for (Entity ExpEntityTest : EntityList){
				if (ExpEntityTest instanceof EntityArmorStand) {
					if (!ExpEntityTest.getTags().contains("AdvExpUSED") && entity.posX == ExpEntityTest.posX && entity.posZ == ExpEntityTest.posZ && entity.posY -0.15625 == ExpEntityTest.posY && ExpEntityTest.getName().contains(String.valueOf('\u00a7') + "f XP")) {
						ExpEntity = ExpEntityTest;
						break;
					}else if (!ExpEntityTest.getTags().contains("AdvExpUSED") && entity.posX == ExpEntityTest.posX && entity.posZ == ExpEntityTest.posZ && entity.posY +0.15625 == ExpEntityTest.posY && ExpEntityTest.getName().contains(String.valueOf('\u00a7') + "f XP")) {
						ExpEntity = ExpEntityTest;
						break;
					}
					if (!ExpEntityTest.getTags().contains("AdvExpUSED") && entity.posX-1 < ExpEntityTest.posX && entity.posX+1 > ExpEntityTest.posX && entity.posZ-1 < ExpEntityTest.posZ && entity.posZ+1 > ExpEntityTest.posZ && ExpEntityTest.getName().contains(String.valueOf('\u00a7') + "f XP") && entity.getDistanceToEntity(ExpEntityTest) < Closest) {
						Closest = entity.getDistanceToEntity(ExpEntityTest);
						ExpEntity = ExpEntityTest;
					}
				}
			}
			entity.setPosition(entity.posX, entity.posY-0.43750, entity.posZ);
			// Find what died
			double closest = 4;
			for (Entity MobEntityTest : EntityList) {
				if (!MobEntityTest.getTags().contains("AdvExpUSED") && !MobEntityTest.isEntityAlive()){
					if (entity.getDistanceToEntity(MobEntityTest)<closest && entity != MobEntityTest) {
						closest = entity.getDistanceToEntity(MobEntityTest);
						MobEntity = MobEntityTest;
					}
				}
			}
			// Locate Entity's Name
			if (MobEntity != null && MobEntity.getTags().toString().contains("EEID")) {
				String EEID = MobEntity.getTags().toString().replace("]", ",");
				EEID = EEID.substring(EEID.indexOf("EEID"), EEID.indexOf(",", EEID.indexOf("EEID") +4));
				
				for (Entity NameTest : EntityList) {
					if (NameTest != MobEntity && NameTest.getTags().contains(EEID)){
						NameEntity = NameTest;
						break;
					}
				}
			}else{
				// If no dead mob found, check for name plates
				closest = 4;
				NameEntity = null;
				for (Entity NameEntityTest : EntityList) {
					if (!NameEntityTest.getTags().contains("AdvExpUSED") && NameEntityTest.getTags().contains("EETO") && NameEntityTest.getName().contains(String.valueOf('\u00a7') + "6 [Lv. ") && !NameEntityTest.getName().contains("Hidden Wall Trap")){
						if (entity.getDistanceToEntity(NameEntityTest)<closest && entity != NameEntityTest) {
							closest = entity.getDistanceToEntity(NameEntityTest);
							NameEntity = NameEntityTest;
						}
					}
				}
				// Grab Entity incase its still alive
				if (NameEntity != null && NameEntity.getTags().toString().contains("EEID")) {
					String EEID = NameEntity.getTags().toString().replace("]", ",");
					EEID = EEID.substring(EEID.indexOf("EEID"), EEID.indexOf(",", EEID.indexOf("EEID") +4));
					
					for (Entity MobEntityTest : EntityList) {
						if (MobEntityTest != NameEntity && MobEntityTest.getTags().contains(EEID)){
							MobEntity = MobEntityTest;
							break;
						}
					}
				}
			}
			
			String MobExp = null;
			String MobExpPer = null;
			String MobName = null;
			String MobLevel = null;
			
			
			if (entity != null) {entity.addTag("AdvExpUSED");
			if (ExpEntity != null) {ExpEntity.addTag("AdvExpUSED");
			if (MobEntity != null) {MobEntity.addTag("AdvExpUSED");
			if (NameEntity != null) {NameEntity.addTag("AdvExpUSED");}}}}
			
			if (ExpEntity != null && NameEntity != null){
				/*entity.addTag("AdvExpUSED");
				ExpEntity.addTag("AdvExpUSED");
				if (MobEntity != null) {MobEntity.addTag("AdvExpUSED");}
				NameEntity.addTag("AdvExpUSED");*/
				
				
				MobExp = ExpEntity.getName().substring(ExpEntity.getName().indexOf(String.valueOf('\u00a7') + "f+") +5, ExpEntity.getName().indexOf(" XP") -2);
				MobExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(MobExp)/Experience.getCurrentWynncraftMaxXp())*100);
				MobExp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(MobExp));
				MobName = NameEntity.getName();
			}else if (NameEntity != null){
				entity.addTag("AdvExpUSED");
				if (MobEntity != null) {MobEntity.addTag("AdvExpUSED");}
				NameEntity.addTag("AdvExpUSED");
				
				MobExp = "0";
				MobExpPer = "00.00";
				MobName = NameEntity.getName();
			}
			
			// Send Data
			if (MobName != null){
				if (MobName.contains("6 [Lv. ")){
					MobLevel = String.valueOf('\u00a7') + "6" + MobName.substring(MobName.indexOf(String.valueOf('\u00a7') +"6 ") +3);
					MobName = MobName.substring(0, MobName.indexOf(String.valueOf('\u00a7') +"6"));
				}
				
				ExperienceUI.ExpHUDHang.Reset();
				ExperienceUI.ExpHUD += 0;
				ExperienceUI.ExpHUDPer += 0;
				ExperienceUI.ExpHUDAnimation = 0;
				ExperienceUI.ExpHUDLength = 1;
				
				String[] AdvExp = {MobName, MobLevel, MobExp, MobExpPer};
				Experience.Exp.add(AdvExp);
				if (Experience.Exp.size() == 0){String[] ExpValues = {"0", "0"}; Experience.Exp.add(ExpValues);}
			}else if(MobExp != null){
				ExperienceUI.ExpHUDHang.Reset();
				ExperienceUI.ExpHUD += 0;
				ExperienceUI.ExpHUDPer += 0;
				ExperienceUI.ExpHUDAnimation = 0;
				ExperienceUI.ExpHUDLength = 1;
				
				String[] AdvExp = {"", "", MobExp, MobExpPer};
				Experience.Exp.add(AdvExp);
			}
		}
		if (!entityRejects.isEmpty()) {entityRejects.clear();}
	}
	
	
	public static void EntangleMobs(Minecraft mc){
		List<Entity> EntityList = mc.world.loadedEntityList;
		int Entanglments = 0;
		for (Entity NamePlate : EntityList){
			if (EntityList == null)
				break;
			if (NamePlate instanceof EntityArmorStand && !NamePlate.getTags().toString().contains("EEID") && NamePlate.getName().contains(String.valueOf('\u00a7') + "6 [Lv. ") && mc.player.getDistanceToEntity(NamePlate) < 32) {
				Entity Mob = null;
				int EntityCount = 0;
				for (Entity MobTest : EntityList) {
					if (MobTest instanceof EntityArmorStand || MobTest instanceof EntityArrow) {
					}else if (!MobTest.getTags().toString().contains("EEID") && NamePlate != MobTest && NamePlate.posX-0.5 < MobTest.posX && NamePlate.posX+0.5 > MobTest.posX && NamePlate.posY-5 < MobTest.posY && NamePlate.posY+2 > MobTest.posY && NamePlate.posZ-0.5 < MobTest.posZ && NamePlate.posZ+0.5 > MobTest.posZ && !MobTest.getAlwaysRenderNameTag()){
						Mob = MobTest;
						EntityCount++;
					}
				}
				if (EntityCount == 1 && Mob != null){
					
					//Check for Entity Entanglement Type Override\\
					
					if (Mob instanceof EntityPlayer) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityItem) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityFallingBlock) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityXPOrb) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityEnderEye) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityWitherSkull) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityMinecart) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityMinecartChest) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityMinecartFurnace) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityMinecartTNT) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityLeashKnot) {NamePlate.addTag("EETO");}
					if (Mob instanceof EntityTNTPrimed) {NamePlate.addTag("EETO");}
					
					//Setup Entity Entanglement ID
					NamePlate.addTag("EEID" + EntityEntanglementID + "-");
					Mob.addTag("EEID" + EntityEntanglementID + "-");
					EntityEntanglementID++;
					
					Entanglments++;
					if (Entanglments>4){break;}
				}
			}
		}
	}
	/*
	public static void EntangleMobs1(Minecraft mc){
		List<Entity> EntityList = mc.world.loadedEntityList;
		int Entanglments = 0;
		for (Entity NamePlate : EntityList){
			if (EntityList == null)
				break;
			
			if (NamePlate instanceof EntityArmorStand && !IsEntangled(NamePlate) && mc.player.getDistanceToEntity(NamePlate) < 32) {
				String Name = NamePlate.getName();
				if (Name.contains(String.valueOf('\u00a7') + "6 [Lv. ")){
					Entity Mob = null;
					int EntityCount = 0;
					AxisAlignedBB NamePlateZone = new AxisAlignedBB(NamePlate.posX-0.5, NamePlate.posY-5, NamePlate.posZ-0.5, NamePlate.posX+0.5, NamePlate.posY+2, NamePlate.posZ+0.5);
					for (Entity MobTest : mc.world.getEntitiesWithinAABBExcludingEntity(NamePlate, NamePlateZone )) {
						if (MobTest instanceof EntityArmorStand || MobTest instanceof EntityArrow || MobTest instanceof EntityItemFrame) {
						}else if (!IsEntangled(MobTest) && !MobTest.getTags().toString().contains("ITEMCHECKED")){
							Mob = MobTest;
							EntityCount++;
						}
					}
					if (EntityCount == 1 && Mob != null){
						AddEntanglement(NamePlate, Mob, (Mob instanceof EntityLiving ? true : false));
					}
					Entanglments++;
					if (Entanglments > 3){break;}
				}
			}
		}
	}
	
	private static boolean IsEntangled(Entity CheckEntity) {return IsEntangled(CheckEntity.getPersistentID());}
	private static boolean IsEntangled(UUID CheckUUID) {
		for (EEID Entanglement : EntanglementsList) {
			if (Entanglement.Name.compareTo(CheckUUID) == 0 || Entanglement.Entity.compareTo(CheckUUID) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean IsUsed(Entity CheckEntity) {return IsUsed(CheckEntity.getPersistentID());}
	private static boolean IsUsed(UUID CheckUUID) {
		for (EEID Entanglement : EntanglementsList) {
			if (Entanglement.Name.compareTo(CheckUUID) == 0 || Entanglement.Entity.compareTo(CheckUUID) == 0) {
				return (Entanglement.ID == 0 ? true : false);
			}
		}
		return false;
	}
	
	private static UUID GetPartner(Entity CheckEntity) {return GetPartner(CheckEntity.getPersistentID());}
	private static UUID GetPartner(UUID CheckUUID) {
		for (EEID Entanglement : EntanglementsList) {
			if (Entanglement.Name.compareTo(CheckUUID) == 0) {
				return Entanglement.Entity;
			}else if (Entanglement.Entity.compareTo(CheckUUID) == 0) {
				return Entanglement.Name;
			}
		}
		return CheckUUID;
	}
	
	private static short MaxCocurrentEntanglements = Short.MAX_VALUE;
	
	private static short EntanglmentID = 1;
	private static void AddEntanglement(Entity Nameplate, Entity Entity, boolean EETO) {AddEntanglement(Nameplate.getPersistentID(), Entity.getPersistentID(), EETO);}
	private static void AddEntanglement(UUID Nameplate, UUID Entity, boolean EETO) {
		if (!EntanglementsList.isEmpty()) {
			if (Math.abs(EntanglementsList.get(EntanglementsList.size()-1).ID) == EntanglmentID) {
				EntanglementsList.remove(EntanglementsList.size()-1);
			}
			EntanglementsList.add(0, new EEID(Nameplate, Entity, (short) (EETO ? EntanglmentID * -1 : EntanglmentID)));
		}else{
			EntanglementsList.add(new EEID(Nameplate, Entity, (short) (EETO ? EntanglmentID * -1 : EntanglmentID)));
		}
		if (EntanglmentID == MaxCocurrentEntanglements) {
			EntanglmentID = 1;
		}else{
			EntanglmentID++;
		}
	}
	
	static class EEID {
		public UUID Name = new UUID(0L, 0L);
		public UUID Entity = new UUID(0L, 0L);
		public short ID = 0;
		
		public EEID(UUID Name, UUID Entity, short ID) {
			this.Name = Name;
			this.Entity = Entity;
			this.ID = ID;
		}
	}
	*/
}
