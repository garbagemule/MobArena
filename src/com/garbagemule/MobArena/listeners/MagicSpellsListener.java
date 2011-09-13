package com.garbagemule.MobArena.listeners;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArena;
import com.nisovin.MagicSpells.Events.SpellCastEvent;
import com.nisovin.MagicSpells.Events.SpellListener;

public class MagicSpellsListener extends SpellListener
{
    private MobArena plugin;
    
    public MagicSpellsListener(MobArena plugin)
    {
        this.plugin = plugin;
    }
    
    public void onSpellCast(SpellCastEvent event)
    {
        Arena arena = plugin.getAM().getArenaWithPlayer(event.getCaster());
        if (arena == null || !arena.isRunning()) return;
        
        if (event.getSpell().getName().equals("purge") && arena.isBossWave())
            event.setCancelled(true);
    }
}
