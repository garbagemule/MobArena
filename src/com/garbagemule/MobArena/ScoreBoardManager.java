package com.garbagemule.MobArena;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public final class ScoreBoardManager
{
   private ScoreBoardManager() {
   }
   private final static ScoreboardManager sbm = Bukkit.getScoreboardManager();
   private static MobArena plugin = null;

   private static Map<Arena, Scoreboard> boards = new HashMap<Arena, Scoreboard>();
   private static Map<String, Scoreboard> playerBoards = new HashMap<String, Scoreboard>();
   
   public static void init(MobArena plugin) {
	   ScoreBoardManager.plugin = plugin;
   }
   
   public static void add(final Arena arena, final Player player) {
	   playerBoards.put(player.getName(), player.getScoreboard());
		
		// first, check if the scoreboard exists
		class RunLater implements Runnable {

			@Override
			public void run() {
				
				if (boards.containsKey(arena)) {
					Scoreboard board = boards.get(arena);
					board.getTeam("mobarena").addPlayer(player);
					board.getObjective("kills").getScore(player).setScore(0);
					update(arena, player);
				} else {

					Scoreboard board = sbm.getNewScoreboard();
					boards.put(arena, board);
					
					try {
						board.registerNewTeam("mobarena");
						board.getTeam("mobarena").setAllowFriendlyFire(true); // let MA handle PVP
						//TODO: add check for PVP?
					} catch (Exception e) {
						
					}
					
					board.getTeam("mobarena").addPlayer(player);
					
					if (board.getObjective("kills") != null) {
						board.getObjective("kills").unregister();
						if (board.getObjective(DisplaySlot.SIDEBAR) != null) {
							board.getObjective(DisplaySlot.SIDEBAR).unregister();
						}
					}
					
					Objective obj = board.registerNewObjective("kills", "totalKillCount");
					
					obj.setDisplayName("§aMobArena Kills");
					obj.setDisplaySlot(DisplaySlot.SIDEBAR);

					obj.getScore(player).setScore(0);
					
					update(arena, player);
				}
			}
			
		}
		Bukkit.getScheduler().runTaskLater(plugin, new RunLater(), 1L);
   }
   
   public static void remove(Arena arena, Player player) {
	   try {
			boards.get(arena).resetScores(player);
		} catch (Exception e) {
			
		}
		player.setScoreboard(playerBoards.get(player.getName()));
		update(arena);
	}

	public static void start(final Arena arena) {
		class RunLater implements Runnable {

			@Override
			public void run() {
				update(arena);
			}
			
		}
		Bukkit.getScheduler().runTaskLater(plugin, new RunLater(), 1L);
	}

	public static void stop(Arena arena) {
		if (!boards.containsKey(arena)) {
			return;
		}
		Scoreboard board = boards.get(arena);
		for (OfflinePlayer player : board.getPlayers()) {
			if (player != null) {
				board.resetScores(player);
			}
		}
		Objective obj = board.getObjective("kills");
		obj.unregister();
		obj = null;
		boards.remove(arena);
		board.clearSlot(DisplaySlot.SIDEBAR);
		board = null;
	}
	private static boolean block = false;

	private static void update(final Arena arena, final Player player) {
		
		player.setScoreboard(sbm.getMainScoreboard());
		if (boards.containsKey(arena)) {
			player.setScoreboard(boards.get(arena));
		}
		
		if (!block) {
			class RunLater implements Runnable {

				@Override
				public void run() {
					update(arena);
				}
				
			}
			Bukkit.getScheduler().runTaskLater(plugin, new RunLater(), 1L);
		}
	}

	private static void update(Arena arena) {
		block = true;
		for (Player player : arena.getAllPlayers()) {
			update(arena, player);
		}
		block = false;
	}
	
	public static void wave(Arena arena, int wave) {
		Scoreboard board = boards.get(arena);
		Objective obj = board.getObjective("kills");
		obj.setDisplayName("§aMobArena Kills§f - §eWave " + wave);
		update(arena);
	}
}
