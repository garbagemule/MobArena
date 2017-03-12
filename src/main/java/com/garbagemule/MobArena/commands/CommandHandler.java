package com.garbagemule.MobArena.commands;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.user.*;
import com.garbagemule.MobArena.commands.admin.*;
import com.garbagemule.MobArena.commands.setup.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

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

        // If the player is in a convo (Setup Mode), bail
        if (sender instanceof Conversable && ((Conversable) sender).isConversing()) {
            return true;
        }
        
        // If there's no base argument, show a helpful message.
        if (base.equals("")) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_HELP);
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
            am.getGlobalMessenger().tell(sender, Msg.MISC_MULTIPLE_MATCHES);
            for (Command cmd : matches) {
                showUsage(cmd, sender, false);
            }
            return true;
        }
        
        // If there are no matches at all, notify.
        if (matches.size() == 0) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NO_MATCHES);
            return true;
        }
        
        // Grab the only match.
        Command command  = matches.get(0);
        CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
        
        // First check if the sender has permission.
        if (!plugin.has(sender, info.permission())) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NO_ACCESS);
            return true;
        }
        
        // Check if the last argument is a ?, in which case, display usage and description
        if (last.equals("?") || last.equals("help")) {
            showUsage(command, sender, true);
            return true;
        }
        
        // Otherwise, execute the command!
        String[] params = trimFirstArg(args);
        if (!command.execute(am, sender, params)) {
            showUsage(command, sender, true);
        }
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
    private void showUsage(Command cmd, CommandSender sender, boolean prefix) {
        CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
        if (!plugin.has(sender, info.permission())) return;

        sender.sendMessage((prefix ? "Usage: " : "") + info.usage() + " " + ChatColor.YELLOW + info.desc());
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
        StringBuilder user = new StringBuilder();
        StringBuilder admin = new StringBuilder();
        StringBuilder setup = new StringBuilder();

        for (Command cmd : commands.values()) {
            CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
            if (!plugin.has(sender, info.permission())) continue;

            StringBuilder buffy;
            if (info.permission().startsWith("mobarena.admin")) {
                buffy = admin;
            } else if (info.permission().startsWith("mobarena.setup")) {
                buffy = setup;
            } else {
                buffy = user;
            }
            buffy.append("\n")
                 .append(ChatColor.RESET).append(info.usage()).append(" ")
                 .append(ChatColor.YELLOW).append(info.desc());
        }

        if (admin.length() == 0 && setup.length() == 0) {
            am.getGlobalMessenger().tell(sender, "Available commands: " + user.toString());
        } else {
            am.getGlobalMessenger().tell(sender, "User commands: " + user.toString());
            if (admin.length() > 0) am.getGlobalMessenger().tell(sender, "Admin commands: " + admin.toString());
            if (setup.length() > 0) am.getGlobalMessenger().tell(sender, "Setup commands: " + setup.toString());
        }
    }
    
    /**
     * Register all the commands directly.
     * This could also be done with a somewhat dirty classloader/resource reader
     * method, but this is neater, albeit more manual work.
     */
    private void registerCommands() {
        commands = new LinkedHashMap<String,Command>();
        
        // mobarena.use
        register(JoinCommand.class);
        register(LeaveCommand.class);
        register(SpecCommand.class);
        register(ArenaListCommand.class);
        register(PlayerListCommand.class);
        register(NotReadyCommand.class);
        register(PickClassCommand.class);

        // mobarena.admin
        register(EnableCommand.class);
        register(DisableCommand.class);
        register(ForceCommand.class);
        register(KickCommand.class);
        register(RestoreCommand.class);
        
        // mobarena.setup
        register(ConfigCommand.class);
        register(SetupCommand.class);
        register(SettingCommand.class);

        register(AddArenaCommand.class);
        register(RemoveArenaCommand.class);
        register(EditArenaCommand.class);
        register(CheckDataCommand.class);

        register(RemoveSpawnpointCommand.class);
        register(CheckSpawnsCommand.class);
        register(RemoveContainerCommand.class);

        register(ListClassesCommand.class);
        register(SetClassCommand.class);
        register(SetClassPriceCommand.class);
        register(RemoveClassCommand.class);
        register(ClassChestCommand.class);
        register(ListClassPermsCommand.class);
        register(AddClassPermCommand.class);
        register(RemoveClassPermCommand.class);

        register(RemoveLeaderboardCommand.class);
        register(AutoGenerateCommand.class);
        register(AutoDegenerateCommand.class);
    }
    
    /**
     * Register a command.
     * The Command's CommandInfo annotation is queried to find its pattern
     * string, which is used to map the commands.
     * @param c a Command
     */
    public void register(Class<? extends Command> c) {
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
