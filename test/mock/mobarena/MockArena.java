package mock.mobarena;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
//import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaPlugin;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.repairable.Repairable;
import com.garbagemule.MobArena.util.Config;
import com.garbagemule.MobArena.waves.BossWave;
import com.garbagemule.MobArena.waves.Wave;

public class MockArena extends Arena
{
    private MobArenaPlugin plugin;
    private String name;
    private boolean enabled;
    
    public MockArena(MobArenaPlugin plugin, String name, World world) {
        this.plugin  = plugin;
        this.name    = name;
        this.enabled = true;
        this.setup   = true;
        this.running = false;
        
        this.arenaPlayers = new HashSet<Player>();
        this.lobbyPlayers = new HashSet<Player>();
    }
    
    @Override
    public boolean startArena()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean endArena()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void forceStart()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void forceEnd()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playerJoin(Player p, Location loc) {
        lobbyPlayers.add(p);
    }

    @Override
    public void playerReady(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playerLeave(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playerDeath(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playerSpec(Player p, Location loc)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void playerKill(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void restoreInvAndGiveRewardsDelayed(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void restoreInvAndGiveRewards(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void storePlayerData(Player p, Location loc)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void storeContainerContents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void restoreContainerContents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void movePlayerToLobby(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void movePlayerToSpec(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void movePlayerToEntry(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void repairBlocks()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void queueRepairable(Repairable r)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void assignClass(Player p, String className)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void assignRandomClass(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void assignClassPermissions(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeClassPermissions(Player p)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void load(Config config)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void restoreRegion()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void serializeConfig()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deserializeConfig()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean serializeRegion()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean deserializeRegion()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inRegion(Location loc)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inRegionRadius(Location loc, int radius)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inArena(Player p)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inLobby(Player p) {
        return lobbyPlayers.contains(p);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isRunning()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPvpEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLightningEnabled()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isBossWave()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String configName() {
        return name;
    }

    @Override
    public String arenaName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MobArena getPlugin()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public World getWorld()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Wave getWave()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWave(Wave wave)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBossWave(BossWave bossWave)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Set<String> getClasses()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getAllSpawnpoints()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getSpawnpoints()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getBossSpawnpoint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPlayerCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void addBlock(Block b)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addMonster(LivingEntity e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addExplodingSheep(LivingEntity e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Player> getAllPlayers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Player> getLivingPlayers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Player> getArenaPlayers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<ArenaPlayer> getArenaPlayerSet()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ArenaPlayerStatistics> getArenaPlayerStatistics(
            Comparator<ArenaPlayerStatistics> comparator)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Player> getNonreadyPlayers()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<LivingEntity> getMonsters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Wolf> getPets()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void resetIdleTimer()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addTrunkAndLeaves(Block b)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canAfford(Player p)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean takeFee(Player p)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canJoin(Player p) {
        if (!enabled)
            plugin.tell(p, Msg.JOIN_ARENA_NOT_ENABLED);
        else if (!setup) //|| recurrentWaves.isEmpty()) TODO: Test waves.
            plugin.tell(p, Msg.JOIN_ARENA_NOT_SETUP);
        else if (edit)
            plugin.tell(p, Msg.JOIN_ARENA_EDIT_MODE);
        else if (running && (notifyPlayers.contains(p) || notifyPlayers.add(p)))
            plugin.tell(p, Msg.JOIN_ARENA_IS_RUNNING);
        else if (arenaPlayers.contains(p) || lobbyPlayers.contains(p))
            plugin.tell(p, Msg.JOIN_ALREADY_PLAYING);
        else if (!p.hasPermission("mobarena.arenas." + configName()))
            plugin.tell(p, Msg.JOIN_ARENA_PERMISSION);
        else if (maxPlayers > 0 && lobbyPlayers.size() >= maxPlayers)
            plugin.tell(p, Msg.JOIN_PLAYER_LIMIT_REACHED);
        else if (joinDistance > 0 && !inRegionRadius(p.getLocation(), joinDistance))
            plugin.tell(p, Msg.JOIN_TOO_FAR);
        else if (emptyInvJoin && !MAUtils.hasEmptyInventory(p))
            plugin.tell(p, Msg.JOIN_EMPTY_INV);
        else
            return true;

        /*else if (!canAfford(p))
            plugin.tell(p, Msg.JOIN_FEE_REQUIRED, MAUtils.listToString(entryFee, plugin));*/
        return false;
    }

    @Override
    public boolean canSpec(Player p)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
