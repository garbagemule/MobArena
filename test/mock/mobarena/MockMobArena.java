package mock.mobarena;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.ArenaMaster;
import com.garbagemule.MobArena.MobArenaPlugin;
import com.garbagemule.MobArena.Msg;

public class MockMobArena implements MobArenaPlugin
{
    private ArenaMaster arenaMaster;
    
    public MockMobArena() {
        this.arenaMaster = new MockArenaMaster(this);
    }
    
    public MockMobArena(ArenaMaster arenaMaster) {
        this.arenaMaster = arenaMaster;
    }
    
    @Override
    public ArenaMaster getArenaMaster() {
        return arenaMaster;
    }

    @Override
    public void tell(CommandSender sender, String msg) {
        if (sender == null || msg.equals("") || msg.equals(" "))
            return;
        
        sender.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg);
    }

    @Override
    public void tell(CommandSender sender, Msg msg) {
        tell(sender, msg.toString());
    }
}
