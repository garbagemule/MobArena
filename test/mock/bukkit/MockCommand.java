package mock.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class MockCommand extends Command
{
    public MockCommand(String name)
    {
        super(name);
    }

    @Override
    public boolean execute(CommandSender arg0, String arg1, String[] arg2)
    {
        return false;
    }

}
