package com.garbagemule.MobArena;

import java.util.*;

import com.garbagemule.MobArena.events.ArenaKillEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Attachable;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.Redstone;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.listeners.MAGlobalListener.TeleportResponse;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.region.RegionPoint;
import com.garbagemule.MobArena.repairable.*;
import com.garbagemule.MobArena.util.TextUtils;
import com.garbagemule.MobArena.waves.MABoss;

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
    @SuppressWarnings("unused")
    private boolean allowTeleport,
            canShare,
            allowMonsters,
            autoIgniteTNT;

    private Set<Player> banned;

    public ArenaListener(Arena arena, MobArena plugin) {
        this.plugin = plugin;
        this.arena = arena;
        this.region = arena.getRegion();
        this.monsters = arena.getMonsterManager();

        /*
         * TODO: Figure out if this is really a good idea + It saves needing all
         * those methods in Arena.java + It is relatively simple + It would be
         * fairly easy to implement an observer pattern - More private fields -
         * Uglier code
         */
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

        this.allowMonsters = arena.getWorld().getAllowMonsters();

        this.banned = new HashSet<Player>();
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
            if (arena.isProtected())
                return false;
            
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
                b.setTypeId(0);
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
                event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
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

        if (b.getType() == Material.WOODEN_DOOR || b.getType() == Material.IRON_DOOR_BLOCK) {
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

    /*
     * TODO: Figure out a solution to this problem with soft-restore.
     *
     * When a player empties a water bucket, and the flowing water creates a
     * new source block somewhere else because of it, currently, this source
     * block is not added to the set of blocks to clear at arena end.
     *
     * This method fixes this, but it is currently not called from the global
     * listener, because it introduces a new issue; source blocks formed when
     * a player FILLS a water bucket (due to the other source blocks flowing
     * back in) are also caught, which means the arena region will restore
     * incorrectly, i.e. the method is not specific enough.
     */
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!protect) return;

        if (!arena.isRunning())
            return;

        if (!arena.getRegion().contains(event.getBlock().getLocation()))
            return;

        Block from = event.getBlock();
        Block to = event.getToBlock();

        if (isWaterSource(from) && isWaterNonSource(to)) {
            for (BlockFace face : EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
                Block adj = to.getRelative(face);
                if (!adj.equals(from) && isWaterSource(adj)) {
                    arena.addBlock(to);
                    return;
                }
            }
        }
    }

    private boolean isWater(Block block) {
        switch (block.getType()) {
            case WATER:
            case STATIONARY_WATER:
                return true;
            default:
                return false;
        }
    }

    private boolean isSource(Block block) {
        return block.getData() == 0x0;
    }

    private boolean isWaterSource(Block block) {
        return isWater(block) && isSource(block);
    }

    private boolean isWaterNonSource(Block block) {
        return isWater(block) && !isSource(block);
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
                            b.setTypeId(0);
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

        Messenger.tell(event.getPlayer(), "Leaderboard made. Now set up the stat signs!");
    }

    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!arena.getRegion().contains(event.getLocation())) {
            return;
        }

        if (event.getSpawnReason() != SpawnReason.CUSTOM) {
            if (event.getSpawnReason() == SpawnReason.BUILD_IRONGOLEM || event.getSpawnReason() == SpawnReason.BUILD_SNOWMAN) {
                monsters.addGolem(event.getEntity());
            }
            else {
                event.setCancelled(true);
                return;
            }
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (arena.isRunning() && entity instanceof Slime)
            monsters.addMonster(entity);

        // If running == true, setCancelled(false), and vice versa.
        event.setCancelled(!arena.isRunning());
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
            List<Block> blocks = new LinkedList<Block>(arena.getBlocks());
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
            if (mat == Material.CAKE_BLOCK || mat == Material.WATER || mat == Material.LAVA)
                arena.removeBlock(b);
            else if (arena.removeBlock(b))
                arena.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(state.getTypeId(), 1));
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
        else if (monsters.removeMonster(event.getEntity())) {
            onMonsterDeath(event);
        }
        else if (monsters.removeMount(event.getEntity())) {
            onMountDeath(event);
        }
        else if (monsters.removeGolem(event.getEntity())) {
            Messenger.announce(arena, Msg.GOLEM_DIED);
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
                Messenger.announce(arena, event.getDeathMessage());
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
            damager = (Entity) ((Projectile) damager).getShooter();
        }
        else if (damager instanceof Wolf && arena.hasPet(damager)) {
            damager = (Player) ((Wolf) damager).getOwner();
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
                    ItemStack reward = boss.getReward();
                    if (reward != null) {
                        String msg = p.getName() + " killed the boss and won: ";
                        if (reward.getTypeId() == MobArena.ECONOMY_MONEY_ID) {
                            plugin.giveMoney(p, reward);
                            msg += plugin.economyFormat(reward);
                        } else {
                            arena.getRewardManager().addReward((Player) damager, reward);
                            msg += MAUtils.toCamelCase(reward.getType().toString()) + ":" + reward.getAmount();
                        }
                        for (Player q : arena.getPlayersInArena()) {
                            Messenger.tell(q, msg);
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
        if (!arena.isRunning() && !arena.getRegion().contains(damagee.getLocation())) {
            return;
        }

        EntityDamageByEntityEvent edbe = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damager = null;

        if (edbe != null) {
            damager = edbe.getDamager();

            if (damager instanceof Projectile) {
                damager = (Entity) ((Projectile) damager).getShooter();
            }

            // Repair weapons if necessary
            if (damager instanceof Player) {
                repairWeapon((Player) damager);
            } else if (damager instanceof TNTPrimed) {
                damager = getPlanter(damager);
            }
        }

        // Pet wolf
        if (damagee instanceof Wolf && arena.hasPet(damagee)) {
            onPetDamage(event, (Wolf) damagee, damager);
        }
        // Mount
        else if (damagee instanceof Horse && monsters.hasMount(damagee)) {
            onMountDamage(event, (Horse) damagee, damager);
        }
        // Player
        else if (damagee instanceof Player) {
            onPlayerDamage(event, (Player) damagee, damager);
        }
        // Snowmen melting
        else if (damagee instanceof Snowman && event.getCause() == DamageCause.MELTING) {
            event.setCancelled(true);
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
            // Repair armor if necessary
            repairArmor(player);

            // Cancel PvP damage if disabled
            if (!pvpEnabled && damager instanceof Player && !damager.equals(player)) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(false);
            arena.getArenaPlayer(player).getStats().add("dmgTaken", event.getDamage());
        }
    }

    private void onPetDamage(EntityDamageEvent event, Wolf pet, Entity damager) {
        event.setCancelled(true);
    }

    private void onMountDamage(EntityDamageEvent event, Horse mount, Entity damager) {
        event.setCancelled(true);
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
        else if (damager instanceof Wolf && arena.hasPet(damager)) {
            //event.setDamage(1);
            Player p = (Player) ((Wolf) damager).getOwner();
            ArenaPlayerStatistics aps = arena.getArenaPlayer(p).getStats();
            aps.add("dmgDone", event.getDamage());
        }
        //TODO add in check for player made golems doing damage
        else if (damager instanceof LivingEntity) {
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

    private static final EnumSet<Material> REPAIRABLE_TYPES = EnumSet.of(
            // Tools and swords
            Material.GOLD_AXE,    Material.GOLD_HOE,    Material.GOLD_PICKAXE,    Material.GOLD_SPADE,    Material.GOLD_SWORD,
            Material.WOOD_AXE,    Material.WOOD_HOE,    Material.WOOD_PICKAXE,    Material.WOOD_SPADE,    Material.WOOD_SWORD,
            Material.STONE_AXE,   Material.STONE_HOE,   Material.STONE_PICKAXE,   Material.STONE_SPADE,   Material.STONE_SWORD,
            Material.IRON_AXE,    Material.IRON_HOE,    Material.IRON_PICKAXE,    Material.IRON_SPADE,    Material.IRON_SWORD,
            Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_SWORD,
            // Armor
            Material.LEATHER_HELMET,   Material.LEATHER_CHESTPLATE,   Material.LEATHER_LEGGINGS,   Material.LEATHER_BOOTS,
            Material.GOLD_HELMET,      Material.GOLD_CHESTPLATE,      Material.GOLD_LEGGINGS,      Material.GOLD_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,      Material.IRON_CHESTPLATE,      Material.IRON_LEGGINGS,      Material.IRON_BOOTS,
            Material.DIAMOND_HELMET,   Material.DIAMOND_CHESTPLATE,   Material.DIAMOND_LEGGINGS,   Material.DIAMOND_BOOTS,
            // Misc
            Material.BOW, Material.FLINT_AND_STEEL, Material.FISHING_ROD, Material.SHEARS, Material.CARROT_STICK
    );

    private void repairWeapon(Player p) {
        ArenaPlayer ap = arena.getArenaPlayer(p);
        if (ap != null) {
            ArenaClass ac = ap.getArenaClass();
            if (ac != null && ac.hasUnbreakableWeapons()) {
                repair(p.getItemInHand());
            }
        }
    }

    private void repairArmor(Player p) {
        ArenaClass ac = arena.getArenaPlayer(p).getArenaClass();
        if (ac != null && ac.hasUnbreakableArmor()) {
            PlayerInventory inv = p.getInventory();
            repair(inv.getHelmet());
            repair(inv.getChestplate());
            repair(inv.getLeggings());
            repair(inv.getBoots());
        }
    }

    private void repair(ItemStack stack) {
        if (stack != null && REPAIRABLE_TYPES.contains(stack.getType())) {
            stack.setDurability((short) 0);
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

        if (arena.hasPet(event.getEntity())) {
            // Pets should never attack players
            if (event.getTarget() instanceof Player) {
                event.setCancelled(true);
            }
        }

        else if (monsters.getMonsters().contains(event.getEntity())) {
            // If the target is null, we probably forgot or the target died
            if (event.getTarget() == null) {
                event.setTarget(MAUtils.getClosestPlayer(plugin, event.getEntity(), arena));
            }

            // Pets are untargetable
            else if (arena.hasPet(event.getTarget())) {
                event.setCancelled(true);
            }

            // So are non-arena players
            else if (event.getTarget() instanceof Player && !arena.inArena((Player) event.getTarget())) {
                event.setCancelled(true);
            }
        }
    }
    
    public void onEntityTeleport(EntityTeleportEvent event) {
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
        if (!arena.isRunning())
            return;

        if (!(event.getEntity() instanceof Player) || !arena.inArena((Player) event.getEntity()))
            return;

        // If the food level is locked, cancel all changes.
        if (lockFoodLevel)
            event.setCancelled(true);
    }

    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (!arena.isRunning() || !arena.inArena(event.getPlayer()))
            return;

        arena.getArenaPlayer(event.getPlayer()).getStats().inc("swings");
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();

        // If the player is active in the arena, only cancel if sharing is not allowed
        if (arena.inArena(p)) {
            if (!canShare) {
                Messenger.tell(p, Msg.LOBBY_DROP_ITEM);
                event.setCancelled(true);
            }
        }
        
        // If the player is in the lobby, just cancel
        else if (arena.inLobby(p)) {
            Messenger.tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }
        
        // Same if it's a spectator, but...
        else if (arena.inSpec(p)) {
            Messenger.tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
            
            // If the spectator isn't in the region, force them to leave
            if (!region.contains(p.getLocation())) {
                Messenger.tell(p, Msg.MISC_MA_LEAVE_REMINDER);
                arena.playerLeave(p);
            }
        }

        /*
         * If the player is not in the arena in any way (as arena player, lobby
         * player or a spectator), but they -are- in the region, it must mean
         * they are trying to drop items when not allowed
         */
        else if (region.contains(p.getLocation())) {
            Messenger.tell(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }

        /*
         * If the player is in the banned set, it means they got kicked or
         * disconnected during a session, meaning they are more than likely
         * trying to steal items, if a PlayerDropItemEvent is fired.
         */
        else if (banned.contains(p)) {
            Messenger.warning("Player " + p.getName() + " tried to steal class items!");
            event.setCancelled(true);
        }
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!arena.getReadyPlayersInLobby().contains(event.getPlayer()) && !arena.inArena(event.getPlayer()))
            return;

        if (!arena.isRunning()) {
            event.getBlockClicked().getRelative(event.getBlockFace()).setTypeId(0);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getRelative(event.getBlockFace());
        arena.addBlock(liquid);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!arena.inLobby(p)) return;

        // Player is in the lobby, so disallow using items.
        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            event.setUseItemInHand(Result.DENY);
            event.setCancelled(true);
        }

        // If there's no block involved, just return.
        if (!event.hasBlock())
            return;

        // Iron block
        if (event.getClickedBlock().getTypeId() == 42) {
            handleReadyBlock(p);
        }
        // Sign
        else if (event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            handleSign(sign, p);
        }
    }

    private void handleReadyBlock(Player p) {
        if (arena.getArenaPlayer(p).getArenaClass() != null) {
            Messenger.tell(p, Msg.LOBBY_PLAYER_READY);
            arena.playerReady(p);
        }
        else {
            Messenger.tell(p, Msg.LOBBY_PICK_CLASS);
        }
    }

    private void handleSign(Sign sign, Player p) {
        // Check if the first line is a class name.
        String className = ChatColor.stripColor(sign.getLine(0)).toLowerCase();

        if (!arena.getClasses().containsKey(className) && !className.equals("random"))
            return;

        // Check for permission.
        if (!plugin.has(p, "mobarena.classes." + className) && !className.equals("random")) {
            Messenger.tell(p, Msg.LOBBY_CLASS_PERMISSION);
            return;
        }
        
        ArenaClass oldAC = arena.getArenaPlayer(p).getArenaClass();
        ArenaClass newAC = arena.getClasses().get(className);
        
        // Same class, do nothing.
        if (newAC.equals(oldAC)) {
            return;
        }
        
        // If the new class is full, inform the player.
        if (!classLimits.canPlayerJoinClass(newAC)) {
            Messenger.tell(p, Msg.LOBBY_CLASS_FULL);
            return;
        }

        // Check price, balance, and inform
        double price = newAC.getPrice();
        if (price > 0D) {
            if (!plugin.hasEnough(p, price)) {
                Messenger.tell(p, Msg.LOBBY_CLASS_TOO_EXPENSIVE, plugin.economyFormat(price));
                return;
            }
        }
        
        // Otherwise, leave the old class, and pick the new!
        classLimits.playerLeftClass(oldAC, p);
        classLimits.playerPickedClass(newAC, p);

        // Delay the inventory stuff to ensure that right-clicking works.
        delayAssignClass(p, className, price, sign);
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

    private void delayAssignClass(final Player p, final String className, final double price, final Sign sign) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable() {
            public void run() {
                if (!className.equalsIgnoreCase("random")) {
                    if (useClassChests) {
                        // Check for stored class chests first
                        ArenaClass ac = plugin.getArenaMaster().getClasses().get(className.toLowerCase());
                        Location loc = ac.getClassChest();
                        Block blockChest;
                        if (loc != null) {
                            blockChest = loc.getBlock();
                        } else {
                            // Otherwise, start the search
                            BlockFace backwards = ((org.bukkit.material.Sign) sign.getData()).getFacing().getOppositeFace();
                            Block blockSign   = sign.getBlock();
                            Block blockBelow  = blockSign.getRelative(BlockFace.DOWN);
                            Block blockBehind = blockBelow.getRelative(backwards);

                            // If the block below this sign is a class sign, swap the order
                            if (blockBelow.getType() == Material.WALL_SIGN || blockBelow.getType() == Material.SIGN_POST) {
                                String className = ChatColor.stripColor(((Sign) blockBelow.getState()).getLine(0)).toLowerCase();
                                if (arena.getClasses().containsKey(className)) {
                                    blockSign = blockBehind;  // Use blockSign as a temp while swapping
                                    blockBehind = blockBelow;
                                    blockBelow = blockSign;
                                }
                            }

                            // TODO: Make number of searches configurable
                            // First check the pillar below the sign
                            blockChest = findChestBelow(blockBelow, 6);

                            // Then, if no chest was found, check the pillar behind the sign
                            if (blockChest == null) blockChest = findChestBelow(blockBehind, 6);
                        }
                        
                        // If a chest was found, get the contents
                        if (blockChest != null) {
                            InventoryHolder holder = (InventoryHolder) blockChest.getState();
                            ItemStack[] contents = holder.getInventory().getContents();
                            // Guard against double-chests for now
                            if (contents.length > 36) {
                                ItemStack[] newContents = new ItemStack[36];
                                System.arraycopy(contents, 0, newContents, 0, 36);
                                contents = newContents;
                            }
                            arena.assignClassGiveInv(p, className, contents);
                            p.getInventory().setContents(contents);
                            Messenger.tell(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(className));
                            if (price > 0D) {
                                Messenger.tell(p, Msg.LOBBY_CLASS_PRICE,  plugin.economyFormat(price));
                            }
                            return;
                        }
                        // Otherwise just fall through and use the items from the config-file
                    }
                    arena.assignClass(p, className);
                    Messenger.tell(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(className));
                    if (price > 0D) {
                        Messenger.tell(p, Msg.LOBBY_CLASS_PRICE,  plugin.economyFormat(price));
                    }
                }
                else {
                    arena.addRandomPlayer(p);
                    Messenger.tell(p, Msg.LOBBY_CLASS_RANDOM);
                }
            }
        });
    }
    
    private Block findChestBelow(Block b, int left) {
        if (left < 0) return null;
        
        if (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST) {
            return b;
        }
        return findChestBelow(b.getRelative(BlockFace.DOWN), left - 1);
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

        Location to = event.getTo();
        Location from = event.getFrom();
        Player p = event.getPlayer();

        if (region.contains(from)) {
            // Players with proper admin permission can warp out
            if (p.hasPermission("mobarena.admin.teleport")) {
                return TeleportResponse.ALLOW;
            }
            
            // Players not in the arena are free to warp out.
            if (!arena.inArena(p) && !arena.inLobby(p) && !arena.inSpec(p)) {
                return TeleportResponse.ALLOW;
            }

            // Covers the case in which both locations are in the arena.
            if (region.contains(to) || region.isWarp(to) || to.equals(arena.getPlayerEntry(p))) {
                return TeleportResponse.ALLOW;
            }

            Messenger.tell(p, Msg.WARP_FROM_ARENA);
            return TeleportResponse.REJECT;
        }
        else if (region.contains(to)) {
            // Players with proper admin permission can warp in
            if (p.hasPermission("mobarena.admin.teleport")) {
                return TeleportResponse.ALLOW;
            }
            
            if (region.isWarp(from) || region.isWarp(to) || to.equals(arena.getPlayerEntry(p))) {
                return TeleportResponse.ALLOW;
            }

            Messenger.tell(p, Msg.WARP_TO_ARENA);
            return TeleportResponse.REJECT;
        }

        return TeleportResponse.IDGAF;
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();

        if (event.isCancelled() || (!arena.inArena(p) && !arena.inSpec(p) && !arena.inLobby(p))) {
            return;
        }

        // This is safe, because commands will always have at least one element.
        String base = event.getMessage().split(" ")[0];

        // Check if the entire base command is allowed.
        if (plugin.getArenaMaster().isAllowed(base)) {
            return;
        }

        // If not, check if the specific command is allowed.
        String noslash = event.getMessage().substring(1);
        if (plugin.getArenaMaster().isAllowed(noslash)) {
            return;
        }

        // This is dirty, but it ensures that commands are indeed blocked.
        event.setMessage("/");

        // Cancel the event regardless.
        event.setCancelled(true);
        Messenger.tell(p, Msg.MISC_COMMAND_NOT_ALLOWED);
    }

    public void onPlayerPreLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (p == null || !p.isOnline()) return;
        
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(p);
        if (arena == null) return;
        
        arena.playerLeave(p);
    }

    public void onVehicleExit(VehicleExitEvent event) {
        LivingEntity entity = event.getExited();
        if (!(entity instanceof Player)) return;

        Player p = (Player) entity;
        if (!arena.inArena(p)) return;

        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Horse)) return;

        if (monsters.hasMount(vehicle)) {
            event.setCancelled(true);
        }
    }
}
