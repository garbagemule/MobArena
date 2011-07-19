package com.garbagemule.MobArena;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
        COMMANDS.add("j");                 // Join
        COMMANDS.add("join");              // Join
        COMMANDS.add("l");                 // Leave
        COMMANDS.add("leave");             // Leave
        COMMANDS.add("notready");          // List of players who aren't ready
        COMMANDS.add("spec");              // Watch arena
        COMMANDS.add("spectate");          // Watch arena
        COMMANDS.add("arenas");            // List of arenas
        COMMANDS.add("list");              // List of players
        COMMANDS.add("players");           // List of players
        COMMANDS.add("info");              // Info/help
        COMMANDS.add("help");              // Info/help
        COMMANDS.add("restore");           // Restore inventory
        COMMANDS.add("enable");            // Enabling
        COMMANDS.add("disable");           // Disabling
        COMMANDS.add("protect");           // Protection on/off
        COMMANDS.add("force");             // Force start/end
        COMMANDS.add("config");            // Reload config
        COMMANDS.add("arena");             // Current arena
        COMMANDS.add("setarena");          // Set current arena
        COMMANDS.add("addarena");          // Add a new arena
        COMMANDS.add("delarena");          // Delete current aren
        COMMANDS.add("editarena");         // Editing
        COMMANDS.add("setregion");         // Set a region point
        COMMANDS.add("expandregion");      // Expand the region
        COMMANDS.add("showregion");        // Show the region
        COMMANDS.add("setlobbyregion");    // Set a region point
        COMMANDS.add("expandlobbyregion"); // Expand the region
        COMMANDS.add("setwarp");           // Set arena/lobby/spec
        COMMANDS.add("spawnpoints");       // List spawnpoints
        COMMANDS.add("addspawn");          // Add a spawnpoint
        COMMANDS.add("delspawn");          // Delete a spawnpoint 
        COMMANDS.add("reset");             // Reset arena coordinates
        COMMANDS.add("addclass");          // Add a new class
        COMMANDS.add("delclass");          // Delete a class
        COMMANDS.add("auto-generate");     // Auto-generate arena
        COMMANDS.add("auto-degenerate");   // Restore cuboid
    }
    private boolean meanAdmins, showingRegion;
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
        boolean player  = (sender instanceof Player);
        boolean op      = player && ((Player) sender).isOp();
        boolean console = (sender instanceof ConsoleCommandSender);
        
        // Cast the sender to Player if possible.
        Player p = (player) ? (Player)sender : null;
        
        if (args.length == 0)
            return false;
        
        // Grab the command base and any arguments.
        String base = args[0].toLowerCase();
        String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";
        String arg2 = (args.length > 2) ? args[2].toLowerCase() : "";
        String arg3 = (args.length > 3) ? args[3].toLowerCase() : "";

        
        
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
            if (!player || !plugin.has(p, "mobarena.use.join"))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            List<Arena> arenas = am.getEnabledArenas();
            if (!am.enabled || arenas.size() < 1)
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                return true;
            }

            boolean error;
            Arena arena;
            
            if (!arg1.isEmpty())
                arena = am.getArenaWithName(arg1);
            else if (arenas.size() == 1)
                arena = arenas.get(0);
            else
                arena = null;
            
            if (arenas.size() > 1 && arg1.isEmpty())
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARG_NEEDED));
            else if (arena == null)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
            else if (am.arenaMap.containsKey(p) && (am.arenaMap.get(p).arenaPlayers.contains(p) || am.arenaMap.get(p).lobbyPlayers.contains(p)))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_IN_OTHER_ARENA));
            else if (!arena.enabled)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_ENABLED));
            else if (!arena.setup || arena.edit)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_SETUP));
            else if (arena.running && arena.notifyPlayers.add(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_IS_RUNNING));
            else if (arena.arenaPlayers.contains(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ALREADY_PLAYING));
            else if (!plugin.has(p, "mobarena.arenas." + arena.configName()))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_PERMISSION));
            else if (arena.playerLimit > 0 && arena.lobbyPlayers.size() >= arena.playerLimit)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_PLAYER_LIMIT_REACHED));
            else if (arena.joinDistance > 0 && !arena.inRegionRadius(p.getLocation(), arena.joinDistance))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_TOO_FAR));
            else if (!arena.canAfford(p) || !arena.takeFee(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_FEE_REQUIRED, MAUtils.listToString(arena.entryFee, plugin)));
            else if (arena.emptyInvJoin && !MAUtils.hasEmptyInventory(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_EMPTY_INV));
            else if (!arena.emptyInvJoin && !MAUtils.storeInventory(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_STORE_INV_FAIL));
            else error = false;
            
            // If there was an error, don't join.
            if (error)
            {
                if (arena != null)
                    arena.refund(p);
                return true;
            }
            
            // If player is in a boat/minecart, eject!
            if (p.isInsideVehicle())
                p.leaveVehicle();
            
            am.arenaMap.put(p,arena);
            arena.playerJoin(p, p.getLocation());
            
            MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_PLAYER_JOINED));
            if (!arena.entryFee.isEmpty())
                MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_FEE_PAID, MAUtils.listToString(arena.entryFee, plugin)));
            
            return true;
        }
        
        /*
         * Player leave
         */
        if (base.equals("leave") || base.equals("l"))
        {
            if (!player || !plugin.has(p, "mobarena.use.leave"))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!am.arenaMap.containsKey(p))
            {
                Arena arena = am.getArenaWithSpectator(p);
                if (arena != null)
                {            
                    arena.playerLeave(p);
                    MAUtils.tellPlayer(p, MAMessages.get(Msg.LEAVE_PLAYER_LEFT));
                    return true;
                }
                
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
            if (!player || !plugin.has(p, "mobarena.use.spectate"))
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            List<Arena> arenas = am.getEnabledArenas();
            if (!am.enabled || arenas.size() < 1)
            {
                MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_NOT_ENABLED));
                return true;
            }

            boolean error;
            Arena arena;
            
            if (!arg1.isEmpty())
                arena = am.getArenaWithName(arg1);
            else if (arenas.size() == 1)
                arena = arenas.get(0);
            else
                arena = null;
            
            if (arenas.size() > 1 && arg1.isEmpty())
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARG_NEEDED));
            else if (arena == null)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
            //else if (am.arenaMap.containsKey(p) && am.arenaMap.get(p).livePlayers.contains(p))
            else if (am.arenaMap.containsKey(p) && (am.arenaMap.get(p).arenaPlayers.contains(p) || am.arenaMap.get(p).lobbyPlayers.contains(p)))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_IN_OTHER_ARENA));
            else if (!arena.enabled)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_ENABLED));
            else if (!arena.setup || arena.edit)
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_ARENA_NOT_SETUP));
            //else if (arena.livePlayers.contains(p))
            else if (arena.arenaPlayers.contains(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_ALREADY_PLAYING));
            else if (arena.emptyInvSpec && !MAUtils.hasEmptyInventory(p))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.SPEC_EMPTY_INV));
            else if (arena.joinDistance > 0 && !arena.inRegionRadius(p.getLocation(), arena.joinDistance))
                error = MAUtils.tellPlayer(p, MAMessages.get(Msg.JOIN_TOO_FAR));
            else error = false;
            
            // If there was an error, don't spec.
            if (error)
                return true;
            
            // If player is in a boat/minecart, eject!
            if (p.isInsideVehicle())
                p.leaveVehicle();
            
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
            String list = MAUtils.listToString(am.arenas, plugin);
            MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_ARENAS, list));
            return true;
        }
        
        /*
         * Prints a list of all live players in all arenas, or live players in a specific arena.
         */
        if (base.equals("players") || base.equals("list"))
        {
            if (!arg1.isEmpty())
            {
                Arena arena = am.getArenaWithName(arg1);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
                
                String list = MAUtils.listToString(arena.getLivingPlayers(), plugin);
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_PLAYERS, list));
            }
            else
            {
                StringBuffer buffy = new StringBuffer();
                List<Player> players = new LinkedList<Player>();
                for (Arena arena : am.arenas)
                    players.addAll(arena.getLivingPlayers());
                buffy.append(MAUtils.listToString(players, plugin));
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_PLAYERS, buffy.toString()));
            }
            return true;
        }
        
        /*
         * Prints a list of all non-ready players in current arena, or non-ready players in a specific arena.
         */
        if (base.equals("notready"))
        {
            Arena arena;
            if (!arg1.isEmpty())
            {
                arena = am.getArenaWithName(arg1);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
            }
            else if (player)
            {
                arena = am.getArenaWithPlayer(p);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.LEAVE_NOT_PLAYING));
                    return true;
                }
            }
            else
            {
                MAUtils.tellPlayer(sender, "Usage: /ma notready <arena name>");
                return true;
            }
            
            String list = MAUtils.listToString(arena.getNonreadyPlayers(), plugin);
            MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_LIST_PLAYERS, list));
            return true;
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
            if (!console && !(player && plugin.has(p, "mobarena.admin.enable")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!arg1.isEmpty())
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
         * Kick player from whichever arena they are in.
         */
        if (base.equals("kick"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.admin.kick")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (arg1.isEmpty())
            {
                MAUtils.tellPlayer(sender, "Usage: /ma kick <player>");
                return true;
            }
            
            Arena arena = am.getArenaWithPlayer(arg1);
            if (arena == null)
            {
                MAUtils.tellPlayer(sender, "That player is not in an arena.");
                return true;
            }
            else
            {
                Player pl = server.getPlayer(arg1);
                am.arenaMap.remove(pl);
                arena.playerLeave(pl);
                MAUtils.tellPlayer(sender, "Player '" + arg1 + "' was kicked from arena '" + arena.configName() + "'.");
                MAUtils.tellPlayer(pl, "You were kicked by " + ((player) ? p.getName() : "the server."));
                return true;
            }
        }
        
        /*
         * Restore a player's inventory.
         */
        if (base.equals("restore"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.admin.restore")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!arg1.isEmpty())
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
        
        /*
         * Force start/end arenas.
         */
        if (base.equals("force"))
        {            
            if (arg1.equals("end"))
            {
                if (!console && !(player && plugin.has(p, "mobarena.admin.force.end")) && !op)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                    return true;
                }
                
                if (arg2.isEmpty())
                {
                    for (Arena arena : am.arenas)
                        arena.forceEnd();
                    
                    am.arenaMap.clear();
                    return true;
                }
                
                Arena arena = am.getArenaWithName(arg2);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                    return true;
                }
                
                //if (arena.livePlayers.isEmpty())
                if (arena.arenaPlayers.isEmpty())
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_END_EMPTY));
                    return true;
                }
                
                arena.forceEnd();
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.FORCE_END_ENDED));
                return true;
            }
            else if (arg1.equals("start"))
            {
                if (!console && !(player && plugin.has(p, "mobarena.admin.force.start")) && !op)
                {
                    MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                    return true;
                }
                
                if (arg2.isEmpty())
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
            else
            {
                MAUtils.tellPlayer(sender, "Usage: /ma force [start|end] (<arena name>)");
                return true;
            }
        }
        
        /*
         * Reload the config-file.
         */
        if (base.equals("config"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.admin.config.reload")) && !op)
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.arena")) && !op)
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.setarena")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1.isEmpty())
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setarena <arena name>");
                return true;
            }
            
            Arena arena = am.getArenaWithName(arg1);
            if (arena != null)
            {
                am.selectedArena = arena;
                MAUtils.tellPlayer(sender, "Currently selected arena: " + arena.configName());
            }
            else
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
            }
            return true;
        }
        
        /*
         * Create a new arena, and set the current arena to this new arena.
         */
        if (base.equals("addarena"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.addarena")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1.isEmpty())
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.delarena")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1.isEmpty())
            {
                MAUtils.tellPlayer(sender, "Usage: /ma delarena <arena name>");
                return true;
            }
            if (am.arenas.size() < 2)
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
        
        /*
         * Enable or disable protection
         */
        if (base.equals("protect"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.setup.protect")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            Arena arena;
            
            // No arguments
            if (arg1.isEmpty())
            {
                arena = am.selectedArena;
                arena.protect = !arena.protect;
            }
            
            // One argument
            else if (arg2.isEmpty())
            {
                // true/false
                if (arg1.equals("true") || arg1.equals("false"))
                {
                    arena = am.selectedArena;
                    arena.protect = arg1.equals("true");
                }
                // Arena name
                else
                {
                    arena = am.getArenaWithName(arg1);
                    if (arena == null)
                    {
                        MAUtils.tellPlayer(sender, "There is no arena with that name.");
                        MAUtils.tellPlayer(sender, "Usage: /ma protect ([true|false])");
                        MAUtils.tellPlayer(sender, "    or /ma protect <arena name> ([true|false])");
                        return true;
                    }
                    arena.protect = !arena.protect;
                }
            }
            
            // Two arguments
            else
            {
                if (!(arg2.equals("true") || arg2.equals("false")))
                {
                    MAUtils.tellPlayer(sender, "Usage: /ma protect ([true|false])");
                    MAUtils.tellPlayer(sender, "    or /ma protect <arena name> ([true|false])");
                    return true;
                }
                arena = am.getArenaWithName(arg1);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, "There is no arena with that name.");
                    MAUtils.tellPlayer(sender, "Usage: /ma protect ([true|false])");
                    MAUtils.tellPlayer(sender, "    or /ma protect <arena name> ([true|false])");
                    return true;
                }
                arena.protect = arg2.equals("true");
            }
            
            arena.serializeConfig();
            MAUtils.tellPlayer(sender, "Protection for arena '" + arena.configName() + "': " + ((arena.protect) ? ChatColor.GREEN + "on" : ChatColor.RED + "off")); 
            return true;
        }
        
        if (base.equals("editarena"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.setup.editarena")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            Arena arena;
            
            // No arguments.
            if (arg1.isEmpty())
            {
                arena = am.selectedArena;
                arena.edit = !arena.edit;
            }
            
            // One argument.
            else if (arg2.isEmpty())
            {
                // Argument is [true|false]
                if (arg1.equals("true") || arg1.equals("false"))
                {
                    arena = am.selectedArena;
                    arena.edit = arg1.equals("true");
                }
                // Argument is <arena name>
                else
                {
                    arena = am.getArenaWithName(arg1);
                    if (arena == null)
                    {
                        MAUtils.tellPlayer(sender, "There is no arena with that name.");
                        MAUtils.tellPlayer(sender, "Usage: /ma editarena ([true|false])");
                        MAUtils.tellPlayer(sender, "    or /ma editarena <arena name> ([true|false])");
                        return true;
                    }
                    arena.edit = !arena.edit;
                }
            }
            
            // Two arguments
            else
            {
                if (!(arg2.equals("true") || arg2.equals("false")))
                {
                    MAUtils.tellPlayer(sender, "Usage: /ma editarena ([true|false])");
                    MAUtils.tellPlayer(sender, "    or /ma editarena <arena name> ([true|false])");
                    return true;
                }
                arena = am.getArenaWithName(arg1);
                if (arena == null)
                {
                    MAUtils.tellPlayer(sender, "There is no arena with that name.");
                    MAUtils.tellPlayer(sender, "Usage: /ma editarena ([true|false])");
                    MAUtils.tellPlayer(sender, "    or /ma editarena <arena name> ([true|false])");
                    return true;
                }
                arena.edit = arg2.equals("true");
            }
            
            MAUtils.tellPlayer(sender, "Edit mode for arena '" + arena.configName() + "': " + ((arena.edit) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            if (arena.edit) MAUtils.tellPlayer(sender, "Remember to turn it back off after editing!"); 
            return true;
        }
        
        /*
         * Set region points [p1|p2] for the current arena.
         */
        if (base.equals("setregion"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.setregion")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!(arg1.equals("p1") || arg1.equals("p2")))
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.expandregion")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (args.length != 3 || !arg1.matches("(-)?[0-9]+"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma expandregion <amount> [up|down|out]");
                return true;
            }
            if (am.selectedArena.p1 == null || am.selectedArena.p2 == null)
            {
                MAUtils.tellPlayer(sender, "You must first define p1 and p2");
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
            
            // In case of a "negative" region, fix it!
            MAUtils.fixRegion(plugin.getConfig(), am.selectedArena.world, am.selectedArena);
            
            MAUtils.tellPlayer(sender, "Region for '" + am.selectedArena.configName() + "' expanded " + arg2 + " by " + arg1 + " blocks.");
            am.selectedArena.serializeConfig();
            am.selectedArena.load(plugin.getConfig());
            return true;
        }
        
        if (base.equals("showregion"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.showregion")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (am.selectedArena.p1 == null || am.selectedArena.p2 == null)
            {
                MAUtils.tellPlayer(sender, "The region is not defined for the selected arena.");
                return true;
            }
            if (showingRegion || !am.selectedArena.edit)
            {
                MAUtils.tellPlayer(sender, "Must be in edit mode.");
                return true;
            }

            Material mat = Material.WOOL;
            byte color = (byte)0;
            
            if (arg1.equals("glowstone"))
                mat = Material.GLOWSTONE;
            else if (arg1.equals("white") || arg1.equals("red") || arg1.equals("blue"))
                color = DyeColor.valueOf(arg1.toUpperCase()).getData();
            else if (arg1.equals("green")) // Dark green sucks
                color = 0x5;
            else
                mat = Material.GLASS;
            
            // Set the variable so we don't overwrite the region.
            showingRegion = true;
            
            // Show the frame.
            final World world = am.selectedArena.world;
            final Set<int[]> blocks = MAUtils.showRegion(world, am.selectedArena.p1, am.selectedArena.p2, mat.getId(), color);
            
            // And hide the frame.
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                new Runnable()
                {
                    public void run()
                    {
                        for (int[] buffer : blocks)
                            world.getBlockAt(buffer[0], buffer[1], buffer[2]).setTypeIdAndData(buffer[3], (byte) 0, false);
                        showingRegion = false;
                    }
                }, 2*20);
            
            return true;
        }
        
        if (base.equals("setlobbyregion"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.setlobbyregion")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            if (!(arg1.equals("l1") || arg1.equals("l2")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setlobbyregion [l1|l2]");
                return true;
            }
            
            MAUtils.setArenaCoord(plugin.getConfig(), am.selectedArena, arg1, p.getLocation());
            MAUtils.tellPlayer(sender, "Set lobby point " + arg1 + " for arena '" + am.selectedArena.configName() + "'");
            return true;
        }

        if (base.equals("expandlobbyregion"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.setup.expandlobbyregion")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (args.length != 3 || !arg1.matches("[0-9]+"))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma expandlobbyregion <amount> [up|down|out]");
                return true;
            }
            if (am.selectedArena.l1 == null || am.selectedArena.l2 == null)
            {
                MAUtils.tellPlayer(sender, "You must first define l1 and l2");
                return true;
            }
            
            if (arg2.equals("up"))
            {
                am.selectedArena.l2.setY(Math.min(127, am.selectedArena.l2.getY() + Integer.parseInt(arg1)));
            }
            else if (arg2.equals("down"))
            {
                am.selectedArena.l1.setY(Math.max(0, am.selectedArena.l1.getY() - Integer.parseInt(arg1)));
            }
            else if (arg2.equals("out"))
            {
                am.selectedArena.l1.setX(am.selectedArena.l1.getX() - Integer.parseInt(arg1));
                am.selectedArena.l1.setZ(am.selectedArena.l1.getZ() - Integer.parseInt(arg1));
                am.selectedArena.l2.setX(am.selectedArena.l2.getX() + Integer.parseInt(arg1));
                am.selectedArena.l2.setZ(am.selectedArena.l2.getZ() + Integer.parseInt(arg1));
            }
            else
            {
                MAUtils.tellPlayer(sender, "Usage: /ma expandlobbyregion <amount> [up|down|out]");
                return true;
            }
            
            MAUtils.tellPlayer(sender, "Lobby region for '" + am.selectedArena.configName() + "' expanded " + arg2 + " by " + arg1 + " blocks.");
            am.selectedArena.serializeConfig();
            am.selectedArena.load(plugin.getConfig());
            return true;
        }
        
        /*
         * Set warp points [arena|lobby|spectator] for the current arena. 
         */
        if (base.equals("setwarp"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.setwarp")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (!(arg1.equals("arena") || arg1.equals("lobby") || arg1.equals("spectator")))
            {
                MAUtils.tellPlayer(sender, "Usage: /ma setwarp [arena|lobby|spectator]");
                return true;
            }
            
            MAUtils.setArenaCoord(plugin.getConfig(), am.selectedArena, arg1, p.getLocation());
            MAUtils.tellPlayer(sender, "Set warp point " + arg1 + " for arena '" + am.selectedArena.configName() + "'");
            return true;
        }
        
        /*
         * List all the current spawnpoints for the current arena.
         */
        if (base.equals("spawnpoints"))
        {
            if (!console && !(player && plugin.has(p, "mobarena.setup.spawnpoints")) && !op)
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
            if (!(player && plugin.has(p, "mobarena.setup.addspawn")) && !op)
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.delspawn")) && !op)
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
            if (!(player && plugin.has(p, "mobarena.setup.autogenerate")) && !op)
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
            if (!console && !(player && plugin.has(p, "mobarena.setup.autodegenerate")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (arg1.isEmpty())
            {
                MAUtils.tellPlayer(sender, "Usage: /ma auto-degenerate <arena name>");
                return true;
            }
            if (am.arenas.size() < 2)
            {
                MAUtils.tellPlayer(sender, "At least one arena must exist!");
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
        
        if (base.equals("dooooo") && arg1.equals("it") && arg2.equals("hippie") && arg3.equals("monster"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.autogenerate")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            
            String name = "a0";
            do name = name.substring(0,1) + (Integer.parseInt(name.substring(1,2)) + 1);
            while (am.getArenaWithName(name) != null);
            
            if (MAUtils.doooooItHippieMonster(p.getLocation(), 13, name, plugin))
                MAUtils.tellPlayer(sender, "Arena with name '" + name + "' generated.");
            else
                MAUtils.tellPlayer(sender, "Could not auto-generate arena.");
            return true;
        }
        
        if (base.equals("undo") && arg1.equals("it") && arg2.equals("hippie") && arg3.equals("monster"))
        {
            if (!(player && plugin.has(p, "mobarena.setup.autodegenerate")) && !op)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.MISC_NO_ACCESS));
                return true;
            }
            if (am.arenas.size() < 2)
            {
                MAUtils.tellPlayer(sender, "At least one arena must exist!");
                return true;
            }
            if (am.getArenaWithName("a1") == null)
            {
                MAUtils.tellPlayer(sender, MAMessages.get(Msg.ARENA_DOES_NOT_EXIST));
                return true;
            }
            
            String name = "a1";
            while (am.getArenaWithName(name) != null)
                name = name.substring(0,1) + (Integer.parseInt(name.substring(1,2)) + 1);
            name = name.substring(0,1) + (Integer.parseInt(name.substring(1,2)) - 1);
            
            if (MAUtils.undoItHippieMonster(name, plugin, true))
                MAUtils.tellPlayer(sender, "Arena with name '" + name + "' degenerated.");
            else
                MAUtils.tellPlayer(sender, "Could not degenerate arena.");
            return true;
        }
        
        MAUtils.tellPlayer(sender, "Command not found.");
        return true;
    }
}