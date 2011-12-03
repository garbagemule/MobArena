package com.garbagemule.MobArena;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EndermanPickupEvent;
import org.bukkit.event.entity.EndermanPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.Redstone;

import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.leaderboards.Leaderboard;
import com.garbagemule.MobArena.repairable.*;

public class MAListener implements ArenaListener
{
    private MobArena plugin;
    private Arena arena;
    
    public MAListener(Arena arena, MobArena plugin)
    {
        this.arena = arena;
        this.plugin = plugin;
    }
    
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (onBlockDestroy(event))
            return;
        
        event.setCancelled(true);
    }
    
    public void onBlockBurn(BlockBurnEvent event)
    {
        if (onBlockDestroy(event))
            return;
        
        event.setCancelled(true);
    }
    
    private boolean onBlockDestroy(BlockEvent event)
    {
        if (!arena.inRegion(event.getBlock().getLocation()) || arena.edit || (!arena.protect && arena.running))
            return true;
        
        Block b = event.getBlock();
        if (arena.blocks.remove(b) || b.getType() == Material.TNT)
            return true;
        
        if (arena.softRestore && arena.running)
        {
            BlockState state = b.getState();

            Repairable r = null;
            if (state instanceof ContainerBlock)
                r = new RepairableContainer(state);
            else if (state instanceof Sign)
                r = new RepairableSign(state);
            else if (state.getData() instanceof Attachable)
                r = new RepairableAttachable(state);
            else
                r = new RepairableBlock(state);
            
            arena.repairables.add(r);
            
            if (!arena.softRestoreDrops)
                b.setTypeId(0);
            
            return true;
        }
        
        return false;
    }
    
    public void onBlockPlace(BlockPlaceEvent event)
    {
        Block b = event.getBlock();
        
        if (!arena.inRegion(b.getLocation()) || arena.edit)
            return;
        
        if (arena.running && arena.inArena(event.getPlayer()))
        {
            arena.blocks.add(b);
            Material mat = b.getType();
            
            if (mat == Material.WOODEN_DOOR || mat == Material.IRON_DOOR_BLOCK)
                arena.blocks.add(b.getRelative(0,1,0));
            
            return;
        }

        // If the arena isn't running, or if the player isn't in the arena, cancel.
        event.setCancelled(true);
    }
    
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        if (!arena.inRegion(event.getBlock().getLocation()))
            return;
        
        switch (event.getCause())
        {
            case LIGHTNING:
                event.setCancelled(true);
                break;
            case SPREAD:
                event.setCancelled(true);
                break;
            case FLINT_AND_STEEL:
                if (arena.running)
                    arena.blocks.add(event.getBlock());
                else
                    event.setCancelled(true);
                break;
            default:
                break;
        }
    }
    
    public void onSignChange(SignChangeEvent event)
    {
        arena.leaderboard = new Leaderboard(plugin, arena, event.getBlock().getLocation());
        MAUtils.setArenaCoord(plugin.getMAConfig(), arena, "leaderboard", event.getBlock().getLocation());
        MAUtils.tellPlayer(event.getPlayer(), "Leaderboard made. Now set up the stat signs!");
    }

    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!arena.inRegion(event.getLocation())) // || event.getSpawnReason() == SpawnReason.CUSTOM)
            return;
        
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (arena.running && entity instanceof Slime)
            arena.monsters.add(entity);
        
        // If running == true, setCancelled(false), and vice versa.
        event.setCancelled(!arena.running);
    }
    
    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!arena.monsters.contains(event.getEntity()) && !arena.inRegionRadius(event.getLocation(), 10))
            return;
        
        arena.monsters.remove(event.getEntity());
        
        // Cancel if the arena isn't running
        if (!arena.running)
        {
            event.setCancelled(true);
            return;
        }
        
        // Uncancel, just in case.
        event.setCancelled(false);
        
        // If the arena isn't destructible, just clear the blocklist.
        if (!arena.softRestore && arena.protect)
        {
            List<Block> blocks = new LinkedList<Block>(arena.blocks);
            event.blockList().retainAll(blocks);
            return;
        }
        
        if (!arena.softRestoreDrops)
            event.setYield(0);
        
        // Handle all the blocks in the block list.
        for (Block b : event.blockList())
        {
            BlockState state = b.getState();
            
            if (state.getData() instanceof Door && ((Door) state.getData()).isTopHalf())
                state = b.getRelative(BlockFace.DOWN).getState();
            else if (state.getData() instanceof Bed && ((Bed) state.getData()).isHeadOfBed())
                state = b.getRelative(((Bed) state.getData()).getFacing().getOppositeFace()).getState();
            
            // Create a Repairable from the block.
            Repairable r = null;
            if (state instanceof ContainerBlock)
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
                arena.blocks.remove(b);
            else if (arena.blocks.remove(b))
                arena.world.dropItemNaturally(b.getLocation(), new ItemStack(state.getTypeId(), 1));
            else if (arena.softRestore)
                arena.repairables.add(r);
            else
                arena.queueRepairable(r);
        }
    }

    
    /******************************************************
     * 
     *              DEATH LISTENERS
     *  
     ******************************************************/
    
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Player)
            onPlayerDeath(event, (Player) event.getEntity());
        
        else if (arena.monsters.remove(event.getEntity()))
            onMonsterDeath(event);
    }
    
    private void onPlayerDeath(EntityDeathEvent event, Player player)
    {
        if (arena.inArena(player) || arena.lobbyPlayers.contains(player))
        {
            event.getDrops().clear();
            arena.playerDeath(player);
        }
    }
    
    private void onMonsterDeath(EntityDeathEvent event)
    {
        EntityDamageEvent e1 = event.getEntity().getLastDamageCause();
        EntityDamageByEntityEvent e2 = (e1 instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) e1 : null;
        Entity damager = (e2 != null) ? e2.getDamager() : null;
        
        if (damager instanceof Player)
            arena.playerKill((Player) damager);
        
        event.getDrops().clear();            
        arena.resetIdleTimer();
        return;
    }

    
    /******************************************************
     * 
     *              DAMAGE LISTENERS
     *  
     ******************************************************/

    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!arena.running) return;
        
        EntityDamageByEntityEvent e = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damagee = event.getEntity();
        Entity damager = null;
        if (e != null)
        {
            damager = e.getDamager();
            if (damager instanceof Projectile)
                damager = ((Projectile) damager).getShooter();
        }
        
        event.setCancelled(false);
        
        // Pet wolf
        if (damagee instanceof Wolf && arena.pets.contains(damagee))
            onPetDamage(event, (Wolf) damagee, damager);
        
        // Player
        else if (damagee instanceof Player)
            onPlayerDamage(event, (Player) damagee, damager);
        
        // Monster
        else if (arena.monsters.contains(damagee))
            onMonsterDamage(event, damagee, damager);
    }
    
    private void onPlayerDamage(EntityDamageEvent event, Player player, Entity damager)
    {
        // Cancel all damage in the lobby
        if (arena.lobbyPlayers.contains(player))
            event.setCancelled(true);
        
        // If not in the lobby or the arena, return
        else if (!arena.inArena(player))
            return;
        
        // Cancel block explosion damage if detonate-damage: false
        else if (!arena.detDamage && event.getCause() == DamageCause.BLOCK_EXPLOSION)
            event.setCancelled(true);
        
        // If PvP is disabled and damager is a player, cancel damage
        else if (!arena.pvp && damager instanceof Player)
            event.setCancelled(true);
        
        // Log damage
        if (!event.isCancelled())
            arena.arenaPlayerMap.get(player).getStats().dmgTaken += event.getDamage();
    }
    
    private void onPetDamage(EntityDamageEvent event, Wolf pet, Entity damager)
    {
        if (arena.hellhounds && (damager == null || damager instanceof Player))
            pet.setFireTicks(32768);
        
        event.setCancelled(true);
    }
    
    private void onMonsterDamage(EntityDamageEvent event, Entity monster, Entity damager)
    {
        if (damager instanceof Player)
        {
            Player p = (Player) damager;
            if (!arena.inArena(p))
            {
                event.setCancelled(true);
                return;
            }
            
            arena.arenaPlayerMap.get(p).getStats().dmgDone += event.getDamage();
            arena.arenaPlayerMap.get(p).getStats().hits++;
        }
        else if (damager instanceof Wolf && arena.pets.contains(damager))
        {                
            event.setDamage(1);
            arena.arenaPlayerMap.get((Player) ((Wolf) damager).getOwner()).getStats().dmgDone += event.getDamage();
        }
        else if (damager instanceof LivingEntity)
        {
            if (!arena.monsterInfight)
                event.setCancelled(true);
        }
        
        // Boss
        if (arena.bossWave != null && monster.equals(arena.bossWave.getEntity()))
        {
            if (event.getCause() == DamageCause.LIGHTNING)
            {
                event.setCancelled(true);
                return;
            }
            
            // Subtract boss health, and reset actual entity health
            arena.bossWave.subtractHealth(event.getDamage());
            arena.bossWave.getEntity().setHealth(100);
            
            // Set damage to 1 for knockback and feedback
            event.setDamage(1);
            
            // If the boss is dead, remove the entity and create an explosion!
            if (arena.bossWave.getHealth() <= 0)
            {
                arena.bossWave.clear();
                arena.bossWave = null;
            }
            else if (arena.bossWave.getHealth() <= 100 && !arena.bossWave.isLowHealthAnnounced())
            {
                MAUtils.tellAll(arena, Msg.WAVE_BOSS_LOW_HEALTH);
                arena.bossWave.setLowHealthAnnounced(true);
            }
        }
    }

    public void onEntityCombust(EntityCombustEvent event)
    {
        if (arena.monsters.contains(event.getEntity()))
            event.setCancelled(true);
    }

    public void onEntityTarget(EntityTargetEvent event)
    {
        if (!arena.running || event.isCancelled())
            return;
        
        if (arena.pets.contains(event.getEntity()))
        {
            if (event.getReason() != TargetReason.TARGET_ATTACKED_OWNER && event.getReason() != TargetReason.OWNER_ATTACKED_TARGET)
                return;
            
            if (!(event.getTarget() instanceof Player))
                return;
            
            // If the target is a player, cancel.
            event.setCancelled(true);
        }
        
        else if (arena.monsters.contains(event.getEntity()))
        {
            if (event.getReason() == TargetReason.FORGOT_TARGET)
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), arena));
                
            else if (event.getReason() == TargetReason.TARGET_DIED)
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), arena));
            
            else if (event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY)
                if (arena.pets.contains(event.getTarget()))
                    event.setCancelled(true);
            
            else if (event.getReason() == TargetReason.CLOSEST_PLAYER)
                if (!arena.inArena((Player) event.getTarget()))
                    event.setCancelled(true);
        }
    }
    
    public void onEndermanPickup(EndermanPickupEvent event)
    {
        if (arena.inRegion(event.getBlock().getLocation()))
            event.setCancelled(true);
    }
    
    public void onEndermanPlace(EndermanPlaceEvent event)
    {
        if (arena.inRegion(event.getLocation()))
            event.setCancelled(true);
    }

    public void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        if (!arena.running) return;
        
        if (!(event.getEntity() instanceof Player) || !arena.inArena((Player)event.getEntity()))
            return;
        
        if (event.getRegainReason() == RegainReason.REGEN)
            event.setCancelled(true);
    }
    
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        if (!arena.running || !arena.inArena(event.getPlayer()))
            return;
        
        arena.arenaPlayerMap.get(event.getPlayer()).getStats().swings++;
    }

    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Player p = event.getPlayer();
        
        // Player is in the lobby
        if (arena.inLobby(p))
        {
            MAUtils.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }
        
        // Player is in the arena
        else if (arena.inArena(p))
        {
            if (!arena.shareInArena)
            {
                MAUtils.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
                event.setCancelled(true);
            }
        }
        
        // Player died/left
        else if (p.getLocation().equals(arena.spectatorLoc) || p.getLocation().equals(arena.locations.get(p)))
        {
            MobArena.warning("Player '" + p.getName() + "' tried to steal item " + event.getItemDrop().getItemStack().getType());
            event.getItemDrop().remove();
        }
        
        // Player is in the spectator area
        else if (arena.specPlayers.contains(p))
        {
            MAUtils.tellPlayer(p, Msg.LOBBY_DROP_ITEM);
            event.setCancelled(true);
        }
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!arena.readyPlayers.contains(event.getPlayer()) && !arena.inArena(event.getPlayer()))
            return;
        
        if (!arena.running)
        {
            event.getBlockClicked().getRelative(event.getBlockFace()).setTypeId(0);
            event.setCancelled(true);
            return;
        }

        Block liquid = event.getBlockClicked().getRelative(event.getBlockFace());
        arena.blocks.add(liquid);
    }

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player p = event.getPlayer();
        if (arena.inArena(p) || !arena.inLobby(p))
            return;
        
        // Player is in the lobby, so disallow using items.
        Action a = event.getAction();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
        {
            event.setUseItemInHand(Result.DENY);
            event.setCancelled(true);
        }
        
        // If there's no block involved, just return.
        if (!event.hasBlock())
            return;
        
        // Iron block
        if (event.getClickedBlock().getTypeId() == 42)
            handleReadyBlock(p);
        
        // Sign
        else if (event.getClickedBlock().getState() instanceof Sign)
        {
            Sign sign = (Sign) event.getClickedBlock().getState();
            handleSign(sign, p);
        }
    }
    
    private void handleReadyBlock(Player p)
    {
        if (arena.classMap.containsKey(p))
        {
            MAUtils.tellPlayer(p, Msg.LOBBY_PLAYER_READY);
            arena.playerReady(p);
        }
        else MAUtils.tellPlayer(p, Msg.LOBBY_PICK_CLASS);
    }
    
    private void handleSign(Sign sign, Player p)
    {
        // Check if the first line is a class name.
        String className = ChatColor.stripColor(sign.getLine(0));
        if (!arena.classes.contains(className) && !className.equalsIgnoreCase("random"))
            return;

        // Check for permission.
        if (!plugin.has(p, "mobarena.classes." + className) && !className.equalsIgnoreCase("random"))
        {
            MAUtils.tellPlayer(p, Msg.LOBBY_CLASS_PERMISSION);
            return;
        }
        
        // Delay the inventory stuff to ensure that right-clicking works.
        delayAssignClass(p, className);
    }
    
    private void delayAssignClass(final Player p, final String className)
    {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    arena.assignClass(p, className);
                    
                    if (!className.equalsIgnoreCase("random"))
                        MAUtils.tellPlayer(p, Msg.LOBBY_CLASS_PICKED, className, arena.classItems.get(className).get(0).getType());
                    else
                        MAUtils.tellPlayer(p, Msg.LOBBY_CLASS_RANDOM);
                }
            });
    }

    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        if (!arena.enabled || (!arena.inArena(p) && !arena.inLobby(p)))
            return;
        
        arena.playerLeave(p);
    }

    public void onPlayerKick(PlayerKickEvent event)
    {
        Player p = event.getPlayer();
        if (!arena.enabled || (!arena.inArena(p) && !arena.inLobby(p)))
            return;
        
        arena.playerLeave(p);
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (!arena.enabled || !arena.setup || arena.allowWarp || arena.edit)
            return;

        Location to   = event.getTo();
        Location from = event.getFrom();
        Player   p    = event.getPlayer();
        
        if (arena.inRegion(from))
        {
            // Covers the case in which both locations are in the arena.
            if ((arena.inRegion(to) && arena.running) || isWarp(to) || to.equals(arena.locations.get(p)))
                return;

            MAUtils.tellPlayer(p, Msg.WARP_FROM_ARENA);
            event.setCancelled(true);
        }
        else if (arena.inRegion(to))
        {
            if (isWarp(from) || isWarp(to) || to.equals(arena.locations.get(p)))
                return;
            
            MAUtils.tellPlayer(p, Msg.WARP_TO_ARENA);
            event.setCancelled(true);
        }
    }
    
    private boolean isWarp(Location l)
    {
        return l.equals(arena.arenaLoc) || l.equals(arena.lobbyLoc) || l.equals(arena.spectatorLoc);
    }

    /*public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (!arena.running || arena.edit || !arena.enabled || !arena.setup || arena.allowWarp)
            return;

        Location to   = event.getTo();
        Location from = event.getFrom();
        
        if (!arena.inRegion(to) && !arena.inRegion(from))
            return;

        Player   p   = event.getPlayer();
        Location old = arena.locations.get(p);
        
        if (arena.inArena(p) || arena.inLobby(p) || arena.specPlayers.contains(p))
        {
            if (arena.inRegion(from))
            {
                if (arena.inRegion(to) || to.equals(arena.arenaLoc) || to.equals(arena.lobbyLoc) || to.equals(arena.spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, Msg.WARP_FROM_ARENA);
                event.setCancelled(true);
                return;
            }
            
            if (arena.inRegion(to))
            {
                if (to.equals(arena.arenaLoc) || to.equals(arena.lobbyLoc) || to.equals(arena.spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, Msg.WARP_TO_ARENA);
                event.setCancelled(true);
                return;
            }
            
            return;
        }
        
        if (arena.running && arena.inRegion(to))
        {
            event.setCancelled(true);
            return;
        }
    }*/

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();
        
        if (!arena.inArena(p) && !arena.inLobby(p))
            return;
        
        String[] args = event.getMessage().split(" ");
        
        if ((args.length > 1 && MACommands.COMMANDS.contains(args[1].trim())) ||
            MACommands.ALLOWED_COMMANDS.contains(event.getMessage().substring(1).trim()) ||
            MACommands.ALLOWED_COMMANDS.contains(args[0]))
            return;
        
        event.setCancelled(true);
        MAUtils.tellPlayer(p, Msg.MISC_COMMAND_NOT_ALLOWED);
    }
}
