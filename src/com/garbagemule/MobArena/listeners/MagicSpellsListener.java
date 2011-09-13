package com.garbagemule.MobArena.listeners;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.FileUtils;
import com.nisovin.MagicSpells.Events.SpellCastEvent;
import com.nisovin.MagicSpells.Events.SpellListener;

public class MagicSpellsListener extends SpellListener
{
    private MobArena plugin;
    private List<String> disabled, disabledOnBoss;
    
    public MagicSpellsListener(MobArena plugin)
    {
        this.plugin = plugin;
        
        // Set up the MagicSpells config-file.
        File spellFile = FileUtils.extractFile(plugin.getDataFolder(), "magicspells.yml");    
        Configuration spellConfig = new Configuration(spellFile);
        spellConfig.load();
        setupSpells(spellConfig);
    }
    
    public void onSpellCast(SpellCastEvent event)
    {
        Arena arena = plugin.getAM().getArenaWithPlayer(event.getCaster());
        if (arena == null || !arena.isRunning()) return;
        
        String spell = event.getSpell().getName();
        
        if (disabled.contains(spell) || (arena.isBossWave() && disabledOnBoss.contains(spell)))
            event.setCancelled(true);
    }
    
    private void setupSpells(Configuration config)
    {
        this.disabled       = config.getStringList("disabled-spells", new LinkedList<String>());
        this.disabledOnBoss = config.getStringList("disabled-only-on-bosses", new LinkedList<String>());
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
