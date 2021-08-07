package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACreature {

    private static final Map<String,MACreature> map = new HashMap<>();
    private static final List<DyeColor> colors = Arrays.asList(DyeColor.values());
    private static final ItemStack[] NO_ARMOR = new ItemStack[0];

    static {
        registerEntityTypeValues();
        registerExtraAliases();
        registerTypeVariants();
        registerCustomTypes();
        registerBrokenTypes();
    }

    private final String name;
    private final String plural;
    private final EntityType type;

    public MACreature(EntityType type, String name) {
        this.type = type;
        this.name = name;
        this.plural = null;
    }

    /**
     * @deprecated This constructor will be removed in a future update.
     * Use {@link #MACreature(EntityType, String)} instead, and register
     * with {@link #register(String, MACreature)}.
     */
    @Deprecated
    public MACreature(String name, String plural, EntityType type) {
        this.name = name;
        this.plural = (plural != null) ? plural : name;
        this.type = type;

        register();
    }

    /**
     * @deprecated This constructor will be removed in a future update.
     * Use {@link #MACreature(EntityType, String)} instead, and register
     * with {@link #register(String, MACreature)}.
     */
    @Deprecated
    public MACreature(String name, EntityType type) {
        this(name, name + "s", type);
    }

    private void register() {
        map.put(name, this);
        map.put(plural, this);
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public LivingEntity spawn(Arena arena, World world, Location loc) {
        LivingEntity e = (LivingEntity) world.spawnEntity(loc, type);
        e.setCanPickupItems(false);
        e.getEquipment().setArmorContents(NO_ARMOR);

        switch (this.name) {
            case "sheep":
                ((Sheep) e).setColor(colors.get(MobArena.random.nextInt(colors.size())));
                break;
            case "explodingsheep":
                arena.getMonsterManager().addExplodingSheep(e);
                ((Sheep) e).setColor(DyeColor.RED);
                break;
            case "poweredcreeper":
                ((Creeper) e).setPowered(true);
                break;
            case "angrybee":
                ((Bee) e).setAnger(Integer.MAX_VALUE);
                break;
            case "angrywolf":
                ((Wolf) e).setAngry(true);
                break;
            case "slime":
            case "magmacube":
                ((Slime) e).setSize( (1 + MobArena.random.nextInt(3)) );
                break;
            case "slimetiny":
            case "magmacubetiny":
                ((Slime) e).setSize(1);
                break;
            case "slimesmall":
            case "magmacubesmall":
                ((Slime) e).setSize(2);
                break;
            case "slimebig":
            case "magmacubebig":
                ((Slime) e).setSize(3);
                break;
            case "slimehuge":
            case "magmacubehuge":
                ((Slime) e).setSize(4);
                break;
            case "babyzombievillager":
            case "babyzombie":
                ((Zombie) e).setBaby(true);
                break;
            case "babypigman":
            case "babyzombifiedpiglin":
                ((Zombie) e).setBaby(true);
                ((PigZombie) e).setAngry(true);
                break;
            case "pigzombie":
            case "zombiepigman":
            case "zombifiedpiglin":
                ((PigZombie) e).setAngry(true);
                break;
            case "killerbunny":
                ((Rabbit) e).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                break;
            default:
                break;
        }

        if (e instanceof Creature) {
            Creature c = (Creature) e;
            c.setTarget(WaveUtils.getClosestPlayer(arena, e));
        }

        return e;
    }

    /**
     * Register an MACreature instance by the given key.
     * <p>
     * Adds the MACreature instance to the internal monster type registry such
     * that it can be used in monster waves.
     * <p>
     * Any existing registration under the given key is silently overwritten.
     *
     * @param key a key to reference the creature by
     * @param creature an MACreature instance
     */
    public static void register(String key, MACreature creature) {
        map.put(key, creature);
    }

    public static MACreature fromString(String string) {
        MACreature creature = map.get(string);
        if (creature != null) {
            return creature;
        }
        String squashed = squash(string);
        return map.get(squashed);
    }

    private static String squash(String string) {
        return string
            .replaceAll("[-_.]", "")
            .toLowerCase();
    }

    private static void registerEntityTypeValues() {
        // Register all living entities in the EntityType enum.
        //
        // This ensures that all valid entities on the running server are
        // made available by their enum value names. This is probably the
        // closest we can get to being forwards compatible with an evolving
        // enum without resorting to nasty hacks and JVM tricks.
        //
        for (EntityType type : EntityType.values()) {
            if (type.isAlive()) {
                String key = squash(type.name());
                put(key, key + "s", type);
            }
        }

        // Register "grammatically correct" plurals
        copy("enderman", "endermen");
        copy("snowman", "snowmen");
        copy("witch", "witches");
        copy("wolf", "wolves");
    }

    private static void registerExtraAliases() {
        // Register extra aliases for certain types.
        //
        // These are names that either "fix" the API names to match in-game
        // text or add extra aliases for no apparent reason. These are very
        // much thorn-in-the-side names, and they should be removed at some
        // point to remove unnecessary complexity.
        //
        put("mooshroom", "mooshrooms", "MUSHROOM_COW");
        put("snowgolem", "snowgolems", "SNOWMAN");
        put("undeadhorse", "undeadhorses", "ZOMBIE_HORSE");
    }

    private static void registerTypeVariants() {
        // Register specialized variants of native types.
        //
        // Type variants are native types that are modified or configured at
        // spawn time. For example, "angry-wolf" is just a normal wolf with
        // its anger property is set to true.
        //
        put("angrybee", "angrybees", "BEE", null);
        put("angrywolf", "angrywolves", "WOLF");
        put("babyzombie", "babyzombies", "ZOMBIE");
        put("babyzombievillager", "babyzombievillagers", "ZOMBIE_VILLAGER");
        put("killerbunny", "killerbunnies", "RABBIT");
        put("magmacubebig", "magmacubesbig", "MAGMA_CUBE");
        put("magmacubehuge", "magmacubeshuge", "MAGMA_CUBE");
        put("magmacubesmall", "magmacubessmall", "MAGMA_CUBE");
        put("magmacubetiny", "magmacubestiny", "MAGMA_CUBE");
        put("poweredcreeper", "poweredcreepers", "CREEPER");
        put("slimebig", "slimesbig", "SLIME");
        put("slimehuge", "slimeshuge", "SLIME");
        put("slimesmall", "slimessmall", "SLIME");
        put("slimetiny", "slimestiny", "SLIME");
    }

    private static void registerCustomTypes() {
        // Register custom MobArena monster types.
        //
        // These are MobArena's own first-class monster types that require a
        // bit of special treatment in various parts of the plugin.
        //
        put("explodingsheep", "explodingsheep", "SHEEP");
    }

    private static void registerBrokenTypes() {
        // Register backward-compatible keys (new MobArena, old API)
        //
        // These are keys from newer versions of the API that don't exist in
        // older versions. Because MobArena might use these keys internally
        // for default config-files, they should be valid on all versions so
        // people on out-of-date servers can still enjoy up-to-date MobArena.
        //
        put("babyzombifiedpiglin", "babyzombifiedpiglins", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
        put("zombifiedpiglin", "zombifiedpiglins", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");

        // Register forward-compatible keys (old MobArena, new API)
        //
        // Conversely, these are keys from older versions of the API that no
        // longer exist in newer versions. Because these keys might be in use
        // in existing setups, they should be valid on all versions so people
        // on bleeding edge servers can still enjoy stable MobArena.
        //
        put("babypigman", "babypigmen", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
        put("pigzombie", "pigzombies", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
        put("zombiepigman", "zombiepigmen", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
    }

    private static void put(String key, String plural, EntityType type) {
        MACreature creature = new MACreature(type, key);
        register(key, creature);
        register(plural, creature);
    }

    private static void put(String key, String plural, String... names) {
        for (String name : names) {
            // If we hit null, abandon the key(s) entirely. This allows for
            // graceful degradation for types that don't exist on certain
            // server versions and have no valid counterparts. For instance,
            // bees don't exist prior to 1.15, so no attempt should be made
            // at registering them on 1.14 or below, but we also don't want
            // to log a warning about them.
            if (name == null) {
                return;
            }

            try {
                EntityType type = EntityType.valueOf(name);
                put(key, plural, type);
                return;
            } catch (IllegalArgumentException e) {
                // Swallow and try again
            }
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("MobArena");
        if (plugin != null) {
            if (names.length == 1) {
                plugin.getLogger().warning("Failed to register monster type '" + key + "', because its type was not found: " + names[0]);
            } else {
                plugin.getLogger().warning("Failed to register monster type '" + key + "', because none of its possible types were found: " + Arrays.toString(names));
            }
        }
    }

    private static void copy(String source, String target) {
        MACreature creature = map.get(source);
        if (creature != null) {
            map.put(target, creature);
        }
    }

}
