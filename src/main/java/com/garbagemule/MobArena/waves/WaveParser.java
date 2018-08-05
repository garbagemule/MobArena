package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.ConfigError;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.things.InvalidThingInputString;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingManager;
import com.garbagemule.MobArena.util.ItemParser;
import com.garbagemule.MobArena.util.PotionEffectParser;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityManager;
import com.garbagemule.MobArena.waves.enums.BossHealth;
import com.garbagemule.MobArena.waves.enums.SwarmAmount;
import com.garbagemule.MobArena.waves.enums.WaveBranch;
import com.garbagemule.MobArena.waves.enums.WaveError;
import com.garbagemule.MobArena.waves.enums.WaveGrowth;
import com.garbagemule.MobArena.waves.enums.WaveType;
import com.garbagemule.MobArena.waves.types.BossWave;
import com.garbagemule.MobArena.waves.types.DefaultWave;
import com.garbagemule.MobArena.waves.types.SpecialWave;
import com.garbagemule.MobArena.waves.types.SupplyWave;
import com.garbagemule.MobArena.waves.types.SwarmWave;
import com.garbagemule.MobArena.waves.types.UpgradeWave;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class WaveParser
{
    public static TreeSet<Wave> parseWaves(Arena arena, ConfigurationSection config, WaveBranch branch) {
        // Create a TreeSet with the Comparator for the specific branch.
        TreeSet<Wave> result = new TreeSet<>(WaveUtils.getComparator(branch));
        
        // If the config is null, return the empty set.
        if (config == null) {
            arena.getPlugin().getLogger().warning(WaveError.BRANCH_MISSING.format(branch.toString().toLowerCase(), arena.configName()));
            return result;
        }
        
        // If no waves were found, return the empty set.
        Set<String> waves = config.getKeys(false);
        if (waves == null) {
            arena.getPlugin().getLogger().warning(WaveError.BRANCH_MISSING.format(branch.toString().toLowerCase(), arena.configName()));
            return result;
        }
        
        // Otherwise, parse each wave in the branch.
        for (String wave : waves) {
            ConfigurationSection waveSection = config.getConfigurationSection(wave);
            Wave w = parseWave(arena, wave, waveSection, branch);
            
            // Only add properly parsed waves.
            if (w == null) {
                continue;
            }
            
            result.add(w);
        }
        
        return result;
    }
    
    public static Wave parseWave(Arena arena, String name, ConfigurationSection config, WaveBranch branch) {
        // Grab the WaveType and verify that it isn't null.
        String t = config.getString("type", null);
        WaveType type = WaveType.fromString(t);
        
        if (type == null) {
            arena.getPlugin().getLogger().warning(WaveError.INVALID_TYPE.format(t, name, arena.configName()));
            return null;
        }
        
        // Prepare the result
        Wave result = null;
        
        // Switch on the type of wave.
        switch (type) {
            case DEFAULT:
                result = parseDefaultWave(arena, name, config);
                break;
            case SPECIAL:
                result = parseSpecialWave(arena, name, config);
                break;
            case SWARM:
                result = parseSwarmWave(arena, name, config);
                break;
            case SUPPLY:
                result = parseSupplyWave(arena, name, config);
                break;
            case UPGRADE:
                result = parseUpgradeWave(arena, name, config);
                break;
            case BOSS:
                result = parseBossWave(arena, name, config);
                break;
        }
        
        // Check that the result isn't null
        if (result == null) {
            arena.getPlugin().getLogger().warning(WaveError.INVALID_WAVE.format(name, arena.configName()));
            return null;
        }
        
        // Grab the branch-specific nodes.
        int priority  = config.getInt("priority", -1);
        int frequency = config.getInt("frequency", -1);
        int firstWave = config.getInt("wave", frequency);
        
        // Get multipliers
        double healthMultiplier = config.getDouble("health-multiplier", -1D);
        if (healthMultiplier == -1D) {
            healthMultiplier = config.getInt("health-multiplier", 1);
        }
        
        double amountMultiplier = config.getDouble("amount-multiplier", -1D);
        if (amountMultiplier == -1D) {
            amountMultiplier = config.getInt("amount-multiplier", 1);
        }
        
        // Grab the specific spawnpoints if any
        List<Location> spawnpoints = getSpawnpoints(arena, name, config);
        
        // Potion effects
        List<PotionEffect> effects = getPotionEffects(arena, name, config);

        // Recurrent must have priority + frequency, single must have firstWave
        if (branch == WaveBranch.RECURRENT && (priority == -1 || frequency <= 0)) {
            arena.getPlugin().getLogger().warning(WaveError.RECURRENT_NODES.format(name, arena.configName()));
            return null;
        } else if (branch == WaveBranch.SINGLE && firstWave <= 0) {
            arena.getPlugin().getLogger().warning(WaveError.SINGLE_NODES.format(name, arena.configName()));
            return null;
        }
        
        // Set the important required values.
        result.setName(name);
        result.setBranch(branch);
        result.setFirstWave(firstWave);
        result.setPriority(priority);
        result.setFrequency(frequency);
        
        // And the multipliers.
        result.setHealthMultiplier(healthMultiplier);
        result.setAmountMultiplier(amountMultiplier);
        
        // Aaand the spawnpoints
        result.setSpawnpoints(spawnpoints);

        // Potions
        result.setEffects(effects);
        
        return result;
    }
    
    private static Wave parseDefaultWave(Arena arena, String name, ConfigurationSection config) {
        // Grab the monster map.
        SortedMap<Integer,MACreature> monsters = getMonsterMap(config);
        if (monsters == null || monsters.isEmpty()) {
            arena.getPlugin().getLogger().warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
            return null;
        }
        
        // Create the wave.
        DefaultWave result = new DefaultWave(monsters);

        // Check if this is a fixed wave
        boolean fixed = config.getBoolean("fixed", false);
        if (fixed) {
            result.setFixed(true);
            return result;
        }
        
        // Grab the WaveGrowth
        String grw = config.getString("growth");
        WaveGrowth growth = WaveGrowth.fromString(grw);
        result.setGrowth(growth);
        
        return result;
    }
    
    private static Wave parseSpecialWave(Arena arena, String name, ConfigurationSection config) {
        SortedMap<Integer,MACreature> monsters = getMonsterMap(config);
        if (monsters == null || monsters.isEmpty()) {
            arena.getPlugin().getLogger().warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
            return null;
        }
        
        SpecialWave result = new SpecialWave(monsters);
        return result;
    }
    
    private static Wave parseSwarmWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(config);
        if (monster == null) {
            arena.getPlugin().getLogger().warning(WaveError.SINGLE_MONSTER_MISSING.format(name, arena.configName()));
            return null;
        }
        
        SwarmWave result = new SwarmWave(monster);
        
        // Grab SwarmAmount
        String amnt = config.getString("amount");
        SwarmAmount amount = SwarmAmount.fromString(amnt);
        result.setAmount(amount);
        
        return result;
    }
    
    private static Wave parseSupplyWave(Arena arena, String name, ConfigurationSection config) {
        SortedMap<Integer,MACreature> monsters = getMonsterMap(config);
        if (monsters == null || monsters.isEmpty()) {
            arena.getPlugin().getLogger().warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
            return null;
        }
        
        SupplyWave result = new SupplyWave(monsters);
        
        // Grab the loot.
        String loot = config.getString("drops");
        List<ItemStack> stacks = ItemParser.parseItems(loot);
        result.setDropList(stacks);
        
        return result;
    }
    
    private static Wave parseUpgradeWave(Arena arena, String name, ConfigurationSection config) {
        ThingManager thingman = arena.getPlugin().getThingManager();
        Map<String,List<Thing>> upgrades = getUpgradeMap(config, name, arena, thingman);
        if (upgrades == null || upgrades.isEmpty()) {
            arena.getPlugin().getLogger().warning(WaveError.UPGRADE_MAP_MISSING.format(name, arena.configName()));
            return null;
        }
        return new UpgradeWave(upgrades);
    }
    
    private static Wave parseBossWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(config);
        if (monster == null) {
            arena.getPlugin().getLogger().warning(WaveError.SINGLE_MONSTER_MISSING.format(name, arena.configName()));
            return null;
        }
        
        BossWave result = new BossWave(monster);
        
        // Check if there's a specific boss name
        String bossName = config.getString("name");
        if (bossName != null && !bossName.equals("")) {
            result.setBossName(bossName);
        }
        
        // Grab the boss health
        String healthString = config.getString("health");
        if (healthString == null) {
            String warning = "No health value found for boss '%s' in arena '%s'. Defaulting to medium.";
            arena.getPlugin().getLogger().warning(String.format(warning, name, arena.configName()));
            result.setHealth(BossHealth.MEDIUM);
        } else {
            BossHealth health = BossHealth.fromString(healthString);
            if (health != null) {
                result.setHealth(health);
            } else {
                int flatHealth = config.getInt("health", 0);
                if (flatHealth <= 0) {
                    String warning = "Unable to parse health of boss '%s' in arena '%s'. Defaulting to medium. Value was '%s'";
                    arena.getPlugin().getLogger().warning(String.format(warning, name, arena.configName(), healthString));
                    result.setHealth(BossHealth.MEDIUM);
                } else {
                    result.setFlatHealth(flatHealth);
                }
            }
        }
        
        // And the abilities.
        String ablts = config.getString("abilities");
        if (ablts != null) {
            String[] parts = ablts.split(",");
            for (String ability : parts) {
                Ability a = AbilityManager.getAbility(ability.trim());
                if (a == null) {
                    arena.getPlugin().getLogger().warning(WaveError.BOSS_ABILITY.format(ability.trim(), name, arena.configName()));
                    continue;
                }
                
                result.addBossAbility(a);
            }
        }
        
        // As well as the ability interval and ability announce.
        result.setAbilityInterval(config.getInt("ability-interval", 3) * 20);
        result.setAbilityAnnounce(config.getBoolean("ability-announce", true));
        
        // Rewards!
        String rew = config.getString("reward");
        if (rew != null) {
            try {
                Thing thing = arena.getPlugin().getThingManager().parse(rew.trim());
                result.setReward(thing);
            } catch (InvalidThingInputString e) {
                throw new ConfigError("Failed to parse boss reward in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
            }
        }

        // Drops!
        String drp = config.getString("drops");
        List<ItemStack> drops = ItemParser.parseItems(drp);
        result.setDrops(drops);
        
        return result;
    }
    
    /**
     * Scan the ConfigSection for a "monster" (singular) node, which
     * must be exactly a single monster.
     * @param config a ConfigSection
     * @return an MACreature, if the monster node contains one that is valid
     */
    private static MACreature getSingleMonster(ConfigurationSection config) {
        String monster = config.getString("monster");
        if (monster == null) {
            return null;
        }
        
        MACreature result = MACreature.fromString(monster);
        return result;
    }
    
    /**
     * Scan the ConfigSection for a "monsters" (plural) node, which
     * must contain a list of at least one "monster: number" node.
     * @param config
     * @return a "reverse" map of monsters and numbers
     */
    private static SortedMap<Integer,MACreature> getMonsterMap(ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("monsters");
        if (section == null) {
            return null;
        }

        Set<String> monsters = section.getKeys(false);
        if (monsters == null || monsters.isEmpty()) {
            return null;
        }
        
        // Prepare the map.
        SortedMap<Integer,MACreature> monsterMap = new TreeMap<>();
        int sum = 0;
        String path = "monsters.";
        
        // Check all the monsters.
        for (String monster : monsters) {
            MACreature creature = MACreature.fromString(monster);
            if (creature == null) continue;
            
            int prob = config.getInt(path + monster, 0);
            if (prob == 0) continue;
            
            sum += prob;
            monsterMap.put(sum, creature);
        }
        
        return monsterMap;
    }
    
    private static List<Location> getSpawnpoints(Arena arena, String name, ConfigurationSection config) {
        List<Location> result = new ArrayList<>();
        
        String spawnString = config.getString("spawnpoints");
        if (spawnString == null) {
            return result;
        }
        
        // Split the string by semicolons
        String[] spawns = spawnString.split(";");
        
        ArenaRegion region = arena.getRegion();
        for (String spawn : spawns) {
            Location spawnpoint = region.getSpawnpoint(spawn.trim());
            
            if (spawnpoint == null) {
                arena.getPlugin().getLogger().warning("Spawnpoint '" + spawn + "' in wave '" + name + "' for arena '" + arena.configName() + "' could not be parsed!");
                continue;
            }
            
            result.add(spawnpoint);
        }
        
        return result;
    }

    private static List<PotionEffect> getPotionEffects(Arena arena, String name, ConfigurationSection config) {
        /*
         * TODO: Make this consistent with the rest somehow
         * - Things only work for Players (currently)
         */
        List<String> effects = config.getStringList("effects");
        if (effects == null || effects.isEmpty()) {
            String value = config.getString("effects", null);
            if (value != null && !value.isEmpty()) {
                effects = Arrays.asList(value.split(","));
            } else {
                effects = config.getStringList("potions");
                if (effects == null || effects.isEmpty()) {
                    value = config.getString("potions", null);
                    if (value != null && !value.isEmpty()) {
                        effects = Arrays.asList(value.split(","));
                    } else {
                        return Collections.emptyList();
                    }
                }
            }
        }
        return effects.stream()
            .map(String::trim)
            .map(input -> {
                PotionEffect effect = PotionEffectParser.parsePotionEffect(input, false);
                if (effect == null) {
                    throw new ConfigError("Failed to parse potion effect for wave " + name + " of arena " + arena.configName() + ": " + input);
                }
                return effect;
            })
            .collect(Collectors.toList());
    }
    
    private static Map<String,List<Thing>> getUpgradeMap(ConfigurationSection config, String name, Arena arena, ThingManager thingman) {
        ConfigurationSection section = config.getConfigurationSection("upgrades");
        if (section == null) {
            return null;
        }

        Set<String> classes = section.getKeys(false);
        if (classes == null || classes.isEmpty()) {
            return null;
        }
        
        Map<String,List<Thing>> upgrades = new HashMap<>();
        String path = "upgrades.";
        
        for (String className : classes) {
            // Legacy support
            Object val = config.get(path + className, null);
            if (val instanceof String) {
                List<Thing> things = loadUpgradesFromString(className, (String) val, name, arena, thingman);
                upgrades.put(className.toLowerCase(), things);
            }
            // New complex setup
            else if (val instanceof ConfigurationSection) {
                List<Thing> list = loadUpgradesFromSection(className, (ConfigurationSection) val, name, arena, thingman);
                upgrades.put(className.toLowerCase(), list);
            }
        }
        
        return upgrades;
    }

    private static List<Thing> loadUpgradesFromString(String className, String value, String name, Arena arena, ThingManager thingman) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(thingman::parse)
                .collect(Collectors.toList());
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse upgrades for class " + className + " in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
        }
    }

    private static List<Thing> loadUpgradesFromSection(String className, ConfigurationSection classSection, String name, Arena arena, ThingManager thingman) {
        List<Thing> list = new ArrayList<>();

        // Items
        List<String> items = classSection.getStringList("items");
        if (items == null || items.isEmpty()) {
            String value = classSection.getString("items", null);
            if (value == null || value.isEmpty()) {
                items = Collections.emptyList();
            } else {
                items = Arrays.asList(value.split(","));
            }
        }
        try {
            items.stream()
                .map(String::trim)
                .map(thingman::parse)
                .forEach(list::add);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse item upgrades for class " + className + " in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
        }

        // Armor
        List<String> armor = classSection.getStringList("armor");
        if (armor == null || armor.isEmpty()) {
            String value = classSection.getString("armor", null);
            if (value == null || value.isEmpty()) {
                armor = Collections.emptyList();
            } else {
                armor = Arrays.asList(value.split(","));
            }
        }
        try {
            // Prepend "armor:" for the armor thing parser
            armor.stream()
                .map(String::trim)
                .map(s -> thingman.parse("armor", s))
                .forEach(list::add);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse armor upgrades for class " + className + " in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
        }

        try {
            // Prepend "perm:" for the permission thing parser
            classSection.getStringList("permissions").stream()
                .map(perm -> thingman.parse("perm", perm))
                .forEach(list::add);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse permission upgrades for class " + className + " in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
        }

        return list;
    }
    
    public static Wave createDefaultWave() {
        SortedMap<Integer,MACreature> monsters = new TreeMap<>();
        monsters.put(10, MACreature.ZOMBIE);
        monsters.put(20, MACreature.SKELETON);
        monsters.put(30, MACreature.SPIDER);
        monsters.put(40, MACreature.SLIMESMALL);
        
        DefaultWave result = new DefaultWave(monsters);
        result.setName("MA_DEFAULT_WAVE");
        result.setBranch(WaveBranch.RECURRENT);
        result.setFirstWave(1);
        result.setPriority(1);
        result.setFrequency(1);
        result.setGrowth(WaveGrowth.OLD);
        result.setHealthMultiplier(1D);
        result.setAmountMultiplier(1D);
        
        return result;
    }
}
