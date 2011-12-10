package garbagemule.mobarena.standard;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.bukkit.util.config.Configuration;
import org.junit.*;

import com.garbagemule.MobArena.util.Config;

@SuppressWarnings("deprecation")
public class TestNewConfig
{
    private Configuration oldConfig;
    private Config newConfig;
    private File testFileOld, testFileNew;
    
    @Before
    public void setup() { 
        testFileOld = makeTestFile("test", "config.yml");
        testFileNew = makeTestFile("test", "config.yml");
        
        oldConfig = new Configuration(testFileOld);
        newConfig = new Config(testFileNew);
        
        oldConfig.load();
        newConfig.load();
    }
    
    @After
    public void cleanup() {
        testFileOld.delete();
        testFileNew.delete();
    }
    
    @Test
    public void getProperty() {
        String path = "arenas.lol.rewards.waves.after.7";
        
        Object oldObj = oldConfig.getProperty(path);
        Object newObj = newConfig.getProperty(path);
        
        assertEquals(oldObj, newObj);
    }
    
    @Test
    public void getInt() {
        String path = "arenas.lol.settings.first-wave-delay";
        int oldInt = oldConfig.getInt(path, -1);
        int newInt = newConfig.getInt(path, -2);
        
        assertEquals(oldInt, newInt);
    }
    
    @Test
    public void getString() {
        String path = "classes.Knight.items";
        String oldString = oldConfig.getString(path, "old");
        String newString = newConfig.getString(path, "new");
        
        assertEquals(oldString, newString);
        
        path = "classes.Knight.armor";
        oldString = oldConfig.getString(path, "old");
        newString = newConfig.getString(path, "new");
        
        assertEquals(oldString, newString);
    }
    
    @Test
    public void getBoolean() {
        String path = "global-settings.enabled";
        boolean oldBoolean = oldConfig.getBoolean(path, false);
        boolean newBoolean = newConfig.getBoolean(path, true);
        
        assertEquals(oldBoolean, newBoolean);
        
        // Test with both permutations of the default value.
        oldBoolean = oldConfig.getBoolean(path, true);
        newBoolean = newConfig.getBoolean(path, false);
        
        assertEquals(oldBoolean, newBoolean);
    }
    
    @Test
    public void getKeys() {
        String path = "classes";

        List<String> oldList = new LinkedList<String>();
        List<String> newList = new LinkedList<String>();
        
        oldList.addAll(oldConfig.getKeys(path));
        newList.addAll(newConfig.getKeys(path));

        // Sort, just in case.
        Collections.sort(oldList);
        Collections.sort(newList);
        
        assertEquals(oldList, newList);
    }
    
    @Test
    public void getStringList() {
        String path = "classes.Knight.permissions";
        
        List<String> oldList = oldConfig.getStringList(path, null);
        List<String> newList = newConfig.getStringList(path, null);
        assertNotNull(oldList);
        assertNotNull(newList);
        assertFalse(oldList.isEmpty());
        assertFalse(newList.isEmpty());
        
        assertEquals(oldList, newList);
        
        // Empty lists.
        path = "classes.Tank.permissions";
        
        oldList = oldConfig.getStringList(path, new LinkedList<String>());
        newList = newConfig.getStringList(path, new LinkedList<String>());
        assertNotNull(oldList);
        assertNotNull(newList);
        assertTrue(oldList.isEmpty());
        assertTrue(newList.isEmpty());
        
        assertEquals(oldList, newList);
        
        // Null values behave the same as empty lists.
        oldList = oldConfig.getStringList(path, null);
        newList = newConfig.getStringList(path, null);
        assertNotNull(oldList);
        assertNotNull(newList);
        assertTrue(oldList.isEmpty());
        assertTrue(newList.isEmpty());
        
        assertEquals(oldList, newList);
        

        // Non-existing lists.
        path = "classes.Archer.permissions";
        
        oldList = oldConfig.getStringList(path, new LinkedList<String>());
        newList = newConfig.getStringList(path, new LinkedList<String>());
        assertNotNull(oldList);
        assertNotNull(newList);
        assertTrue(oldList.isEmpty());
        assertTrue(newList.isEmpty());
        
        assertEquals(oldList, newList);
    }
    
    @Test
    public void removeProperty() {
        String path = "global-settings.enabled";
        
        // Make sure something exists first.
        Object oldObj = oldConfig.getProperty(path);
        Object newObj = newConfig.getProperty(path);
        assertNotNull(oldObj);
        assertNotNull(newObj);
        
        assertEquals(oldObj, newObj);

        // Remove and try again.
        oldConfig.removeProperty(path);
        newConfig.removeProperty(path);
        
        // They should now both be null.
        oldObj = oldConfig.getProperty(path);
        newObj = newConfig.getProperty(path);
        assertNull(oldObj);
        assertNull(newObj);
        
        assertEquals(oldObj, newObj);
        
        // Should be idempotent.
        oldConfig.removeProperty(path);
        newConfig.removeProperty(path);
        
        oldObj = oldConfig.getProperty(path);
        newObj = newConfig.getProperty(path);
        assertNull(oldObj);
        assertNull(newObj);
        
        assertEquals(oldObj, newObj);
    }
    
    private File makeTestFile(String path, String filename) {
        // Grab the time
        Timestamp timestamp = new Timestamp(new Date().getTime());
        
        // Convert to a string
        String time = new SimpleDateFormat("MM-dd-yyyy-H_m_s_S").format(timestamp);
        
        // Make a new file
        File testFile = new File(path + File.separator + time + "_" + filename);
        
        // Start copying the bytes
        try {
            FileUtils.copyFile(new File(path + File.separator + filename), testFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return testFile;
    }
}
