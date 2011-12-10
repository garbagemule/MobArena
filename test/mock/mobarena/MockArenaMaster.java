package mock.mobarena;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;

public class MockArenaMaster extends ArenaMaster
{
    private MobArenaPlugin plugin;
    private List<Arena> arenas;
    
    public MockArenaMaster(MobArenaPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new LinkedList<Arena>();
    }
    
    @Override
    public List<Arena> getEnabledArenas() {
        List<Arena> result = new LinkedList<Arena>();
        
        for (Arena a : arenas) {
            if (a.isEnabled())
                result.add(a);
        }
        
        return result;
    }

    @Override
    public List<Arena> getPermittedArenas(Player p) {
        List<Arena> result = new LinkedList<Arena>();
        
        for (Arena a : arenas) {
            String perm = "mobarena.arenas." + a.configName();
            
            if (!p.isPermissionSet(perm) || p.hasPermission(perm))
                result.add(a);
        }
        
        return result;
    }

    @Override
    public List<Arena> getEnabledAndPermittedArenas(Player p) {
        List<Arena> result = new LinkedList<Arena>();
        
        for (Arena a : arenas) {
            // We only want enabled arenas.
            if (!a.isEnabled())
                continue;
            
            // And the player must have permission.
            String perm = "mobarena.arenas." + a.configName();
            if (!p.isPermissionSet(perm) || p.hasPermission(perm))
                result.add(a);
        }
        
        return result;
    }

    @Override
    public Arena getArenaAtLocation(Location loc)
    {
        return null;
    }

    @Override
    public List<Arena> getArenasInWorld(World world)
    {
        return null;
    }

    @Override
    public List<Player> getAllPlayers()
    {
        return null;
    }

    @Override
    public List<Player> getAllPlayersInArena(String arenaName)
    {
        return null;
    }

    @Override
    public List<Player> getAllLivingPlayers()
    {
        return null;
    }

    @Override
    public List<Player> getLivingPlayersInArena(String arenaName)
    {
        return null;
    }

    @Override
    public Arena getArenaWithPlayer(Player p)
    {
        return null;
    }

    @Override
    public Arena getArenaWithPlayer(String playerName)
    {
        return null;
    }

    @Override
    public Arena getArenaWithSpectator(Player p)
    {
        return null;
    }

    @Override
    public Arena getArenaWithMonster(Entity e)
    {
        return null;
    }

    @Override
    public Arena getArenaWithPet(Entity e)
    {
        return null;
    }

    @Override
    public Arena getArenaWithName(String configName) {
        return getArenaWithName(this.arenas, configName);
    }

    @Override
    public Arena getArenaWithName(Collection<Arena> arenas, String configName) {
        for (Arena a : arenas) {
            if (a.configName().equals(configName))
                return a;
        }
        return null;
    }

    @Override
    public void initialize()
    {
        
    }

    @Override
    public void loadSettings()
    {
        
    }

    @Override
    public void loadClasses()
    {
        
    }

    @Override
    public void loadArenas()
    {
        
    }

    @Override
    public Arena createArenaNode(String configName, World world) {
        Arena a = new MockArena(plugin, configName, world);
        arenas.add(a);
        return a;
    }

    @Override
    public void removeArenaNode(String configName)
    {
        
    }

    @Override
    public void update(boolean settings, boolean classes, boolean arenalist)
    {
        
    }

    @Override
    public void serializeSettings()
    {
        
    }

    @Override
    public void serializeArenas()
    {
        
    }

    @Override
    public void deserializeArenas()
    {
        
    }

    @Override
    public void updateSettings()
    {
        
    }

    @Override
    public void updateClasses()
    {
        
    }

    @Override
    public void updateArenas()
    {
        
    }

    @Override
    public void updateAll()
    {
        
    }
}
