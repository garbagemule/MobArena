package com.garbagemule.MobArena;

import java.util.List;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;

import com.garbagemule.MobArena.MAMessages.Msg;

public class MACommands implements CommandExecutor
{
    public static List<String> ALLOWED_COMMANDS = new LinkedList<String>();
    public static final List<String> COMMANDS = new LinkedList<String>();
    static
    {
        COMMANDS.add("j");               // Join
        COMMANDS.add("join");            // Join
        COMMANDS.add("l");               // Leave
        COMMANDS.add("leave");           // Leave
        COMMANDS.add("notready");        // List of players who aren't ready
        COMMANDS.add("spec");            // Watch arena
        COMMANDS.add("spectate");        // Watch arena
        COMMANDS.add("arenas");          // List of arenas
        COMMANDS.add("list");            // List of players
        COMMANDS.add("players");         // List of players
        COMMANDS.add("restore");         // Restore inventory
        COMMANDS.add("enable");          // Enabling
        COMMANDS.add("disable");         // Disabling
        COMMANDS.add("protect");         // Protection on/off
        COMMANDS.add("force");           // Force start/end
        COMMANDS.add("config");          // Reload config
        COMMANDS.add("arena");           // Current arena
        COMMANDS.add("setarena");        // Set current arena
        COMMANDS.add("addarena");        // Add a new arena
        COMMANDS.add("delarena");        // Delete current aren
        COMMANDS.add("editarena");       // Editing
        COMMANDS.add("setregion");       // Set a region point
        COMMANDS.add("setwarp");         // Set arena/lobby/spec
        COMMANDS.add("spawnpoints");     // List spawnpoints
        COMMANDS.add("addspawn");        // Add a spawnpoint
        COMMANDS.add("delspawn");        // Delete a spawnpoint 
        COMMANDS.add("expandregion");    // Expand the region
        COMMANDS.add("reset");           // Reset arena coordinates
        COMMANDS.add("auto-generate");   // Auto-generate arena
        COMMANDS.add("auto-degenerate"); // Restore cuboid
    }
    private boolean player, op, console, meanAdmins;
    private Server server;
    private MobArena plugin;
    private ArenaMaster am;
    
    public MACommands(MobArena plugin, ArenaMaster am)
    {
        this.plugin      = plugin;
        this.am          = am;
        server           = Bukkit.getServer();
        meanAdmins       = (server.getPluginManager().getPlugin("Mean Admins") != null);
        ALLOWED_COMMANDS = MAUtils.getAllowedCommands(plugin.getConfig());
    }
    
    /**
     * Handles all command parsing.
     * Unrecognized commands return false, giving the sender a list of
     * valid commands (from plugin.yml).
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        // Play nice with Mean Admins.
        if (meanAdmins && !COMMANDS.contains(args[0].toLowerCase()))
        {
            server.getPluginManager().getPlugin("Mean Admins").onCommand(sender, command, label, args);
            return true;
        }
        
        // Determine if the sender is a player (and an op), or the console.
        player  = (sender instanceof Player);
        op      = player && ((Player) sender).isOp();
        console = (sender instanceof ConsoleCommandSender);
        
        // Cast the sender to Player if possible.
        Player p = (player) ? (Player)sender : null;
        
        if (args.length == 0)
            return false;
        
        // Grab the command base and any arguments.
        String base = args[0].toLowerCase();
        String arg1 = (args.length >= 2) ? args[1].toLowerCase() : null;
        String arg2 = (args.length >= 3) ? args[2].toLowerCase() : null;
        //String arg3 = (args.length >= 4) ? args[3].toLowerCase() : null;

        
        
        /*////////////////////////////////////////////////////////////////
        //
        //      Basics
        //
        ////////////////////////////////////////////////////////////////*/
        
