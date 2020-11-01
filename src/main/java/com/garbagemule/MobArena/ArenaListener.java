package com.garbagemule.MobArena;

import com.garbagemule.MobArena.events.ArenaKillEvent;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.listeners.MAGlobalListener.TeleportResponse;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.region.RegionPoint;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.repairable.RepairableAttachable;
import com.garbagemule.MobArena.repairable.RepairableBed;
import com.garbagemule.MobArena.repairable.RepairableBlock;
import com.garbagemule.MobArena.repairable.RepairableContainer;
import com.garbagemule.MobArena.repairable.RepairableDoor;
import com.garbagemule.MobArena.repairable.RepairableSign;
import com.garbagemule.MobArena.things.ExperienceThing;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.things.ThingPicker;
import com.garbagemule.MobArena.util.ClassChests;
import com.garbagemule.MobArena.util.Slugs;
import com.garbagemule.MobArena.waves.MABoss;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.Redstone;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ArenaListener
{
    private MobArena plugin;
    private Arena arena;
    private ArenaRegion region;
    private MonsterManager monsters;
    private ClassLimitManager classLimits;

    private boolean softRestore,
            softRestoreDrops,
            protect;
    private boolean monsterExp,
            monsterInfight,
            pvpOn,               // pvp-enabled in config
            pvpEnabled = false,  // activated on first wave
            foodRegen,
            lockFoodLevel,
            useClassChests;
    private boolean allowTeleport,
            canShare,
            autoIgniteTNT;

    private Set<Player> banned;

    private EnumSet<EntityType> excludeFromRetargeting;

    public ArenaListener(Arena arena, MobArena plugin) {
        this.plugin = plugin;
        this.arena = arena;
        this.region = arena.getRegion();
        this.monsters = arena.getMonsterManager();

        ConfigurationSection s = arena.getSettings();
        this.softRestore      = s.getBoolean("soft-restore",         false);
        this.softRestoreDrops = s.getBoolean("soft-restore-drops",   false);
        this.protect          = s.getBoolean("protect",              true);
        this.monsterExp       = s.getBoolean("monster-exp",          false);
        this.monsterInfight   = s.getBoolean("monster-infight",      false);
        this.pvpOn            = s.getBoolean("pvp-enabled",          false);
        this.foodRegen        = s.getBoolean("food-regen",           false);
        this.lockFoodLevel    = s.getBoolean("lock-food-level",      true);
        this.allowTeleport    = s.getBoolean("allow-teleporting",    false);
        this.canShare         = s.getBoolean("share-items-in-arena", true);
        this.autoIgniteTNT    = s.getBoolean("auto-ignite-tnt",      false);
        this.useClassChests   = s.getBoolean("use-class-chests",     false);

        this.classLimits = arena.getClassLimitManager();

        this.banned = new HashSet<>();

        this.excludeFromRetargeting = EnumSet.of(
            EntityType.ELDER_GUARDIAN,
            EntityType.GUARDIAN
        );
    }

    void pvpActivate() {
        if (arena.isRunning() && !arena.getPlayersInArena().isEmpty()) {
            pvpEnabled = pvpOn;
        }
    }

    void pvpDeactivate() {
        if (pvpOn) pvpEnabled = false;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the block is a sign, it might be a leaderboard
        if (event.getBlock() instanceof Sign) {
            // If the sign is the leaderboard sign, null out the config
            if (event.getBlock().getLocation().equals(arena.getRegion().getLeaderboard())) {
                arena.getRegion().set("leaderboard", null);
            }
        }

        // If the arena isn't protected, care
        if (!protect) return;

        if (!arena.getRegion().contains(event.getBlock().getLocation()))
            return;

        if (!arena.inArena(event.getPlayer())) {
            if (arena.inEditMode())
                return;
            else
                event.setCancelled(true);
        }

        if (onBlockDestroy(event))
            return;

        event.setCancelled(true);
    }

    public void onHangingBreak(HangingBreakEvent event) {
        // If the arena isn't protected, care
        if (!protect) return;

        Location l = event.getEntity().getLocation();
        if (!arena.getRegion().contains(l)) {
            return;
        }
        if (arena.inEditMode()) {
            return;
        }
        event.setCancelled(true);
    }

    public void onBlockBurn(BlockBurnEvent event) {
        // If the arena isn't protected, care
        if (!protect) return;

        if (!arena.getRegion().contains(event.getBlock().getLocation()) || onBlockDestroy(event))
            return;

        event.setCancelled(true);
    }

    private boolean onBlockDestroy(BlockEvent event) {
        if (arena.inEditMode())
            return true;

        if (!arena.isRunning())
            return false;

        Block b = event.getBlock();
        if (arena.removeBlock(b) || b.getType() == Material.TNT)
            return true;

        if (softRestore) {
            BlockState state = b.getState();
            Repairable r = null;

            if (state instanceof InventoryHolder)
                r = new RepairableContainer(state);
            else if (state instanceof Sign)
                r = new RepairableSign(state);
            else if (state.getData() instanceof Attachable)
                r = new RepairableAttachable(state);
            else
                r = new RepairableBlock(state);

            arena.addRepairable(r);

            if (!softRestoreDrops)
                b.setType(Material.AIR);
            return true;
        }

        return false;
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Block b = event.getBlock();

        // If the event didn't happen in the region, or if in edit mode, ignore
        if (!arena.getRegion().contains(b.getLocation()) || arena.inEditMode()) {
            return;
        }

        // If the arena isn't running, or if the player isn't in the arena, cancel.
        if (!arena.isRunning() || !arena.inArena(event.getPlayer())) {
            // But only if we're protecting the region
            if (protect) {
                event.setCancelled(true);
            }
            return;
        }

        // If the block is TNT, set its planter
        if (b.getType() == Material.TNT) {
            // If auto-igniting, set the planter of the primed TNT instead
            if (autoIgniteTNT) {
                event.setCancelled(true);
                ItemStack stack = event.getItemInHand();
                if (stack == null || stack.getType() != Material.TNT) {
                    plugin.getLogger().warning("Player " + event.getPlayer().getDisplayName() + " just placed TNT without holding a TNT block");
                    return;
                }
                stack.setAmount(stack.getAmount() - 1);
                TNTPrimed tnt = b.getWorld().spawn(b.getRelative(BlockFace.UP).getLocation(), TNTPrimed.class);
                setPlanter(tnt, event.getPlayer());
                return;
            }
            setPlanter(b, event.getPlayer());
        }

        // Any other block we don't care about if we're not protecting
        if (!protect) {
            return;
        }

        // Otherwise, block was placed during a session.
        arena.addBlock(b);

        if (b.getType().name().endsWith("_DOOR")) {
            // For doors, add the block just above (so we get both halves)
            arena.addBlock(b.getRelative(0, 1, 0));
        }
    }

    private void setPlanter(Metadatable tnt, Player planter) {
        tnt.setMetadata("mobarena-planter", new FixedMetadataValue(plugin, planter));
    }

    private Player getPlanter(Metadatable tnt) {
        List<MetadataValue> values = tnt.getMetadata("mobarena-planter");
        for (MetadataValue value : values) {
            if (value.getOwningPlugin().equals(plugin)) {
                return (Player) value.value();
            }
        }
        return null;
    }

    public void onBlockForm(BlockFormEvent event) {
        // If the arena isn't protected, care
        if (!protect) return;

        if (!arena.getRegion().contains(event.getBlock().getLocation()))
            return;

        // If a snowman forms some snow on its path, add the block
        if (event.getNewState().getType() == Material.SNOW)
            arena.addBlock(event.getBlock());
    }

    public void onBlockFade(BlockFadeEvent event) {
        if (!protect) {
            return;
        }
        if (!arena.getRegion().contains(event.getBlock().getLocation())) {
            return;
        }
        switch (event.getBlock().getType()) {
            case ICE:
            case SNOW:
                event.setCancelled(true);
                break;
        }
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        // If the arena isn't protected, care
        if (!protect) return;

        Block b = event.getBlock();
        if (!arena.getRegion().contains(b.getLocation()))
            return;

        switch (event.getCause()) {
            case FLINT_AND_STEEL:
                if (arena.inEditMode()) return;
                if (arena.isRunning()) {
                    if (b.getType() == Material.TNT) {
                        Player planter = getPlanter(b);
                        if (planter != null) {
                            b.setType(Material.AIR);
                            TNTPrimed tnt = b.getWorld().spawn(b.getLocation(), TNTPrimed.class);
                            setPlanter(tnt, planter);
                        }
                    } else {
                        arena.addBlock(event.getBlock().getRelative(BlockFace.UP));
                    }
                    break;
                }
            case LIGHTNING:
            case SPREAD:
            case FIREBALL:
            case EXPLOSION:
            case LAVA:
                event.setCancelled(true);
                break;
        }
    }

    public void onSignChange(SignChangeEvent event) {
        arena.setLeaderboard(new Leaderboard(plugin, arena, event.getBlock().getLocation()));
        arena.getRegion().set(RegionPoint.LEADERBOARD, event.getBlock().getLocation());

        arena.getPlugin().getGlobalMessenger().tell(event.getPlayer(), "Leaderboard made. Now set up the stat signs!");
    }

    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!arena.getRegion().contains(event.getLocation())) {
            return;
        }

        if (arena.inEditMode() && event.getEntityType() == EntityType.ARMOR_STAND) {
            return;
        }

        if (!arena.isRunning()) {
            // Just block everything if we're not running
            event.setCancelled(true);
            return;
        }

        SpawnReason reason = event.getSpawnReason();

        // Allow player-made iron golems and snowmen
        if (reason == SpawnReason.BUILD_IRONGOLEM || reason == SpawnReason.BUILD_SNOWMAN) {
            event.setCancelled(false);
            monsters.addGolem(event.getEntity());
            return;
        }

        /*
         * We normally want to block all "default" spawns, because this
         * reason means MobArena didn't trigger the event. However, we
         * make an exception for certain mobs that spawn as results of
         * other entities spawning them, e.g. when Evokers summon Vexes.
         */
        if (reason == SpawnReason.DEFAULT) {
            if (event.getEntityType() == EntityType.VEX) {
                event.setCancelled(false);
                monsters.addMonster(event.getEntity());
            } else {
                event.setCancelled(true);
            }
            return;
        }

        // If not custom, we probably don't want it, so get rid of it
        if (reason != SpawnReason.CUSTOM) {
            event.setCancelled(true);
            return;
        }

        // Otherwise, we probably want it, so uncancel just in case
        event.setCancelled(false);

        /*
         * Because MACreature works with the Creature interface rather
         * than the Mob interface, it doesn't catch Slimes and Magma
         * Cubes, so we catch them here.
         */
        LivingEntity entity = event.getEntity();
        if (entity instanceof Slime) {
            monsters.addMonster(entity);
        }
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        if (!monsters.getMonsters().contains(event.getEntity()) && !arena.getRegion().contains(event.getLocation(), 10))
            return;

        // The generic remove method removes bosses as well
        monsters.remove(event.getEntity());

        // Cancel if the arena isn't running
        if (!arena.isRunning()) {
            event.setCancelled(true);
            return;
        }

        // Uncancel, just in case.
        event.setCancelled(false);

        // If the arena isn't destructible, just clear the blocklist.
        if (!softRestore && protect) {
            List<Block> blocks = new LinkedList<>(arena.getBlocks());
            event.blockList().retainAll(blocks);
            return;
        }

        if (!softRestoreDrops)
            event.setYield(0);

        // Handle all the blocks in the block list.
        for (Block b : event.blockList()) {
            BlockState state = b.getState();

            if (state.getData() instanceof Door && ((Door) state.getData()).isTopHalf()) {
                state = b.getRelative(BlockFace.DOWN).getState();
            }
            else if (state.getData() instanceof Bed && ((Bed) state.getData()).isHeadOfBed()) {
                state = b.getRelative(((Bed) state.getData()).getFacing().getOppositeFace()).getState();
            }

            // Create a Repairable from the block.
            Repairable r = null;
            if (state instanceof InventoryHolder)
                r = new RepairableContainer(state);
            else if (state instanceof Sign)
                r = new RepairableSign(state);
            else if (state.getData() instanceof Bed)
                r = new RepairableBed(state);
            else if (state.getData() instanceof Door)
                r = new RepairableDoor(state);
            else if (state.getData() instanceof Attachable || state.getData() instanceof Redstone)
                r = new RepairableAttachable(state);
            else
                r = new RepairableBlock(state);

            // Cakes and liquids should just get removed. If player-placed block, drop as item.
            Material mat = state.getType();
            if (mat == Material.CAKE || mat == Material.WATER || mat == Material.LAVA)
                arena.removeBlock(b);
            else if (arena.removeBlock(b))
                arena.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(state.getType(), 1));
            else if (softRestore)
                arena.addRepairable(r);
            else
                arena.queueRepairable(r);
        }
    }

    /******************************************************
     *
     *                  DEATH LISTENERS
     *
     ******************************************************/

    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) {
            onPlayerDeath((PlayerDeathEvent) event, (Player) event.getEntity());
        }
        else if (monsters.hasPet(event.getEntity())) {
            monsters.removePet(event.getEntity());
        }
        else if (monsters.removeMonster(event.getEntity())) {
            onMonsterDeath(event);
        }
        else if (monsters.removeMount(event.getEntity())) {
            onMountDeath(event);
        }
        else if (monsters.removeGolem(event.getEntity())) {
            arena.announce(Msg.GOLEM_DIED);
        }
    }

    private void onPlayerDeath(PlayerDeathEvent event, Player player) {
        if (arena.inArena(player) || arena.inLobby(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setKeepLevel(true);
            if (player.getKiller() != null) {
                callKillEvent(player.getKiller(), player);
            }
            if (arena.getSettings().getBoolean("show-death-messages", true)) {
                arena.announce(event.getDeathMessage());
            }
            if (arena.getSettings().getBoolean("keep-exp", false)) {
                arena.getRewardManager().addReward(player, new ExperienceThing(player.getTotalExperience()));
            }
            event.setDeathMessage(null);
            arena.getScoreboard().death(player);
            arena.playerDeath(player);
        } else if (arena.inSpec(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            arena.getScoreboard().death(player);
            arena.playerLeave(player);
        }
    }

    public boolean onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        if (!arena.isDead(p)) {
            return false;
        }

        Location loc = arena.getRespawnLocation(p);
        event.setRespawnLocation(loc);

        arena.playerRespawn(p);
        return true;
    }

    private void onMountDeath(EntityDeathEvent event) {
        // Shouldn't ever happen
    }

    private void onMonsterDeath(EntityDeathEvent event) {
        EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
        EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
        Entity damager = (e2 != null) ? e2.getDamager() : null;
        LivingEntity damagee = event.getEntity();

        // Make sure to grab the owner of a projectile/pet
        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Entity) {
                damager = (Entity) shooter;
            }
        } else {
            Player owner = arena.getMonsterManager().getOwner(damager);
            if (owner != null) {
                damager = owner;
            }
        }

        // If the damager was a player, add to kills.
        if (damager instanceof Player) {
            Player p = (Player) damager;
            ArenaPlayer ap = arena.getArenaPlayer(p);
            if (ap != null) {
                ArenaPlayerStatistics stats = ap.getStats();
                if (stats != null) {
                    ap.getStats().inc("kills");
                    arena.getScoreboard().addKill(p);
                }
                MABoss boss = monsters.getBoss(damagee);
                if (boss != null) {
                    for (Player q : arena.getPlayersInArena()) {
                        arena.getMessenger().tell(q, Msg.WAVE_BOSS_KILLED, p.getName());
                    }
                    ThingPicker picker = boss.getReward();
                    if (picker != null) {
                        Thing reward = picker.pick();
                        if (reward != null) {
                            arena.getRewardManager().addReward(p, reward);
                            arena.getMessenger().tell(damager, Msg.WAVE_BOSS_REWARD_EARNED, reward.toString());
                        }
                    }
                }
            }
            callKillEvent(p, damagee);
        }

        if (!monsterExp) {
            event.setDroppedExp(0);
        }

        event.getDrops().clear();

        MABoss boss = monsters.removeBoss(damagee);
        if (boss != null) {
            List<ItemStack> drops = boss.getDrops();
            if (drops != null && !drops.isEmpty()) {
                event.getDrops().addAll(drops);
            }
            boss.setDead(true);
            boss.getHealthBar().setProgress(0);
        }

        List<ItemStack> loot = monsters.getLoot(damagee);
        if (loot != null && !loot.isEmpty()) {
            event.getDrops().add(getRandomItem(loot));
        }
    }

    private void callKillEvent(Player killer, Entity victim) {
        ArenaKillEvent event = new ArenaKillEvent(arena, killer, victim);
        plugin.getServer().getPluginManager().callEvent(event);
    }

    private ItemStack getRandomItem(List<ItemStack> stacks) {
        return stacks.get((new Random()).nextInt(stacks.size()));
    }

    /******************************************************
     *
     *                  DAMAGE LISTENERS
     *
     ******************************************************/

    public void onEntityDamage(EntityDamageEvent event) {
        Entity damagee = event.getEntity();

        EntityDamageByEntityEvent edbe = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damager = null;

        if (edbe != null) {
            damager = edbe.getDamager();

            if (damager instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) damager).getShooter();
                if (shooter instanceof Entity) {
                    damager = (Entity) shooter;
                }
            }

            if (damager instanceof TNTPrimed) {
                damager = getPlanter(damager);
            }
        }

        MABoss boss = (damagee instanceof LivingEntity)
            ? monsters.getBoss((LivingEntity) damagee)
            : null;

        // Pets
        if (arena.hasPet(damagee)) {
            onPetDamage(event, damagee, damager);
        }
        else if (damagee instanceof ArmorStand) {
            onArmorStandDamage(event);
        }
        // Mount
        else if (damagee instanceof AbstractHorse && monsters.hasMount(damagee)) {
            onMountDamage(event, (Horse) damagee, damager);
        }
        // Player
        else if (damagee instanceof Player) {
            onPlayerDamage(event, (Player) damagee, damager);
        }
        // Snowmen melting
        else if (damagee instanceof Snowman && event.getCause() == DamageCause.MELTING) {
            if (arena.isRunning() && arena.getRegion().contains(damagee.getLocation())) {
                event.setCancelled(true);
            }
        }
        // Boss monster
        else if (boss != null) {
            onBossDamage(event, boss, damager);
        }
        // Regular monster
        else if (monsters.getMonsters().contains(damagee)) {
            onMonsterDamage(event, damagee, damager);
        }
        // Player made golems
        else if (monsters.getGolems().contains(damagee)) {
            onGolemDamage(event, damagee, damager);
        }
    }

    private void onPlayerDamage(EntityDamageEvent event, Player player, Entity damager) {
        // Cancel all damage in the lobby and spec area
        if (arena.inLobby(player) || arena.inSpec(player)) {
            event.setCancelled(true);
            return;
        }

        if (arena.inArena(player)) {
            // Cancel PvP damage if disabled
            if (!pvpEnabled && damager instanceof Player && !damager.equals(player)) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(false);
            arena.getArenaPlayer(player).getStats().add("dmgTaken", event.getDamage());

            // Redirect pet aggro (but not at players)
            if (damager instanceof LivingEntity && !(damager instanceof Player)) {
                LivingEntity target = (LivingEntity) damager;
                monsters.getPets(player).forEach(pet -> {
                    if (pet instanceof Mob) {
                        Mob mob = (Mob) pet;
                        if (mob.getTarget() == null) {
                            mob.setTarget(target);
                        }
                    }
                });
            }
        }
    }

    private void onPetDamage(EntityDamageEvent event, Entity pet, Entity damager) {
        event.setCancelled(true);
    }

    private void onArmorStandDamage(EntityDamageEvent event) {
        if (protect && !arena.inEditMode() && region.contains(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    private void onMountDamage(EntityDamageEvent event, Horse mount, Entity damager) {
        event.setCancelled(true);
    }

    private void onBossDamage(EntityDamageEvent event, MABoss boss, Entity damager) {
        onMonsterDamage(event, boss.getEntity(), damager);
        if (event.isCancelled()) {
            return;
        }

        double progress = boss.getHealth() / boss.getMaxHealth();
        boss.getHealthBar().setProgress(progress);
    }

    private void onMonsterDamage(EntityDamageEvent event, Entity monster, Entity damager) {
        if (damager instanceof Player) {
            Player p = (Player) damager;
            if (!arena.inArena(p)) {
                event.setCancelled(true);
                return;
            }

            ArenaPlayerStatistics aps = arena.getArenaPlayer(p).getStats();
            aps.add("dmgDone", event.getDamage());
            aps.inc("hits");
        }
        else if (arena.hasPet(damager)) {
            Player owner = arena.getMonsterManager().getOwner(damager);
            if (owner != null) {
                ArenaPlayerStatistics aps = arena.getArenaPlayer(owner).getStats();
                aps.add("dmgDone", event.getDamage());
            }
        }
        else if (monsters.getMonsters().contains(damager)) {
            if (!monsterInfight)
                event.setCancelled(true);
        }
    }

    private void onGolemDamage(EntityDamageEvent event, Entity golem, Entity damager) {
        if (damager instanceof Player) {
            Player p = (Player) damager;
            if (!arena.inArena(p)) {
                event.setCancelled(true);
                return;
            }

            if (!pvpEnabled) {
                event.setCancelled(true);
            }
        }
    }

    public void onEntityCombust(EntityCombustEvent event) {
        if (monsters.getMonsters().contains(event.getEntity())) {
            if (event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent) {
                return;
            }
            event.setCancelled(true);
        }
    }

    public void onEntityTarget(EntityTargetEvent event) {
        if (!arena.isRunning() || event.isCancelled())
            return;

        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (isArenaPet(entity)) {
            onPetTarget(event, target);
        } else if (isArenaMonster(entity)) {
            onMonsterTarget(event, entity, target);
        } else {
            onForeignTarget(event, target);
        }
    }

    private void onPetTarget(EntityTargetEvent event, Entity target) {
        // If the target is null, do nothing
        if (target == null) {
            return;
        }

        // Pets should only attack monsters in the arena, nothing else
        if (!isArenaMonster(target)) {
            event.setCancelled(true);
        }
    }

    private void onMonsterTarget(EntityTargetEvent event, Entity monster, Entity target) {
        // Null means we lost our target or the target died, so find a new one
        if (target == null) {
            // ... unless the monster is excluded from retargeting
            if (excludeFromRetargeting.contains(monster.getType())) {
                return;
            }
            event.setTarget(MAUtils.getClosestPlayer(plugin, monster, arena));
            return;
        }

        // If monster infighting is on and target is monster, let it happen
        if (monsterInfight && isArenaMonster(target)) {
            return;
        }

        // At this point, if the target is _not_ an arena player, we can just
        // cancel, because this check also covers the target being a pet, an
        // arena monster when infighting is off (due to the check above), or
        // any foreign entity.
        if (!isArenaPlayer(target)) {
            event.setCancelled(true);
        }
    }

    private void onForeignTarget(EntityTargetEvent event, Entity target) {
        // If null, just bail
        if (target == null) {
            return;
        }

        // Foreign entities can't target arena pets, -players, or -monsters.
        if (isArenaPet(target) || isArenaPlayer(target) || isArenaMonster(target)) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private boolean isArenaPlayer(Entity entity) {
        return arena.getPlayersInArena().contains(entity);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private boolean isArenaMonster(Entity entity) {
        return monsters.getMonsters().contains(entity);
    }

    private boolean isArenaPet(Entity entity) {
        return arena.hasPet(entity);
    }

    public void onEntityTeleport(EntityTeleportEvent event) {
        if (monsters.hasPet(event.getEntity()) && region.contains(event.getTo())) {
            return;
        }
        if (region.contains(event.getFrom()) || region.contains(event.getTo())) {
            event.setCancelled(true);
        }
    }

    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        if (!region.contains(potion.getLocation())) {
            return;
        }

        if (potion.getShooter() instanceof Player) {
            // Check for PvP stuff if the shooter is a player
            if (!pvpEnabled) {
                // If a potion has harmful effects, remove all players.
                for (PotionEffect effect : potion.getEffects()) {
                    PotionEffectType type = effect.getType();
                    if (type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.POISON)) {
                        for (LivingEntity le : event.getAffectedEntities()) {
                            if (le instanceof Player) {
                                event.setIntensity(le, 0.0);
                            }
                        }
                        break;
                    }
                }
            }
        } else if (!monsterInfight) {
            // Otherwise, check for monster infighting
            for (PotionEffect effect : potion.getEffects()) {
                PotionEffectType type = effect.getType();
                if (type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.POISON)) {
                    for (LivingEntity le : event.getAffectedEntities()) {
                        if (!(le instanceof Player)) {
                            event.setIntensity(le, 0.0);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (arena.getRegion().contains(event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!arena.isRunning())
            return;

        if (!(event.getEntity() instanceof Player) || !arena.inArena((Player) event.getEntity()))
            return;

        if (!foodRegen && event.getRegainReason() == RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) event.getEntity();

        if (arena.isRunning()) {
            if (arena.inArena(p) && lockFoodLevel) {
                event.setCancelled(true);
            }
        } else {
            // Always locked in lobby/spec
            if (arena.inLobby(p) || arena.inSpec(p)) {
                event.setCancelled(true);
            }
        }
    }

    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (!arena.isRunning() || !arena.inArena(event.getPlayer()))
            return;

        arena.getArenaPlayer(event.getPlayer()).getStats().inc("swings");
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();

        /*
         * If the player "drops an item" while in the process of leaving the
         * arena, it has to be due to something that happens in the leaving
         * process because most of the event system is single-threaded. This
         * doesn't make much sense, but it can happen if the player earns a
         * command reward of /give, which drops the item with a drop delay of
         * 0 from the player, causing the player to immediately pick it up.
         * Cancelling this event causes the player to somehow pick up the item
         * twice, so we don't want to do that. This early return should guard
         * against this specific case, and hopefully not break anything else.
         */
        if (arena.isLeaving(p)) {
            return;
        }

        // If the player is active in the arena, only cancel if sharing is not allowed
        if (arena.inArena(p)) {
            if (!canShare) {
                arena.getMessenger().tell(p, Msg.LOBBY_DROP_ITEM);
                event.setCancelled(true);
            }
        }

        // If the player is in the lobby, just cancel
        else if (arena.inLobby(p)) {
            arena.getMessenger().tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }

        // Same if it's a spectator, but...
        else if (arena.inSpec(p)) {
            arena.getMessenger().tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);

            // If the spectator isn't in the region, force them to leave
            if (!region.contains(p.getLocation())) {
                arena.getMessenger().tell(p, Msg.MISC_MA_LEAVE_REMINDER);
                arena.playerLeave(p);
            }
        }

        /*
         * If the player is not in the arena in any way (as arena player, lobby
         * player or a spectator), but they -are- in the region, it must mean
         * they are trying to drop items when not allowed
         */
        else if (region.contains(p.getLocation())) {
            arena.getMessenger().tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }

        /*
         * If the player is in the banned set, it means they got kicked or
         * disconnected during a session, meaning they are more than likely
         * trying to steal items, if a PlayerDropItemEvent is fired.
         */
        else if (banned.contains(p)) {
            plugin.getLogger().warning("Player " + p.getName() + " tried to steal class items!");
            event.setCancelled(true);
        }
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!arena.getReadyPlayersInLobby().contains(event.getPlayer()) && !arena.inArena(event.getPlayer()))
            return;

        if (!arena.isRunning()) {
            event.getBlockClicked().getRelative(event.getBlockFace()).setType(Material.AIR);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getRelative(event.getBlockFace());
        arena.addBlock(liquid);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!arena.inLobby(p)) return;

        // Prevent placing blocks and using held items
        if (event.hasItem()) {
            event.setUseItemInHand(Result.DENY);
        }

        // Bail if off-hand or if there's no block involved.
        if (event.getHand() == EquipmentSlot.OFF_HAND || !event.hasBlock())
            return;

        // Iron block
        if (event.getClickedBlock().getType() == Material.IRON_BLOCK) {
            handleReadyBlock(p);
        }
        // Sign
        else if (event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            handleSign(sign, p);
        }
    }

    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (protect && !arena.inEditMode() && region.contains(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    private void handleReadyBlock(Player p) {
        if (arena.getArenaPlayer(p).getArenaClass() != null) {
            arena.getMessenger().tell(p, Msg.LOBBY_PLAYER_READY);
            arena.playerReady(p);
        }
        else {
            arena.getMessenger().tell(p, Msg.LOBBY_PICK_CLASS);
        }
    }

    private void handleSign(Sign sign, Player p) {
        // Check if the first line is a class name.
        String className = ChatColor.stripColor(sign.getLine(0));
        String slug = Slugs.create(className);

        if (!arena.getClasses().containsKey(slug) && !slug.equals("random"))
            return;

        ArenaClass newAC = arena.getClasses().get(slug);

        // Check for permission.
        if (!newAC.hasPermission(p) && !slug.equals("random")) {
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PERMISSION);
            return;
        }

        ArenaClass oldAC = arena.getArenaPlayer(p).getArenaClass();

        // Same class, do nothing.
        if (newAC.equals(oldAC)) {
            return;
        }

        // If the new class is full, inform the player.
        if (!classLimits.canPlayerJoinClass(newAC)) {
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_FULL);
            return;
        }

        // Check price, balance, and inform
        Thing price = newAC.getPrice();
        if (price != null) {
            if (!price.heldBy(p)) {
                arena.getMessenger().tell(p, Msg.LOBBY_CLASS_TOO_EXPENSIVE, price.toString());
                return;
            }
        }

        // Otherwise, leave the old class, and pick the new!
        classLimits.playerLeftClass(oldAC, p);
        classLimits.playerPickedClass(newAC, p);

        // Delay the inventory stuff to ensure that right-clicking works.
        delayAssignClass(p, slug, price, sign);
    }

    /*private boolean cansPlayerJoinClass(ArenaClass ac, Player p) {
        // If they can not join the class, deny them
        if (!classLimits.canPlayerJoinClass(ac)) {
            Messenger.tell(p, Msg.LOBBY_CLASS_FULL);
            return false;
        }

        // Increment the "in use" in the Class Limit Manager
        classLimits.playerPickedClass(ac);
        return true;
    }*/

    private void delayAssignClass(final Player p, final String slug, final Thing price, final Sign sign) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable() {
            public void run() {
                if (!slug.equalsIgnoreCase("random")) {
                    if (useClassChests) {
                        ArenaClass ac = plugin.getArenaMaster().getClasses().get(slug);
                        if (ClassChests.assignClassFromStoredClassChest(arena, p, ac)) {
                            return;
                        }
                        if (ClassChests.assignClassFromClassChestSearch(arena, p, ac, sign)) {
                            return;
                        }
                        // Otherwise just fall through and use the items from the config-file
                    }
                    arena.assignClass(p, slug);
                    arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PICKED, arena.getClasses().get(slug).getConfigName());
                    if (price != null) {
                        arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PRICE,  price.toString());
                    }
                }
                else {
                    arena.addRandomPlayer(p);
                    arena.getMessenger().tell(p, Msg.LOBBY_CLASS_RANDOM);
                }
            }
        });
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (!arena.isEnabled() || (!arena.inArena(p) && !arena.inLobby(p) && !arena.inSpec(p))) {
            return;
        }

        arena.playerLeave(p);
        banned.add(p);
        scheduleUnban(p, 20);
    }

    public void onPlayerKick(PlayerKickEvent event) {
        Player p = event.getPlayer();
        if (!arena.isEnabled() || (!arena.inArena(p) && !arena.inLobby(p) && !arena.inSpec(p))) {
            return;
        }

        arena.playerLeave(p);
        banned.add(p);
        scheduleUnban(p, 20);
    }

    private void scheduleUnban(final Player p, int ticks) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                banned.remove(p);
            }
        }, ticks);
    }

    public TeleportResponse onPlayerTeleport(PlayerTeleportEvent event) {
        if (!arena.isEnabled() || !region.isSetup() || arena.inEditMode() || allowTeleport) {
            return TeleportResponse.IDGAF;
        }

        Player p = event.getPlayer();

        /*
         * Players that are being moved around by the arena are always allowed
         * to teleport. They stay in this "moving" state only during the actual
         * transition and are removed from that state as soon as the transition
         * is complete.
         */
        if (arena.isMoving(p)) {
            return TeleportResponse.ALLOW;
        }

        Location to = event.getTo();
        Location from = event.getFrom();

        /*
         * At this point we're looking at warping of players that are either in
         * the arena - but not in the "moving" state - or not in the arena. The
         * tricky bit here is to figure out the edge cases. This is essentially
         * a matter of players "teleporting on their own" (or with assistance
         * from other plugins).
         */
        if (region.contains(from)) {
            if (region.contains(to)) {
                // Inside -> inside
                if (!(arena.inArena(p) || arena.inLobby(p))) {
                    arena.getMessenger().tell(p, Msg.WARP_TO_ARENA);
                    return TeleportResponse.REJECT;
                }
                return TeleportResponse.ALLOW;
            } else {
                // Inside -> outside
                if (arena.getAllPlayers().contains(p)) {
                    arena.getMessenger().tell(p, Msg.WARP_FROM_ARENA);
                    return TeleportResponse.REJECT;
                }
                return TeleportResponse.IDGAF;
            }
        } else {
            if (region.contains(to)) {
                // Outside -> inside
                arena.getMessenger().tell(p, Msg.WARP_TO_ARENA);
                return TeleportResponse.REJECT;
            } else {
                // Outside -> outside
                return TeleportResponse.IDGAF;
            }
        }
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();

        if (event.isCancelled() || (!arena.inArena(p) && !arena.inSpec(p) && !arena.inLobby(p))) {
            return;
        }

        // This is safe, because commands will always have at least one element.
        String base = event.getMessage().split(" ")[0].toLowerCase();

        // Check if the entire base command is allowed.
        if (plugin.getArenaMaster().isAllowed(base)) {
            return;
        }

        // If not, check if the specific command is allowed.
        String noslash = event.getMessage().substring(1).toLowerCase();
        if (plugin.getArenaMaster().isAllowed(noslash)) {
            return;
        }

        // This is dirty, but it ensures that commands are indeed blocked.
        event.setMessage("/");

        // Cancel the event regardless.
        event.setCancelled(true);
        arena.getMessenger().tell(p, Msg.MISC_COMMAND_NOT_ALLOWED);
    }

    public void onPlayerPreLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (p == null || !p.isOnline()) return;

        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(p);
        if (arena == null) return;

        arena.playerLeave(p);
    }

    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        if (!(entity instanceof Player)) return;

        Player p = (Player) entity;
        if (!arena.inArena(p)) return;

        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Horse)) return;

        Horse horse = (Horse) vehicle;
        if (!monsters.hasMount(horse)) return;

        AnimalTamer tamer = horse.getOwner();
        if (tamer.equals(p)) {
            horse.setAI(true);
        } else {
            event.setCancelled(true);
        }
    }

    public void onVehicleExit(VehicleExitEvent event) {
        LivingEntity entity = event.getExited();
        if (!(entity instanceof Player)) return;

        Player p = (Player) entity;
        if (!arena.inArena(p)) return;

        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Horse)) return;

        Horse horse = (Horse) vehicle;
        if (!monsters.hasMount(horse)) return;

        horse.setAI(false);
    }
}
