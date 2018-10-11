package cf.wynntils.webapi.profiles.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.item.Item;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public class ItemProfile {

    public static Gson GSON;

    public String name;
    public String tier;
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
    public boolean identified;
    public String displayName;
    public MinecraftTexturesPayload skin;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeHierarchyAdapter(Color.class, new ColorDeserialiser());
        builder.registerTypeHierarchyAdapter(ItemType.class, new ItemType.ItemTypeDeserialiser());
        builder.registerTypeHierarchyAdapter(MinecraftTexturesPayload.class, new SkinDeserialiser());
        builder.registerTypeHierarchyAdapter(HashMap.class, new HashMapDeserialiser());
        GSON = builder.create();
    }

    public boolean isIdentified() {
        return identified;
    }

    public String getClassRequirement() {
        return classRequirement;
    }

    public String getName() {
        return name;
    }

    public String getTier() {
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

    public MinecraftTexturesPayload getSkin() {
        return skin;
    }

    public static class ItemType {
        private Item item = null;

        private int damage = 0;

        public ItemType(Item item, int damage) {
            this.item = item;
            this.damage = damage;
        }

        public Item getItem() {
            return item;
        }

        public int getDamage() {
            return damage;
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

    public static class SkinDeserialiser implements JsonDeserializer<MinecraftTexturesPayload> {

        @Override
        public MinecraftTexturesPayload deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String texturesPayLoad = new String(Base64.decodeBase64(json.getAsString()), Charsets.UTF_8);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
            return builder.create().fromJson(texturesPayLoad, MinecraftTexturesPayload.class);
        }

    }

    public static class HashMapDeserialiser implements JsonDeserializer<HashMap<String, ItemProfile>> {

        @Override
        public HashMap<String, ItemProfile> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            HashMap<String, ItemProfile> items = new HashMap<>();
            for (JsonElement element : json.getAsJsonArray()) {
                String name = element.getAsJsonObject().get("name").getAsString();
                ItemProfile item = context.deserialize(element, ItemProfile.class);
                items.put(name, item);
            }
            return items;
        }

    }
}
