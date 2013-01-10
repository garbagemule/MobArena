package com.garbagemule.MobArena.waves;

import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public enum MACreature
{
    // Default creatures
    ZOMBIE(EntityType.ZOMBIE),            ZOMBIES(EntityType.ZOMBIE), 
    SKELETON(EntityType.SKELETON),        SKELETONS(EntityType.SKELETON),
    SPIDER(EntityType.SPIDER),            SPIDERS(EntityType.SPIDER),
    CREEPER(EntityType.CREEPER),          CREEPERS(EntityType.CREEPER),
    WOLF(EntityType.WOLF),                WOLVES(EntityType.WOLF),
    
    // Special creatures
    ZOMBIEPIGMAN(EntityType.PIG_ZOMBIE),  ZOMBIEPIGMEN(EntityType.PIG_ZOMBIE),
    POWEREDCREEPER(EntityType.CREEPER),   POWEREDCREEPERS(EntityType.CREEPER),
    ANGRYWOLF(EntityType.WOLF),           ANGRYWOLVES(EntityType.WOLF),
    GIANT(EntityType.GIANT),              GIANTS(EntityType.GIANT),
    GHAST(EntityType.GHAST),              GHASTS(EntityType.GHAST),
    ENDERMAN(EntityType.ENDERMAN),        ENDERMEN(EntityType.ENDERMAN),
    CAVESPIDER(EntityType.CAVE_SPIDER),   CAVESPIDERS(EntityType.CAVE_SPIDER),
    SILVERFISH(EntityType.SILVERFISH),
    
    // 1.0 creatures
    BLAZE(EntityType.BLAZE),              BLAZES(EntityType.BLAZE),
    ENDERDRAGON(EntityType.ENDER_DRAGON), ENDERDRAGONS(EntityType.ENDER_DRAGON),
    SNOWMAN(EntityType.SNOWMAN),          SNOWMEN(EntityType.SNOWMAN),
    SNOWGOLEM(EntityType.SNOWMAN),        SNOWGOLEMS(EntityType.SNOWMAN),
    MUSHROOMCOW(EntityType.MUSHROOM_COW), MUSHROOMCOWS(EntityType.MUSHROOM_COW),
    VILLAGER(EntityType.VILLAGER),        VILLAGERS(EntityType.VILLAGER),
    
    // 1.2 creatures
    OCELOT(EntityType.OCELOT),            OCELOTS(EntityType.OCELOT),
    IRONGOLEM(EntityType.IRON_GOLEM),     IRONGOLEMS(EntityType.IRON_GOLEM),
    
    // Passive creatures
    CHICKEN(EntityType.CHICKEN),          CHICKENS(EntityType.CHICKEN),
    COW(EntityType.COW),                  COWS(EntityType.COW),
    PIG(EntityType.PIG),                  PIGS(EntityType.PIG),
    SHEEP(EntityType.SHEEP),
    SQUID(EntityType.SQUID),              SQUIDS(EntityType.SQUID),
    
    // Extended creatures
    EXPLODINGSHEEP(EntityType.SHEEP),
    
    // Slimes
    SLIME(EntityType.SLIME),              SLIMES(EntityType.SLIME),
    SLIMETINY(EntityType.SLIME),          SLIMESTINY(EntityType.SLIME),
    SLIMESMALL(EntityType.SLIME),         SLIMESSMALL(EntityType.SLIME),
    SLIMEBIG(EntityType.SLIME),           SLIMESBIG(EntityType.SLIME),
    SLIMEHUGE(EntityType.SLIME),          SLIMESHUGE(EntityType.SLIME),
    
    // Magma cubes
    MAGMACUBE(EntityType.MAGMA_CUBE),     MAGMACUBES(EntityType.MAGMA_CUBE),
    MAGMACUBETINY(EntityType.MAGMA_CUBE), MAGMACUBESTINY(EntityType.MAGMA_CUBE),
    MAGMACUBESMALL(EntityType.MAGMA_CUBE),MAGMACUBESSMALL(EntityType.MAGMA_CUBE),
    MAGMACUBEBIG(EntityType.MAGMA_CUBE),  MAGMACUBESBIG(EntityType.MAGMA_CUBE),
    MAGMACUBEHUGE(EntityType.MAGMA_CUBE), MAGMACUBESHUGE(EntityType.MAGMA_CUBE),
        
    // 1.4 creatures
    BAT(EntityType.BAT),                  BATS(EntityType.BAT),
    WITCH(EntityType.WITCH),              WITCHES(EntityType.WITCH),
    WITHER(EntityType.WITHER),            WITHERS(EntityType.WITHER),
    WITHERSKELETON(EntityType.SKELETON),  WITHERSKELETONS(EntityType.SKELETON),
    BABYZOMBIE(EntityType.ZOMBIE),        BABYZOMBIES(EntityType.ZOMBIE),
    ZOMBIEVILLAGER(EntityType.ZOMBIE),    ZOMBIEVILLAGERS(EntityType.ZOMBIE);
    
    private List<DyeColor> colors = Arrays.asList(DyeColor.values());
    private EntityType type;
    
    private MACreature(EntityType type) {
        this.type = type;
    }
    
    public EntityType getType() {
        return type;
    }
    
    public static MACreature fromString(String string) {
        return WaveUtils.getEnumFromString(MACreature.class, string.replaceAll("[-_\\.]", ""));
    }
    
    public LivingEntity spawn(Arena arena, World world, Location loc) {
        LivingEntity e = (LivingEntity) world.spawnEntity(loc, type);
        
        switch (this) {
            case SHEEP:
                ((Sheep) e).setColor(colors.get(MobArena.random.nextInt(colors.size())));
                break;
            case EXPLODINGSHEEP:
                arena.getMonsterManager().addExplodingSheep(e);
                ((Sheep) e).setColor(DyeColor.RED);
                break;
            case POWEREDCREEPERS:
                ((Creeper) e).setPowered(true);
                break;
            case ANGRYWOLVES:
                ((Wolf) e).setAngry(true);
                break;
            case SLIME:
            case SLIMES:
            case MAGMACUBE:
            case MAGMACUBES:
                ((Slime) e).setSize( (1 + MobArena.random.nextInt(3)) );
                break;
            case SLIMETINY:
            case SLIMESTINY:
            case MAGMACUBETINY:
            case MAGMACUBESTINY:
                ((Slime) e).setSize(1);
                break;
            case SLIMESMALL:
            case SLIMESSMALL:
            case MAGMACUBESMALL:
            case MAGMACUBESSMALL:
                ((Slime) e).setSize(2);
                break;
            case SLIMEBIG:
            case SLIMESBIG:
            case MAGMACUBEBIG:
            case MAGMACUBESBIG:
                ((Slime) e).setSize(3);
                break;
            case SLIMEHUGE:
            case SLIMESHUGE:
            case MAGMACUBEHUGE:
            case MAGMACUBESHUGE:
                ((Slime) e).setSize(4);
                break;
            case SKELETON:
            case SKELETONS:
                ((Skeleton) e).getEquipment().setItemInHand(new ItemStack(Material.BOW, 1));
            	break;
            case ZOMBIEPIGMAN:
            case ZOMBIEPIGMEN:
            	((PigZombie) e).getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD, 1));
            	break;
            case ZOMBIEVILLAGER:
            case ZOMBIEVILLAGERS:
                ((Zombie) e).setVillager(true);
                break;
            case BABYZOMBIE:
            case BABYZOMBIES:
                ((Zombie) e).setBaby(true);
            case WITHERSKELETON:
            case WITHERSKELETONS:
                ((Skeleton) e).getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD, 1));
                ((Skeleton) e).setSkeletonType(SkeletonType.WITHER);
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
