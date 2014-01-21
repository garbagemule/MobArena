package com.garbagemule.MobArena.waves;

import java.util.*;

import com.garbagemule.MobArena.ArenaClass;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.util.ItemParser;
import com.garbagemule.MobArena.waves.ability.Ability;
import com.garbagemule.MobArena.waves.ability.AbilityManager;
import com.garbagemule.MobArena.waves.enums.*;
import com.garbagemule.MobArena.waves.types.BossWave;
import com.garbagemule.MobArena.waves.types.DefaultWave;
import com.garbagemule.MobArena.waves.types.SpecialWave;
import com.garbagemule.MobArena.waves.types.SupplyWave;
import com.garbagemule.MobArena.waves.types.SwarmWave;
import com.garbagemule.MobArena.waves.types.UpgradeWave;
import com.garbagemule.MobArena.waves.types.UpgradeWave.*;

public class WaveParser
{
    public static TreeSet<Wave> parseWaves(Arena arena, ConfigurationSection config, WaveBranch branch) {
        // Create a TreeSet with the Comparator for the specific branch.
        TreeSet<Wave> result = new TreeSet<Wave>(WaveUtils.getComparator(branch));
        
        // If the config is null, return the empty set.
        if (config == null) {
            Messenger.warning(WaveError.BRANCH_MISSING.format(branch.toString().toLowerCase(), arena.configName()));
            return result;
        }
        
        // If no waves were found, return the empty set.
        Set<String> waves = config.getKeys(false);
        if (waves == null) {
            Messenger.warning(WaveError.BRANCH_MISSING.format(branch.toString().toLowerCase(), arena.configName()));
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
            Messenger.warning(WaveError.INVALID_TYPE.format(t, name, arena.configName()));
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
            Messenger.warning(WaveError.INVALID_WAVE.format(name, arena.configName()));
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
        
        // Recurrent must have priority + frequency, single must have firstWave
        if (branch == WaveBranch.RECURRENT && (priority == -1 || frequency <= 0)) {
            Messenger.warning(WaveError.RECURRENT_NODES.format(name, arena.configName()));
            return null;
        } else if (branch == WaveBranch.SINGLE && firstWave <= 0) {
            Messenger.warning(WaveError.SINGLE_NODES.format(name, arena.configName()));
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
        
        return result;
    }
    
    private static Wave parseDefaultWave(Arena arena, String name, ConfigurationSection config) {
        // Grab the monster map.
        SortedMap<Integer,MACreature> monsters = getMonsterMap(config);
        if (monsters == null || monsters.isEmpty()) {
            Messenger.warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
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
            Messenger.warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
            return null;
        }
        
        SpecialWave result = new SpecialWave(monsters);
        return result;
    }
    
    private static Wave parseSwarmWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(config);
        if (monster == null) {
            Messenger.warning(WaveError.SINGLE_MONSTER_MISSING.format(name, arena.configName()));
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
            Messenger.warning(WaveError.MONSTER_MAP_MISSING.format(name, arena.configName()));
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
        Map<String,List<Upgrade>> upgrades = getUpgradeMap(config);
        if (upgrades == null || upgrades.isEmpty()) {
            Messenger.warning(WaveError.UPGRADE_MAP_MISSING.format(name, arena.configName()));
            return null;
        }

        UpgradeWave result = new UpgradeWave(upgrades);

        // Determine if all items should be given
        boolean giveAll = config.getBoolean("give-all-items", false);
        result.setGiveAll(giveAll);

        return result;
    }
    
    private static Wave parseBossWave(Arena arena, String name, ConfigurationSection config) {
        MACreature monster = getSingleMonster(config);
        if (monster == null) {
            Messenger.warning(WaveError.SINGLE_MONSTER_MISSING.format(name, arena.configName()));
            return null;
        }
        
        BossWave result = new BossWave(monster);
        
        // Check if there's a specific boss name
        String bossName = config.getString("name");
        if (bossName != null && !bossName.equals("")) {
            result.setBossName(bossName);
        }
        
        // Grab the boss health
        String hlth = config.getString("health");
        BossHealth health = BossHealth.fromString(hlth);
        if (health != null) {
            result.setHealth(health);
        } else {
            try {
                int flatHealth;
                if (hlth != null) {
                    flatHealth = Integer.parseInt(hlth);
                } else {
                    flatHealth = config.getInt("health");
                }
                result.setFlatHealth(flatHealth);
            } catch (Exception e) {
                String warning = "Unable to parse health of boss '%s' in arena '%s'. Defaulting to medium. Value was '%s'";
                Messenger.warning(String.format(warning, name, arena.configName(), hlth));
                result.setHealth(BossHealth.MEDIUM);
            }
        }
        
        // And the abilities.
        String ablts = config.getString("abilities");
        if (ablts != null) {
            String[] parts = ablts.split(",");
            for (String ability : parts) {
                Ability a = AbilityManager.getAbility(ability.trim());
                if (a == null) {
                    Messenger.warning(WaveError.BOSS_ABILITY.format(ability.trim(), name, arena.configName()));
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
            ItemStack item = ItemParser.parseItem(rew);
            if (item != null) result.setReward(item);
        }
        
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
        Set<String> monsters = config.getConfigurationSection("monsters").getKeys(false);
        if (monsters == null || monsters.isEmpty()) {
            return null;
        }
        
        // Prepare the map.
        SortedMap<Integer,MACreature> monsterMap = new TreeMap<Integer,MACreature>();
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
        List<Location> result = new ArrayList<Location>();
        
        String spawnString = config.getString("spawnpoints");
        if (spawnString == null) {
            return result;
        }
        
        // Split the string by commas
        String[] spawns = spawnString.split(",");
        
        ArenaRegion region = arena.getRegion();
        for (String spawn : spawns) {
            Location spawnpoint = region.getSpawnpoint(spawn.trim());
            
            if (spawnpoint == null) {
                Messenger.warning("Spawnpoint '" + spawn + "' in wave '" + name + "' for arena '" + arena.configName() + "' could not be parsed!");
                continue;
            }
            
            result.add(spawnpoint);
        }
        
        return result;
    }
    
    private static Map<String,List<Upgrade>> getUpgradeMap(ConfigurationSection config) {
        Set<String> classes = config.getConfigurationSection("upgrades").getKeys(false);
        if (classes == null || classes.isEmpty()) {
            return null;
        }
        
        Map<String,List<Upgrade>> upgrades = new HashMap<String,List<Upgrade>>();
        String path = "upgrades.";
        
        for (String className : classes) {
            String itemList;
            // Legacy support
            Object val = config.get(path + className, null);
            if (val instanceof String) {
                itemList = (String) val;
                List<ItemStack> stacks = ItemParser.parseItems(itemList);
                List<Upgrade> list = new ArrayList<Upgrade>();
                for (ItemStack stack : stacks) {
                    list.add(new GenericUpgrade(stack));
                }
                upgrades.put(className.toLowerCase(), list);
            }
            // New complex setup
            else if (val instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) val;
                List<Upgrade> list = new ArrayList<Upgrade>();

                // Items (Generic + Weapons)
                itemList = section.getString("items", null);
                if (itemList != null) {
                    for (ItemStack stack : ItemParser.parseItems(itemList)) {
                        list.add(ArenaClass.isWeapon(stack) ? new WeaponUpgrade(stack) : new GenericUpgrade(stack));
                    }
                }

                // Armor
                itemList = section.getString("armor", null);
                if (itemList != null) {
                    for (ItemStack stack : ItemParser.parseItems(itemList)) {
                        list.add(new ArmorUpgrade(stack));
                    }
                }

                // Permissions
                List<String> perms = section.getStringList("permissions");
                if (!perms.isEmpty()) {
                    for (String perm : perms) {
                        list.add(new PermissionUpgrade(perm));
                    }
                }

                // Put in the map
                upgrades.put(className.toLowerCase(), list);
            }
        }
        
        return upgrades;
    }
    
    public static Wave createDefaultWave() {
        SortedMap<Integer,MACreature> monsters = new TreeMap<Integer,MACreature>();
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
