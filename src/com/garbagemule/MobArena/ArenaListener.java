package com.garbagemule.MobArena;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.Redstone;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.listeners.MAGlobalListener.TeleportResponse;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.region.RegionPoint;
import com.garbagemule.MobArena.repairable.*;
import com.garbagemule.MobArena.util.TextUtils;
import com.garbagemule.MobArena.util.config.ConfigSection;
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
            lockFoodLevel;
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
        ConfigSection s = arena.getSettings();
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

    public void onBlockBurn(BlockBurnEvent event) {
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
            event.setCancelled(true);
            return;
        }
        
        // If the block is TNT, replace with a TNTPrimed
        if (autoIgniteTNT && b.getType() == Material.TNT) {
            event.setCancelled(true);
            event.getPlayer().getInventory().removeItem(new ItemStack(Material.TNT, 1));
            b.getWorld().spawn(b.getRelative(BlockFace.UP).getLocation(), TNTPrimed.class);
            return;
        }

        // Otherwise, block was placed during a session.
        arena.addBlock(b);

        if (b.getType() == Material.WOODEN_DOOR || b.getType() == Material.IRON_DOOR_BLOCK) {
            // For doors, add the block just above (so we get both halves)
            arena.addBlock(b.getRelative(0, 1, 0));
        }
    }

    public void onBlockForm(BlockFormEvent event) {
        if (!arena.getRegion().contains(event.getBlock().getLocation()))
            return;

        // If a snowman forms some snow on its path, add the block
        if (event.getNewState().getType() == Material.SNOW)
            arena.addBlock(event.getBlock());
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        if (!arena.getRegion().contains(event.getBlock().getLocation()))
            return;

        switch (event.getCause()){
            case LIGHTNING:
            case SPREAD:
                event.setCancelled(true);
                break;
            case FLINT_AND_STEEL:
                if (arena.isRunning())
                    arena.addBlock(event.getBlock().getRelative(BlockFace.UP));
                else
                    event.setCancelled(true);
                break;
            default:
                break;
        }
    }

    public void onSignChange(SignChangeEvent event) {
        arena.setLeaderboard(new Leaderboard(plugin, arena, event.getBlock().getLocation()));
        arena.getRegion().set(RegionPoint.LEADERBOARD, event.getBlock().getLocation());

        Messenger.tellPlayer(event.getPlayer(), "Leaderboard made. Now set up the stat signs!");
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

        monsters.removeMonster(event.getEntity());

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
        else if (monsters.removeGolem(event.getEntity())) {
            Messenger.tellAll(arena, Msg.GOLEM_DIED);
        }
    }

    private void onPlayerDeath(PlayerDeathEvent event, Player player) {
        if (arena.inArena(player) || arena.inLobby(player)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            event.setKeepLevel(true);
            arena.playerDeath(player);
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

    private void onMonsterDeath(EntityDeathEvent event) {
        EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
        EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
        Entity damager = (e2 != null) ? e2.getDamager() : null;

        // Make sure to grab the owner of a projectile/pet
        if (damager instanceof Projectile) {
            damager = ((Projectile) damager).getShooter();
        }
        else if (damager instanceof Wolf && arena.hasPet(damager)) {
            damager = (Player) ((Wolf) damager).getOwner();
        }

        // If the damager was a player, add to kills.
        if (damager instanceof Player) {
            ArenaPlayer ap = arena.getArenaPlayer((Player) damager);
            if (ap != null) {
                ArenaPlayerStatistics stats = ap.getStats();
                if (stats != null) {
                    ap.getStats().inc("kills");
                }
            }
        }
        
        MABoss boss = monsters.removeBoss(event.getEntity());
        if (boss != null) {
            boss.setDead(true);
        }

        if (!monsterExp) {
            event.setDroppedExp(0);
        }

        event.getDrops().clear();

        List<ItemStack> loot = monsters.getLoot(event.getEntity());
        if (loot != null && !loot.isEmpty()) {
            event.getDrops().add(getRandomItem(loot));
        }

        return;
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
        if (!arena.isRunning() || !arena.getRegion().contains(damagee.getLocation())) {
            return;
        }

        EntityDamageByEntityEvent edbe = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damager = null;

        if (edbe != null) {
            damager = edbe.getDamager();

            if (damager instanceof Projectile) {
                damager = ((Projectile) damager).getShooter();
            }
        }

        // Pet wolf
        if (damagee instanceof Wolf && arena.hasPet(damagee)) {
            onPetDamage(event, (Wolf) damagee, damager);
        }
        // Player
        else if (damagee instanceof Player) {
            onPlayerDamage(event, (Player) damagee, damager);
        }
        // Snowmen melting
        else if (damagee instanceof Snowman && event.getCause() == DamageCause.MELTING) {
            event.setCancelled(true);
        }
        // Boss
        else if (monsters.getBossMonsters().contains(damagee)) {
            onBossDamage(event, (LivingEntity) damagee, damager); // Now an emtpy method
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
        // If PvP is disabled and damager is a player, cancel damage
        else if (arena.inArena(player)) {
            if (!pvpEnabled && (damager instanceof Player || damager instanceof Wolf)) {
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
                return;
            }
        }
    }

    private void onBossDamage(EntityDamageEvent event, LivingEntity monster, Entity damager) {
        //TODO useless method as of Entity Max Health API, maybe add in some stat tracking for leaderboards instead?
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
            if (event.getReason() != TargetReason.TARGET_ATTACKED_OWNER && event.getReason() != TargetReason.OWNER_ATTACKED_TARGET)
                return;

            if (!(event.getTarget() instanceof Player))
                return;

            // If the target is a player, cancel.
            event.setCancelled(true);
        }

        else if (monsters.getMonsters().contains(event.getEntity())) {
            if (event.getReason() == TargetReason.FORGOT_TARGET) {
                event.setTarget(MAUtils.getClosestPlayer(plugin, event.getEntity(), arena));
            }
            else if (event.getReason() == TargetReason.TARGET_DIED) {
                event.setTarget(MAUtils.getClosestPlayer(plugin, event.getEntity(), arena));
            }
            else if (event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY) {
                if (arena.hasPet(event.getTarget())) {
                    event.setCancelled(true);
                }
            }
            else if (event.getReason() == TargetReason.CLOSEST_PLAYER) {
                if (!arena.inArena((Player) event.getTarget())) {
                    event.setCancelled(true);
                }
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
        if (!region.contains(potion.getLocation()) || pvpEnabled) {
            return;
        }

        // If a potion has harmful effects, remove all players.
        for (PotionEffect effect : potion.getEffects()) {
            PotionEffectType type = effect.getType();
            if (type.equals(PotionEffectType.HARM) || type.equals(PotionEffectType.POISON)) {
                Set<LivingEntity> players = new HashSet<LivingEntity>();
                for (LivingEntity le : event.getAffectedEntities()) {
                    if (le instanceof Player) {
                        players.add(le);
                    }
                }
                event.getAffectedEntities().removeAll(players);
                break;
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
                Messenger.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
                event.setCancelled(true);
            }
        }
        
        // If the player is in the lobby, just cancel
        else if (arena.inLobby(p)) {
            Messenger.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }
        
        // Same if it's a spectator, but...
        else if (arena.inSpec(p)) {
            Messenger.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
            
            // If the spectator isn't in the region, force them to leave
            if (!region.contains(p.getLocation())) {
                Messenger.tellPlayer(p, Msg.MISC_MA_LEAVE_REMINDER);
                arena.playerLeave(p);
            }
        }

        /*
         * If the player is not in the arena in any way (as arena player, lobby
         * player or a spectator), but they -are- in the region, it must mean
         * they are trying to drop items when not allowed
         */
        else if (region.contains(p.getLocation())) {
            Messenger.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
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
        if (arena.inArena(p) || !arena.inLobby(p))
            return;

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
            Messenger.tellPlayer(p, Msg.LOBBY_PLAYER_READY);
            arena.playerReady(p);
        }
        else {
            Messenger.tellPlayer(p, Msg.LOBBY_PICK_CLASS);
        }
    }

    private void handleSign(Sign sign, Player p) {
        // Check if the first line is a class name.
        String className = ChatColor.stripColor(sign.getLine(0)).toLowerCase();

        if (!arena.getClasses().containsKey(className) && !className.equals("random"))
            return;

        // Check for permission.
        if (!plugin.has(p, "mobarena.classes." + className) && !className.equals("random")) {
            Messenger.tellPlayer(p, Msg.LOBBY_CLASS_PERMISSION);
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
            Messenger.tellPlayer(p, Msg.LOBBY_CLASS_FULL);
            return;
        }
        
        // Otherwise, leave the old class, and pick the new!
        classLimits.playerLeftClass(oldAC);
        classLimits.playerPickedClass(newAC);

        // Delay the inventory stuff to ensure that right-clicking works.
        delayAssignClass(p, className);
    }
    
    /*private boolean cansPlayerJoinClass(ArenaClass ac, Player p) {
        // If they can not join the class, deny them
        if (!classLimits.canPlayerJoinClass(ac)) {
            Messenger.tellPlayer(p, Msg.LOBBY_CLASS_FULL);
            return false;
        }
        
        // Increment the "in use" in the Class Limit Manager
        classLimits.playerPickedClass(ac);
        return true;
    }*/

    private void delayAssignClass(final Player p, final String className) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable() {
            public void run() {
                if (!className.equalsIgnoreCase("random")) {
                    arena.assignClass(p, className);
                    Messenger.tellPlayer(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(className), arena.getClassLogo(className));
                }
                else {
                    arena.addRandomPlayer(p);
                    Messenger.tellPlayer(p, Msg.LOBBY_CLASS_RANDOM);
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

            Messenger.tellPlayer(p, Msg.WARP_FROM_ARENA);
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

            Messenger.tellPlayer(p, Msg.WARP_TO_ARENA);
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
        Messenger.tellPlayer(p, Msg.MISC_COMMAND_NOT_ALLOWED);
    }

    public void onPlayerPreLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (p == null || !p.isOnline()) return;
        
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(p);
        if (arena == null) return;
        
        arena.playerLeave(p);
    }
}
