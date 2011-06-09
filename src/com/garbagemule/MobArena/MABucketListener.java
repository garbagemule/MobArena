package com.garbagemule.MobArena;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;


/**
 * This class serves as listener for player bucket drop events. It checks when they
 * empty a bucket, and adds the source block to blockSet to allow water to be removed from
 * arena correcty - written desmin88
 */
public class MABucketListener extends PlayerListener{

	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event)
	{
		Player p = event.getPlayer();
		//Best way to do this I think. Seems playerset only contains players in the Arena, so no other checking needed.
		if(ArenaManager.playerSet.contains(p)) {
			Block water = event.getBlockClicked().getFace(event.getBlockFace());
			ArenaManager.blockSet.add(water);
		}
	}
}