        /*
         * Player join
         */
        if (base.equals("join") || base.equals("j"))
        {            
            if (!player)
            {
                MAUtils.tellPlayer(sender, "Players only.");
                return true;
            }

            boolean error;
            
            if (arg1 != null)
            {
                Arena arena = am.getArenaWithName(arg1);
                
                // Crap-load of sanity-checks.
                if (!am.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                else if (arena == null)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                else if (am.arenaMap.containsKey(p) && am.arenaMap.get(p).livePlayers.contains(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_IN_OTHER_ARENA));
                else if (!arena.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_ENABLED));
                else if (!arena.setup)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_SETUP));
                else if (arena.running)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_IS_RUNNING));
                else if (arena.livePlayers.contains(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ALREADY_PLAYING));
                else if (arena.emptyInv && !MAUtils.hasEmptyInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_EMPTY_INV));
                else if (!arena.emptyInv && !MAUtils.storeInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_STORE_INV_FAIL));
                else error = false;
                
                // If there was an error, don't join.
                if (error)
                    return true;
                
                am.arenaMap.put(p,arena);
                arena.playerJoin(p, p.getLocation());
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_PLAYER_JOINED));
                return true;
            }
            
            if (arg1 == null)
            {
                if (am.arenas.size() < 1)
                {
                    MAUtils.tellPlayer(sender, "There are no arenas loaded. Check your config-file.");
                    return true;
                }
                
                Arena arena = am.arenas.get(0);
                
                if (!am.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                else if (am.arenas.size() > 1)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARG_NEEDED));
                else if (arena == null)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                else if (am.arenaMap.containsKey(p) && am.arenaMap.get(p).livePlayers.contains(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_IN_OTHER_ARENA));
                else if (!arena.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_ENABLED));
                else if (!arena.setup)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_SETUP));
                else if (arena.running)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_IS_RUNNING));
                else if (arena.livePlayers.contains(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ALREADY_PLAYING));
                else if (arena.emptyInv && !MAUtils.hasEmptyInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_EMPTY_INV));
                else if (!arena.emptyInv && !MAUtils.storeInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_STORE_INV_FAIL));
                else error = false;
                
                // If there was an error, don't join.
                if (error)
                    return true;
                
                am.arenaMap.put(p,arena);
                arena.playerJoin(p, p.getLocation());
                
                MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_PLAYER_JOINED));
                return true;
            }
        }
        
        /*
         * Player leave
         */
        if (base.equals("leave") || base.equals("l"))
        {
            if (!player)
            {
                MAUtils.tellPlayer(sender, "Players only.");
                return true;
            }
            
            if (!am.arenaMap.containsKey(p))
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.LEAVE_NOT_PLAYING));
                return true;
            }
            
            Arena arena = am.arenaMap.remove(p);            
            arena.playerLeave(p);
            MAUtils.tellPlayer(p, MAMessages.get(Msg.LEAVE_PLAYER_LEFT));
            return true;
        }
        
        /*
         * Player spectate
         */
        if (base.equals("spectate") || base.equals("spec"))
        {
            if (!player)
            {
                MAUtils.tellPlayer(sender, "Players only.");
                return true;
            }

            boolean error;
            Arena arena = null;
            
            if (arg1 != null)
            {
                arena = am.getArenaWithName(arg1);

                if (!am.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                else if (am.arenaMap.containsKey(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_ALREADY_PLAYING));
                else if (arena == null)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                /*else if (!arena.running)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_NOT_RUNNING));
                else if (!MAUtils.hasEmptyInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_EMPTY_INV));*/
                else error = false;
                
                if (error)
                    return true;
            }
            else
            {
                arena = am.arenas.get(0);
                
                if (!am.enabled)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                else if (am.arenaMap.containsKey(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_ALREADY_PLAYING));
                else if (am.arenas.size() > 1)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARG_NEEDED));
                else if (arena == null)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                /*else if (!arena.running)
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_NOT_RUNNING));
                else if (!MAUtils.hasEmptyInventory(p))
                    error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_EMPTY_INV));*/
                else error = false;
                
                if (error)
                    return true;
            }

            am.arenaMap.put(p,arena);
            arena.playerSpec(p, p.getLocation());
            MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_PLAYER_SPECTATE));
            return true;
        }
        
        /*
         * Prints a list of all arenas.
         */
        if (base.equals("arenas"))
        {
            String list = MAUtils.listToString(am.arenas);
            MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_ARENAS, list));
            return true;
        }
        
        /*
         * Prints a list of all live players in all arenas, or live players in a specific arena.
         */
        if (base.equals("players") || base.equals("list"))
        {
            if (arg1 != null)
            {
                Arena arena = am.getArenaWithName(arg1);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
                
                String list = MAUtils.listToString(arena.getLivingPlayers());
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_PLAYERS, list));
            }
            else
            {
                StringBuffer buffy = new StringBuffer();
                for (Arena arena : am.arenas)
                    buffy.append(MAUtils.listToString(arena.getLivingPlayers()));
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_PLAYERS, buffy.toString()));
            }
            return true;
        }
        
        /*
         * Restore a player's inventory.
         */
        if (base.equals("restore"))
        {
            if (arg1 == null && player)
            {
                if (am.getArenaWithPlayer((Player) sender) != null)
                {
                    MAUtils.tellPlayer(sender, "You must first leave the current arena.");
                    return true;
                }
                
                if (MAUtils.restoreInventory(p))
                    MAUtils.tellPlayer(sender, "Restored your inventory!");
                return true;
            }
            if (arg1 != null && (op || console))
            {
                if (am.getArenaWithPlayer(arg1) != null)
                {
                    MAUtils.tellPlayer(sender, "Player is currently in an arena.");
                    return true;
                }
                
                if (MAUtils.restoreInventory(Bukkit.getServer().getPlayer(arg1)));
                    MAUtils.tellPlayer(sender, "Restored " + arg1 + "'s inventory!");
                return true;
            }
        }


        
        /*////////////////////////////////////////////////////////////////
        //
        //      Setup & Reload
        //
        ////////////////////////////////////////////////////////////////*/
        
        /*
         * Enable or disable arena(s)
         */
        if ((base.equals("enable") || base.equals("disable")))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (arg1 != null)
            {
                Arena arena = am.getArenaWithName(arg1);
                if (arena != null)
                {
                    arena.enabled = base.equals("enable");
                    arena.serializeConfig();
                    MAUtils.tellPlayer(sender, "Arena '" + arena.configName() + "' " + ((arena.enabled) ? ChatColor.GREEN : ChatColor.RED) + base + "d");
                }
                else
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                }
            }
            else
            {
                am.enabled = base.equals("enable");
                am.serializeSettings();
                MAUtils.tellPlayer(sender, "All arenas " + ((am.enabled) ? ChatColor.GREEN : ChatColor.RED) + base + "d");
            }
            return true;
        }
        
        /*
         * Enable or disable protection
         */
        if (base.equals("protect"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$") || arg2 == null || !(arg2.equals("true") || arg2.equals("false")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma protect <arena name> [true|false]");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena == null)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                return true;
            }
            
            arena.protect = arg2.equals("true");
            arena.serializeConfig();
            arena.load(plugin.getConfig());
            MAUtils.tellPlayer(sender, "Protection for arena '" + arg1 + "' set to " + arg2);
            return true;
        }
        
        /*
         * Force start/end arenas.
         */
        if (base.equals("force"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma force [start|end] (<arena name>)");
                return true;
            }
            
            if (arg1.equals("end"))
            {
                if (arg2 == null)
                {
                    for (Arena arena : am.arenas)
                        arena.forceEnd();
                    
                    am.arenaMap.clear();
                    return true;
                }
                if (!arg2.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
                {
                    MAUtils.tellPlayer(sender, "Usage: /ma force end (<arena name>)");
                    return true;
                }
                
                Arena arena = am.getArenaWithName(arg2);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
                
                // The arena exists.
                if (arena.livePlayers.isEmpty())
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_END_EMPTY));
                    return true;
                }
                
                arena.forceEnd();
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_END_ENDED));
                return true;
            }
            
            if (arg1.equals("start"))
            {
                if (arg2 == null || !arg2.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
                {
                    MAUtils.tellPlayer(sender, "Usage: /ma force start <arena name>");
                    return true;
                }
                
                Arena arena = am.getArenaWithName(arg2);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
                
                // The arena exists.
                if (arena.running)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_START_RUNNING));
                    return true;
                }
                if (arena.readyPlayers.isEmpty())
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_START_NOT_READY));
                    return true;
                }
                
                arena.forceStart();
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_START_STARTED));
                return true;
            }
        }
        
        /*
         * Reload the config-file.
         */
        if (base.equals("config") && arg1 != null)
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!arg1.equals("reload"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma config reload");
                return true;
            }
            
            am.updateAll();
            MAUtils.tellPlayer(sender, "Config reloaded.");
            return true;
        }
        
        /* 
         * Get the current arena, and list all other arenas.
         */
        if (base.equals("arena"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            MAUtils.tellPlayer(sender, "Currently selected arena: " + ChatColor.GREEN + am.selectedArena.configName());

            StringBuffer buffy = new StringBuffer();
            if (am.arenas.size() > 1)
            {
                for (Arena arena : am.arenas)
                    if (!arena.equals(am.selectedArena))
                        buffy.append(arena.configName() + " ");
            }
            else buffy.append(MAMessages.get(Msg.MISC_NONE));
            
            MAUtils.tellPlayer(sender, "Other arenas: " + buffy.toString());
            return true;
        }
        
        /*
         * Set the current arena
         */
        if (base.equals("setarena"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setarena <arena name>");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena != null)
                am.selectedArena = arena;
            else
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
            return true;
        }
        
        /*
         * Create a new arena, and set the current arena to this new arena.
         */
        if (base.equals("addarena"))
        {
            if (!op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma addarena <arena name>");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena != null)
            {
                MAUtils.tellPlayer(sender, "An arena with that name already exists.");
                return true;
            }
            
            arena = am.createArenaNode(arg1, p.getWorld());
            am.arenas.add(arena);
            am.selectedArena = arena;
            
            MAUtils.tellPlayer(sender, "New arena with name '" + arg1 + "' created!");
            return true;
        }
        
        if (base.equals("delarena"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma delarena <arena name>");
                return true;
            }
            if (am.arenas.size() == 1)
            {
                MAUtils.tellPlayer(sender, "At least one arena must exist.");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena == null)
            {
                MAUtils.tellPlayer(sender, "There is no arena with that name.");
                return true;
            }
            
            am.removeArenaNode(arg1);
            am.arenas.remove(arena);
            am.selectedArena = (am.selectedArena.equals(arena)) ? am.arenas.get(0) : am.selectedArena;
            
            MAUtils.tellPlayer(sender, "Arena '" + arena.configName() + "' deleted.");
            return true;
        }
        
        if (base.equals("editarena"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9_]*$") || arg2 == null || (!arg2.equals("true") && !arg2.equals("false")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma editarena <arena name> [true|false]");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena == null)
            {
                MAUtils.tellPlayer(sender, "There is no arena with that name.");
                return true;
            }
            
            arena.edit = arg2.equals("true");
            MAUtils.tellPlayer(sender, "Edit mode for arena '" + arg1 + "': " + ((arena.edit) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            if (arena.edit) MAUtils.tellPlayer(sender, "Remember to turn it back off after editing!"); 
            return true;
        }
        
        /*
         * Set region points [p1|p2] for the current arena.
         */
        if (base.equals("setregion"))
        {
            if (!op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (arg1 == null || !(arg1.equals("p1") || arg1.equals("p2")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setregion [p1|p2]");
                return true;
            }
            
            MAUtils.setArenaCoord(plugin.getConfig(), am.selectedArena, arg1, p.getLocation());
            MAUtils.tellPlayer(sender, "Set region point " + arg1 + " for arena '" + am.selectedArena.configName() + "'");
            return true;
        }
        
        /*
         * Expand the region <amount> (arg1) [up|down|out]
         */
        if (base.equals("expandregion"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (args.length != 3 || !arg1.matches("[0-9]+"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma expandregion <amount> [up|down|out]");
                return true;
            }
            
            if (arg2.equals("up"))
            {
                am.selectedArena.p2.setY(Math.min(127, am.selectedArena.p2.getY() + Integer.parseInt(arg1)));
            }
            else if (arg2.equals("down"))
            {
                am.selectedArena.p1.setY(Math.max(0, am.selectedArena.p1.getY() - Integer.parseInt(arg1)));
            }
            else if (arg2.equals("out"))
            {
                am.selectedArena.p1.setX(am.selectedArena.p1.getX() - Integer.parseInt(arg1));
                am.selectedArena.p1.setZ(am.selectedArena.p1.getZ() - Integer.parseInt(arg1));
                am.selectedArena.p2.setX(am.selectedArena.p2.getX() + Integer.parseInt(arg1));
                am.selectedArena.p2.setZ(am.selectedArena.p2.getZ() + Integer.parseInt(arg1));
            }
            else
            {
                MAUtils.tellPlayer(sender, "Usage: /ma expandregion <amount> [up|down|out]");
                return true;
            }
            
            MAUtils.tellPlayer(sender, "Region for '" + am.selectedArena.configName() + "' expanded " + arg2 + " by " + arg1 + " blocks.");
            am.selectedArena.serializeConfig();
            am.selectedArena.load(plugin.getConfig());
            return true;
        }
        
        /*
         * Set warp points [arena|lobby|spectator] for the current arena. 
         */
        if (base.equals("setwarp"))
        {
            if (!op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !(arg1.equals("arena") || arg1.equals("lobby") || arg1.equals("spectator")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setwarp [arena|lobby|spectator]");
                return true;
            }

            MAUtils.setArenaCoord(plugin.getConfig(), am.selectedArena, arg1, p.getLocation().getBlock().getRelative(0,1,0).getLocation());
            MAUtils.tellPlayer(sender, "Set warp point " + arg1 + " for arena '" + am.selectedArena.configName() + "'");
            return true;
        }
        
        /*
         * List all the current spawnpoints for the current arena.
         */
        if (base.equals("spawnpoints"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            StringBuffer buffy = new StringBuffer();
            List<String> spawnpoints = plugin.getConfig().getKeys("arenas." + am.selectedArena.configName() + ".coords.spawnpoints");
            
            if (spawnpoints != null)
            {
                for (String s : spawnpoints)
                {
                    buffy.append(s);
                    buffy.append(" ");
                }
            }
            else
            {
                buffy.append(MAMessages.get(Msg.MISC_NONE));
            }
            
            MAUtils.tellPlayer(sender, "Spawnpoints for arena '" + am.selectedArena.configName() + "': " + buffy.toString());
            return true;
        }
        
        /*
         * Add a spawnpoint for the current arena.
         */
        if (base.equals("addspawn"))
        {
            if (!op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma addspawn <spawn name>");
                return true;
            }
            
            MAUtils.setArenaCoord(plugin.getConfig(), am.selectedArena, "spawnpoints." + arg1, p.getLocation());
            MAUtils.tellPlayer(sender, "Added spawnpoint " + arg1 + " for arena \"" + am.selectedArena.configName() + "\"");
            return true;
        }
        
        /*
         * Delete a spawnpoint for the current arena.
         */
        if (base.equals("delspawn"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma delspawn <spawn name>");
                return true;
            }

            if (MAUtils.delArenaCoord(plugin.getConfig(), am.selectedArena, "spawnpoints." + arg1))
                MAUtils.tellPlayer(sender, "Deleted spawnpoint " + arg1 + " for arena '" + am.selectedArena.configName() + "'");
            else
                MAUtils.tellPlayer(sender, "Could not find the spawnpoint " + arg1 + "for the arena '" + am.selectedArena.configName() + "'");
            return true;
        }
        
        if (base.equals("auto-generate"))
        {
            if (!op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma autogenerate <arena name>");
                return true;
            }
            if (am.getArenaWithName(arg1) != null)
            {
                MAUtils.tellPlayer(sender, "An arena with that name already exists.");
                return true;
            }
            
            if (MAUtils.doooooItHippieMonster(p.getLocation(), 13, arg1, plugin))
                MAUtils.tellPlayer(sender, "Arena with name '" + arg1 + "' generated.");
            else
                MAUtils.tellPlayer(sender, "Could not auto-generate arena.");
            return true;
        }
        
        if (base.equals("auto-degenerate"))
        {
            if (!(op || console))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1 == null || !arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma auto-degenerate <arena name>");
                return true;
            }
            if (am.getArenaWithName(arg1) == null)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                return true;
            }
            
            if (MAUtils.undoItHippieMonster(arg1, plugin, true))
                MAUtils.tellPlayer(sender, "Arena with name '" + arg1 + "' degenerated.");
            else
                MAUtils.tellPlayer(sender, "Could not degenerate arena.");
            return true;
        }
        
        MAUtils.tellPlayer(sender, "Command not found.");
        return true;
    }
}