package com.garbagemule.MobArena;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.garbagemule.MobArena.leaderboards.Stats;

public class MABlockListener extends BlockListener
{
    private ArenaMaster am;
    
    public MABlockListener(ArenaMaster am)
    {
        this.am = am;
    }

    public void onBlockBreak(BlockBreakEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockBreak(event);
    }
    
    public void onBlockBurn(BlockBurnEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockBurn(event);
    }

    public void onBlockPlace(BlockPlaceEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockPlace(event);
    }
    
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockIgnite(event);
    }
    
    public void onSignChange(SignChangeEvent event)
    {
        if (!event.getPlayer().hasPermission("mobarena.setup.leaderboards"))
            return;
        
        if (event.getLine(0).startsWith("[MA]"))
        {
            String text = event.getLine(0).substring((4));
            Arena arena;
            Stats stat;
            if ((arena = am.getArenaWithName(text)) != null)
            {
                arena.eventListener.onSignChange(event);
                setSignLines(event, ChatColor.GREEN + "MobArena", ChatColor.YELLOW + arena.arenaName(), ChatColor.AQUA + "Players", "---------------");
            }
            else if ((stat = Stats.fromString(text)) != null)
            {
                setSignLines(event, ChatColor.GREEN + "", "", ChatColor.AQUA + stat.getFullName(), "---------------");
                MAUtils.tellPlayer(event.getPlayer(), "Stat sign created.");
            }
        }
    }
    
    private void setSignLines(SignChangeEvent event, String s1, String s2, String s3, String s4)
    {
        event.setLine(0, s1);
        event.setLine(1, s2);
        event.setLine(2, s3);
        event.setLine(3, s4);
    }
    
    /*
    public void onBlockPhysics(BlockPhysicsEvent event)
    {
        for (Arena arena : am.arenas)
            arena.eventListener.onBlockPhysics(event);
    }
    */
}