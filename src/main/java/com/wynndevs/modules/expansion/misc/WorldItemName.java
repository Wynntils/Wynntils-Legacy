package com.wynndevs.modules.expansion.misc;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.webapi.ItemDB;
import com.wynndevs.modules.expansion.webapi.WebAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WorldItemName {
	private static SoundEvent MythicSoundEvent = new SoundEvent(new ResourceLocation("minecraft", "entity.wither.spawn"));
	
	private static Delay refresh = new Delay(0.5f, true);
	//private static Delay ResetScoreboards = new Delay(300.0f, true);
	
	
	public static boolean DisplayName = false;
	public static boolean SmartDeobfuscate = false;
	public static boolean ShortEmerald = false;
	public static boolean ShortPotions = false;
	public static boolean ShortPowders = false;
	public static boolean ShortItems = false;
	
	public static boolean NameEmerald = false;
	public static boolean NamePotions = true;
	public static boolean NamePowders = true;
	public static boolean NameKeyScroll = false;
	public static boolean NameMisc = true;
	public static boolean NameJunk = false;
	
	public static boolean NameMythic = false;
	public static boolean NameLegendary = false;
	public static boolean NameRare = false;
	public static boolean NameUnique = false;
	public static boolean NameNormal = false;
	public static boolean NameSet = false;
	
	public static boolean AnnounceMythic = false;
	public static boolean MythicSound = false;
	public static boolean AnnounceLegendary = false;
	
	public static boolean HighlightMythic = false;
	public static boolean HighlightLegendary = false;
	public static boolean HighlightRare = false;
	public static boolean HighlightUnique = false;
	public static boolean HighlightSet = false;
	
	private static List<String> PotionCheck = new ArrayList<String>();
	private static List<String> PotionFormat = new ArrayList<String>();
	private static List<String> PotionFormatShort = new ArrayList<String>();
	private static List<String> PowderCheck = new ArrayList<String>();
	private static List<String> PowderFormat = new ArrayList<String>();
	private static List<String> PowderFormatShort = new ArrayList<String>();
	
	public static void SetupItemTables(){
		try {
			ExpReference.ConsoleOut("Retrieving Item database [2]");
			PotionCheck.clear();
			PotionFormat.clear();
			PotionFormatShort.clear();
			PowderCheck.clear();
			PowderFormat.clear();
			PowderFormatShort.clear();
			// Retrieve Item Lists from file
			BufferedReader DataFile = new BufferedReader(new InputStreamReader(new URL(WebAPI.ItemDB2URL).openConnection().getInputStream()));
			String DataLine;
			DataLine = DataFile.readLine();
			while (DataLine != null) {
				if (DataLine.equals("--Potion Check--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PotionCheck.add(DataLine);
					}
				}else if (DataLine.equals("--Potion Format--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PotionFormat.add(DataLine);
					}
				}else if (DataLine.equals("--Potion Format Short--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PotionFormatShort.add(DataLine);
					}
				}else if (DataLine.equals("--Powder Check--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PowderCheck.add(DataLine);
					}
				}else if (DataLine.equals("--Powder Format--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PowderFormat.add(DataLine);
					}
				}else if (DataLine.equals("--Powder Format Short--")){
					while ((DataLine = DataFile.readLine()) != null && !DataLine.startsWith("--")) {
						DataLine = DataSetupFormat(DataLine);
						PowderFormatShort.add(DataLine);
					}
				}else{
					DataLine = DataFile.readLine();
				}
			}
			DataFile.close();
			ExpReference.ConsoleOut("Item database [2] successfully retrieved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String DataSetupFormat(String Data){
		if (Data.contains("\\u")){
			String DataFormated = "";
			for (int i=0;i<Data.length();i++){
				if (Data.length() >= i+6 && Data.substring(i, i+2).equals("\\u")){
					DataFormated += String.valueOf((char) Integer.parseInt(Data.substring(i+2, (Data.length() > i+6? i+6 : Data.length())), 16));
					i = i+5;
				}else{
					DataFormated = DataFormated + Data.charAt(i);
				}
			}
			return DataFormated;
		}else{
			return Data;
		}
	}

    public static Delay MythicDisplayTime = new Delay(5.0f, false);
    public static Delay LegendaryDisplayTime = new Delay(5.0f, false);
	public static void t(Minecraft mc) {

		if ((DisplayName || HighlightMythic || HighlightLegendary || HighlightRare || HighlightUnique || HighlightSet) && mc.world != null && mc.world.loadedEntityList != null && refresh.Passed()) {
			Scoreboard Scoreboard = mc.player.getWorldScoreboard();
			
			if (!Scoreboard.getTeamNames().contains("Mythic")) Scoreboard.createTeam("Mythic").setPrefix(String.valueOf('\u00a7') + '5');
			if (!Scoreboard.getTeamNames().contains("Legendary")) Scoreboard.createTeam("Legendary").setPrefix(String.valueOf('\u00a7') + 'b');
			if (!Scoreboard.getTeamNames().contains("Rare")) Scoreboard.createTeam("Rare").setPrefix(String.valueOf('\u00a7') + 'd');
			if (!Scoreboard.getTeamNames().contains("Unique")) Scoreboard.createTeam("Unique").setPrefix(String.valueOf('\u00a7') + 'e');
			if (!Scoreboard.getTeamNames().contains("Set")) Scoreboard.createTeam("Set").setPrefix(String.valueOf('\u00a7') + 'a');
			
			Team Mythic = Scoreboard.getTeam("Mythic");
			Team Legendary = Scoreboard.getTeam("Legendary");
			Team Rare = Scoreboard.getTeam("Rare");
			Team Unique = Scoreboard.getTeam("Unique");
			Team Set = Scoreboard.getTeam("Set");
			
			for (Entity entity : mc.world.loadedEntityList) {
				if (mc.world.loadedEntityList == null)
					break;
				if (entity instanceof EntityItem) {
					ItemStack itemstack = ((EntityItem) entity).getItem();
					if (itemstack.hasDisplayName() && (!entity.getTags().contains("ITEMCHECKED") || (entity.getName().endsWith(String.valueOf('\u00B2')) && NameEmerald))) {
						String name = "";
						boolean flag = false;
						for (Character chr : itemstack.getDisplayName().toCharArray()) {
							if (!flag) {
								if (chr.equals('\u00a7')) {
									flag = true;
								} else {
									name += chr;
								}
							} else {
								flag = false;
							}
						}
						if (entity.getTags().contains("ITEMCHECKED1")){
							entity.addTag("ITEMCHECKED");
						}else{
							entity.addTag("ITEMCHECKED1");
						}
						if (DisplayName) {
							String nameTmp = name;
							String Lore = itemstack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString();
							if (name.contentEquals("Emerald")) {
								if (NameEmerald) {
									if (ShortEmerald) {
										name = String.valueOf('\u00a7') + "a" + itemstack.getCount() + String.valueOf('\u00B2');
									} else {
										name = String.valueOf('\u00a7') + "a" + name + " " + itemstack.getCount() + String.valueOf('\u00B2');
									}
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (name.contentEquals("Block of Emerald")) {
								if (NameEmerald) {
									if (ShortEmerald) {
										name = String.valueOf('\u00a7') + "a" + itemstack.getCount() * 64 + String.valueOf('\u00B2');
									} else {
										name = String.valueOf('\u00a7') + "a" + name + " " + itemstack.getCount() * 64 + String.valueOf('\u00B2');
									}
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (name.contentEquals("Liquified Emerald")) {
								if (NameEmerald) {
									if (ShortEmerald) {
										name = String.valueOf('\u00a7') + "a" + itemstack.getCount() * 4096 + String.valueOf('\u00B2');
									} else {
										name = String.valueOf('\u00a7') + "a" + name + itemstack.getCount() * 4096 + String.valueOf('\u00B2');
									}
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (DataCheck(PotionCheck, name)) {
								if (NamePotions) {
									if (ShortPotions){
										name = DataFormat(PotionCheck, name, PotionFormatShort);
									}else{
										name = DataFormat(PotionCheck, name, PotionFormat);
									}
									if (name.contains(String.valueOf('\u2764')) || name.contains("Healing")){
										if (Lore.contains("remaining")){
											name += " [" + Lore.substring(19,20) + "/3]";
										}else{
											name += " [3/3]";
										}
									}else if (name.contains(String.valueOf('\u273A')) || name.contains("Mana")){
										if (Lore.contains("remaining")){
											name += " [" + Lore.substring(19,20) + "/2]";
										}else{
											name += " [2/2]";
										}
									}else{
										if (Lore.contains("remaining")){
											name += String.valueOf('\u00a7') + "a [" + Lore.substring(19,20) + "/2]";
										}else{
											name += String.valueOf('\u00a7') + "a [2/2]";
										}
									}
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (DataCheck(PowderCheck, name)) {
								if (NamePowders) {
									String PowderTier = (name.contains(" ") ? name.substring(name.lastIndexOf(" ") +1) : "?");
									if (ShortPowders){
										name = DataFormat(PowderCheck, name, PowderFormatShort);
									}else{
										name = DataFormat(PowderCheck, name, PowderFormat);
									}
									switch (PowderTier) {
										case "1": PowderTier = "I"; break;
										case "2": PowderTier = "II"; break;
										case "3": PowderTier = "III"; break;
										case "4": PowderTier = "IV"; break;
										case "5": PowderTier = "V"; break;
										case "6": PowderTier = "VI"; break;
										default: break;
									}
									if (((EntityItem) entity).getItem().getMetadata() == 0) {
										switch (name.charAt(2)) {
											case '\u2739': ((EntityItem) entity).getItem().setItemDamage((PowderTier.contains("V") ? 1 : 9)); break; // Fire
											case '\u2749': ((EntityItem) entity).getItem().setItemDamage((PowderTier.contains("V") ? 6 : 12)); break; // Water
											case '\u274B': ((EntityItem) entity).getItem().setItemDamage((PowderTier.contains("V") ? 7 : 8)); break; // Air
											case '\u2726': ((EntityItem) entity).getItem().setItemDamage((PowderTier.contains("V") ? 14 : 11)); break; // Thunder
											case '\u2724': ((EntityItem) entity).getItem().setItemDamage((PowderTier.contains("V") ? 2 : 10)); break; // Earth
											case '\u2588': break; // Blank
											default: break;
										}
									}
									name = name + PowderTier;
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (Lore.contains("misc. Item")){
								if (NameMisc) {
									name = String.valueOf('\u00a7') + "7" + name;
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							} else if (Lore.contains("Junk Item")){
								if (NameJunk) {
									name = String.valueOf('\u00a7') + "7" + name;
									entity.setCustomNameTag(name);
									entity.setAlwaysRenderNameTag(true);
								}
							}else{
								for (int j = 0; j < ItemDB.ItemDB.size(); j++) {
									if (ItemDB.ItemDB.get(j).Name.contentEquals(name)) {
										switch (ItemDB.ItemDB.get(j).GetRarity()) {
                                            case 4:
                                                if (NameMythic) {
                                                    name = String.valueOf('\u00a7') + "5" + name;
                                                }
                                                if (HighlightMythic) {
                                                    Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Mythic.getName());
                                                    entity.setGlowing(true);
                                                }
                                                if (!itemstack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString().contains("identifications") && !itemstack.serializeNBT().getString("id").equals("minecraft:air") && !entity.getTags().contains("ITEMCHECKED")) {
                                                    if (AnnounceMythic && !entity.getTags().contains("ITEMTHROWNPLAYER") && MythicDisplayTime.Passed()) {
                                                        if (MythicSound)
                                                            mc.player.playSound(MythicSoundEvent, 1.0f, 2.0f);
                                                        mc.ingameGUI.displayTitle("", "\u00A75Mythic item appeared!", 1, 3, 1);
                                                        mc.ingameGUI.displayTitle(null, "\u00A75Mythic item appeared!", 1, 3, 1);
                                                        MythicDisplayTime.Reset();
                                                    }
                                                }
                                                break;
                                            case 3:
                                                if (NameLegendary) {
                                                    name = String.valueOf('\u00a7') + "b" + name;
                                                }
                                                if (HighlightLegendary) {
                                                    Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Legendary.getName());
                                                    entity.setGlowing(true);
                                                }
                                                if (!itemstack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString().contains("identifications") && !itemstack.serializeNBT().getString("id").equals("minecraft:air") && !entity.getTags().contains("ITEMCHECKED")) {
                                                    if (AnnounceLegendary && !entity.getTags().contains("ITEMTHROWNPLAYER") && LegendaryDisplayTime.Passed()) {
                                                        mc.ingameGUI.displayTitle("", "\u00A7bLegendary item appeared!", 1, 3, 1);
                                                        mc.ingameGUI.displayTitle(null, "\u00A7bLegendary item appeared!", 1, 3, 1);
                                                        LegendaryDisplayTime.Reset();
                                                    }
                                                }
                                                break;
										case 2:
											if (NameRare){name = String.valueOf('\u00a7') + "d" + name;}
											if (HighlightRare) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Rare.getName()); entity.setGlowing(true);}
											break;
										case 1:
											if (NameUnique){name = String.valueOf('\u00a7') + "e" + name;}
											if (HighlightUnique) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Unique.getName()); entity.setGlowing(true);}
											break;
										case 5:
											if (NameSet){name = String.valueOf('\u00a7') + "a" + name;}
											if (HighlightSet) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Set.getName()); entity.setGlowing(true);}
											break;
										default:
											if (NameNormal){name = String.valueOf('\u00a7') + "f" + name;}
											break;
										}
										if (!name.equals(nameTmp)){
											int ItemLevel = ItemDB.ItemDB.get(j).GetLevel();
											if (SmartDeobfuscate){
												if (Lore.contains("identifications") || name.charAt(1) == 'f'){
													if (ShortItems) {
														name = name + String.valueOf('\u00a7') + "6 [" + ItemLevel + "]";
													}else{
														name = name + String.valueOf('\u00a7') + "6 [Lv. " + ItemLevel + "]";
													}
													entity.setCustomNameTag(name);
													entity.setAlwaysRenderNameTag(true);
												}else{
													if (ItemDB.ItemDB.get(j).GetType() == 11){
														if (ShortItems) {
															name = name.substring(0, 2) + "UnI'd Item" + String.valueOf('\u00a7') + "6 [" + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
														}else{
															name = name.substring(0, 2) + "Unidentified Item" + String.valueOf('\u00a7') + "6 [Lv. " + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
														}
													}else{
														if (ShortItems) {
															name = name.substring(0, 2) + "UnI'd " + ItemDB.ItemDB.get(j).GetTypeName() + String.valueOf('\u00a7') + "6 [" + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
														}else{
															name = name.substring(0, 2) + "Unidentified " + ItemDB.ItemDB.get(j).GetTypeName() + String.valueOf('\u00a7') + "6 [Lv. " + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
														}
													}
													entity.setCustomNameTag(name);
													entity.setAlwaysRenderNameTag(true);
												}
											}else{
												if (name.charAt(1) == 'f'){
													if (ShortItems) {
														name = name + String.valueOf('\u00a7') + "6 [" + ItemLevel + "]";
													}else{
														name = name + String.valueOf('\u00a7') + "6 [Lv. " + ItemLevel + "]";
													}
													entity.setCustomNameTag(name);
													entity.setAlwaysRenderNameTag(true);
												}else if (ItemDB.ItemDB.get(j).GetType() == 11){
													if (ShortItems) {
														name = name.substring(0, 2) + "UnI'd Item" + String.valueOf('\u00a7') + "6 [" + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
													}else{
														name = name.substring(0, 2) + "Unidentified Item" + String.valueOf('\u00a7') + "6 [Lv. " + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
													}
												}else{
													if (ShortItems) {
														name = name.substring(0, 2) + "UnI'd " + ItemDB.ItemDB.get(j).GetTypeName() + String.valueOf('\u00a7') + "6 [" + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
													}else{
														name = name.substring(0, 2) + "Unidentified " + ItemDB.ItemDB.get(j).GetTypeName() + String.valueOf('\u00a7') + "6 [Lv. " + (((ItemLevel-1) / 4) * 4) + "-" + ((((ItemLevel-1) / 4) * 4) + 4) + "]";
													}
												}
												entity.setCustomNameTag(name);
												entity.setAlwaysRenderNameTag(true);
											}
										}
										break;
									}
								}
							}
						} else {
							for (int j = 0; j < ItemDB.ItemDB.size(); j++) {
								if (ItemDB.ItemDB.get(j).Name.contentEquals(name)) {
									switch (ItemDB.ItemDB.get(j).GetRarity()) {
									case 4:
										if (NameMythic){name = String.valueOf('\u00a7') + "5" + name;}
										if (HighlightMythic) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Mythic.getName()); entity.setGlowing(true);}
										if (!itemstack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString().contains("identifications") && !itemstack.serializeNBT().getString("id").equals("minecraft:air") && !entity.getTags().contains("ITEMCHECKED")){
                                            if (AnnounceMythic && !entity.getTags().contains("ITEMTHROWNPLAYER") && MythicDisplayTime.Passed()) {
                                                if (MythicSound) mc.player.playSound(MythicSoundEvent, 1.0f, 2.0f);
                                                mc.ingameGUI.displayTitle("", "\u00A75Mythic item appeared!", 1, 3, 1);
                                                mc.ingameGUI.displayTitle(null, "\u00A75Mythic item appeared!", 1, 3, 1);
                                                MythicDisplayTime.Reset();
                                            }
										}
										break;
									case 3:
										if (NameLegendary){name = String.valueOf('\u00a7') + "b" + name;}
										if (HighlightLegendary) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Legendary.getName()); entity.setGlowing(true);}
										if (!itemstack.serializeNBT().getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8).toString().contains("identifications") && !itemstack.serializeNBT().getString("id").equals("minecraft:air") && !entity.getTags().contains("ITEMCHECKED")){
                                            if (AnnounceLegendary && !entity.getTags().contains("ITEMTHROWNPLAYER") && LegendaryDisplayTime.Passed()) {
                                                mc.ingameGUI.displayTitle("", "\u00A7bLegendary item appeared!", 1, 3, 1);
                                                mc.ingameGUI.displayTitle(null, "\u00A7bLegendary item appeared!", 1, 3, 1);
                                                LegendaryDisplayTime.Reset();
                                            }
                                        }
										break;
									case 2:
										if (NameRare){name = String.valueOf('\u00a7') + "d" + name;}
										if (HighlightRare) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Rare.getName()); entity.setGlowing(true);}
										break;
									case 1:
										if (NameUnique){name = String.valueOf('\u00a7') + "e" + name;}
										if (HighlightUnique) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Unique.getName()); entity.setGlowing(true);}
										break;
									case 5:
										if (NameSet){name = String.valueOf('\u00a7') + "a" + name;}
										if (HighlightSet) {Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Set.getName()); entity.setGlowing(true);}
										break;
									default:
										if (NameNormal){name = String.valueOf('\u00a7') + "f" + name;}
										break;
									}
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean DataCheck(List<String> Check, String Name){
		for (String Test : Check){
			if (Name.startsWith(Test)){
				return true;
			}
		}
		return false;
	}
	
	private static String DataFormat(List<String> Check, String Name, List<String> Format){
		for (int i=0;i<Check.size();i++){
			if (Name.startsWith(Check.get(i))){
				Name = Format.get(i % Format.size());
				return Name;
			}
		}
		return Name;
	}
}
