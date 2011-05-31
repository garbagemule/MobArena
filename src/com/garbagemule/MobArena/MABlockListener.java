package com.garbagemule.MobArena;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockDamageEvent;

/**
 * This listener serves as a protection class. Blocks within
 * the arena region cannot be destroyed, and blocks can only
 * be placed by a participant in the current arena session.
 * Any placed blocks will be removed by the cleanup method in
 * ArenaManager when the session ends.
 */
public class MABlockListener extends BlockListener
{
	private MobArena plugin;

	public MABlockListener(MobArena instance)
	{
		plugin = instance;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event)
	{
		if(event.getBlock().getTypeId() == 46 || ArenaManager.blockSet.contains(event.getBlock()) )
			return;
		if (!ArenaManager.isSetup || !ArenaManager.isProtected)
			return;

		if (ArenaManager.blockSet.contains(event.getBlock()))
			return;

		if (MAUtils.inRegion(event.getBlock().getLocation()))
			event.setCancelled(true);



	}

	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(event.getBlock().getTypeId() == 46 || ArenaManager.blockSet.contains(event.getBlock()) )
			return;
		if (!ArenaManager.isSetup || !ArenaManager.isProtected)
			return;

		if (ArenaManager.blockSet.contains(event.getBlock()))
			return;

		if (MAUtils.inRegion(event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (!ArenaManager.isSetup || !ArenaManager.isProtected)
			return;

		if (MAUtils.inRegion(event.getBlock().getLocation()))
		{
			if (ArenaManager.isRunning && ArenaManager.playerSet.contains(event.getPlayer()))
			{
				ArenaManager.blockSet.add(event.getBlock());
				return;
			}

			event.setCancelled(true);
		}
	}
}