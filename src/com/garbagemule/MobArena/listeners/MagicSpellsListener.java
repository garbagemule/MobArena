package com.garbagemule.MobArena.listeners;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.FileUtils;
import com.garbagemule.MobArena.util.config.Config;
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
        File spellFile = FileUtils.extractResource(plugin.getDataFolder(), "magicspells.yml");
        Config spellConfig = new Config(spellFile);
        spellConfig.load();
        setupSpells(spellConfig);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpellCast(SpellCastEvent event)
    {
        Arena arena = plugin.getArenaMaster().getArenaWithPlayer(event.getCaster());
        if (arena == null || !arena.isRunning()) return;
        
        String spell = event.getSpell().getName();
        WaveType type = (arena.getWave() != null) ? arena.getWave().getType() : null;
        
        if (disabled.contains(spell) ||
           (type == WaveType.BOSS && disabledOnBoss.contains(spell)) ||
           (type == WaveType.SWARM && disabledOnSwarm.contains(spell))) {
            event.setCancelled(true);
        }
    }
    
    private void setupSpells(Config config)
    {
        this.disabled        = config.getStringList("disabled-spells", new LinkedList<String>());
        this.disabledOnBoss  = config.getStringList("disabled-on-bosses", new LinkedList<String>());
        this.disabledOnSwarm = config.getStringList("disabled-on-swarms", new LinkedList<String>());
    }
}
