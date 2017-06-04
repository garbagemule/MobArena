package com.garbagemule.MobArena.listeners;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.WaveType;
import com.nisovin.magicspells.events.SpellCastEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;

public class MagicSpellsListener implements Listener
{
    private MobArena plugin;
    private List<String> disabled, disabledOnBoss, disabledOnSwarm;
    
    public MagicSpellsListener(MobArena plugin)
    {
        this.plugin = plugin;

        // Set up the MagicSpells config-file.
        File file = new File(plugin.getDataFolder(), "magicspells.yml");
        if (!file.exists()) {
            plugin.saveResource("magicspells.yml", false);
            plugin.getLogger().info("magicspells.yml created.");
        }
        try {
            FileConfiguration config = new YamlConfiguration();
            config.load(file);
            setupSpells(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpellCast(SpellCastEvent event)
    {
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(event.getCaster());
        if (arena == null || !arena.isRunning()) return;
        
        String spell = event.getSpell().getName();
        WaveType type = (arena.getWaveManager().getCurrent() != null) ? arena.getWaveManager().getCurrent().getType() : null;
        
        if (disabled.contains(spell) ||
           (type == WaveType.BOSS && disabledOnBoss.contains(spell)) ||
           (type == WaveType.SWARM && disabledOnSwarm.contains(spell))) {
            event.setCancelled(true);
        }
    }
    
    private void setupSpells(ConfigurationSection config)
    {
        this.disabled        = config.getStringList("disabled-spells");
        this.disabledOnBoss  = config.getStringList("disabled-on-bosses");
        this.disabledOnSwarm = config.getStringList("disabled-on-swarms");
    }
}
