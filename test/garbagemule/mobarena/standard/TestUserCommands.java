package garbagemule.mobarena.standard;

import static org.junit.Assert.*;

import mock.bukkit.*;
import mock.mobarena.*;
import mock.util.MockLogger;

import org.junit.*;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;

public class TestUserCommands
{
    private static final String LABEL = "ma";
    
    private MockLogger log;
    private String expected;
    
    private MobArenaPlugin plugin;
    private ArenaMaster arenaMaster;
    private CommandHandler commandHandler;
    private MockPlayer player;

    @Before
    public void setup() {
        // Set up the log.;
        log = new MockLogger();
        log.log("Start");
        
        // Create a plugin.
        plugin = new MockMobArena();
        
        // Get the ArenaMaster.
        arenaMaster = plugin.getArenaMaster();
        
        // Create a player and a command handler.
        player = new MockPlayer("garbagemule", log);
        commandHandler = new CommandHandler(plugin);
    }
    
    @After
    public void cleanup() {
        log = null;
        plugin = null;
        arenaMaster = null;
        player = null;
        commandHandler = null;
    }
    
    
    
    /* ********************************************************************* */
    /*
    /*                              JOIN COMMAND
    /* 
    /* ********************************************************************* */
    
    // TODO: Expand all tests to check that the player is indeed added and warped.
    
    @Test
    public void expectArguments() {
        commandHandler.onCommand(player, null, LABEL, compileArgs());
        expected = MockLogger.compileMsgToPlayer(player, Msg.MISC_NONE);
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    @Test
    public void playerCanJoinExistingArena() {
        // Create some arenas.
        createArenas("test","test2","test21","test12");
        
        // Try to join an existing arena.
        commandHandler.onCommand(player, null, LABEL, compileArgs("join","test"));
        
        // Success!
        expected = MockLogger.compileMsgToPlayer(player, Msg.JOIN_PLAYER_JOINED);
        assertEquals(expected, log.getLastEntry().getValue());
        
        // Verify that the player is now in the lobby.
        Arena a = arenaMaster.getArenaWithName("test");
        assertTrue(a.inLobby(player));
    }
    
    @Test
    public void playerCantJoinNonExistingArena() {
        // Create some arenas.
        createArenas("test","test2","test21","test12");
        
        // Try to join one that doesn't exist.
        commandHandler.onCommand(player, null, LABEL, compileArgs("join","test1"));
        
        // Failure!
        expected = MockLogger.compileMsgToPlayer(player, Msg.ARENA_DOES_NOT_EXIST);
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    @Test
    public void argsRequiredForMultipleArenas() {
        // Create some arenas.
        createArenas("test","test2","test21","test12");

        // Try to join with no arguments.
        commandHandler.onCommand(player, null, LABEL, compileArgs("join"));
        
        // Should join the only existing arena.
        expected = MockLogger.compileMsgToPlayer(player, Msg.JOIN_ARG_NEEDED);
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    @Test
    public void noArgsNeededForOneArenaOnly() {
        // Create one arena.
        createArenas("gjklqwejklewjglkejg");

        // Try to join with no arguments.
        commandHandler.onCommand(player, null, LABEL, compileArgs("join"));
        
        // Should join the only existing arena.
        expected = MockLogger.compileMsgToPlayer(player, Msg.JOIN_PLAYER_JOINED);
        assertEquals(expected, log.getLastEntry().getValue());
        
        // Verify that the player is now in the lobby.
        Arena a = arenaMaster.getArenaWithName("gjklqwejklewjglkejg");
        assertTrue(a.inLobby(player));
    }
    
    @Test
    public void playerCanOnlyJoinPermittedArenas() {
        // Create a couple of arenas.
        createArenas("yay", "nay");
        
        // Give the player no permission for one.
        player.setPermission("mobarena.arenas.nay", false);
        
        // Try to join.
        commandHandler.onCommand(player, null, LABEL, compileArgs("join","nay"));
        
        // No permission.
        expected = MockLogger.compileMsgToPlayer(player, Msg.JOIN_ARENA_PERMISSION);
        assertEquals(expected, log.getLastEntry().getValue());
        
        // Verify that the player didn't join and isn't in the lobby.
        Arena a = arenaMaster.getArenaWithName("nay");
        assertFalse(a.inLobby(player));
    }
    
    
    
    /* ********************************************************************* */
    /*
    /*                          LIST ARENAS COMMAND
    /* 
    /* ********************************************************************* */

    @Test
    public void shouldListAllArenas() {
        // Create some arenas.
        createArenas("one", "two", "three", "four");

        // Try to list them.
        commandHandler.onCommand(player, null, LABEL, compileArgs("arenas"));
        
        // Should show all of them, because player has permission to all per default.
        expected = MockLogger.compileMsgToPlayer(player, Msg.MISC_LIST_ARENAS.toString("one, two, three, four"));
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    @Test
    public void shouldListMiscNoneIfNoArenas() {
        // Create an arena and remove the permission.
        createArenas("one");
        player.setPermission("mobarena.arenas.one", false);
        
        // Try to list arenas.
        commandHandler.onCommand(player, null, LABEL, compileArgs("arenas"));
        
        // Should display MISC_NONE
        String none = Msg.MISC_NONE.toString();
        expected = MockLogger.compileMsgToPlayer(player, Msg.MISC_LIST_ARENAS.toString(none));
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    @Test
    public void shouldDisplayOnlyPermittedArenas() {
        // Create three arenas, remove permissions for one.
        createArenas("one", "two", "three");
        player.setPermission("mobarena.arenas.two", false);
        
        // List arenas.
        commandHandler.onCommand(player, null, LABEL, compileArgs("arenas"));
        
        // Should display one and three.
        expected = MockLogger.compileMsgToPlayer(player, Msg.MISC_LIST_ARENAS.toString("one, three"));
        assertEquals(expected, log.getLastEntry().getValue());
    }
    
    private void createArenas(String... args) {
        for (String s : args) {
            arenaMaster.createArenaNode(s, null);
        }
    }
    
    private String[] compileArgs(String... args) {
        return args;
    }
}
