package mock.mobarena;

import mock.util.MockLogger;

import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.config.Config;

public class MockMobArena //extends MobArena //implements MobArenaPlugin
{
    private ArenaMaster arenaMaster;
    private MockLogger log;
    private Config config;
    
    public MockMobArena() {
        //this.arenaMaster = new MockArenaMaster(this);
        this.log = MockLogger.getLogger("MobArenaTest");
    }

    //@Override
    public void onEnable() {
        //this.config = new Config(new File(getDataFolder(), "config.yml"));
        //this.arenaMaster = new ArenaMasterImpl(this);
        this.log.log("MockMobArena enabled.");
    }

    //@Override
    public void onDisable() {
        log.log("MockMobArena disabled.");
    }

    //@Override
    public Config getMAConfig() {
        return config;
    }
    
    //@Override
    public ArenaMaster getArenaMaster() {
        return arenaMaster;
    }
    
    //@Override
    public void info(String msg)    { log.log("[MobArena] " + msg); }
    //@Override
    public void warning(String msg) { log.log("[MobArena] " + msg); }
    //@Override
    public void error(String msg)   { log.log("[MobArena] " + msg); }
}
