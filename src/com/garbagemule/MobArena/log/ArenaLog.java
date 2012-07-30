package com.garbagemule.MobArena.log;

import java.util.HashMap;
import java.util.Map;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.MutableInt;

public class ArenaLog
{
    private Arena arena;
    private LogSessionBuilder sessionBuilder;
    private LogTotalsBuilder  totalsBuilder;
    
    public ArenaLog(Arena arena, LogSessionBuilder sessionBuilder, LogTotalsBuilder totalsBuilder) {
        this.arena = arena;
        this.sessionBuilder = sessionBuilder;
        this.totalsBuilder  = totalsBuilder;
    }
    
    public void start() {
        // Log number of players.
        sessionBuilder.buildNumberOfPlayers(arena.getPlayersInArena().size());
        
        // Log the distribution of classes.
        Map<String,MutableInt> classDistribution = new HashMap<String,MutableInt>();
        for (String classname : arena.getClasses().keySet()) {
            classDistribution.put(classname, new MutableInt());
        }
        for (ArenaPlayer ap : arena.getArenaPlayerSet()) {
            classDistribution.get(ap.getArenaClass().getLowercaseName()).inc();
        }
        sessionBuilder.buildClassDistribution(classDistribution);
        totalsBuilder.updateClassDistribution(classDistribution);
        
        // Log the current time and set it as the start time.
        sessionBuilder.buildStartTime();
        totalsBuilder.recordStartTime();
    }
    
    public void end() {
        // Log the end time and duration
        sessionBuilder.buildEndTime();
        sessionBuilder.buildDuration();
        
        totalsBuilder.updateTimePlayed();
        totalsBuilder.updateSessionsPlayed();
        
        // Update the last wave.
        totalsBuilder.updateLastWave(arena.getWaveManager().getWaveNumber());
        
        // Finalize the session.
        sessionBuilder.finalize();
        totalsBuilder.finish();
    }
    
    public void playerDeath(ArenaPlayer ap) {
        ArenaLogPlayerEntry entry = ArenaLogPlayerEntry.create(ap);
        sessionBuilder.buildPlayerEntry(entry, arena.getRewardManager().getRewards(ap.getPlayer()));
        totalsBuilder.updatePlayerEntry(entry);
    }
}
