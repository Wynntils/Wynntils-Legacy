/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import com.wynntils.core.utils.Pair;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ItemProfile {

    public static Gson GSON;

    public String name;
    public ItemRarity tier;
    public int sockets;
    public Color armorColor;
    public String armorType;
    public String addedLore;
    public String type;
    public String set;
    public ItemType material;
    public String dropType;
    public String restrictions;
    public String damage;
    public String fireDamage;
    public String waterDamage;
    public String airDamage;
    public String thunderDamage;
    public String earthDamage;
    public String attackSpeed;
    public int level;
    public String quest;
    public int strength;
    public int dexterity;
    public int intelligence;
    public int agility;
    public int defense;
    public int healthRegen;
    public int manaRegen;
    public int spellDamage;
    public int damageBonus;
    public int lifeSteal;
    public int manaSteal;
    public int xpBonus;
    public int lootBonus;
    public int reflection;
    public int strengthPoints;
    public int dexterityPoints;
    public int intelligencePoints;
    public int agilityPoints;
    public int defensePoints;
    public int thorns;
    public int exploding;
    public int speed;
    public int attackSpeedBonus;
    public int poison;
    public int healthBonus;
    public int soulPoints;
    public int emeraldStealing;
    public int healthRegenRaw;
    public int spellDamageRaw;
    public int damageBonusRaw;
    public int bonusFireDamage;
    public int bonusWaterDamage;
    public int bonusAirDamage;
    public int bonusThunderDamage;
    public int bonusEarthDamage;
    public int bonusFireDefense;
    public int bonusWaterDefense;
    public int bonusAirDefense;
    public int bonusThunderDefense;
    public int bonusEarthDefense;
    public String category;
    public int fireDefense;
    public int waterDefense;
    public int airDefense;
    public int thunderDefense;
    public int earthDefense;
    public int health;
    public String accessoryType;
    public String classRequirement;
    public boolean identified = false;
    public String displayName;
    public String skin;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Color.class, new ColorDeserialiser());
        builder.registerTypeHierarchyAdapter(ItemType.class, new ItemType.ItemTypeDeserialiser());
        builder.registerTypeHierarchyAdapter(UUID.class, new UUIDTypeAdapter());
        builder.registerTypeHierarchyAdapter(HashMap.class, new HashMapDeserialiser());
        builder.registerTypeHierarchyAdapter(ItemRarity.class, new ItemRarity.ItemRariryDeserializer());
        GSON = builder.create();
    }

    public boolean isIdentified() {
        return identified;
    }

    public String getClassRequirement() {
        return classRequirement;
    }

    public String getName() {
        return displayName == null ? name : displayName;
    }

    public ItemRarity getTier() {
        return tier;
    }

    public int getSockets() {
        return sockets;
    }

    public String getType() {
        return type;
    }

    public ItemType getMaterial() {
        return material;
    }

    public String getDropType() {
        return dropType;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public String getDamage() {
        return damage;
    }

    public String getFireDamage() {
        return fireDamage;
    }

    public String getWaterDamage() {
        return waterDamage;
    }

    public String getAirDamage() {
        return airDamage;
    }

    public String getThunderDamage() {
        return thunderDamage;
    }

    public String getEarthDamage() {
        return earthDamage;
    }

    public String getAttackSpeed() {
        return attackSpeed;
    }

    public int getLevel() {
        return level;
    }

    public String getQuest() {
        return quest;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public int getDefense() {
        return defense;
    }

    public int getHealthRegen() {
        return healthRegen;
    }

    public int getManaRegen() {
        return manaRegen;
    }

    public int getSpellDamage() {
        return spellDamage;
    }

    public int getDamageBonus() {
        return damageBonus;
    }

    public int getLifeSteal() {
        return lifeSteal;
    }

    public int getManaSteal() {
        return manaSteal;
    }

    public int getXpBonus() {
        return xpBonus;
    }

    public int getLootBonus() {
        return lootBonus;
    }

    public int getReflection() {
        return reflection;
    }

    public int getStrengthPoints() {
        return strengthPoints;
    }

    public int getDexterityPoints() {
        return dexterityPoints;
    }

    public int getIntelligencePoints() {
        return intelligencePoints;
    }

    public int getAgilityPoints() {
        return agilityPoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }

    public int getThorns() {
        return thorns;
    }

    public int getExploding() {
        return exploding;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAttackSpeedBonus() {
        return attackSpeedBonus;
    }

    public int getPoison() {
        return poison;
    }

    public int getHealthBonus() {
        return healthBonus;
    }

    public int getSoulPoints() {
        return soulPoints;
    }

    public int getEmeraldStealing() {
        return emeraldStealing;
    }

    public int getHealthRegenRaw() {
        return healthRegenRaw;
    }

    public int getSpellDamageRaw() {
        return spellDamageRaw;
    }

    public int getDamageBonusRaw() {
        return damageBonusRaw;
    }

    public int getBonusFireDamage() {
        return bonusFireDamage;
    }

    public int getBonusWaterDamage() {
        return bonusWaterDamage;
    }

    public int getBonusAirDamage() {
        return bonusAirDamage;
    }

    public int getBonusThunderDamage() {
        return bonusThunderDamage;
    }

    public int getBonusEarthDamage() {
        return bonusEarthDamage;
    }

    public int getBonusFireDefense() {
        return bonusFireDefense;
    }

    public int getBonusWaterDefense() {
        return bonusWaterDefense;
    }

    public int getBonusAirDefense() {
        return bonusAirDefense;
    }

    public int getBonusThunderDefense() {
        return bonusThunderDefense;
    }

    public int getBonusEarthDefense() {
        return bonusEarthDefense;
    }

    public String getCategory() {
        return category;
    }

    public Color getArmorColor() {
        return armorColor;
    }

    public String getArmorType() {
        return armorType;
    }

    public String getAddedLore() {
        return addedLore;
    }

    public String getSet() {
        return set;
    }

    public int getFireDefense() {
        return fireDefense;
    }

    public int getWaterDefense() {
        return waterDefense;
    }

    public int getAirDefense() {
        return airDefense;
    }

    public int getThunderDefense() {
        return thunderDefense;
    }

    public int getEarthDefense() {
        return earthDefense;
    }

    public int getHealth() {
        return health;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public String getDisplayName() {
        return  displayName;
    }

    public String getSkin() {
        return skin;
    }

    transient Pair<ItemStack, ArrayList<String>> generated = null;
    public Pair<ItemStack, ArrayList<String>> asStack() {
        if(generated == null) {
            ItemStack original;

            if (material != null) {
                original = material.getAsStack();
            } else {
                Item result = null;
                Block resultBlock = null;
                
                if(accessoryType != null) {
                    if (accessoryType.equalsIgnoreCase("Necklace")) {
                        resultBlock = Blocks.GLASS_PANE;
                    } else if (accessoryType.equalsIgnoreCase("Bracelet")) {
                        resultBlock = Blocks.SPRUCE_FENCE;
                    } else if (accessoryType.equalsIgnoreCase("Ring")) {
                        resultBlock = Blocks.STAINED_GLASS;
                    }
                }else if (type != null && !type.isEmpty()) {
                    if(armorType == null) {
                        if (type.equalsIgnoreCase("Wand")) {
                            result = Items.STICK;
                        } else if (type.equalsIgnoreCase("Spear")) {
                            result = Items.IRON_SHOVEL;
                        } else if (type.equalsIgnoreCase("Dagger")) {
                            result = Items.SHEARS;
                        } else if (type.equalsIgnoreCase("Bow")) {
                            result = Items.BOW;
                        }
                    }else{
                        if (type.equalsIgnoreCase("Helmet")) {
                            if (armorType.equalsIgnoreCase("Leather")) {
                                result = Items.LEATHER_HELMET;
                            }
                            if (armorType.equalsIgnoreCase("Diamond")) result = Items.DIAMOND_HELMET;
                            if (armorType.equalsIgnoreCase("Iron")) result = Items.IRON_HELMET;
                            if (armorType.equalsIgnoreCase("Golden")) result = Items.GOLDEN_HELMET;
                            if (armorType.equalsIgnoreCase("Chain")) result = Items.CHAINMAIL_HELMET;
                            if (skin != null) result = Items.SKULL;
                        } else if (type.equalsIgnoreCase("Chestplate")) {
                            if (armorType.equalsIgnoreCase("Leather")) {
                                result = Items.LEATHER_CHESTPLATE;
                            }
                            if (armorType.equalsIgnoreCase("Diamond")) result = Items.DIAMOND_CHESTPLATE;
                            if (armorType.equalsIgnoreCase("Iron")) result = Items.IRON_CHESTPLATE;
                            if (armorType.equalsIgnoreCase("Golden")) result = Items.GOLDEN_CHESTPLATE;
                            if (armorType.equalsIgnoreCase("Chain")) result = Items.CHAINMAIL_CHESTPLATE;
                        } else if (type.equalsIgnoreCase("Leggings")) {
                            if (armorType.equalsIgnoreCase("Leather")) {
                                result = Items.LEATHER_LEGGINGS;
                            }
                            if (armorType.equalsIgnoreCase("Diamond")) result = Items.DIAMOND_LEGGINGS;
                            if (armorType.equalsIgnoreCase("Iron")) result = Items.IRON_LEGGINGS;
                            if (armorType.equalsIgnoreCase("Golden")) result = Items.GOLDEN_LEGGINGS;
                            if (armorType.equalsIgnoreCase("Chain")) result = Items.CHAINMAIL_LEGGINGS;
                        } else if (type.equalsIgnoreCase("Boots")) {
                            if (armorType.equalsIgnoreCase("Leather")) {
                                result = Items.LEATHER_BOOTS;
                            }
                            if (armorType.equalsIgnoreCase("Diamond")) result = Items.DIAMOND_BOOTS;
                            if (armorType.equalsIgnoreCase("Iron")) result = Items.IRON_BOOTS;
                            if (armorType.equalsIgnoreCase("Golden")) result = Items.GOLDEN_BOOTS;
                            if (armorType.equalsIgnoreCase("Chain")) result = Items.CHAINMAIL_BOOTS;
                        }
                    }
                }
                if (result == null && resultBlock == null) return null;

                if(resultBlock != null) {
                    original = new ItemStack(resultBlock);
                }else{ original = new ItemStack(result); }

                if (skin != null && type.equalsIgnoreCase("Helmet")) {
                    original.setItemDamage(3);
                    MinecraftTexturesPayload deserializedSkin = GSON.fromJson(new String(Base64.decodeBase64(skin), Charsets.UTF_8), MinecraftTexturesPayload.class);
                    UUID uuid = deserializedSkin.getProfileId();
                    if (uuid == null) uuid = UUID.randomUUID();
                    GameProfile profile = new GameProfile(uuid, deserializedSkin.getProfileName());
                    profile.getProperties().put("textures", new Property("textures", skin));
                    NBTUtil.writeGameProfile(original.getOrCreateSubCompound("SkullOwner"), profile);
                } else if (armorType != null && armorType.equals("Leather") && armorColor != null) {
                    ((ItemArmor) original.getItem()).setColor(original, (armorColor.getRed() << 16) + (armorColor.getGreen() << 8) + armorColor.getBlue());
                }
            }

            ArrayList<String> description = new ArrayList<>();

            switch (getTier()) {
                case LEGENDARY:
                    description.add(TextFormatting.AQUA + getName());
                    break;
                case MYTHIC:
                    description.add(TextFormatting.DARK_PURPLE + getName());
                    break;
                case RARE:
                    description.add(TextFormatting.LIGHT_PURPLE + getName());
                    break;
                case UNIQUE:
                    description.add(TextFormatting.YELLOW + getName());
                    break;
                case SET:
                    description.add(TextFormatting.GREEN + getName());
                    break;
                default:
                    description.add(getName());
            }

            if(attackSpeed != null) {
                switch (getAttackSpeed().toLowerCase()) {
                    case "super_fast":
                        description.add(TextFormatting.GRAY + "Super Fast Attack Speed");
                        break;
                    case "super_slow":
                        description.add(TextFormatting.GRAY + "Super Slow Attack Speed");
                        break;
                    case "very_fast":
                        description.add(TextFormatting.GRAY + "Very Fast Attack Speed");
                        break;
                    case "normal":
                        description.add(TextFormatting.GRAY + "Normal Attack Speed");
                        break;
                    case "very_slow":
                        description.add(TextFormatting.GRAY + "Very Slow Attack Speed");
                        break;
                    case "fast":
                        description.add(TextFormatting.GRAY + "Fast Attack Speed");
                        break;
                    case "slow":
                        description.add(TextFormatting.GRAY + "Slow Attack Speed");
                        break;
                }

                description.add(" ");

                if(damage != null && !damage.equals("0-0")) description.add(TextFormatting.GOLD + "✣ Neutral Damage: " + getDamage());
                if(fireDamage != null && !fireDamage.equals("0-0")) description.add(TextFormatting.RED + "✹ Fire " + TextFormatting.GRAY + "Damage: " + getFireDamage());
                if(waterDamage != null && !waterDamage.equals("0-0")) description.add(TextFormatting.AQUA + "❉ Water " + TextFormatting.GRAY + "Damage: " + getWaterDamage());
                if(airDamage != null && !airDamage.equals("0-0")) description.add(TextFormatting.WHITE + "❋ Air " + TextFormatting.GRAY + "Damage: " + getAirDamage());
                if(thunderDamage != null && !thunderDamage.equals("0-0")) description.add(TextFormatting.YELLOW + "✦ Thunder " + TextFormatting.GRAY + "Damage: " + getThunderDamage());
                if(earthDamage != null && !earthDamage.equals("0-0")) description.add(TextFormatting.DARK_GREEN + "✤ Earth " + TextFormatting.GRAY + "Damage: " + getEarthDamage());
            }
            if(category != null && category.equalsIgnoreCase("armor")) {
                description.add(" ");

                description.add(TextFormatting.DARK_RED + "❤ Health: " + (getHealth() < 0 ? "" : "+") + getHealth());
                if(fireDefense != 0) description.add(TextFormatting.RED + "✹ Fire " + TextFormatting.GRAY + "Defense: " + (getFireDefense() > 0 ? "+" + getFireDefense() : getFireDefense()));
                if(waterDefense != 0) description.add(TextFormatting.AQUA + "❉ Water " + TextFormatting.GRAY + "Defense: " + (getWaterDefense() > 0 ? "+" + getWaterDefense() : getWaterDefense()));
                if(airDefense != 0) description.add(TextFormatting.WHITE + "❋ Air " + TextFormatting.GRAY + "Defense: " + (getAirDefense() > 0 ? "+" + getAirDefense() : getAirDefense()));
                if(thunderDefense != 0) description.add(TextFormatting.YELLOW + "✦ Thunder " + TextFormatting.GRAY + "Defense: " + (getThunderDefense() > 0 ? "+" + getThunderDefense() : getThunderDefense()));
                if(earthDefense != 0) description.add(TextFormatting.DARK_GREEN + "✤ Earth " + TextFormatting.GRAY + "Defense: " + (getEarthDefense() > 0 ? "+" + getEarthDefense() : getEarthDefense()));
            }

            description.add(" ");

            if(quest != null) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Quest Req: " + getQuest());
            description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Lv. Min: " + getLevel());
            if(strength != 0) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Strength Min: " + getStrength());
            if(agility != 0) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Agility Min: " + getAgility());
            if(defense != 0) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Defense Min: " + getDefense());
            if(intelligence != 0) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Intelligence Min: " + getIntelligence());
            if(dexterity != 0) description.add(TextFormatting.GREEN + "✔ " + TextFormatting.GRAY + "Dexterity Min: " + getDexterity());
            description.add(" ");

            if(attackSpeedBonus != 0) description.add(calculateStatus(attackSpeedBonus, identified, "") + " " + TextFormatting.GRAY + "Attack Speed");

            if(damageBonusRaw != 0) description.add(calculateStatus(damageBonusRaw, identified, "") + " " + TextFormatting.GRAY + "Melee Damage");
            if(damageBonus != 0) description.add(calculateStatus(damageBonus, identified, "%") + " " + TextFormatting.GRAY + "Melee Damage");

            if(spellDamageRaw != 0) description.add(calculateStatus(spellDamageRaw, identified, "") + " " + TextFormatting.GRAY + "Spell Damage");
            if(spellDamage != 0) description.add(calculateStatus(spellDamage, identified, "%") + " " + TextFormatting.GRAY + "Spell Damage");

            if(healthBonus != 0) description.add(calculateStatus(healthBonus, identified, "") + " " + TextFormatting.GRAY + "Health");
            if(healthRegenRaw != 0) description.add(calculateStatus(healthRegenRaw, identified, "") + " " + TextFormatting.GRAY + "Health Regen");
            if(healthRegen != 0) description.add(calculateStatus(healthRegen, identified, "%") + " " + TextFormatting.GRAY + "Health Regen");

            if(lifeSteal != 0) description.add(calculateStatus(lifeSteal, identified, "/4s") + " " + TextFormatting.GRAY + "Life Steal");
            if(manaRegen != 0) description.add(calculateStatus(manaRegen, identified, "/4s") + " " + TextFormatting.GRAY + "Mana Regen");
            if(manaSteal != 0) description.add(calculateStatus(manaSteal, identified, "/4s") + " " + TextFormatting.GRAY + "Mana Steal");

            if(bonusFireDamage != 0) description.add(calculateStatus(bonusFireDamage, identified, "%") + " " + TextFormatting.GRAY + "Fire Damage");
            if(bonusWaterDamage != 0) description.add(calculateStatus(bonusWaterDamage, identified, "%") + " " + TextFormatting.GRAY + "Water Damage");
            if(bonusAirDamage != 0) description.add(calculateStatus(bonusAirDamage, identified, "%") + " " + TextFormatting.GRAY + "Air Damage");
            if(bonusThunderDamage != 0) description.add(calculateStatus(bonusThunderDamage, identified, "%") + " " + TextFormatting.GRAY + "Thunder Damage");
            if(bonusEarthDamage != 0) description.add(calculateStatus(bonusEarthDamage, identified, "%") + " " + TextFormatting.GRAY + "Earth Damage");

            if(bonusFireDefense != 0) description.add(calculateStatus(bonusFireDefense, identified, "%") + " " + TextFormatting.GRAY + "Fire Defense");
            if(bonusWaterDefense != 0) description.add(calculateStatus(bonusWaterDefense, identified, "%") + " " + TextFormatting.GRAY + "Water Defense");
            if(bonusAirDefense != 0) description.add(calculateStatus(bonusAirDefense, identified, "%") + " " + TextFormatting.GRAY + "Air Defense");
            if(bonusThunderDefense != 0) description.add(calculateStatus(bonusThunderDefense, identified, "%") + " " + TextFormatting.GRAY + "Thunder Defense");
            if(bonusEarthDefense != 0) description.add(calculateStatus(bonusEarthDefense, identified, "%") + " " + TextFormatting.GRAY + "Earth Defense");

            if(strengthPoints != 0) description.add(calculateStatus(strengthPoints, identified, "") + " " + TextFormatting.GRAY + "Strength");
            if(dexterityPoints != 0) description.add(calculateStatus(dexterityPoints, identified, "") + " " + TextFormatting.GRAY + "Dexterity");
            if(intelligencePoints != 0) description.add(calculateStatus(intelligencePoints, identified, "") + " " + TextFormatting.GRAY + "Intelligence");
            if(defensePoints != 0) description.add(calculateStatus(defensePoints, identified, "") + " " + TextFormatting.GRAY + "Defense");
            if(agilityPoints != 0) description.add(calculateStatus(agilityPoints, identified, "") + " " + TextFormatting.GRAY + "Agility");

            if(speed != 0) description.add(calculateStatus(speed, identified, "%") + " " + TextFormatting.GRAY + "Walk Speed");
            if(exploding != 0) description.add(calculateStatus(exploding, identified, "%") + " " + TextFormatting.GRAY + "Exploding");
            if(poison != 0) description.add(calculateStatus(poison, identified, "") + " " + TextFormatting.GRAY + "Poison");
            if(thorns != 0) description.add(calculateStatus(thorns, identified, "%") + " " + TextFormatting.GRAY + "Thorns");
            if(reflection != 0) description.add(calculateStatus(reflection, identified, "%") + " " + TextFormatting.GRAY + "Reflection");
            if(soulPoints != 0) description.add(calculateStatus(soulPoints, identified, "%") + " " + TextFormatting.GRAY + "Soul Point Regen");
            if(emeraldStealing != 0) description.add(calculateStatus(emeraldStealing, identified, "%") + " " + TextFormatting.GRAY + "Stealing");
            if(lootBonus != 0) description.add(calculateStatus(lootBonus, identified, "%") + " " + TextFormatting.GRAY + "Loot Bonus");
            if(xpBonus != 0) description.add(calculateStatus(xpBonus, identified, "%") + " " + TextFormatting.GRAY + "XP Bonus");

            if (!description.get(description.size() - 1).equals(" ")) {
                description.add(" ");
            }
            if (sockets != 0) {
                description.add(TextFormatting.GRAY + "[" + sockets + " Powder Slot" + (sockets != 1 ? "s" : "") + "]");
            }

            switch (getTier()) {
                case LEGENDARY:
                    description.add(TextFormatting.AQUA + "Legendary Item");
                    break;
                case MYTHIC:
                    description.add(TextFormatting.DARK_PURPLE + "Mythic Item");
                    break;
                case RARE:
                    description.add(TextFormatting.LIGHT_PURPLE + "Rare Item");
                    break;
                case UNIQUE:
                    description.add(TextFormatting.YELLOW + "Unique Item");
                    break;
                case SET:
                    description.add(TextFormatting.GREEN + "Set Item");
                    break;
                default:
                    break;
            }
            
            if (description.get(description.size() - 1).equals(" ")) {
                description.remove(description.size() - 1);
            }

            generated = new Pair<>(original, description);
        }

        return generated;
    }

    private static String calculateStatus(int rawStatus, boolean identified, String format) {
        int maxStatus = (int)Math.round(rawStatus * 1.3);
        int minStatus = (rawStatus < 0 ? (int)Math.round(rawStatus * 0.7) : (int)Math.round(rawStatus * 0.3));

        return identified ? (rawStatus > 0 ? TextFormatting.GREEN + "+" + rawStatus + format : TextFormatting.RED + (rawStatus + format)) : (rawStatus > 0 ? TextFormatting.GREEN + "+" + minStatus + format + TextFormatting.GRAY + " to " + TextFormatting.GREEN + "+" + maxStatus + format : TextFormatting.RED + (minStatus + format) + TextFormatting.GRAY + " to " + TextFormatting.RED + maxStatus + format);
    }

    public static class ItemType {
        private Item item = null;

        private int damage = 0;

        private ItemStack asStack;

        public ItemType(Item item, int damage) {
            this.item = item;
            this.damage = damage;

            asStack = new ItemStack(item);
            asStack.setItemDamage(damage);
        }

        public Item getItem() {
            return item;
        }

        public int getDamage() {
            return damage;
        }

        public ItemStack getAsStack() {
            return asStack;
        }

        public static class ItemTypeDeserialiser implements JsonDeserializer<ItemType> {

            @Override
            public ItemType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                String itemType = json.getAsString();
                String[] itemIDs = itemType.split(":");
                int itemID = Integer.valueOf(itemIDs[0]);
                int damage = itemIDs.length == 2 ? Integer.valueOf(itemIDs[1]) : 0;
                Item item = Item.REGISTRY.getObjectById(itemID);
                return new ItemType(item, damage);
            }

        }
    }

    public static class ColorDeserialiser implements JsonDeserializer<Color> {

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String color = json.getAsString();
            String[] colors = color.split("[^\\d]");
            int red = Integer.valueOf(colors[0]);
            int green = Integer.valueOf(colors[1]);
            int blue = Integer.valueOf(colors[2]);
            return new Color(red, green, blue);
        }

    }

    public static class HashMapDeserialiser implements JsonDeserializer<HashMap<String, ItemProfile>> {

        @Override
        public HashMap<String, ItemProfile> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            HashMap<String, ItemProfile> items = new HashMap<>();
            for (JsonElement element : json.getAsJsonArray()) {
                String name;
                if(element.getAsJsonObject().has("displayName")) name = element.getAsJsonObject().get("displayName").getAsString();
                else name = element.getAsJsonObject().get("name").getAsString();
                ItemProfile item = context.deserialize(element, ItemProfile.class);
                items.put(name, item);
            }
            return items;
        }

    }

    public static enum ItemRarity {

        NORMAL(0), SET(1), UNIQUE(2), RARE(3), LEGENDARY(4), MYTHIC(5);

        int id;

        ItemRarity(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }

        public static class ItemRariryDeserializer implements JsonDeserializer<ItemRarity> {

            @Override
            public ItemRarity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return ItemRarity.valueOf(json.getAsString().toUpperCase());
            }

        }
    }

}
