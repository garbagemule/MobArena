package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.ConfigError;
import com.garbagemule.MobArena.formula.Formula;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.things.InvalidThingInputString;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingManager;
import com.garbagemule.MobArena.things.ThingPicker;
import com.garbagemule.MobArena.util.ItemParser;
import com.garbagemule.MobArena.util.PotionEffectParser;
import com.garbagemule.MobArena.util.Slugs;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityManager;
import com.garbagemule.MobArena.waves.enums.WaveBranch;
import com.garbagemule.MobArena.waves.enums.WaveType;
import com.garbagemule.MobArena.waves.types.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class WaveParser
{
    public static TreeSet<Wave> parseWaves(Arena arena, ConfigurationSection config, WaveBranch branch) {
        // Create a TreeSet with the Comparator for the specific branch.
        TreeSet<Wave> result = new TreeSet<>(WaveUtils.getComparator(branch));

        // If the config is null, return the empty set.
        if (config == null) {
            return result;
        }

        // If no waves were found, return the empty set.
        Set<String> waves = config.getKeys(false);
        if (waves == null) {
            return result;
        }

        // Otherwise, parse each wave in the branch.
        for (String wave : waves) {
            ConfigurationSection waveSection = config.getConfigurationSection(wave);
            Wave w = parseWave(arena, wave, waveSection, branch);
            result.add(w);
        }

        return result;
    }

    public static Wave parseWave(Arena arena, String name, ConfigurationSection config, WaveBranch branch) {
        // Grab the WaveType and verify that it isn't null.
        String t = config.getString("type", null);
        WaveType type = WaveType.fromString(t);

        if (type == null) {
            throw new ConfigError("Invalid wave type for wave " + name + " of arena " + arena.configName() + ": " + t);
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
            case COMBO:
                result = parseComboWave(arena, name, config);
                break;
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

        // Recurrent must have priority + frequency, single must have firstWave, combo doesn't need this
        if (branch == WaveBranch.RECURRENT) {
            if (priority <= 0) {
                throw new ConfigError("Missing or invalid 'priority' node for recurrent wave " + name + " of arena " + arena.configName());
            }
            if (frequency <= 0) {
                throw new ConfigError("Missing or invalid 'frequency' node for recurrent wave " + name + " of arena " + arena.configName());
            }
        } else if (branch == WaveBranch.SINGLE && firstWave <= 0) {
            throw new ConfigError("Missing or invalid 'wave' node for single wave " + name + " of arena " + arena.configName());
        }

        // Set the important required values.
        result.setName(name);
        result.setBranch(branch);
        result.setFirstWave(firstWave);
        result.setPriority(priority);
        result.setFrequency(frequency);
        result.setShadowBlacklist(Optional.ofNullable(config.getString("noshadow")).orElse(""));

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
        SortedMap<Integer,MACreature> monsters = getMonsterMap(arena, name, config);

        // Create the wave.
        DefaultWave result = new DefaultWave(monsters);

        // Check if this is a fixed wave
        boolean fixed = config.getBoolean("fixed", false);
        if (fixed) {
            result.setFixed(true);
            return result;
        }

        // Grab the WaveGrowth
        String growth = config.getString("growth", null);
        if (growth == null || growth.isEmpty()) {
            growth = "<initial-players> + <current-wave>";
        } else {
            String macro = arena.getPlugin().getFormulaMacros().get("wave-growth", growth);
            if (macro != null) {
                growth = macro;
            }
        }
        try {
            Formula formula = arena.getPlugin().getFormulaManager().parse(growth);
            result.setGrowth(formula);
        } catch (IllegalArgumentException e) {
            String message = String.format("Failed to parse wave growth for wave %s of arena %s: %s\n%s", name, arena.configName(), growth, e.getMessage());
            throw new ConfigError(message);
        }

        return result;
    }

    private static Wave parseSpecialWave(Arena arena, String name, ConfigurationSection config) {
        SortedMap<Integer,MACreature> monsters = getMonsterMap(arena, name, config);

        return new SpecialWave(monsters);
    }

    private static Wave parseSwarmWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(arena, name, config);

        SwarmWave result = new SwarmWave(monster);

        // Grab SwarmAmount
        String amount = config.getString("amount", null);
        if (amount == null || amount.isEmpty()) {
            amount = "<initial-players> * 5";
        } else {
            String macro = arena.getPlugin().getFormulaMacros().get("swarm-amount", amount);
            if (macro != null) {
                amount = macro;
            }
        }
        try {
            Formula formula = arena.getPlugin().getFormulaManager().parse(amount);
            result.setAmount(formula);
        } catch (IllegalArgumentException e) {
            String message = String.format("Failed to parse wave amount for wave %s of arena %s: %s\n%s", name, arena.configName(), amount, e.getMessage());
            throw new ConfigError(message);
        }

        return result;
    }

    private static Wave parseSupplyWave(Arena arena, String name, ConfigurationSection config) {
        SortedMap<Integer,MACreature> monsters = getMonsterMap(arena, name, config);

        SupplyWave result = new SupplyWave(monsters);

        // Grab the loot.
        List<String> loot = config.getStringList("drops");
        if (loot == null || loot.isEmpty()) {
            String value = config.getString("drops", null);
            if (value == null) {
                throw new ConfigError("Missing 'drops' node for wave " + name + " of arena " + arena.configName());
            }
            loot = Arrays.asList(value.split(","));
        }
        List<ItemStack> stacks = loot.stream()
            .map(String::trim)
            .map(value -> {
                ItemStack stack = ItemParser.parseItem(value, false);
                if (stack == null) {
                    throw new ConfigError("Failed to parse loot for wave " + name + " of arena " + arena.configName() + ": " + value);
                }
                return stack;
            })
            .collect(Collectors.toList());
        result.setDropList(stacks);

        return result;
    }

    private static Wave parseUpgradeWave(Arena arena, String name, ConfigurationSection config) {
        ThingManager thingman = arena.getPlugin().getThingManager();
        Map<String,List<Thing>> upgrades = getUpgradeMap(config, name, arena, thingman);

        return new UpgradeWave(upgrades);
    }

    private static Wave parseBossWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(arena, name, config);

        BossWave result = new BossWave(monster);

        // Check if there's a specific boss name
        String bossName = config.getString("name", null);
        if (bossName != null && !bossName.isEmpty()) {
            result.setBossName(ChatColor.translateAlternateColorCodes('&', bossName));
        }

        // Grab the boss health
        String health = config.getString("health", null);
        if (health == null || health.isEmpty()) {
            health = "(<initial-players> + 1) * 20 * 8";
        } else {
            String macro = arena.getPlugin().getFormulaMacros().get("boss-health", health);
            if (macro != null) {
                health = macro;
            }
        }
        try {
            Formula formula = arena.getPlugin().getFormulaManager().parse(health);
            result.setHealth(formula);
        } catch (IllegalArgumentException e) {
            String message = String.format("Failed to parse boss health for wave %s of arena %s: %s\n%s", name, arena.configName(), health, e.getMessage());
            throw new ConfigError(message);
        }

        // And the abilities.
        List<String> abilities = config.getStringList("abilities");
        if (abilities == null || abilities.isEmpty()) {
            String value = config.getString("abilities", null);
            if (value == null) {
                abilities = Collections.emptyList();
            } else {
                abilities = Arrays.asList(value.split(","));
            }
        }
        if (abilities.isEmpty()) {
            arena.getPlugin().getLogger().warning("No boss abilities for boss wave " + name + " of arena " + arena.configName());
        }
        abilities.stream()
            .map(String::trim)
            .map(value -> {
                Ability ability = AbilityManager.getAbility(value);
                if (ability == null) {
                    throw new ConfigError("Failed to parse boss ability for boss wave " + name + " of arena " + arena.configName() + ": " + value);
                }
                return ability;
            })
            .forEach(result::addBossAbility);

        // As well as the ability interval and ability announce.
        result.setAbilityInterval(config.getInt("ability-interval", 3) * 20);
        result.setAbilityAnnounce(config.getBoolean("ability-announce", true));

        // Rewards!
        String rew = config.getString("reward", null);
        if (rew != null && !rew.isEmpty()) {
            try {
                ThingPicker picker = arena.getPlugin().getThingPickerManager().parse(rew.trim());
                result.setReward(picker);
            } catch (InvalidThingInputString e) {
                throw new ConfigError("Failed to parse boss reward in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
            }
        }

        // Drops!
        List<String> drops = config.getStringList("drops");
        if (drops == null || drops.isEmpty()) {
            String value = config.getString("drops", null);
            if (value == null) {
                drops = Collections.emptyList();
            } else {
                drops = Arrays.asList(value.split(","));
            }
        }
        List<ItemStack> stacks = drops.stream()
            .map(String::trim)
            .map(value -> {
                ItemStack stack = ItemParser.parseItem(value, false);
                if (stack == null) {
                    throw new ConfigError("Failed to parse boss drop in wave " + name + " of arena " + arena.configName() + ": " + value);
                }
                return stack;
            })
            .collect(Collectors.toList());
        result.setDrops(stacks);

        return result;
    }

    private static Wave parseComboWave(Arena arena, String name, ConfigurationSection config) {
        ConfigurationSection waveAConfig = config.getConfigurationSection("waveA");
        if (waveAConfig == null) {
            throw new ConfigError("No waveA defined in wave " + name + " of arena " + arena.configName() + ".");
        }
        Wave waveA = parseWave(arena, name, waveAConfig, WaveBranch.COMBO);
        ConfigurationSection waveBConfig = config.getConfigurationSection("waveB");
        if (waveBConfig == null) {
            throw new ConfigError("No waveB defined in wave " + name + " of arena " + arena.configName() + ".");
        }
        Wave waveB = parseWave(arena, name, waveBConfig, WaveBranch.COMBO);

        return new ComboWave(waveA, waveB);
    }

    /**
     * Scan the ConfigSection for a "monster" (singular) node, which
     * must be exactly a single monster.
     * @param config a ConfigSection
     * @return an MACreature, if the monster node contains one that is valid
     */
    private static MACreature getSingleMonster(Arena arena, String name, ConfigurationSection config) {
        String monster = config.getString("monster", null);
        if (monster == null || monster.isEmpty()) {
            throw new ConfigError("Missing 'monster' node for wave " + name + " of arena " + arena.configName());
        }

        MACreature result = MACreature.fromString(monster);
        if (result == null) {
            throw new ConfigError("Failed to parse monster for wave " + name + " of arena " + arena.configName() + ": " + monster);
        }
        return result;
    }

    /**
     * Scan the ConfigSection for a "monsters" (plural) node, which
     * must contain a list of at least one "monster: number" node.
     * @param config
     * @return a "reverse" map of monsters and numbers
     */
    private static SortedMap<Integer,MACreature> getMonsterMap(Arena arena, String name, ConfigurationSection config) {
        ConfigurationSection section = config.getConfigurationSection("monsters");
        if (section == null) {
            throw new ConfigError("Missing 'monsters' node for wave " + name + " of arena " + arena.configName());
        }

        Set<String> monsters = section.getKeys(false);
        if (monsters == null || monsters.isEmpty()) {
            throw new ConfigError("Empty 'monsters' node for wave " + name + " of arena " + arena.configName());
        }

        // Prepare the map.
        SortedMap<Integer,MACreature> monsterMap = new TreeMap<>();
        int sum = 0;
        String path = "monsters.";

        // Check all the monsters.
        for (String monster : monsters) {
            MACreature creature = MACreature.fromString(monster);
            if (creature == null) {
                throw new ConfigError("Failed to parse monster for wave " + name + " of arena " + arena.configName() + ": " + monster);
            }

            int prob = config.getInt(path + monster, -1);
            if (prob < 0) {
                throw new ConfigError("Failed to parse probability for monster " + monster + " in wave " + name + " of arena " + arena.configName());
            }

            sum += prob;
            monsterMap.put(sum, creature);
        }

        return monsterMap;
    }

    private static List<Location> getSpawnpoints(Arena arena, String name, ConfigurationSection config) {
        List<String> spawnpoints = config.getStringList("spawnpoints");
        if (spawnpoints == null || spawnpoints.isEmpty()) {
            String value = config.getString("spawnpoints", null);
            if (value == null || value.isEmpty()) {
                return Collections.emptyList();
            }
            spawnpoints = Arrays.asList(value.split(";"));
        }

        ArenaRegion region = arena.getRegion();
        return spawnpoints.stream()
            .map(String::trim)
            .map(value -> {
                Location spawnpoint = region.getSpawnpoint(value);
                if (spawnpoint == null) {
                    throw new ConfigError("Spawnpoint '" + value + "' in wave '" + name + "' for arena '" + arena.configName() + "' could not be parsed!");
                }
                return spawnpoint;
            })
            .collect(Collectors.toList());
    }

    private static List<PotionEffect> getPotionEffects(Arena arena, String name, ConfigurationSection config) {
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
            throw new ConfigError("Missing 'upgrades' node for wave " + name + " of arena " + arena.configName());
        }

        Set<String> classes = section.getKeys(false);
        if (classes == null || classes.isEmpty()) {
            throw new ConfigError("Empty 'upgrades' node for wave " + name + " of arena " + arena.configName());
        }

        Map<String,List<Thing>> upgrades = new HashMap<>();
        String path = "upgrades.";

        for (String className : classes) {
            String slug = Slugs.create(className);

            // Legacy support
            Object val = config.get(path + className, null);
            if (val instanceof String) {
                List<Thing> things = loadUpgradesFromString(className, (String) val, name, arena, thingman);
                upgrades.put(slug, things);
            }
            // New complex setup
            else if (val instanceof ConfigurationSection) {
                List<Thing> list = loadUpgradesFromSection(className, (ConfigurationSection) val, name, arena, thingman);
                upgrades.put(slug, list);
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

        // Effects
        List<String> effects = classSection.getStringList("effects");
        if (effects == null || effects.isEmpty()) {
            String value = classSection.getString("effects", null);
            if (value == null || value.isEmpty()) {
                effects = Collections.emptyList();
            } else {
                effects = Arrays.asList(value.split(","));
            }
        }
        try {
            // Prepend "effect:" for the potion effect thing parser
            effects.stream()
                .map(String::trim)
                .map(s -> thingman.parse("effect", s))
                .forEach(list::add);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse potion effects for class " + className + " in wave " + name + " of arena " + arena.configName() + ": " + e.getInput());
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
        monsters.put(10, MACreature.fromString("zombie"));
        monsters.put(20, MACreature.fromString("skeleton"));
        monsters.put(30, MACreature.fromString("spider"));
        monsters.put(40, MACreature.fromString("creeper"));
        monsters.put(50, MACreature.fromString("wolf"));

        DefaultWave result = new DefaultWave(monsters);
        result.setName("MA_DEFAULT_WAVE");
        result.setBranch(WaveBranch.RECURRENT);
        result.setFirstWave(1);
        result.setPriority(1);
        result.setFrequency(1);
        result.setHealthMultiplier(1D);
        result.setAmountMultiplier(1D);

        return result;
    }
}
