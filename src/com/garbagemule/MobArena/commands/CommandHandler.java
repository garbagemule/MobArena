package com.garbagemule.MobArena.commands;

import java.io.File;
import java.util.*;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.util.classparsing.*;

public class CommandHandler implements CommandExecutor
{
    /* COMMAND_PATH is an incomplete package-name for all commands in MobArena,
     * hence the trailing dot, and the COMMAND_TYPES array contains all the
     * different types of commands (the sub-folders). Both are used in the
     * loadCommands() method. */
    private static final String   COMMAND_PATH = "com.garbagemule.MobArena.commands.";
    private static final String[] COMMAND_TYPES = new String[]{"user", "admin", "setup"};
    
    private MobArenaPlugin plugin;
    private Map<String,MACommand> commands;
    
    public CommandHandler(MobArenaPlugin plugin) {
        this.plugin   = plugin;
        this.commands = new HashMap<String,MACommand>();
        loadCommands();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("You need arguments, pal!");
            return true;
        }
        
        // Grab the command from the map, and if null, didn't work.
        MACommand command = commands.get(args[0]);
        if (command == null) {
            System.out.println("Command '" + label + "' not recognized");
            return true;
        }
        
        // Cast if sender is a Player, null otherwise.
        Player p = (sender instanceof Player) ? (Player) sender : null;
        
        // If null, must be console.
        if (p == null) {
            if (!command.executeFromConsole(plugin, sender, args)) {
                sender.sendMessage("Can't use that command from the console.");
            }
            return true;
        }
        
        // Otherwise, check permissions
        if (!p.hasPermission(command.getPermission())) {
            sender.sendMessage("No permission");
            return true;
        }
        
        // Execute!
        command.execute(plugin, p, trimFirstArg(args));
        
        return true;
    }
    
    /**
     * Since Java doesn't support pointer arithmetic, this method
     * attempts to remove the first element of a String[] object
     * by creating a new array of the same size -1 and copying all
     * the elements over.
     * @param args a String[] to trim
     * @return a String[] with the same elements except for the very first
     */
    private String[] trimFirstArg(String[] args) {
        String[] result = new String[args.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = args[i+1];
        }
        return result;
    }
    
    /**
     * Load all of the MobArena commands from either the jar or the bin
     * folder into the commands-map. Each one of a commands' names/aliases
     * is added to the map, such that both "/ma join" and "/ma j" work, for
     * instance.
     */
    private void loadCommands() {
        // Prepare a strategy for the class parser.
        ClassIterationStrategy strategy = null;
        File f = new File("plugins" + File.separator + "MobArena.jar");

        try {
            // If jar exists, use it - otherwise, use bin.
            strategy = (f.exists()) ? new JarClassIterationStrategy(f) :
                                      new DirClassIterationStrategy(new File("bin"));
        
            // Create a new ClassParser with the given strategy.
            ClassParser parser = new ClassParser(strategy);
            
            // Iterate through all three folders, user/admin/setup
            for (String type : COMMAND_TYPES) {
                Collection<Class<?>> classes = parser.getClasses(COMMAND_PATH + type);
                
                for (Class<?> c : classes) {
                    // Make sure the class is indeed an instance of MACommand
                    Object obj = c.newInstance();
                    if (!(obj instanceof MACommand))
                        continue;
                    
                    // Then cast, and add for each command name/alias
                    MACommand command = (MACommand) obj;
                    for (String name : command.getNames()) {
                        commands.put(name, command);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}