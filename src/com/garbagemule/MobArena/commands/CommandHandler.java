package com.garbagemule.MobArena.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.user.*;
import com.garbagemule.MobArena.commands.admin.*;
import com.garbagemule.MobArena.commands.setup.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

public class CommandHandler implements CommandExecutor
{
    private MobArena plugin;
    private ArenaMaster am;
    
    private Map<String,Command> commands;
    
    public CommandHandler(MobArena plugin) {
        this.plugin = plugin;
        this.am     = plugin.getArenaMaster();
        
        registerCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bcmd, String label, String[] args) {
        // Grab the base and arguments.
        String base = (args.length > 0 ? args[0] : "");
        String last = (args.length > 0 ? args[args.length - 1] : "");
        
        // If there's no base argument, show a helpful message.
        if (base.equals("")) {
            Messenger.tellPlayer(sender, Msg.MISC_HELP);
            return true;
        }
        
        // The help command is a little special
        if (base.equals("?") || base.equals("help")) {
            showHelp(sender);
            return true;
        }
        
        // Get all commands that match the base.
        List<Command> matches = getMatchingCommands(base);
        
        // If there's more than one match, display them.
        if (matches.size() > 1) {
            Messenger.tellPlayer(sender, Msg.MISC_MULTIPLE_MATCHES);
            for (Command cmd : matches) {
                showUsage(cmd, sender);
            }
            return true;
        }
        
        // If there are no matches at all, notify.
        if (matches.size() == 0) {
            Messenger.tellPlayer(sender, Msg.MISC_NO_MATCHES);
            return true;
        }
        
        // Grab the only match.
        Command command  = matches.get(0);
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        
        // First check if the sender has permission.
        if (!plugin.has(sender, info.permission())) {
            Messenger.tellPlayer(sender, Msg.MISC_NO_ACCESS);
            return true;
        }
        
        // Check if the last argument is a ?, in which case, display usage and description
        if (last.equals("?") || last.equals("help")) {
            showUsage(command, sender);
            return true;
        }
        
        // Otherwise, execute the command!
        String[] params = trimFirstArg(args);
        command.execute(am, sender, params);
        
        return true;
    }
    
    /**
     * Get all commands that match a given string.
     * @param arg the given string
     * @return a list of commands whose patterns match the given string
     */
    private List<Command> getMatchingCommands(String arg) {
        List<Command> result = new ArrayList<Command>();
        
        // Grab the commands that match the argument.
        for (Entry<String,Command> entry : commands.entrySet()) {
            if (arg.matches(entry.getKey())) {
                result.add(entry.getValue());
            }
        }
        
        return result;
    }
    
    /**
     * Show the usage and description messages of a command to a player.
     * The usage will only be shown, if the player has permission for the command.
     * @param cmd a Command
     * @param sender a CommandSender
     */
    private void showUsage(Command cmd, CommandSender sender) {
        CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
        if (!plugin.has(sender, info.permission())) return;
        
        Messenger.tellPlayer(sender, info.usage() + " " + ChatColor.YELLOW + info.desc());
    }
    
    /**
     * Remove the first argument of a string. This is because the very first
     * element of the arguments array will be the command itself.
     * @param args an array of length n
     * @return the same array minus the first element, and thus of length n-1
     */
    private String[] trimFirstArg(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }
    
    /**
     * List all the available MobArena commands for the CommandSender.
     * @param sender a player or the console
     */
    private void showHelp(CommandSender sender) {
        Messenger.tellPlayer(sender, "Available MobArena commands:");
        
        for (Command cmd : commands.values()) {
            showUsage(cmd, sender);
        }
    }
    
    /**
     * Register all the commands directly.
     * This could also be done with a somewhat dirty classloader/resource reader
     * method, but this is neater, albeit more manual work.
     */
    private void registerCommands() {
        commands = new HashMap<String,Command>();
        
        // mobarena.use
        register(ArenaListCommand.class);
        register(JoinCommand.class);
        register(LeaveCommand.class);
        register(NotReadyCommand.class);
        register(SpecCommand.class);
        register(PlayerListCommand.class);
        
        // mobarena.admin
        register(DisableCommand.class);
        register(EnableCommand.class);
        register(ForceCommand.class);
        register(KickCommand.class);
        register(RestoreCommand.class);
        
        // mobarena.setup
        register(AddArenaCommand.class);
        register(AddClassPermCommand.class);
        register(AddContainerCommand.class);
        register(AddSpawnpointCommand.class);
        register(ArenaCommand.class);
        register(CheckDataCommand.class);
        register(ConfigCommand.class);
        register(ContainersCommand.class);
        register(EditArenaCommand.class);
        register(ExpandLobbyRegionCommand.class);
        register(ExpandRegionCommand.class);
        register(ListClassesCommand.class);
        register(ListClassPermsCommand.class);
        register(ProtectCommand.class);
        register(RemoveArenaCommand.class);
        register(RemoveClassCommand.class);
        register(RemoveClassPermCommand.class);
        register(RemoveContainerCommand.class);
        register(RemoveLeaderboardCommand.class);
        register(RemoveSpawnpointCommand.class);
        register(SetArenaCommand.class);
        register(SetClassCommand.class);
        register(SetLobbyRegionCommand.class);
        register(SetRegionCommand.class);
        register(SetWarpCommand.class);
        register(ShowRegionCommand.class);
        register(SpawnpointsCommand.class);
        register(AutoGenerateCommand.class);
        register(AutoDegenerateCommand.class);
    }
    
    /**
     * Register a single command.
     * The Command's CommandInfo annotation is queried to find its pattern
     * string, which is used to map the commands.
     * @param c a Command
     */
    private void register(Class<? extends Command> c) {
        CommandInfo info = c.getAnnotation(CommandInfo.class);
        if (info == null) return;
        
        try {
            commands.put(info.pattern(), c.newInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
