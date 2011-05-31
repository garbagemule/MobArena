package com.garbagemule.MobArena;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Spider;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
/**
 * Very simple listener for preventing Creeper explosions from
 * damaging the blocks of the arena.
 */
public class MACreeperListener extends EntityListener
{
	private MobArena plugin;

	public MACreeperListener(MobArena instance)
	{
		plugin = instance;
	}

	@Override
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (MAUtils.inRegion(event.getLocation()))
			event.setCancelled(true);
	}
	@Override
	public void onEntityCombust(EntityCombustEvent event)
	{
		if (MAUtils.inRegion(event.getEntity().getLocation()))
			event.setCancelled(true);

	}
	public void onEntityTargetEvent(EntityTargetEvent event) {
		if(event.getEntity() instanceof Spider) {
			if(event.getReason() == TargetReason.FORGOT_TARGET && MAUtils.inRegion(event.getEntity().getLocation()) ) {
				event.setCancelled(true);
			}
		}
	}

}