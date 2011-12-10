package mock.util;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.Msg;

public class MockLogger
{
    private TreeMap<Timestamp,String> entries;
    
    public MockLogger() {
        this.entries = new TreeMap<Timestamp,String>();
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
