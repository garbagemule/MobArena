package com.prosicraft.MobArena.util.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
//import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.prosicraft.MobArena.ArenaLog;
import com.prosicraft.MobArena.ArenaPlayer;
import com.prosicraft.MobArena.MAUtils;
import com.prosicraft.MobArena.MobArena;
import com.prosicraft.MobArena.util.configLoader;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;

public class XML
{
    public static void saveSessionData(ArenaLog log)
    {
        File dir = new File(MobArena.arenaDir, log.getArena().configName());
        if (!dir.exists()) dir.mkdirs();
        
        // It's here if people want it.
        //Namespace w = Namespace.getNamespace("com.garbagemule.MobArena");
        
        // General info
        Element ge = new Element("general-info");
        ge.addContent(new Element("start-time").addContent(log.getStartTime().toString()));
        ge.addContent(new Element("end-time").addContent(log.getEndTime().toString()));
        ge.addContent(new Element("duration").addContent(log.getDuration().toString()));
        ge.addContent(new Element("last-wave").addContent(log.getLastWave() + ""));
        
        // Class distribution
        Element cd = new Element("class-distribution");
        for (Map.Entry<String,Integer> entry : log.distribution.entrySet())
            cd.addContent(new Element(entry.getKey()).addContent(entry.getValue() + ""));
        
        // Player data
        Element pd = new Element("player-data");
        for (Map.Entry<Player,ArenaPlayer> entry : log.players.entrySet())
        {
            // Name as attribute
            Element p = new Element("player").setAttribute(new Attribute("name", entry.getKey().getName()));
            ArenaPlayer ap = entry.getValue();

            p.addContent(new Element("last-wave").addContent(ap.getStats().lastWave + ""));
            p.addContent(new Element("kills").addContent(ap.getStats().lastWave + ""));
            p.addContent(new Element("damage-done").addContent(ap.getStats().dmgDone + ""));
            p.addContent(new Element("damage-taken").addContent(ap.getStats().dmgTaken + ""));
            p.addContent(new Element("swings").addContent(ap.getStats().swings + ""));
            p.addContent(new Element("hits").addContent(ap.getStats().hits + ""));
            
            // Rewards
            Element rw = new Element("rewards");
            for (ItemStack stack : ap.rewards)
                rw.addContent(getReward(stack));
            
            p.addContent(rw);
            pd.addContent(p);
        }
        
        // Add the main nodes
        Element root = new Element("last-session");
        root.addContent(ge);
        root.addContent(cd);
        root.addContent(pd);
        
        // Create a new document
        Document doc = new Document();
        doc.addContent(root);
        
        // Serialize!
        serialize(log, dir, doc, "lastsession.xml");
    }
    
    public static void updateArenaTotals(ArenaLog log)
    {
        configLoader ld = Totals.getArenaTotals(log.arena);
        FileConfiguration totals = ld.getConfig();
        File file = ld.getAssociatedFile();
        MAUtils.loadFileConfiguration(totals, file);

        // General data
        Element gd = new Element("general-info");
        gd.addContent(new Element("total-games-played").addContent(totals.getInt("general-info.total-games-played", 0) + ""));
        gd.addContent(new Element("most-players").addContent(totals.getInt("general-info.most-players", 0) + ""));
        gd.addContent(new Element("highest-wave-reached").addContent(totals.getInt("general-info.highest-wave-reached", 0) + ""));
        gd.addContent(new Element("total-monsters-killed").addContent(totals.getInt("general-info.total-monsters-killed", 0) + ""));
        gd.addContent(new Element("total-duration").addContent(totals.getString("general-info.total-duration", "0:00:00")));
        gd.addContent(new Element("longest-session-duration").addContent(totals.getString("general-info.longest-session-duration", "0:00:00")));
        
        // Classes
        Element cl = new Element("classes");
        for (String c : log.arena.getClasses())
        {
            Element e = new Element("class").setAttribute(new Attribute("name", c));
            e.addContent(new Element("kills").addContent(totals.getInt("classes." + c + ".kills", 0) + ""));
            e.addContent(new Element("damage-done").addContent(totals.getInt("classes." + c + ".damage-done", 0) + ""));
            e.addContent(new Element("damage-taken").addContent(totals.getInt("classes." + c + ".damage-taken", 0) + ""));
            cl.addContent(e);
        }
        
        // Rewards
        Element rw = new Element("rewards");
        for (ArenaPlayer ap : log.players.values())
            for (ItemStack stack : ap.rewards)
                rw.addContent(getReward(stack));
        
        // Players
        Element pl = new Element("players");
        for (ArenaPlayer ap : log.players.values())
        {
            String name = ap.player.getName();
            Element p = new Element("player").setAttribute(new Attribute("name", name));
            p.addContent(new Element("games-played").addContent(totals.getInt("players." + name + ".games-played", 1) + ""));
            p.addContent(new Element("kills").addContent(totals.getInt("players." + name + ".kills", 1) + ""));
            p.addContent(new Element("damage-done").addContent(totals.getInt("players." + name + ".damage-done", 1) + ""));
            p.addContent(new Element("damage-taken").addContent(totals.getInt("players." + name + ".damage-taken", 1) + ""));
            p.addContent(new Element("swings").addContent(totals.getInt("players." + name + ".swings", 1) + ""));
            p.addContent(new Element("hits").addContent(totals.getInt("players." + name + ".hits", 1) + ""));
            
            Set<String> classes = totals.getConfigurationSection("players." + name + ".classes").getKeys(false);
            Element pcl = new Element("classes");
            if (classes != null)
            {
                for (String c : classes)
                    pcl.addContent(new Element("class").setAttribute(new Attribute("name", c)).addContent(totals.getInt("players." + name + ".classes." + c, 0) + ""));
            }
            
            p.addContent(pcl);
            pl.addContent(p);
        }
        
        // Add the main nodes
        Element root = new Element("last-session");
        root.addContent(gd);
        root.addContent(cl);
        root.addContent(rw);
        root.addContent(pl);
        
        // Create a new document
        Document doc = new Document();
        doc.addContent(root);
        
        // Serialize!
        File dir = new File(MobArena.arenaDir, log.getArena().configName());
        if (!dir.exists()) dir.mkdirs();
        serialize(log, dir, doc, "totals.xml");
    }
    
    private static Element getReward(ItemStack stack)
    {
        boolean money = stack.getTypeId() == MobArena.ECONOMY_MONEY_ID;
        
        Element result = new Element("reward");        
        result.setAttribute(new Attribute("id", stack.getTypeId() + ""));
        result.setAttribute(new Attribute("material", money ? "money" : stack.getType().toString().toLowerCase()));
        result.setAttribute(new Attribute("data", money || stack.getData() == null ? "0" : stack.getData().toString().toLowerCase()));
        result.setAttribute(new Attribute("amount", stack.getAmount() + ""));
        
        return result;
    }
    
    private static void serialize(ArenaLog log, File dir, Document doc, String filename)
    {
        try
        {
            File file = new File(dir, filename);
            FileOutputStream fop = new FileOutputStream(file);
            XMLOutputter out = new XMLOutputter();
            out.output(doc, fop);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Problem saving session data for arena '" + log.getArena().configName() + "'");
        }
    }
}
