package com.garbagemule.MobArena.listeners;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.enums.*;
import com.garbagemule.MobArena.MobArena;
import com.nisovin.magicspells.events.SpellCastEvent;

public class MagicSpellsListener implements Listener
{
    private MobArena plugin;
    private List<String> disabled, disabledOnBoss, disabledOnSwarm;
    
    public MagicSpellsListener(MobArena plugin)
    {
        this.plugin = plugin;

        // Set up the MagicSpells config-file.
        plugin.saveResource("res/magicspells.yml", false);
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(plugin.getDataFolder(), "magicspells.yml"));
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
