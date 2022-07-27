/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.HorseData;
import com.wynntils.core.framework.instances.data.*;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.core.managers.PingManager;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.interfaces.InfoModule;
import com.wynntils.modules.utilities.managers.*;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoFormatter {

    public Map<String, String> cache = new HashMap<>();
    public Map<String, InfoModule> formatters = new HashMap<>();

    private int tick = 0;

    private static final Pattern formatRegex = Pattern.compile(
        "%([a-zA-Z_]+|%)%|\\\\([\\\\n%§EBLMH]|x[0-9A-Fa-f]{2}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})"
    );

    public InfoFormatter() {
        // Escape for % character
        registerFormatter((input) ->
                "%",
                "%");

        // Blocks per second
        registerFormatter((input) ->
                CharacterData.PER_FORMAT.format(SpeedometerManager.getCurrentSpeed()),
                "bps");

        // Blocks per minute
        registerFormatter((input) ->
                CharacterData.PER_FORMAT.format(SpeedometerManager.getCurrentSpeed() * 60),
                "bpm");

        // Kilometers per hour (1000 blocks per hour)
        registerFormatter((input) ->
                CharacterData.PER_FORMAT.format(SpeedometerManager.getCurrentSpeed() * 3.6),
                "kmph");

        // X coordinate
        registerFormatter((input) ->
                Integer.toString((int) McIf.player().posX),
                "x");

        // Y coordinate
        registerFormatter((input) ->
                Integer.toString((int) McIf.player().posY),
                "y");

        // Z coordinate
        registerFormatter((input) ->
                Integer.toString((int) McIf.player().posZ),
                "z");

        // The facing cardinal direction
        registerFormatter((input) ->
                Utils.getPlayerDirection(McIf.player().rotationYaw),
                "dir");

        // Frames per second
        registerFormatter((input) ->
                Integer.toString(Minecraft.getDebugFPS()),
                "fps");

        // The world/server number
        registerFormatter((input) ->
                Reference.inStream ? "WC -" : Reference.getUserWorld(),
                "world");

        // The ping time to the server
        registerFormatter((input) -> {
            PingManager.calculatePing();
            return Long.toString(PingManager.getLastPing());
        }, "ping");

        // The wall clock time, formatted in the current locale style
        registerFormatter((input) -> {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
            return date.format(formatter);
        }, "clock");

        // The wall clock time, formatted to 24h format
        registerFormatter((input) -> {
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return date.format(formatter);
        }, "clockm");

        // Current mana
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(CharacterData.class).getCurrentMana()),
                "mana");

        // Max mana
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(CharacterData.class).getMaxMana()),
                "mana_max");

        // Current health
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(CharacterData.class).getCurrentHealth()),
                "health");

        // Max health
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(CharacterData.class).getMaxHealth()),
                "health_max");

        // Health percentage
        registerFormatter((input) -> {
            double currentHealth = PlayerInfo.get(CharacterData.class).getCurrentHealth();
            double maxHealth = PlayerInfo.get(CharacterData.class).getMaxHealth();
            return Integer.toString((int)Math.round(currentHealth / maxHealth * 100));
        }, "health_pct");

        // Current XP (formatted)
        registerFormatter((input) ->
                StringUtils.integerToShortString(PlayerInfo.get(CharacterData.class).getCurrentXP()),
                "xp");

        // Current XP (raw)
        registerFormatter((input) ->
                Long.toString(PlayerInfo.get(CharacterData.class).getCurrentXP()),
                "xp_raw");

        // XP required to level up (formatted)
        registerFormatter((input) ->
                StringUtils.integerToShortString(PlayerInfo.get(CharacterData.class).getXpNeededToLevelUp()),
                "xp_req");

        // XP required to level up (raw)
        registerFormatter((input) ->
                Long.toString(PlayerInfo.get(CharacterData.class).getXpNeededToLevelUp()),
                "xp_req_raw");

        // Percentage XP to next level
        registerFormatter((input) ->
                PlayerInfo.get(CharacterData.class).getCurrentXPAsPercentage(),
                "xp_pct");

        // Horse XP
        registerFormatter((input) -> {
            if (!cache.containsKey("horsexp")) {
                cacheHorseData();
            }

            return cache.get("horsexp");
        }, "horse_xp", "h_xp");

        // Horse Level
        registerFormatter((input) -> {
            if (!cache.containsKey("horselevel")) {
                cacheHorseData();
            }

            return cache.get("horselevel");
        }, "horse_level", "h_lvl");

        // Max horse level
        registerFormatter((input) -> {
            if (!cache.containsKey("horselevelmax")) {
                cacheHorseData();
            }

            return cache.get("horselevelmax");
        }, "horse_level_max", "h_mlvl");

        // Horse Tier
        registerFormatter((input) -> {
            if (!cache.containsKey("horsetier")) {
                cacheHorseData();
            }

            return cache.get("horsetier");
        }, "horse_tier", "h_tier");

        // Approximate time (in minutes) until next horse level
        registerFormatter((input) -> {
            if (PlayerInfo.get(HorseData.class).getLevel() != PlayerInfo.get(HorseData.class).getMaxLevel() && PlayerInfo.get(HorseData.class).getInventorySlot() >= 1 && PlayerInfo.get(HorseData.class).getInventorySlot() <= 9) {
                return Double.toString((double) Math.ceil((3.0 * PlayerInfo.get(HorseData.class).getLevel() + 2) / 6.0 * (100.0 - PlayerInfo.get(HorseData.class).getXp()) / 100 * 10) / 10);
                // This is based off of a formula from https://wynncraft.fandom.com/wiki/Horses#Levels
            }
            else {
                return "-";
            }
        }, "horse_time_estimate", "h_te");

        // Number of items in ingredient pouch
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getIngredientPouchCount(false)),
                "pouch");

        // Number of free slots in ingredient pouch
        registerFormatter((input) ->
                Integer.toString(27 - PlayerInfo.get(InventoryData.class).getIngredientPouchCount(true)),
                "pouch_free");

        // Number of used slots in ingredient pouch
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getIngredientPouchCount(true)),
                "pouch_slots");

        // Number of free slots in the inventory
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getFreeInventorySlots()),
                "inv_free");

        // Number of used slots in the inventory
        registerFormatter((input) ->
                Integer.toString(28 - PlayerInfo.get(InventoryData.class).getFreeInventorySlots()),
                "inv_slots");

        // Current territory
        registerFormatter((input) ->
                PlayerInfo.get(LocationData.class).getLocation(),
                "location", "loc");


        // Current guild that owns current territory
        registerFormatter((input) -> {
                    String territory = PlayerInfo.get(LocationData.class).getLocation();
                    if (territory.isEmpty()) return "";

                    TerritoryProfile profile = WebManager.getTerritories().get(territory);

                    if (profile == null) {
                        Reference.LOGGER.warn(String.format("Invalid territory for %%territory_owner%% %s", territory));
                        return "?";
                    }

                    return profile.getGuild();
                },
                "territory_owner", "terguild");


        // Current guild that owns current territory (prefix)
        registerFormatter((input) -> {
                    String territory = PlayerInfo.get(LocationData.class).getLocation();
                    if (territory.isEmpty()) return "";

                    TerritoryProfile profile = WebManager.getTerritories().get(territory);

                    if (profile == null) {
                        Reference.LOGGER.warn(String.format("Invalid territory for %%territory_owner_prefix%% %s", territory));
                        return "?";
                    }

                    return profile.getGuildPrefix();
                    },
                "territory_owner_prefix", "terguild_pref");

        // Distance from compass beacon
        registerFormatter((input) ->{
            Location compass = CompassManager.getCompassLocation();
            Location playerPos = new Location(McIf.player());

            if (compass == null) return "";
            return String.valueOf((int) compass.distance(playerPos));
        }, "beacon_distance", "beacdist");


        // Current level
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(CharacterData.class).getLevel()),
                "level", "lvl");

        // Time until soul point (formatted)
        registerFormatter((input) -> {
            if (!cache.containsKey("soulpointtimer")) {
                cacheSoulPointTimer();
            }

            return cache.get("soulpointtimer");
        }, "soulpoint_timer", "sp_timer");

        // Minutes until soul point
        registerFormatter((input) -> {
            if (!cache.containsKey("soulpointsminutes")) {
                cacheSoulPointTimer();
            }

            return cache.get("soulpointsminutes");
        },"soulpoint_timer_m", "sp_timer_m");

        // Seconds until soul point
        registerFormatter((input) -> {
            if (!cache.containsKey("soulpointseconds")) {
                cacheSoulPointTimer();
            }

            return cache.get("soulpointseconds");
        }, "soulpointtimer_s", "sptimer_s");

        // Mana regen timer, dependent on the timer overlay config
        registerFormatter((input) -> {
            if (!cache.containsKey("manaregenformatted")) {
                cacheManaRegenTimer();
            }

            return cache.get("manaregenformatted");
        },"manaregen_timer", "mr_timer");

        // Current soul points
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getSoulPoints()),
                "soulpoints", "sp");

        // Max soul points
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getMaxSoulPoints()),
                "soulpoints_max", "sp_max");

        // Total money in inventory
        registerFormatter((input) -> {
            if (!cache.containsKey("money")) {
                cacheMoney();
            }

            return cache.get("money");
        }, "money");

        // Total money in inventory, formatted as le, blocks and emeralds
        registerFormatter((input) -> {
            if (!cache.containsKey("money_desc")) {
                cacheMoney();
            }

            return cache.get("money_desc");
        }, "money_desc");

        // Count of full liquid emeralds in inventory
        registerFormatter((input) -> {
            if (!cache.containsKey("liquid")) {
                cacheMoney();
            }

            return cache.get("liquid");
        }, "le", "liquidemeralds");

        // Count of full emerald blocks in inventory (excluding that in liquid emerald counter)
        registerFormatter((input) -> {
            if (!cache.containsKey("blocks")) {
                cacheMoney();
            }

            return cache.get("blocks");
        }, "eb", "blocks", "emeraldblocks");

        // Count of emeralds in inventory (excluding that in liquid emerald and block counters)
        registerFormatter((input) -> {
            if (!cache.containsKey("emeralds")) {
                cacheMoney();
            }

            return cache.get("emeralds");
        }, "e", "emeralds");

        // health pot charges
        registerFormatter((input) -> {
            if (!cache.containsKey("potionchargesremaining")) {
                cachePotionCharges();
            }

            return cache.get("potionchargesremaining");
        },"potions_health_charges", "hp_pot_charges");

        // max health pot charges
        registerFormatter((input) -> {
            if (!cache.containsKey("potionchargesmax")) {
                cachePotionCharges();
            }

            return cache.get("potionchargesmax");
        }, "potions_health_max", "hp_pot_max");

        // combined health pot charges
        registerFormatter((input) -> {
            if (!cache.containsKey("potionchargescombined")) {
                cachePotionCharges();
            }

            return cache.get("potionchargescombined");
        }, "potions_health_combined", "hp_pot_combined");

        // Count of mana potions
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(InventoryData.class).getManaPotions()),
                "potions_mana", "mp_pot");

        // Current class
        registerFormatter((input) -> {
            String className = PlayerInfo.get(CharacterData.class).getCurrentClass().name().toLowerCase();

            if (input.equals("Class")) {  // %Class% is title case
                className = StringUtils.capitalizeFirst(className);
            } else if (input.equals("CLASS")) {  // %CLASS% is all caps
                className = className.toUpperCase();
            }

            return className;
        }, "class");

        // Max allocated memory
        registerFormatter((input) -> {
            if (!cache.containsKey("memorymax")) {
                cacheMemory();
            }

            return cache.get("memorymax");
        }, "memmax", "mem_max");

        // Current used memory
        registerFormatter((input) -> {
            if (!cache.containsKey("memoryused")) {
                cacheMemory();
            }

            return cache.get("memoryused");
        }, "memused", "mem_used");

        // Current used memory percent
        registerFormatter((input) -> {
            if (!cache.containsKey("memorypct")) {
                cacheMemory();
            }

            return cache.get("memorypct");
        }, "mempct", "mem_pct");

        // Number of players in the party
        registerFormatter((input) ->
                Integer.toString(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().size()),
                "party_count");

        // Owner of players party
        registerFormatter((input) ->
                PlayerInfo.get(SocialData.class).getPlayerParty().getOwner(),
                "party_owner");

        registerFormatter((input) ->
                String.valueOf(AreaDPSManager.getCurrentDPS()),
                "adps", "areadps");

        registerFormatter((input ->
                StringUtils.integerToShortString(LevelingManager.getXpPerMinute())),
                "xpm");

        registerFormatter((input ->
                String.valueOf(LevelingManager.getXpPerMinute())),
                "xpm_raw");

        registerFormatter((input ->
                LevelingManager.getXpPercentPerMinute()),
                "xppm");

        registerFormatter((input ->
                StringUtils.integerToShortString(KillsManager.getKillsPerMinute())),
                "kpm");

        registerFormatter((input ->
                String.valueOf(KillsManager.getKillsPerMinute())),
                "kpm_raw");

        // Uptime variables
        registerFormatter((input) ->
                Reference.inStream ? "-" : ServerListManager.getUptimeHours(Reference.getUserWorld()),
                "uptime_h");

        registerFormatter((input) ->
                Reference.inStream ? "-" : ServerListManager.getUptimeMinutes(Reference.getUserWorld()),
                "uptime_m");

        //Dry streak counter
        registerFormatter((input) ->
                !UtilitiesConfig.INSTANCE.enableDryStreak ? "-" : String.valueOf(UtilitiesConfig.INSTANCE.dryStreakCount),
                "dry_streak");

        registerFormatter((input) ->
                        !UtilitiesConfig.INSTANCE.enableDryStreak ? "-" : String.valueOf(UtilitiesConfig.INSTANCE.dryStreakBoxes),
                "dry_boxes", "dry_streak_boxes");
    }

    private void registerFormatter(InfoModule formatter, String... vars) {
        for (String var : vars) {
            formatters.put(var, formatter);
        }
    }

    String doEscapeFormat(String escaped) {
        switch (escaped) {
            case "\\": return "\\\\";
            case "n": return "\n";
            case "%": return "%";
            case "§": return "&";
            case "E": return EmeraldSymbols.E_STRING;
            case "B": return EmeraldSymbols.B_STRING;
            case "L": return EmeraldSymbols.L_STRING;
            case "M": return "✺";
            case "H": return "❤";
            default:
                // xXX, uXXXX, UXXXXXXXX
                int codePoint = Integer.parseInt(escaped.substring(1), 16);
                if (Utils.StringUtils.isValidCodePoint(codePoint)) {
                    return new String(new int[]{ codePoint }, 0, 1);
                }
                return null;
        }
    }

    public String doFormat(String format) {
        StringBuffer sb = new StringBuffer(format.length() + 10);
        Matcher m = formatRegex.matcher(format);
        while (m.find()) {
            String replacement = null;
            String group;
            if ((group = m.group(1)) != null) {
                // %name%
                InfoModule module = formatters.getOrDefault(group.toLowerCase(), null);
                if (module != null) {
                    replacement = module.generate(group);
                }

            } else if ((group = m.group(2)) != null) {
                // \escape
                replacement = doEscapeFormat(group);
            }
            if (replacement == null) {
                replacement = m.group(0);
            }
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);

        tick++;

        if (tick > 4) {
            cache.clear();
            tick = 0;
        }

        return sb.toString();
    }

    private void cacheMoney() {
        int total = PlayerInfo.get(InventoryData.class).getMoney();

        String eb = Integer.toString((total % 4096) / 64);
        String em = Integer.toString(total % 64);
        String le = Integer.toString(total / 4096);
        String output = Integer.toString(total);

        cache.put("blocks", eb);
        cache.put("liquid", le);
        cache.put("emeralds", em);
        cache.put("money", output);
        cache.put("money_desc", ItemUtils.describeMoney(total));
    }

    private void cachePotionCharges() {
        String potionCharges = PlayerInfo.get(InventoryData.class).getHealthPotionCharges();
        String remainingCharges = potionCharges.split("/")[0];
        String maxCharges = potionCharges.split("/")[1];

        cache.put("potionchargesremaining", remainingCharges);
        cache.put("potionchargesmax", maxCharges);
        cache.put("potionchargescombined", potionCharges);
    }

    private void cacheMemory() {
        long max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);

        int pct = (int) (((float) used / max) * 100f);

        cache.put("memorymax", Long.toString(max));
        cache.put("memoryused", Long.toString(used));
        cache.put("memorypct", Long.toString(pct));
    }

    private void cacheSoulPointTimer() {
        int totalseconds = PlayerInfo.get(InventoryData.class).getTicksToNextSoulPoint() / 20;

        int seconds = totalseconds % 60;
        int minutes = totalseconds / 60;
        String timer = String.format("%d:%02d", minutes, seconds);

        cache.put("soulpointtimer", timer);
        cache.put("soulpointminutes", Integer.toString(minutes));
        cache.put("soulpointseconds", Integer.toString(seconds));
    }

    private void cacheManaRegenTimer() {
        int ticks = PlayerInfo.get(InventoryData.class).getTicksToNextManaRegen();
        float displayedValue = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ? ticks : ticks / 20.0f;

        String decimalFormat = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ?
                OverlayConfig.ManaTimer.ManaTimerDecimalFormats.Zero.getDecimalFormat() :
                OverlayConfig.ManaTimer.INSTANCE.manaTimerDecimal.getDecimalFormat();

        String displayedProgress = (!OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage && OverlayConfig.ManaTimer.INSTANCE.manaTimerCountDown) ?
                String.format(decimalFormat, 5.0f - displayedValue) :
                String.format(decimalFormat, displayedValue);

        cache.put("manaregenformatted", displayedProgress);
    }

    private void cacheHorseData() {
        HorseData horse = PlayerInfo.get(HorseData.class);

        if (!horse.hasHorse()) {
            cache.put("horselevel", "??");
            cache.put("horsexp", "??");
            cache.put("horsetier", "?");
            cache.put("horselevelmax", "??");

            return;
        }

        cache.put("horselevel", Integer.toString(horse.getLevel()));
        cache.put("horsexp", Integer.toString(horse.getXp()));
        cache.put("horsetier", Integer.toString(horse.getTier()));
        cache.put("horselevelmax", Integer.toString(horse.getMaxLevel()));
    }
}

