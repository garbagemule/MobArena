package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
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

import com.garbagemule.MobArena.MAMessages.Msg;

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
        if (!arena.inRegion(event.getBlock().getLocation()) || arena.edit || (!arena.protect && arena.running))
            return;
        
        Block b = event.getBlock();
        if (arena.blocks.remove(b) || b.getType() == Material.TNT)
            return;
        
        if (arena.softRestore && arena.running)
        {
            int[] buffer = new int[5];
            buffer[0] = b.getX();
            buffer[1] = b.getY();
            buffer[2] = b.getZ();
            buffer[3] = b.getTypeId();
            buffer[4] = (int) b.getData();
            arena.repairList.add(buffer);
            if (!arena.softRestoreDrops) event.getBlock().setTypeId(0);
            return;
        }
        
        event.setCancelled(true);
    }

    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!arena.inRegion(event.getBlock().getLocation()) || arena.edit)
            return;
        
        Block b = event.getBlock();
        if (arena.running && arena.arenaPlayers.contains(event.getPlayer()))
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

    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        if (!arena.inRegion(event.getLocation()) || event.getSpawnReason() == SpawnReason.CUSTOM)
            return;
        
        // If running == true, setCancelled(false), and vice versa.
        event.setCancelled(!arena.running);
    }

    public void onEntityExplode(EntityExplodeEvent event)
    {
        if (!arena.monsters.contains(event.getEntity()) && !arena.inRegionRadius(event.getLocation(), 10))
            return;
        
        event.setYield(0);
        arena.monsters.remove(event.getEntity());
        
        // If the arena isn't running
        if (!arena.running || arena.repairDelay == 0)
        {
            event.setCancelled(true);
            return;
        }
        
        // If there is a sign in the blocklist, cancel
        for (Block b : event.blockList())
        {
            if (!(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN))
                continue;
            
            event.setCancelled(true);
            return;
        }

        // Uncancel, just in case.
        event.setCancelled(false);

        int[] buffer;
        final HashMap<Block,Integer> blockMap = new HashMap<Block,Integer>();
        for (Block b : event.blockList())
        {
            Material mat = b.getType();
            
            if (mat == Material.LAVA)       b.setType(Material.STATIONARY_LAVA);
            else if (mat == Material.WATER) b.setType(Material.STATIONARY_WATER);
            
            if (mat == Material.WOODEN_DOOR || mat == Material.IRON_DOOR_BLOCK || mat == Material.FIRE || mat == Material.CAKE_BLOCK || mat == Material.WATER || mat == Material.LAVA)
            {
                arena.blocks.remove(b);
            }
            else if (arena.blocks.remove(b))
            {
                arena.world.dropItemNaturally(b.getLocation(), new ItemStack(b.getTypeId(), 1));
            }
            else if (arena.softRestore)
            {
                buffer = new int[5];
                buffer[0] = b.getX();
                buffer[1] = b.getY();
                buffer[2] = b.getZ();
                buffer[3] = b.getTypeId();
                buffer[4] = (int) b.getData();
                arena.repairList.add(buffer);
                blockMap.put(b, b.getTypeId() + (b.getData() * 1000));
            }
            else
            {
                blockMap.put(b, b.getTypeId() + (b.getData() * 1000));
            }
        }
        
        if (!arena.protect || arena.softRestore)
            return;
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
            new Runnable()
            {
                public void run()
                {
                    for (Map.Entry<Block,Integer> entry : blockMap.entrySet())
                    {
                        Block b = entry.getKey();
                        int type = entry.getValue();
                        
                        b.getLocation().getBlock().setTypeId(type % 1000);
                        
                        if (type > 1000)
                            b.getLocation().getBlock().setData((byte) (type / 1000));
                    }
                }
            }, arena.repairDelay);
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
            return;
        }
        
        if (arena.monsters.contains(event.getEntity()))
        {
            if (event.getReason() == TargetReason.FORGOT_TARGET)
            {
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), arena));
                return;
            }
                
            if (event.getReason() == TargetReason.TARGET_DIED)
            {
                event.setTarget(MAUtils.getClosestPlayer(event.getEntity(), arena));
                return;
            }
            
            if (event.getReason() == TargetReason.CLOSEST_PLAYER)
                if (!arena.arenaPlayers.contains(event.getTarget()))
                    event.setCancelled(true);
            return;
        }
    }

    public void onEntityRegainHealth(EntityRegainHealthEvent event)
    {
        if (!arena.running) return;
        
        if (!(event.getEntity() instanceof Player) || !arena.arenaPlayers.contains((Player)event.getEntity()))
            return;
        
        if (event.getRegainReason() == RegainReason.REGEN)
            event.setCancelled(true);
    }

    public void onEntityDeath(EntityDeathEvent event)
    {        
        if (event.getEntity() instanceof Player)
        {
            Player p = (Player) event.getEntity();
            
            if (!arena.arenaPlayers.contains(p))
                return;
            
            event.getDrops().clear();
            arena.playerDeath(p);
            return;
        }
        
        if (arena.monsters.remove(event.getEntity()))
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
    }

    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!arena.running) return;
        
        EntityDamageByEntityEvent e = (event instanceof EntityDamageByEntityEvent) ? (EntityDamageByEntityEvent) event : null;
        Entity damager = (e != null) ? e.getDamager() : null;
        Entity damagee = event.getEntity();
        
        // Pet wolf
        if (damagee instanceof Wolf && arena.pets.contains(damagee))
        {            
            if (damager == null)
            {
                damagee.setFireTicks(32768);
                event.setCancelled(true);
                return;
            }
            else if (damager instanceof Player)
                event.setCancelled(true);
            else
                event.setDamage(0);
            
            return;
        }
        // Arena player
        else if (damagee instanceof Player)
        {
            if (arena.lobbyPlayers.contains(damagee))
                event.setCancelled(true);
            else if (!arena.arenaPlayers.contains(damagee))
                return;
            else if (!arena.detDamage && event.getCause() == DamageCause.BLOCK_EXPLOSION)
                event.setCancelled(true);
            else if (damager instanceof Player && !arena.pvp)
            {
                // if 'inLobby' fails, and 'not inArena' fails, 'inArena' is true
                event.setCancelled(true);
                return;
            }
            
            if (!event.isCancelled())
                arena.log.players.get((Player) damagee).dmgTaken += event.getDamage();
        }
        // Other LivingEntity
        else if (arena.monsters.contains(damagee))
        {            
            if (damager instanceof Player)
            {
                if (!arena.arenaPlayers.contains(damager))
                {
                    event.setCancelled(true);
                    return;
                }
                
                arena.log.players.get((Player) damager).dmgDone += event.getDamage();
                arena.log.players.get((Player) damager).hits++;
            }
            else if (damager instanceof Wolf && arena.pets.contains(damager))
            {                
                event.setDamage(1);
                arena.log.players.get((Player) ((Wolf) damager).getOwner()).dmgDone += event.getDamage();
            }
            else if (damager instanceof LivingEntity)
            {
                if (!arena.monsterInfight)
                    event.setCancelled(true);
            }
            
            // Boss
            if (arena.bossWave != null && damagee.equals(arena.bossWave.getEntity()))
            {
                // Subtract boss health, and reset actual entity health
                arena.bossWave.subtractHealth(event.getDamage());
                arena.bossWave.getEntity().setHealth(200);
                
                // Set damage to 1 for knockback and feedback
                event.setDamage(1);
                
                // If the boss is dead, remove the entity and create an explosion!
                if (arena.bossWave.getHealth() <= 0)
                {
                    arena.bossWave.clear();
                    arena.bossWave = null;
                }
            }
        }
    }
    
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        if (!arena.running || !arena.arenaPlayers.contains(event.getPlayer()))
            return;
        
        arena.log.players.get(event.getPlayer()).swings++;
    }

    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        if (arena.running && arena.shareInArena) return;
        
        Player p = event.getPlayer();
        if (p.getLocation().equals(arena.spectatorLoc))
        {
            event.getItemDrop().setItemStack(new ItemStack(1, 0));
            event.setCancelled(true);
        }
        
        if (!arena.arenaPlayers.contains(p) && !arena.lobbyPlayers.contains(p))
            return;
        
        MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_DROP_ITEM));
        event.setCancelled(true);
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        if (!arena.readyPlayers.contains(event.getPlayer()) && !arena.arenaPlayers.contains(event.getPlayer()))
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
        if (!arena.arenaPlayers.contains(event.getPlayer()) && !arena.lobbyPlayers.contains(event.getPlayer()))
            return;
        
        if (arena.running)
        {
            if (event.hasBlock() && event.getClickedBlock().getType() == Material.SAPLING)
                arena.addTrunkAndLeaves(event.getClickedBlock());
            return;
        }
        
        Action a = event.getAction();
        Player p = event.getPlayer();
        if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)
        {            
            event.setUseItemInHand(Result.DENY);
            event.setCancelled(true);
        }
        
        // Iron block
        if (event.hasBlock() && event.getClickedBlock().getTypeId() == 42)
        {
            if (arena.classMap.containsKey(p))
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_PLAYER_READY));
                arena.playerReady(p);
            }
            else
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_PICK_CLASS));
            }
            return;
        }
        
        // Sign
        if (event.hasBlock() && event.getClickedBlock().getState() instanceof Sign)
        {
            if (a == Action.RIGHT_CLICK_BLOCK)
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_RIGHT_CLICK));
                return;
            }
            
            // Cast the block to a sign to get the text on it.
            Sign sign = (Sign) event.getClickedBlock().getState();
            
            // Check if the first line of the sign is a class name.
            String className = sign.getLine(0);
            if (!arena.classes.contains(className) && !className.equalsIgnoreCase("random"))
                return;
            
            if (!plugin.hasDefTrue(p, "mobarena.classes." + className) && !className.equalsIgnoreCase("random"))
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_PERMISSION));
                return;
            }

            // Set the player's class.
            arena.assignClass(p, className);
            if (!className.equalsIgnoreCase("random"))
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_PICKED, className));
            else
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LOBBY_CLASS_RANDOM));
                
            return;
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        if (!arena.enabled || (!arena.arenaPlayers.contains(p) && !arena.lobbyPlayers.contains(p)))
            return;
        
        plugin.getAM().arenaMap.remove(p);
        arena.playerLeave(p);
    }

    public void onPlayerKick(PlayerKickEvent event)
    {
        Player p = event.getPlayer();
        if (!arena.enabled || (!arena.arenaPlayers.contains(p) && !arena.lobbyPlayers.contains(p)))
            return;
        
        plugin.getAM().arenaMap.remove(p);
        arena.playerLeave(p);
    }

    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        if (arena.edit || !arena.enabled || !arena.setup || arena.allowWarp)
            return;
        
        if (!arena.inRegion(event.getTo()) && !arena.inRegion(event.getFrom()))
            return;

        Player   p    = event.getPlayer();
        Location old  = arena.locations.get(p);
        Location to   = event.getTo();
        Location from = event.getFrom();
        
        if (arena.arenaPlayers.contains(p) || arena.lobbyPlayers.contains(p) || arena.specPlayers.contains(p))
        {
            if (arena.inRegion(from))
            {
                if (arena.inRegion(to) || to.equals(arena.arenaLoc) || to.equals(arena.lobbyLoc) || to.equals(arena.spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.WARP_FROM_ARENA));
                event.setCancelled(true);
                return;
            }
            
            if (arena.inRegion(to))
            {
                if (to.equals(arena.arenaLoc) || to.equals(arena.lobbyLoc) || to.equals(arena.spectatorLoc) || to.equals(old))
                    return;
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.WARP_TO_ARENA));
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
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();
        
        if (!arena.arenaPlayers.contains(p) && !arena.lobbyPlayers.contains(p))
            return;
        
        String[] args = event.getMessage().split(" ");
        
        if ((args.length > 1 && MACommands.COMMANDS.contains(args[1].trim())) ||
            MACommands.ALLOWED_COMMANDS.contains(event.getMessage().substring(1).trim()) ||
            MACommands.ALLOWED_COMMANDS.contains(args[0]))
            return;
        
        event.setCancelled(true);
        MAUtils.tellPlayer(p, MAMessages.get(Msg.MISC_COMMAND_NOT_ALLOWED));
    }
}
