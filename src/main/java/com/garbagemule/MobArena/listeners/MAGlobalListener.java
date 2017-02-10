package com.garbagemule.MobArena.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.leaderboards.Stats;
import com.garbagemule.MobArena.util.VersionChecker;
import com.garbagemule.MobArena.util.inventory.InventoryManager;

/**
 * The point of this class is to simply redirect all events to each arena's
 * own listener(s).
 * This means only one actual listener need be registered in Bukkit, and thus
 * less overhead. Of course, this requires a little bit of "hackery" here
 * and there.
 */
public class MAGlobalListener implements Listener
{
    private MobArena plugin;
    private ArenaMaster am;

    public MAGlobalListener(MobArena plugin, ArenaMaster am) {
        this.plugin = plugin;
        this.am = am;
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                            BLOCK EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////

    //TODO watch block physics, piston extend, and piston retract events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onBlockBreak(event);
    }

    @EventHandler
    public void hangingBreak(HangingBreakEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onHangingBreak(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBurn(BlockBurnEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onBlockBurn(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void blockForm(BlockFormEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onBlockForm(event);
    }

//    // TODO: See ArenaListener.onBlockFromTo()
//    @EventHandler(priority = EventPriority.NORMAL)
//    public void blockFromTo(BlockFromToEvent event) {
//        for (Arena arena : am.getArenas())
//            arena.getEventListener().onBlockFromTo(event);
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void blockIgnite(BlockIgniteEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onBlockIgnite(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onBlockPlace(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void signChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("mobarena.setup.leaderboards")) {
            return;
        }

        if (!event.getLine(0).startsWith("[MA]")) {
            return;
        }

        String text = event.getLine(0).substring((4));
        Arena arena;
        Stats stat;

        if ((arena = am.getArenaWithName(text)) != null) {
            arena.getEventListener().onSignChange(event);
            setSignLines(event, ChatColor.GREEN + "MobArena", ChatColor.YELLOW + arena.arenaName(), ChatColor.AQUA + "Players", "---------------");
        }
        else if ((stat = Stats.getByShortName(text)) != null) {
            setSignLines(event, ChatColor.GREEN + "", "", ChatColor.AQUA + stat.getFullName(), "---------------");
            Messenger.tell(event.getPlayer(), "Stat sign created.");
        }
    }

    private void setSignLines(SignChangeEvent event, String s1, String s2, String s3, String s4) {
        event.setLine(0, s1);
        event.setLine(1, s2);
        event.setLine(2, s3);
        event.setLine(3, s4);
    }



    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                           ENTITY EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////


    @EventHandler(priority = EventPriority.HIGHEST)
    public void creatureSpawn(CreatureSpawnEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onCreatureSpawn(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityChangeBlock(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityCombust(EntityCombustEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityCombust(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void entityDamage(EntityDamageEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityDamage(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void entityDeath(EntityDeathEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityDeath(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityExplode(EntityExplodeEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityExplode(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockExplode(BlockExplodeEvent event) {
        // Account for Spigot's messy extra event
        EntityExplodeEvent fake = new EntityExplodeEvent(null, event.getBlock().getLocation(), event.blockList(), event.getYield());
        entityExplode(fake);

        // Copy the values over
        event.setCancelled(fake.isCancelled());
        event.blockList().clear();
        event.blockList().addAll(fake.blockList());
        event.setYield(fake.getYield());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityRegainHealth(EntityRegainHealthEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityRegainHealth(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityFoodLevelChange(FoodLevelChangeEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onFoodLevelChange(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void entityTarget(EntityTargetEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onEntityTarget(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void entityTeleport(EntityTeleportEvent event) {
        for (Arena arena : am.getArenas()) {
            arena.getEventListener().onEntityTeleport(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void potionSplash(PotionSplashEvent event) {
        for (Arena arena : am.getArenas()) {
            arena.getEventListener().onPotionSplash(event);
        }
    }



    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                           PLAYER EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////


    @EventHandler(priority = EventPriority.NORMAL)
    public void playerAnimation(PlayerAnimationEvent event) {
        if (!am.isEnabled()) return;
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerAnimation(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!am.isEnabled()) return;
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerBucketEmpty(event);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerChat(AsyncPlayerChatEvent event) {
        if (!am.isEnabled()) return;

        Arena arena = am.getArenaWithPlayer(event.getPlayer());
        if (arena == null || !arena.hasIsolatedChat()) return;

        event.getRecipients().retainAll(arena.getAllPlayers());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!am.isEnabled()) return;
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerCommandPreprocess(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerDropItem(PlayerDropItemEvent event) {
        if (!am.isEnabled()) return;
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerDropItem(event);
    }

    // HIGHEST => after SignShop
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent event) {
        if (!am.isEnabled()) return;
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerInteract(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerJoin(PlayerJoinEvent event) {
        InventoryManager.restoreFromFile(plugin, event.getPlayer());
        if (!am.notifyOnUpdates() || !event.getPlayer().isOp()) return;

        VersionChecker.checkForUpdates(plugin, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerKick(PlayerKickEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerKick(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerQuit(PlayerQuitEvent event) {
        for (Arena arena : am.getArenas())
            arena.getEventListener().onPlayerQuit(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerRespawn(PlayerRespawnEvent event) {
        for (Arena arena : am.getArenas()) {
            if (arena.getEventListener().onPlayerRespawn(event)) {
                return;
            }
        }

        plugin.restoreInventory(event.getPlayer());
    }

    public enum TeleportResponse {
        ALLOW, REJECT, IDGAF
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerTeleport(PlayerTeleportEvent event) {
        if (!am.isEnabled()) return;

        boolean allow = true;
        for (Arena arena : am.getArenas()) {
            TeleportResponse r = arena.getEventListener().onPlayerTeleport(event);

            // If just one arena allows, uncancel and stop.
            switch (r) {
                case ALLOW:
                    event.setCancelled(false);
                    return;
                case REJECT:
                    allow = false;
                    break;
                default: break;
            }
        }

        // Only cancel if at least one arena has rejected the teleport.
        if (!allow) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerPreLogin(PlayerLoginEvent event) {
        for (Arena arena : am.getArenas()) {
            arena.getEventListener().onPlayerPreLogin(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void vehicleExit(VehicleExitEvent event) {
        for (Arena arena : am.getArenas()) {
            arena.getEventListener().onVehicleExit(event);
        }
    }



    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                            WORLD EVENTS                               //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////


    @EventHandler(priority = EventPriority.NORMAL)
    public void worldLoadEvent(WorldLoadEvent event) {
        am.loadArenasInWorld(event.getWorld().getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void worldUnloadEvent(WorldUnloadEvent event) {
        am.unloadArenasInWorld(event.getWorld().getName());
    }
}
