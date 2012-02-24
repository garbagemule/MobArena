package mock.util;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Msg;

public class MockLogger extends Logger
{
    private static Map<String,MockLogger> loggers = new TreeMap<String,MockLogger>();
    private TreeMap<Timestamp,String> entries     = new TreeMap<Timestamp,String>();
    
    /**
     * Private constructor to ensure that all MockLoggers exist within this class as singletons.
     */
    /*private MockLogger() {
        this.entries = new TreeMap<Timestamp,String>();
    }*/
    protected MockLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }
    
    /**
     * All MockLogger objects are singletons.
     * @param name the name of a MockLogger
     * @return the MockLogger with the input name, or a new MockLogger if it didn't already exist.
     */
    public static MockLogger getLogger(String name) {
        if (!loggers.containsKey(name))
            loggers.put(name, new MockLogger(name, null));
        
        return loggers.get(name);
    }
    
    public void log(String msg) {
        Timestamp time = new Timestamp(new Date().getTime());
        this.entries.put(time, msg);
    }
    
    public int size() {
        return entries.size();
    }
    
    public Entry<Timestamp,String> getLastEntry() {
        if (entries.isEmpty())
            return null;
        
        return entries.lastEntry();
    }
    
    public Entry<Timestamp,String> findEntry(String msg) {
        for (Entry<Timestamp,String> entry : entries.entrySet()) {
            if (entry.getValue().equals(msg))
                return entry;
        }
        return null;
    }
    
    public Entry<Timestamp,String> findEntryAfterTime(String msg, Timestamp time) {
        for (Entry<Timestamp,String> entry : entries.entrySet()) {
            if (entry.getKey().compareTo(time) >= 0 && entry.getValue().equals(msg))
                return entry;
        }
        return null;
    }
    
    public static String compileMsgToPlayer(Player p, String msg) {
        return ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg;
    }
    
    public static String compileMsgToPlayer(Player p, Msg msg) {
        return compileMsgToPlayer(p, msg.toString());
    }
}
