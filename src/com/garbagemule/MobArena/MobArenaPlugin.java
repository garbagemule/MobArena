package com.garbagemule.MobArena;

import org.bukkit.command.CommandSender;

public interface MobArenaPlugin
{
    public ArenaMaster getArenaMaster();
    
    public void tell(CommandSender sender, String msg);

    public void tell(CommandSender sender, Msg msg);
}
