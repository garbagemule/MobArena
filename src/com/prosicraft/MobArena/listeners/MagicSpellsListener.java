package com.prosicraft.MobArena.listeners;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.prosicraft.MobArena.Arena;
import com.prosicraft.MobArena.MobArena;
import com.prosicraft.MobArena.util.FileUtils;
import com.prosicraft.MobArena.waves.Wave.WaveType;
import com.nisovin.magicspells.events.SpellCastEvent;
import com.nisovin.magicspells.events.SpellListener;
import com.prosicraft.MobArena.MAUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MagicSpellsListener extends SpellListener
{
    private MobArena plugin;
    private List<String> disabled, disabledOnBoss, disabledOnSwarm;
    
    public MagicSpellsListener(MobArena plugin)
    {
        this.plugin = plugin;
        
        // Set up the MagicSpells config-file.
        File spellFile = FileUtils.extractFile(plugin.getDataFolder(), "magicspells.yml");    
        FileConfiguration spellConfig = YamlConfiguration.loadConfiguration(spellFile);        
        setupSpells(spellConfig);
    }
        
    @Override
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
        this.disabled        = getStringList(config, "disabled-spells", new LinkedList<String>());
        this.disabledOnBoss  = getStringList(config, "disabled-only-on-bosses", new LinkedList<String>());
        this.disabledOnSwarm = getStringList(config, "disabled-only-on-swarms", new LinkedList<String>());
    }
    
    private static List<String> getStringList(FileConfiguration config, String path, List<String> def) {
        List<String> res = null;
        if ( (res = config.getStringList(path)) == null )
            res = def;
        return res;
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
