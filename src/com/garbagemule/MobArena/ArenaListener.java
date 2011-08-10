package com.garbagemule.MobArena;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public interface ArenaListener
{	
	public void onBlockBreak(BlockBreakEvent event);
    public void onBlockPlace(BlockPlaceEvent event);
    public void onBlockIgnite(BlockIgniteEvent event);
    public void onCreatureSpawn(CreatureSpawnEvent event);
    public void onEntityExplode(EntityExplodeEvent event);
    public void onEntityCombust(EntityCombustEvent event);
    public void onEntityTarget(EntityTargetEvent event);
    public void onEntityRegainHealth(EntityRegainHealthEvent event);
    public void onEntityDeath(EntityDeathEvent event);
    public void onEntityDamage(EntityDamageEvent event);
    public void onPlayerDropItem(PlayerDropItemEvent event);
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event);
    public void onPlayerInteract(PlayerInteractEvent event);
    public void onPlayerQuit(PlayerQuitEvent event);
    public void onPlayerKick(PlayerKickEvent event);
    public void onPlayerTeleport(PlayerTeleportEvent event);
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event);
    
    /*
	public abstract void onBlockBreak(BlockBreakEvent event);
	public abstract void onBlockPlace(BlockPlaceEvent event);
    public abstract void onCreatureSpawn(CreatureSpawnEvent event);
    public abstract void onEntityExplode(EntityExplodeEvent event);
    public abstract void onEntityCombust(EntityCombustEvent event);
    public abstract void onEntityTarget(EntityTargetEvent event);
    public abstract void onEntityRegainHealth(EntityRegainHealthEvent event);
    public abstract void onEntityDeath(EntityDeathEvent event);
    public abstract void onEntityDamage(EntityDamageEvent event);
    public abstract void onPlayerDropItem(PlayerDropItemEvent event);
    public abstract void onPlayerBucketEmpty(PlayerBucketEmptyEvent event);
    public abstract void onPlayerInteract(PlayerInteractEvent event);
    public abstract void onPlayerQuit(PlayerQuitEvent event);
    public abstract void onPlayerKick(PlayerKickEvent event);
    public abstract void onPlayerTeleport(PlayerTeleportEvent event);
    public abstract void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event);
    */
}
