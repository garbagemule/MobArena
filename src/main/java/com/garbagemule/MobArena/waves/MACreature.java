package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACreature
{
    // This part must come before the constants!
    private static Map<String,MACreature> map;
    static {
        map = new HashMap<>();
        for (EntityType type : EntityType.values()) {
            if (type.isAlive()) {
                // Instantiating a new creature registers it
                new MACreature(type);
            }
        }
    }

    // Default creatures
    public static final MACreature ZOMBIE = new MACreature("zombie", EntityType.ZOMBIE);
    public static final MACreature SKELETON = new MACreature("skeleton", EntityType.SKELETON);
    public static final MACreature SPIDER = new MACreature("spider", EntityType.SPIDER);
    public static final MACreature CREEPER = new MACreature("creeper", EntityType.CREEPER);
    public static final MACreature WOLF = new MACreature("wolf", "wolves", EntityType.WOLF);

    // Special creatures
    public static final MACreature ZOMBIEPIGMAN = new MACreature("zombiepigman", "zombiepigmen", EntityType.PIG_ZOMBIE);
    public static final MACreature POWEREDCREEPER = new MACreature("poweredcreeper", EntityType.CREEPER);
    public static final MACreature ANGRYWOLF = new MACreature("angrywolf", "angrywolves", EntityType.WOLF);
    public static final MACreature ENDERMAN = new MACreature("enderman", "endermen", EntityType.ENDERMAN);

    // 1.0 creatures
    public static final MACreature SNOWMAN = new MACreature("snowman", "snowmen", EntityType.SNOWMAN);
    public static final MACreature SNOWGOLEM = new MACreature("snowgolem", EntityType.SNOWMAN);
    public static final MACreature MOOSHROOM = new MACreature("mooshroom", EntityType.MUSHROOM_COW);

    // Passive creatures
    public static final MACreature SHEEP = new MACreature("sheep", null, EntityType.SHEEP);

    // Extended creatures
    public static final MACreature EXPLODINGSHEEP = new MACreature("explodingsheep", null, EntityType.SHEEP);

    // Slimes
    public static final MACreature SLIMETINY = new MACreature("slimetiny", "slimestiny", EntityType.SLIME);
    public static final MACreature SLIMESMALL = new MACreature("slimesmall", "slimessmall", EntityType.SLIME);
    public static final MACreature SLIMEBIG = new MACreature("slimebig", "slimesbig", EntityType.SLIME);
    public static final MACreature SLIMEHUGE = new MACreature("slimehuge", "slimeshuge", EntityType.SLIME);

    // Magma cubes
    public static final MACreature MAGMACUBETINY = new MACreature("magmacubetiny", "magmacubestiny", EntityType.MAGMA_CUBE);
    public static final MACreature MAGMACUBESMALL = new MACreature("magmacubesmall", "magmacubessmall", EntityType.MAGMA_CUBE);
    public static final MACreature MAGMACUBEBIG = new MACreature("magmacubebig", "magmacubesbig", EntityType.MAGMA_CUBE);
    public static final MACreature MAGMACUBEHUGE = new MACreature("magmacubehuge", "magmacubeshuge", EntityType.MAGMA_CUBE);

    // 1.4 creatures
    public static final MACreature WITCH = new MACreature("witch", "witches", EntityType.WITCH);
    public static final MACreature BABYZOMBIE = new MACreature("babyzombie", EntityType.ZOMBIE);
    public static final MACreature BABYPIGMAN = new MACreature("babypigman", "babypigmen", EntityType.PIG_ZOMBIE);
    public static final MACreature BABYZOMBIEVILLAGER = new MACreature("babyzombievillager", EntityType.ZOMBIE_VILLAGER);

    // 1.6 creatures
    public static final MACreature UNDEADHORSE = new MACreature("undeadhorse", EntityType.ZOMBIE_HORSE);

    // 1.8 creatures
    public static final MACreature KILLERBUNNY = new MACreature("killerbunny", "killerbunnies", EntityType.RABBIT);

    private List<DyeColor> colors = Arrays.asList(DyeColor.values());
    private String name;
    private String plural;
    private EntityType type;

    public MACreature(String name, String plural, EntityType type) {
        this.name = name;
        this.plural = (plural != null) ? plural : name;
        this.type = type;

        register();
    }

    public MACreature(String name, EntityType type) {
        this(name, name + "s", type);
    }

    private MACreature(EntityType type) {
        this(
            type.name().toLowerCase().replaceAll("[-_\\.]", ""),
            type.name().toLowerCase().replaceAll("[-_\\.]", "") + "s",
            type
        );
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

    public static MACreature fromString(String string) {
        return map.get(string.toLowerCase().replaceAll("[-_\\.]", ""));
    }

    public LivingEntity spawn(Arena arena, World world, Location loc) {
        LivingEntity e = (LivingEntity) world.spawnEntity(loc, type);
        e.getEquipment().clear();
        e.setCanPickupItems(false);

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
            case "skeleton":
            case "stray":
                e.getEquipment().setItemInMainHand(new ItemStack(Material.BOW, 1));
                break;
            case "babyzombievillager":
            case "babyzombie":
                ((Zombie) e).setBaby(true);
                break;
            case "babypigman":
                ((Zombie) e).setBaby(true);
                ((PigZombie) e).setAngry(true);
                break;
            case "pigzombie":
            case "zombiepigman":
                ((PigZombie) e).setAngry(true);
                break;
            case "killerbunny":
                ((Rabbit) e).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                break;
            case "witherskeleton":
                e.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD, 1));
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
}
