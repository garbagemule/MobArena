package com.garbagemule.MobArena;

import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

public class MACommands implements CommandExecutor
{
    /**
     * Handles all command parsing.
     * Unrecognized commands return false, giving the sender a list of
     * valid commands (from plugin.yml).
     */
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        // Check if the server is also running Mean Admins.
        Plugin ma = ArenaManager.server.getPluginManager().getPlugin("Mean Admins");
        if (ma != null && !Arrays.asList(ArenaManager.plugin.COMMANDS).contains(args[0].toLowerCase()))
        {
            ma.onCommand(sender, command, commandLabel, args);
            return true;
        }
            
        // Only accept commands from players.
        if ((sender == null) || !(sender instanceof Player))
        {
            System.out.println("Only players can use these commands, silly.");
            return true;
        }
        
        // Cast the sender to a Player object.
        Player p = (Player) sender;
        
        /* If more than one argument, must be an advanced command.
         * Only allow operators to access these commands. */
        if (args.length > 1)
        {
            if (p.isOp())
                return advancedCommands(p, args);
            
            ArenaManager.tellPlayer(p, "Must be operator for advanced commands.");
            return true;
        }
        
        // If not exactly one argument, must be an invalid command.
        if (args.length != 1)
            return false;
        
        // Exactly one argument, return whatever simpleCommands returns.
        return basicCommands(p, args[0].toLowerCase());
    }
    
    /**
     * Handles basic commands.
     */
    private boolean basicCommands(Player p, String cmd)
    {
        if (cmd.equals("join") || cmd.equals("j"))
        {
            ArenaManager.playerJoin(p);
            return true;
        }
        
        if (cmd.equals("leave") || cmd.equals("l"))
        {
            ArenaManager.playerLeave(p);
            return true;
        }
        
        if (cmd.equals("list") || cmd.equals("who"))
        {
            ArenaManager.playerList(p);
            return true;
        }

        if (cmd.equals("spectate") || cmd.equals("spec"))
        {
            ArenaManager.playerSpectate(p);
            return true;
        }
        
        return false;
    }
    
    /**
     * Handles advanced commands, mainly for setting up the arena.
     */
    private boolean advancedCommands(Player p, String[] args)
    {        
        String cmd = args[0].toLowerCase();
        String arg = args[1].toLowerCase();
        
        // ma setwarp [arena|lobby|spectator]
        if (cmd.equals("setwarp"))
        {
            if (!arg.equals("arena") && !arg.equals("lobby") && !arg.equals("spectator"))
            {
                ArenaManager.tellPlayer(p, "/ma setwarp [arena|lobby|spectator]");
                return true;
            }
            
            // Write the coordinate data to the config-file.
            MAUtils.setCoords(arg, p.getLocation());
            
            ArenaManager.tellPlayer(p, "Warp point \"" + arg + "\" set.");
            MAUtils.notifyIfSetup(p);
            return true;
        }
        
        // ma addspawn <name>
        if (cmd.equals("addspawn"))
        {
            // The name must start with a letter, followed by any letter(s) or number(s).
            if (!arg.matches("[a-z]+([[0-9][a-z]])*"))
            {
                ArenaManager.tellPlayer(p, "Name must consist of only letters a-z and numbers 0-9");
                return true;
            }
            
            // Write the coordinate data to the config-file.
            MAUtils.setCoords("spawnpoints." + arg, p.getLocation());
            
            ArenaManager.tellPlayer(p, "Spawn point with name \"" + arg + "\" added.");
            MAUtils.notifyIfSetup(p);
            return true;
        }
        
        // ma delspawn <name>
        if (cmd.equals("delspawn"))
        {
            // The name must start with a letter, followed by any letter(s) or number(s).
            if (!arg.matches("[a-z]+([[0-9][a-z]])*"))
            {
                ArenaManager.tellPlayer(p, "Name must consist of only letters a-z and numbers 0-9");
                return true;
            }
            
            // If the spawnpoint does not exist, notify the player.
            if (MAUtils.getCoords("spawnpoints." + arg) == null)
            {
                ArenaManager.tellPlayer(p, "Couldn't find spawnpoint \"" + arg + "\".");
                return true;
            }
            
            MAUtils.delCoords("spawnpoints." + arg);
            
            ArenaManager.tellPlayer(p, "Spawn point with name \"" + arg + "\" removed.");
            MAUtils.notifyIfSetup(p);
            return true;
        }
        
        // ma setregion [p1|p2]
        if (cmd.equalsIgnoreCase("setregion"))
        {
            if (!arg.equals("p1") && !arg.equals("p2"))
            {
                ArenaManager.tellPlayer(p, "/ma setregion [p1|p2]");
                return true;
            }
            
            MAUtils.setCoords(arg, p.getLocation());
            MAUtils.fixCoords();
            
            ArenaManager.tellPlayer(p, "Region point \"" + arg + "\" set.");
            MAUtils.notifyIfSetup(p);
            return true;
        }
        
        // ma expandregion [up|down|out] <amount>
        if (cmd.equalsIgnoreCase("expandregion"))
        {
            if (ArenaManager.p1 == null || ArenaManager.p2 == null)
            {
                ArenaManager.tellPlayer(p, "Set up region points first: /ma setregion [p1|p2]");
                return true;
            }
            if (!arg.equals("up") && !arg.equals("down") && !arg.equals("out"))
            {
                ArenaManager.tellPlayer(p, "/ma expandregion [up|down|out] <amount>");
                return true;
            }
            
            if (args.length != 3 || !args[2].matches("[0-9]+"))
                return false;
            
            int i = Integer.parseInt(args[2]);
            MAUtils.expandRegion(arg, i);
            
            ArenaManager.tellPlayer(p, "Region expanded " + arg + " by " + i + " blocks.");
            return true;
        }
        
        // ma protect true/false
        if (cmd.equals("protect"))
        {
            if (!arg.equals("true") && !arg.equals("false"))
                return false;
            
            // Set the boolean
            ArenaManager.isProtected = Boolean.valueOf(arg);
            
            ArenaManager.tellPlayer(p, "Region protection: " + arg);
            return true;
        }
        
        // ma dooooo it hippie monster
        if (cmd.equals("dooooo"))
        {
            if (args.length != 4)
                return false;
            
            if (args[1].equals("it") && args[2].equals("hippie") && args[3].equals("monster"))
            {
                MAUtils.DoooooItHippieMonster(p.getLocation(), 13);
                ArenaManager.tellPlayer(p, "Auto-generated a working MobArena!");
                return true;
            }
        }
        
        if (cmd.equals("undo"))
        {
            if (args.length != 4)
                return false;
            
            if (args[1].equals("it") && args[2].equals("hippie") && args[3].equals("monster"))
            {
                MAUtils.UnDoooooItHippieMonster();
                ArenaManager.tellPlayer(p, "Restored your precious little patch >_>");
                return true;
            }
        }
        
        return false;
    }
}