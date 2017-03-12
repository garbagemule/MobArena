package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "setclassprice",
    pattern = "set(class)?price|fee",
    usage   = "/ma setclassprice <classname> $<price>",
    desc    = "set the price of a class",
    permission = "mobarena.setup.classes"
)
public class SetClassPriceCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require at least a class name
        if (args.length < 1) return false;

        // Grab the argument, if any.
        String arg1 = args[0];
        String arg2 = (args.length > 1 ? args[1] : "");

        // Grab the class.
        ArenaClass ac = am.getClasses().get(arg1);
        if (ac == null) {
            am.getGlobalMessenger().tell(sender, "No class named '" + arg1 + "'.");
            return true;
        }

        // Config-file value to set, and message to print
        String value;
        String msg;

        if (!arg2.equals("")) {
            // Strip the dollar sign, if it's there
            if (arg2.startsWith("$")) {
                arg2 = arg2.substring(1);
            }

            // Not a valid number? Bail!
            if (!arg2.matches("([1-9]\\d*)|(\\d*.\\d\\d?)")) {
                am.getGlobalMessenger().tell(sender, "Could not parse price '" + arg2 + "'. Expected e.g. $10 or $2.50 or $.25");
                return true;
            }
            double price = Double.parseDouble(arg2);

            value = "$" + arg2;
            msg = "Price for class '" + ac.getConfigName() + "' was set to " + am.getPlugin().economyFormat(price);
        } else {
            value = null;
            msg = "Price for class '" + ac.getConfigName() + "' was removed. The class is now free!";
        }

        // Set the value, save and reload config
        am.getPlugin().getConfig().set("classes." + ac.getConfigName() + ".price", value);
        am.getPlugin().saveConfig();
        am.loadClasses();
        am.getGlobalMessenger().tell(sender, msg);
        return true;
    }
}
