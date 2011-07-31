package com.garbagemule.MobArena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lobby
{
    protected Arena arena;
    protected ArenaMaster am;
    protected Location warp, l1, l2;
    
    public Lobby(Arena arena)
    {
        this.arena = arena;
    }
    public Lobby()
    {
        this(null);
    }
    
    public void playerJoin(Player p)
    {
        p.teleport(warp);
    }
}
