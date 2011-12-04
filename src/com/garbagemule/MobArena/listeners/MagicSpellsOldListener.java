package com.garbagemule.MobArena.listeners;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellListener;
import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.util.FileUtils;
import com.garbagemule.MobArena.waves.Wave.WaveType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MagicSpellsOldListener extends SpellListener
{
    private MobArena plugin;
    private List<String> disabled, disabledOnBoss, disabledOnSwarm;
    
    public MagicSpellsOldListener(MobArena plugin)
    {
        this.plugin = plugin;
        
        // Set up the MagicSpells config-file.
        File spellFile = FileUtils.extractFile(plugin.getDataFolder(), "magicspells.yml");    
        FileConfiguration spellConfig = YamlConfiguration.loadConfiguration(spellFile);
        MAUtils.loadFileConfiguration(spellConfig, spellFile);
        setupSpells(spellConfig);
    }
    
    public void onSpellCast(SpellCastEvent event)
    {
        Arena arena = plugin.getAM().getArenaWithPlayer(event.getCaster());
        if (arena == null || !arena.isRunning()) return;
        
        String spell = event.getSpell().getName();
        WaveType type = (arena.getWave() != null) ? arena.getWave().getType() : null;
        
        if (disabled.contains(spell) ||
            (type == WaveType.BOSS && disabledOnBoss.contains(spell)) ||
            (type == WaveType.SWARM && disabledOnSwarm.contains(spell)))
            event.setCancelled(true);
    }
    
    private void setupSpells(FileConfiguration config)
    {
        if ( (this.disabled = config.getStringList("disabled-spells")) == null )
                this.disabled = new LinkedList<String>();
        if ( (this.disabledOnBoss = config.getStringList("disabled-only-on-bosses")) == null )
                this.disabledOnBoss = new LinkedList<String>();
        if ( (this.disabledOnSwarm = config.getStringList("disabled-only-on-swarms")) == null )
                this.disabledOnSwarm = new LinkedList<String>();                        
    }        
    
    public void disableSpell(String spell)
    {
        disabled.add(spell);
    }
    
    public void disableSpellOnBoss(String spell)
    {
        disabledOnBoss.add(spell);
    }
}
